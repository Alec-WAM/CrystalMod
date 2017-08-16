package alec_wam.CrystalMod.tiles.machine.power.battery;

import alec_wam.CrystalMod.blocks.WrapperState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class FakeBatteryState extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public final TileEntityBattery battery;
    
    public FakeBatteryState(IBlockState state, final IBlockAccess w, final BlockPos p, TileEntityBattery battery) {
    	super(state);
        this.blockAccess = w;
        this.pos = p;
        this.battery = battery;
    }

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		TileEntityBattery tile = battery !=null ? battery : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityBattery ? (TileEntityBattery)blockAccess.getTileEntity(pos) : null);
		return new FakeBatteryState(state.withProperty(property, value), blockAccess, pos, tile);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		TileEntityBattery tile = battery !=null ? battery : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityBattery ? (TileEntityBattery)blockAccess.getTileEntity(pos) : null);
	    return new FakeBatteryState(state.cycleProperty(property), blockAccess, pos, tile);
	}
}
