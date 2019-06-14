package alec_wam.CrystalMod.tiles.chests.wireless;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.chests.metal.MetalCrystalChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

public class WirelessChestManager extends WorldSavedData implements IWirelessChestList {
	
	private static final String StorageKey = CrystalMod.MODID + "_WirelessChestManager";

    private Container global = new Container();
    private Map<UUID, Container> perPlayer = Maps.newHashMap();

    public WirelessChestManager()
    {
        super(StorageKey);
    }

    public WirelessChestManager(String s)
    {
        super(s);
    }

    private static WirelessChestManager CLIENT_MANAGER;
    
    public static void resetClientManager(){
    	CLIENT_MANAGER = null;
    }
    
    public static WirelessChestManager get(World world)
    {
    	if(world instanceof ServerWorld){
    		ServerWorld serverWorld = (ServerWorld)world;
	    	DimensionSavedDataManager storage = serverWorld.func_217481_x();
	        WirelessChestManager instance = (WirelessChestManager) storage.func_215753_b(WirelessChestManager::new, StorageKey);
	        if (instance == null)
	        {
	            instance = new WirelessChestManager();
	            storage.func_215757_a(instance);
	        }
	        return instance;
    	} 
    	if(CLIENT_MANAGER == null){
    		CLIENT_MANAGER = new WirelessChestManager();
    	}
    	return CLIENT_MANAGER;
    }

    @Override
	public void setDirty()
    {
        markDirty();
    }

    @Override
	public WirelessInventory getInventory(int code)
    {
        return global.getInventory(code);
    }

    public IWirelessChestList getPrivate(PlayerEntity owner)
    {
        UUID key = owner.getUniqueID();
        return getPrivate(key);
    }

    public IWirelessChestList getPrivate(UUID uuid)
    {
        Container container = perPlayer.get(uuid);
        if (container == null)
        {
            container = new Container();
            perPlayer.put(new UUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()), container);
            markDirty();
        }

        return container;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
    	global.deserializeNBT(nbt);

        if (nbt.contains("Private", Constants.NBT.TAG_LIST))
        {
            ListNBT list = nbt.getList("Private", Constants.NBT.TAG_COMPOUND);

            perPlayer.clear();

            for (int i = 0; i < list.size(); ++i)
            {
                CompoundNBT containerTag = list.getCompound(i);
                UUID uuid = NBTUtil.readUniqueId(containerTag.getCompound("OwnerUUID"));

                Container container = new Container();
                container.deserializeNBT(containerTag);

                perPlayer.put(uuid, container);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbtTagCompound)
    {
    	CompoundNBT temp = global.serializeNBT();

        nbtTagCompound.put("Inventories", temp.get("Inventories"));

        ListNBT list = new ListNBT();
        for (Map.Entry<UUID, Container> e : perPlayer.entrySet())
        {
            CompoundNBT tag = e.getValue().serializeNBT();
            tag.put("OwnerUUID", NBTUtil.writeUniqueId(e.getKey()));
            list.add(tag);
        }

        nbtTagCompound.put("Private", list);

        return nbtTagCompound;
    }

    public void importCapabilityData(PlayerEntity player, CompoundNBT nbt)
    {
        ((Container) getPrivate(player)).importNBT(nbt);
    }

    private class Container implements INBTSerializable<CompoundNBT>, IWirelessChestList
    {
        private Map<Integer, WirelessInventory> inventories = new HashMap<Integer, WirelessInventory>();

        @Override
		public WirelessInventory getInventory(int code)
        {
        	WirelessInventory inventory = inventories.get(code);

            if (inventory == null)
            {
                inventory = new WirelessInventory(this);
                inventories.put(code, inventory);
            }

            return inventory;
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            CompoundNBT tag = new CompoundNBT();
            ListNBT inventories = new ListNBT();

            for (Map.Entry<Integer, WirelessInventory> entry : this.inventories.entrySet())
            {
            	WirelessInventory inventory = entry.getValue();

                CompoundNBT inventoryTag = new CompoundNBT();
                inventoryTag.putInt("Code", entry.getKey());
                inventoryTag.put("Contents", inventory.serializeNBT());
                inventories.add(inventoryTag);
            }
            tag.put("Inventories", inventories);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            ListNBT nbtTagList = nbt.getList("Inventories", Constants.NBT.TAG_COMPOUND);

            inventories.clear();

            for (int i = 0; i < nbtTagList.size(); ++i)
            {
                CompoundNBT inventoryTag = nbtTagList.getCompound(i);
                int code = inventoryTag.getInt("Code");

                WirelessInventory inventory = new WirelessInventory(this);

                inventory.deserializeNBT(inventoryTag.getCompound("Contents"));

                inventories.put(code, inventory);
            }
        }

        void importNBT(CompoundNBT nbt)
        {
            ListNBT nbtTagList = nbt.getList("Inventories", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < nbtTagList.size(); ++i)
            {
                CompoundNBT inventoryTag = nbtTagList.getCompound(i);
                int code = inventoryTag.getInt("Code");

                if (!inventories.containsKey(code))
                {
                	WirelessInventory inventory = new WirelessInventory(this);


                    inventory.deserializeNBT(inventoryTag.getCompound("Contents"));

                    inventories.put(code, inventory);
                }
            }
        }

        @Override
		public void setDirty()
        {
            markDirty();
        }
    }
	
	public static class WirelessInventory extends ItemStackHandler {
		public int playerUsingCount;
		private IWirelessChestList list;
		public WirelessInventory(IWirelessChestList list){
			super(MetalCrystalChestType.DARKIRON.size);
			this.list = list;
		}
		@Override
	    protected void onContentsChanged(int slot)
	    {
			super.onContentsChanged(slot);
	        list.setDirty();
	    }
	}
}