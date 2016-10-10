package com.alec_wam.CrystalMod.tiles.pipes.estorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import com.alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import com.alec_wam.CrystalMod.tiles.pipes.PipeUtil;
import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.types.IPipeType;

public class TileEntityPipeEStorage extends TileEntityPipe {
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
	}
	
	@Override
	public IPipeType getPipeType() {
		return EStorageType.INSTANCE;
	}

	@Override
	public AbstractPipeNetwork createNetwork() {
		return new EStorageNetwork();
	}
	
	public boolean canConnectToPipe(EnumFacing faceHit, TileEntityPipe neighbour) {
		return super.canConnectToPipe(faceHit, neighbour);
	}
	
	public boolean canConnectToExternal(EnumFacing direction, boolean ignoreDisabled) {
	    TileEntity tile = getExternalTile(direction);
	    if (tile==null || this.getAttachmentData(direction) !=null) {
	      return false;
	    } 
	    return ignoreDisabled || getConnectionMode(direction) !=ConnectionMode.DISABLED;
	}

	public boolean onActivated(EntityPlayer player, EnumFacing side, EnumHand hand) {
		 
		 
		 /*if (closest != null) {
				if (closest.component != null) {
					if (closest.component.dir != null) {
						EnumFacing dir = closest.component.dir;
						if (closest.component.data instanceof PipePart) {
							PipePart part = (PipePart) closest.component.data;

							if (part == PipePart.ATTACHMENT) {
								if(hasWrench){
									this.setAttachment(dir, null);
									return true;
								}
							}
						}
					}
				}
			}*/
			if(super.onActivated(player, side, hand))return true;
			
	    return false;
	}
	
	protected ConnectionMode getDefaultConnectionMode() {
		return ConnectionMode.INPUT;
	}
	
	public ConnectionMode getNextConnectionMode(EnumFacing dir) {
		ConnectionMode curMode = getConnectionMode(dir);
		if (curMode == ConnectionMode.NOT_SET) {
			curMode = ConnectionMode.INPUT;
		}
		return curMode == ConnectionMode.INPUT ? ConnectionMode.DISABLED : ConnectionMode.INPUT;
	}

	public ConnectionMode getPreviousConnectionMode(EnumFacing dir) {
		ConnectionMode curMode = getConnectionMode(dir);
		if (curMode == ConnectionMode.NOT_SET) {
			curMode = ConnectionMode.DISABLED;
		}
		return curMode == ConnectionMode.INPUT ? ConnectionMode.DISABLED : ConnectionMode.INPUT;
	}
	
	@Override
	  public void externalConnectionAdded(EnumFacing direction) {
	    super.externalConnectionAdded(direction);
	    if(network != null && network instanceof EStorageNetwork) {
	      BlockPos p = getPos().offset(direction);
	      ((EStorageNetwork)network).tileAdded(this, direction, p, getExternalTile(direction));
	    }
	  }

	  public TileEntity getExternalTile(EnumFacing direction) {
	    World world = getWorld();
	    if(world == null) {
	      return null;
	    }
	    BlockPos loc = getPos().offset(direction);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null && (te instanceof IPipeWrapper || te instanceof INetworkTile)) {
	      return te;
	    }
	    return null;
	  }

	  @Override
	  public void externalConnectionRemoved(EnumFacing direction) {
		//IPipeWrapper wrapper = (getExternalTile(direction) !=null && getExternalTile(direction) instanceof IPipeWrapper) ? (IPipeWrapper) getExternalTile(direction) : null;
		externalConnections.remove(direction);
	    connectionsChanged();
	    if(network != null && network instanceof EStorageNetwork) {
	      BlockPos p = getPos().offset(direction);
	      ((EStorageNetwork)network).tileRemoved(this, p);
	    }
	    /*if(wrapper !=null/* && !wrapper.isSender()){
	    	this.setNetwork(null);
	    }*/
	  }

	  public void update(){
		  super.update();
		  if(!getWorld().isRemote){
			  if(getWorld().isBlockLoaded(getPos())){
					AbstractPipeNetwork oNetwork = null;
				    for(EnumFacing face : externalConnections){
			    		TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
			    		if(tile !=null && tile instanceof IPipeWrapper){
			    			IPipeWrapper wrapper = ((IPipeWrapper)tile);
			    			if(!wrapper.isSender()){
			    				if(wrapper.getPipe() !=null){
			    					oNetwork = wrapper.getPipe().getNetwork();
			    					break;
			    				}
			    			}
			    		}
				    }
				    
				    if(oNetwork !=null && oNetwork !=getNetwork()){
				    	if(this.network !=null)network.destroyNetwork();
				    	oNetwork.init(this, PipeUtil.getConnectedPipes(getWorld(), getPos(), getPipeType()), getWorld());
				    }
				}
		  }
	  }
	  
	  @Override
	  public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
	    ConnectionMode oldVal = conectionModes.get(dir);
	    if(oldVal == mode) {
	      return;
	    }
	    super.setConnectionMode(dir, mode);
	    
	    
	    if (!containsPipeConnection(dir) && canConnectToExternal(dir, false)) {
			externalConnectionAdded(dir);
			connectionsChanged();
			dirty();
		}else{
			if(this.containsExternalConnection(dir)){
				externalConnectionRemoved(dir);
				connectionsChanged();
				dirty();
			}
		}
	  }
	
}
