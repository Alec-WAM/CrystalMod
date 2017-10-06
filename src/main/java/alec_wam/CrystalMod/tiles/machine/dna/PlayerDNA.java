package alec_wam.CrystalMod.tiles.machine.dna;

import java.util.UUID;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class PlayerDNA {

	public static final String NBT_PLAYERDNA = "PlayerDNA";
	
	public static void savePlayerDNA(ItemStack stack, UUID uuid){
		savePlayerDNA(ItemNBTHelper.getCompound(stack), uuid, NBT_PLAYERDNA);
	}
	
	public static void savePlayerDNA(NBTTagCompound nbt, UUID uuid, String tagName){
		NBTTagCompound uuidNBT = NBTUtil.createUUIDTag(uuid);
		nbt.setTag(tagName, uuidNBT);
	}
	
	public static UUID loadPlayerDNA(ItemStack stack){
		if(!stack.hasTagCompound()) return null;
		return loadPlayerDNA(ItemNBTHelper.getCompound(stack), NBT_PLAYERDNA);
	}
	
	public static UUID loadPlayerDNA(NBTTagCompound nbt, String tagName){
		return NBTUtil.getUUIDFromTag(nbt.getCompoundTag(tagName));
	}
}
