package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.accessories.boats.EntityBoatChest;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.BlockUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

public class PacketGuiMessage extends AbstractPacketThreadsafe {

	private String type;
	public NBTTagCompound data;
	
	public PacketGuiMessage(){}
	
	public PacketGuiMessage(String type){
		this(type, new NBTTagCompound());
	}
	
    public PacketGuiMessage(String type, NBTTagCompound data){
    	this.type = type;
    	this.data = data;
    }
    
    public void setOpenGui(int id, int x, int y, int z)
    {
    	data.setBoolean("openGui", true);
    	data.setInteger("id", id);
    	data.setInteger("x", x);
    	data.setInteger("y", y);
    	data.setInteger("z", z);
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		type = buffer.readString(100);
		try {
			data = buffer.readCompoundTag();
		} catch (IOException e) {
			data = new NBTTagCompound();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeString(type);
		buffer.writeCompoundTag(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof IMessageHandler){
			((IMessageHandler)screen).handleMessage(type, data, true);
		}
		if(player !=null && player.openContainer !=null){
			if(player.openContainer instanceof IMessageHandler){
				((IMessageHandler)player.openContainer).handleMessage(type, data, true);
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayerMP player = netHandler.playerEntity;
		if(data.hasKey("openGui"))
		{
			BlockUtil.openWorksiteGui(player, data.getInteger("id"), data.getInteger("x"), data.getInteger("y"), data.getInteger("z"));
		}
		if(type .equalsIgnoreCase("DisplayChest")){
			Entity entity = netHandler.playerEntity.world.getEntityByID(data.getInteger("ID"));
			if(entity !=null && entity instanceof EntityBoatChest){
				((EntityBoatChest)entity).openChestGUI(netHandler.playerEntity);
				return;
			}
		}
		if(player.openContainer !=null){
			if(player.openContainer instanceof IMessageHandler){
				((IMessageHandler)player.openContainer).handleMessage(type, data, false);
			}
		}
	}

}
