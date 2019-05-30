package alec_wam.CrystalMod.tiles.chests.wireless;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.chests.metal.MetalCrystalChestType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;
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

    public static WirelessChestManager get(World world)
    {
    	WorldSavedDataStorage storage = world.getMapStorage();
        WirelessChestManager instance = (WirelessChestManager) storage.func_212426_a(DimensionType.OVERWORLD, WirelessChestManager::new, StorageKey);
        if (instance == null)
        {
            instance = new WirelessChestManager();
            storage.func_212424_a(DimensionType.OVERWORLD, StorageKey, instance);
        }
        return instance;
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

    public IWirelessChestList getPrivate(EntityPlayer owner)
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
    public void read(NBTTagCompound nbt)
    {
    	global.deserializeNBT(nbt);

        if (nbt.contains("Private", Constants.NBT.TAG_LIST))
        {
            NBTTagList list = nbt.getList("Private", Constants.NBT.TAG_COMPOUND);

            perPlayer.clear();

            for (int i = 0; i < list.size(); ++i)
            {
                NBTTagCompound containerTag = list.getCompound(i);
                UUID uuid = NBTUtil.readUniqueId(containerTag.getCompound("OwnerUUID"));

                Container container = new Container();
                container.deserializeNBT(containerTag);

                perPlayer.put(uuid, container);
            }
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound nbtTagCompound)
    {
    	NBTTagCompound temp = global.serializeNBT();

        nbtTagCompound.setTag("Inventories", temp.getTag("Inventories"));

        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, Container> e : perPlayer.entrySet())
        {
            NBTTagCompound tag = e.getValue().serializeNBT();
            tag.setTag("OwnerUUID", NBTUtil.writeUniqueId(e.getKey()));
            list.add(tag);
        }

        nbtTagCompound.setTag("Private", list);

        return nbtTagCompound;
    }

    public void importCapabilityData(EntityPlayer player, NBTTagCompound nbt)
    {
        ((Container) getPrivate(player)).importNBT(nbt);
    }

    private class Container implements INBTSerializable<NBTTagCompound>, IWirelessChestList
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
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList inventories = new NBTTagList();

            for (Map.Entry<Integer, WirelessInventory> entry : this.inventories.entrySet())
            {
            	WirelessInventory inventory = entry.getValue();

                NBTTagCompound inventoryTag = new NBTTagCompound();
                inventoryTag.setInt("Code", entry.getKey());
                inventoryTag.setTag("Contents", inventory.serializeNBT());
                inventories.add(inventoryTag);
            }
            tag.setTag("Inventories", inventories);
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            NBTTagList nbtTagList = nbt.getList("Inventories", Constants.NBT.TAG_COMPOUND);

            inventories.clear();

            for (int i = 0; i < nbtTagList.size(); ++i)
            {
                NBTTagCompound inventoryTag = nbtTagList.getCompound(i);
                int code = inventoryTag.getInt("Code");

                WirelessInventory inventory = new WirelessInventory(this);

                inventory.deserializeNBT(inventoryTag.getCompound("Contents"));

                inventories.put(code, inventory);
            }
        }

        void importNBT(NBTTagCompound nbt)
        {
            NBTTagList nbtTagList = nbt.getList("Inventories", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < nbtTagList.size(); ++i)
            {
                NBTTagCompound inventoryTag = nbtTagList.getCompound(i);
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