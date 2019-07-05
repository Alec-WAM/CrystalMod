package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public class ItemMobSkull extends ItemVariant<ItemMobSkull.EnumSkullType> {

	public static enum EnumSkullType implements IStringSerializable {
		ENDERMAN, PHANTOM, GUARDIAN, HORSE;

		EnumSkullType() {}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

	}
	public static final HorseType[] BASIC_HORSE_TYPES = {HorseType.WHITE, HorseType.CREAMY, HorseType.CHESTNUT, HorseType.BROWN, HorseType.BLACK, HorseType.GRAY, HorseType.DARKBROWN};
	public static final String NBT_HORSE_TYPE = "HorseType";
	public static enum HorseType implements IStringSerializable {
		WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARKBROWN, ZOMBIE(true), SKELETON(true), DONKEY(true), MULE(true);

		public final boolean hasName;
		HorseType() {
			this(false);
		}
		HorseType(boolean hasName) {
			this.hasName = hasName;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
		public static HorseType getFromString(String str){
			for(HorseType type : values()){
				if(type.getName().equalsIgnoreCase(str)){
					return type;
				}
			}
			return null;
		}
		
		public static HorseType getFromHorse(AbstractHorseEntity entity){
			if(entity instanceof DonkeyEntity){
				return DONKEY;
			}
			if(entity instanceof MuleEntity){
				return MULE;
			}
			if(entity instanceof ZombieHorseEntity){
				return ZOMBIE;
			}
			if(entity instanceof SkeletonHorseEntity){
				return SKELETON;
			}
			if(entity instanceof HorseEntity){
				HorseEntity horse = (HorseEntity)entity;
				int i = horse.getHorseVariant();
				int type = (i & 255) % 7;
				return BASIC_HORSE_TYPES[type];
			}
			return HorseType.BROWN;
		}

	}
	
	public final EnumSkullType type;
	public ItemMobSkull(EnumSkullType type,	ItemVariantGroup<? extends Enum<EnumSkullType>, ItemMobSkull> variantGroup,	Properties properties) {
		super(type, variantGroup, properties);
		this.type = type;
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		if(type == EnumSkullType.HORSE){
			HorseType type = loadHorseType(stack);
			if(type !=null && type.hasName){
				return super.getTranslationKey(stack) + "." + type.getName();
			}
		}
		return super.getTranslationKey(stack);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(type == EnumSkullType.HORSE){
			if (this.isInGroup(group)) {
				for(HorseType type : HorseType.values()){
					ItemStack stack = new ItemStack(this);
					setHorseType(stack, type);
					items.add(stack);					
				}
			}
		} else {
			super.fillItemGroup(group, items);
		}
	}
	
	public static void setHorseType(ItemStack stack, HorseType type){
		ItemNBTHelper.putString(stack, NBT_HORSE_TYPE, type.getName());
	}
	
	public static HorseType loadHorseType(ItemStack stack){
		String type = ItemNBTHelper.getString(stack, NBT_HORSE_TYPE, "");
		if(!type.isEmpty()){
			return HorseType.getFromString(type);
		}
		return null;
	}
	
}
