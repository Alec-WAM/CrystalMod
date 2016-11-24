package alec_wam.CrystalMod.capability;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketExtendedPlayerInvSync extends AbstractPacketThreadsafe {

	int playerId;
	byte slot=0;
	ItemStack stack;
	
	public PacketExtendedPlayerInvSync(){}
	
	public PacketExtendedPlayerInvSync(EntityPlayer player, int slot) {		
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
		ExtendedPlayerInventory inv = ePlayer.getInventory();
		this.slot = (byte) slot;
		this.stack = inv.getStackInSlot(slot);
		this.playerId = player.getEntityId();
		inv.setChanged(slot, false);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		playerId = buf.readInt();
		slot = buf.readByte();
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(playerId);
		buf.writeByte(slot);
		ByteBufUtils.writeItemStack(buf, stack);
	}
	
	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		World world = CrystalMod.proxy.getClientWorld();
		if(world !=null){
			Entity entity = world.getEntityByID(playerId);
			if(entity !=null && entity instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)entity;
				ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
				ExtendedPlayerInventory inv = ePlayer.getInventory();
				inv.setStackInSlot(slot, stack);
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {}

}
