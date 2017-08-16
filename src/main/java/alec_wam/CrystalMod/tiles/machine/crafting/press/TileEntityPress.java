package alec_wam.CrystalMod.tiles.machine.crafting.press;

import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPress extends TileEntityMachine implements ISidedInventory {

	public TileEntityPress() {
		super("Press", 2);
	}
	
	@Override
	public boolean canStart() {
		ItemStack stack = getStackInSlot(0);
        if (ItemStackTools.isNullStack(stack)) {
            return false;
        }
        final BasicMachineRecipe recipe = PressRecipeManager.getRecipe(stack);
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
        return processRem <= 0 && hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	final BasicMachineRecipe recipe = PressRecipeManager.getRecipe(getStackInSlot(0));
        return recipe != null && recipe.getInputSize() <= ItemStackTools.getStackSize(getStackInSlot(0));
    }
    
    @Override
	public void processStart() {
    	this.processMax = PressRecipeManager.getRecipe(getStackInSlot(0)).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    @Override
	public void processFinish() {
    	ItemStack stack = getStackInSlot(0);
    	ItemStack stack2 = getStackInSlot(1);
    	BasicMachineRecipe recipe = PressRecipeManager.getRecipe(stack);
    	final ItemStack output = recipe.getOutput();
        if (ItemStackTools.isNullStack(stack2)) {
            setInventorySlotContents(1, output);
        }
        else {
            ItemStackTools.incStackSize(stack2, ItemStackTools.getStackSize(output));
        }
        setInventorySlotContents(0, ItemUtil.consumeItem(stack));
    }

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 && PressRecipeManager.getRecipe(itemStackIn) !=null;
	}

	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerPress(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiPress(player, this);
	}

}
