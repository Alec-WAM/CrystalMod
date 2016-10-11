package alec_wam.CrystalMod.tiles.machine.power.engine.lava;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.tiles.machine.power.engine.TileEntityEngineBase;

public class TileEntityEngineLava extends TileEntityEngineBase {

	public FluidTank tank;
	public FluidStack renderFluid;
	
	public TileEntityEngineLava(){
		super();
		tank = new FluidTank(Fluid.BUCKET_VOLUME * 4 * multi);
	}
	
	public void updateMulti(int multi){
		super.updateMulti(multi);
		final FluidStack fluid = this.tank.getFluid();
		tank = new FluidTank(Fluid.BUCKET_VOLUME * 4 * multi);
		tank.setFluid(fluid);
	}
	
	@Override
	public CEnergyStorage createStorage(int multi) {
		return new CEnergyStorage(60000*multi, 30*multi);
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(renderFluid !=null)nbt.setTag("RenderFluid", renderFluid.writeToNBT(new NBTTagCompound()));
		tank.writeToNBT(nbt);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		tank.readFromNBT(nbt);
		if(nbt.hasKey("RenderFluid"))renderFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("RenderFluid"));
	}
	
	public void update(){
		super.update();
	}
	
	public int getFuelEnergyValue(FluidStack stack){
		if(stack.getFluid() == FluidRegistry.LAVA){
			return 50;
		}
		return 0;
	}
	
	public void refuel(){
		int amt = (tank.getFluid() == null || (getFuelEnergyValue(tank.getFluid()) == 0)) ? 0 : multi;
		for(int m = 0; m < amt; m++){
			if(tank.getFluidAmount() < getFuelUsage())break;
			fuel.setValue(fuel.getValue()+getFuelEnergyValue(tank.getFluid()));
			renderFluid = tank.getFluid();
			maxFuel.setValue(fuel.getValue());
			tank.drain(getFuelUsage(), !worldObj.isRemote);
		}
	}
	
	public int getFuelUsage(){
		return 50;
	}
	
	public int getFuelValue(){
		return 30;
	}

	@Override
	public int drainCEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return tank;
            	}
            	
            	public int fill(FluidStack resource, boolean doFill) {
            		//if(getFuelEnergyValue(resource) <= 0)return 0;
                   return tank.fill(resource, doFill);
                }

                public FluidStack drain(int maxEmpty, boolean doDrain) {
                	return null;
                }

                public FluidStack drain(FluidStack resource, boolean doDrain) {
                    return null;
                }

				@Override
				public IFluidTankProperties[] getTankProperties() {
					return getTank().getTankProperties();
				}
                
            };
        }
        return super.getCapability(capability, facing);
    }

}
