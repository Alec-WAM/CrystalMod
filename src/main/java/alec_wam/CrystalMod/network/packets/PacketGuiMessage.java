package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.BlockUtil;

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
		buffer.writeString(type);
		buffer.writeNBTTagCompoundToBuffer(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
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
		if(player.openContainer !=null){
			if(player.openContainer instanceof IMessageHandler){
				((IMessageHandler)player.openContainer).handleMessage(type, data, false);
			}
		}
	}

}
