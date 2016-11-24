package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketOpenBackpack extends AbstractPacketThreadsafe {

	public PacketOpenBackpack(){}
	
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		netHandler.playerEntity.openGui(CrystalMod.instance, GuiHandler.GUI_ID_BACKPACK, netHandler.playerEntity.getEntityWorld(), OpenType.BACK.ordinal(), 0, 0);
	}

}
