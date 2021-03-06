package alec_wam.CrystalMod.tiles.pipes.wireless;

import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TileEntityPipeWrapper extends TileEntityMod implements IPipeWrapper{

	public BlockPos connectionPos;
	public int connectionDim = 0;
	
	public EnumFacing pipeDir;
	public boolean isSender;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(connectionPos !=null){
			nbt.setInteger("ConX", connectionPos.getX());
			nbt.setInteger("ConY", connectionPos.getY());
			nbt.setInteger("ConZ", connectionPos.getZ());
		}
		nbt.setInteger("ConD", connectionDim);
		nbt.setBoolean("IsSender", isSender);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("ConX") && nbt.hasKey("ConY") && nbt.hasKey("ConZ")){
			connectionPos = new BlockPos(nbt.getInteger("ConX"), nbt.getInteger("ConY"), nbt.getInteger("ConZ"));
		}
		connectionDim = nbt.getInteger("ConD");
		isSender = nbt.getBoolean("IsSender");
	}
	
	@Override
	public World getOtherWorld(){
		try{
			return DimensionManager.getWorld(connectionDim);
		} catch(Exception e){}
		return null;
	}
	
	@Override
	public BlockPos getOtherPos(){
		return connectionPos;
	}
	
	@Override
	public boolean isSender(){
		return isSender;
	}

	@Override
	public void update() {
		super.update();
		if(getWorld().isRemote)return;
		final boolean lastSender = isSender;
		isSender = (getOtherWorld() != null && getOtherPos() != null && getOtherWorld().isBlockLoaded(getOtherPos()));
		if(lastSender !=isSender){
			/*if(isSender){
				TileEntity tile = getOtherWorld().getTileEntity(getOtherPos());
				if(tile !=null && tile instanceof TileEntityPipeWrapper){
					TileEntityPipeWrapper otherWrapper = (TileEntityPipeWrapper)tile;
					if(otherWrapper.connectionPos != getPos() || otherWrapper.connectionDim != getWorld().provider.getDimension()){
						otherWrapper.connectionPos = getPos();
						otherWrapper.connectionDim = getWorld().provider.getDimension();
						BlockUtil.markBlockForUpdate(getOtherWorld(), getOtherPos());
					}
				}
			}*/
			BlockUtil.markBlockForUpdate(getWorld(), pos);
		}
	}

}
