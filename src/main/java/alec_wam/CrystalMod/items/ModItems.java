package alec_wam.CrystalMod.items;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
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
import alec_wam.CrystalMod.items.armor.ItemCustomArmor;
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
import alec_wam.CrystalMod.items.tools.ItemLock;
import alec_wam.CrystalMod.items.tools.ItemSuperTorch;
import alec_wam.CrystalMod.items.tools.ItemToolParts;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackCrafting;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade;
import alec_wam.CrystalMod.items.tools.bat.ItemBat;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemWorksiteUpgrade;
import alec_wam.CrystalMod.tiles.pipes.attachments.ItemPipeAttachment;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ItemPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.ItemWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter;
import alec_wam.CrystalMod.tiles.spawner.ItemEmptyMobEssence;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
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
	
	public static ToolMaterial ToolMaterialCrystal = EnumHelper.addToolMaterial(CrystalMod.MODID.toLowerCase()+".crystal", 5, 2000, 20.0F, 6.0F, 22);
	public static ToolMaterial ToolMaterialDarkIron = EnumHelper.addToolMaterial(CrystalMod.MODID.toLowerCase()+".darkIron", 2, 500/*250*/, 7.0F/*6.0F*/, 2.5F/*2.0F*/, 12/*14*/);

	public static ArmorMaterial ArmorMaterialDarkIron = EnumHelper.addArmorMaterial(CrystalMod.MODID.toLowerCase()+".darkIron", "darkiron", 16, new int[]{2, 5, 6, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
	
	public static ItemCrystalAxe crystalAxe;
	public static ItemCrystalHoe crystalHoe;
	public static ItemCrystalShovel crystalShovel;
	public static ItemCrystalPickaxe crystalPickaxe;
	public static ItemCrystalSword crystalSword;
	public static ItemCrystalShears shears;
	
	public static ItemArmor darkIronHelmet, darkIronChestplate, darkIronLeggings, darkIronBoots;
	
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
	
	public static ItemCrystalSeeds crystalSeedsBlue, crystalSeedsRed, crystalSeedsGreen, crystalSeedsDark;
	public static ItemCrystalSeedTree crystalTreeSeedsBlue, crystalTreeSeedsRed, crystalTreeSeedsGreen, crystalTreeSeedsDark;
	public static Item crystalReedsBlue, crystalReedsRed, crystalReedsGreen, crystalReedsDark;
	public static ItemMaterialSeed materialSeed;
	
	public static ItemPipeCover pipeCover;
	public static ItemPipeFilter pipeFilter;
	
	public static ItemHDD harddrive;
	public static ItemPattern craftingPattern; 
	public static ItemWirelessPanel wirelessPanel; 
	public static ItemPipeAttachment pipeAttachmant;
	
	public static ItemBackpackNormal normalBackpack;
	public static ItemBackpackBase craftingBackpack;
	public static ItemBackpackUpgrade backpackupgrade;
	public static ItemLock lock;
	
	public static ItemMiscCard miscCard;
	public static ItemTeloportTool telePearl;
	public static ItemSuperTorch superTorch;
	public static ItemDisguise disguise;
	
	public static ItemFlag flag;
	public static ItemDragonWings wings;
	public static ItemMobEssence mobEssence;
	public static ItemEmptyMobEssence emptyMobEssence;
	public static ItemMinion minion;
	public static ItemMinionStaff minionStaff;
	public static ItemWorksiteUpgrade worksiteUpgrade;
	public static ItemBombomb bombomb;
	
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
		
		crystalAxe = new ItemCrystalAxe(ToolMaterialCrystal);
		crystalHoe = new ItemCrystalHoe(ToolMaterialCrystal);
		crystalShovel = new ItemCrystalShovel(ToolMaterialCrystal);
		crystalPickaxe = new ItemCrystalPickaxe(ToolMaterialCrystal);
		crystalSword = new ItemCrystalSword(ToolMaterialCrystal);
		shears = new ItemCrystalShears();
		
		ItemStack darkIronPlate = new ItemStack(plates, 1, PlateType.DARK_IRON.getMetadata());
		darkIronHelmet = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.HEAD, "darkIron", darkIronPlate), "darkironhelmet");
		darkIronChestplate = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.CHEST, "darkIron", darkIronPlate), "darkironchestplate");
		darkIronLeggings = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.LEGS, "darkIron", darkIronPlate), "darkironleggings");
		darkIronBoots = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.FEET, "darkIron", darkIronPlate), "darkironboots");
		
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
		pipeCover = new ItemPipeCover();
		pipeFilter = new ItemPipeFilter();
		wrench = new ItemCrystalWrench();
		guide = new ItemCrystalGuide();
		harddrive = new ItemHDD();
		craftingPattern = new ItemPattern();
		wirelessPanel = new ItemWirelessPanel();
		pipeAttachmant = new ItemPipeAttachment();
		miscCard = new ItemMiscCard();
		normalBackpack = new ItemBackpackNormal(new BackpackNormal());
		craftingBackpack = new ItemBackpackBase(new BackpackCrafting());
		backpackupgrade = new ItemBackpackUpgrade();
		lock = new ItemLock();
		telePearl = new ItemTeloportTool();
		superTorch = new ItemSuperTorch();
		disguise = new ItemDisguise();
		flag = new ItemFlag();
		wings = new ItemDragonWings();
		mobEssence = new ItemMobEssence();
		emptyMobEssence = new ItemEmptyMobEssence();
		minion = new ItemMinion();
		minionStaff = new ItemMinionStaff();
		worksiteUpgrade = new ItemWorksiteUpgrade();
		bombomb = new ItemBombomb();
		
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
	
	//TODO Add Forced Lowercase Registry name
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
