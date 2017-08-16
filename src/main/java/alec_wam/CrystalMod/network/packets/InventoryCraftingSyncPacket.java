package alec_wam.CrystalMod.network.packets;

import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;

public class InventoryCraftingSyncPacket extends AbstractPacketThreadsafe {

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    // Serverside only
    throw new UnsupportedOperationException("Serverside only");
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    Container container = netHandler.playerEntity.openContainer;
    if(container != null) {
      container.onCraftMatrixChanged(null);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    // no data, yay
  }

  @Override
  public void toBytes(ByteBuf buf) {
    // no data, yay
  }
}
