package alec_wam.CrystalMod.tiles.playercube;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A wrapper for MobileChunks, used to give blocks accurate information about it's neighbors.
 */
public class FakeWorld extends World {

    FakeChunk mobileChunk;

    private FakeWorld(boolean remote, World parentWorld) {
        super(parentWorld.getSaveHandler(), parentWorld.getWorldInfo(), parentWorld.provider, parentWorld.theProfiler, remote);
    }

    public static FakeWorld getFakeWorld(FakeChunk chunk) {
        FakeWorld retVal = new FakeWorld(chunk.worldObj.isRemote, chunk.worldObj);
        retVal.mobileChunk = chunk;
        return retVal;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
    	return mobileChunk.getTileEntity(pos);
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
    	return mobileChunk.getBlockState(pos);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state) {
        return false;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        return false;
    }

    private boolean isValidPosition(BlockPos pos) {
    	if(mobileChunk.getCube() == null)return false;
        return pos.getX() >= mobileChunk.getCube().minBlock.getX() && pos.getZ() >= mobileChunk.getCube().minBlock.getZ() && pos.getX() < mobileChunk.getCube().minBlock.getX()+16 && pos.getZ() < mobileChunk.getCube().minBlock.getZ()+16 && pos.getY() >= 0 && pos.getY() < mobileChunk.getCube().minBlock.getY()+16;
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
        return isValidPosition(pos);
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
    	IBlockState state = getBlockState(pos);
        if (state == null || isAirBlock(pos)) return _default;

        return state.getBlock().isSideSolid(state, this, pos, side);
    }

    @Override
    public float getLightBrightness(BlockPos pos) {
    	IBlockState state = getBlockState(pos);
        return state.getBlock().getLightValue(state, mobileChunk, pos);
    }

    @Override
    public long getTotalWorldTime() {
        return mobileChunk.worldObj.getTotalWorldTime();
    }

    @Override
    public long getWorldTime() {
        return mobileChunk.worldObj.getWorldTime();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return mobileChunk.getCombinedLight(pos, lightValue);
    }

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		if(mobileChunk.worldObj.isRemote){
			return allowEmpty || !mobileChunk.worldObj.getChunkProvider().provideChunk(x, z).isEmpty();
		}
		return ((WorldServer)mobileChunk.worldObj).getChunkProvider().chunkExists(x, z);
	}
}
