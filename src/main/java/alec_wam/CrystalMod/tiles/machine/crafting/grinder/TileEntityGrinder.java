package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderManager.GrinderRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityGrinder extends TileEntityMachine implements ISidedInventory {

	public TileEntityGrinder(){
		super("Grinder", 3);
	}
	
	@Override
	public boolean canStart() {
		ItemStack stack = getStackInSlot(0);
        if (ItemStackTools.isEmpty(stack)) {
            return false;
        }
        final GrinderRecipe recipe = GrinderManager.getRecipe(stack);
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        ItemStack recipeOutput = ItemStackTools.safeCopy(recipe.getMainOutput());
        if(recipeOutput.getItemDamage() == OreDictionary.WILDCARD_VALUE){
        	recipeOutput.setItemDamage(0);
        }
        ItemStack currentOutput = ItemStackTools.safeCopy(getStackInSlot(1));
        if(currentOutput.getItemDamage() == OreDictionary.WILDCARD_VALUE){
        	currentOutput.setItemDamage(0);
        }
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
        
        ItemStack secondRecipeOutput = ItemStackTools.safeCopy(recipe.getSecondaryOutput());
        if(secondRecipeOutput.getItemDamage() == OreDictionary.WILDCARD_VALUE){
        	secondRecipeOutput.setItemDamage(0);
        }
        ItemStack currentSecondOutput = ItemStackTools.safeCopy(getStackInSlot(2));
        if(currentSecondOutput.getItemDamage() == OreDictionary.WILDCARD_VALUE){
        	currentSecondOutput.setItemDamage(0);
        }
        boolean soundOutputFits = true;        
        if(ItemStackTools.isValid(secondRecipeOutput)){
        	if(ItemStackTools.isValid(currentSecondOutput)){
        		int outputSize = ItemStackTools.getStackSize(secondRecipeOutput);
        		int currentSize = ItemStackTools.getStackSize(currentSecondOutput);
        		if(!ItemUtil.canCombine(currentSecondOutput, secondRecipeOutput)){
        			soundOutputFits = false;
        		} else {
        			soundOutputFits = (outputSize + currentSize) <= currentSecondOutput.getMaxStackSize();
        		}
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
    	final GrinderRecipe recipe = GrinderManager.getRecipe(getStackInSlot(0));
        return recipe != null && recipe.getInputSize() <= ItemStackTools.getStackSize(getStackInSlot(0));
    }
    
    @Override
	public void processStart() {
    	this.processMax = GrinderManager.getRecipe(getStackInSlot(0)).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    @Override
	public void processFinish() {
    	ItemStack stack = getStackInSlot(0);
    	ItemStack stack2 = getStackInSlot(1);
    	ItemStack stack3 = getStackInSlot(2);
    	final GrinderRecipe recipe = GrinderManager.getRecipe(stack);
    	final ItemStack output = recipe.getMainOutput();
    	if(ItemStackTools.isValid(output)){
    		if (output.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
    			output.setItemDamage(0);
        	}
    	}
        if (ItemStackTools.isNullStack(stack2)) {
            setInventorySlotContents(1, output);
        }
        else {
            ItemStackTools.incStackSize(stack2, ItemStackTools.getStackSize(output));
        }
        
        int rand = this.getWorld().rand.nextInt(100)+1;
        if(rand <=recipe.getSecondaryChance()){
	        final ItemStack outputSecond = recipe.getSecondaryOutput();
	        if(ItemStackTools.isValid(outputSecond)){
	    		if (outputSecond.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
	    			outputSecond.setItemDamage(0);
	        	}
	    	}
	        if (ItemStackTools.isNullStack(stack3)) {
	            setInventorySlotContents(2, outputSecond);
	        }
	        else {
	            ItemStackTools.incStackSize(stack3, ItemStackTools.getStackSize(outputSecond));
	        }
        }
        
        ItemStackTools.incStackSize(stack, -1);
        if (ItemStackTools.isEmpty(stack)) {
            setInventorySlotContents(0, ItemStackTools.getEmptyStack());
        }
    }

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 && GrinderManager.getRecipe(itemStackIn) !=null;
	}

	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerGrinder(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiGrinder(player, this);
	}

}
