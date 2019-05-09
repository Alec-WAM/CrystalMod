package alec_wam.CrystalMod.tiles;

import net.minecraft.block.state.BlockState;

public class WrapperState extends BlockState {

	public BlockState state;
	
	public WrapperState(BlockState state/*IBlockState state, ImmutableMap<IProperty<?>, Comparable<?>> properties*/){
		super(state.getBlock(), state.getValues());
		this.state = state;
	}
	
	@Override
	public boolean equals(Object p_equals_1_) {
		return super.equals(p_equals_1_);
		//return state.equals(p_equals_1_);
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}
}
