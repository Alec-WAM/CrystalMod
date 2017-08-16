package alec_wam.CrystalMod.world.game.tag;

import java.io.IOException;
import java.util.List;

import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.world.game.tag.TagManager.PlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketTagPlayerData extends AbstractPacketThreadsafe {

	public static int TYPE_UPDATE_LIST = 0, TYPE_ADD_PLAYER = 1, TYPE_REMOVE_PLAYER = 2, TYPE_UPDATE_PLAYER = 3, TYPE_UPDATE_TAGGERS = 4; 
	
	private int type;
	private byte[] compressed;
	
	public PacketTagPlayerData(){}
	
	public PacketTagPlayerData(int type, byte[] compressed){
		this.type = type;
		this.compressed = compressed;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		type = buf.readInt();
		compressed = readByteArray(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(type);
		writeByteArray(buf, compressed);
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
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		try {
			if(type == TYPE_UPDATE_LIST){
				List<PlayerData> dataList = TagManager.decompressDataList(compressed);
				TagManager.getInstance().players = dataList;
			}else if(type == TYPE_ADD_PLAYER){
				TagManager.getInstance().players.add(TagManager.decompressData(compressed));
			}else if(type == TYPE_REMOVE_PLAYER){
				TagManager.getInstance().removePlayer(TagManager.decompressData(compressed));
			}else if(type == TYPE_UPDATE_PLAYER){
				PlayerData data = TagManager.decompressData(compressed);
				PlayerData old = TagManager.getInstance().getData(data.playerName);
				if(old !=null){
					old.copyValues(data);
				}
			}else if(type == TYPE_UPDATE_TAGGERS){
				List<PlayerData> dataList = TagManager.decompressDataList(compressed);
				TagManager.getInstance().currentTaggers = dataList;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		
	}

}
