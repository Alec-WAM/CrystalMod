package alec_wam.CrystalMod.network.packet;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.network.IMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class PacketGuiMessage extends AbstractPacket {

	private String type;
	private NBTTagCompound data;
	
	public PacketGuiMessage(){}
	
	public PacketGuiMessage(String type){
		this(type, new NBTTagCompound());
	}
	
    public PacketGuiMessage(String type, NBTTagCompound data){
    	this.type = type;
    	this.data = data;
    }
	
	public static PacketGuiMessage decode(PacketBuffer buffer) {
		String type = buffer.readString(100);
		NBTTagCompound data = buffer.readCompoundTag();
		return new PacketGuiMessage(type, data);
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeString(type);
		buffer.writeCompoundTag(data);
	}
	
	@Override
	public void handleClient(EntityPlayer player) {
		GuiScreen currentScreen = Minecraft.getInstance().currentScreen;
		if(currentScreen !=null){
			if(currentScreen instanceof IMessageHandler)((IMessageHandler)currentScreen).handleMessage(type, data, true);
		}
	}

	@Override
	public void handleServer(EntityPlayerMP player) {
		if(player.openContainer !=null){
			if(player.openContainer instanceof IMessageHandler)((IMessageHandler)player.openContainer).handleMessage(type, data, false);
		}
	}

}
