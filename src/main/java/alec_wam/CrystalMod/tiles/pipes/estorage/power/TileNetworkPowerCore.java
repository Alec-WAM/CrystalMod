package alec_wam.CrystalMod.tiles.pipes.estorage.power;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.estorage.INetworkPowerTile;
import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.MessageTileContainerUpdate;
import alec_wam.CrystalMod.tiles.ISynchronizedContainer;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class TileNetworkPowerCore extends TileEntityMod implements INetworkTile, ISynchronizedContainer {

	private EStorageNetwork network;
	public boolean connected;
	public NetworkPowerInfo info;
	
	private CEnergyStorage energyStorage;
	
	public TileNetworkPowerCore(){
		energyStorage = new CEnergyStorage(5000) {
			@Override
			public boolean canExtract(){
				return false;
			}
		};
	}
	
	public CEnergyStorage getEnergyStorage(){
		return energyStorage;
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
		if(network !=null)network.powerController = null;
		this.connected = false;
		markDirty();
	}

	@Override
	public void readContainerData(ByteBuf buf) {
		int energy = buf.readInt();
		int maxEnergy = buf.readInt();
		int usage = buf.readInt();
		List<ClientPowerTileInfo> infoList = Lists.newArrayList();
		int size = buf.readInt();
		for(int i = 0; i < size; i++){
			ItemStack tileStack = ByteBufUtils.readItemStack(buf);
			int count = buf.readInt();
			int energyUsage = buf.readInt();
			infoList.add(new ClientPowerTileInfo(tileStack, count, energyUsage));
		}
		this.info = new NetworkPowerInfo(energy, maxEnergy, usage, infoList);
	}

	@Override
	public void writeContainerData(ByteBuf buf) {
		buf.writeInt(energyStorage.getCEnergyStored());
		buf.writeInt(energyStorage.getMaxCEnergyStored());
		buf.writeInt(getEnergyUsage());
		
		if(network == null) buf.writeInt(0);
		else {
			List<ClientPowerTileInfo> infoList = Lists.newArrayList();
			Iterator<INetworkPowerTile> i = network.networkPoweredTiles.values().iterator();
			while(i.hasNext()){
				INetworkPowerTile powerTile = i.next();
				ItemStack tileStack = powerTile.getDisplayStack();
				ClientPowerTileInfo info = new ClientPowerTileInfo(tileStack, 1, powerTile.getEnergyUsage());
				if(infoList.contains(info)){
					ClientPowerTileInfo otherInfo = infoList.get(infoList.indexOf(info));
					otherInfo.count+=1;
				} else {
					infoList.add(info);
				}
			}
			Collections.sort(infoList, new Comparator<ClientPowerTileInfo>(){

				@Override
				public int compare(ClientPowerTileInfo arg0, ClientPowerTileInfo arg1) {
					return Integer.compare(arg0.usage, arg1.usage);
				}
				
			});
			buf.writeInt(infoList.size());
			for(ClientPowerTileInfo info : infoList){
				ByteBufUtils.writeItemStack(buf, info.stack);
				buf.writeInt(info.count);
				buf.writeInt(info.usage);
			}
		}
	}

	@Override
	public Class<? extends Container> getContainer() {
		return ContainerPowerCore.class;
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
	  if(capability == CapabilityCrystalEnergy.CENERGY){
		  return true;
	  }
      return super.hasCapability(capability, facingIn);
    }

	@SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityCrystalEnergy.CENERGY) {
            return (T) energyStorage;
        }
        return super.getCapability(capability, facing);
    }
	
	public static class NetworkPowerInfo {
		public int storedEnergy;
		public int maxEnergy;
		public int energyUsage;
		public List<ClientPowerTileInfo> infoList;
		
		public NetworkPowerInfo(int stored, int maxEnergy, int usage, List<ClientPowerTileInfo> infoList){
			this.storedEnergy = stored;
			this.maxEnergy = maxEnergy;
			this.energyUsage = usage;
			this.infoList = infoList;
		}
	}
	
	public static class ClientPowerTileInfo {
		public ItemStack stack = ItemStackTools.getEmptyStack();
		public int count;
		public int usage;
		
		public ClientPowerTileInfo(ItemStack stack, int count, int usage){
			this.stack = stack;
			this.count = count;
			this.usage = usage;
		}
		
		@Override
	    public boolean equals(Object other) {
	        if (this == other) {
	            return true;
	        }

	        if (!(other instanceof ClientPowerTileInfo)) {
	            return false;
	        }

	        return usage == ((ClientPowerTileInfo) other).usage && ItemUtil.canCombine(stack, ((ClientPowerTileInfo) other).stack);
	    }

	    @Override
	    public int hashCode() {
	        int result = stack.hashCode();
	        result = 31 * result + usage;
	        return result;
	    } 
	}

}
