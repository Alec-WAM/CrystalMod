package alec_wam.CrystalMod.tiles.pipes;

import alec_wam.CrystalMod.blocks.WrapperState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class FakeState extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public final TileEntityPipe pipe;
    
    public FakeState(IBlockState state, final IBlockAccess w, final BlockPos p, TileEntityPipe pipe) {
    	super(state);
        this.blockAccess = w;
        this.pos = p;
        this.pipe = pipe;
    }

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		TileEntityPipe tile = pipe !=null ? pipe : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityPipe ? (TileEntityPipe)blockAccess.getTileEntity(pos) : null);
		return new FakeState(state.withProperty(property, value), blockAccess, pos, tile);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		TileEntityPipe tile = pipe !=null ? pipe : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityPipe ? (TileEntityPipe)blockAccess.getTileEntity(pos) : null);
	    return new FakeState(state.cycleProperty(property), blockAccess, pos, tile);
	}
}
