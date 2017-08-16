package alec_wam.CrystalMod.tiles.endertorch;

import alec_wam.CrystalMod.handler.EventHandler;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class TileEnderTorch extends TileEntityMod {

	public double range = 8.0D;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setDouble("Range", range);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		range = nbt.getDouble("Range");
	}
	
	public boolean isActive(){
		return true;
	}
	
	public boolean inRange(Vec3d vec, boolean isTeleportTo){
		Vec3d thisVec = new Vec3d(getPos().getX() + 0.5D, getPos().getY() + 0.5D, getPos().getZ() + 0.5D);
		double dis = thisVec.distanceTo(vec);
		return dis <=(range/2);
	}
	
	@Override
	public void validate() {
		super.validate();
		EventHandler.addEnderTorch(getWorld().provider.getDimension(), getPos());
	}

	@Override
	public void invalidate() {
		super.invalidate();
		EventHandler.removeEnderTorch(getWorld().provider.getDimension(), getPos());
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		EventHandler.removeEnderTorch(getWorld().provider.getDimension(), getPos());
	}
	
}
