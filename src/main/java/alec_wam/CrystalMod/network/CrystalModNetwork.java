package alec_wam.CrystalMod.network;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.packet.PacketRequestProfile;
import alec_wam.CrystalMod.network.packet.PacketResponseProfile;
import alec_wam.CrystalMod.network.packet.PacketUpdateItemNBT;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.util.ChatUtil.PacketNoSpamChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CrystalModNetwork {
	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(CrystalMod.resource("network"));
	public static final String NETWORK_VERSION = CrystalMod.resource("1");
	public static final SimpleChannel network = getNetworkChannel();
	private static int id = 0;
	
	public static int getID(){
		return id++;
	}
	
	public static SimpleChannel getNetworkChannel() {
		final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
				.clientAcceptedVersions(version -> true)
				.serverAcceptedVersions(version -> true)
				.networkProtocolVersion(() -> NETWORK_VERSION)
				.simpleChannel();

		channel.messageBuilder(PacketTileMessage.class, getID())
		.decoder(PacketTileMessage::decode)
		.encoder(PacketTileMessage::encode)
		.consumer(PacketTileMessage::handle)
		.add();
		
		channel.messageBuilder(PacketUpdateItemNBT.class, getID())
		.decoder(PacketUpdateItemNBT::decode)
		.encoder(PacketUpdateItemNBT::encode)
		.consumer(PacketUpdateItemNBT::handle)
		.add();
		
		channel.messageBuilder(PacketNoSpamChat.class, getID())
		.decoder(PacketNoSpamChat::decode)
		.encoder(PacketNoSpamChat::encode)
		.consumer(PacketNoSpamChat::handle)
		.add();
		
		channel.messageBuilder(PacketRequestProfile.class, getID())
		.decoder(PacketRequestProfile::decode)
		.encoder(PacketRequestProfile::encode)
		.consumer(PacketRequestProfile::handle)
		.add();
		
		channel.messageBuilder(PacketResponseProfile.class, getID())
		.decoder(PacketResponseProfile::decode)
		.encoder(PacketResponseProfile::encode)
		.consumer(PacketResponseProfile::handle)
		.add();
		
		return channel;
	}

	public static void sendToAll(AbstractPacket packet)
	{
		network.send(PacketDistributor.ALL.noArg(), packet);
	}
	
	public static void sendTo(EntityPlayerMP player, AbstractPacket packet)
	{
		network.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	public static void sendToServer(AbstractPacket packet)
	{
		network.sendToServer(packet);
	}

	public static void sendToChunk(WorldServer world, BlockPos pos, AbstractPacket packet) {
		Chunk chunk = world.getChunk(pos);
		network.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
	}
	
	public static void sendToAllAround(AbstractPacket packet, TileEntity tile)
	{
		sendToAllAround(packet, new PacketDistributor.TargetPoint(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 64, tile.getWorld().getDimension().getType()));
	}

	public static void sendToAllAround(AbstractPacket packet, Entity entity)
	{
		sendToAllAround(packet, new PacketDistributor.TargetPoint(entity.posX, entity.posY, entity.posZ, 64, entity.getEntityWorld().getDimension().getType()));
	}

	public static void sendToAllAround(AbstractPacket packet, PacketDistributor.TargetPoint point)
	{
		network.send(PacketDistributor.NEAR.with(() -> point), packet);
	}

	public static void sendToDimension(AbstractPacket packet, DimensionType type)
	{
		network.send(PacketDistributor.DIMENSION.with(() -> type), packet);
	}

	public static void sendMCPacket(Entity player, Packet<?> packet) {
		if(player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
			((EntityPlayerMP) player).connection.sendPacket(packet);
		}
	}

}
