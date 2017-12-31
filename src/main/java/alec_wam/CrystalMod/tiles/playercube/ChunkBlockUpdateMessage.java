package alec_wam.CrystalMod.tiles.playercube;

import java.io.IOException;
import java.util.Collection;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.fishtank.TileEntityFishTank;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkBlockUpdateMessage extends PacketTileMessage {

	private Collection<BlockPos> sendQueue;
	private FakeChunk chunk;
	
	public ChunkBlockUpdateMessage(){
		
	}
	
	public ChunkBlockUpdateMessage(FakeChunk chunk, Collection<BlockPos> send){
		super(chunk.portal.getPos(), "NULL");
		this.chunk = chunk;
		this.sendQueue = send;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		World world = CrystalMod.proxy.getClientWorld();//DimensionManager.getWorld(dim);
		if(world !=null){
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			if (tile != null && tile instanceof TileEntityPlayerCubePortal) {
				try {
	                ChunkIO.readCompressed(buf, ((TileEntityPlayerCubePortal)tile).mobileChunk);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
			if (tile != null && tile instanceof TileEntityFishTank) {
				try {
	                ChunkIO.readCompressed(buf, ((TileEntityFishTank)tile).mobileChunk);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		try {
            ChunkIO.writeCompressed(buf, chunk, sendQueue);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}
