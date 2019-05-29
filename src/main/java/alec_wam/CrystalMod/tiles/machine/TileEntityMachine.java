package alec_wam.CrystalMod.tiles.machine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class TileEntityMachine extends TileEntityInventory implements IMessageHandler, ISidedInventory, INBTDrop, IInteractionObject {
	protected CEnergyStorage eStorage;
	private final LazyOptional<ICEnergyStorage> holder; 
	private EnergyConfig energyConfig;
	
	boolean isRunning;
	boolean wasRunning;
	protected int processMax;
    protected int processRem;
    
    protected float lastSyncPowerStored = -1;
	
	public TileEntityMachine(TileEntityType<?> tileEntityTypeIn, String name, int size) {
		super(tileEntityTypeIn, name, size);
		
		energyConfig = new EnergyConfig().setEnergyParams(20);
		eStorage = new CEnergyStorage(this.energyConfig.maxEnergy, this.energyConfig.maxPower * 4) {
			@Override
			public boolean canExtract(){
				return false;
			}
		};
		holder = LazyOptional.of(() -> eStorage);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
        super.writeCustomNBT(nbt);		
		eStorage.writeToNBT(nbt);
		nbt.setBoolean("Running", this.isRunning);
		nbt.setInt("ProcMax", this.processMax);
        nbt.setInt("ProcRem", this.processRem);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		eStorage.readFromNBT(nbt);
		this.isRunning = nbt.getBoolean("Running");
		this.processMax = nbt.getInt("ProcMax");
        this.processRem = nbt.getInt("ProcRem");
		updateAfterLoad();
	}
	
	public static final String NBT_DATA = "MachineData";	
	@Override
	public void writeToItemNBT(ItemStack stack) {
		NBTTagCompound nbt = new NBTTagCompound();
        writeCustomNBT(nbt);
		ItemNBTHelper.getCompound(stack).setTag(NBT_DATA, nbt);
	}

	@Override
	public void readFromItemNBT(ItemStack stack) {
		if(ItemNBTHelper.verifyExistance(stack, NBT_DATA)){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompound(NBT_DATA);
	        readCustomNBT(nbt);
			updateAfterLoad();
		}
	}
	
	public void syncProcessValues(){
		if(hasWorld() && !getWorld().isRemote){
			NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInt("Progress", processRem);
            nbt.setInt("Max", processMax);
  	      	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateProgress", nbt), this);
		}
	}

	@Override
	public void tick(){
		super.tick();
		boolean redstone = getWorld().isBlockPowered(getPos());
    	
		if(!getWorld().isRemote){			
			boolean powerChanged = (lastSyncPowerStored != eStorage.getCEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = eStorage.getCEnergyStored();
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInt("Power", eStorage.getCEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
			
			final boolean curActive = this.isRunning;
	        if (this.isRunning) {        	
	        	if(!canContinueRunning() || redstone){
	        		this.isRunning = false;
	                this.wasRunning = true;
	                processRem = processMax = 0;
	                return;
	        	}
	        	
	            if (this.processRem > 0) {
	                final int energy = calcEnergy();
	                this.eStorage.modifyEnergyStored(-energy);
	                this.processRem -= energy;
	                if(shouldDoWorkThisTick(4)){
		                NBTTagCompound nbt = new NBTTagCompound();
		                nbt.setInt("Progress", processRem);
		      	      	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateProgress", nbt), this);
	                }
	            }
	            
	            if (canFinish()) {
	                processFinish();
	                this.eStorage.modifyEnergyStored(-this.processRem);
	                if (!canStart()) {
	                    this.isRunning = false;
	                    this.wasRunning = true;
	                }
	                else {
	                    processStart();
	                }
	            }
	        }
	        else {
	            if (canStart() && !redstone) {
	                processStart();
	                final int energy = calcEnergy();
	                this.eStorage.modifyEnergyStored(-energy);
	                this.processRem -= energy;
	                this.isRunning = true;
	                this.markDirty();
	            }
	        }
	        if (curActive != this.isRunning && this.isRunning) {
	        	BlockUtil.markBlockForUpdate(getWorld(), getPos());
	        }
	        else if (this.wasRunning) {
	            this.wasRunning = false;
	            BlockUtil.markBlockForUpdate(getWorld(), getPos());
	        }
		}
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	public int calcEnergy() {
        if (!this.isRunning) {
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
	
	public abstract EnumFacing getFacing();
	public abstract boolean canStart();
	public abstract void processStart();
	public abstract boolean canContinueRunning();
	public abstract boolean canFinish();
	public abstract void processFinish();
	
	public int getScaledProgress(int scale)
	{
		if ((!this.isRunning) || (this.processMax <= 0) || (this.processRem <= 0)) {
			return 0;
		}
		return scale * (this.processMax - this.processRem) / this.processMax;
	}

	public int getScaledSpeed(int scale)
	{
		if (!this.isRunning) {
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

	public ICEnergyStorage getEnergyStorage() {
		return eStorage;
	}
	
	@Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
		if (side != getFacing() && cap == CapabilityCrystalEnergy.CENERGY){
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return canInsertItem(index, itemStackIn);
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return canExtract(index, stack.getCount());
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInt("Power");
			this.eStorage.setEnergyStored(newPower);
		}
		if(messageId.equalsIgnoreCase("UpdateRunning")){
			boolean newRunning = messageData.getBoolean("Running");
			this.isRunning = newRunning;
		}
		if(messageId.equalsIgnoreCase("UpdateProgress")){
			int newProg = messageData.getInt("Progress");
			int newMax = messageData.hasKey("Max") ? messageData.getInt("Max") : this.processMax;
			this.processRem = newProg;
			this.processMax = newMax;
		}
	}
    
    public boolean canInsertFluidWithBucket() {
    	return false;
    }
    
    public boolean canExtractFluidWithBucket() {
    	return false;
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

}
