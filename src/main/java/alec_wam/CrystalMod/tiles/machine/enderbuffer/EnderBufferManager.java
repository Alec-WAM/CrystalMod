package alec_wam.CrystalMod.tiles.machine.enderbuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.machine.power.CustomEnergyStorage;
import alec_wam.CrystalMod.tiles.tank.Tank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnderBufferManager extends WorldSavedData implements IEnderBufferList {
	
	private static final String StorageKey = CrystalMod.MODID + "_EnderBufferManager";

    private Container global = new Container(null);
    private Map<UUID, Container> perPlayer = Maps.newHashMap();

    public EnderBufferManager()
    {
        super(StorageKey);
    }

    public EnderBufferManager(String s)
    {
        super(s);
    }

    public static EnderBufferManager get(World world)
    {
        MapStorage storage = world.getMapStorage();
        EnderBufferManager instance = (EnderBufferManager) storage.getOrLoadData(EnderBufferManager.class, StorageKey);
        if (instance == null)
        {
            instance = new EnderBufferManager();
            storage.setData(StorageKey, instance);
        }

        return instance;
    }

    @Override
	public void setDirty()
    {
        markDirty();
    }

    @Override
	public EnderBuffer getBuffer(int id)
    {
        return global.getBuffer(id);
    }

    public IEnderBufferList getPrivate(EntityPlayer owner)
    {
        UUID key = owner.getUniqueID();
        return getPrivate(key);
    }

    public IEnderBufferList getPrivate(UUID uuid)
    {
        Container container = perPlayer.get(uuid);
        if (container == null)
        {
            container = new Container(uuid);
            perPlayer.put(new UUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()), container);
            markDirty();
        }

        return container;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        global.deserializeNBT(nbt);

        if (nbt.hasKey("Private", Constants.NBT.TAG_LIST))
        {
            NBTTagList list = nbt.getTagList("Private", Constants.NBT.TAG_COMPOUND);

            perPlayer.clear();

            for (int i = 0; i < list.tagCount(); ++i)
            {
                NBTTagCompound containerTag = list.getCompoundTagAt(i);
                UUID uuid = NBTUtil.getUUIDFromTag(containerTag.getCompoundTag("OwnerUUID"));

                Container container = new Container(uuid);
                container.deserializeNBT(containerTag);

                perPlayer.put(uuid, container);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagCompound temp = global.serializeNBT();

        nbtTagCompound.setTag("Buffers", temp.getTag("Buffers"));

        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, Container> e : perPlayer.entrySet())
        {
            NBTTagCompound tag = e.getValue().serializeNBT();
            tag.setTag("OwnerUUID", NBTUtil.createUUIDTag(e.getKey()));
            list.appendTag(tag);
        }

        nbtTagCompound.setTag("Private", list);

        return nbtTagCompound;
    }

    public void importCapabilityData(EntityPlayer player, NBTTagCompound nbt)
    {
        ((Container) getPrivate(player)).importNBT(nbt);
    }

    private class Container implements INBTSerializable<NBTTagCompound>, IEnderBufferList
    {
        private Map<Integer, EnderBuffer> inventories = new HashMap<Integer, EnderBuffer>();
        private UUID ownerUUID;
        public Container(UUID uuid){
        	this.ownerUUID = uuid;
        }
        
        @Override
		public EnderBuffer getBuffer(int id)
        {
            EnderBuffer inventory = inventories.get(id);

            if (inventory == null)
            {
                inventory = new EnderBuffer(this, id, ownerUUID);
                inventories.put(id, inventory);
            }

            return inventory;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList inventories = new NBTTagList();
            for (Map.Entry<Integer, EnderBuffer> entry : this.inventories.entrySet())
            {
                EnderBuffer inventory = entry.getValue();

                NBTTagCompound inventoryTag = new NBTTagCompound();
                inventoryTag.setInteger("BufferId", entry.getKey());
                inventoryTag.setTag("BufferContents", inventory.serializeNBT());
                inventories.appendTag(inventoryTag);
            }
            tag.setTag("Buffers", inventories);
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
        	NBTTagList nbtTagList = nbt.getTagList("Buffers", Constants.NBT.TAG_COMPOUND);

            inventories.clear();

            for (int i = 0; i < nbtTagList.tagCount(); ++i)
            {
                NBTTagCompound inventoryTag = nbtTagList.getCompoundTagAt(i);
                int j = inventoryTag.getInteger("BufferId");

                EnderBuffer inventory = new EnderBuffer(this, j, ownerUUID);

                inventory.deserializeNBT(inventoryTag.getCompoundTag("BufferContents"));

                inventories.put(j, inventory);
            }
        }

        void importNBT(NBTTagCompound nbt)
        {
        	NBTTagList nbtTagList = nbt.getTagList("Buffers", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < nbtTagList.tagCount(); ++i)
            {
                NBTTagCompound inventoryTag = nbtTagList.getCompoundTagAt(i);
                int j = inventoryTag.getInteger("BufferId");

                if (!inventories.containsKey(j))
                {
                    EnderBuffer inventory = new EnderBuffer(this, j, ownerUUID);


                    inventory.deserializeNBT(inventoryTag.getCompoundTag("BufferContents"));

                    inventories.put(j, inventory);
                }
            }
        }

        @Override
		public void setDirty()
        {
            markDirty();
        }
    }
	
    public static class EnderBufferClientData {
    	public int cu;
    	public int rf;
    	public FluidStack fluid;
    }
    
	public static class EnderBuffer implements INBTSerializable<NBTTagCompound>{
		private int code;
		private UUID ownerUUID;
		public CEnergyStorage cuStorage;
		public int lastSyncedCU;
		
		public CustomEnergyStorage rfStorage;
		public int lastSyncedRF;
		
		public Tank tank;
		public FluidStack lastSyncedFluid;
		
		public GenericInventory sendInv;
		
		public boolean forceSync;
		public List<EntityPlayerMP> watchers;
		@SideOnly(Side.CLIENT)
		public EnderBufferClientData clientData;
		
		public EnderBuffer(IEnderBufferList list, int code, UUID ownerUUID){
			this.code = code;
			this.ownerUUID = ownerUUID;
			this.watchers = Lists.newArrayList();
			this.rfStorage = new CustomEnergyStorage(500000, Integer.MAX_VALUE, Integer.MAX_VALUE){
				@Override
				public void onContentsChanged(){
					list.setDirty();
					updateWatchers();
				}
			};
			this.cuStorage = new CEnergyStorage(500000, Integer.MAX_VALUE){
				@Override
				public void onContentsChanged(){
					list.setDirty();
					updateWatchers();
				}
			};
			this.tank = new Tank("Tank", Fluid.BUCKET_VOLUME * 16, null) {
				@Override
				public void onContentsChanged(){
					list.setDirty();
					updateWatchers();
				}
			};
			this.sendInv = new GenericInventory(10, list);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			
			NBTTagCompound rfNBT = new NBTTagCompound();
			rfStorage.writeToNBT(rfNBT);
			nbt.setTag("RF", rfNBT);
			
			NBTTagCompound cuNBT = new NBTTagCompound();
			cuStorage.writeToNBT(cuNBT);
			nbt.setTag("CU", cuNBT);
			
			nbt.setTag("Send_Inv", sendInv.serializeNBT());
			
			tank.writeToNBT(nbt);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			NBTTagCompound rfNBT = nbt.getCompoundTag("RF");
			rfStorage.readFromNBT(rfNBT);
			
			NBTTagCompound cuNBT = nbt.getCompoundTag("CU");
			cuStorage.readFromNBT(cuNBT);
			
			NBTTagCompound sendInvNBT = nbt.getCompoundTag("Send_Inv");
			sendInv.deserializeNBT(sendInvNBT);
			
			tank.readFromNBT(nbt);
		}
		
		public void onPlayerOpenContainer(EntityPlayer player){
			if(player instanceof EntityPlayerMP){
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
	        	watchers.add(playerMP);
				CrystalModNetwork.sendTo(new PacketEnderBufferClientSync(code, ownerUUID, cuStorage.getCEnergyStored(), rfStorage.getEnergyStored(), tank.getFluid()), playerMP);
	        }
		}
		
		public void updateWatchers(){
			//if(FMLCommonHandler.instance().getEffectiveSide() !=Side.SERVER)return;
			for(EntityPlayerMP player : watchers){
				CrystalModNetwork.sendTo(new PacketEnderBufferClientSync(code, ownerUUID, cuStorage.getCEnergyStored(), rfStorage.getEnergyStored(), tank.getFluid()), player);
			}
		}
	}
}