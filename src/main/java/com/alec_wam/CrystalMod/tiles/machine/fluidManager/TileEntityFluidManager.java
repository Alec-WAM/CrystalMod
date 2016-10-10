package com.alec_wam.CrystalMod.tiles.machine.fluidManager;

import com.alec_wam.CrystalMod.tiles.TileEntityIOSides;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileEntityFluidManager extends TileEntityIOSides {

	public static enum OutputMode{
		NONE, CHECK, ALLWAYS;
	}
	
	public static final int[] capacities = {Fluid.BUCKET_VOLUME, 16*Fluid.BUCKET_VOLUME, 32*Fluid.BUCKET_VOLUME, 48*Fluid.BUCKET_VOLUME, 64*Fluid.BUCKET_VOLUME};
	
	public FluidTank tank;
	protected int lastUpdateLevel = -1;
	private boolean tankDirty = false;
	private Fluid lastFluid = null;
	byte outputTracker;
	boolean cached = false;
	
	public OutputMode oMode; 
	public int outputAmt;
	
	public TileEntityFluidManager(){
		this(0);
	}
	
	public TileEntityFluidManager(int meta){
		tank = new FluidTank(capacities[meta % capacities.length]);
		oMode = OutputMode.NONE;
		outputAmt = 0;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbtRoot) {
		super.writeCustomNBT(nbtRoot);
		nbtRoot.setInteger("Type", this.getBlockMetadata());
		nbtRoot.setByte("OMode", (byte)oMode.ordinal());
		nbtRoot.setInteger("OAmt", outputAmt);
    	if(tank.getFluidAmount() > 0) {
	    	NBTTagCompound fluidRoot = new NBTTagCompound();
	      	tank.getFluid().writeToNBT(fluidRoot);
	      	nbtRoot.setTag("tankContents", fluidRoot);
    	}
	}

	@Override
  	public void readCustomNBT(NBTTagCompound nbtRoot) {
		super.readCustomNBT(nbtRoot);
    	tank = new FluidTank(capacities[nbtRoot.getInteger("Type") % capacities.length]);
    	oMode = OutputMode.values()[nbtRoot.getByte("OMode")];
    	outputAmt = nbtRoot.getInteger("OAmt");
    	if(nbtRoot.hasKey("tankContents")) {
    	FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbtRoot.getTag("tankContents"));
      	tank.setFluid(fl);
    	} else {
    	tank.setFluid(null);
    	}
  	}
	
	public void incrsAmt(int incrs){
		outputAmt += incrs;
		
		if(outputAmt < 0){
			outputAmt = this.tank.getCapacity();
		}
		if(outputAmt > this.tank.getCapacity()){
			outputAmt = 0;
		}
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("OAmt", incrs);
		markDirty();
	}
	
	public void update(){
		super.update();
		if(!worldObj.isRemote){
			/*int filledLevel = getFilledLevel();
		    if(lastUpdateLevel != filledLevel) {
		      lastUpdateLevel = filledLevel;
		      tankDirty = false;
		      return;
		    }*/
		    
		    if (this.tank !=null)
		    {
		      for (int i = this.outputTracker; (i < 6) && (this.tank.getFluidAmount() > 0); i++) {
		        transferFluid(EnumFacing.getFront(i));
		      }
		      this.outputTracker = ((byte)(this.outputTracker + 1));
		      this.outputTracker = ((byte)(this.outputTracker % 6));
		    }
		    
		    if(tankDirty && shouldDoWorkThisTick(10)) {
		    	this.markDirty();
		    	Fluid held = tank.getFluid() == null ? null : tank.getFluid().getFluid();
		    	if(lastFluid != held) {
		    		lastFluid = held;
		    	}
		    	tankDirty = false;
	      }
		}
	}
	
	protected void transferFluid(EnumFacing face)
	{
		if (this.getIO(face) !=IOType.OUT || this.oMode == OutputMode.NONE) {
			return;
		}
		
		TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
		
		if(tile == null || !isFluidHandler(tile, face))return;
		
		IFluidHandler fHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
		
		int out = this.outputAmt;
		if(this.oMode == OutputMode.CHECK){
			IFluidTankProperties[] infoArray = fHandler.getTankProperties();
			for(int t = 0; t < infoArray.length; t++){
				IFluidTankProperties info = infoArray[t];
				if(info.getContents() !=null){
					if(info.getContents().amount >= this.outputAmt){
						return;
					}else{
						out-=info.getContents().amount;
						break;
					}
				}
			}
		}
		FluidStack copy = tank.getFluid().copy();
		copy.amount = Math.min(tank.getFluidAmount(), out);
		if(tank.getFluidAmount() == 0)return;
		this.drainInternal(fHandler.fill(copy, true), true);
		this.tankDirty = true;
	}
	
	public static boolean isFluidHandler(TileEntity tile, EnumFacing face) {
		return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite()) !=null;
	}
	
	//@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return fillInternal(resource, doFill);
	}
	
	int fillInternal(FluidStack resource, boolean doFill) {
		int res = tank.fill(resource, doFill);
		if(res > 0 && doFill) {
			tankDirty = true;
		}
		return res;
	}
	
	FluidStack drainInternal(FluidStack resource, boolean doDrain) {
		if(resource == null)return null;
		FluidStack res = tank.drain(resource.amount, doDrain);
		if(res != null && res.amount > 0 && doDrain) {
			tankDirty = true;
		}
		return res;
	}
	
	FluidStack drainInternal(int maxDrain, boolean doDrain) {
		FluidStack res = tank.drain(maxDrain, doDrain);
		if(res != null && res.amount > 0 && doDrain) {
			tankDirty = true;
		}
		return res;
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && getIO(facing) == IOType.IN) {
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return tank;
            	}
            	
            	public int fill(FluidStack resource, boolean doFill) {
                    return getTank().fill(resource, doFill);
                }

                public FluidStack drain(int maxEmpty, boolean doDrain) {
                	return null;
                }

                public FluidStack drain(FluidStack resource, boolean doDrain) {
                    return drain(resource.amount, doDrain);
                }

				@Override
				public IFluidTankProperties[] getTankProperties() {
					return getTank().getTankProperties();
				}
                
            };
        }
        return super.getCapability(capability, facing);
    }

	/*@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return getIO(from) == IOType.IN && (tank.getFluid() == null || tank.getFluid().getFluid().equals(fluid));
	}*/
	
	/*private int getFilledLevel() {
		int level = (int) Math.floor(16 * tank.getFilledRatio());
		if(level == 0 && tank.getFluidAmount() > 0) {
			level = 1;
		}
		return level;
	}*/
	
	/*public int getComparatorOutput() {
		FluidTankInfo info = getTankInfo(null)[0];
		return info == null || info.fluid == null ? 0 : (int) (((double) info.fluid.amount / (double) info.capacity) * 15);
	}*/
	
	public void writeToStack(ItemStack stack){
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
		nbt.setByte("OMode", (byte)oMode.ordinal());
		nbt.setInteger("OAmt", outputAmt);
    	if(tank.getFluidAmount() > 0) {
	    	NBTTagCompound fluidRoot = new NBTTagCompound();
	      	tank.getFluid().writeToNBT(fluidRoot);
	      	nbt.setTag("tankContents", fluidRoot);
    	}
	}
	
	public void readFromStack(ItemStack stack){
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
		tank = new FluidTank(capacities[nbt.getInteger("Type") % capacities.length]);
		oMode = OutputMode.values()[nbt.getByte("OMode")];
		outputAmt = nbt.getInteger("OAmt");
		if(nbt.hasKey("tankContents")) {
			FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("tankContents"));
	  		tank.setFluid(fl);
		} else {
			tank.setFluid(null);
		}
	}
	
	
}

