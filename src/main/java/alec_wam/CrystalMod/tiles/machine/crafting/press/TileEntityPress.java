package alec_wam.CrystalMod.tiles.machine.crafting.press;

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

public class TileEntityPress extends TileEntityMachine implements ISidedInventory {

	public TileEntityPress() {
		super("Press", 2);
	}
	
	public boolean canStart() {
        if (ItemStackTools.isNullStack(inventory[0])) {
            return false;
        }
        final BasicMachineRecipe recipe = PressRecipeManager.getRecipe(inventory[0]);
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        final ItemStack output = recipe.getOutput();
        return ItemStackTools.isValid(output) && (ItemStackTools.isNullStack(inventory[1]) || (ItemUtil.canCombine(output, inventory[1]) && ItemStackTools.getStackSize(inventory[1]) + ItemStackTools.getStackSize(output) <= output.getMaxStackSize()));
    }
	
	public boolean canFinish() {
        return processRem <= 0 && hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	final BasicMachineRecipe recipe = PressRecipeManager.getRecipe(this.inventory[0]);
        return recipe != null && recipe.getInputSize() <= ItemStackTools.getStackSize(this.inventory[0]);
    }
    
    public void processStart() {
    	this.processMax = PressRecipeManager.getRecipe(this.inventory[0]).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    public void processFinish() {
    	BasicMachineRecipe recipe = PressRecipeManager.getRecipe(this.inventory[0]);
    	final ItemStack output = recipe.getOutput();
        if (ItemStackTools.isNullStack(this.inventory[1])) {
            this.inventory[1] = output;
        }
        else {
            final ItemStack itemStack = this.inventory[1];
            ItemStackTools.incStackSize(itemStack, ItemStackTools.getStackSize(output));
        }
        final ItemStack itemStack2 = this.inventory[0];
        ItemStackTools.incStackSize(itemStack2, -recipe.getInputSize());
        if (ItemStackTools.isEmpty(itemStack2)) {
            this.inventory[0] = ItemStackTools.getEmptyStack();
        }
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
