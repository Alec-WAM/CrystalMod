package alec_wam.CrystalMod.network;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.PacketExtendedPlayerInvSync;
import alec_wam.CrystalMod.capability.PacketOpenExtendedInventory;
import alec_wam.CrystalMod.items.tools.backpack.PacketOpenBackpack;
import alec_wam.CrystalMod.items.tools.backpack.network.PacketToolSwap;
import alec_wam.CrystalMod.network.packets.InventoryCraftingSyncPacket;
import alec_wam.CrystalMod.network.packets.MessageTileContainerUpdate;
import alec_wam.CrystalMod.network.packets.PacketDimensionNameRequest;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.network.packets.PacketExtendedPlayer;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.network.packets.PacketItemNBT;
import alec_wam.CrystalMod.network.packets.PacketRecipeTransfer;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.elevator.PacketPlayerMove;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.packets.PacketSyncBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.packets.PacketSyncBufferList;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageAddItem;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageItemList;
import alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import alec_wam.CrystalMod.tiles.playercube.ChunkBlockUpdateMessage;
import alec_wam.CrystalMod.tiles.playercube.PacketRequestTileEntitites;
import alec_wam.CrystalMod.tiles.playercube.TileEntitiesMessage;
import alec_wam.CrystalMod.tiles.tooltable.PacketEnhancementTable;
import alec_wam.CrystalMod.util.ChatUtil.PacketNoSpamChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CrystalModNetwork extends NetworkWrapper {
	  public static CrystalModNetwork instance = new CrystalModNetwork();

	  public CrystalModNetwork() {
	    super(CrystalMod.MODID);
	  }

	  public void setup() {
	    // register all the packets
	    //registerPacketClient(ConfigSyncPacket.class);

		registerPacket(PacketDimensionNameRequest.class);  
		  
	    // TOOLS
	    registerPacketServer(InventoryCraftingSyncPacket.class);
	    registerPacket(PacketPlayerMove.class);
	    
	    registerPacketClient(PacketExtendedPlayerInvSync.class);
	    registerPacketServer(PacketOpenExtendedInventory.class);
	    registerPacketServer(PacketOpenBackpack.class);
	    registerPacketServer(PacketToolSwap.class);
	    
	    registerPacketClient(PacketNoSpamChat.class);
	    registerPacket(PacketExtendedPlayer.class);
	    
	    registerPacket(PacketEntityMessage.class);
	    registerPacket(PacketGuiMessage.class);
	    
	    registerPacket(PacketSyncBufferList.class);
	    registerPacket(PacketSyncBuffer.class);
	    
	    // TILES
	    registerPacket(PacketPipe.class);
	    
	    registerPacket(PacketTileMessage.class);
	    
	    registerPacket(PacketRecipeTransfer.class);
	    
	    registerPacket(PacketEStorageItemList.class);
	    
	    registerPacket(PacketEStorageAddItem.class);
	    
	    registerPacket(ChunkBlockUpdateMessage.class);
	    
	    registerPacket(TileEntitiesMessage.class);
	    registerPacket(PacketRequestTileEntitites.class);
	    
	    registerPacket(MessageTileContainerUpdate.class);
	    
	    registerPacket(PacketEnhancementTable.class);
	    registerPacket(PacketItemNBT.class);
	  }

	  public static void sendToAll(AbstractPacket packet)
	  {
	    instance.network.sendToAll(packet);
	  }

	  public static void sendTo(AbstractPacket packet, EntityPlayerMP player)
	  {
	    instance.network.sendTo(packet, player);
	  }

	  public static void sendToAllAround(AbstractPacket packet, TileEntity tile)
	  {
		  sendToAllAround(packet, new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 64));
	  }
	  
	  public static void sendToAllAround(AbstractPacket packet, Entity tile)
	  {
		  sendToAllAround(packet, new NetworkRegistry.TargetPoint(tile.getEntityWorld().provider.getDimension(), tile.posX, tile.posY, tile.posZ, 64));
	  }

	  public static void sendToAllAround(AbstractPacket packet, NetworkRegistry.TargetPoint point)
	  {
	    instance.network.sendToAllAround(packet, point);
	  }

	  public static void sendToDimension(AbstractPacket packet, int dimensionId)
	  {
	    instance.network.sendToDimension(packet, dimensionId);
	  }

	  public static void sendToServer(AbstractPacket packet)
	  {
		 instance.network.sendToServer(packet);
	  }

	  public static void sendToClients(WorldServer world, BlockPos pos, AbstractPacket packet) {
	      Chunk chunk = world.getChunkFromBlockCoords(pos);
	      for(EntityPlayer player : world.playerEntities) {
	        // only send to relevant players
	        if(!(player instanceof EntityPlayerMP)) {
	          continue;
	        }
	        EntityPlayerMP playerMP = (EntityPlayerMP) player;
	        if(world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition)) {
	          CrystalModNetwork.sendTo(packet, playerMP);
	        }
	      }
	  }
	  
	  public static void sendMCPacket(Entity player, Packet<?> packet) {
		  if(player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
			  ((EntityPlayerMP) player).connection.sendPacket(packet);
		  }
	  }
	}
