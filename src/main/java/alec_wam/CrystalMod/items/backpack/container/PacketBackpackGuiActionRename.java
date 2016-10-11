package alec_wam.CrystalMod.items.backpack.container;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatAllowedCharacters;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;

import com.google.common.base.Strings;

public class PacketBackpackGuiActionRename extends AbstractPacketThreadsafe {

	private String name;
	
	public PacketBackpackGuiActionRename(){}
	
    public PacketBackpackGuiActionRename(String name){
    	this.name = name;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		name = buffer.readStringFromBuffer(32767);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeString(name);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayerMP player = netHandler.playerEntity;
		if(player !=null && player.openContainer !=null && player.openContainer instanceof ContainerBackpackRepair){
			ContainerBackpackRepair containerrepair = (ContainerBackpackRepair) player.openContainer;
			String s = ChatAllowedCharacters.filterAllowedCharacters(name);

			if(Strings.isNullOrEmpty(s)){
				containerrepair.updateItemName("");
			}
			else if (s.length() <= 30)
            {
                containerrepair.updateItemName(s);
            }
            
		}
	}

}
