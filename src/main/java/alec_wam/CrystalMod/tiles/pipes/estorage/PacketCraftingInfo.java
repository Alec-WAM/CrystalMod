package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup.CraftingInfoPopup;

public class PacketCraftingInfo extends AbstractPacketThreadsafe {

	private BasicCraftingTask task;
	private ItemStack stack;
	private int quantity;
	
	public PacketCraftingInfo(){}
	
	public PacketCraftingInfo(BasicCraftingTask task, ItemStack stack, int quantity){
		this.task = task;
		this.stack = stack;
		this.quantity = quantity;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		try {
			//TODO Investigate why this is commented
			//this.task = EStorageNetwork.readCraftingTask(buffer.readNBTTagCompoundFromBuffer());
			this.stack = buffer.readItemStackFromBuffer();
			this.quantity = buffer.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		NBTTagCompound nbt = new NBTTagCompound();
		task.writeToNBT(nbt);
		buffer.writeNBTTagCompoundToBuffer(nbt);
		buffer.writeItemStackToBuffer(stack);
		buffer.writeInt(quantity);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen !=null){
			if(mc.currentScreen instanceof GuiPanel){
				GuiPanel panel = (GuiPanel)mc.currentScreen;
				panel.currentPopup = new CraftingInfoPopup(stack, task, quantity);
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		
	}

}
