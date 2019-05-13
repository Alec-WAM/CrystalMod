package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.items.EnumDarkIronItemVariant;
import alec_wam.CrystalMod.items.EnumPlateItem;
import alec_wam.CrystalMod.items.ItemCrystalShard;
import alec_wam.CrystalMod.items.ItemVariant;
import alec_wam.CrystalMod.items.ItemWrench;
import alec_wam.CrystalMod.tiles.crate.EnumCrateUpgrades;
import alec_wam.CrystalMod.tiles.pipes.EnumPipeUpgrades;
import alec_wam.CrystalMod.tiles.pipes.item.ItemPipeFilter;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CrystalMod.MODID)
public class ModItems {
	
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>> crystalGroup;
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemCrystalShard> crystalShardGroup;
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>> crystalIngotGroup;
	public static ItemVariantGroup<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>> crystalNuggetGroup;

	public static ItemVariantGroup<EnumDarkIronItemVariant, ItemVariant<EnumDarkIronItemVariant>> darkIronGroup;
	public static ItemVariantGroup<EnumPlateItem, ItemVariant<EnumPlateItem>> metalPlateGroup;
	
	public static Item wrench;
	public static Item pipeFilter;
	public static ItemVariantGroup<EnumPipeUpgrades, ItemVariant<EnumPipeUpgrades>> pipeUpgrades;
	public static ItemVariantGroup<EnumCrateUpgrades, ItemVariant<EnumCrateUpgrades>> crateUpgrades;

	public static void buildList(){
		crystalGroup = ItemVariantGroup.Builder.<EnumCrystalColorSpecial, ItemVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystal")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.itemFactory(ItemVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addItemGroup(crystalGroup);
		crystalShardGroup = ItemVariantGroup.Builder.<EnumCrystalColorSpecial, ItemCrystalShard>create()
				.groupName("crystalshard")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.itemFactory(ItemCrystalShard::new)
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
		darkIronGroup = ItemVariantGroup.Builder.<EnumDarkIronItemVariant, ItemVariant<EnumDarkIronItemVariant>>create()
				.groupName("darkiron")
				.suffix()
				.variants(EnumDarkIronItemVariant.values())
				.itemFactory(ItemVariant<EnumDarkIronItemVariant>::new)
				.build();
		RegistrationHandler.addItemGroup(darkIronGroup);
		metalPlateGroup = ItemVariantGroup.Builder.<EnumPlateItem, ItemVariant<EnumPlateItem>>create()
				.groupName("plate")
				.suffix()
				.variants(EnumPlateItem.values())
				.itemFactory(ItemVariant<EnumPlateItem>::new)
				.build();
		RegistrationHandler.addItemGroup(metalPlateGroup);
		
		wrench = new ItemWrench(new Item.Properties().group(ModItemGroups.ITEM_GROUP_ITEMS).maxStackSize(1));
		RegistrationHandler.addItem(wrench, "wrench");
		pipeFilter = new ItemPipeFilter(new Item.Properties().group(ModItemGroups.ITEM_GROUP_ITEMS).maxStackSize(16));
		RegistrationHandler.addItem(pipeFilter, "pipefilter");
		pipeUpgrades = ItemVariantGroup.Builder.<EnumPipeUpgrades, ItemVariant<EnumPipeUpgrades>>create()
				.groupName("pipe_upgrade")
				.suffix()
				.variants(EnumPipeUpgrades.values())
				.itemFactory(ItemVariant<EnumPipeUpgrades>::new)
				.build();
		RegistrationHandler.addItemGroup(pipeUpgrades);
		crateUpgrades = ItemVariantGroup.Builder.<EnumCrateUpgrades, ItemVariant<EnumCrateUpgrades>>create()
				.groupName("crate_upgrade")
				.suffix()
				.variants(EnumCrateUpgrades.values())
				.itemFactory(ItemVariant<EnumCrateUpgrades>::new)
				.build();
		RegistrationHandler.addItemGroup(crateUpgrades);
		
	}	
	
}
