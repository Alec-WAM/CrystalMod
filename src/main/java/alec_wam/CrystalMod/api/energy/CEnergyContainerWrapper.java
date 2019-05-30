package alec_wam.CrystalMod.api.energy;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CEnergyContainerWrapper implements ICEnergyStorage, ICapabilityProvider {
	
	public final ItemStack stack;
	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;

	public CEnergyContainerWrapper(ItemStack stack, int capacity) {

		this(stack, capacity, capacity, capacity);
	}

	public CEnergyContainerWrapper(ItemStack stack, int capacity, int maxTransfer) {

		this(stack, capacity, maxTransfer, maxTransfer);
	}

	public CEnergyContainerWrapper(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
		this.stack = stack;
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public int getItemEnergy(){
		return ItemNBTHelper.getInteger(stack, "Energy", 0);
	}
	
	public void setItemEnergy(int energy){
		ItemNBTHelper.setInteger(stack, "Energy", energy);
	}
	
	public CEnergyContainerWrapper setCapacity(int capacity) {

		this.capacity = capacity;

		if (getItemEnergy() > capacity) {
			setItemEnergy(capacity);
		}
		return this;
	}

	public CEnergyContainerWrapper setMaxTransfer(int maxTransfer) {

		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
		return this;
	}

	public CEnergyContainerWrapper setMaxReceive(int maxReceive) {

		this.maxReceive = maxReceive;
		return this;
	}

	public CEnergyContainerWrapper setMaxExtract(int maxExtract) {

		this.maxExtract = maxExtract;
		return this;
	}

	public int getMaxReceive() {

		return maxReceive;
	}

	public int getMaxExtract() {

		return maxExtract;
	}

	public void checkEnergyValues(){
		if (getItemEnergy() > capacity) {
			setItemEnergy(capacity);
		} else if (getItemEnergy() < 0) {
			setItemEnergy(0);
		}
	}
	
	/**
	 * This function is included to allow for server to client sync. Do not call this externally to the containing Tile Entity, as not all IEnergyHandlers
	 * are guaranteed to have it.
	 *
	 * @param energy
	 */
	public void setEnergyStored(int energy) {
		setItemEnergy(energy);
		checkEnergyValues();
	}

	/**
	 * This function is included to allow the containing tile to directly and efficiently modify the energy contained in the EnergyStorage. Do not rely on this
	 * externally, as not all IEnergyHandlers are guaranteed to have it.
	 *
	 * @param energy
	 */
	public void modifyEnergyStored(int energy) {
		setItemEnergy(getItemEnergy() + energy);
		onContentsChanged();
		checkEnergyValues();
	}
	
	/**
	 * Used to test if storage has enough room for i
	 * @param i
	 */
	public boolean hasRoom(int i){
		return getItemEnergy() + i <= capacity;
	}

	/* IEnergyStorage */
	@Override
	public int fillCEnergy(int maxReceive, boolean simulate) {
		if(!this.canReceive()){
            return 0;
        }
		int energyReceived = Math.min(capacity - getItemEnergy(), Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			modifyEnergyStored(energyReceived);
		}
		return energyReceived;
	}

	@Override
	public int drainCEnergy(int maxExtract, boolean simulate) {
		if(!this.canExtract()){
            return 0;
        }
		int energyExtracted = Math.min(getItemEnergy(), Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			modifyEnergyStored(-energyExtracted);
		}
		return energyExtracted;
	}

	@Override
	public int getCEnergyStored() {
		return getItemEnergy();
	}

	@Override
	public int getMaxCEnergyStored() {

		return capacity;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

    protected void onContentsChanged()
    {

    }
    
    private final LazyOptional<ICEnergyStorage> holder = LazyOptional.of(() -> this);
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, EnumFacing side) {
		return CapabilityCrystalEnergy.CENERGY.orEmpty(cap, holder);
	}

}
