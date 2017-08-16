package alec_wam.CrystalMod.tiles.machine.power.redstonereactor;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.power.CustomEnergyStorage;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileRedstoneReactor extends TileEntityInventory implements IMessageHandler {

	public CustomEnergyStorage energyStorage;
	public WatchableInteger remainingFuel = new WatchableInteger();
	protected int lastSyncPowerStored = -1;
	
	public TileRedstoneReactor() {
		super("RedstoneReactor", 2);
		energyStorage = new CustomEnergyStorage(1000000);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Fuel", remainingFuel.getValue());
		energyStorage.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		remainingFuel.setValue(nbt.getInteger("Fuel"));
		energyStorage.readFromNBT(nbt);
	}
	
	@Override
	public void update(){
		super.update();
		
		if(!getWorld().isRemote){
			boolean powerChanged = (lastSyncPowerStored != energyStorage.getEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = energyStorage.getEnergyStored();
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Power", energyStorage.getEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
		    
		    boolean fuelChanged = (remainingFuel.getLastValue() != remainingFuel.getValue() && shouldDoWorkThisTick(5));
		    if(fuelChanged) {
		      remainingFuel.setLastValue(remainingFuel.getValue());
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Fuel", remainingFuel.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFuel", nbt), this);
		    }
		    
		    int offset = 0;
		    int currentRFPerTick = 2500;
			ItemStack upgrade = getStackInSlot(1);
			if(ItemStackTools.isValid(upgrade) && upgrade.getItem() == ModItems.reactorUpgrade){
				int tier = upgrade.getItemDamage()+1;
				currentRFPerTick+=2500*tier;
				offset = 0;
			}
			
			ItemStack currentFuel = getStackInSlot(0);
			if(ItemStackTools.isValid(currentFuel) && currentFuel.getItem() == ModItems.congealedRedstone){
				if(remainingFuel.getValue() <= 0 && energyStorage.receiveEnergy(currentRFPerTick, true) == currentRFPerTick){
					remainingFuel.setValue((TimeUtil.SECOND * 2)-offset);
					setInventorySlotContents(0, ItemUtil.consumeItem(currentFuel));
				}
			}
			
			if(shouldDoWorkThisTick(10)){
				if(remainingFuel.getValue() > 0 && !getWorld().isBlockPowered(getPos())){
					if(energyStorage.receiveEnergy(currentRFPerTick, true) == currentRFPerTick){
						remainingFuel.sub(1);
						energyStorage.receiveEnergy(currentRFPerTick, false);
					}
				}
			}
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 0){
			if(ItemStackTools.isValid(stack) && stack.getItem() == ModItems.congealedRedstone){
				return true;
			}
		}
		if(index == 1){
			if(ItemStackTools.isValid(stack) && stack.getItem() == ModItems.reactorUpgrade){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInteger("Power");
			energyStorage.setEnergyStored(newPower);
		}
		if(messageId.equalsIgnoreCase("UpdateFuel")){
			int newFuel = messageData.getInteger("Fuel");
			remainingFuel.setValue(newFuel);
		}
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == CapabilityEnergy.ENERGY){
            return (T) new IEnergyStorage(){

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {
					return 0;
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {
					return energyStorage.extractEnergy(maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {
					return energyStorage.getEnergyStored();
				}

				@Override
				public int getMaxEnergyStored() {
					return energyStorage.getMaxEnergyStored();
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
