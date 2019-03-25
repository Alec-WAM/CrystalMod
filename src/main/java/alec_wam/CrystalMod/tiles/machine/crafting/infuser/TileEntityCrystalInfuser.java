package alec_wam.CrystalMod.tiles.machine.crafting.infuser;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager.InfusionMachineRecipe;
import alec_wam.CrystalMod.tiles.tank.Tank;
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
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCrystalInfuser extends TileEntityMachine {

	public Tank tank;
	private FluidStack lastSyncedTankFluid;
	
	public TileEntityCrystalInfuser(){
		super("CrystalInfuser", 2);
		this.tank = new Tank("Tank", Fluid.BUCKET_VOLUME * 8, this);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		tank.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		tank.readFromNBT(nbt);
	}
	
	@Override
	public boolean canInsertFluidWithBucket() {
    	return true;
    }
	
	@Override
	public boolean canExtractFluidWithBucket() {
    	return true;
    }
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			boolean update = false;
			
			if((lastSyncedTankFluid == null && tank.getFluid() !=null) || !FluidUtil.canCombine(lastSyncedTankFluid, tank.getFluid())){
				update = true;
			} else {
				if(lastSyncedTankFluid !=null && lastSyncedTankFluid.amount !=tank.getFluidAmount()){
					update = true;
				}
			}
			
			if(update && shouldDoWorkThisTick(5)){
				lastSyncedTankFluid = tank.getFluid() == null ? null : tank.getFluid().copy();
				NBTTagCompound nbt = new NBTTagCompound();
				if(tank.getFluid() !=null)nbt.setTag("Fluid", tank.getFluid().writeToNBT(new NBTTagCompound()));
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFluid", nbt), this);
			}
		}
	}
	
	@Override
	public boolean canStart() {
		ItemStack stack = getStackInSlot(0);
        if (ItemStackTools.isEmpty(stack) || tank.getFluid() == null) {
            return false;
        }
        final InfusionMachineRecipe recipe = CrystalInfusionManager.getRecipe(stack, tank.getFluid());
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        final ItemStack output = recipe.getOutput();
        ItemStack stack2 = getStackInSlot(1);
        return ItemStackTools.isValid(output) && (ItemStackTools.isEmpty(stack2) || (ItemUtil.canCombine(output, stack2) && ItemStackTools.getStackSize(stack2) + ItemStackTools.getStackSize(output) <= output.getMaxStackSize()));
    }
	
	@Override
	public boolean canContinueRunning(){
		return hasValidInput();
	}
	
	@Override
	public boolean canFinish() {
        return processRem <= 0 && this.hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	final InfusionMachineRecipe recipe = CrystalInfusionManager.getRecipe(getStackInSlot(0), tank.getFluid());
        return recipe != null && recipe.getFluidInput().amount <= tank.getFluidAmount();
    }
    
    @Override
	public void processStart() {
    	this.processMax = CrystalInfusionManager.getRecipe(getStackInSlot(0), tank.getFluid()).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    @Override
	public void processFinish() {
    	ItemStack stack = getStackInSlot(0);
    	ItemStack stack2 = getStackInSlot(1);
    	InfusionMachineRecipe recipe = CrystalInfusionManager.getRecipe(stack, tank.getFluid());
    	final ItemStack output = recipe.getOutput();
        if (ItemStackTools.isEmpty(stack2)) {
            setInventorySlotContents(1, output);
        }
        else {
            ItemStackTools.incStackSize(stack2, ItemStackTools.getStackSize(output));
        }
        ItemStackTools.incStackSize(stack, -1);
        if (ItemStackTools.isEmpty(stack)) {
            setInventorySlotContents(0, ItemStackTools.getEmptyStack());
        }
        final FluidStack fluidStack = tank.getFluid();
        fluidStack.amount-=recipe.getFluidInput().amount;
        if (tank.getFluid().amount <= 0) {
        	tank.setFluid(null);
        }
    }
    
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 && CrystalInfusionManager.isInput(itemStackIn);
	}

	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerCrystalInfuser(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiCrystalInfuser(player, this);
	}
	
    @Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
    	if(messageId.equalsIgnoreCase("UpdateFluid")){
    		this.tank.setFluid(FluidStack.loadFluidStackFromNBT(messageData.getCompoundTag("Fluid")));
    		return;
    	}
    	super.handleMessage(messageId, messageData, client);
    }
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }

	private IFluidHandler fluidHandler = new IFluidHandler() {
    	
    	public FluidTank getTank(){
    		return tank;
    	}
    	
    	@Override
		public int fill(FluidStack resource, boolean doFill) {
    		int ret = tank.fill(resource, doFill);
    		if(ret > 0 && doFill){
    		}
    		return ret;
        }

        @Override
		public FluidStack drain(int maxEmpty, boolean doDrain) {
        	return tank.drain(maxEmpty, doDrain);
        }

        @Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
        	return tank.drain(resource, doDrain);
        }

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return getTank().getTankProperties();
		}
        
    };
	
    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) fluidHandler;
        }
        return super.getCapability(capability, facing);
    }

}
