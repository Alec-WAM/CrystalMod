package alec_wam.CrystalMod.tiles.machine.xpfountain;

import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.fluids.XpUtil;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.tank.Tank;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.entity.item.EntityXPOrb;
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

public class TileEntityXPFountain extends TileEntityMachine {

	public static final int MAX_FOUNTAIN_TIME = 5 * TimeUtil.SECOND;
	
	public Tank tankXP;
	private FluidStack lastSyncedXP;
	public Tank tankEnder;
	private FluidStack lastSyncedEnder;
	public WatchableInteger fountainTime = new WatchableInteger();
	
	public TileEntityXPFountain() {
		super("XPFountain", 0);
		this.tankXP = new Tank("TankXP", Fluid.BUCKET_VOLUME * 4, this) {
			@Override
			public boolean canFillFluidType(FluidStack fluid)
		    {
				return fluid.getFluid() == ModFluids.fluidXpJuice;
		    }
		};
		this.tankEnder = new Tank("TankEnder", Fluid.BUCKET_VOLUME * 4, this) {
			@Override
			public boolean canFillFluidType(FluidStack fluid)
		    {
				return fluid.getFluid() == ModFluids.fluidEnder;
		    }
		};
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		tankXP.writeToNBT(nbt);
		tankEnder.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		tankXP.readFromNBT(nbt);
		tankEnder.readFromNBT(nbt);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	public boolean hasInputs(){
		boolean hasXP = tankXP.getFluidAmount() >= Fluid.BUCKET_VOLUME;
		boolean hasEnder = tankEnder.getFluidAmount() >= Fluid.BUCKET_VOLUME;
		return hasXP && hasEnder;
	}
	
	@Override
	public boolean canStart() {
		//TODO Config Power needs
		boolean hasPower = this.getEnergyStorage().getCEnergyStored() >= 3000;
		return fountainTime.getValue() == 0 && hasInputs() && hasPower;
	}

	@Override
	public void processStart() {
		this.processMax = 3000;
        this.processRem = this.processMax;
        syncProcessValues();
	}

	@Override
	public boolean canContinueRunning() {
		return hasInputs();
	}

	@Override
	public boolean canFinish() {
		return processRem <= 0 && hasInputs();
	}

	@Override
	public void processFinish() {
		tankXP.drain(Fluid.BUCKET_VOLUME, true);
		tankEnder.drain(Fluid.BUCKET_VOLUME, true);		
		
		fountainTime.setValue(MAX_FOUNTAIN_TIME);
	}

	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			boolean update = false;
			
			if((lastSyncedXP == null && tankXP.getFluid() !=null) || !FluidUtil.canCombine(lastSyncedXP, tankXP.getFluid())){
				update = true;
			} else {
				if(lastSyncedXP !=null && lastSyncedXP.amount !=tankXP.getFluidAmount()){
					update = true;
				}
			}
			
			if((lastSyncedEnder == null && tankEnder.getFluid() !=null) || !FluidUtil.canCombine(lastSyncedEnder, tankEnder.getFluid())){
				update = true;
			} else {
				if(lastSyncedEnder !=null && lastSyncedEnder.amount !=tankEnder.getFluidAmount()){
					update = true;
				}
			}
			
			if(update && shouldDoWorkThisTick(5)){
				lastSyncedXP = tankXP.getFluid() == null ? null : tankXP.getFluid().copy();
				lastSyncedEnder = tankEnder.getFluid() == null ? null : tankEnder.getFluid().copy();
				NBTTagCompound nbt = new NBTTagCompound();
				if(tankXP.getFluid() !=null)nbt.setTag("FluidXP", tankXP.getFluid().writeToNBT(new NBTTagCompound()));
				if(tankEnder.getFluid() !=null)nbt.setTag("FluidEnder", tankEnder.getFluid().writeToNBT(new NBTTagCompound()));
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFluid", nbt), this);
			}
			
			if(fountainTime.getValue() > 0){
				double xpX = getPos().getX() + 0.5D;
				double xpY = getPos().getY() + 1.05D;
				double xpZ = getPos().getZ() + 0.5D;
				
				int bucketValue = XpUtil.liquidToExperience(Fluid.BUCKET_VOLUME);
				int cooldown = 4;
				int perOrb = bucketValue / (MAX_FOUNTAIN_TIME / cooldown);
				if(fountainTime.getValue() % cooldown == 0){
					EntityXPOrb orb = new EntityXPOrb(getWorld(), xpX, xpY, xpZ, perOrb);
					getWorld().spawnEntity(orb);
				}
				fountainTime.add(-1);
			}
			
			if(fountainTime.needsSync() && shouldDoWorkThisTick(5)){
				fountainTime.syncValues();
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Time", fountainTime.getValue());
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateTimer", nbt), this);
			}
		}
	}
	
	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerXPFountain(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiXPFountain(player, this);
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
    	if(messageId.equalsIgnoreCase("UpdateFluid")){
    		this.tankXP.setFluid(FluidStack.loadFluidStackFromNBT(messageData.getCompoundTag("FluidXP")));
    		this.tankEnder.setFluid(FluidStack.loadFluidStackFromNBT(messageData.getCompoundTag("FluidEnder")));
    		return;
    	}
    	if(messageId.equalsIgnoreCase("UpdateTimer")){
    		this.fountainTime.setLastValue(fountainTime.getValue());
    		this.fountainTime.setValue(messageData.getInteger("Time"));
    	}
    	super.handleMessage(messageId, messageData, client);
    }
	
	public EnumFacing getXPTankSide(){
		if(facing == 1){
			return EnumFacing.NORTH;
		}
		if(facing == 2){
			return EnumFacing.EAST;
		}
		if(facing == 3){
			return EnumFacing.SOUTH;
		}
		return EnumFacing.WEST;
	}
	
	public EnumFacing getEnderTankSide(){
		if(facing == 1){
			return EnumFacing.SOUTH;
		}
		if(facing == 2){
			return EnumFacing.WEST;
		}
		if(facing == 3){
			return EnumFacing.NORTH;
		}
		return EnumFacing.EAST;
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return facingIn == getXPTankSide() || facingIn == getEnderTankSide();
		}
		return super.hasCapability(capability, facingIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {
        if ((facing == getXPTankSide() || facing == getEnderTankSide()) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return facing == getEnderTankSide() ? tankEnder : tankXP;
            	}
            	
            	@Override
				public int fill(FluidStack resource, boolean doFill) {
            		int ret = getTank().fill(resource, doFill);
            		if(ret > 0 && doFill){
            		}
            		return ret;
                }

                @Override
				public FluidStack drain(int maxEmpty, boolean doDrain) {
                	return null;
                }

                @Override
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
