package alec_wam.CrystalMod.tiles.machine;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityMachine extends TileEntityInventory implements IMessageHandler, IMachineTile, ISidedInventory, INBTDrop {

	protected CEnergyStorage eStorage;
	private EnergyConfig energyConfig;
	public int facing = EnumFacing.NORTH.getHorizontalIndex();
	
	boolean isActive;
	boolean wasActive;
	protected int processMax;
    protected int processRem;
    
    protected float lastSyncPowerStored = -1;
    
    public TileEntityMachine(String name, int size){
    	super(name, size);

		energyConfig = new EnergyConfig().setEnergyParams(20);
		eStorage = new CEnergyStorage(this.energyConfig.maxEnergy, this.energyConfig.maxPower * 4) {
			public boolean canExtract(){
				return false;
			}
		};
    }
    
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", facing);
		NBTTagCompound eNBT = new NBTTagCompound();
		eStorage.writeToNBT(eNBT);
		nbt.setTag("Energy", eNBT);
		
		nbt.setBoolean("Active", this.isActive);
		nbt.setInteger("ProcMax", this.processMax);
        nbt.setInteger("ProcRem", this.processRem);
	}
	
	public void writeToStack(NBTTagCompound nbt){
		this.writeCustomNBT(nbt);
	}
	
	public void readFromStack(NBTTagCompound nbt){
		this.readCustomNBT(nbt);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = nbt.getInteger("Facing");
		if(nbt.hasKey("Energy")){
			eStorage.readFromNBT(nbt.getCompoundTag("Energy"));
		}
		this.isActive = nbt.getBoolean("Active");
		this.processMax = nbt.getInteger("ProcMax");
        this.processRem = nbt.getInteger("ProcRem");
		updateAfterLoad();
	}
	
	public void syncProcessValues(){
		if(hasWorld() && !getWorld().isRemote){
			NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("Progress", processRem);
            nbt.setInteger("Max", processMax);
  	      	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateProgress", nbt), this);
		}
	}
    
    public void update(){
		super.update();
		if(getWorld().isRemote){
			return;
		}
		
		boolean powerChanged = (lastSyncPowerStored != eStorage.getCEnergyStored() && shouldDoWorkThisTick(5));
	    if(powerChanged) {
	      lastSyncPowerStored = eStorage.getCEnergyStored();
	      NBTTagCompound nbt = new NBTTagCompound();
	      nbt.setInteger("Power", eStorage.getCEnergyStored());
	      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
	    }
		
		final boolean curActive = this.isActive;
        if (this.isActive) {
            if (this.processRem > 0) {
                final int energy = calcEnergy();
                this.eStorage.modifyEnergyStored(-energy);
                this.processRem -= energy;
                if(shouldDoWorkThisTick(4)){
	                NBTTagCompound nbt = new NBTTagCompound();
	                nbt.setInteger("Progress", processRem);
	      	      	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateProgress", nbt), this);
                }
            }
            if (canFinish()) {
                processFinish();
                this.eStorage.modifyEnergyStored(-this.processRem);
                if (!canStart()) {
                    this.isActive = false;
                    this.wasActive = true;
                }
                else {
                    processStart();
                }
            }
        }
        else {
            if (shouldDoWorkThisTick(4) && canStart()) {
                processStart();
                final int energy = calcEnergy();
                this.eStorage.modifyEnergyStored(-energy);
                this.processRem -= energy;
                this.isActive = true;
                this.markDirty();
            }
        }
        if (curActive != this.isActive && this.isActive) {
        	BlockUtil.markBlockForUpdate(getWorld(), getPos());
        }
        else if (this.wasActive) {
            this.wasActive = false;
            BlockUtil.markBlockForUpdate(getWorld(), getPos());
        }
	}
    

	
	public int calcEnergy() {
        if (!this.isActive) {
            return 0;
        }
        if (this.eStorage.getCEnergyStored() > energyConfig.maxPowerLevel) {
            return energyConfig.maxPower;
        }
        if (this.eStorage.getCEnergyStored() < energyConfig.minPowerLevel) {
            return energyConfig.minPower;
        }
        return this.eStorage.getCEnergyStored() / energyConfig.energyRamp;
    }
	
	public abstract boolean canStart();
	public abstract void processStart();
	
	public abstract boolean canFinish();
	public abstract void processFinish();

	public ICEnergyStorage getEnergyStorage() {
		return eStorage;
	}
	
	public static class EnergyConfig
    {
      public int minPower;
      public int maxPower;
      public int maxEnergy;
      public int minPowerLevel;
      public int maxPowerLevel;
      public int energyRamp;
      
      public boolean setEnergyParams(int minPower, int maxPower, int maxEnergy)
      {
        if ((minPower <= 0) || (maxPower <= 0) || (maxEnergy <= 0)) {
          return false;
        }
        this.minPower = minPower;
        this.maxPower = maxPower;
        this.maxEnergy = maxEnergy;
        this.maxPowerLevel = (maxEnergy * 8 / 10);
        this.energyRamp = (this.maxPowerLevel / maxPower);
        this.minPowerLevel = (minPower * this.energyRamp);
        
        return true;
      }
      
      public EnergyConfig setEnergyParams(int maxPower)
      {
        setEnergyParams(maxPower / 4, maxPower, maxPower * 1200);
        return this;
      }
    }
	


	public int getScaledProgress(int scale)
    {
      if ((!this.isActive) || (this.processMax <= 0) || (this.processRem <= 0)) {
        return 0;
      }
      return scale * (this.processMax - this.processRem) / this.processMax;
    }

    public int getScaledSpeed(int scale)
    {
    	if (!this.isActive) {
	      return 0;
	    }
    	double power = eStorage.getCEnergyStored() / energyConfig.energyRamp;
    	power = clip(power, energyConfig.minPower, energyConfig.maxPower);
      
    	return round(scale * power / energyConfig.maxPower);
    }
    
    public static int round(double d)
    {
      return (int)(d + 0.5D);
    }
    
    public static double clip(double value, double min, double max)
    {
      if (value > max) {
        value = max;
      } else if (value < min) {
        value = min;
      }
      return value;
    }

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInteger("Power");
			this.eStorage.setEnergyStored(newPower);
		}
		if(messageId.equalsIgnoreCase("UpdateProgress")){
			int newProg = messageData.getInteger("Progress");
			int newMax = messageData.hasKey("Max") ? messageData.getInteger("Max") : this.processMax;
			this.processRem = newProg;
			this.processMax = newMax;
		}
	}

	@Override
	public void setFacing(int facing) {
		this.facing = facing;
	}

	@Override
	public int getFacing() {
		return facing;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(this.getSizeInventory() == 1)return new int[]{0};
		if(side == EnumFacing.UP){
			return new int[]{0};
		}
		return new int[]{1};
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if(this.getSizeInventory() == 1)return true;
		return direction !=EnumFacing.UP;
	}
	
	net.minecraftforge.items.IItemHandler handlerInput = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.UP);
	net.minecraftforge.items.IItemHandler handlerOut = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.NORTH);

	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
	  if(capability == CapabilityCrystalEnergy.CENERGY){
		  return facingIn.getHorizontalIndex() !=facing;
	  }
      return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }
	
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return facing == EnumFacing.UP ? (T) handlerInput : (T) handlerOut;
        if(capability == CapabilityCrystalEnergy.CENERGY){
        	return (T) eStorage;
        }
        return super.getCapability(capability, facing);
    }

    public abstract Object getContainer(EntityPlayer player, int id);
    @SideOnly(Side.CLIENT)
	public abstract Object getGui(EntityPlayer player, int id);
	
}
