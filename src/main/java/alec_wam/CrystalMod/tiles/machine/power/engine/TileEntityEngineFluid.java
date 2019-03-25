package alec_wam.CrystalMod.tiles.machine.power.engine;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.FluidTankWrapperInputOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public abstract class TileEntityEngineFluid extends TileEntityEngineBase {

	public FluidTank tank;
	public FluidStack renderFluid;
	private int lastTankFluidAmount;
	
	private final int capacity;
	
	public TileEntityEngineFluid(int capacity){
		super();
		this.capacity = capacity;
		tank = new FluidTank(Fluid.BUCKET_VOLUME * capacity * multi);
		tankWrapper.setTank(tank);
	}
	
	@Override
	public void updateMulti(int multi){
		super.updateMulti(multi);
		final FluidStack fluid = this.tank.getFluid();
		tank = new FluidTank(Fluid.BUCKET_VOLUME * capacity * multi);
		tank.setFluid(fluid);
		tankWrapper.setTank(tank);
	}
	
	@Override
	public CEnergyStorage createStorage(int multi) {
		return new CEnergyStorage(60000*multi, 30*multi);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(renderFluid !=null)nbt.setTag("RenderFluid", renderFluid.writeToNBT(new NBTTagCompound()));
		tank.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		tank.readFromNBT(nbt);
		if(nbt.hasKey("RenderFluid"))renderFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("RenderFluid"));
	}
	
	@Override
	public void update(){
		super.update();

		if(!getWorld().isRemote){
			boolean changed = lastTankFluidAmount != tank.getFluidAmount();
			if(changed && shouldDoWorkThisTick(5)){
				NBTTagCompound nbt = new NBTTagCompound();
				this.tank.writeToNBT(nbt);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFluid", nbt), this);
			}
		}
	}
	
	public abstract int getFuelEnergyValue(FluidStack stack);
	
	@Override
	public void refuel(){
		int amt = (tank.getFluid() == null || (getFuelEnergyValue(tank.getFluid()) == 0)) ? 0 : multi;
		for(int m = 0; m < amt; m++){
			if(tank.getFluidAmount() < getFuelUsage())break;
			fuel.setValue(fuel.getValue()+getFuelEnergyValue(tank.getFluid()));
			renderFluid = tank.getFluid();
			maxFuel.setValue(fuel.getValue());
			tank.drain(getFuelUsage(), true);
		}
	}
	
	public abstract int getFuelUsage();
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("UpdateFluid")){
			this.tank.readFromNBT(messageData);
		}
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }

	public final FluidTankWrapperInputOnly tankWrapper = new FluidTankWrapperInputOnly(null);
	
    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) tankWrapper;
        }
        return super.getCapability(capability, facing);
    }

}
