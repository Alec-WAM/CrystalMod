package alec_wam.CrystalMod.tiles.playercube;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketRequestTileEntitites extends PacketTileMessage {

	private FakeChunk chunk;
	
    public PacketRequestTileEntitites() {
        super();
    }

    public PacketRequestTileEntitites(FakeChunk chunk) {
    	super(chunk.portal.getPos(), "NULL");
		this.chunk = chunk;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        if (chunk != null) {
            if (chunk.chunkTileEntityMap.isEmpty()) {
                return;
            }

            TileEntitiesMessage msg = new TileEntitiesMessage(chunk);
            CrystalModNetwork.sendTo(msg, netHandler.playerEntity);
        }
    }
}
