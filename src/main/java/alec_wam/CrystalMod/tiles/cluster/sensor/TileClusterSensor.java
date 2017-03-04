package alec_wam.CrystalMod.tiles.cluster.sensor;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.CompareType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileClusterSensor extends TileEntityMod implements IMessageHandler, IFacingTile {

	private EnumFacing facing = EnumFacing.NORTH;
	public WatchType currentWatchType = WatchType.HEALTH;
	public CompareType compareType = CompareType.EQUALS;
	public int lastOutRedstonePower;
	public int outRedstonePower;
	public int redstonePower = 15;
	public int watchValue;
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			this.outRedstonePower = 0;
			BlockPos facingPos = getPos().offset(facing);
			TileEntity tile = getWorld().getTileEntity(facingPos);
			if(tile !=null && tile instanceof TileCrystalCluster){
				TileCrystalCluster cluster = (TileCrystalCluster)tile;
				if(currentWatchType == WatchType.HEALTH){
					if(compareType.passes(cluster.getHealth(), watchValue)){
						this.outRedstonePower = redstonePower;
					}
				} else if(currentWatchType == WatchType.CURRENT_OUTPUT){
					if(this.compareType.passes(cluster.getPowerOutput(), watchValue)){
						this.outRedstonePower = redstonePower;
					}
				} 
			}
			
			if(this.lastOutRedstonePower !=this.outRedstonePower){
				this.lastOutRedstonePower = this.outRedstonePower;
				notifyBlocks(getWorld(), getPos(), facing);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Output", outRedstonePower);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Redstone", nbt), this);
			}
		}
	}
	
	public void notifyBlocks(World world, BlockPos pos, EnumFacing dir){
		BlockPos bc2 = pos.offset(dir);
		world.scheduleUpdate(pos, getBlockType(), getBlockType().tickRate(world));
		if (world.isBlockLoaded(bc2)) {
			world.notifyNeighborsOfStateChange(bc2, getBlockType(), true);
			
			IBlockState bs = world.getBlockState(bc2);
			if (bs.isBlockNormalCube()) {
				for (EnumFacing dir2 : EnumFacing.VALUES) {
					BlockPos bc3 = bc2.offset(dir2);
					if (!bc3.equals(pos) && world.isBlockLoaded(bc3)) {
						world.notifyNeighborsOfStateChange(bc3, getBlockType(), true);
					}
				}
			}
		}
	}
	
	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}
	
	@Override
	public boolean useVerticalFacing(){
		return true;
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		
	}
	
	public static enum WatchType {
		HEALTH, CURRENT_OUTPUT;
	}	

}
