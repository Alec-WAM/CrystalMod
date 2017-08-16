package alec_wam.CrystalMod.blocks.glass;

import alec_wam.CrystalMod.blocks.WrapperState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class GlassBlockState extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public GlassBlockState(IBlockState state, final IBlockAccess w, final BlockPos p) {
    	super(state);
    	this.state = state;
        this.blockAccess = w;
        this.pos = p;
    }
	

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		return new GlassBlockState(state.withProperty(property, value), blockAccess, pos);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		return new GlassBlockState(state.cycleProperty(property), blockAccess, pos);
	}

}
