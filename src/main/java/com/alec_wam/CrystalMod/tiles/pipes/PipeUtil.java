package com.alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import com.alec_wam.CrystalMod.util.BlockUtil;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PipeUtil {

	@SuppressWarnings({ })
	  public static void ensureValidNetwork(TileEntityPipe pipe) {
		World world = pipe.getWorld();
	    Collection<TileEntityPipe> connections = PipeUtil.getConnectedPipes(world, pipe.getPos(), pipe.getPipeType());

	    if (reuseNetwork(pipe, connections, world)) {
	      return;
	    }

	    AbstractPipeNetwork res = pipe.createNetwork();
	    res.init(pipe, connections, world);
	    return;
	  }

	  private static boolean reuseNetwork(TileEntityPipe pip, Collection<TileEntityPipe> connections, World world) {
	    AbstractPipeNetwork network = null;
	    /*if(pip instanceof TileEntityPipeEStorage){
		    for(EnumFacing face : pip.externalConnections){
	    		TileEntity tile = world.getTileEntity(pip.getPos().offset(face));
	    		if(pip.canConnectToExternal(face, false)){
		    		if(tile !=null && tile instanceof IPipeWrapper){
		    			if(!((IPipeWrapper)tile).isSender() && ((IPipeWrapper)tile).getPipe() !=null){
		    				network = ((IPipeWrapper)tile).getPipe().getNetwork();
		    				break;
		    			}
		    		}
	    		}
	    	}
	    }*/
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
		    /*if (te !=null && te instanceof IPipeWrapper) {
		      TileEntityPipe pip = ((IPipeWrapper) te).getPipe();
		      return pip == null ? null : type == null ? pip : pip.getPipeType().getClass() == type.getClass() ? pip : null;
		    }*/
		    return null;
		  }

		  public static TileEntityPipe getPipe(World world, TileEntity te, EnumFacing dir, IPipeType type) {
		    return PipeUtil.getPipe(world, te.getPos().offset(dir), type);
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
	
}
