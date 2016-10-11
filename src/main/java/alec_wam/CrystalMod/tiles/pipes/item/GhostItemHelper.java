package alec_wam.CrystalMod.tiles.pipes.item;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GhostItemHelper
{
    public static void setItemGhostAmount(ItemStack stack, int amount)
    {
        NBTTagCompound tag = ItemNBTHelper.getCompound(stack);

        tag.setInteger("stackSize", amount);
    }

    public static int getItemGhostAmount(ItemStack stack)
    {
        NBTTagCompound tag = ItemNBTHelper.getCompound(stack);

        return tag.getInteger("stackSize");
    }

    public static boolean hasGhostAmount(ItemStack stack)
    {
        if (!stack.hasTagCompound())
        {
            return false;
        }

        NBTTagCompound tag = stack.getTagCompound();
        return tag.hasKey("stackSize");
    }

    public static void incrementGhostAmout(ItemStack stack, int value)
    {
        int amount = getItemGhostAmount(stack);
        amount += value;
        setItemGhostAmount(stack, amount);
    }

    public static void decrementGhostAmount(ItemStack stack, int value)
    {
        int amount = getItemGhostAmount(stack);
        amount -= value;
        setItemGhostAmount(stack, amount);
    }

    public static ItemStack getStackFromGhost(ItemStack ghostStack)
    {
        ItemStack newStack = ghostStack.copy();
        NBTTagCompound tag = ItemNBTHelper.getCompound(ghostStack);
        int amount = getItemGhostAmount(ghostStack);
        tag.removeTag("stackSize");
        if (tag.hasNoTags())
        {
            newStack.setTagCompound(null);
        }
        newStack.stackSize = amount;

        return newStack;
    }
}
