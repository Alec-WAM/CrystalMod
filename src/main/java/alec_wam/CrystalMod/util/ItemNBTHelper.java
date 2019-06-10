package alec_wam.CrystalMod.util;

import java.util.UUID;

import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;


public final class ItemNBTHelper {
	// SETTERS ///////////////////////////////////////////////////////////////////
	public static CompoundNBT getCompound(ItemStack stack){
		if (!stack.hasTag()) stack.setTag(new CompoundNBT());
		return stack.getTag();
	}

	public static void resetNBT(ItemStack stack) {
		stack.setTag(new CompoundNBT());
	}

	public static ItemStack putByte(ItemStack stack, String tag, byte b)
	{
		CompoundNBT compound = getCompound(stack);
		compound.putByte(tag, b);
		stack.setTag(compound);
		return stack;
	}

	public static ItemStack putBoolean(ItemStack stack, String tag, boolean b)
	{
		CompoundNBT compound = getCompound(stack);
		compound.putBoolean(tag, b);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack putShort(ItemStack stack, String tag, short s)
	{
		CompoundNBT compound = getCompound(stack);
		compound.putShort(tag, s);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack putInteger(ItemStack stack, String tag, int i)
	{
		CompoundNBT compound = getCompound(stack);
		compound.putInt(tag, i);
		stack.setTag(compound);
		return stack;
	}

	public static ItemStack putLong(ItemStack stack, String tag, long i)
	{
		CompoundNBT compound = getCompound(stack);
		compound.putLong(tag, i);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack putFloat(ItemStack stack, String tag, float f)
	{
		CompoundNBT compound = getCompound(stack);
		compound.putFloat(tag, f);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack putDouble(ItemStack stack, String tag, double d)
	{
		CompoundNBT compound = getCompound(stack);
		compound.putDouble(tag, d);
		stack.setTag(compound);
		return stack;
	}
	
	public static ItemStack putString(ItemStack stack, String tag, String s) {
		CompoundNBT compound = getCompound(stack);
		compound.putString(tag, s);
		stack.setTag(compound);
		return stack;
	}

	public static ItemStack putUUID(ItemStack stack, String tag, UUID u){
		CompoundNBT compound = getCompound(stack);
		compound.putString(tag, UUIDTypeAdapter.fromUUID(u));
		stack.setTag(compound);
		return stack;
	}
	
	// GETTERS ///////////////////////////////////////////////////////////////////

	public static boolean verifyExistance(ItemStack stack, String tag) {
		if(ItemStackTools.isNullStack(stack))return false;
		CompoundNBT compound = stack.getTag();
		if (compound == null)
			return false;
		else
			return stack.getTag().contains(tag);
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
