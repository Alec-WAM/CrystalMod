package alec_wam.CrystalMod.tiles.explosives.fuser;

import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.nbt.NBTTagCompound;

public class TileOppositeFuser extends TileEntityMod {

	public boolean facingNS;
	public boolean hasPure;
	public boolean hasDark;
	public int fuseTime;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setBoolean("NorthSouth", facingNS);
		nbt.setBoolean("hasPure", hasPure);
		nbt.setBoolean("hasDark", hasDark);
		nbt.setInteger("Fuse", fuseTime);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facingNS = nbt.getBoolean("NorthSouth");
		hasPure = nbt.getBoolean("hasPure");
		hasDark = nbt.getBoolean("hasDark");
		fuseTime = nbt.getInteger("Fuse");
	}
	
	
	public void triggerExplosion(){
		this.fuseTime = 30 * TimeUtil.SECOND;
		BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}
	
	@Override
	public void update(){
		super.update();
		
		if(hasPure && hasDark){
			if(fuseTime > 0){
				fuseTime--;
				if(fuseTime <= 0){
					if(!getWorld().isRemote){
						explode();
					}
				}
			}
		}
	}

	public void explode() {
		getWorld().createExplosion(null, getPos().getX() + 0.5D, getPos().getY() + 0.5D, getPos().getZ() + 0.5D, 32F, true);
	}
	
}
