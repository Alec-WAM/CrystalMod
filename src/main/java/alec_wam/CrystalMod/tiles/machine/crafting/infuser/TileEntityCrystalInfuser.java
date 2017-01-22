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
		this.tank = new Tank("Tank", Fluid.BUCKET_VOLUME * 4, null);
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		tank.writeToNBT(nbt);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		tank.readFromNBT(nbt);
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
	
	public boolean canFinish() {
        return processRem <= 0 && this.hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	final InfusionMachineRecipe recipe = CrystalInfusionManager.getRecipe(getStackInSlot(0), tank.getFluid());
        return recipe != null && recipe.getFluidInput().amount <= tank.getFluidAmount();
    }
    
    public void processStart() {
    	this.processMax = CrystalInfusionManager.getRecipe(getStackInSlot(0), tank.getFluid()).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
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

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return tank;
            	}
            	
            	public int fill(FluidStack resource, boolean doFill) {
            		int ret = tank.fill(resource, doFill);
            		if(ret > 0 && doFill){
            		}
            		return ret;
                }

                public FluidStack drain(int maxEmpty, boolean doDrain) {
                	if(facing !=EnumFacing.DOWN) return null;
                	return tank.drain(maxEmpty, doDrain);
                }

                public FluidStack drain(FluidStack resource, boolean doDrain) {
                	if(facing !=EnumFacing.DOWN) return null;
                    return tank.drain(resource, doDrain);
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
