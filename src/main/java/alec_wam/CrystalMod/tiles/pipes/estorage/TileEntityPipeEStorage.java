package alec_wam.CrystalMod.tiles.pipes.estorage;

import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityPipeEStorage extends TileEntityPipe {
	
	public TileEntityPipeEStorage(){}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
	}
	
	@Override
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
	
	@Override
	public boolean canConnectToPipe(EnumFacing faceHit, TileEntityPipe neighbour) {
		return super.canConnectToPipe(faceHit, neighbour);
	}
	
	@Override
	public boolean canConnectToExternal(EnumFacing direction, boolean ignoreDisabled) {
	    TileEntity tile = getExternalTile(direction);
	    if (tile==null || this.getAttachmentData(direction) !=null) {
	      return false;
	    } 
	    
	    if(network != null && network instanceof EStorageNetwork) {
	    	if(!((EStorageNetwork)network).canConnect(tile))return false;
	    }
	    
	    return ignoreDisabled || getConnectionMode(direction) !=ConnectionMode.DISABLED;
	}
	
	@Override
	protected ConnectionMode getDefaultConnectionMode() {
		return ConnectionMode.INPUT;
	}
	
	@Override
	public ConnectionMode getNextConnectionMode(EnumFacing dir) {
		ConnectionMode curMode = getConnectionMode(dir);
		if (curMode == ConnectionMode.NOT_SET) {
			curMode = ConnectionMode.INPUT;
		}
		return curMode == ConnectionMode.INPUT ? ConnectionMode.DISABLED : ConnectionMode.INPUT;
	}

	@Override
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

	  @Override
	public void update(){
		  super.update();
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
	  
	@Override
	public boolean canEditAttachments(EntityPlayer player){
		if(network != null && network instanceof EStorageNetwork) {
			return ((EStorageNetwork)network).hasAbility(player, NetworkAbility.BUILD);
		}
		return true;
	}

	public static boolean isNetworkTile(TileEntity tile) {
		return (tile instanceof IPipeWrapper || tile instanceof INetworkTile || tile instanceof TileEntityPipeEStorage);
	}
	
	public static EStorageNetwork getTileNetwork(TileEntity tile){
		if(tile instanceof TileEntityPipeEStorage){
			AbstractPipeNetwork network = ((TileEntityPipeEStorage)tile).network;
			return network !=null && network instanceof EStorageNetwork ? (EStorageNetwork)network : null;
		}
		if(tile instanceof INetworkTile){
			return ((INetworkTile)tile).getNetwork();
		}
		return null;
	}
}
