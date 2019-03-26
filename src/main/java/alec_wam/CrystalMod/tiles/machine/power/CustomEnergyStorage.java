package alec_wam.CrystalMod.tiles.machine.power;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage{

	public CustomEnergyStorage(int capacity){
        super(capacity, capacity, capacity);
    }
	
    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract){
        super(capacity, maxReceive, maxExtract);
    }

    public int extractEnergyInternal(int maxExtract, boolean simulate){
        int before = this.maxExtract;
        this.maxExtract = Integer.MAX_VALUE;

        int toReturn = this.extractEnergy(maxExtract, simulate);

        this.maxExtract = before;
        return toReturn;
    }

    public int receiveEnergyInternal(int maxReceive, boolean simulate){
        int before = this.maxReceive;
        this.maxReceive = Integer.MAX_VALUE;

        int toReturn = this.receiveEnergy(maxReceive, simulate);

        this.maxReceive = before;
        return toReturn;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate){
        if(!this.canReceive()){
            return 0;
        }
        int energy = this.getEnergyStored();

        int energyReceived = Math.min(this.capacity-energy, Math.min(this.maxReceive, maxReceive));
        if(!simulate){
            this.setEnergyStored(energy+energyReceived);
            onContentsChanged();
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate){
        if(!this.canExtract()){
            return 0;
        }
        int energy = this.getEnergyStored();

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if(!simulate){
            this.setEnergyStored(energy-energyExtracted);
            onContentsChanged();
        }
        return energyExtracted;
    }

    public void readFromNBT(NBTTagCompound compound){
        this.setEnergyStored(compound.getInteger("Energy"));
    }

    public void writeToNBT(NBTTagCompound compound){
        compound.setInteger("Energy", this.getEnergyStored());
    }

    public void setEnergyStored(int energy){
        this.energy = energy;
    }

    protected void onContentsChanged()
    {

    }
}
