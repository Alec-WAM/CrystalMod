package alec_wam.CrystalMod.tiles.explosives.fuser;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import gnu.trove.set.hash.THashSet;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ExplosionMaker {
	private final WorldServer serverWorld;
    private THashSet<Chunk> modifiedChunks = new THashSet<>();
    private HashSet<BlockPos> blocksToUpdate = new HashSet<>();
    private HashMap<ChunkPos, Chunk> chunkCache = new HashMap<>();
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    public LinkedList<BlockPos> toRemove = new LinkedList<>();

    public ExplosionMaker(WorldServer serverWorld) {
        this.serverWorld = serverWorld;
    }

    public void setBlocksForRemoval(LinkedList<BlockPos> list) {
        this.toRemove = list;
    }

    public void addBlocksForUpdate(Collection<BlockPos> blocksToUpdate) {
        this.blocksToUpdate.addAll(blocksToUpdate);
    }

    private void removeBlock(BlockPos pos) {
    	if(pos.getY() < 0 || pos.getY() >= 256)return;
        Chunk chunk = getChunk(pos);
        IBlockState oldState = chunk.getBlockState(pos);

        if (oldState.getBlock().hasTileEntity(oldState)) {
            serverWorld.setBlockToAir(pos);

            PlayerChunkMap playerChunkMap = serverWorld.getPlayerChunkMap();
            if (playerChunkMap != null) {
                PlayerChunkMapEntry watcher = playerChunkMap.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
                if (watcher != null) {
                    watcher.sendPacket(new SPacketBlockChange(serverWorld, pos));
                }
            }
        }

        ExtendedBlockStorage storage = getBlockStorage(pos);
        if(storage !=null){
        	storage.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);
        }
        setChunkModified(pos);
    }

    public void setChunkModified(BlockPos blockPos) {
        Chunk chunk = getChunk(blockPos);
        setChunkModified(chunk);
    }

    public void setChunkModified(Chunk chunk) {
        modifiedChunks.add(chunk);
    }

    private Chunk getChunk(BlockPos pos) {
        ChunkPos cp = new ChunkPos(pos);
        if (!chunkCache.containsKey(cp)) {
            chunkCache.put(cp, serverWorld.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4));
        }

        return chunkCache.get(cp);
    }

    private boolean hasBlockStorage(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        return chunk.getBlockStorageArray()[pos.getY() >> 4] != null;
    }

    private ExtendedBlockStorage getBlockStorage(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        int index = pos.getY() >> 4;
        return chunk.getBlockStorageArray()[index];
    }

    private void fireBlockBreak(BlockPos pos, IBlockState oldState) {
        oldState.getBlock().breakBlock(serverWorld, pos, oldState);
    }

    private void removeTileEntity(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        TileEntity tileEntity = chunk.getTileEntity(pos, EnumCreateEntityType.CHECK);
        if (tileEntity != null) {
            serverWorld.removeTileEntity(pos);
        }
    }


    /**
     * Call when finished removing blocks to calculate lighting and send chunk updates to the client.
     */
    public void finish() {
    	MinecraftServer server = serverWorld.getMinecraftServer();
        long currentTime = MinecraftServer.getCurrentTimeMillis();
        while (MinecraftServer.getCurrentTimeMillis() - currentTime < 50 && toRemove.size() > 0) {
            BlockPos pos = toRemove.removeFirst();
            removeBlock(pos);
        }
        finishChunks();

        if (toRemove.isEmpty()) {
            updateBlocks();
        }
    }

    public boolean isAirBlock(BlockPos pos) {
        return serverWorld.isAirBlock(pos);
    }

    public IBlockState getBlockState(BlockPos pos) {
        ExtendedBlockStorage storage = getBlockStorage(pos);
        if (storage == null) {
            return Blocks.AIR.getDefaultState();
        }
        return storage.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }
    


    public void finishChunks() {
        PlayerChunkMap playerChunkMap = serverWorld.getPlayerChunkMap();
        if (playerChunkMap == null) {
            return;
        }

        for (Chunk chunk : modifiedChunks) {
            chunk.setModified(true);
            chunk.generateSkylightMap(); //This is where this falls short. It can calculate basic sky lighting for blocks exposed to the sky but thats it.

            PlayerChunkMapEntry watcher = playerChunkMap.getEntry(chunk.xPosition, chunk.zPosition);
            if (watcher != null) {//TODO Change chunk mask to only the sub chunks changed.
                watcher.sendPacket(new SPacketChunkData(chunk, 65535));
            }
        }

        modifiedChunks.clear();
    }

    private void updateBlocks() {
        try {
            BlockFalling.fallInstantly = true;
            for (BlockPos pos : blocksToUpdate) {
                IBlockState state = serverWorld.getBlockState(pos);
                if (state.getBlock() instanceof BlockFalling) {
                    state.getBlock().updateTick(serverWorld, pos, state, serverWorld.rand);
                }
                state.neighborChanged(serverWorld, pos, Blocks.AIR, pos);
                serverWorld.notifyNeighborsOfStateChange(pos, state.getBlock(), true);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

        BlockFalling.fallInstantly = false;
    }
}
