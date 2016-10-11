package alec_wam.CrystalMod.network.packets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;

public class PacketItemNBT extends AbstractPacketThreadsafe {

	private int slot;
	private NBTTagCompound data;
	
	public PacketItemNBT(){}
	
    public PacketItemNBT(int invSlot, NBTTagCompound nbt){
    	this.slot = invSlot;
    	this.data = nbt;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		slot = buffer.readInt();
		try {
			data = buffer.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(slot);
		buffer.writeNBTTagCompoundToBuffer(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		if(player == null){
			return;
		}
		ItemStack stack = player.inventory.getStackInSlot(slot < 0 ? player.inventory.currentItem : slot);
		if(stack !=null){
			stack.setTagCompound(data);
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayer player = netHandler.playerEntity;
		if(player == null){
			return;
		}
		ItemStack stack = player.inventory.getStackInSlot(slot < 0 ? player.inventory.currentItem : slot);
		if(stack !=null){
			stack.setTagCompound(data);
		}
	}

}
