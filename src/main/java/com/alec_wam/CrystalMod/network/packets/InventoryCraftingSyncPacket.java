package com.alec_wam.CrystalMod.network.packets;

import com.alec_wam.CrystalMod.network.AbstractPacketThreadsafe;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import io.netty.buffer.ByteBuf;

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
