package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array;

import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.api.estorage.INetworkItemProvider;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.EnumUpdateType;
import alec_wam.CrystalMod.api.estorage.storage.IItemProvider;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileHDDArray extends TileEntityInventory implements ITickable, IMessageHandler, INetworkItemProvider {

	public TileHDDArray() {
		super("InvName", 8);
	}

	private EStorageNetwork network;
	private int priority = 0;
	public int facing = EnumFacing.NORTH.ordinal();
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Priority", priority);
		nbt.setInteger("Facing", facing);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		priority = nbt.getInteger("Priority");
		facing = nbt.getInteger("Facing");
		updateAfterLoad();
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return !ItemStackTools.isNullStack(stack) && stack.getItem() instanceof IItemProvider;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		super.setInventorySlotContents(slot, itemstack);
		if(this.network !=null && getNetworkInventory() !=null){
			network.getItemStorage().invalidate();
		}
		if(this.getWorld() !=null && this.getPos() !=null)BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("Priority")){
			setPriority(messageData.getInteger("Priority"));
		}
	}

	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}
	
	@Override
	public int getPriority(){
		return priority;
	}

	@Override
	public void setPriority(int i) {
		this.priority = i;
		if(getNetwork() !=null){
			getNetwork().updateInterfaces();
		}
	}

	@Override
	public INetworkInventory getNetworkInventory() {
		return new NetworkInventoryHDDArray(this);
	}

	@Override
	public void onDisconnected() {}
}
