package alec_wam.CrystalMod.network.packet;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;

public class PacketUpdateItemNBT extends AbstractPacket {

	public ItemStack stack = ItemStackTools.getEmptyStack();
	public EnumHand hand = EnumHand.MAIN_HAND;
	
	public PacketUpdateItemNBT(){}
	
	public PacketUpdateItemNBT(ItemStack stack, EnumHand hand){
    	this.stack = stack;
    	this.hand = hand;
    }
	
	public static PacketUpdateItemNBT decode(PacketBuffer buffer) {
		ItemStack stack = buffer.readItemStack();
		EnumHand hand = buffer.readEnumValue(EnumHand.class);
		return new PacketUpdateItemNBT(stack, hand);
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeItemStack(stack);
		buffer.writeEnumValue(hand);
	}
	
	@Override
	public void handleClient(EntityPlayer player) {
		player.setHeldItem(hand, stack);
	}

	@Override
	public void handleServer(EntityPlayerMP player) {
		player.setHeldItem(hand, stack);
	}

}
