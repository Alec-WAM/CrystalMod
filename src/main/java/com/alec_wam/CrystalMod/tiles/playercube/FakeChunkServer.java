package com.alec_wam.CrystalMod.tiles.playercube;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeChunkServer extends FakeChunk {

	private Set<BlockPos> sendQueue;

    public FakeChunkServer(World world, TileEntityPlayerCubePortal entityMovingWorld) {
        super(world, entityMovingWorld);
        sendQueue = new HashSet<BlockPos>();
    }

    public Collection<BlockPos> getSendQueue() {
        return sendQueue;
    }

    @Override
    public boolean addBlockWithState(BlockPos pos, IBlockState blockState) {
        if (super.addBlockWithState(pos, blockState)) {
            sendQueue.add(pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state) {
        if (super.setBlockState(pos, state)) {
            sendQueue.add(pos);
            return true;
        }
        return false;
    }
	
}
