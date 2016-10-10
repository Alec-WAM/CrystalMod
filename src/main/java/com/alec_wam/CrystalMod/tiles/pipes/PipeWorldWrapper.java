package com.alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import com.alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;

public class PipeWorldWrapper extends World {

	public World world;
	public BlockPos loc;
	public EnumFacing face;
	public boolean allowOutsideSet;
	public PipeWorldWrapper(World world, BlockPos loc, EnumFacing dir){
		super(world.getSaveHandler(), world.getWorldInfo(), world.provider, world.theProfiler, world.isRemote);
		this.world = world;
		this.loc = loc;
		this.face = dir;
		this.chunkProvider = world.getChunkProvider();
	}
	
	public void tick()
    {
		
    }
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return world.getTileEntity(pos);
	}

	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
    {
        if (!pos.equals(loc))
        {
        	return allowOutsideSet ? world.setBlockState(pos, newState, flags) : false;
        }
        TileEntity tile = world.getTileEntity(loc);
		if(tile !=null && tile instanceof TileEntityPipe){
			TileEntityPipe pipe = (TileEntityPipe) tile;
			pipe.setCover(face, new CoverData(newState));
			pipe.markDirty();
			return true;
		}
		return false;
    }
	
	@Override
    public boolean spawnEntityInWorld(Entity entity) {
        return false;
    }
	
	@Override
    public void removeEntity(Entity entity) {

    }
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		//if(pos.equals(loc)){
			TileEntity tile = world.getTileEntity(pos);
			if(tile !=null && tile instanceof TileEntityPipe){
				TileEntityPipe pipe = (TileEntityPipe) tile;
				CoverData data = pipe.getCoverData(face);
				if(data !=null && data.getBlockState() !=null){
					return data.getBlockState();
				}
			}
		//}
		return world.getBlockState(pos);
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return world.getChunkProvider();
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		if(world.isRemote){
			return allowEmpty || !this.getChunkProvider().provideChunk(x, z).isEmpty();
		}
		return ((ChunkProviderServer)world.getChunkProvider()).chunkExists(x, z);
	}

}
