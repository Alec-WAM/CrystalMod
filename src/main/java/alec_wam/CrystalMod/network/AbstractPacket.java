package alec_wam.CrystalMod.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class AbstractPacket {

	public static void encode(AbstractPacket message, PacketBuffer buffer) {
		message.writeToBuffer(buffer);
	}
	
	public abstract void writeToBuffer(PacketBuffer buffer);

	public abstract void handleClient(PlayerEntity player);

	public abstract void handleServer(ServerPlayerEntity player);
	
	public static void handle(final AbstractPacket message, final Supplier<NetworkEvent.Context> ctx) {
		NetworkDirection dir = ctx.get().getDirection();
		if(dir.getReceptionSide() == LogicalSide.CLIENT){
			final PlayerEntity player = Minecraft.getInstance().player;
			message.handleClient(player);
		} else {
			final ServerPlayerEntity player = ctx.get().getSender();
			message.handleServer(player);
		}
		/*ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			final PlayerEntity player = Minecraft.getInstance().player;
			message.handleClient(player);
		}));
		ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
			System.out.println(ctx.get().getSender());
			final ServerPlayerEntity player = ctx.get().getSender();
			message.handleServer(player);
		}));*/

		ctx.get().setPacketHandled(true);
	}
}
