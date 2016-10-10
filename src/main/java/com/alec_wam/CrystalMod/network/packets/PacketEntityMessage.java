package com.alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import com.alec_wam.CrystalMod.network.IMessageHandler;

public class PacketEntityMessage extends AbstractPacketThreadsafe {

	public int id;
	private String type;
	private NBTTagCompound data;
	
	public PacketEntityMessage(){}
	
	public PacketEntityMessage(Entity entity, String type){
		this(entity, type, new NBTTagCompound());
	}
	
    public PacketEntityMessage(Entity entity, String type, NBTTagCompound data){
    	this.id = entity.getEntityId();
    	this.type = type;
    	this.data = data;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		id = buffer.readInt();
		type = buffer.readStringFromBuffer(100);
		try {
			data = buffer.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			data = new NBTTagCompound();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(id);
		buffer.writeString(type);
		buffer.writeNBTTagCompoundToBuffer(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		handle(CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().worldObj, id);
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		handle(netHandler.playerEntity.worldObj, id);
	}
	
	public void handle(World world, int id){
		if(world == null){
			return;
		}
		Entity entity = world.getEntityByID(id);
		if(entity !=null && entity instanceof IMessageHandler){
			((IMessageHandler)entity).handleMessage(type, data, false);
		}
	}

}
