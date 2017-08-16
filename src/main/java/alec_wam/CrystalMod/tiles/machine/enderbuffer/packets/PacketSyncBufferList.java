package alec_wam.CrystalMod.tiles.machine.enderbuffer.packets;

import java.io.IOException;

import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.util.ModLogger;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

public class PacketSyncBufferList extends AbstractPacketThreadsafe {

	private NBTTagCompound nbt;
	
	public PacketSyncBufferList(){}
	
	public PacketSyncBufferList(NBTTagCompound nbt){
		this.nbt = nbt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		try {
			nbt = buffer.readCompoundTag();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeCompoundTag(nbt);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		/*if(nbt !=null){
			EnderBufferManager.buffers.clear();
		    NBTTagCompound nbtData = nbt;
		    
		    Iterator<String> iterator = nbtData.getKeySet().iterator();
		    while (iterator.hasNext())
		    {
		      String key = (String)iterator.next();
		      NBTTagCompound nbt = nbtData.getCompoundTag(key);
		      EnderBuffer chestContents = new EnderBuffer("*ERROR*", null);
		      chestContents.deserializeNBT(nbt);
		      EnderBufferManager.buffers.put(Integer.parseInt(key), chestContents);
		    }
		}*/
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		ModLogger.warning("PacketSyncBufferList is trying to run of SERVER!");
	}

}
