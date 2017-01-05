package alec_wam.CrystalMod.items.tools.backpack.network;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketToolSwap extends AbstractPacketThreadsafe {

	public int slotToSwap;
	
	public PacketToolSwap(){}
	
	public PacketToolSwap(int slotToSwap){
		this.slotToSwap = slotToSwap;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		slotToSwap = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slotToSwap);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayerMP player = netHandler.playerEntity;
		if(player !=null){
			if(slotToSwap == 0){
				BackpackUtil.swapWeapons(player);
			} else {
				BackpackUtil.swapTools(player);
			}
		}
	}

}
