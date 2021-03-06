package alec_wam.CrystalMod.util;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public final class ItemNBTHelper {
	public static final String MOST_SIG_UUID = "MostSigUUID";
    public static final String LEAST_SIG_UUID = "LeastSigUUID";


	// SETTERS ///////////////////////////////////////////////////////////////////
	public static NBTTagCompound getCompound(ItemStack stack){
		if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
		return stack.getTagCompound();
	}

	public static void resetNBT(ItemStack stack) {
		stack.setTagCompound(new NBTTagCompound());
	}

	public static ItemStack setByte(ItemStack stack, String tag, byte b)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setByte(tag, b);
		stack.setTagCompound(compound);
		return stack;
	}

	public static ItemStack setBoolean(ItemStack stack, String tag, boolean b)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setBoolean(tag, b);
		stack.setTagCompound(compound);
		return stack;
	}
	
	public static ItemStack setShort(ItemStack stack, String tag, short s)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setShort(tag, s);
		stack.setTagCompound(compound);
		return stack;
	}
	
	public static ItemStack setInteger(ItemStack stack, String tag, int i)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setInteger(tag, i);
		stack.setTagCompound(compound);
		return stack;
	}

	public static ItemStack setLong(ItemStack stack, String tag, long i)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setLong(tag, i);
		stack.setTagCompound(compound);
		return stack;
	}
	
	public static ItemStack setFloat(ItemStack stack, String tag, float f)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setFloat(tag, f);
		stack.setTagCompound(compound);
		return stack;
	}
	
	public static ItemStack setDouble(ItemStack stack, String tag, double d)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setDouble(tag, d);
		stack.setTagCompound(compound);
		return stack;
	}
	
	public static ItemStack setString(ItemStack stack, String tag, String s) {
		NBTTagCompound compound = getCompound(stack);
		compound.setString(tag, s);
		stack.setTagCompound(compound);
		return stack;
	}
	
	public static void updateUUID(ItemStack itemStack){
        if (!hasUUID(itemStack)){
            UUID itemUUID = UUID.randomUUID();

            setLong(itemStack, MOST_SIG_UUID, itemUUID.getMostSignificantBits());
            setLong(itemStack, LEAST_SIG_UUID, itemUUID.getLeastSignificantBits());
        }
    }

	// GETTERS ///////////////////////////////////////////////////////////////////

	public static boolean verifyExistance(ItemStack stack, String tag) {
		if(ItemStackTools.isNullStack(stack))return false;
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null)
			return false;
		else
			return stack.getTagCompound().hasKey(tag);
	}
	
	public static boolean hasUUID(ItemStack itemStack){
        return verifyExistance(itemStack, MOST_SIG_UUID) && verifyExistance(itemStack, LEAST_SIG_UUID);
    }

	public static byte getByte(ItemStack stack, String tag, byte defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getByte(tag) : defaultExpected;
	}

	public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getBoolean(tag) : defaultExpected;
	}
	
	public static short getShort(ItemStack stack, String tag, short defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getShort(tag) : defaultExpected;
	}
	
	public static int getInteger(ItemStack stack, String tag, int defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getInteger(tag) : defaultExpected;
	}

	public static long getLong(ItemStack stack, String tag, long defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getLong(tag) : defaultExpected;
	}

	public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getFloat(tag) : defaultExpected;
	}
	
	public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getDouble(tag) : defaultExpected;
	}
	
	public static String getString(ItemStack stack, String tag, String defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getString(tag) : defaultExpected;
	}
	
	public static UUID getUUID(ItemStack itemStack){
        if (hasUUID(itemStack)){
            return new UUID(itemStack.getTagCompound().getLong(MOST_SIG_UUID), itemStack.getTagCompound().getLong(LEAST_SIG_UUID));
        }
        return null;
    }
}
