package alec_wam.CrystalMod.tiles.workbench;

import alec_wam.CrystalMod.tiles.TileEntityInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileEntityCrystalWorkbench extends TileEntityInventory implements ISidedInventory{

	public TileEntityCrystalWorkbench(){
		super("CrystalWorkbench", 10);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if(getWorld().getTileEntity(pos) != this || getWorld().getBlockState(pos).getBlock() == Blocks.AIR) {
	      return false;
	    }

	    return
	    	player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D)
	        <= 64D;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.UP ? new int[]{0} : new int[]{1,2,3,4,5,6,7,8,9};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return direction !=EnumFacing.UP && index > 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return direction == EnumFacing.UP && index == 0;
	}

}
