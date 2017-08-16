package alec_wam.CrystalMod.capability;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketOpenExtendedInventory extends AbstractPacketThreadsafe {

	public PacketOpenExtendedInventory(){}
	
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		netHandler.playerEntity.openGui(CrystalMod.instance, GuiHandler.GUI_ID_EXTENDED, netHandler.playerEntity.getEntityWorld(), 0, 0, 0);
	}

}
