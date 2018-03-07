package alec_wam.CrystalMod.blocks;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class FakeBlockStateWithData extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public FakeBlockStateWithData(IBlockState state, final IBlockAccess w, final BlockPos p) {
    	super(state);
        this.blockAccess = w;
        this.pos = p;
    }

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		return new FakeBlockStateWithData(state.withProperty(property, value), blockAccess, pos);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		return new FakeBlockStateWithData(state.cycleProperty(property), blockAccess, pos);
	}
}
