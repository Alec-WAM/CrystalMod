package alec_wam.CrystalMod.tiles.chest.wireless;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.PlayerUtil;

import com.google.common.collect.Maps;

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
        MapStorage storage = world.getMapStorage();
        WirelessChestManager instance = (WirelessChestManager) storage.getOrLoadData(WirelessChestManager.class, StorageKey);
        if (instance == null)
        {
            instance = new WirelessChestManager();
            storage.setData(StorageKey, instance);
        }

        return instance;
    }

    public void setDirty()
    {
        markDirty();
    }

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
                UUID uuid = PlayerUtil.uuidFromNBT(containerTag);

                Container container = new Container();
                container.deserializeNBT(containerTag);

                perPlayer.put(uuid, container);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagCompound temp = global.serializeNBT();

        nbtTagCompound.setTag("Inventories", temp.getTag("Inventories"));

        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, Container> e : perPlayer.entrySet())
        {
            NBTTagCompound tag = e.getValue().serializeNBT();
            PlayerUtil.uuidToNBT(tag, e.getKey());
            list.appendTag(tag);
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
                inventoryTag.setInteger("Code", entry.getKey());
                inventoryTag.setTag("Contents", inventory.serializeNBT());
                inventories.appendTag(inventoryTag);
            }
            tag.setTag("Inventories", inventories);
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            NBTTagList nbtTagList = nbt.getTagList("Inventories", Constants.NBT.TAG_COMPOUND);

            inventories.clear();

            for (int i = 0; i < nbtTagList.tagCount(); ++i)
            {
                NBTTagCompound inventoryTag = nbtTagList.getCompoundTagAt(i);
                int code = inventoryTag.getInteger("Code");

                WirelessInventory inventory = new WirelessInventory(this);

                inventory.deserializeNBT(inventoryTag.getCompoundTag("Contents"));

                inventories.put(code, inventory);
            }
        }

        void importNBT(NBTTagCompound nbt)
        {
            NBTTagList nbtTagList = nbt.getTagList("Inventories", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < nbtTagList.tagCount(); ++i)
            {
                NBTTagCompound inventoryTag = nbtTagList.getCompoundTagAt(i);
                int code = inventoryTag.getInteger("Code");

                if (!inventories.containsKey(code))
                {
                	WirelessInventory inventory = new WirelessInventory(this);


                    inventory.deserializeNBT(inventoryTag.getCompoundTag("Contents"));

                    inventories.put(code, inventory);
                }
            }
        }

        public void setDirty()
        {
            markDirty();
        }
    }
	
	public static class WirelessInventory extends ItemStackHandler {
		public int playerUsingCount;
		private IWirelessChestList list;
		public WirelessInventory(IWirelessChestList list){
			super(CrystalChestType.DARKIRON.size);
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