package alec_wam.CrystalMod.network.packet;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.network.AbstractPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PacketOpenCustomGui extends AbstractPacket {

	private ResourceLocation id;
    private int windowId;
    private ITextComponent name;
    private PacketBuffer additionalData;
	
	public PacketOpenCustomGui(){}
	
    public PacketOpenCustomGui(ResourceLocation id, int windowId, ITextComponent name, PacketBuffer additionalData){
    	this.id = id;
    	this.windowId = windowId;
    	this.name = name;
    	this.additionalData = additionalData;
    }
	
	public static PacketOpenCustomGui decode(PacketBuffer buffer) {
		return new PacketOpenCustomGui(buffer.readResourceLocation(), buffer.readVarInt(), buffer.readTextComponent(), new PacketBuffer(Unpooled.wrappedBuffer(buffer.readByteArray(32600))));
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeResourceLocation(id);
		buffer.writeVarInt(windowId);
		buffer.writeTextComponent(name);
		buffer.writeByteArray(additionalData.readByteArray());
	}
	
	@Override
	public void handleClient(PlayerEntity player) {
		GuiHandler.createScreen(id, windowId, Minecraft.getInstance(), name, additionalData);
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
	}

}
