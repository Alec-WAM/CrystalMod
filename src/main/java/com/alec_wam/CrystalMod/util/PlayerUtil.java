package com.alec_wam.CrystalMod.util;

import java.util.UUID;

import com.alec_wam.CrystalMod.CrystalMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayerUtil {

	public static final UUID Alec_WAM = UUIDUtils.fromString("6b89d7a120c445f39c31218e91101a51");
	public static final UUID AH9902 = UUIDUtils.fromString("4cc1974e7cf247d19562198ce2d9bb91");
	public static final UUID Kilowag1453 = UUIDUtils.fromString("668afe63291d479d9d854f32979eec40");
	public static final UUID long_shot99 = UUIDUtils.fromString("0c5e14dc9e884fa68eb78112e99ac67d");
	
	public static boolean isPlayerDev(EntityPlayer player){
		
		if(player == null)return false;
		String uuid = getUUID(player);
		if(UUIDUtils.fromUUID(Alec_WAM).equals(uuid) || UUIDUtils.fromUUID(AH9902).equals(uuid) || UUIDUtils.fromUUID(Kilowag1453).equals(uuid) || UUIDUtils.fromUUID(long_shot99).equals(uuid))return true;
		return false;
	}
	
	public static UUID getPlayerUUID(EntityPlayer player){
		return player.getGameProfile().getId();
	}
	
	public static String getUUID(EntityPlayer player){
		return UUIDUtils.fromUUID(getPlayerUUID(player));
	}
	
	public static void teleportPlayerToDimension(EntityPlayerMP player, int dimension)
	{
		boolean comingFromEnd = player.dimension == 1;
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		server.getPlayerList().transferPlayerToDimension(player, dimension, new SimpleTeleporter(server.worldServerForDimension(dimension)));

		// If a player is teleported from the end certain logic elements are ignored in transferPlayerToDimension
		if (comingFromEnd)
		{
			double d0 = (double) MathHelper.clamp_int((int) player.posX, -29999872, 29999872);
			double d1 = (double) MathHelper.clamp_int((int) player.posZ, -29999872, 29999872);

			if (player.isEntityAlive())
			{
				player.setLocationAndAngles(d0, player.posY, d1, player.rotationYaw, player.rotationPitch);
				player.worldObj.spawnEntityInWorld(player);
				player.worldObj.updateEntityWithOptionalForce(player, false);
			}
		}

		player.removeExperienceLevel(0);
		player.setPlayerHealthUpdated();
	}

	public static ItemStack createPlayerHead(EntityPlayer player){
 	    ItemStack skull = new ItemStack(Items.SKULL, 1, 3);
 	    if(player == null || player instanceof FakePlayer)return skull;
        skull.setTagCompound(new NBTTagCompound());
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTUtil.writeGameProfile(nbttagcompound, player.getGameProfile());
        skull.getTagCompound().setTag("SkullOwner", nbttagcompound);
        return skull;
    }

	public static String getName(String string){
		if(UUIDUtils.isUUID(string)){
			return ProfileUtil.getUsername(UUIDUtils.fromString(string)); 
		}
		return string;
	}

	public static NBTTagCompound getPersistantNBT(EntityPlayer entity)
	{
		return getPersistTag(entity, CrystalMod.MODID);
	}
	
	public static NBTTagCompound getPersistTag(EntityPlayer player, String modName) {

		if(player == null)return null;
		
		NBTTagCompound tag = player.getEntityData();

		NBTTagCompound persistTag = null;
		if (tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			persistTag = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		} else {
			persistTag = new NBTTagCompound();
			tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistTag);
		}

		NBTTagCompound modTag = null;
		if (persistTag.hasKey(modName)) {
			modTag = persistTag.getCompoundTag(modName);
		} else {
			modTag = new NBTTagCompound();
			persistTag.setTag(modName, modTag);
		}

		return modTag;
	}
	
}
