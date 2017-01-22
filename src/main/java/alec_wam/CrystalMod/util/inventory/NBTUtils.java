package alec_wam.CrystalMod.util.inventory;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public class NBTUtils
{
    public static NBTTagCompound writeTagToNBT(NBTTagCompound nbt, String name, NBTBase tag)
    {
        if (name == null)
        {
            return nbt;
        }

        if (nbt == null)
        {
            if (tag == null)
            {
                return nbt;
            }

            nbt = new NBTTagCompound();
        }

        if (tag == null)
        {
            nbt.removeTag(name);
        }
        else
        {
            nbt.setTag(name, tag);
        }

        return nbt;
    }

    /**
     * Sets the root compound tag in the given ItemStack. An empty compound will be stripped completely.
     */
    public static ItemStack setRootCompoundTag(ItemStack stack, NBTTagCompound nbt)
    {
        if (nbt != null && nbt.hasNoTags() == true)
        {
            nbt = null;
        }

        stack.setTagCompound(nbt);
        return stack;
    }

    /**
     * Get the root compound tag from the ItemStack.
     * If one doesn't exist, then it will be created and added if <b>create</b> is true, otherwise null is returned.
     */
    public static NBTTagCompound getRootCompoundTag(ItemStack stack, boolean create)
    {
        NBTTagCompound nbt = stack.getTagCompound();

        if (create == false)
        {
            return nbt;
        }

        // create = true
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        return nbt;
    }

    /**
     * Get a compound tag by the given name <b>tagName</b> from the other compound tag <b>nbt</b>.
     * If one doesn't exist, then it will be created and added if <b>create</b> is true, otherwise null is returned.
     */
    public static NBTTagCompound getCompoundTag(NBTTagCompound nbt, String tagName, boolean create)
    {
        if (nbt == null)
        {
            return null;
        }

        if (create == false)
        {
            return nbt.hasKey(tagName, Constants.NBT.TAG_COMPOUND) == true ? nbt.getCompoundTag(tagName) : null;
        }

        // create = true

        if (nbt.hasKey(tagName, Constants.NBT.TAG_COMPOUND) == false)
        {
            nbt.setTag(tagName, new NBTTagCompound());
        }

        return nbt.getCompoundTag(tagName);
    }

    /**
     * Returns a compound tag by the given name <b>tagName</b>. If <b>tagName</b> is null,
     * then the root compound tag is returned instead. If <b>create</b> is <b>false</b>
     * and the tag doesn't exist, null is returned and the tag is not created.
     * If <b>create</b> is <b>true</b>, then the tag(s) are created and added if necessary.
     */
    public static NBTTagCompound getCompoundTag(ItemStack stack, String tagName, boolean create)
    {
        NBTTagCompound nbt = getRootCompoundTag(stack, create);

        if (tagName != null)
        {
            nbt = getCompoundTag(nbt, tagName, create);
        }

        return nbt;
    }

    /**
     * Get a nested compound tag by the name <b>tagName</b> from inside another compound tag <b>containerTagName</b>.
     * If some of the tags don't exist, then they will be created and added if <b>create</b> is true, otherwise null is returned.
     */
    public static NBTTagCompound getCompoundTag(ItemStack stack, String containerTagName, String tagName, boolean create)
    {
        NBTTagCompound nbt = getRootCompoundTag(stack, create);

        if (containerTagName != null)
        {
            nbt = getCompoundTag(nbt, containerTagName, create);
        }

        return getCompoundTag(nbt, tagName, create);
    }

    /**
     * Gets the stored UUID from the given ItemStack. If <b>containerTagName</b> is not null,
     * then the UUID is read from a compound tag by that name.
     * If <b>create</b> is true and a UUID isn't found, a new random UUID will be created and added.
     * If <b>create</b> is false and a UUID isn't found, then null is returned.
     */
    public static UUID getUUIDFromItemStack(ItemStack stack, String containerTagName, boolean create)
    {
        NBTTagCompound nbt = getCompoundTag(stack, containerTagName, create);

        UUID uuid = getUUIDFromNBT(nbt);
        if (uuid == null && create == true)
        {
            uuid = UUID.randomUUID();
            nbt.setLong("UUIDM", uuid.getMostSignificantBits());
            nbt.setLong("UUIDL", uuid.getLeastSignificantBits());
        }

        return uuid;
    }

    /**
     * Gets the stored UUID from the given compound tag. If one isn't found, null is returned.
     */
    public static UUID getUUIDFromNBT(NBTTagCompound nbt)
    {
        if (nbt != null && nbt.hasKey("UUIDM", Constants.NBT.TAG_LONG) && nbt.hasKey("UUIDL", Constants.NBT.TAG_LONG))
        {
            return new UUID(nbt.getLong("UUIDM"), nbt.getLong("UUIDL"));
        }

        return null;
    }

    /**
     * Stores the given UUID to the given ItemStack. If <b>containerTagName</b> is not null,
     * then the UUID is stored inside a compound tag by that name. Otherwise it is stored
     * directly inside the root compound tag.
     */
    public static void setUUID(ItemStack stack, UUID uuid, String containerTagName)
    {
        NBTTagCompound nbt = getCompoundTag(stack, containerTagName, true);

        nbt.setLong("UUIDM", uuid.getMostSignificantBits());
        nbt.setLong("UUIDL", uuid.getLeastSignificantBits());
    }
}
