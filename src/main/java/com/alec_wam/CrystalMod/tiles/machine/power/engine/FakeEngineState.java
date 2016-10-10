package com.alec_wam.CrystalMod.tiles.machine.power.engine;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.alec_wam.CrystalMod.blocks.WrapperState;

public class FakeEngineState extends WrapperState {

	public IBlockAccess blockAccess;
    public BlockPos pos;
    
    public final TileEntityEngineBase engine;
    
    public FakeEngineState(IBlockState state, final IBlockAccess w, final BlockPos p, TileEntityEngineBase pipe) {
    	super(state);
    	this.state = state;
        this.blockAccess = w;
        this.pos = p;
        this.engine = pipe;
    }

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		TileEntityEngineBase tile = engine !=null ? engine : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityEngineBase ? (TileEntityEngineBase)blockAccess.getTileEntity(pos) : null);
		return new FakeEngineState(state.withProperty(property, value), blockAccess, pos, tile);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		TileEntityEngineBase tile = engine !=null ? engine : (blockAccess.getTileEntity(pos) !=null && blockAccess.getTileEntity(pos) instanceof TileEntityEngineBase ? (TileEntityEngineBase)blockAccess.getTileEntity(pos) : null);
	    return new FakeEngineState(state.cycleProperty(property), blockAccess, pos, tile);
	}

}
