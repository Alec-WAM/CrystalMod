package alec_wam.CrystalMod.tiles.energy.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.data.WatchableInteger;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class TileEntityEngineBase extends TileEntityMod implements IMessageHandler, INamedContainerProvider, INBTDrop {

	public CEnergyStorage energyStorage;
	public WatchableInteger fuel = new WatchableInteger();
	public WatchableInteger maxFuel = new WatchableInteger();
	public int multi;
	
    //private MachineSound runningSound;
	
	public TileEntityEngineBase(TileEntityType<?> tileEntityTypeIn){
		super(tileEntityTypeIn);
		multi = 1;
		energyStorage = createStorage(multi);
	}
	
	public TileEntityEngineBase(TileEntityType<?> tileEntityTypeIn, int multi){
		super(tileEntityTypeIn);
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
	public void writeToItemNBT(ItemStack stack){
		CompoundNBT nbt = new CompoundNBT();
		writeEngineData(nbt);
	    ItemNBTHelper.getCompound(stack).put("EngineData", nbt);
	}
	
	public void writeEngineData(CompoundNBT nbt){
		this.energyStorage.writeToNBT(nbt);
	    nbt.putInt("Fuel", fuel.getValue());
	    nbt.putInt("MaxFuel", maxFuel.getValue());
	}
	
	@Override
	public void readFromItemNBT(ItemStack stack){
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack).getCompound("EngineData");
		readEngineData(nbt);
	}
	
	public void readEngineData(CompoundNBT nbt){
		this.fuel.setValue(nbt.getInt("Fuel"));
	    this.maxFuel.setValue(nbt.getInt("MaxFuel"));
		this.energyStorage.readFromNBT(nbt);
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
	    nbt.putInt("Fuel", fuel.getValue());
	    nbt.putInt("MaxFuel", maxFuel.getValue());
	    nbt.putInt("Multi", this.multi);
	    this.energyStorage.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt)
	{
	    super.readCustomNBT(nbt);
	    this.fuel.setValue(nbt.getInt("Fuel"));
	    this.maxFuel.setValue(nbt.getInt("MaxFuel"));
	    this.multi = nbt.getInt("Multi");
	    this.energyStorage.readFromNBT(nbt);
	}
	
	protected float lastSyncPowerStored = -1;
	
	/*public boolean hasRunningSound() {
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
	}*/
	
	@Override
	public void tick(){
		super.tick();
		/*if(getWorld().isRemote){
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
		}*/
		if(!getWorld().isRemote){
			boolean powered = getWorld().getRedstonePowerFromNeighbors(getPos()) > 0;
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
			boolean powerChanged = (lastSyncPowerStored != energyStorage.getCEnergyStored());
		    
		    if(!powered){
				for(Direction face : Direction.values()){
					TileEntity tile = this.getWorld().getTileEntity(getPos().offset(face));
					if(tile !=null && tile.getCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite()).isPresent()){
						ICEnergyStorage rec = tile.getCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite()).orElse(null);
						if(rec !=null){
							if(!rec.canReceive())continue;
							int fill = rec.fillCEnergy(Math.min(energyStorage.getMaxExtract(), energyStorage.getCEnergyStored()), false);
							if(fill > 0){
								this.energyStorage.modifyEnergyStored(-fill);
								powerChanged = true;
							}
						}
					}
				}
		    }
			
			
			if(powerChanged && shouldDoWorkThisTick(5)) {
		      lastSyncPowerStored = energyStorage.getCEnergyStored();
		      CompoundNBT nbt = new CompoundNBT();
		      nbt.putInt("Power", energyStorage.getCEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
		    
		    boolean fuelChanged = (fuel.getLastValue() != fuel.getValue() && shouldDoWorkThisTick(5));
		    if(fuelChanged) {
		      fuel.setLastValue(fuel.getValue());
		      CompoundNBT nbt = new CompoundNBT();
		      nbt.putInt("Fuel", fuel.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFuel", nbt), this);
		    }
		    
		    boolean maxChanged = (maxFuel.getLastValue() != maxFuel.getValue() && shouldDoWorkThisTick(5));
		    if(maxChanged) {
		      maxFuel.setLastValue(maxFuel.getValue());
		      CompoundNBT nbt = new CompoundNBT();
		      nbt.putInt("Max", maxFuel.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFuel", nbt), this);
		    }
		    
		    boolean newActive = fuel.getValue() > 0 && !powered;
		    if(isActive() !=newActive){
		    	setActive(newActive);
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
		return scale * (this.fuel.getValue()) / this.maxFuel.getValue();
	}
	
	public Direction getFacing(){
		return getBlockState().get(BlockEngine.FACING);
	}
	
	public boolean isActive(){
		return getBlockState().get(BlockEngine.ACTIVE);
	}
	
	public void setActive(boolean value){
		this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BlockEngine.ACTIVE, Boolean.valueOf(value)), 3);
		this.markDirty();
	}
	
	@Override
	public void handleMessage(String messageId, CompoundNBT messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInt("Power");
			this.energyStorage.setEnergyStored(newPower);
		}
		
		if(messageId.equalsIgnoreCase("UpdateFuel")){
			int newFuel = messageData.contains("Fuel") ? messageData.getInt("Fuel") : fuel.getValue();
			int newMax = messageData.contains("Max") ? messageData.getInt("Max") : maxFuel.getValue();
			this.fuel.setValue(newFuel);
			this.maxFuel.setValue(newMax);
		}
	}
	

	private final ICEnergyStorage energyHandler = new ICEnergyStorage(){

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
	private final LazyOptional<ICEnergyStorage> holder = LazyOptional.of(() -> energyHandler);
	
	@Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityCrystalEnergy.CENERGY){
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }
}
