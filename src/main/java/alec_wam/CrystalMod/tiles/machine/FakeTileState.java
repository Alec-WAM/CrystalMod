package alec_wam.CrystalMod.tiles.machine;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import alec_wam.CrystalMod.blocks.WrapperState;

public class FakeTileState<B extends TileEntity> extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public final B tile;
    
    public FakeTileState(IBlockState state, final IBlockAccess w, final BlockPos p, B tile) {
    	super(state);
        this.blockAccess = w;
        this.pos = p;
        this.tile = tile;
    }

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		return new FakeTileState<B>(state.withProperty(property, value), blockAccess, pos, tile);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		return new FakeTileState<B>(state.cycleProperty(property), blockAccess, pos, tile);
	}
}
