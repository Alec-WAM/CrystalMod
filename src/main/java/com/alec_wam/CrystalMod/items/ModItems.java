package com.alec_wam.CrystalMod.items;

import java.util.Map;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.BlockCrystalPlant.PlantType;
import com.alec_wam.CrystalMod.blocks.ICustomModel;
import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.entities.minions.ItemMinion;
import com.alec_wam.CrystalMod.entities.minions.ItemMinionStaff;
import com.alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import com.alec_wam.CrystalMod.items.armor.ItemCustomArmor;
import com.alec_wam.CrystalMod.items.backpack.ItemBackpack;
import com.alec_wam.CrystalMod.items.game.ItemFlag;
import com.alec_wam.CrystalMod.items.guide.ItemCrystalGuide;
import com.alec_wam.CrystalMod.items.tools.ItemCrystalAxe;
import com.alec_wam.CrystalMod.items.tools.ItemCrystalShears;
import com.alec_wam.CrystalMod.items.tools.ItemDarkIronBow;
import com.alec_wam.CrystalMod.items.tools.ItemCrystalHoe;
import com.alec_wam.CrystalMod.items.tools.ItemCrystalPickaxe;
import com.alec_wam.CrystalMod.items.tools.ItemCrystalShovel;
import com.alec_wam.CrystalMod.items.tools.ItemCrystalSword;
import com.alec_wam.CrystalMod.items.tools.ItemCustomAxe;
import com.alec_wam.CrystalMod.items.tools.ItemCustomPickaxe;
import com.alec_wam.CrystalMod.items.tools.ItemToolParts;
import com.alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard;
import com.alec_wam.CrystalMod.tiles.machine.worksite.ItemWorksiteUpgrade;
import com.alec_wam.CrystalMod.tiles.pipes.attachments.ItemPipeAttachment;
import com.alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ItemPattern;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.ItemWirelessPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import com.alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter;
import com.alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import com.google.common.collect.Maps;

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
	public static Item crystalReeds;
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
	
	public static ItemCrystalWrench wrench;
	public static ItemCrystalGuide guide;
	
	public static ItemCrystalSeeds crystalSeedsBlue, crystalSeedsRed, crystalSeedsGreen, crystalSeedsDark;
	public static ItemCrystalSeedTree crystalTreeSeedsBlue, crystalTreeSeedsRed, crystalTreeSeedsGreen, crystalTreeSeedsDark;
	public static ItemPipeCover pipeCover;
	public static ItemPipeFilter pipeFilter;
	
	public static ItemHDD harddrive;
	public static ItemPattern craftingPattern; 
	public static ItemWirelessPanel wirelessPanel; 
	public static ItemPipeAttachment pipeAttachmant;
	
	public static ItemMiscCard miscCard;
	public static ItemBackpack backpack;
	public static ItemTeloportTool telePearl;
	
	public static ItemFlag flag;
	public static ItemDragonWings wings;
	public static ItemMobEssence mobEssence;
	public static ItemMinion minion;
	public static ItemMinionStaff minionStaff;
	public static ItemWorksiteUpgrade worksiteUpgrade;
	
	public static void init() {
		crystals = new ItemCrystal();
		ingots = new ItemIngot();
		plates = new ItemMetalPlate();
		crystalReeds = registerItem(new ItemBlockSpecial(ModBlocks.crystalReeds).setCreativeTab(CrystalMod.tabItems), "crystalReeds");
		toolParts = new ItemToolParts();
		machineFrame = new ItemMachineFrame();
		
		crystalAxe = new ItemCrystalAxe(ToolMaterialCrystal);
		crystalHoe = new ItemCrystalHoe(ToolMaterialCrystal);
		crystalShovel = new ItemCrystalShovel(ToolMaterialCrystal);
		crystalPickaxe = new ItemCrystalPickaxe(ToolMaterialCrystal);
		crystalSword = new ItemCrystalSword(ToolMaterialCrystal);
		shears = new ItemCrystalShears();
		
		ItemStack darkIronPlate = new ItemStack(plates, 1, PlateType.DARK_IRON.getMetadata());
		darkIronHelmet = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.HEAD, "darkIron", darkIronPlate), "darkIronHelmet");
		darkIronChestplate = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.CHEST, "darkIron", darkIronPlate), "darkIronChestplate");
		darkIronLeggings = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.LEGS, "darkIron", darkIronPlate), "darkIronLeggings");
		darkIronBoots = registerItem(new ItemCustomArmor(ArmorMaterialDarkIron, EntityEquipmentSlot.FEET, "darkIron", darkIronPlate), "darkIronBoots");
		
		darkIronAxe = registerItem(new ItemCustomAxe(ToolMaterialDarkIron), "darkIronAxe");
		darkIronHoe = (ItemHoe) registerItem(new ItemHoe(ToolMaterialDarkIron), "darkIronHoe").setCreativeTab(CrystalMod.tabTools);
		darkIronShovel = (ItemSpade) registerItem(new ItemSpade(ToolMaterialDarkIron), "darkIronShovel").setCreativeTab(CrystalMod.tabTools);
		darkIronPickaxe = registerItem(new ItemCustomPickaxe(ToolMaterialDarkIron), "darkIronPickaxe");
		darkIronSword = (ItemSword) registerItem(new ItemSword(ToolMaterialDarkIron), "darkIronSword").setCreativeTab(CrystalMod.tabTools);
		darkIronBow = new ItemDarkIronBow();
		
		crystalSeedsBlue = new ItemCrystalSeeds(PlantType.BLUE);
		crystalSeedsRed = new ItemCrystalSeeds(PlantType.RED);
		crystalSeedsGreen = new ItemCrystalSeeds(PlantType.GREEN);
		crystalSeedsDark = new ItemCrystalSeeds(PlantType.DARK);
		crystalTreeSeedsBlue = new ItemCrystalSeedTree(PlantType.BLUE);
		crystalTreeSeedsRed = new ItemCrystalSeedTree(PlantType.RED);
		crystalTreeSeedsGreen = new ItemCrystalSeedTree(PlantType.GREEN);
		crystalTreeSeedsDark = new ItemCrystalSeedTree(PlantType.DARK);
		pipeCover = new ItemPipeCover();
		pipeFilter = new ItemPipeFilter();
		wrench = new ItemCrystalWrench();
		guide = new ItemCrystalGuide();
		harddrive = new ItemHDD();
		craftingPattern = new ItemPattern();
		wirelessPanel = new ItemWirelessPanel();
		pipeAttachmant = new ItemPipeAttachment();
		miscCard = new ItemMiscCard();
		backpack = new ItemBackpack();
		telePearl = new ItemTeloportTool();
		flag = new ItemFlag();
		wings = new ItemDragonWings();
		mobEssence = new ItemMobEssence();
		minion = new ItemMinion();
		minionStaff = new ItemMinionStaff();
		worksiteUpgrade = new ItemWorksiteUpgrade();
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
		item.setUnlocalizedName(CrystalMod.prefix(name));
		item.setRegistryName(name);
		GameRegistry.register(item);
		REGISTRY.put(name, item);
		return item;
	}
	
	@SideOnly(Side.CLIENT)
	public static void initBasicModel(Item item){
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
}
