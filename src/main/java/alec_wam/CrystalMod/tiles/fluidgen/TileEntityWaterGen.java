package alec_wam.CrystalMod.tiles.fluidgen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.compatibility.FluidTankFixed;
import alec_wam.CrystalMod.init.FixedFluidRegistry;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityWaterGen extends TileEntity {

	//TODO Change to proper type
	public TileEntityWaterGen() {
		super(ModBlocks.TILE_PEDESTAL);
	}

	public static final FluidTank INIFINITE_WATER = new FluidTankFixed(1000){
		@Override
	    public FluidStack drain(FluidStack resource, boolean doDrain)
	    {
	        return resource;
	    }

	    @Override
	    public FluidStack drain(int maxDrain, boolean doDrain)
	    {
	        return new FluidStack(FixedFluidRegistry.WATER, maxDrain);
	    }
	};
	
	public static final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> INIFINITE_WATER);
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(cap, side);
    }
}
