package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.items.EnumDarkIronItemVariant;
import alec_wam.CrystalMod.items.EnumPlateItem;
import alec_wam.CrystalMod.items.ItemCrystalShard;
import alec_wam.CrystalMod.items.ItemGrassSeeds;
import alec_wam.CrystalMod.items.ItemGrassSeeds.EnumGrassSeedItem;
import alec_wam.CrystalMod.items.ItemVariant;
import alec_wam.CrystalMod.items.ItemWrench;
import alec_wam.CrystalMod.tiles.crate.EnumMiscUpgrades;
import alec_wam.CrystalMod.tiles.fusion.ItemFusionWand;
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
	
	public static ItemVariantGroup<EnumGrassSeedItem, ItemGrassSeeds> grassSeeds;
	
	public static Item wrench;
	public static Item fusionWand;
	public static Item pipeFilter;
	public static ItemVariantGroup<EnumPipeUpgrades, ItemVariant<EnumPipeUpgrades>> pipeUpgrades;
	public static ItemVariantGroup<EnumMiscUpgrades, ItemVariant<EnumMiscUpgrades>> miscUpgrades;

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
		
		
		grassSeeds = ItemVariantGroup.Builder.<EnumGrassSeedItem, ItemGrassSeeds>create()
				.groupName("grass_seed")
				.suffix()
				.variants(EnumGrassSeedItem.values())
				.itemFactory(ItemGrassSeeds::new)
				.build();
		RegistrationHandler.addItemGroup(grassSeeds);
		
		
		wrench = new ItemWrench(new Item.Properties().group(ModItemGroups.ITEM_GROUP_ITEMS).maxStackSize(1));
		RegistrationHandler.addItem(wrench, "wrench");
		fusionWand = new ItemFusionWand(new Item.Properties().group(ModItemGroups.ITEM_GROUP_ITEMS).maxStackSize(1));
		RegistrationHandler.addItem(fusionWand, "fusionwand");
		pipeFilter = new ItemPipeFilter(new Item.Properties().group(ModItemGroups.ITEM_GROUP_ITEMS).maxStackSize(16));
		RegistrationHandler.addItem(pipeFilter, "pipefilter");
		pipeUpgrades = ItemVariantGroup.Builder.<EnumPipeUpgrades, ItemVariant<EnumPipeUpgrades>>create()
				.groupName("pipe_upgrade")
				.suffix()
				.variants(EnumPipeUpgrades.values())
				.itemFactory(ItemVariant<EnumPipeUpgrades>::new)
				.build();
		RegistrationHandler.addItemGroup(pipeUpgrades);
		miscUpgrades = ItemVariantGroup.Builder.<EnumMiscUpgrades, ItemVariant<EnumMiscUpgrades>>create()
				.groupName("misc_upgrade")
				.suffix()
				.variants(EnumMiscUpgrades.values())
				.itemFactory(ItemVariant<EnumMiscUpgrades>::new)
				.build();
		RegistrationHandler.addItemGroup(miscUpgrades);
		
	}	
	
}
