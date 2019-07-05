package alec_wam.CrystalMod.tiles.machine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class TileEntityPoweredInventory extends TileEntityInventory implements IMessageHandler {
	public CEnergyStorage eStorage;
	public LazyOptional<ICEnergyStorage> holder; 
    protected float lastSyncPowerStored = -1;
	
	public TileEntityPoweredInventory(TileEntityType<?> tileEntityTypeIn, String name, int size) {
		super(tileEntityTypeIn, name, size);
		setupEnergy();
	}

	public abstract void setupEnergy();
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt){
        super.writeCustomNBT(nbt);		
		if(this.eStorage !=null){
			eStorage.writeToNBT(nbt);
		}
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		if(this.eStorage !=null){
			eStorage.readFromNBT(nbt);
		}
	}
	
	@Override
	public void tick(){
		super.tick();
    	
		if(!getWorld().isRemote){			
			boolean powerChanged = (lastSyncPowerStored != eStorage.getCEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = eStorage.getCEnergyStored();
		      CompoundNBT nbt = new CompoundNBT();
		      nbt.putInt("Power", eStorage.getCEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
		}
	}

	public ICEnergyStorage getEnergyStorage() {
		return eStorage;
	}
	
	@Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
		if (cap == CapabilityCrystalEnergy.CENERGY){
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }

	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInt("Power");
			this.eStorage.setEnergyStored(newPower);
		}
	}
}
