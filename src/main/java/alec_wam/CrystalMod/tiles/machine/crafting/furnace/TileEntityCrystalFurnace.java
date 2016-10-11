package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.util.ItemUtil;

public class TileEntityCrystalFurnace extends TileEntityMachine implements ISidedInventory {

	public TileEntityCrystalFurnace(){
		super("CrystalFurnace", 2);
	}
	
	public boolean canStart() {
        if (inventory[0] == null) {
            return false;
        }
        final BasicMachineRecipe recipe = CrystalFurnaceManager.getRecipe(inventory[0]);
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        final ItemStack output = recipe.getOutput();
        return output != null && (inventory[1] == null || (ItemUtil.canCombine(output, inventory[1]) && inventory[1].stackSize + output.stackSize <= output.getMaxStackSize()));
    }
	
	public boolean canFinish() {
        return processRem <= 0 && this.hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	final BasicMachineRecipe recipe = CrystalFurnaceManager.getRecipe(this.inventory[0]);
        return recipe != null && recipe.getInputSize() <= this.inventory[0].stackSize;
    }
    
    public void processStart() {
    	this.processMax = CrystalFurnaceManager.getRecipe(this.inventory[0]).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    public void processFinish() {
    	final ItemStack output = CrystalFurnaceManager.getRecipe(this.inventory[0]).getOutput();
        if (this.inventory[1] == null) {
            this.inventory[1] = output;
        }
        else {
            final ItemStack itemStack = this.inventory[1];
            itemStack.stackSize += output.stackSize;
        }
        final ItemStack itemStack2 = this.inventory[0];
        --itemStack2.stackSize;
        if (this.inventory[0].stackSize <= 0) {
            this.inventory[0] = null;
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
