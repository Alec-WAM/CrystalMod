package alec_wam.CrystalMod.network.packet;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class PacketUpdateItemNBT extends AbstractPacket {

	public ItemStack stack = ItemStackTools.getEmptyStack();
	public Hand hand = Hand.MAIN_HAND;
	
	public PacketUpdateItemNBT(){}
	
	public PacketUpdateItemNBT(ItemStack stack, Hand hand){
    	this.stack = stack;
    	this.hand = hand;
    }
	
	public static PacketUpdateItemNBT decode(PacketBuffer buffer) {
		ItemStack stack = buffer.readItemStack();
		Hand hand = buffer.readEnumValue(Hand.class);
		return new PacketUpdateItemNBT(stack, hand);
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeItemStack(stack);
		buffer.writeEnumValue(hand);
	}
	
	@Override
	public void handleClient(PlayerEntity player) {
		player.setHeldItem(hand, stack);
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
		player.setHeldItem(hand, stack);
	}

}
