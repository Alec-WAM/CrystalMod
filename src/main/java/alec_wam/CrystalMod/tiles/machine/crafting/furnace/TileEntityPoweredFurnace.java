package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemTagHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class TileEntityPoweredFurnace extends TileEntityMachine implements IInteractionObject, IRecipeHolder, IRecipeHelperPopulator {
	private final Map<ResourceLocation, Integer> recipeUseCounts = Maps.newHashMap();
	
	public TileEntityPoweredFurnace() {
		super(ModBlocks.TILE_MACHINE_FURNACE, "PoweredFurnace", 2);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		
		nbt.setShort("RecipesUsedSize", (short)this.recipeUseCounts.size());
		int i = 0;

		for(Entry<ResourceLocation, Integer> entry : this.recipeUseCounts.entrySet()) {
			nbt.setString("RecipeLocation" + i, entry.getKey().toString());
			nbt.setInt("RecipeAmount" + i, entry.getValue());
			++i;
		}
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		int i = nbt.getShort("RecipesUsedSize");

		for(int j = 0; j < i; ++j) {
			ResourceLocation resourcelocation = new ResourceLocation(nbt.getString("RecipeLocation" + j));
			int k = nbt.getInt("RecipeAmount" + j);
			this.recipeUseCounts.put(resourcelocation, k);
		}
	}
	
	@Override
	public EnumFacing getFacing() {
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
    		this.canUseRecipe(this.world, (EntityPlayerMP)null, recipe.getRecipe());
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
	public boolean canInsertItem(int index, ItemStack itemStackIn) {
		return index == 0 && canSmelt(itemStackIn, this);
	}
	
	@Override
	public boolean canExtract(int index, int amt) {
		return index == 1;
	}
    
	public static boolean canSmelt(ItemStack stack, TileEntityPoweredFurnace furnace) {
		for(IRecipe irecipe : furnace.getWorld().getRecipeManager().getRecipes(net.minecraftforge.common.crafting.VanillaRecipeTypes.SMELTING)) {
			if (irecipe.getIngredients().get(0).test(stack)) {
				return true;
			}
		}

		return false;
	}

	public PoweredFurnaceRecipe getRecipe(){
		FurnaceRecipe recipe = getWorld().getRecipeManager().getRecipe(this, getWorld(), net.minecraftforge.common.crafting.VanillaRecipeTypes.SMELTING);
		if(recipe !=null){

			ItemStack output = recipe.getRecipeOutput().copy();
			int energy = 1600;
			if ((output.getItem() instanceof ItemFood)) {
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

	@Override
	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerPoweredFurnace(playerIn, this);
	}

	//RecipeBook stuff
	
	@Override
	public void setRecipeUsed(IRecipe recipe) {
		if (this.recipeUseCounts.containsKey(recipe.getId())) {
			this.recipeUseCounts.put(recipe.getId(), this.recipeUseCounts.get(recipe.getId()) + 1);
		} else {
			this.recipeUseCounts.put(recipe.getId(), 1);
		}

	}

	@Nullable
	public IRecipe getRecipeUsed() {
		return null;
	}

	//TODO Auto Export XP to XP Tank when added
	public Map<ResourceLocation, Integer> getRecipeUseCounts() {
		return this.recipeUseCounts;
	}

	@Override
	public boolean canUseRecipe(World worldIn, EntityPlayerMP player, @Nullable IRecipe recipe) {
		if (recipe != null) {
			this.setRecipeUsed(recipe);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onCrafting(EntityPlayer player) {
		if (!this.world.getGameRules().getBoolean("doLimitedCrafting")) {
			List<IRecipe> list = Lists.newArrayList();

			for(ResourceLocation resourcelocation : this.recipeUseCounts.keySet()) {
				IRecipe irecipe = player.world.getRecipeManager().getRecipe(resourcelocation);
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
