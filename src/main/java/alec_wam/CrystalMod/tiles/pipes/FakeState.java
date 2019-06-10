package alec_wam.CrystalMod.tiles.pipes;

import alec_wam.CrystalMod.tiles.WrapperState;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class FakeState extends WrapperState {

	public IBlockReader blockAccess;
    public BlockPos pos;
    
    public final TileEntityPipeBase pipe;
    
    public FakeState(BlockState state, final IBlockReader world, final BlockPos p, TileEntityPipeBase pipe) {
    	super(state);
        this.blockAccess = world;
        this.pos = p;
        this.pipe = pipe;
    }

	@Override
	public <T extends Comparable<T>, V extends T> BlockState with(IProperty<T> property, V value) {
		/*TileEntityPipeBase tile = pipe !=null ? pipe : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityPipeBase ? (TileEntityPipeBase)blockAccess.getTileEntity(pos) : null);
		return new FakeState(state.with(property, value), blockAccess, pos, tile);*/
		return super.with(property, value);
	}

	@Override
	public <T extends Comparable<T>> BlockState cycle(IProperty<T> property) {
		/*TileEntityPipeBase tile = pipe !=null ? pipe : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityPipeBase ? (TileEntityPipeBase)blockAccess.getTileEntity(pos) : null);
	    return new FakeState(state.cycle(property), blockAccess, pos, tile);*/
		return super.cycle(property);				
	}
}
