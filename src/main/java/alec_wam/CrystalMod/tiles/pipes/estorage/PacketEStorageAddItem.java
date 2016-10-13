package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;

public class PacketEStorageAddItem extends AbstractPacketThreadsafe {

	private int type;
	private int slot;
	private int amount;
	private byte[] compressed;
	
	public PacketEStorageAddItem(){}
	
    public PacketEStorageAddItem(int type, int slot, int amount, byte[] compressed){
    	this.type = type;
    	this.slot = slot;
    	this.amount = amount;
    	this.compressed = compressed;
    }
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		type = buffer.readInt();
		slot = buffer.readInt();
		amount = buffer.readInt();
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
		buffer.writeInt(type);
		buffer.writeInt(slot);
		buffer.writeInt(amount);
		writeByteArray(buffer, compressed);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		if(type == 0){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				try{
					ItemStackData data = EStorageNetwork.decompressItem(compressed);
					pan.sendItemStackToNetwork(netHandler.playerEntity, slot, data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return;
		}
		if(type == 1){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				try{
					ItemStackData data = EStorageNetwork.decompressItem(compressed);
					pan.grabItemStackFromNetwork(netHandler.playerEntity, slot, amount, data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(type == 2){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				try{
					ItemStackData data = EStorageNetwork.decompressItem(compressed);
					if(data !=null && data.stack !=null){
						ItemStack full = data.stack.copy();
						full.stackSize = full.getMaxStackSize();
						netHandler.playerEntity.inventory.setItemStack(full);
						netHandler.playerEntity.updateHeldItem();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(type == 3){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				try{
					ItemStackData data = EStorageNetwork.decompressItem(compressed);
					if(pan.getNetwork() !=null && data.stack !=null){
						ItemStack copy = data.stack.copy();
						copy.stackSize = amount;
						if(pan.getNetwork().getItemStorage().addItem(copy, true) > 0){
							Slot pSlot = con.getSlot(slot);
							pSlot.decrStackSize(pan.getNetwork().getItemStorage().addItem(copy, false));
							pSlot.onPickupFromSlot(netHandler.playerEntity, data.stack);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(type == 4){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				try{
					ItemStackData data = EStorageNetwork.decompressItem(compressed);
					if(pan.getNetwork() !=null && data.stack !=null){
						pan.getNetwork().handleCraftingRequest(data, Math.max(1, amount));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(type == 5){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				if(pan.getNetwork() !=null){
					pan.getNetwork().handleCraftingCancel(slot);
				}
			}
		}
	}

}
