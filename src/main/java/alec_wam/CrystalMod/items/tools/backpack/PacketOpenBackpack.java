package alec_wam.CrystalMod.items.tools.backpack;

import com.enderio.core.common.util.ChatUtil;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
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
		//Only open backpack on player's back.
		ItemStack back = BackpackUtil.getBackpack(netHandler.playerEntity, OpenType.BACK);
		if(ItemStackTools.isValid(back)){
			if(!BackpackUtil.canOpen(back, netHandler.playerEntity.getUniqueID())){
				ChatUtil.sendNoSpam(netHandler.playerEntity, "You do not own this backpack.");
				return;
			}
			ItemNBTHelper.updateUUID(back);
	    	ExtendedPlayerProvider.getExtendedPlayer(netHandler.playerEntity).setOpenBackpack(back);
			netHandler.playerEntity.openGui(CrystalMod.instance, GuiHandler.GUI_ID_BACKPACK, netHandler.playerEntity.getEntityWorld(), 0, 0, 0);
		}
	}

}
