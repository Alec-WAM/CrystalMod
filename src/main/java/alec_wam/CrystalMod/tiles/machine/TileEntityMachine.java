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
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class TileEntityMachine extends TileEntityInventory implements IMessageHandler, ISidedInventory, INBTDrop, INamedContainerProvider {
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
	public void writeCustomNBT(CompoundNBT nbt){
        super.writeCustomNBT(nbt);		
		eStorage.writeToNBT(nbt);
		nbt.putBoolean("Running", this.isRunning);
		nbt.putInt("ProcMax", this.processMax);
        nbt.putInt("ProcRem", this.processRem);
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
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
		CompoundNBT nbt = new CompoundNBT();
        writeCustomNBT(nbt);
		ItemNBTHelper.getCompound(stack).put(NBT_DATA, nbt);
	}

	@Override
	public void readFromItemNBT(ItemStack stack) {
		if(ItemNBTHelper.verifyExistance(stack, NBT_DATA)){
			CompoundNBT nbt = ItemNBTHelper.getCompound(stack).getCompound(NBT_DATA);
	        readCustomNBT(nbt);
			updateAfterLoad();
		}
	}
	
	public void syncProcessValues(){
		if(hasWorld() && !getWorld().isRemote){
			CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("Progress", processRem);
            nbt.putInt("Max", processMax);
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
		      CompoundNBT nbt = new CompoundNBT();
		      nbt.putInt("Power", eStorage.getCEnergyStored());
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
		                CompoundNBT nbt = new CompoundNBT();
		                nbt.putInt("Progress", processRem);
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
	        boolean updateState = false;
	        if (curActive != this.isRunning && this.isRunning) {
	        	updateState = true;
	        }
	        else if (this.wasRunning) {
	            this.wasRunning = false;
	        	updateState = true;
	        }
	        
	        if(updateState){
	        	BlockState state = getBlockState().with(BlockCraftingMachine.RUNNING, Boolean.valueOf(isRunning));
	        	this.getWorld().setBlockState(getPos(), state, 3);
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
	
	public abstract Direction getFacing();
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
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
		if (side != getFacing() && cap == CapabilityCrystalEnergy.CENERGY){
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }
	
	//TODO Override with IOTypes
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] { 0 };
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return canInsertItem(index, itemStackIn);
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return canExtract(index, stack.getCount());
	}

	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
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
			int newMax = messageData.contains("Max") ? messageData.getInt("Max") : this.processMax;
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
