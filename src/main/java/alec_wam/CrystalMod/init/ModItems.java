package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.plants.ItemCattail;
import alec_wam.CrystalMod.client.CustomItemRender;
import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.items.EnumDarkIronItemVariant;
import alec_wam.CrystalMod.items.EnumPlateItem;
import alec_wam.CrystalMod.items.ItemBucketXP;
import alec_wam.CrystalMod.items.ItemCrystalShard;
import alec_wam.CrystalMod.items.ItemElectricBread;
import alec_wam.CrystalMod.items.ItemGrassSeeds;
import alec_wam.CrystalMod.items.ItemGrassSeeds.EnumGrassSeedItem;
import alec_wam.CrystalMod.items.ItemMobSkull;
import alec_wam.CrystalMod.items.ItemPencil;
import alec_wam.CrystalMod.items.ItemVariant;
import alec_wam.CrystalMod.items.ItemWrench;
import alec_wam.CrystalMod.items.tools.ItemPoweredShield;
import alec_wam.CrystalMod.tiles.crate.EnumMiscUpgrades;
import alec_wam.CrystalMod.tiles.fusion.ItemFusionWand;
import alec_wam.CrystalMod.tiles.pipes.EnumPipeUpgrades;
import alec_wam.CrystalMod.tiles.pipes.item.ItemPipeFilter;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
	public static ItemVariantGroup<EnumCrystalColor, ItemVariant<EnumCrystalColor>> crystalBerryGroup;
	public static Item cattailFluff;
	public static Item cattailRootsRaw;
	public static Item cattailRootsCooked;

	public static Item wrench;
	public static Item machineFrame;
	public static Item fusionWand;
	public static Item pencil;
	public static Item itemFilter;
	public static ItemVariantGroup<EnumPipeUpgrades, ItemVariant<EnumPipeUpgrades>> pipeUpgrades;
	public static ItemVariantGroup<EnumMiscUpgrades, ItemVariant<EnumMiscUpgrades>> miscUpgrades;

	public static Item electricBread;
	public static Item poweredShield;
	
	public static Item xpBucket;
	
	public static ItemVariantGroup<ItemMobSkull.EnumSkullType, ItemMobSkull> mobHeads;	
	
	
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
		//Apple Food Value
		crystalBerryGroup = ItemVariantGroup.Builder.<EnumCrystalColor, ItemVariant<EnumCrystalColor>>create()
				.groupName("crystalberry")
				.suffix()
				.variants(EnumCrystalColor.values())
				.itemFactory(ItemVariant<EnumCrystalColor>::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_ITEMS).food(Foods.APPLE))
				.build();
		RegistrationHandler.addItemGroup(crystalBerryGroup);
		cattailFluff = new ItemCattail(new Item.Properties().group(ItemGroup.MATERIALS));
		RegistrationHandler.addItem(cattailFluff, "cattail_fluff");
		//Raw Fish food value (1)
		cattailRootsRaw = new Item(new Item.Properties().group(ItemGroup.DECORATIONS).food(Foods.COD));
		RegistrationHandler.addItem(cattailRootsRaw, "cattail_roots_raw");
		//Carrot food value (3)
		cattailRootsCooked = new Item(new Item.Properties().group(ItemGroup.FOOD).food(Foods.CARROT));
		RegistrationHandler.addItem(cattailRootsCooked, "cattail_roots_cooked");
		
		wrench = new ItemWrench(new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES).maxStackSize(1));
		RegistrationHandler.addItem(wrench, "wrench");
		machineFrame = new Item(new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES));
		RegistrationHandler.addItem(machineFrame, "machineframe");
		fusionWand = new ItemFusionWand(new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES).maxStackSize(1));
		RegistrationHandler.addItem(fusionWand, "fusionwand");
		pencil = new ItemPencil(new Item.Properties().group(ModItemGroups.ITEM_GROUP_ITEMS).maxStackSize(1).maxDamage(16));
		RegistrationHandler.addItem(pencil, "pencil");
		itemFilter = new ItemPipeFilter(new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES).maxStackSize(16));
		RegistrationHandler.addItem(itemFilter, "itemfilter");
		pipeUpgrades = ItemVariantGroup.Builder.<EnumPipeUpgrades, ItemVariant<EnumPipeUpgrades>>create()
				.groupName("pipe_upgrade")
				.suffix()
				.variants(EnumPipeUpgrades.values())
				.itemFactory(ItemVariant<EnumPipeUpgrades>::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES))
				.build();
		RegistrationHandler.addItemGroup(pipeUpgrades);
		miscUpgrades = ItemVariantGroup.Builder.<EnumMiscUpgrades, ItemVariant<EnumMiscUpgrades>>create()
				.groupName("misc_upgrade")
				.suffix()
				.variants(EnumMiscUpgrades.values())
				.itemFactory(ItemVariant<EnumMiscUpgrades>::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES))
				.build();
		RegistrationHandler.addItemGroup(miscUpgrades);
		
		//Bread Food Property
		electricBread = new ItemElectricBread(new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(Foods.BREAD));
		RegistrationHandler.addItem(electricBread, "electric_bread");	
		
		poweredShield = new ItemPoweredShield(new Item.Properties().group(ItemGroup.COMBAT).maxStackSize(1).setTEISR(() -> CustomItemRender::new));
		RegistrationHandler.addItem(poweredShield, "powered_shield");	
		
		xpBucket = new ItemBucketXP(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
		RegistrationHandler.addItem(xpBucket, "bucket_xp");
		
		mobHeads = ItemVariantGroup.Builder.<ItemMobSkull.EnumSkullType, ItemMobSkull>create()
				.groupName("skull")
				.suffix()
				.variants(ItemMobSkull.EnumSkullType.values())
				.itemFactory(ItemMobSkull::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ItemGroup.COMBAT).setTEISR(() -> CustomItemRender::new))
				.build();
		RegistrationHandler.addItemGroup(mobHeads);
	}	
	
}
