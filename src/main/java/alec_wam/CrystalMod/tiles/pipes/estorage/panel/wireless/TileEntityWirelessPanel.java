package alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless;

import alec_wam.CrystalMod.api.estorage.INetworkPowerTile;
import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityWirelessPanel extends TileEntityMod implements IMessageHandler, INetworkPowerTile {

	public EStorageNetwork network;
	public EnumFacing connectionDir;
	public boolean connected;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(connectionDir !=null){
			nbt.setInteger("ConDir", connectionDir.ordinal());
		}
		nbt.setBoolean("Connected", connected);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("ConDir")){
			connectionDir = EnumFacing.getFront(nbt.getInteger("ConDir"));
		}
		connected = nbt.getBoolean("Connected");
	}
	
	@Override
	public void update() {
		super.update();
		if(!getWorld().isRemote){
			if(this.connected == true && this.network == null){
				this.connected = false;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("Connected", false);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(this.getPos(), "Connected", nbt), this);
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
			if(this.connected == false && this.network != null){
				this.connected = true;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("Connected", true);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(this.getPos(), "Connected", nbt), this);
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
		}
		
		
		if(network == null){
			if(getWorld().isRemote){
				network = new EStorageNetworkClient();
				return;
			}
			
			if(connectionDir !=null){
				TileEntity tile = getWorld().getTileEntity(getPos().offset(connectionDir));
				if(tile !=null && tile instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage pipe = (TileEntityPipeEStorage)tile;
					if(pipe.getConnectionMode(connectionDir.getOpposite()) == ConnectionMode.INPUT){
						if(pipe.network !=null && pipe.network instanceof EStorageNetwork){
							network = (EStorageNetwork) pipe.network;
						}
					}
					if(network !=null){
						return;
					}
				}
			}
			for(EnumFacing dir : EnumFacing.VALUES){
				TileEntity tile = getWorld().getTileEntity(getPos().offset(dir));
				if(tile !=null && tile instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage pipe = (TileEntityPipeEStorage)tile;
					if(pipe.getConnectionMode(dir.getOpposite()) == ConnectionMode.INPUT){
						if(pipe.network !=null && pipe.network instanceof EStorageNetwork){
							network = (EStorageNetwork) pipe.network;
							connectionDir = dir;
						}
					}
					if(network !=null){
						break;
					}
				}
			}
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("ResetNet")){
			this.network = null;
		}
		if(messageId.equalsIgnoreCase("Connected")){
			if(messageData.getBoolean("Connected")){
				connected = true;
			}else {
				connected = false;
			}
		}
	}

	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
		if(network == null){
			if(getWorld() !=null && !getWorld().isRemote){
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "ResetNet"), this);
			}
		}
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {}
	
	@Override
	public int getEnergyUsage() {
		return 16;
	}
	
	public final ItemStack displayStack = new ItemStack(ModBlocks.wirelessPanel);
	
	@Override
	public ItemStack getDisplayStack(){
		return displayStack;
	}

}
