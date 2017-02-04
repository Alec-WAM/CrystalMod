package alec_wam.CrystalMod.tiles.pipes.estorage.power;

import java.util.Iterator;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import alec_wam.CrystalMod.api.estorage.INetworkPowerTile;
import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.MessageTileContainerUpdate;
import alec_wam.CrystalMod.tiles.ISynchronizedContainer;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileNetworkPowerCore extends TileEntityMod implements INetworkTile, ICEnergyReceiver, ISynchronizedContainer {

	//TODO Finish up Energy
	private EStorageNetwork network;
	public boolean connected;
	public NetworkPowerInfo info;
	
	private CEnergyStorage energyStorage;
	
	public TileNetworkPowerCore(){
		energyStorage = new CEnergyStorage(5000);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setTag("Energy", energyStorage.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		energyStorage = new CEnergyStorage(5000);
		energyStorage.readFromNBT(nbt.getCompoundTag("Energy"));
	}
	
	@Override
	public void update(){
		super.update();
		
		if(!getWorld().isRemote){
			for (EntityPlayer player : getWorld().playerEntities) {
                if (((ISynchronizedContainer) this).getContainer() == player.openContainer.getClass()) {
                    CrystalModNetwork.sendTo(new MessageTileContainerUpdate(this), (EntityPlayerMP) player);
                }
            }
			boolean creative = false;
			if(creative){
				energyStorage.setEnergyStored(energyStorage.getMaxCEnergyStored());
			} else {
				if(energyStorage.getCEnergyStored() - getEnergyUsage() >= 0){
					energyStorage.setEnergyStored(energyStorage.getCEnergyStored() - getEnergyUsage());
				} else {
					energyStorage.setEnergyStored(0);
				}
			}
		}
	}
	
	public int getEnergyUsage(){
		int usage = 0;
		if(network !=null){
			Iterator<INetworkPowerTile> ii = network.networkPoweredTiles.values().iterator();
			while(ii.hasNext()){
				usage+=ii.next().getEnergyUsage();
			}
		}
		return usage;
	}
	
	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
		this.connected = network !=null;
		markDirty();
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {
		if(network !=null){
			network.powerController = null;
		}
		this.connected = false;
		markDirty();
	}

	@Override
	public int getCEnergyStored(EnumFacing from) {
		return energyStorage.getCEnergyStored();
	}

	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return energyStorage.getMaxCEnergyStored();
	}

	@Override
	public boolean canConnectCEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int fillCEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyStorage.fillCEnergy(maxReceive, simulate);
	}

	@Override
	public void readContainerData(ByteBuf buf) {
		int energy = buf.readInt();
		int maxEnergy = buf.readInt();
		int usage = buf.readInt();
		this.info = new NetworkPowerInfo(energy, maxEnergy, usage);
	}

	@Override
	public void writeContainerData(ByteBuf buf) {
		buf.writeInt(energyStorage.getCEnergyStored());
		buf.writeInt(energyStorage.getMaxCEnergyStored());
		buf.writeInt(getEnergyUsage());
	}

	@Override
	public Class<? extends Container> getContainer() {
		return ContainerPowerCore.class;
	}
	
	public static class NetworkPowerInfo {
		public int storedEnergy;
		public int maxEnergy;
		public int energyUsage;
		
		public NetworkPowerInfo(int stored, int maxEnergy, int usage){
			this.storedEnergy = stored;
			this.maxEnergy = maxEnergy;
			this.energyUsage = usage;
		}
	}

}
