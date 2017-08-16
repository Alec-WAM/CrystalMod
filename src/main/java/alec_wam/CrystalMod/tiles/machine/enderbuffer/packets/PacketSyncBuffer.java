package alec_wam.CrystalMod.tiles.machine.enderbuffer.packets;

import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.util.ModLogger;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

public class PacketSyncBuffer extends AbstractPacketThreadsafe {

	public int mode;
	public int channel;
	public NBTTagCompound nbt;
	
	public PacketSyncBuffer(){}
	
	public PacketSyncBuffer(int mode, int id, EnderBuffer buffer){
		this.mode = mode;
		this.channel = id;
		this.nbt = buffer.serializeNBT();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer pBuffer = new PacketBuffer(buf);
		mode = pBuffer.readInt();
		channel = pBuffer.readInt();
		try{
			nbt = pBuffer.readCompoundTag();
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer pBuffer = new PacketBuffer(buf);
		pBuffer.writeInt(mode);
		pBuffer.writeInt(channel);
		pBuffer.writeCompoundTag(nbt);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		/*if(mode == 0){
			//ADD & SYNC
			EnderBuffer buffer = new EnderBuffer("*ERROR*", null);
			buffer.deserializeNBT(nbt);
			EnderBufferManager.buffers.put(channel, buffer);
		}
		else if(mode == 1){
			//REMOVE
			EnderBufferManager.buffers.remove(channel);
		}
		else if(mode == 2){
			//SYNC
			EnderBuffer buffer = EnderBufferManager.getBuffer(channel);
			if(buffer !=null){
				buffer.deserializeNBT(nbt);
			}
		}*/
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		ModLogger.warning("PacketSyncBuffer is trying to run of SERVER!");
	}

}
