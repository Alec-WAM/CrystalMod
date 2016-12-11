package alec_wam.CrystalMod.tiles;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidTankWrapperInputOnly implements IFluidHandler {

	private FluidTank tank;
	
	public FluidTankWrapperInputOnly(FluidTank tank){
		this.tank = tank;
	}
	
	public void setTank(FluidTank tank){
		this.tank = tank;
	}
	
	@Override
	public IFluidTankProperties[] getTankProperties() {
		if(tank == null)return new IFluidTankProperties[0];
		return tank.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(tank == null)return 0;
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return null;
	}

}
