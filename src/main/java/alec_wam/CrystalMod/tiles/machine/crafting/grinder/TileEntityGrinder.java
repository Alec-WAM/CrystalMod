package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IInteractionObject;

public class TileEntityGrinder extends TileEntityMachine implements IInteractionObject {

	public TileEntityGrinder(){
		super(ModBlocks.TILE_MACHINE_GRINDER, "Grinder", 3);
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
        final GrinderRecipe recipe = getRecipe();
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        ItemStack recipeOutput = ItemStackTools.safeCopy(recipe.getRecipeOutput());
        ItemStack currentOutput = ItemStackTools.safeCopy(getStackInSlot(1));
        boolean firstOutputFits = true;        
        if(ItemStackTools.isValid(recipeOutput)){
        	if(ItemStackTools.isValid(currentOutput)){
        		int outputSize = ItemStackTools.getStackSize(recipeOutput);
        		int currentSize = ItemStackTools.getStackSize(currentOutput);
        		if(!ItemUtil.canCombine(currentOutput, recipeOutput)){
        			firstOutputFits = false;
        		} else {
        			firstOutputFits = (outputSize + currentSize) <= currentOutput.getMaxStackSize();
        		}
        	}
        }
        
        ItemStack secondRecipeOutput = ItemStackTools.safeCopy(recipe.getBonusOutput());
        ItemStack currentSecondOutput = ItemStackTools.safeCopy(getStackInSlot(2));
        boolean soundOutputFits = true;             
        if(ItemStackTools.isValid(secondRecipeOutput)){
        	if(ItemStackTools.isValid(currentSecondOutput)){
        		if(!ItemUtil.canCombine(currentSecondOutput, secondRecipeOutput)){
        			return false;
        		}            	
        		int outputSize = ItemStackTools.getStackSize(secondRecipeOutput);
        		int currentSize = ItemStackTools.getStackSize(currentSecondOutput);
        		soundOutputFits = (outputSize + currentSize) <= currentSecondOutput.getMaxStackSize();
        	}
        }
        return firstOutputFits && soundOutputFits;
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
    	ItemStack stack3 = getStackInSlot(2);
    	final GrinderRecipe recipe = getRecipe();
    	final ItemStack output = recipe.getRecipeOutput().copy();
    	if (ItemStackTools.isNullStack(stack2)) {
            setInventorySlotContents(1, output);
        }
        else {
            ItemStackTools.incStackSize(stack2, ItemStackTools.getStackSize(output));
        }
    	
    	final ItemStack outputSecond = recipe.getBonusOutput().copy();        
    	if(ItemStackTools.isValid(outputSecond) && ItemUtil.canCombine(outputSecond, stack3)){
	        float rand = this.getWorld().rand.nextFloat();
	        if(recipe.getBonusChance() > 0.0 && rand <= recipe.getBonusChance()){
		        if (ItemStackTools.isNullStack(stack3)) {
		            setInventorySlotContents(2, outputSecond);
		        }
		        else {
		            ItemStackTools.incStackSize(stack3, ItemStackTools.getStackSize(outputSecond));
		        }
	        }
    	}
        
        ItemStackTools.incStackSize(stack, -1);
        if (ItemStackTools.isEmpty(stack)) {
            setInventorySlotContents(0, ItemStackTools.getEmptyStack());
        }
    }

    public static boolean canGrind(ItemStack stack, TileEntityGrinder grinder) {
    	for(IRecipe irecipe : grinder.getWorld().getRecipeManager().getRecipes(ModRecipes.GRINDER)) {
    		if (irecipe.getIngredients().get(0).test(stack)) {
    			return true;
    		}
    	}
    	return false;
    }

    public GrinderRecipe getRecipe(){
    	return getWorld().getRecipeManager().getRecipe(this, getWorld(), ModRecipes.GRINDER);
    }
    
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn) {
		return index == 0 && canGrind(itemStackIn, this);
	}
	
	@Override
	public boolean canExtract(int index, int amt){
		return index > 0;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerGrinder(playerIn, this);
	}

	@Override
	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}

}
