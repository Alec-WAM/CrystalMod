package alec_wam.CrystalMod.entities.accessories;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.IWolfArmor;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class WolfAccessories {

	public static String NBT_ACCESSORY_WOLF_ARMOR = "WolfArmor";
	public static final UUID UUID_ARMOR_ATTRIBUTE = UUID.nameUUIDFromBytes((CrystalMod.MODID.toLowerCase()+".wolfarmor").getBytes());
	
	public static void setWolfArmor(EntityWolf wolf, ItemStack armor){
		NBTTagCompound nbt = EntityUtil.getCustomEntityData(wolf);
    	nbt.setTag(NBT_ACCESSORY_WOLF_ARMOR, ItemStackTools.isEmpty(armor) ? new NBTTagCompound() : armor.serializeNBT());
    	EntityUtil.setCustomEntityData(wolf, nbt);
    	syncArmor(wolf, true);
	}
	
	public static ItemStack getWolfArmorStack(EntityWolf wolf){
		NBTTagCompound nbt = EntityUtil.getCustomEntityData(wolf);
		if(!nbt.hasKey(NBT_ACCESSORY_WOLF_ARMOR))return ItemStackTools.getEmptyStack();
    	return ItemStackTools.loadFromNBT(nbt.getCompoundTag(NBT_ACCESSORY_WOLF_ARMOR));
	}
	
	public static WolfArmor getWolfArmor(ItemStack stack){
		if(ItemStackTools.isValid(stack) && stack.getItem() instanceof IWolfArmor){
			return ((IWolfArmor)stack.getItem()).getWolfArmor(stack);
		}
		return WolfArmor.NONE;
	}

	public static void syncArmor(EntityWolf entity, boolean sendPacket) {
		if (!entity.getEntityWorld().isRemote)
        {
			entity.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(UUID_ARMOR_ATTRIBUTE);
			ItemStack wolfArmor = getWolfArmorStack(entity);
			WolfArmor armor = getWolfArmor(wolfArmor);
            int i = armor.getProtection();

            if (i != 0)
            {
            	entity.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(UUID_ARMOR_ATTRIBUTE, "Wolf armor bonus", i, 0)).setSaved(false));
            }
            if(sendPacket){
            	EntityUtil.sendSyncPacket(entity);
            }
        }
	}


	public static void onEntityLoad(Entity entity) {
		if(entity instanceof EntityWolf){
			syncArmor((EntityWolf)entity, false);
		}
	}

	public static boolean handleWolfInteract(EntityPlayer player, ItemStack held, EnumHand hand, EntityWolf entity) {
		if(ItemStackTools.isValid(held) && !entity.getEntityWorld().isRemote){
			if(held.getItem() instanceof IWolfArmor){
				ItemStack lastStack = WolfAccessories.getWolfArmorStack(entity);
				if(ItemStackTools.isValid(lastStack)){
					entity.entityDropItem(lastStack, 0);
				}
				ItemStack copy = ItemStackTools.safeCopy(held);
				if(ItemStackTools.isValid(copy)){
					setWolfArmor(entity, copy);
					if(!player.capabilities.isCreativeMode)player.setHeldItem(hand, ItemUtil.consumeItem(held));
					return true;
				}
			}
		}
		return false;
	}
	
	public static enum WolfArmor {
		NONE(0), LEATHER(7), CHAIN(12), IRON(15), DIRON(17), DIAMOND(20, 2.0F), GOLD(11);
		
		public final int damageReduction;
		//Currently Unused
		public final float toughness;
		
		WolfArmor(int damageReduction){
			this(damageReduction, 0.0F);
		}
		WolfArmor(int damageReduction, float toughness){
			this.damageReduction = damageReduction;
			this.toughness = toughness;
		}
		
		public int getProtection(){
			return damageReduction;
		}
		
		public double getToughness(){
			return toughness;
		}
		
		@Nonnull
		public static WolfArmor getByName(@Nullable String name){
			if(Strings.isNullOrEmpty(name)) return NONE;
			for(WolfArmor armor : values()){
				if(armor.name().toLowerCase().equals(name)){
					return armor;
				}
			}
			return NONE;
		}
	}
}
