package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;
import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.api.estorage.security.SecurityData;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketEStorageItemList extends AbstractPacketThreadsafe {

	public static enum EnumListType {
		UPDATE, ITEM, ITEM_ALL, CRAFTING, SECURITY;
	}
	
	private int x;
	private int y;
	private int z;
	private EnumListType type;
	private byte[] compressed;
	
	public PacketEStorageItemList(){}
	
    public PacketEStorageItemList(BlockPos pos, EnumListType type, byte[] compressed){
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
		type = EnumListType.values()[buffer.readInt()];
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
		buffer.writeInt(type.ordinal());
		writeByteArray(buffer, compressed);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		World world = CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().getEntityWorld();
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
			if(type == EnumListType.SECURITY){
				try {
					SecurityData data = SecurityData.decompress(compressed);
					if(network instanceof EStorageNetworkClient){
						((EStorageNetworkClient)network).clientSecurityData = data;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					List<ItemStackData> data = EStorageNetwork.decompressItems(compressed);
					if(type == EnumListType.ITEM || type == EnumListType.ITEM_ALL){
						network.getItemStorage().setItemList(data);
						if(network instanceof EStorageNetworkClient){
							((EStorageNetworkClient)network).needsListUpdate = true;
						}
					}
					if(type == EnumListType.CRAFTING){
						if(network instanceof EStorageNetworkClient){
							((EStorageNetworkClient)network).craftingItems = data;
							((EStorageNetworkClient)network).needsListUpdate = true;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		if(type == EnumListType.UPDATE){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				if(pan !=null && pan.getNetwork() !=null){
					pan.getNetwork().updateItems = true;
				}
			}
		}
		if(type == EnumListType.ITEM_ALL){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				((INetworkContainer)con).sendItemsToAll();
			}
		}
		if(type == EnumListType.ITEM){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				((INetworkContainer)con).sendItemsTo(netHandler.playerEntity);
			}
		}
		if(type == EnumListType.SECURITY){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				((INetworkContainer)con).sendSecurityTo(netHandler.playerEntity);
			}
		}
	}

}
