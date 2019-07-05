package alec_wam.CrystalMod.tiles.machine.crafting.press;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.ContainerBasicCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.EnumCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.TileEntityCraftingMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TileEntityPress extends TileEntityCraftingMachine {

	public TileEntityPress(){
		super(ModBlocks.craftingMachine.getTileType(EnumCraftingMachine.PRESS), "Press", 2);
	}

	@Override
	public Direction getFacing() {
		return getBlockState().get(BlockCraftingMachine.FACING);
	}
	
	@Override
	public boolean canStart() {
		ItemStack stack = getStackInSlot(0);
        if (ItemStackTools.isEmpty(stack)) {
            return false;
        }
        final PressRecipe recipe = getRecipe();
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        ItemStack recipeOutput = ItemStackTools.safeCopy(recipe.getRecipeOutput());
        ItemStack currentOutput = ItemStackTools.safeCopy(getStackInSlot(1));
        boolean outputFits = true;        
        if(ItemStackTools.isValid(recipeOutput)){
        	if(ItemStackTools.isValid(currentOutput)){
        		int outputSize = ItemStackTools.getStackSize(recipeOutput);
        		int currentSize = ItemStackTools.getStackSize(currentOutput);
        		if(!ItemUtil.canCombine(currentOutput, recipeOutput)){
        			return false;
        		} else {
        			outputFits = (outputSize + currentSize) <= currentOutput.getMaxStackSize();
        		}
        	}
        }
        return outputFits;
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
    	final PressRecipe recipe = getRecipe();
    	final ItemStack output = recipe.getRecipeOutput().copy();
    	if (ItemStackTools.isNullStack(stack2)) {
            setInventorySlotContents(1, output);
        }
        else {
            ItemStackTools.incStackSize(stack2, ItemStackTools.getStackSize(output));
        }
        
        ItemStackTools.incStackSize(stack, -1);
        if (ItemStackTools.isEmpty(stack)) {
            setInventorySlotContents(0, ItemStackTools.getEmptyStack());
        }
    }

    @Override
    public boolean isItemValidInput(ItemStack stack) {
    	for(IRecipe<IInventory> irecipe : ModRecipes.getRecipes(getWorld().getRecipeManager(), ModRecipes.PRESS_TYPE)) {
    		if (irecipe.getIngredients().get(0).test(stack)) {
    			return true;
    		}
    	}
    	return false;
    }

    public PressRecipe getRecipe(){
    	return getWorld().getRecipeManager().getRecipe((IRecipeType<PressRecipe>)ModRecipes.PRESS_TYPE, this, getWorld()).orElse(null);
    }

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent("Press");
	}
	
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerIn) {
		return new ContainerBasicCraftingMachine<TileEntityPress>(i, playerIn, this);
	}

	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}

}
