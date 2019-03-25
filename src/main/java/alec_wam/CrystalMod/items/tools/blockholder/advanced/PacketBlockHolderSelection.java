package alec_wam.CrystalMod.items.tools.blockholder.advanced;

import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketBlockHolderSelection extends AbstractPacketThreadsafe {

	public int selection;
	
	public PacketBlockHolderSelection(){}
	
	public PacketBlockHolderSelection(int selection){
		this.selection = selection;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		selection = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(selection);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayerMP player = netHandler.playerEntity;
		if(player !=null){
			ItemStack held = player.getHeldItemMainhand();
			if(held.getItem() instanceof ItemAdvancedBlockHolder){
				ItemAdvancedBlockHolder.setSelection(held, selection);
			}
		}
	}

}
