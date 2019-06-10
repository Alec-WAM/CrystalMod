package alec_wam.CrystalMod.network.packet;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketSyncSlot extends AbstractPacket {

	private int slot;
	private ItemStack stack = ItemStackTools.getEmptyStack();
	
	public PacketSyncSlot(){}
	
    public PacketSyncSlot(int slot, ItemStack stack){
    	this.slot = slot;
    	this.stack = stack;
    }
	
	public static PacketSyncSlot decode(PacketBuffer buffer) {
		int slot = buffer.readInt();
		ItemStack stack = buffer.readItemStack();
		return new PacketSyncSlot(slot, stack);
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeInt(slot);
		buffer.writeItemStack(stack);
	}
	
	@Override
	public void handleClient(PlayerEntity player) {
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
		if(player.openContainer !=null){
			Slot s = player.openContainer.getSlot(slot);
			s.putStack(stack);
		}
	}

}
