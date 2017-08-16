package alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PipeUtil {

	public static class PipeConnection {
		private EnumFacing facing;
		private TileEntityPipe pipe;
		
		public PipeConnection(EnumFacing facing, TileEntityPipe pipe){
			this.facing = facing;
			this.pipe = pipe;
		}
		
		public EnumFacing getFacing(){
			return facing;
		}
		
		public TileEntityPipe getPipe(){
			return pipe;
		}
		
		@Override
		public boolean equals(Object obj){
			if(obj == null || !(obj instanceof PipeConnection))return false;
			PipeConnection other = (PipeConnection)obj;
			if(getFacing() != other.getFacing()) return false;
			if(getPipe() == null && other.getPipe() !=null || getPipe() !=null && other.getPipe() == null)return false;
			if(getPipe().getPos() != other.getPipe().getPos()) return false;
			if(getPipe().getWorld().provider.getDimension() != other.getPipe().getWorld().provider.getDimension())return false;
			return true;
		}
		
	}
	
	@SuppressWarnings({ })
	public static void ensureValidNetwork(TileEntityPipe pipe) {
		World world = pipe.getWorld();
		List<TileEntityPipe> connections = Lists.newArrayList();
		PipeUtil.buildNetwork(connections, world, pipe.getPos(), null, pipe.getPipeType());
		//Collection<TileEntityPipe> connections = PipeUtil.getConnectedPipes(world, pipe.getPos(), pipe.getPipeType());

		if (reuseNetwork(pipe, connections, world)) {
			return;
		}

		AbstractPipeNetwork res = pipe.createNetwork();
		res.init(pipe, connections, world);
		return;
	}

	private static boolean reuseNetwork(TileEntityPipe pip, Collection<TileEntityPipe> connections, World world) {
		AbstractPipeNetwork network = null;
		if(network == null){
			for (TileEntityPipe pipe : connections) {
				if (network == null) {
					network = pipe.getNetwork();
				} else if (network != pipe.getNetwork()) {
					return false;
				}
			}
		}
		if (network == null) {
			return false;
		}
		if (pip.setNetwork(network)) {
			network.addPipe(pip);
			network.notifyNetworkOfUpdate();
			return true;
		}
		return false;
	}

	public static void disconectPipes(TileEntityPipe pip, EnumFacing connDir) {
		pip.pipeConnectionRemoved(connDir);
		BlockPos loc = pip.getPos().offset(connDir);
		TileEntityPipe neighbour = PipeUtil.getPipe(pip.getWorld(), loc, pip.getPipeType());
		if (neighbour != null) {
			neighbour.pipeConnectionRemoved(connDir.getOpposite());
			if (neighbour.getNetwork() != null) {
				neighbour.getNetwork().destroyNetwork();
			}
		}
		if (pip.getNetwork() != null) { // this should have been destroyed when
			// destroying the neighbours network but
			// lets just make sure
			pip.getNetwork().destroyNetwork();
		}
		pip.connectionsChanged();
		BlockUtil.markBlockForUpdate(pip.getWorld(), pip.getPos());
		if (neighbour != null) {
			neighbour.connectionsChanged();
			BlockUtil.markBlockForUpdate(neighbour.getWorld(), neighbour.getPos());
		}
	}

	public static boolean joinPipes(TileEntityPipe pip, EnumFacing faceHit) {
		BlockPos loc = pip.getPos().offset(faceHit);
		TileEntityPipe neighbour = PipeUtil.getPipe(pip.getWorld(), loc, pip.getPipeType());
		if (neighbour != null && pip.canConnectToPipe(faceHit, neighbour) && neighbour.canConnectToPipe(faceHit.getOpposite(), pip)) {
			pip.pipeConnectionAdded(faceHit);
			neighbour.pipeConnectionAdded(faceHit.getOpposite());
			if (pip.getNetwork() != null) {
				pip.getNetwork().destroyNetwork();
			}
			if (neighbour.getNetwork() != null) {
				neighbour.getNetwork().destroyNetwork();
			}
			pip.connectionsChanged();
			neighbour.connectionsChanged();
			return true;
		}
		return false;
	}

	public static TileEntityPipe getPipe(IBlockAccess world, BlockPos pos, IPipeType type) {
		if (world == null) {
			return null;
		}
		TileEntity te = world.getTileEntity(pos);
		if (te !=null && te instanceof TileEntityPipe) {
			TileEntityPipe pip = (TileEntityPipe) te;
			return type == null ? pip : pip.getPipeType().getClass() == type.getClass() ? pip : null;
		}
		return null;
	}

	public static TileEntityPipe getPipe(World world, TileEntity te, EnumFacing dir, IPipeType type) {
		return getPipe(world, te.getPos().offset(dir), type);
	}

	public static Collection<TileEntityPipe> getConnectedPipes(World world, BlockPos pos, IPipeType type) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityPipe)) {
			return Collections.emptyList();
		}
		List<TileEntityPipe> result = new ArrayList<TileEntityPipe>();
		TileEntityPipe pip = (TileEntityPipe) te;
		if (pip != null) {
			for (EnumFacing dir : pip.getPipeConnections()) {
				TileEntityPipe connected = getPipe(world, pip, dir, type);
				if (connected != null) {
					result.add(connected);
				}
			}
		}
		return result;
	}
	
	public static void buildNetwork(List<TileEntityPipe> pipes, World world, BlockPos pos, @Nullable EnumFacing from, @Nullable IPipeType type){
		TileEntity sourceTile = world.getTileEntity(pos);
		TileEntityPipe sourcePipe = null;
		if(sourceTile !=null && sourceTile instanceof TileEntityPipe){
			sourcePipe = (TileEntityPipe)sourceTile;
		}
		master : for(EnumFacing facing : EnumFacing.VALUES){
			if(from !=null && facing == from)continue;
			BlockPos offsetPos = pos.offset(facing);
			TileEntity tile = world.getTileEntity(offsetPos);
			if(tile !=null){
				if(tile instanceof TileEntityPipe){
					TileEntityPipe pipe = (TileEntityPipe)tile;
					if(sourcePipe !=null){
						if(!sourcePipe.containsPipeConnection(facing))continue master;
						if(!sourcePipe.canConnectToPipe(facing, pipe)){
							continue master;
						}
					}
					if(type == null || pipe.getPipeType() !=null && pipe.getPipeType() == type){
						if(!containsPipe(pipes, pipe)){
							pipes.add(pipe);
							//Continue Searching
							if(pos != offsetPos && (from == null || from.getIndex() != facing.getOpposite().getIndex())){
								buildNetwork(pipes, world, offsetPos, facing.getOpposite(), type);
							}
						}
					}
				}
				
				if(tile instanceof IPipeWrapper){
					if(sourcePipe !=null){
						if(sourcePipe.getConnectionMode(facing) == ConnectionMode.DISABLED){
							continue master;
						}
					}
					IPipeWrapper wrapper = (IPipeWrapper)tile;
					World otherWorld = wrapper.getOtherWorld();
					BlockPos otherPos = wrapper.getOtherPos();
					if(otherWorld !=null && otherPos !=null && otherWorld.isBlockLoaded(otherPos)){
						//Search around all of the wrapper's connection
						otherSearch : for(EnumFacing otherFacing : EnumFacing.VALUES){
							BlockPos offsetOtherPos = otherPos.offset(otherFacing);
							TileEntity otherTile = otherWorld.getTileEntity(offsetOtherPos);
							if(otherTile !=null && otherTile instanceof TileEntityPipe){
								TileEntityPipe pipe = (TileEntityPipe)otherTile;
								if(pipe.getConnectionMode(otherFacing.getOpposite()) == ConnectionMode.DISABLED){
									continue otherSearch;
								}
								if(sourcePipe !=null){
									if(!sourcePipe.canConnectToPipe(otherFacing, pipe)){
										continue otherSearch;
									}
								}
								if(type == null || pipe.getPipeType() !=null && pipe.getPipeType() == type){
									if(!containsPipe(pipes, pipe)){
										pipes.add(pipe);
										//Continue Searching
										buildNetwork(pipes, otherWorld, offsetOtherPos, otherFacing.getOpposite(), type);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean containsPipe(List<TileEntityPipe> list, TileEntityPipe pipe){
		for(TileEntityPipe oPipe : list){
			if(oPipe.getPos() == pipe.getPos()){
				World oWorld = oPipe.getWorld();
				if(oWorld !=null){
					if(oWorld.provider.getDimension() == pipe.getWorld().provider.getDimension()){
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

}
