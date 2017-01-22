package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

public class TileEntityCrystalFurnace extends TileEntityMachine implements ISidedInventory {

	public TileEntityCrystalFurnace(){
		super("CrystalFurnace", 2);
	}
	
	public boolean canStart() {
		ItemStack stack = getStackInSlot(0);
        if (ItemStackTools.isNullStack(stack)) {
            return false;
        }
        final BasicMachineRecipe recipe = CrystalFurnaceManager.getRecipe(stack);
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        final ItemStack output = recipe.getOutput();
        ItemStack stack2 = getStackInSlot(1);
        return ItemStackTools.isValid(output) && (ItemStackTools.isNullStack(stack2) || (ItemUtil.canCombine(output, stack2) && ItemStackTools.getStackSize(stack2) + ItemStackTools.getStackSize(output) <= output.getMaxStackSize()));
    }
	
	public boolean canFinish() {
        return processRem <= 0 && this.hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	ItemStack stack = getStackInSlot(0);
    	final BasicMachineRecipe recipe = CrystalFurnaceManager.getRecipe(stack);
        return recipe != null && recipe.getInputSize() <= ItemStackTools.getStackSize(stack);
    }
    
    public void processStart() {
    	this.processMax = CrystalFurnaceManager.getRecipe(getStackInSlot(0)).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    public void processFinish() {
    	ItemStack stack = getStackInSlot(0);
    	ItemStack stack2 = getStackInSlot(1);
    	final ItemStack output = CrystalFurnaceManager.getRecipe(stack).getOutput();
        if (ItemStackTools.isNullStack(stack2)) {
            setInventorySlotContents(1, output);
        }
        else {
            ItemStackTools.incStackSize(stack2, ItemStackTools.getStackSize(output));
        }
        ItemStackTools.incStackSize(stack, -1);
        if (ItemStackTools.isEmpty(stack)) {
            this.setInventorySlotContents(0, ItemStackTools.getEmptyStack());
        }
    }

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 && CrystalFurnaceManager.getRecipe(itemStackIn) !=null;
	}

	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerCrystalFurnace(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiCrystalFurnace(player, this);
	}

}
