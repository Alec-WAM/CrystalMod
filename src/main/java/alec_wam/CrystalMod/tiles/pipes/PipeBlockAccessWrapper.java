package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;

public class PipeBlockAccessWrapper implements IBlockAccess {

	public IBlockAccess world;
	public BlockPos loc;
	public EnumFacing face;
	public PipeBlockAccessWrapper(IBlockAccess world, BlockPos loc, EnumFacing dir){
		this.world = world;
		this.loc = loc;
		this.face = dir;
	}
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return pos.equals(loc) ? null : world.getTileEntity(pos);
	}
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		if(pos.equals(loc)){
			TileEntity tile = world.getTileEntity(pos);
			if(tile !=null && tile instanceof TileEntityPipe){
				TileEntityPipe pipe = (TileEntityPipe) tile;
				CoverData data = pipe.getCoverData(face);
				if(data !=null && data.getBlockState() !=null){
					return data.getBlockState();
				}
			}
		}
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityPipe){
			TileEntityPipe pipe = (TileEntityPipe) tile;
			CoverData data = pipe.getCoverData(face);
			if(data !=null && data.getBlockState() !=null){
				return data.getBlockState();
			}
		}
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return world.getCombinedLight(pos, lightValue);
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		IBlockState state = getBlockState(pos);
		return state.getBlock().isAir(state, this, pos);
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return world.getBiome(pos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		IBlockState iblockstate = this.getBlockState(pos);
        return iblockstate.getBlock().getStrongPower(iblockstate, this, pos, direction);
	}

	@Override
	public WorldType getWorldType() {
		return world.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		IBlockState state = getBlockState(pos);
		return state.getBlock().isSideSolid(state, this, pos, side);
	}

}
