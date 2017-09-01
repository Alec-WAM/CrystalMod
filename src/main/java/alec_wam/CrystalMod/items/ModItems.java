package alec_wam.CrystalMod.items;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.ItemCorn;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.blocks.crops.material.ItemMaterialSeed;
import alec_wam.CrystalMod.entities.disguise.ItemDisguise;
import alec_wam.CrystalMod.entities.minecarts.chests.ItemCrystalChestMinecart;
import alec_wam.CrystalMod.entities.minecarts.chests.ItemEnderChestMinecart;
import alec_wam.CrystalMod.entities.minecarts.chests.wireless.ItemWirelessChestMinecart;
import alec_wam.CrystalMod.entities.minions.ItemMinion;
import alec_wam.CrystalMod.entities.minions.ItemMinionStaff;
import alec_wam.CrystalMod.entities.pet.bombomb.ItemBombomb;
import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.integration.baubles.ItemBaubleWings;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.armor.ItemCrystalArmor;
import alec_wam.CrystalMod.items.armor.ItemCustomArmor;
import alec_wam.CrystalMod.items.armor.ItemWolfArmor;
import alec_wam.CrystalMod.items.game.ItemFlag;
import alec_wam.CrystalMod.items.guide.ItemCrystalGuide;
import alec_wam.CrystalMod.items.tools.ItemCrystalAxe;
import alec_wam.CrystalMod.items.tools.ItemCrystalHoe;
import alec_wam.CrystalMod.items.tools.ItemCrystalPickaxe;
import alec_wam.CrystalMod.items.tools.ItemCrystalShears;
import alec_wam.CrystalMod.items.tools.ItemCrystalShovel;
import alec_wam.CrystalMod.items.tools.ItemCrystalSword;
import alec_wam.CrystalMod.items.tools.ItemCustomAxe;
import alec_wam.CrystalMod.items.tools.ItemCustomPickaxe;
import alec_wam.CrystalMod.items.tools.ItemDarkIronBow;
import alec_wam.CrystalMod.items.tools.ItemEnhancementKnowledge;
import alec_wam.CrystalMod.items.tools.ItemLock;
import alec_wam.CrystalMod.items.tools.ItemMegaCrystalAxe;
import alec_wam.CrystalMod.items.tools.ItemMegaCrystalPickaxe;
import alec_wam.CrystalMod.items.tools.ItemMegaCrystalShovel;
import alec_wam.CrystalMod.items.tools.ItemSuperTorch;
import alec_wam.CrystalMod.items.tools.ItemToolParts;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackCrafting;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackWireless;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade;
import alec_wam.CrystalMod.items.tools.bat.ItemBat;
import alec_wam.CrystalMod.items.tools.projectiles.ItemDagger;
import alec_wam.CrystalMod.items.tools.projectiles.ItemDarkarang;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.ItemCongealedRedstone;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.ItemReactorUpgrade;
import alec_wam.CrystalMod.tiles.machine.specialengines.ItemEngineCore;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemWorksiteUpgrade;
import alec_wam.CrystalMod.tiles.pipes.attachments.ItemPipeAttachment;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ItemPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.ItemWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.ItemSecurityCard;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter;
import alec_wam.CrystalMod.tiles.spawner.ItemEmptyMobEssence;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

	public static final Map<String, Item> REGISTRY = Maps.newHashMap();
	
	public static ItemCrystal crystals;
	public static ItemIngot ingots;
	public static ItemMetalPlate plates;
	public static ItemToolParts toolParts;
	public static ItemMachineFrame machineFrame;
	public static ItemCursedBone cursedBone;
	public static ItemCrystalSap crystalSap;
	public static ItemCrystalBerry crystalBerry;
	public static ItemGlowBerry glowBerry;
	public static ItemCongealedRedstone congealedRedstone;
	
	public static ItemCrystex crystexItems;
	
	public static ToolMaterial ToolMaterialCrystal = EnumHelper.addToolMaterial(CrystalMod.MODID.toLowerCase()+".crystal", 5, 2000, 20.0F, 6.0F, 22);
	public static ToolMaterial ToolMaterialDarkIron = EnumHelper.addToolMaterial(CrystalMod.MODID.toLowerCase()+".darkIron", 2, 500/*250*/, 7.0F/*6.0F*/, 2.5F/*2.0F*/, 12/*14*/);

	public static ArmorMaterial ArmorMaterialDarkIron = EnumHelper.addArmorMaterial(CrystalMod.MODID.toLowerCase()+".darkIron", "darkiron", 16, new int[]{2, 6, 7, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
	public static ArmorMaterial ArmorMaterialCrystal = EnumHelper.addArmorMaterial(CrystalMod.MODID.toLowerCase()+".crystal", "crystal", 35, new int[]{3, 6, 8, 3}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 4.0F);

	public static ItemCrystalAxe crystalAxe;
	public static ItemMegaCrystalAxe megaCrystalAxe;
	public static ItemCrystalHoe crystalHoe;
	public static ItemCrystalShovel crystalShovel;
	public static ItemMegaCrystalShovel megaCrystalShovel;
	public static ItemCrystalPickaxe crystalPickaxe;
	public static ItemMegaCrystalPickaxe megaCrystalPickaxe;
	public static ItemCrystalSword crystalSword;
	public static ItemCrystalShears shears;
	
	public static ItemArmor darkIronHelmet, darkIronChestplate, darkIronLeggings, darkIronBoots;
	public static ItemArmor crystalHelmet, crystalChestplate, crystalLeggings, crystalBoots;
	public static ItemWolfArmor wolfArmor;
	
	public static ItemAxe darkIronAxe;
	public static ItemHoe darkIronHoe;
	public static ItemSpade darkIronShovel;
	public static ItemPickaxe darkIronPickaxe;
	public static ItemSword darkIronSword;
	public static ItemDarkIronBow darkIronBow;
	public static ItemBat bat;
	
	public static ItemCrystalChestMinecart chestMinecart;
	public static ItemEnderChestMinecart enderChestMinecart;
	public static ItemWirelessChestMinecart wirelessChestMinecart;
	
	public static ItemCrystalWrench wrench;
	public static ItemCrystalGuide guide;
	public static ItemEnhancementKnowledge enhancementKnowledge;
	
	public static ItemCrystalSeeds crystalSeedsBlue, crystalSeedsRed, crystalSeedsGreen, crystalSeedsDark;
	public static ItemCrystalSeedTree crystalTreeSeedsBlue, crystalTreeSeedsRed, crystalTreeSeedsGreen, crystalTreeSeedsDark;
	public static Item crystalReedsBlue, crystalReedsRed, crystalReedsGreen, crystalReedsDark;
	public static ItemMaterialSeed materialSeed;
	public static ItemCorn corn;
	
	public static ItemPipeCover pipeCover;
	public static ItemPipeFilter pipeFilter;
	
	public static ItemHDD harddrive;
	public static ItemPattern craftingPattern; 
	public static ItemSecurityCard securityCard;
	public static ItemWirelessPanel wirelessPanel; 
	public static ItemPipeAttachment pipeAttachmant;
	
	public static ItemBackpackNormal normalBackpack;
	public static ItemBackpackBase wirelessBackpack;
	public static ItemBackpackBase craftingBackpack;
	public static ItemBackpackUpgrade backpackupgrade;
	public static ItemLock lock;
	
	public static ItemMiscCard miscCard;
	public static ItemTeloportTool telePearl;
	public static ItemSuperTorch superTorch;
	public static ItemDisguise disguise;
	public static ItemReactorUpgrade reactorUpgrade;
	public static ItemEngineCore engineCore;
	
	public static ItemFlag flag;
	public static ItemDragonWings wings;
	public static ItemMobEssence mobEssence;
	public static ItemEmptyMobEssence emptyMobEssence;
	public static ItemMinion minion;
	public static ItemMinionStaff minionStaff;
	public static ItemWorksiteUpgrade worksiteUpgrade;
	public static ItemBombomb bombomb;
	public static ItemDarkarang darkarang;
	public static ItemDagger dagger;
	
	//Baubles
	public static ItemBaubleWings dragonWingsBauble;
	
	public static void init() {
		crystals = new ItemCrystal();
		ingots = new ItemIngot();
		plates = new ItemMetalPlate();
		crystalReedsBlue = registerItem(new ItemBlockSpecial(ModBlocks.crystalReedsBlue).setCreativeTab(CrystalMod.tabCrops), "crystalreedsblue");
		crystalReedsRed = registerItem(new ItemBlockSpecial(ModBlocks.crystalReedsRed).setCreativeTab(CrystalMod.tabCrops), "crystalreedsred");
		crystalReedsGreen = registerItem(new ItemBlockSpecial(ModBlocks.crystalReedsGreen).setCreativeTab(CrystalMod.tabCrops), "crystalreedsgreen");
		crystalReedsDark = registerItem(new ItemBlockSpecial(ModBlocks.crystalReedsDark).setCreativeTab(CrystalMod.tabCrops), "crystalreedsdark");
		toolParts = new ItemToolParts();
		machineFrame = new ItemMachineFrame();
		cursedBone = new ItemCursedBone();
		crystalSap = new ItemCrystalSap();
		crystalBerry = new ItemCrystalBerry();
		glowBerry = new ItemGlowBerry();
		congealedRedstone = new ItemCongealedRedstone();
		
		crystexItems = new ItemCrystex();

		crystalAxe = new ItemCrystalAxe(ToolMaterialCrystal);
		megaCrystalAxe = new ItemMegaCrystalAxe(ToolMaterialCrystal);
		crystalHoe = new ItemCrystalHoe(ToolMaterialCrystal);
		crystalShovel = new ItemCrystalShovel(ToolMaterialCrystal);
		megaCrystalShovel = new ItemMegaCrystalShovel(ToolMaterialCrystal);
		crystalPickaxe = new ItemCrystalPickaxe(ToolMaterialCrystal);
		megaCrystalPickaxe = new ItemMegaCrystalPickaxe(ToolMaterialCrystal);
		crystalSword = new ItemCrystalSword(ToolMaterialCrystal);
		shears = new ItemCrystalShears();
		
		ItemStack darkIronPlate = new ItemStack(plates, 1, PlateType.DARK_IRON.getMetadata());
		darkIronHelmet = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.HEAD, "diron", darkIronPlate), "darkironhelmet");
		darkIronChestplate = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.CHEST, "diron", darkIronPlate), "darkironchestplate");
		darkIronLeggings = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.LEGS, "diron", darkIronPlate), "darkironleggings");
		darkIronBoots = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.FEET, "diron", darkIronPlate), "darkironboots");
		
		crystalHelmet = registerItem(new ItemCrystalArmor(ArmorMaterialCrystal, EntityEquipmentSlot.HEAD, "helmet", ItemStackTools.getEmptyStack()), "crystalhelmet");
		crystalChestplate = registerItem(new ItemCrystalArmor(ArmorMaterialCrystal, EntityEquipmentSlot.CHEST, "chest", ItemStackTools.getEmptyStack()), "crystalchestplate");
		crystalLeggings = registerItem(new ItemCrystalArmor(ArmorMaterialCrystal, EntityEquipmentSlot.LEGS, "legs", ItemStackTools.getEmptyStack()), "crystalleggings");
		crystalBoots = registerItem(new ItemCrystalArmor(ArmorMaterialCrystal, EntityEquipmentSlot.FEET, "boots", ItemStackTools.getEmptyStack()), "crystalboots");
		
		wolfArmor = new ItemWolfArmor();
		
		darkIronAxe = registerItem(new ItemCustomAxe(ToolMaterialDarkIron), "darkironaxe");
		darkIronHoe = (ItemHoe) registerItem(new ItemHoe(ToolMaterialDarkIron), "darkironhoe").setCreativeTab(CrystalMod.tabTools);
		darkIronShovel = (ItemSpade) registerItem(new ItemSpade(ToolMaterialDarkIron), "darkironshovel").setCreativeTab(CrystalMod.tabTools);
		darkIronPickaxe = registerItem(new ItemCustomPickaxe(ToolMaterialDarkIron), "darkironpickaxe");
		darkIronSword = (ItemSword) registerItem(new ItemSword(ToolMaterialDarkIron), "darkironsword").setCreativeTab(CrystalMod.tabTools);
		darkIronBow = new ItemDarkIronBow();
		
		bat = new ItemBat();
		
		chestMinecart = new ItemCrystalChestMinecart();
		enderChestMinecart = new ItemEnderChestMinecart();
		wirelessChestMinecart = new ItemWirelessChestMinecart();
		
		crystalSeedsBlue = new ItemCrystalSeeds(PlantType.BLUE);
		crystalSeedsRed = new ItemCrystalSeeds(PlantType.RED);
		crystalSeedsGreen = new ItemCrystalSeeds(PlantType.GREEN);
		crystalSeedsDark = new ItemCrystalSeeds(PlantType.DARK);
		crystalTreeSeedsBlue = new ItemCrystalSeedTree(PlantType.BLUE);
		crystalTreeSeedsRed = new ItemCrystalSeedTree(PlantType.RED);
		crystalTreeSeedsGreen = new ItemCrystalSeedTree(PlantType.GREEN);
		crystalTreeSeedsDark = new ItemCrystalSeedTree(PlantType.DARK);
		materialSeed = new ItemMaterialSeed();
		corn = new ItemCorn();
		pipeCover = new ItemPipeCover();
		pipeFilter = new ItemPipeFilter();
		wrench = new ItemCrystalWrench();
		guide = new ItemCrystalGuide();
		enhancementKnowledge = new ItemEnhancementKnowledge();
		harddrive = new ItemHDD();
		craftingPattern = new ItemPattern();
		securityCard = new ItemSecurityCard();
		wirelessPanel = new ItemWirelessPanel();
		pipeAttachmant = new ItemPipeAttachment();
		miscCard = new ItemMiscCard();
		normalBackpack = new ItemBackpackNormal(new BackpackNormal());
		craftingBackpack = new ItemBackpackBase(new BackpackCrafting());
		wirelessBackpack = new ItemBackpackBase(new BackpackWireless());
		backpackupgrade = new ItemBackpackUpgrade();
		lock = new ItemLock();
		telePearl = new ItemTeloportTool();
		superTorch = new ItemSuperTorch();
		disguise = new ItemDisguise();
		reactorUpgrade = new ItemReactorUpgrade();
		engineCore = new ItemEngineCore();
		flag = new ItemFlag();
		wings = new ItemDragonWings();
		mobEssence = new ItemMobEssence();
		emptyMobEssence = new ItemEmptyMobEssence();
		minion = new ItemMinion();
		minionStaff = new ItemMinionStaff();
		worksiteUpgrade = new ItemWorksiteUpgrade();
		bombomb = new ItemBombomb();
		darkarang = new ItemDarkarang();
		dagger = new ItemDagger();
		
		//Baubles
		if(BaublesIntegration.instance().hasBaubles()){
			dragonWingsBauble = new ItemBaubleWings();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(){
		for(Item item : REGISTRY.values()){
			if(item instanceof ICustomModel){
				((ICustomModel)item).initModel();
			}else{
				initBasicModel(item);
			}
		}
	}
	
	public static <T extends Item> T registerItem(T item, String name){
		String finalName = name;
		String lowerCase = name.toLowerCase();
		if(name !=lowerCase){
			throw new RuntimeException(name+" is not lowercase!");
		}
		
		item.setUnlocalizedName(CrystalMod.prefix(finalName));
		item.setRegistryName(finalName);
		GameRegistry.register(item);
		REGISTRY.put(finalName, item);
		return item;
	}
	
	@SideOnly(Side.CLIENT)
	public static void initBasicModel(Item item){
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
}
