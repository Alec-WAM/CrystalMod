package alec_wam.CrystalMod.util;

import java.util.UUID;

import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public final class ItemNBTHelper {
	// SETTERS ///////////////////////////////////////////////////////////////////
	public static NBTTagCompound getCompound(ItemStack stack){
		if (!stack.hasTag()) stack.setTag(new NBTTagCompound());
		return stack.getTag();
	}

	public static void resetNBT(ItemStack stack) {
		stack.setTag(new NBTTagCompound());
	}

	public static ItemStack setByte(ItemStack stack, String tag, byte b)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setByte(tag, b);
		stack.setTag(compound);
		return stack;
	}

	public static ItemStack setBoolean(ItemStack stack, String tag, boolean b)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setBoolean(tag, b);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack setShort(ItemStack stack, String tag, short s)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setShort(tag, s);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack setInteger(ItemStack stack, String tag, int i)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setInt(tag, i);
		stack.setTag(compound);
		return stack;
	}

	public static ItemStack setLong(ItemStack stack, String tag, long i)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setLong(tag, i);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack setFloat(ItemStack stack, String tag, float f)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setFloat(tag, f);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack setDouble(ItemStack stack, String tag, double d)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setDouble(tag, d);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack setString(ItemStack stack, String tag, String s) {
		NBTTagCompound compound = getCompound(stack);
		compound.setString(tag, s);
		stack.setTag(compound);
		return stack;
	}

	public static ItemStack setUUID(ItemStack stack, String tag, UUID u){
		NBTTagCompound compound = getCompound(stack);
		compound.setString(tag, UUIDTypeAdapter.fromUUID(u));
		stack.setTag(compound);
		return stack;
	}
	
	// GETTERS ///////////////////////////////////////////////////////////////////

	public static boolean verifyExistance(ItemStack stack, String tag) {
		if(ItemStackTools.isNullStack(stack))return false;
		NBTTagCompound compound = stack.getTag();
		if (compound == null)
			return false;
		else
			return stack.getTag().hasKey(tag);
	}

	public static byte getByte(ItemStack stack, String tag, byte defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getByte(tag) : defaultExpected;
	}

	public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getBoolean(tag) : defaultExpected;
	}
	
	public static short getShort(ItemStack stack, String tag, short defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getShort(tag) : defaultExpected;
	}
	
	public static int getInteger(ItemStack stack, String tag, int defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getInt(tag) : defaultExpected;
	}

	public static long getLong(ItemStack stack, String tag, long defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getLong(tag) : defaultExpected;
	}

	public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getFloat(tag) : defaultExpected;
	}
	
	public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getDouble(tag) : defaultExpected;
	}
	
	public static String getString(ItemStack stack, String tag, String defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTag().getString(tag) : defaultExpected;
	}
	
	public static UUID getUUID(ItemStack stack, String tag, UUID defaultExpected) {
		return verifyExistance(stack, tag) ? UUIDTypeAdapter.fromString(stack.getTag().getString(tag)) : defaultExpected;
	}
}
