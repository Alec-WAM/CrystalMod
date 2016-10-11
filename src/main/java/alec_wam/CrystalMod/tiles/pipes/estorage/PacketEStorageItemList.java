package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;

public class PacketEStorageItemList extends AbstractPacketThreadsafe {

	private int x;
	private int y;
	private int z;
	private int type;
	private byte[] compressed;
	
	public PacketEStorageItemList(){}
	
    public PacketEStorageItemList(BlockPos pos, int type, byte[] compressed){
    	x = pos.getX();
    	y = pos.getY();
    	z = pos.getZ();
    	this.type = type;
    	this.compressed = compressed;
    }
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		type = buffer.readInt();
		compressed = readByteArray(buffer);
	}
	
	public static byte[] readByteArray(ByteBuf buf) {
	    int size = buf.readMedium();
	    byte[] res = new byte[size];
	    buf.readBytes(res);
	    return res;
	}

	public static void writeByteArray(ByteBuf buf, byte[] arr) {
	    buf.writeMedium(arr.length);
	    buf.writeBytes(arr);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(type);
		writeByteArray(buffer, compressed);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		World world = CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().worldObj;
		if(world == null){
			return;
		}
		EStorageNetwork network = null;
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile !=null){
			if(tile instanceof TileEntityPanel){
				TileEntityPanel panel = (TileEntityPanel)tile;
				if(panel.network !=null){
					network = panel.network;
				}
			}else if(tile instanceof TileEntityWirelessPanel){
				TileEntityWirelessPanel panel = (TileEntityWirelessPanel)tile;
				if(panel.network !=null){
					network = panel.network;
				}
			}
		}
		if(network !=null){
			try {
				List<ItemStackData> data = EStorageNetwork.decompressItems(compressed);
				if(type == 0){
					network.items = data;
				}
				if(type == 1){
					for(ItemStackData itemData : data){
						boolean edited = false;
						ArrayList<ItemStackData> copy = new ArrayList<ItemStackData>();
						copy.addAll(network.items);
						for(ItemStackData storedData : copy){
							  if(storedData.interPos !=null && storedData.interPos.equals(itemData.interPos) && storedData.interDim == itemData.interDim){
								  if(storedData.index == itemData.index){
									  storedData.stack = itemData.stack;
									  edited = true;
									  if(storedData.stack == null){
										  network.items.remove(data);
									  }
									  continue;
								  }
							  }
						  }
						  if(itemData !=null && edited == false){
							  network.items.add(itemData);
						  }
					}
				}
				if(type == 3){
					if(network instanceof EStorageNetworkClient){
						((EStorageNetworkClient)network).craftingItems = data;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		if(type == 0){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				if(pan !=null && pan.getNetwork() !=null){
					pan.getNetwork().updateItems = true;
				}
			}
		}
		if(type == 1){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				((INetworkContainer)con).sendItemsToAll();
			}
		}
		if(type == 2){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				((INetworkContainer)con).sendItemsTo(netHandler.playerEntity);
			}
		}
	}

}
