package alec_wam.CrystalMod.tiles.machine.power.engine;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.client.sound.MachineSound;
import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IMachineTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.data.watchable.WatchableBoolean;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityEngineBase extends TileEntityMod implements IMessageHandler, IMachineTile {

	public CEnergyStorage energyStorage;
	public WatchableInteger fuel = new WatchableInteger();
	public WatchableInteger maxFuel = new WatchableInteger();
	public WatchableBoolean active = new WatchableBoolean();
	public int multi;
	public int facing;
	@SideOnly(Side.CLIENT)
    private MachineSound runningSound;
	
	public TileEntityEngineBase(){
		multi = 1;
		energyStorage = createStorage(multi);
	}
	
	public TileEntityEngineBase(int multi){
		this.multi = multi;
		energyStorage = createStorage(this.multi);
	}
	
	public void updateMulti(int multi){
		this.multi = multi;
		final int energy = this.energyStorage.getCEnergyStored();
		energyStorage = createStorage(this.multi);
		energyStorage.setEnergyStored(energy);
	}
	
	public abstract CEnergyStorage createStorage(int multi);
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
	    nbt.setInteger("Fuel", fuel.getValue());
	    nbt.setInteger("MaxFuel", maxFuel.getValue());
	    nbt.setInteger("Multi", this.multi);
	    nbt.setInteger("Facing", facing);
	    this.energyStorage.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt)
	{
	    super.readCustomNBT(nbt);
	    this.fuel.setValue(nbt.getInteger("Fuel"));
	    this.maxFuel.setValue(nbt.getInteger("MaxFuel"));
	    this.multi = nbt.getInteger("Multi");
	    this.facing = nbt.getInteger("Facing");
	    this.energyStorage.readFromNBT(nbt);
	}
	
	protected float lastSyncPowerStored = -1;
	
	public boolean hasRunningSound() {
		return getRunningSound() != null;
	}
	
	@Nullable
	public ResourceLocation getRunningSound() {
		return ModSounds.engine.getSoundName();
	}
	
	public float getVolume() {
		return 0.8F;
	}

	public float getPitch() {
		return 1.0f;
	}
	
	public boolean shouldPlayRunningSound(){
		return isActive() && !isInvalid();
	}
	
	@Override
	public void invalidate(){
		super.invalidate();
		this.runningSound.endPlaying();
	}
	
	@Override
	public void update(){
		super.update();
		if(getWorld().isRemote){
			if(hasRunningSound()){
				final ResourceLocation soundLocation = getRunningSound();
				if (shouldPlayRunningSound() && soundLocation != null) {
					if (runningSound == null) {
						FMLClientHandler.instance().getClient().getSoundHandler().playSound(runningSound = new MachineSound(soundLocation, pos, getVolume(), getPitch()));
					}
				} else if (runningSound != null) {
					runningSound.endPlaying();
					runningSound = null;
				}
			}
		}
		if(!getWorld().isRemote){
			boolean powered = getWorld().isBlockIndirectlyGettingPowered(getPos()) > 0;
			if(!powered && this.energyStorage.getCEnergyStored() < this.energyStorage.getMaxCEnergyStored()){
				if(canRefuel()){
					refuel();
				}else{
					
					//Energy added is value x fuel (ticks) speed is multiplier.
					
					for(int m = 0; (m < multi && fuel.getValue() > 0 && energyStorage.hasRoom(getFuelValue())); m++){
				  		final int calc = getFuelValue();
				    	this.energyStorage.modifyEnergyStored(calc);
				    	fuel.subSafe(1);
					}
				}
			}
			
			boolean powerChanged = (lastSyncPowerStored != energyStorage.getCEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = energyStorage.getCEnergyStored();
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Power", energyStorage.getCEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
		    
		    boolean fuelChanged = (fuel.getLastValue() != fuel.getValue() && shouldDoWorkThisTick(5));
		    if(fuelChanged) {
		      fuel.setLastValue(fuel.getValue());
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Fuel", fuel.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFuel", nbt), this);
		    }
		    
		    boolean maxChanged = (maxFuel.getLastValue() != maxFuel.getValue() && shouldDoWorkThisTick(5));
		    if(maxChanged) {
		      maxFuel.setLastValue(maxFuel.getValue());
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Max", maxFuel.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFuel", nbt), this);
		    }
		    
		    active.setValue(fuel.getValue() > 0 && !powered);
		    
		    boolean activeChanged = (active.getLastValue() != active.getValue() && shouldDoWorkThisTick(5));
		    if(activeChanged) {
		      active.setLastValue(active.getValue());
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setBoolean("Active", active.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateActive", nbt), this);
		    }
			
		    if(!powered){
				for(EnumFacing face : EnumFacing.VALUES){
					TileEntity tile = this.getWorld().getTileEntity(getPos().offset(face));
					if(tile !=null && tile.hasCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite())){
						ICEnergyStorage rec = tile.getCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite());
						if(!rec.canReceive())continue;
						int fill = rec.fillCEnergy(Math.min(energyStorage.getMaxExtract(), energyStorage.getCEnergyStored()), false);
						if(fill > 0){
							this.energyStorage.modifyEnergyStored(-fill);
						}
					}
				}
		    }
		}
		
	}
	
	public boolean canRefuel(){
		return this.energyStorage.getCEnergyStored() < this.energyStorage.getMaxCEnergyStored() && fuel.getValue() <= 0;
	}
	
	public abstract void refuel();
	
	public abstract int getFuelValue();

	public int getScaledFuel(int scale) {
		if ((this.maxFuel.getValue() <= 0) || (this.fuel.getValue() <= 0)) {
	        return 0;
	    }
		return scale * (this.maxFuel.getValue() - this.fuel.getValue()) / this.maxFuel.getValue();
	}
	
	@Override
	public void setFacing(int facing){
		this.facing = facing;
	}
	
	@Override
	public int getFacing(){
		return facing;
	}
	
	@Override
	public boolean isActive(){
		return active.getValue();
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInteger("Power");
			this.energyStorage.setEnergyStored(newPower);
		}
		
		if(messageId.equalsIgnoreCase("UpdateActive")){
			active.setValue(messageData.getBoolean("Active"));
			BlockUtil.markBlockForUpdate(world, pos);
		}
		
		if(messageId.equalsIgnoreCase("UpdateFuel")){
			int newFuel = messageData.hasKey("Fuel") ? messageData.getInteger("Fuel") : fuel.getValue();
			int newMax = messageData.hasKey("Max") ? messageData.getInteger("Max") : maxFuel.getValue();
			this.fuel.setValue(newFuel);
			this.maxFuel.setValue(newMax);
		}
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
		return capability == CapabilityCrystalEnergy.CENERGY || super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == CapabilityCrystalEnergy.CENERGY){
            return (T) new ICEnergyStorage(){

				@Override
				public int fillCEnergy(int maxReceive, boolean simulate) {
					return 0;
				}

				@Override
				public int drainCEnergy(int maxExtract, boolean simulate) {
					return 0;
				}

				@Override
				public int getCEnergyStored() {
					return energyStorage.getCEnergyStored();
				}

				@Override
				public int getMaxCEnergyStored() {
					return energyStorage.getMaxCEnergyStored();
				}

				@Override
				public boolean canExtract() {
					return true;
				}

				@Override
				public boolean canReceive() {
					return false;
				}
            	
            };
        }
        return super.getCapability(capability, facing);
    }
}
