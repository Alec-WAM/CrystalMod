package alec_wam.CrystalMod.tiles.machine.crafting.press;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.ContainerBasicCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.ContainerBasicCraftingMachine.SlotItemChecker;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IInteractionObject;

public class TileEntityPress extends TileEntityMachine implements IInteractionObject {

	public TileEntityPress(){
		super(ModBlocks.TILE_MACHINE_PRESS, "Press", 2);
	}

	@Override
	public EnumFacing getFacing() {
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

    public static boolean canPress(ItemStack stack, TileEntityPress press) {
    	for(IRecipe irecipe : press.getWorld().getRecipeManager().getRecipes(ModRecipes.PRESS)) {
    		if (irecipe.getIngredients().get(0).test(stack)) {
    			return true;
    		}
    	}
    	return false;
    }

    public PressRecipe getRecipe(){
    	return getWorld().getRecipeManager().getRecipe(this, getWorld(), ModRecipes.PRESS);
    }
    
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn) {
		return index == 0 && canPress(itemStackIn, this);
	}
	
	@Override
	public boolean canExtract(int index, int amt){
		return index > 0;
	}

	public static final SlotItemChecker<TileEntityPress> CHECKER = new SlotItemChecker<TileEntityPress>() {

		@Override
		public boolean canProcessItem(TileEntityPress machine, ItemStack stack) {
			return TileEntityPress.canPress(stack, machine);
		}
		
	};
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerBasicCraftingMachine<TileEntityPress>(playerIn, this, CHECKER);
	}

	@Override
	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}

}
