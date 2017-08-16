package alec_wam.CrystalMod.tiles.tank;

import alec_wam.CrystalMod.blocks.WrapperState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class FakeTankState extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public final TileEntityTank tank;
    
    public FakeTankState(IBlockState state, final IBlockAccess w, final BlockPos p, TileEntityTank tank) {
    	super(state);
    	this.state = state;
        this.blockAccess = w;
        this.pos = p;
        this.tank = tank;
    }
	

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		TileEntityTank tile = tank !=null ? tank : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityTank ? (TileEntityTank)blockAccess.getTileEntity(pos) : null);
		return new FakeTankState(state.withProperty(property, value), blockAccess, pos, tile);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		TileEntityTank tile = tank !=null ? tank : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityTank ? (TileEntityTank)blockAccess.getTileEntity(pos) : null);
	    return new FakeTankState(state.cycleProperty(property), blockAccess, pos, tile);
	}

}
