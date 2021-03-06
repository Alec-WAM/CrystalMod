package alec_wam.CrystalMod.tiles.pipes.estorage.storage.external;

import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.api.estorage.INetworkItemProvider;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityExternalInterface extends TileEntityMod implements IMessageHandler, INetworkItemProvider{

	private EStorageNetwork network;
	private int priority = 0;
	public int facing = EnumFacing.NORTH.ordinal();
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Priority", priority);
		nbt.setInteger("Facing", facing);
	}
	
	@Override
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
			getNetwork().getItemStorage().invalidate();
			getNetwork().getFluidStorage().invalidate();
		}
	}

	@Override
	public void onDisconnected() {}

}
