package alec_wam.CrystalMod.tiles.machine.seismic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SeismicDataWorldWrapper extends World {

	public World wrappedWorld;
	public SeismicData data;
	
	public SeismicDataWorldWrapper(World world, SeismicData data) {
		super(world.getSaveHandler(), world.getWorldInfo(), world.provider, world.theProfiler, world.isRemote);
		wrappedWorld = world;
		this.data = data;
	}

	@Override
	public void tick()
    {
		
    }
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return wrappedWorld.getTileEntity(pos);
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
    {
        return false;
    }
	
	@Override
    public boolean spawnEntity(Entity entity) {
        return false;
    }
	
	@Override
    public void removeEntity(Entity entity) {

    }
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		if(data !=null){
			
			IBlockState dataState = data.getStateForPos(pos);
			if(dataState != Blocks.AIR.getDefaultState()){
				return dataState;
			}
		}
		return Blocks.AIR.getDefaultState();
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return wrappedWorld.getChunkProvider();
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		if(wrappedWorld.isRemote){
			return allowEmpty || !this.getChunkProvider().provideChunk(x, z).isEmpty();
		}
		return ((ChunkProviderServer)wrappedWorld.getChunkProvider()).chunkExists(x, z);
	}

    @Override
    public float getLightBrightness(BlockPos pos) {
    	IBlockState state = getBlockState(pos);
        return this.provider.getLightBrightnessTable()[15];
    }

    @Override
    public long getTotalWorldTime() {
        return wrappedWorld.getTotalWorldTime();
    }

    @Override
    public long getWorldTime() {
        return wrappedWorld.getWorldTime();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
    	int i = 15;
        int j = 15;

        /*if (j < lightValue)
        {
            j = lightValue;
        }*/

        return 15;
    }

}
