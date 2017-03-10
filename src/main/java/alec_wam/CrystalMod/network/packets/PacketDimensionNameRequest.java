package alec_wam.CrystalMod.network.packets;

import java.util.UUID;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.util.StringUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketDimensionNameRequest extends AbstractPacketThreadsafe {

	private int dimension;
	private UUID playerUUID;
	private String dimensionName;
	
	public PacketDimensionNameRequest(){}
	
	public PacketDimensionNameRequest(UUID playerUUID, int dimension){
		this.dimension = dimension;
		this.playerUUID = playerUUID;
	}
	
	public PacketDimensionNameRequest(int dimension, String name){
		this.dimension = dimension;
		this.dimensionName = name;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		dimension = buffer.readInt();
		if(buffer.readBoolean())playerUUID = buffer.readUniqueId();
		else dimensionName = buffer.readString(100);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(dimension);
		if(playerUUID !=null){
			buffer.writeBoolean(true);
			buffer.writeUniqueId(playerUUID);
		} else {
			buffer.writeBoolean(false);
			if(Strings.isNullOrEmpty(dimensionName)){
				buffer.writeString(""+dimension);
			} else {
				buffer.writeString(dimensionName);
			}
		}
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		StringUtils.DIMENSION_NAMES.put(dimension, dimensionName);
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayerMP playerMP = FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(playerUUID);
		if(playerMP !=null){
			CrystalModNetwork.sendTo(new PacketDimensionNameRequest(dimension, StringUtils.getDimensionName(playerUUID, dimension)), playerMP);
		}
	}

}
