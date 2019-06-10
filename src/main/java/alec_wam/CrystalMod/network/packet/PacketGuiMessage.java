package alec_wam.CrystalMod.network.packet;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.network.IMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class PacketGuiMessage extends AbstractPacket {

	private String type;
	private CompoundNBT data;
	
	public PacketGuiMessage(){}
	
	public PacketGuiMessage(String type){
		this(type, new CompoundNBT());
	}
	
    public PacketGuiMessage(String type, CompoundNBT data){
    	this.type = type;
    	this.data = data;
    }
	
	public static PacketGuiMessage decode(PacketBuffer buffer) {
		String type = buffer.readString(100);
		CompoundNBT data = buffer.readCompoundTag();
		return new PacketGuiMessage(type, data);
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeString(type);
		buffer.writeCompoundTag(data);
	}
	
	@Override
	public void handleClient(PlayerEntity player) {
		Screen currentScreen = Minecraft.getInstance().field_71462_r;
		if(currentScreen !=null){
			if(currentScreen instanceof IMessageHandler)((IMessageHandler)currentScreen).handleMessage(type, data, true);
		}
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
		if(player.openContainer !=null){
			if(player.openContainer instanceof IMessageHandler)((IMessageHandler)player.openContainer).handleMessage(type, data, false);
		}
	}

}
