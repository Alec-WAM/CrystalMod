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
        if (ItemStackTools.isNullStack(inventory[0])) {
            return false;
        }
        final GrinderRecipe recipe = GrinderManager.getRecipe(inventory[0]);
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        final ItemStack output = recipe.getMainOutput();
        boolean passesMain = ItemStackTools.isValid(output) && (ItemStackTools.isEmpty(inventory[1]) || (ItemUtil.canCombine(output, inventory[1]) && ItemStackTools.getStackSize(inventory[1]) + ItemStackTools.getStackSize(output) <= output.getMaxStackSize()));
        final ItemStack outputSecond = recipe.getSecondaryOutput();
        boolean passesSecond = ItemStackTools.isValid(outputSecond) ? (ItemStackTools.isEmpty(inventory[2]) || (ItemUtil.canCombine(outputSecond, inventory[2]) && ItemStackTools.getStackSize(inventory[2]) + ItemStackTools.getStackSize(outputSecond) <= outputSecond.getMaxStackSize())) : true;
        return passesMain && passesSecond;
	}
	
	public boolean canFinish() {
        return processRem <= 0 && this.hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	final GrinderRecipe recipe = GrinderManager.getRecipe(this.inventory[0]);
        return recipe != null && recipe.getInputSize() <= ItemStackTools.getStackSize(this.inventory[0]);
    }
    
    public void processStart() {
    	this.processMax = GrinderManager.getRecipe(this.inventory[0]).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    public void processFinish() {
    	final GrinderRecipe recipe = GrinderManager.getRecipe(this.inventory[0]);
    	final ItemStack output = recipe.getMainOutput();
    	if(ItemStackTools.isValid(output)){
    		if (output.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
    			output.setItemDamage(0);
        	}
    	}
        if (ItemStackTools.isNullStack(this.inventory[1])) {
            this.inventory[1] = output;
        }
        else {
            final ItemStack itemStack = this.inventory[1];
            ItemStackTools.incStackSize(itemStack, ItemStackTools.getStackSize(output));
        }
        
        int rand = this.getWorld().rand.nextInt(100)+1;
        if(rand <=recipe.getSecondaryChance()){
	        final ItemStack outputSecond = recipe.getSecondaryOutput();
	        if(ItemStackTools.isValid(outputSecond)){
	    		if (outputSecond.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
	    			outputSecond.setItemDamage(0);
	        	}
	    	}
	        if (ItemStackTools.isNullStack(this.inventory[2])) {
	            this.inventory[2] = outputSecond;
	        }
	        else {
	            final ItemStack itemStack = this.inventory[2];
	            ItemStackTools.incStackSize(itemStack, ItemStackTools.getStackSize(outputSecond));
	        }
        }
        
        final ItemStack itemStack2 = this.inventory[0];
        ItemStackTools.incStackSize(itemStack2, -1);
        if (ItemStackTools.isEmpty(itemStack2)) {
            this.inventory[0] = ItemStackTools.getEmptyStack();
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
