package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderManager.GrinderRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

public class TileEntityGrinder extends TileEntityMachine implements ISidedInventory {

	public TileEntityGrinder(){
		super("Grinder", 3);
	}
	
	public boolean canStart() {
		ItemStack stack = getStackInSlot(0);
        if (ItemStackTools.isNullStack(stack)) {
            return false;
        }
        final GrinderRecipe recipe = GrinderManager.getRecipe(stack);
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        final ItemStack output = recipe.getMainOutput();
        ItemStack stack2 = getStackInSlot(1);
        boolean passesMain = ItemStackTools.isValid(output) && (ItemStackTools.isEmpty(stack2) || (ItemUtil.canCombine(output, stack2) && ItemStackTools.getStackSize(stack2) + ItemStackTools.getStackSize(output) <= output.getMaxStackSize()));
        final ItemStack outputSecond = recipe.getSecondaryOutput();
        ItemStack stack3 = getStackInSlot(2);
        boolean passesSecond = ItemStackTools.isValid(outputSecond) ? (ItemStackTools.isEmpty(stack3) || (ItemUtil.canCombine(outputSecond, stack3) && ItemStackTools.getStackSize(stack3) + ItemStackTools.getStackSize(outputSecond) <= outputSecond.getMaxStackSize())) : true;
        return passesMain && passesSecond;
	}
	
	public boolean canFinish() {
        return processRem <= 0 && this.hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	final GrinderRecipe recipe = GrinderManager.getRecipe(getStackInSlot(0));
        return recipe != null && recipe.getInputSize() <= ItemStackTools.getStackSize(getStackInSlot(0));
    }
    
    public void processStart() {
    	this.processMax = GrinderManager.getRecipe(getStackInSlot(0)).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
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
        
        setInventorySlotContents(0, ItemUtil.consumeItem(stack));
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
