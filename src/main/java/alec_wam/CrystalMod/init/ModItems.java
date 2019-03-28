package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.items.ItemVariant;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CrystalMod.MODID)
public class ModItems {
	
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>> crystalGroup;
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>> crystalShardGroup;
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>> crystalIngotGroup;
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>> crystalNuggetGroup;

	public static void buildList(){
		crystalGroup = ItemVariantGroup.Builder.<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystal")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.itemFactory(ItemVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addItemGroup(crystalGroup);
		crystalShardGroup = ItemVariantGroup.Builder.<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystalshard")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.itemFactory(ItemVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addItemGroup(crystalShardGroup);
		crystalIngotGroup = ItemVariantGroup.Builder.<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystalingot")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.itemFactory(ItemVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addItemGroup(crystalIngotGroup);
		crystalNuggetGroup = ItemVariantGroup.Builder.<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystalnugget")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.itemFactory(ItemVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addItemGroup(crystalNuggetGroup);
	}	
	
}
