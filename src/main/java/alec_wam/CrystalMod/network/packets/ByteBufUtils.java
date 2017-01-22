package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;

public class ByteBufUtils
{
    public static void writeItemStackToBuffer(ByteBuf buf, ItemStack stack)
    {
        if (stack == null)
        {
            buf.writeShort(-1);
            return;
        }

        buf.writeShort(Item.getIdFromItem(stack.getItem()));
        buf.writeShort(stack.getItemDamage());
        buf.writeInt(ItemStackTools.getStackSize(stack));

        NBTTagCompound tag = null;
        if (stack.getItem().isDamageable() == true || stack.getItem().getShareTag() == true)
        {
            tag = stack.getTagCompound();
        }

        writeNBTTagCompoundToBuffer(buf, tag);
    }

    public static ItemStack readItemStackFromBuffer(ByteBuf buf) throws IOException
    {
        ItemStack stack = null;
        short id = buf.readShort();

        if (id >= 0)
        {
            short meta = buf.readShort();
            int stackSize = buf.readInt();
            stack = new ItemStack(Item.getItemById(id), stackSize, meta);
            stack.setTagCompound(readNBTTagCompoundFromBuffer(buf));
        }

        return stack;
    }

    public static void writeNBTTagCompoundToBuffer(ByteBuf buf, NBTTagCompound tag)
    {
        if (tag == null)
        {
            buf.writeByte(0);
            return;
        }

        try
        {
            CompressedStreamTools.write(tag, new ByteBufOutputStream(buf));
        }
        catch (IOException ioexception)
        {
            ModLogger.error("IOException while trying to write a NBTTagCompound to ByteBuf");
            throw new EncoderException(ioexception);
        }
    }

    public static NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf buf) throws IOException
    {
        int i = buf.readerIndex();
        byte b0 = buf.readByte();

        if (b0 == 0)
        {
            return null;
        }
        else
        {
            buf.readerIndex(i);
            return CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
        }
    }
}
