package alec_wam.CrystalMod.tiles.machine.inventory.charger;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.item.InventoryWrapper;

public abstract class TileEntityInventoryCharger extends TileEntityMod implements ITickable, IMessageHandler {


	public int facing = EnumFacing.NORTH.ordinal();
	
	protected float lastSyncPowerStored = -1;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", facing);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = nbt.getInteger("Facing");
		updateAfterLoad();
	}
	
	public void update(){
		super.update();
		if(!worldObj.isRemote){
			
			boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = getEnergyStored();
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Power", getEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
			
			ISidedInventory inv = getInventory();
			if(inv !=null){
				EnumFacing face = EnumFacing.getFront(facing);
				int[] slots = inv.getSlotsForFace(face.getOpposite());
				for(int i = 0; i < slots.length; i++){
					int s = slots[i];
					ItemStack stack = inv.getStackInSlot(s);
					if(stack !=null){
						if(canChargeItem(stack)){
							chargeItem(stack);
						}
					}
				}
			}
		}
	}
	
	public ISidedInventory getInventory(){
		TileEntity external = getWorld().getTileEntity(getPos().offset(EnumFacing.getFront(facing)));
		if(external !=null){
			if(external instanceof ISidedInventory){
				return (ISidedInventory)external;
			}else if(external instanceof IInventory){
				return new InventoryWrapper((IInventory)external);
			}
		}
		return null; 
	}
	
	public abstract boolean canChargeItem(ItemStack stack);
	
	public abstract void chargeItem(ItemStack stack);
	
	protected abstract int getEnergyStored();
	protected abstract void setEnergyStored(int energy);
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInteger("Power");
			setEnergyStored(newPower);
		}
	}
	
}
