package alec_wam.CrystalMod.blocks.connected;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import alec_wam.CrystalMod.blocks.WrapperState;

public class ConnectedBlockState extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public ConnectedBlockState(IBlockState state, final IBlockAccess w, final BlockPos p) {
    	super(state);
    	this.state = state;
        this.blockAccess = w;
        this.pos = p;
    }
	

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		return new ConnectedBlockState(state.withProperty(property, value), blockAccess, pos);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		return new ConnectedBlockState(state.cycleProperty(property), blockAccess, pos);
	}

}
