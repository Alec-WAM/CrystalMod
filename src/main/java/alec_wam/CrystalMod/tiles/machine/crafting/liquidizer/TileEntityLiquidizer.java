package alec_wam.CrystalMod.tiles.machine.crafting.liquidizer;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.LiquidizerRecipeManager.LiquidizerRecipe;
import alec_wam.CrystalMod.tiles.tank.Tank;
import alec_wam.CrystalMod.util.ItemStackTools;
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

public class TileEntityLiquidizer extends TileEntityMachine {

	public Tank tank;
	private FluidStack lastSyncedTankFluid;
	
	public final static int CAPACITY = Fluid.BUCKET_VOLUME * 8;
	
	public TileEntityLiquidizer() {
		super("Liquidizer", 1);
		tank = new Tank("Tank", CAPACITY, this);
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
	public boolean canExtractFluidWithBucket() {
    	return true;
    }
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			boolean update = lastSyncedTankFluid !=tank.getFluid() && shouldDoWorkThisTick(5);
			if(update){
				lastSyncedTankFluid = tank.getFluid();
				NBTTagCompound nbt = new NBTTagCompound();
				if(tank.getFluid() !=null)nbt.setTag("Fluid", tank.getFluid().writeToNBT(new NBTTagCompound()));
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFluid", nbt), this);
			}
		}
	}
	
	@Override
	public boolean canStart() {
		ItemStack stack = getStackInSlot(0);
        if (ItemStackTools.isEmpty(stack)) {
            return false;
        }
        final LiquidizerRecipe recipe = LiquidizerRecipeManager.getRecipe(stack);
        if (recipe == null || eStorage.getCEnergyStored() < recipe.getEnergy()) {
            return false;
        }
        final FluidStack output = recipe.getOutput();
        return output != null && (tank.getFluid() == null || (tank.getFluid().isFluidEqual(output) && tank.getFluidAmount() + output.amount <= tank.getCapacity()));
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
    	ItemStack stack = getStackInSlot(0);
    	final LiquidizerRecipe recipe = LiquidizerRecipeManager.getRecipe(stack);
        return recipe != null && recipe.getInputSize() <= ItemStackTools.getStackSize(stack);
    }
    
    @Override
	public void processStart() {
    	this.processMax = LiquidizerRecipeManager.getRecipe(getStackInSlot(0)).getEnergy();
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    @Override
	public void processFinish() {
    	LiquidizerRecipe recipe = LiquidizerRecipeManager.getRecipe(getStackInSlot(0));
    	final FluidStack output = recipe.getOutput();
        if (this.tank.getFluid() == null) {
        	this.tank.setFluid(output);
        }
        else {
        	this.tank.getFluid().amount+=output.amount;
        }
        final ItemStack itemStack2 = getStackInSlot(0);
        ItemStackTools.incStackSize(itemStack2, -recipe.getInputSize());
        if (ItemStackTools.isEmpty(getStackInSlot(0))) {
        	this.setInventorySlotContents(0, ItemStackTools.getEmptyStack());
        }
    }

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 && LiquidizerRecipeManager.getRecipe(itemStackIn) !=null;
	}

	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerLiquidizer(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiLiquidizer(player, this);
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
    		return 0;
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
            return (T) fluidHandler;
        }
        return super.getCapability(capability, facing);
    }

}
