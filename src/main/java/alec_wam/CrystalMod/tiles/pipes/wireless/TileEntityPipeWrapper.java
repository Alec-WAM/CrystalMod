package alec_wam.CrystalMod.tiles.pipes.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.util.BlockUtil;

public class TileEntityPipeWrapper extends TileEntityMod implements IPipeWrapper{

	public BlockPos connectionPos;
	public int connectionDim = 0;
	
	public EnumFacing pipeDir;
	public boolean isSender;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(connectionPos !=null){
			nbt.setInteger("ConX", connectionPos.getX());
			nbt.setInteger("ConY", connectionPos.getY());
			nbt.setInteger("ConZ", connectionPos.getZ());
		}
		nbt.setInteger("ConD", connectionDim);
		if(pipeDir !=null)nbt.setInteger("PipeDir", pipeDir.ordinal());
		nbt.setBoolean("Sender", isSender);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("ConX") && nbt.hasKey("ConY") && nbt.hasKey("ConZ")){
			connectionPos = new BlockPos(nbt.getInteger("ConX"), nbt.getInteger("ConY"), nbt.getInteger("ConZ"));
		}
		connectionDim = nbt.getInteger("ConD");
		if(nbt.hasKey("PipeDir")){
			pipeDir = EnumFacing.getFront(nbt.getInteger("PipeDir"));
		}
		if(nbt.hasKey("Sender")){
			isSender = nbt.getBoolean("Sender");
		}
	}
	
	@Override
	public TileEntityPipe getPipe(){
		if(getWorld() == null || getWorld().isRemote || DimensionManager.getWorld(connectionDim) == null){
			return null;
		}
		World world = getWorld().provider.getDimensionType().getId() !=connectionDim ? DimensionManager.getWorld(connectionDim) : getWorld();
		if(world !=null && connectionPos !=null && world.isBlockLoaded(connectionPos)){
			
			TileEntity tile = world.getTileEntity(connectionPos);
			if(tile !=null && tile instanceof IPipeWrapper){
				IPipeWrapper wrapper = (IPipeWrapper) tile;
				if(wrapper.isSender() == false || wrapper == null || tile.getPos() == null || wrapper.getPipeDir() == null){
					return null;
				}
				TileEntity tile2 = world.getTileEntity(tile.getPos().offset(wrapper.getPipeDir()));
				if(tile2 !=null && tile2 instanceof TileEntityPipe){
					TileEntityPipe pipe = (TileEntityPipe) tile2;
					if(pipe.canConnectToExternal(wrapper.getPipeDir().getOpposite(), false))
					return pipe;
				}
			}
		}
		return null;
	}
	
	public boolean isSender(){
		return isSender;
	}
	
	public EnumFacing getPipeDir(){
		return pipeDir;
	}

	@Override
	public void update() {
		super.update();
		if(getWorld().isRemote)return;
		this.isSender = this.connectionPos == null;
		
		/*if(((Boolean)getWorld().getBlockState(getPos()).getValue(BlockWirelessPipeWrapper.SENDER)).booleanValue() !=isSender){
			getWorld().setBlockState(pos, getWorld().getBlockState(getPos()).withProperty(BlockWirelessPipeWrapper.SENDER, Boolean.valueOf(isSender)), 3);
		}*/
		
		if(isSender){
			if(pipeDir == null){
				for(EnumFacing face : EnumFacing.VALUES){
					TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
					if(tile !=null && tile instanceof TileEntityPipe){
						TileEntityPipe pipe = (TileEntityPipe) tile;
						
						if(pipe.canConnectToExternal(face.getOpposite(), false)){
							pipeDir = face;
							if(this.worldObj.isBlockLoaded(getPos())){
								BlockUtil.markBlockForUpdate(getWorld(), getPos());
							}else markDirty();
							break;
						}
					}
				}
			}else{
				TileEntity tile = getWorld().getTileEntity(getPos().offset(pipeDir));
				if(tile == null || !(tile instanceof TileEntityPipe) || !((TileEntityPipe)tile).canConnectToExternal(pipeDir.getOpposite(), false)){
					pipeDir = null;
					if(this.worldObj.isBlockLoaded(getPos())){
						BlockUtil.markBlockForUpdate(getWorld(), getPos());
					}else markDirty();
				}
			}
		}else{
			if(pipeDir != null){
				pipeDir = null;
				if(this.worldObj.isBlockLoaded(getPos())){
					BlockUtil.markBlockForUpdate(getWorld(), getPos());
				}else markDirty();
			}
		}
	}

}
