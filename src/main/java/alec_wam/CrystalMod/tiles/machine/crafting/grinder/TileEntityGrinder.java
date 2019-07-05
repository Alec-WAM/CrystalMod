package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.EnumCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.TileEntityCraftingMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TileEntityGrinder extends TileEntityCraftingMachine implements INamedContainerProvider {

	public TileEntityGrinder(){
		super(ModBlocks.craftingMachine.getTileType(EnumCraftingMachine.GRINDER), "Grinder", 3);
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

    @Override
    public boolean isItemValidInput(ItemStack stack) {    	
    	for(IRecipe<IInventory> irecipe : ModRecipes.getRecipes(getWorld().getRecipeManager(), ModRecipes.GRINDER_TYPE)) {
    		if (irecipe.getIngredients().get(0).test(stack)) {
    			return true;
    		}
    	}
    	return false;
    }
	
    @Override
	public int[] getOutputSlots() {
		return new int[] {1, 2};
	}

    public GrinderRecipe getRecipe(){
    	return getWorld().getRecipeManager().getRecipe((IRecipeType<GrinderRecipe>)ModRecipes.GRINDER_TYPE, this, getWorld()).orElse(null);
    }
	
	@Override
	public boolean canExtract(int index, int amt){
		return index > 0;
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerIn) {
		return new ContainerGrinder(i, playerIn, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent("MachineGrinder");
	}

}
