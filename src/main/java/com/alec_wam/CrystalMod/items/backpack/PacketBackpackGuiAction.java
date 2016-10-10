package com.alec_wam.CrystalMod.items.backpack;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

import com.alec_wam.CrystalMod.network.AbstractPacketThreadsafe;

public class PacketBackpackGuiAction extends AbstractPacketThreadsafe {

	private int action;
    private int elementId;
    
    public PacketBackpackGuiAction(){}
    
    public PacketBackpackGuiAction(int act, int id){
    	this.action = act;
    	this.elementId = id;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		action = buf.readInt();
		elementId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(action);
		buf.writeInt(elementId);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		ItemBackpack.performGuiAction(netHandler.playerEntity, action, elementId);
	}

}
