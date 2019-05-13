package alec_wam.CrystalMod.tiles.pipes;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PipeNetworkBuilder {

	public static void buildNetwork(TileEntityPipeBase pipe){
		World world = pipe.getWorld();
		BlockPos pos = pipe.getPos();
		List<PipeNetworkBase<?>> otherNetworks = Lists.newArrayList();
		for(EnumFacing facing : EnumFacing.values()){
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile instanceof TileEntityPipeBase){
				TileEntityPipeBase otherPipe = (TileEntityPipeBase)tile;
				if(otherPipe.getNetworkType() == pipe.getNetworkType()){
					if(otherPipe.getNetwork() !=null){
						if(otherPipe.getConnectionSetting(facing.getOpposite()) !=PipeConnectionMode.DISABLED){
							otherNetworks.add(otherPipe.getNetwork());
						}
					}
				}
			}
		}
		
		if(otherNetworks.size() == 1){
			PipeNetworkBase<?> network = otherNetworks.get(0);
			if(network.addPipe(pipe)){
				pipe.setNetwork(network);
			}
		} else {
			//Merge networks
			for(PipeNetworkBase<?> otherNet : otherNetworks){
				otherNet.resetNetwork();
			}
			//Create new network
			PipeNetworkBase<?> network = pipe.createNewNetwork();
			network.addPipe(pipe);
			pipe.setNetwork(network);
			List<BlockPos> visited = Lists.newArrayList();
			visited.add(pos);
			for(EnumFacing facing : EnumFacing.values()){
				if(pipe.getConnectionSetting(facing) !=PipeConnectionMode.DISABLED){
					scanPipes(network, pipe.getNetworkType(), world, pos.offset(facing), facing, visited);
				}
			}
			PipeNetworkTickHandler.INSTANCE.NETWORKS.add(network);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TileEntityPipeBase> void scanPipes(PipeNetworkBase<T> network, NetworkType type, World world, BlockPos pos, EnumFacing from, List<BlockPos> visited){
		if(!world.isBlockLoaded(pos)) return;
		visited.add(pos);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityPipeBase){
			TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
			if(pipe.getNetworkType() == type){
				if(pipe.getConnectionSetting(from) !=PipeConnectionMode.DISABLED){
					if(network.addPipe((T) pipe)){
						pipe.setNetwork(network);
						
						for(EnumFacing facing : pipe.pipeConnections){
							if(from !=null && facing.getOpposite() == from)continue;
							BlockPos offset = pos.offset(facing);
							if(!visited.contains(offset)){
								scanPipes(network, type, world, offset, facing, visited);
							}
						}						
					}
				}
			}
		}
	}
	
	public static void linkPipes(TileEntityPipeBase pipe, EnumFacing face){
		//TODO Set Default for tile
		pipe.setConnectionSetting(face, PipeConnectionMode.IN);
		
		TileEntity tile = pipe.getWorld().getTileEntity(pipe.getPos().offset(face));
		if(tile instanceof TileEntityPipeBase){
			TileEntityPipeBase otherPipe = (TileEntityPipeBase)tile;
			otherPipe.setConnectionSetting(face.getOpposite(), PipeConnectionMode.IN);
			otherPipe.rebuildConnections = true;
			otherPipe.serverDirty = true;
		}
		
		pipe.setNetwork(null);
		pipe.rebuildConnections = true;
		pipe.serverDirty = true;
	}
	
	public static void unlinkPipes(TileEntityPipeBase pipe, EnumFacing face){
		pipe.setConnectionSetting(face, PipeConnectionMode.DISABLED);
		pipe.pipeConnections.remove(face);
		pipe.getNetwork().resetNetwork();
		pipe.rebuildConnections = true;
		
		TileEntity tile = pipe.getWorld().getTileEntity(pipe.getPos().offset(face));
		if(tile instanceof TileEntityPipeBase){
			TileEntityPipeBase otherPipe = (TileEntityPipeBase)tile;
			otherPipe.setConnectionSetting(face.getOpposite(), PipeConnectionMode.DISABLED);
			otherPipe.pipeConnections.remove(face.getOpposite());
			if(otherPipe.getNetwork() !=null)otherPipe.getNetwork().resetNetwork();
			otherPipe.rebuildConnections = true;
		}
	}
	
}
