package alec_wam.CrystalMod.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class AbstractPacket {

	public static void encode(AbstractPacket message, PacketBuffer buffer) {
		message.writeToBuffer(buffer);
	}
	
	public abstract void writeToBuffer(PacketBuffer buffer);

	public abstract void handleClient(EntityPlayer player);

	public abstract void handleServer(EntityPlayerMP player);
	
	public static void handle(final AbstractPacket message, final Supplier<NetworkEvent.Context> ctx) {
		NetworkDirection dir = ctx.get().getDirection();
		if(dir.getReceptionSide() == LogicalSide.CLIENT){
			final EntityPlayer player = Minecraft.getInstance().player;
			message.handleClient(player);
		} else {
			final EntityPlayerMP player = ctx.get().getSender();
			message.handleServer(player);
		}
		/*ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			final EntityPlayer player = Minecraft.getInstance().player;
			message.handleClient(player);
		}));
		ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
			System.out.println(ctx.get().getSender());
			final EntityPlayerMP player = ctx.get().getSender();
			message.handleServer(player);
		}));*/

		ctx.get().setPacketHandled(true);
	}
}
