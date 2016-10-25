package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CraftingTaskScheduler {
    private static final String NBT_SCHEDULED = "CraftingTaskScheduled";

    private ItemStack scheduledItem;

    public boolean canSchedule(ItemStack item) {
        return scheduledItem == null || !ItemUtil.canCombine(scheduledItem, item);
    }

    public void schedule(EStorageNetwork network, ItemStack item) {
        CraftingPattern pattern = network.getPatternWithBestScore(item);

        if (pattern != null && network.craftingController !=null) {
            scheduledItem = item;

            network.craftingController.addCraftingTaskAsLast(network.craftingController.createCraftingTask(scheduledItem, pattern, scheduledItem.stackSize));
        }
    }

    public void resetSchedule() {
        scheduledItem = null;
    }

    public void writeToNBT(NBTTagCompound tag) {
        if (scheduledItem != null) {
            tag.setTag(NBT_SCHEDULED, scheduledItem.serializeNBT());
        } else {
            tag.removeTag(NBT_SCHEDULED);
        }
    }

    public void read(NBTTagCompound tag) {
        if (tag.hasKey(NBT_SCHEDULED)) {
            scheduledItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_SCHEDULED));
        }
    }
}
