package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.EnumCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.TileEntityCraftingMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemTagHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class TileEntityPoweredFurnace extends TileEntityCraftingMachine implements IRecipeHolder, IRecipeHelperPopulator {
	private final Map<ResourceLocation, Integer> recipeUseCounts = Maps.newHashMap();
	
	public TileEntityPoweredFurnace() {
		super(ModBlocks.craftingMachine.getTileType(EnumCraftingMachine.FURNACE), "PoweredFurnace", 2);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		
		nbt.putShort("RecipesUsedSize", (short)this.recipeUseCounts.size());
		int i = 0;

		for(Entry<ResourceLocation, Integer> entry : this.recipeUseCounts.entrySet()) {
			nbt.putString("RecipeLocation" + i, entry.getKey().toString());
			nbt.putInt("RecipeAmount" + i, entry.getValue());
			++i;
		}
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		int i = nbt.getShort("RecipesUsedSize");

		for(int j = 0; j < i; ++j) {
			ResourceLocation resourcelocation = new ResourceLocation(nbt.getString("RecipeLocation" + j));
			int k = nbt.getInt("RecipeAmount" + j);
			this.recipeUseCounts.put(resourcelocation, k);
		}
	}
	
	@Override
	public Direction getFacing() {
		return getBlockState().get(BlockCraftingMachine.FACING);
	}
	
	@Override
	public boolean canStart() {
		final PoweredFurnaceRecipe recipe = getRecipe();
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
        	return false;
        }
        final ItemStack output = recipe.getOutput();
        ItemStack stack2 = getStackInSlot(1);
        return ItemStackTools.isValid(output) && (ItemStackTools.isNullStack(stack2) || (ItemUtil.canCombine(output, stack2) && ItemStackTools.getStackSize(stack2) + ItemStackTools.getStackSize(output) <= output.getMaxStackSize()));
    }
	
	@Override
	public boolean canContinueRunning(){
		return hasValidInput();
	}
	
	@Override
	public boolean canFinish() {
        return processRem <= 0 && this.hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	return getRecipe() != null;
    }
    
    @Override
	public void processStart() {
    	this.processMax = getRecipe().getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    @Override
	public void processFinish() {
    	ItemStack stack = getStackInSlot(0);
    	ItemStack stack2 = getStackInSlot(1);
    	PoweredFurnaceRecipe recipe = getRecipe();
    	final ItemStack output = recipe.getOutput().copy();
    	if (!this.world.isRemote) {
    		this.canUseRecipe(this.world, (ServerPlayerEntity)null, recipe.getRecipe());
    	}
    	
        if (ItemStackTools.isNullStack(stack2)) {
            setInventorySlotContents(1, output);
        }
        else {
            ItemStackTools.incStackSize(stack2, ItemStackTools.getStackSize(output));
        }
        stack.shrink(1);
        this.setInventorySlotContents(0, stack);
    }
    
	@Override
	public boolean isItemValidInput(ItemStack stack) {
		for(IRecipe<IInventory> irecipe : ModRecipes.getRecipes(getWorld().getRecipeManager(), IRecipeType.SMELTING)) {
    		if (irecipe.getIngredients().get(0).test(stack)) {
    			return true;
    		}
    	}
    	return false;
	}

	public PoweredFurnaceRecipe getRecipe(){
		FurnaceRecipe recipe = getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, this, getWorld()).orElse(null);
		if(recipe !=null){

			ItemStack output = recipe.getRecipeOutput().copy();
			int energy = 1600;
			//Is it food?
			if ((output.getItem().isFood())) {
				energy /= 2;
			}
			if (ItemTagHelper.isIngot(output)) {
				energy = 1000;
			}
			return new PoweredFurnaceRecipe(recipe, recipe.getIngredients().get(0), output, energy);
		}
		return null;
	}

	public static class PoweredFurnaceRecipe {
		final FurnaceRecipe recipe;
		final Ingredient input;
	    final ItemStack output;
	    final int energy;
	    
	    public PoweredFurnaceRecipe(FurnaceRecipe recipe, Ingredient input, ItemStack output, int energy)
	    {
	    	this.recipe = recipe;
	    	this.input = input;
	        this.output = output;
	        this.energy = energy;
	    }
	    
	    public FurnaceRecipe getRecipe(){
	    	return recipe;
	    }
	    
	    public Ingredient getInput()
	    {
	    	return input;
	    }
	    
	    public boolean matchesInput(ItemStack stack){
	    	return input.test(stack);
	    }
	    
	    public ItemStack getOutput()
	    {
	      return this.output.copy();
	    }
	    
	    public int getEnergy()
	    {
	      return this.energy;
	    }
	    
	    @Override
	    public boolean equals(Object obj){
	    	if(obj == null || !(obj instanceof PoweredFurnaceRecipe)) return false;
	    	PoweredFurnaceRecipe other = (PoweredFurnaceRecipe)obj;
	    	
	    	if(getInput() != other.getInput()) return false;
	    	
	    	return ItemUtil.canCombine(getOutput(), other.getOutput());
	    }
	}

	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent("PoweredFurnace");
	}
	
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerIn) {
		return new ContainerPoweredFurnace(i, playerIn, this);
	}

	//RecipeBook stuff
	
	@Override
	public void setRecipeUsed(IRecipe<?> recipe) {
		if (this.recipeUseCounts.containsKey(recipe.getId())) {
			this.recipeUseCounts.put(recipe.getId(), this.recipeUseCounts.get(recipe.getId()) + 1);
		} else {
			this.recipeUseCounts.put(recipe.getId(), 1);
		}

	}

	@Nullable
	public IRecipe<?> getRecipeUsed() {
		return null;
	}

	//TODO Auto Export XP to XP Tank when added
	public Map<ResourceLocation, Integer> getRecipeUseCounts() {
		return this.recipeUseCounts;
	}

	@Override
	public boolean canUseRecipe(World worldIn, ServerPlayerEntity player, @Nullable IRecipe<?> recipe) {
		if (recipe != null) {
			this.setRecipeUsed(recipe);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onCrafting(PlayerEntity player) {
		if (!this.world.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING)) {
			List<IRecipe<?>> list = Lists.newArrayList();

			for(ResourceLocation resourcelocation : this.recipeUseCounts.keySet()) {
				IRecipe<?> irecipe = player.world.getRecipeManager().getRecipe(resourcelocation).orElse(null);
				if (irecipe != null) {
					list.add(irecipe);
				}
			}

			player.unlockRecipes(list);
		}

		this.recipeUseCounts.clear();
	}

	@Override
	public void fillStackedContents(RecipeItemHelper helper) {
		for(ItemStack itemstack : this.inventory) {
			helper.accountStack(itemstack);
		}
	}

}
