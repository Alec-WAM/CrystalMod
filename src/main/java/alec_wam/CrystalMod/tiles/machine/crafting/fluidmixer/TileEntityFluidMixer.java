package alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer.FluidMixerRecipeManager.FluidMixRecipe;
import alec_wam.CrystalMod.tiles.tank.Tank;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityFluidMixer extends TileEntityMachine {

	public Tank tankLeft;
	public Tank tankRight;
	private FluidStack lastSyncedLeftFluid;
	private FluidStack lastSyncedRightFluid;
	protected String selectedRecipe;
	private String runningRecipe;
	
	public TileEntityFluidMixer(){
		super("FluidMixer", 1);
		this.selectedRecipe = "cobblestone";
		this.tankLeft = new Tank("TankLeft", Fluid.BUCKET_VOLUME * 8, this);
		this.tankRight = new Tank("TankRight", Fluid.BUCKET_VOLUME * 8, this);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		tankLeft.writeToNBT(nbt);
		tankRight.writeToNBT(nbt);
		nbt.setString("SelectedRecipe", selectedRecipe);
		if(runningRecipe !=null)nbt.setString("RunningRecipe", runningRecipe);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		tankLeft.readFromNBT(nbt);
		tankRight.readFromNBT(nbt);
		selectedRecipe = nbt.getString("SelectedRecipe");
		if(nbt.hasKey("RunningRecipe")){
			runningRecipe = nbt.getString("RunningRecipe");
		}
		updateAfterLoad();
	}
	
	@Override
	public boolean canInsertFluidWithBucket() {
    	return true;
    }
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			boolean update = false;
			
			if((lastSyncedLeftFluid == null && tankLeft.getFluid() !=null) || !FluidUtil.canCombine(lastSyncedLeftFluid, tankLeft.getFluid())){
				update = true;
			} else {
				if(lastSyncedLeftFluid !=null && lastSyncedLeftFluid.amount !=tankLeft.getFluidAmount()){
					update = true;
				}
			}
			if((lastSyncedRightFluid == null && tankRight.getFluid() !=null) || !FluidUtil.canCombine(lastSyncedRightFluid, tankRight.getFluid())){
				update = true;
			} else {
				if(lastSyncedRightFluid !=null && lastSyncedRightFluid.amount !=tankRight.getFluidAmount()){
					update = true;
				}
			}
			
			if(update && shouldDoWorkThisTick(5)){
				lastSyncedLeftFluid = tankLeft.getFluid() == null ? null : tankLeft.getFluid().copy();
				lastSyncedRightFluid = tankRight.getFluid() == null ? null : tankRight.getFluid().copy();
				NBTTagCompound nbt = new NBTTagCompound();
				if(tankLeft.getFluid() !=null)nbt.setTag("FluidLeft", tankLeft.getFluid().writeToNBT(new NBTTagCompound()));
				if(tankRight.getFluid() !=null)nbt.setTag("FluidRight", tankRight.getFluid().writeToNBT(new NBTTagCompound()));
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFluid", nbt), this);
			}
		}
	}
	
	public FluidMixRecipe getSelectedRecipe(){
		return FluidMixerRecipeManager.getRecipe(selectedRecipe);
	}
	
	public FluidMixRecipe getRunningRecipe(){
		return FluidMixerRecipeManager.getRecipe(runningRecipe);
	}
	
	@Override
	public boolean canStart() {		
		FluidMixRecipe recipe = getSelectedRecipe();
		if(recipe !=null){
			ItemStack stack = getStackInSlot(0);
			ItemStack output = recipe.getOutput();
	        if (ItemStackTools.isValid(stack) && !ItemUtil.canCombine(stack, output)) {
	            return false;
	        }
	        if(ItemStackTools.isValid(stack) && ItemStackTools.getStackSize(stack) + ItemStackTools.getStackSize(output) > output.getMaxStackSize()){
	        	return false;
	        }
	        if (eStorage.getCEnergyStored() < recipe.getEnergy()) {
	            return false;
	        }	        
	        if(tankLeft.getFluid() !=null && FluidUtil.canCombine(tankLeft.getFluid(), recipe.getLeftFluidInput()) && tankLeft.getFluidAmount() >= recipe.getLeftFluidInput().amount){
	        	if(tankRight.getFluid() !=null && FluidUtil.canCombine(tankRight.getFluid(), recipe.getRightFluidInput()) && tankRight.getFluidAmount() >= recipe.getRightFluidInput().amount){
		        	return true;
		        }
	        }	     
		}   
        return false;
	}
	
	@Override
	public boolean canContinueRunning(){
		return hasValidInput();
	}
	
	@Override
	public boolean canFinish() {
        return processRem <= 0 && hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	FluidMixRecipe recipe = getRunningRecipe();		
		if(recipe !=null){
	    	if(tankLeft.getFluid() !=null && FluidUtil.canCombine(tankLeft.getFluid(), recipe.getLeftFluidInput()) && tankLeft.getFluidAmount() >= recipe.getLeftFluidInput().amount){
	        	if(tankRight.getFluid() !=null && FluidUtil.canCombine(tankRight.getFluid(), recipe.getRightFluidInput()) && tankRight.getFluidAmount() >= recipe.getRightFluidInput().amount){
		        	return true;
		        }
	        }	 
		}
		return false;
    }
    
    @Override
	public void processStart() {
    	FluidMixRecipe recipe = getSelectedRecipe();		
    	if(recipe !=null){
    		this.runningRecipe = selectedRecipe;
    		this.processMax = recipe.getEnergy();
    		this.processRem = this.processMax;
    		syncProcessValues();
    	}
    }
    
    @Override
	public void processFinish() {
    	FluidMixRecipe recipe = getRunningRecipe();		
		if(recipe !=null){
	    	ItemStack stack = getStackInSlot(0);
	    	final ItemStack output = recipe.getOutput();
	        if (ItemStackTools.isEmpty(stack)) {
	            setInventorySlotContents(0, output);
	        }
	        else {
	            ItemStackTools.incStackSize(stack, ItemStackTools.getStackSize(output));
	        }
	        tankLeft.drainInternal(recipe.getLeftConsumption(), !world.isRemote);
	        tankRight.drainInternal(recipe.getRightConsumption(), !world.isRemote);
			this.runningRecipe = null;
		}
    }
    
    public boolean canFillLeft(FluidStack stack){
    	FluidMixRecipe recipe = getSelectedRecipe();		
		if(recipe !=null){
			if(stack !=null && FluidUtil.canCombine(stack, recipe.getLeftFluidInput())){
				return true;
			}
		}
    	return false;
    }
    
    public boolean canFillRight(FluidStack stack){
    	FluidMixRecipe recipe = getSelectedRecipe();		
		if(recipe !=null){
			if(stack !=null && FluidUtil.canCombine(stack, recipe.getRightFluidInput())){
				return true;
			}
		}
    	return false;
    }
    
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerFluidMixer(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiFluidMixer(player, this);
	}
	
    @Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
    	if(messageId.equalsIgnoreCase("UpdateFluid")){
    		if(!messageData.hasKey("FluidLeft")){
    			this.tankLeft.setFluid(null);
    		} else {
    			this.tankLeft.setFluid(FluidStack.loadFluidStackFromNBT(messageData.getCompoundTag("FluidLeft")));
    		}
    		if(!messageData.hasKey("FluidRight")){
    			this.tankRight.setFluid(null);
    		} else {
    			this.tankRight.setFluid(FluidStack.loadFluidStackFromNBT(messageData.getCompoundTag("FluidRight")));
    		}
    		return;
    	}
    	if(messageId.equalsIgnoreCase("SetRecipe")){
    		this.selectedRecipe = messageData.getString("Recipe");
    		return;
    	}
    	super.handleMessage(messageId, messageData, client);
    }
	
    private IFluidHandler fluidHandlerLeft = new IFluidHandler() {
    	
    	@Override
		public int fill(FluidStack resource, boolean doFill) {
    		if(!canFillLeft(resource)){
    			return 0;
    		}
    		return tankLeft.fill(resource, doFill);
        }

        @Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
        	return tankLeft.drain(maxDrain, doDrain);
        }

        @Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
        	return tankLeft.drain(resource, doDrain);
        }

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return tankLeft.getTankProperties();
		}
        
    };
	
    private IFluidHandler fluidHandlerRight = new IFluidHandler() {
    	
    	@Override
		public int fill(FluidStack resource, boolean doFill) {
    		if(!canFillRight(resource)){
    			return 0;
    		}
    		return tankRight.fill(resource, doFill);
        }

        @Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
        	return tankRight.drain(maxDrain, doDrain);
        }

        @Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
        	return tankRight.drain(resource, doDrain);
        }

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return tankRight.getTankProperties();
		}
        
    };
    
    private IFluidHandler fluidHandlerAll = new IFluidHandler() {
    	private IFluidTankProperties[] tankProperties;
    	@Override
		public int fill(FluidStack resource, boolean doFill) {
    		if(canFillLeft(resource)){
    			return tankLeft.fill(resource, doFill);
    		}
    		if(canFillRight(resource)){
    			return tankRight.fill(resource, doFill);
    		}
    		return 0;
        }

        @Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
        	return null;
        }

        @Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
        	return null;
        }

		@Override
		public IFluidTankProperties[] getTankProperties() {
			if (this.tankProperties == null)
	        {
	            this.tankProperties = new IFluidTankProperties[] { new FluidTankPropertiesWrapper(tankLeft), new FluidTankPropertiesWrapper(tankRight)};
	        }
	        return this.tankProperties;
		}
        
    };
    
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
	  if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
		  return true;
	  }
      return super.hasCapability(capability, facingIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
        	EnumFacing thisFacing = EnumFacing.getHorizontal(this.facing);
  		  	if(facing == BlockUtil.getLeft(thisFacing)){
  			  	return (T) fluidHandlerLeft;
  		  	}
  		  	if(facing == BlockUtil.getRight(thisFacing)){
			  	return (T) fluidHandlerRight;
		  	}
  		  	return (T)fluidHandlerAll;
        }
        return super.getCapability(capability, facing);
    }

}
