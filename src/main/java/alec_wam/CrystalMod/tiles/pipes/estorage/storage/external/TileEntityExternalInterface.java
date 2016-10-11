package alec_wam.CrystalMod.tiles.pipes.estorage.storage.external;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.INetworkItemProvider;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.INetworkInventory;

public class TileEntityExternalInterface extends TileEntityMod implements IMessageHandler, INetworkItemProvider{

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
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public void setPriority(int i) {
		priority = i;
		if(getNetwork() !=null){
			getNetwork().updateInterfaces();
		}
	}

	@Override
	public INetworkInventory getNetworkInventory() {
		return new NetworkInventoryExternal(this);
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("Priority")){
			priority = messageData.getInteger("Priority");
		}
	}

	public void onNeighborChange() {
		if(getNetworkInventory() !=null && getNetwork() !=null){
			getNetworkInventory().updateItems(getNetwork(), -1);
		}
	}

	@Override
	public void onDisconnected() {}

}
