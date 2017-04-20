package alec_wam.CrystalMod.tiles.darkinfection;

import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public class TileDarkInfection extends TileEntityMod {

	private int currentRadius;
	private int delay;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Radius", currentRadius);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.currentRadius = nbt.getInteger("Radius");
	}
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			if(delay > 0){
				if(!getWorld().isBlockPowered(getPos()))delay--;
			}
			
			if(delay <= 0){
				if(currentRadius < 64){
					delay = TimeUtil.SECOND * 1;
					currentRadius++;
					BlockUtil.createOrb(getWorld(), getPos(), Blocks.STONE.getDefaultState(), currentRadius, true, true);
				}
			}
		}
	}
	
}
