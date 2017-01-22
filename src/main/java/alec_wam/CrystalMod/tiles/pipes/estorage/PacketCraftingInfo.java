package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;

import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCraftingController;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup.CraftingInfoPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

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
			ICraftingTask taskR = TileCraftingController.readCraftingTask(null, buffer.readCompoundTag());
			if(taskR !=null && taskR instanceof BasicCraftingTask){
				this.task = (BasicCraftingTask) taskR;
			}
			this.stack = buffer.readItemStack();
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
		buffer.writeCompoundTag(nbt);
		buffer.writeItemStack(stack);
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
