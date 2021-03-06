package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;

import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

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
				if(pan.getNetwork() !=null && pan.getNetwork().hasAbility(netHandler.playerEntity, NetworkAbility.INSERT)){
					try{
						ItemStackData data = EStorageNetwork.decompressItem(compressed);
						pan.sendItemStackToNetwork(netHandler.playerEntity, slot, data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					ChatUtil.sendNoSpam(netHandler.playerEntity, Lang.localize("gui.networkability."+NetworkAbility.INSERT.getId()));
				}
			}
			return;
		}
		if(type == 1){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				if(pan.getNetwork() !=null && pan.getNetwork().hasAbility(netHandler.playerEntity, NetworkAbility.EXTRACT)){
					try{
						ItemStackData data = EStorageNetwork.decompressItem(compressed);
						pan.grabItemStackFromNetwork(netHandler.playerEntity, slot, amount, data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					ChatUtil.sendNoSpam(netHandler.playerEntity, Lang.localize("gui.networkability."+NetworkAbility.EXTRACT.getId()));
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
						ItemStackTools.setStackSize(full, full.getMaxStackSize());
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
				if(pan.getNetwork() !=null && pan.getNetwork().hasAbility(netHandler.playerEntity, NetworkAbility.INSERT)){
					try{
						ItemStackData data = EStorageNetwork.decompressItem(compressed);
						if(!ItemStackTools.isNullStack(data.stack)){
							ItemStack copy = data.stack.copy();
							ItemStackTools.setStackSize(copy, amount);
							final int old = amount;
							ItemStack remain = pan.getNetwork().getItemStorage().addItem(copy, false);
							Slot pSlot = con.getSlot(slot);
							int decAmt = ItemStackTools.isNullStack(remain) ? old : (old-ItemStackTools.getStackSize(remain));
							pSlot.decrStackSize(decAmt);
							if(decAmt > 0){
								pSlot.onTake(netHandler.playerEntity, data.stack);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else {
					ChatUtil.sendNoSpam(netHandler.playerEntity, Lang.localize("gui.networkability."+NetworkAbility.INSERT.getId()));
				}
			}
		}
		
		if(type == 4){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				if(pan.getNetwork() !=null && pan.getNetwork().hasAbility(netHandler.playerEntity, NetworkAbility.CRAFT)){
					try{
						ItemStackData data = EStorageNetwork.decompressItem(compressed);
						if(pan.getNetwork() !=null && !ItemStackTools.isNullStack(data.stack)){
							if(pan.getNetwork().craftingController !=null){
								pan.getNetwork().craftingController.handleCraftingRequest(data, Math.max(1, amount));
							}else {
								ChatUtil.sendNoSpam(netHandler.playerEntity, "Missing crafting controller");
							}

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else {
					ChatUtil.sendNoSpam(netHandler.playerEntity, Lang.localize("gui.networkability."+NetworkAbility.CRAFT.getId()));
				}
			}
		}
		
		if(type == 5){
			Container con = netHandler.playerEntity.openContainer;
			if(con !=null && con instanceof INetworkContainer){
				INetworkContainer pan = ((INetworkContainer)con);
				if(pan.getNetwork() !=null && pan.getNetwork().hasAbility(netHandler.playerEntity, NetworkAbility.SETTINGS)){
					if(pan.getNetwork().craftingController !=null){
						pan.getNetwork().craftingController.handleCraftingCancel(slot);
					}
				}else {
					ChatUtil.sendNoSpam(netHandler.playerEntity, Lang.localize("gui.networkability."+NetworkAbility.SETTINGS.getId()));
				}
			}
		}
	}

}
