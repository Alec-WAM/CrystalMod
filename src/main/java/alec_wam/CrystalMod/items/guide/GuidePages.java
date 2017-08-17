package alec_wam.CrystalMod.items.guide;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.crop.CropRecipe;
import alec_wam.CrystalMod.api.crop.SpecialCropRecipe;
import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.api.guide.GuideChapter;
import alec_wam.CrystalMod.api.guide.GuideIndex;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.api.guide.ITextEditor;
import alec_wam.CrystalMod.api.guide.TranslationHandler;
import alec_wam.CrystalMod.blocks.BlockCompressed.CompressedBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.blocks.BlockDecorative.DecorativeBlockType;
import alec_wam.CrystalMod.blocks.BlockFallingCompressed.FallingCompressedBlockType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.material.IMaterialCrop;
import alec_wam.CrystalMod.blocks.crops.material.ItemMaterialSeed;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockBasicTiles.BasicTileType;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockBasicTiles2.BasicTileType2;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockCrystalTiles.CrystalTileType;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBasicItemPage;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import alec_wam.CrystalMod.client.util.comp.GuiComponentStandardRecipePage;
import alec_wam.CrystalMod.entities.accessories.WolfAccessories.WolfArmor;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemCrystalSap.SapType;
import alec_wam.CrystalMod.items.ItemCursedBone.BoneType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.guide.page.PageCrafting;
import alec_wam.CrystalMod.items.guide.page.PageFurnace;
import alec_wam.CrystalMod.items.guide.page.PageIcon;
import alec_wam.CrystalMod.items.guide.page.PageMaterialCropRecipe;
import alec_wam.CrystalMod.items.guide.page.PagePress;
import alec_wam.CrystalMod.items.guide.page.PageText;
import alec_wam.CrystalMod.items.tools.ItemEnhancementKnowledge;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.tiles.cases.BlockCase.EnumCaseType;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.crate.BlockCrate.CrateType;
import alec_wam.CrystalMod.tiles.explosives.remover.BlockRemoverExplosion.RemoverType;
import alec_wam.CrystalMod.tiles.lamps.BlockAdvancedLamp.LampType;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard.CardType;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine.EngineType;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.ItemReactorUpgrade.UpgradeType;
import alec_wam.CrystalMod.tiles.machine.specialengines.BlockSpecialEngine.SpecialEngineType;
import alec_wam.CrystalMod.tiles.machine.specialengines.ItemEngineCore.EngineCoreType;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.shieldrack.BlockShieldRack.WoodType;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench.WorkbenchType;
import alec_wam.CrystalMod.util.FileInfo;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.StringUtils;
import alec_wam.CrystalMod.util.Util;
import net.minecraft.block.Block;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;

@SuppressWarnings("deprecation")
public class GuidePages {

	public static void createPages(){
		CrystalModAPI.GUIDE_INDEXES.clear();
		CrystalModAPI.BLOCKS = CrystalModAPI.regiterGuideIndex(new GuideIndex("blocks"));
		CrystalModAPI.ITEMS = CrystalModAPI.regiterGuideIndex(new GuideIndex("items"));
		CrystalModAPI.MATERIALCROPS = CrystalModAPI.regiterGuideIndex(new GuideIndex("materialcrops"));
		CrystalModAPI.ENTITES = CrystalModAPI.regiterGuideIndex(new GuideIndex("entites"));
		CrystalModAPI.WORKBENCH = CrystalModAPI.regiterGuideIndex(new GuideIndex("workbench"));
		CrystalModAPI.MISC = CrystalModAPI.regiterGuideIndex(new GuideIndex("misc"));
		
		CrystalType[] crystalArray = new CrystalType[]{CrystalType.BLUE, CrystalType.RED, CrystalType.GREEN, CrystalType.DARK};
		NonNullList<ItemStack> crystalFullList = ItemUtil.getItemSubtypes(ModItems.crystals, crystalArray);
		
		NonNullList<ItemStack> oreList = ItemUtil.getBlockSubtypes(ModBlocks.crystalOre, CrystalOreType.values());
		Map<String, List<?>> lookUp = Maps.newHashMap();
		lookUp.put("0", oreList);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalore", new PageIcon("0", oreList), new PageFurnace("smelt", crystalFullList)).setDisplayObject(oreList).setLookUpData(lookUp));

		
		CrystalBlockType[] normalArray = new CrystalBlockType[]{CrystalBlockType.BLUE, CrystalBlockType.RED, CrystalBlockType.GREEN, CrystalBlockType.DARK};
		CrystalBlockType[] chisledArray = new CrystalBlockType[]{CrystalBlockType.BLUE_CHISELED, CrystalBlockType.RED_CHISELED, CrystalBlockType.GREEN_CHISELED, CrystalBlockType.DARK_CHISELED};
		CrystalBlockType[] brickArray = new CrystalBlockType[]{CrystalBlockType.BLUE_BRICK, CrystalBlockType.RED_BRICK, CrystalBlockType.GREEN_BRICK, CrystalBlockType.DARK_BRICK};
		List<ItemStack> normalBlocksDisplayList = ItemUtil.getBlockSubtypes(ModBlocks.crystal, CrystalBlockType.BLUE, CrystalBlockType.BLUE_CHISELED, CrystalBlockType.BLUE_BRICK);
		GuideChapter chapterCrystalBlock = new GuideChapter("crystalblock", new PageCrafting("normal", ItemUtil.getBlockSubtypes(ModBlocks.crystal, normalArray)), new PageIcon("chiseled", ItemUtil.getBlockSubtypes(ModBlocks.crystal, chisledArray)), new PageCrafting("brick", ItemUtil.getBlockSubtypes(ModBlocks.crystal, brickArray))).setDisplayObject(normalBlocksDisplayList);
		CrystalModAPI.BLOCKS.registerChapter(chapterCrystalBlock);
		
		NonNullList<ItemStack> ingotBlockList = ItemUtil.getBlockSubtypes(ModBlocks.crystalIngot, CrystalIngotBlockType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalingotblock", new PageCrafting("main", ingotBlockList)).setDisplayObject(ingotBlockList));
		
		NonNullList<ItemStack> glassList = ItemUtil.getBlockSubtypes(ModBlocks.crystalGlass, GlassType.BLUE, GlassType.RED, GlassType.GREEN, GlassType.DARK);
		NonNullList<ItemStack> paneList = ItemUtil.getBlockSubtypes(ModBlocks.crystalGlassPane, GlassType.BLUE, GlassType.RED, GlassType.GREEN, GlassType.DARK);
		NonNullList<ItemStack> tintedList = ItemUtil.getBlockSubtypes(ModBlocks.crystalGlassTinted, GlassType.BLUE, GlassType.RED, GlassType.GREEN, GlassType.DARK);
		NonNullList<ItemStack> paintedList = ItemUtil.getBlockSubtypes(ModBlocks.crystalGlassPainted, GlassType.BLUE, GlassType.RED, GlassType.GREEN, GlassType.DARK);

		NonNullList<ItemStack> glassDisplayList = NonNullList.create();
		glassDisplayList.addAll(glassList); glassDisplayList.addAll(paneList);
		glassDisplayList.addAll(tintedList); glassDisplayList.addAll(paintedList);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalglass", new PageCrafting("main", glassList), new PageCrafting("pane", paneList), new PageIcon("tinted", tintedList), new PageCrafting("painted", paintedList)).setDisplayObject(glassDisplayList));
		
		ItemStack charcoalBlock = new ItemStack(ModBlocks.compressed, 1, CompressedBlockType.CHARCOAL.getMeta());
		ItemStack flintBlock = new ItemStack(ModBlocks.compressed, 1, CompressedBlockType.FLINT.getMeta());
		ItemStack gunpowderBlock = new ItemStack(ModBlocks.fallingCompressed, 1, FallingCompressedBlockType.GUNPOWDER.getMeta());
		ItemStack sugarBlock = new ItemStack(ModBlocks.fallingCompressed, 1, FallingCompressedBlockType.SUGAR.getMeta());
		ItemStack blazeRodBlock = new ItemStack(ModBlocks.blazeRodBlock);
		NonNullList<ItemStack> compressedList = NonNullList.create();
		compressedList.add(charcoalBlock);
		compressedList.add(flintBlock);
		compressedList.add(gunpowderBlock);
		compressedList.add(sugarBlock);
		compressedList.add(blazeRodBlock);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("compressedblocks", new PageCrafting("blazerod", blazeRodBlock), new PageCrafting("charcoal", charcoalBlock), new PageCrafting("flint", flintBlock), new PageCrafting("gunpowder", gunpowderBlock), new PageCrafting("sugar", sugarBlock)).setDisplayObject(compressedList));

		NonNullList<ItemStack> tiles = NonNullList.create();
		tiles.addAll(ItemUtil.getBlockSubtypes(ModBlocks.tileBasic, BasicTileType.values()));
		tiles.addAll(ItemUtil.getBlockSubtypes(ModBlocks.tileBasic2, BasicTileType2.values()));
		tiles.addAll(ItemUtil.getBlockSubtypes(ModBlocks.tileCrystal, CrystalTileType.values()));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("tiles", new PageCrafting("main", tiles)).setDisplayObject(tiles));
		
		NonNullList<ItemStack> workbenchList = ItemUtil.getBlockSubtypes(ModBlocks.crystalWorkbench, WorkbenchType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalworkbench", new PageCrafting("main", workbenchList)).setDisplayObject(workbenchList));
		
		ItemStack enhancementtable = new ItemStack(ModBlocks.enhancementTable);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("enhancementtable", new PageCrafting("main", enhancementtable)).setDisplayObject(enhancementtable));
		
		ItemStack cauldron = new ItemStack(ModBlocks.cauldron);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("cauldron", new PageCrafting("main", cauldron)).setDisplayObject(cauldron));
		
		NonNullList<ItemStack> pedestals = NonNullList.create();
		pedestals.add(new ItemStack(ModBlocks.pedistal));
		pedestals.add(new ItemStack(ModBlocks.fusionPedistal));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("pedestals", new PageCrafting("main", pedestals), new PageIcon("setup", new ItemStack(ModBlocks.fusionPedistal))).setDisplayObject(pedestals));
		
		NonNullList<ItemStack> chestList = getEnumSpecial(ModBlocks.crystalChest, new CrystalChestType[]{CrystalChestType.DARKIRON, CrystalChestType.BLUE, CrystalChestType.RED, CrystalChestType.GREEN, CrystalChestType.DARK, CrystalChestType.PURE});
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalchest", new PageCrafting("main", chestList)).setDisplayObject(chestList));

		NonNullList<ItemStack> pipes = ItemUtil.getBlockSubtypes(ModBlocks.crystalPipe, PipeType.values());
		PageCrafting[] pipePages = new PageCrafting[PipeType.values().length];
		for(PipeType pipeType : PipeType.values()){
			NonNullList<ItemStack> list = NonNullList.create();
			if(pipeType == PipeType.POWERCU || pipeType == PipeType.POWERRF){
				for(int i = 0; i < 4; i++){
					ItemStack stack = new ItemStack(ModBlocks.crystalPipe, 1, pipeType.getMeta());
					ItemNBTHelper.setInteger(stack, "Tier", i);
					list.add(stack);
				}
			} 
			else {
				ItemStack stack = new ItemStack(ModBlocks.crystalPipe, 1, pipeType.getMeta());
				list.add(stack);
			}
			pipePages[pipeType.ordinal()] = new PageCrafting(pipeType.getName().toLowerCase(), list);
		}
		List<GuidePage> pipePageList = Lists.<GuidePage>newArrayList();
		pipePageList.addAll(Arrays.asList(pipePages));
		NonNullList<ItemStack> attachmentList = NonNullList.create();
		ModItems.pipeAttachmant.getSubItems(null, null, attachmentList);
		pipePageList.add(new PageCrafting("attachments", attachmentList));
		pipePageList.add(new PageText("iomodes"));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("pipes", pipePageList.toArray(new GuidePage[0])).setDisplayObject(pipes));

		ItemStack engineFurnace = new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta());
		ItemNBTHelper.setInteger(engineFurnace, "Tier", 0);
		ItemStack engineLava = new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta());
		ItemNBTHelper.setInteger(engineLava, "Tier", 0);
		ItemStack engineVampire = new ItemStack(ModBlocks.engine, 1, EngineType.VAMPIRE.getMeta());
		ItemNBTHelper.setInteger(engineVampire, "Tier", 0);
		NonNullList<ItemStack> engines = NonNullList.create();
		engines.add(engineFurnace); engines.add(engineLava); engines.add(engineVampire);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("engines", new PageCrafting("furnace", engineFurnace), new PageCrafting("lava", engineLava), new PageCrafting("vampire", engineVampire), new PageText("tiers")).setDisplayObject(engines));

		NonNullList<ItemStack> advEngines = NonNullList.create();
		NonNullList<ItemStack> specialEngines = NonNullList.create();
		specialEngines.add(new ItemStack(ModBlocks.specialengine, 1, SpecialEngineType.FINITE.getMeta()));
		specialEngines.add(new ItemStack(ModBlocks.specialengine, 1, SpecialEngineType.INFINITE.getMeta()));
		advEngines.addAll(specialEngines);		
		NonNullList<ItemStack> enginecores = NonNullList.create();
		enginecores.add(new ItemStack(ModItems.engineCore, 1, EngineCoreType.FINITE.getMetadata()));
		enginecores.add(new ItemStack(ModItems.engineCore, 1, EngineCoreType.INFINITE.getMetadata()));
		advEngines.addAll(enginecores);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("advancedengines", new PageCrafting("engines", specialEngines), new PageCrafting("cores", enginecores)).setDisplayObject(advEngines));
		
		NonNullList<ItemStack> batteries = ItemUtil.getBlockSubtypes(ModBlocks.battery, BatteryType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("battery", new PageCrafting("main", batteries)).setDisplayObject(batteries));
		
		NonNullList<ItemStack> storagecases = ItemUtil.getBlockSubtypes(ModBlocks.storageCase, EnumCaseType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("storagecases", new PageCrafting("main", storagecases)).setDisplayObject(storagecases));
			
		//TODO Add Clusters and Bridge
		
		NonNullList<ItemStack> reedList = NonNullList.create();
		reedList.add(new ItemStack(ModItems.crystalReedsBlue));
		reedList.add(new ItemStack(ModItems.crystalReedsRed));
		reedList.add(new ItemStack(ModItems.crystalReedsGreen));
		reedList.add(new ItemStack(ModItems.crystalReedsDark));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalreeds", new PageIcon("main", reedList)).setDisplayObject(reedList));
		
		ItemStack lilyPad = new ItemStack(ModBlocks.flowerLilypad);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("flowerlilypad", new PageCrafting("main", lilyPad)).setDisplayObject(lilyPad));
		
		NonNullList<ItemStack> plantList = NonNullList.create();
		plantList.add(new ItemStack(ModItems.crystalSeedsBlue));
		plantList.add(new ItemStack(ModItems.crystalSeedsRed));
		plantList.add(new ItemStack(ModItems.crystalSeedsGreen));
		plantList.add(new ItemStack(ModItems.crystalSeedsDark));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalplant", new PageIcon("main", plantList)).setDisplayObject(plantList));
		
		NonNullList<ItemStack> listSaplings = ItemUtil.getBlockSubtypes(ModBlocks.crystalSapling, BlockCrystalLog.WoodType.values());
		NonNullList<ItemStack> listLogs = ItemUtil.getBlockSubtypes(ModBlocks.crystalLog, BlockCrystalLog.WoodType.values());
		NonNullList<ItemStack> treePlantList = NonNullList.create();
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsBlue));
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsRed));
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsGreen));
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsDark));
		NonNullList<ItemStack> plankList = ItemUtil.getBlockSubtypes(ModBlocks.crystalPlanks, BlockCrystalLog.WoodType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystaltrees", new PageIcon("main", listSaplings), new PageIcon("logs", listLogs), new PageCrafting("planks", plankList), new PageIcon("treeplant", treePlantList)).setDisplayObject(listSaplings));

		ItemStack sapExtractor = new ItemStack(ModBlocks.sapExtractor);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("sap", new PageCrafting("extractor", sapExtractor), new PageIcon("sap", ItemUtil.getItemSubtypes(ModItems.crystalSap, SapType.values()))).setDisplayObject(sapExtractor));
		
		NonNullList<ItemStack> listTanks = ItemUtil.getBlockSubtypes(ModBlocks.crystalTank, TankType.BLUE, TankType.RED, TankType.GREEN, TankType.DARK);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystaltank", new PageCrafting("main", listTanks)).setDisplayObject(listTanks));

		ItemStack weather = new ItemStack(ModBlocks.weather);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("weatherforcaster", new PageCrafting("main", weather)).setDisplayObject(weather));

		ItemStack spawner = new ItemStack(ModBlocks.customSpawner);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("spawner", new PageCrafting("main", spawner), new PageText("upgrades")).setDisplayObject(spawner));

		ItemStack darkinfection = new ItemStack(ModBlocks.darkInfection);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("darkinfection", new PageCrafting("main", darkinfection)).setDisplayObject(darkinfection));
		
		ItemStack fusor0 = new ItemStack(ModBlocks.oppositeFuser, 1, 0);
		ItemStack fusor1 = new ItemStack(ModBlocks.oppositeFuser, 1, 1);
		ItemStack fusor2 = new ItemStack(ModBlocks.oppositeFuser, 1, 2);
		NonNullList<ItemStack> fusorList = NonNullList.create();
		fusorList.add(fusor0); fusorList.add(fusor1); fusorList.add(fusor2);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("oppositefusor", new PageCrafting("normal", fusor0), new PageCrafting("advanced", fusor1), new PageCrafting("super", fusor2)).setDisplayObject(fusorList));
				
		ItemStack mobgrinder = new ItemStack(ModBlocks.mobGrinder);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("mobgrinder", new PageCrafting("main", mobgrinder)).setDisplayObject(mobgrinder));

		ItemStack elevator = new ItemStack(ModBlocks.elevator);
		ItemStack elevatorFloor = new ItemStack(ModBlocks.elevatorFloor);
		ItemStack elevatorCaller = new ItemStack(ModBlocks.elevatorCaller);
		List<ItemStack> listElevators = Lists.newArrayList();
		listElevators.add(elevator); listElevators.add(elevatorFloor); listElevators.add(elevatorCaller);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("elevator", new PageCrafting("elevator", elevator), new PageCrafting("floor", elevatorFloor), new PageCrafting("caller", elevatorCaller)).setDisplayObject(listElevators));

		NonNullList<ItemStack> enderBuffers = NonNullList.create();
		for(int i = 0; i < 16; i++){
			int code = WirelessChestHelper.getDefaultCode(EnumDyeColor.byMetadata(i));
			ItemStack enderbuffer = new ItemStack(ModBlocks.enderBuffer);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Code", code);
			ItemNBTHelper.getCompound(enderbuffer).setTag(BlockMachine.TILE_NBT_STACK, nbt);
			enderBuffers.add(enderbuffer);
		}
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("enderbuffer", new PageCrafting("main", enderBuffers)).setDisplayObject(enderBuffers));
		
		ItemStack playercube = new ItemStack(ModBlocks.cubePortal);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("playercubeportal", new PageText("main"), new PageText("creation"), new PageCrafting("portalcrafting", playercube), new PageCrafting("cardcrafting", new ItemStack(ModItems.miscCard, 1, CardType.CUBE.getMetadata()))).setDisplayObject(playercube));
		
		ItemStack endertorch = new ItemStack(ModBlocks.enderTorch);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("endertorch", new PageCrafting("main", endertorch)).setDisplayObject(endertorch));

		ItemStack purelamp = new ItemStack(ModBlocks.advancedLamp, 1, LampType.PURE.getMeta());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("purelamp", new PageCrafting("main", purelamp)).setDisplayObject(purelamp));
		
		ItemStack darklamp = new ItemStack(ModBlocks.advancedLamp, 1, LampType.DARK.getMeta());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("darklamp", new PageCrafting("main", darklamp)).setDisplayObject(darklamp));

		ItemStack advdispenser = new ItemStack(ModBlocks.advDispenser);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("advdispenser", new PageCrafting("main", advdispenser), new PageText("modes")).setDisplayObject(advdispenser));
		
		ItemStack entityhopper = new ItemStack(ModBlocks.entityHopper);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("entityhopper", new PageCrafting("main", entityhopper), new PageText("types")).setDisplayObject(entityhopper));
				
		NonNullList<ItemStack> crates = ItemUtil.getBlockSubtypes(ModBlocks.crates, CrateType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crate", new PageCrafting("main", crates)).setDisplayObject(crates));
		
		ItemStack jar = new ItemStack(ModBlocks.jar);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("jar", new PageCrafting("main", jar)).setDisplayObject(jar));
		
		NonNullList<ItemStack> shieldracks = ItemUtil.getBlockSubtypes(ModBlocks.shieldRack, WoodType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("shieldrack", new PageCrafting("main", shieldracks)).setDisplayObject(shieldracks));
		
		ItemStack particleThrower = new ItemStack(ModBlocks.particleThrower);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("particlethrower", new PageCrafting("main", particleThrower)).setDisplayObject(particleThrower));
		
		NonNullList<ItemStack> explosives = ItemUtil.getBlockSubtypes(ModBlocks.remover, RemoverType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("removerexplosives", new PageCrafting("main", explosives)).setDisplayObject(explosives));
		
		NonNullList<ItemStack> reactorList = NonNullList.create();
		ItemStack redstoneCore = new ItemStack(ModBlocks.redstoneCore);
		reactorList.add(redstoneCore);
		ItemStack reactor = new ItemStack(ModBlocks.redstoneReactor);
		reactorList.add(reactor);
		NonNullList<ItemStack> congealedredstone = NonNullList.create();
		congealedredstone.add(new ItemStack(ModItems.congealedRedstone));
		ItemStack redstoneBrick = new ItemStack(ModBlocks.decorativeBlock, 1, DecorativeBlockType.REDSTONE_BRICKS.getMeta());
		congealedredstone.add(redstoneBrick);
		reactorList.addAll(congealedredstone);
		NonNullList<ItemStack> reactorUpgrades = ItemUtil.getItemSubtypes(ModItems.reactorUpgrade, UpgradeType.values());
		reactorList.addAll(reactorUpgrades);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("redstonereactor", new PageCrafting("core", redstoneCore), new PageCrafting("reactor", reactor), new PageCrafting("congealedredstone", congealedredstone), new PageCrafting("upgrades", reactorUpgrades)).setDisplayObject(reactorList));
		
		ItemStack heatedsponge = new ItemStack(ModBlocks.redstoneSponge);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("heatedsponge", new PageCrafting("main", heatedsponge)).setDisplayObject(heatedsponge));

		CrystalType[] nuggetArray = new CrystalType[]{CrystalType.BLUE_NUGGET, CrystalType.RED_NUGGET, CrystalType.GREEN_NUGGET, CrystalType.DARK_NUGGET};
		CrystalType[] shardArray = new CrystalType[]{CrystalType.BLUE_SHARD, CrystalType.RED_SHARD, CrystalType.GREEN_SHARD, CrystalType.DARK_SHARD};

		NonNullList<ItemStack> shardList = ItemUtil.getItemSubtypes(ModItems.crystals, shardArray);
		NonNullList<ItemStack> crystalList = ItemUtil.getItemSubtypes(ModItems.crystals, CrystalType.BLUE, CrystalType.BLUE_NUGGET, CrystalType.BLUE_SHARD);
		NonNullList<ItemStack> nuggetList = ItemUtil.getItemSubtypes(ModItems.crystals, nuggetArray);
		GuideChapter chapterCrystals = new GuideChapter("crystals", new PageIcon("shards", shardList), new PageCrafting("craftShard", shardList), new PageIcon("nuggets", nuggetList), new PageFurnace("smeltNugget", nuggetList), new PageIcon("crystal", crystalFullList), new PageFurnace("smeltCrystal", crystalFullList)).setDisplayObject(crystalList);
		CrystalModAPI.ITEMS.registerChapter(chapterCrystals);
		
		IngotType[] ingotArray = new IngotType[]{IngotType.BLUE, IngotType.RED, IngotType.GREEN, IngotType.DARK};
		NonNullList<ItemStack> ingotList = ItemUtil.getItemSubtypes(ModItems.ingots, ingotArray);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("crystalingots", new PageIcon("0", ingotList), new PageFurnace("smelt", ingotList)).setDisplayObject(ingotList));
		
		PlateType[] plateArray = new PlateType[]{PlateType.BLUE, PlateType.RED, PlateType.GREEN, PlateType.DARK};
		NonNullList<ItemStack> plateList = ItemUtil.getItemSubtypes(ModItems.plates, plateArray);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("metalplate", new PageIcon("main", plateList), new PagePress("press", plateList)).setDisplayObject(plateList));

		NonNullList<ItemStack> darkArmorList = NonNullList.create();
		darkArmorList.add(new ItemStack(ModItems.darkIronHelmet));
		darkArmorList.add(new ItemStack(ModItems.darkIronChestplate));
		darkArmorList.add(new ItemStack(ModItems.darkIronLeggings));
		darkArmorList.add(new ItemStack(ModItems.darkIronBoots));
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("darkarmor", 
				new PageCrafting("main", darkArmorList)).setDisplayObject(darkArmorList));
		
		NonNullList<ItemStack> darkToolList = NonNullList.create();
		darkToolList.add(new ItemStack(ModItems.darkIronSword));
		darkToolList.add(new ItemStack(ModItems.darkIronPickaxe));
		darkToolList.add(new ItemStack(ModItems.darkIronShovel));
		darkToolList.add(new ItemStack(ModItems.darkIronAxe));
		darkToolList.add(new ItemStack(ModItems.darkIronHoe));
		final NonNullList<ItemStack> basicDarkTools = darkToolList;
		darkToolList.add(new ItemStack(ModItems.darkIronBow));
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("darktools", 
				new PageCrafting("main", basicDarkTools), 
				new PageCrafting("bow", new ItemStack(ModItems.darkIronBow))).setDisplayObject(darkToolList));
		
		NonNullList<ItemStack> backpacks = ItemUtil.getItemSubtypes(ModItems.normalBackpack, CrystalBackpackType.values());
		NonNullList<ItemStack> backpackUpgrades = ItemUtil.getItemSubtypes(ModItems.backpackupgrade, BackpackUpgrade.values());
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("backpacknormal", new PageCrafting("main", backpacks), new PageCrafting("upgrades", backpackUpgrades)).setDisplayObject(backpacks));

		ItemStack lock = new ItemStack(ModItems.lock);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("lock", new PageCrafting("main", lock)).setDisplayObject(lock));

		ItemStack curveddagger = new ItemStack(ModItems.dagger);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("dagger", new PageIcon("main", curveddagger)).setDisplayObject(curveddagger));
		
		ItemStack darkarang = new ItemStack(ModItems.darkarang);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("darkarang", new PageIcon("main", darkarang)).setDisplayObject(darkarang));
				
		ItemStack telePearl = new ItemStack(ModItems.telePearl);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("telepearl", new PageIcon("main", NonNullList.withSize(1, telePearl))).setDisplayObject(telePearl));
		
		ItemStack superTorch = new ItemStack(ModItems.superTorch);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("supertorch", new PageCrafting("main", superTorch)).setDisplayObject(superTorch));
		
		NonNullList<ItemStack> knowledgeBookList = NonNullList.create();
		for(IEnhancement enhancement : EnhancementManager.getEnhancements()){
        	if(enhancement.requiresKnowledge())knowledgeBookList.add(ItemEnhancementKnowledge.createItem(enhancement));
        }
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("enhancementknowledge", new PageIcon("main", knowledgeBookList), new PageText("info")).setDisplayObject(knowledgeBookList));
				
		ItemStack wings = new ItemStack(ModItems.wings);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("wings", new PageIcon("main", NonNullList.withSize(1, wings))).setDisplayObject(wings));
		
		NonNullList<ItemStack> essenceList = NonNullList.withSize(1, ItemMobEssence.createStack("Pig"));
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("mobessence", new PageIcon("main", essenceList)).setDisplayObject(essenceList));
		
		NonNullList<ItemStack> wolfArmor = NonNullList.create();
		for(WolfArmor armor : new WolfArmor[]{WolfArmor.LEATHER, WolfArmor.CHAIN, WolfArmor.IRON, WolfArmor.DIRON, WolfArmor.DIAMOND, WolfArmor.GOLD}){
			ItemStack stack = new ItemStack(ModItems.wolfArmor);
			ItemNBTHelper.setString(stack, "ArmorID", armor.name().toLowerCase());
			wolfArmor.add(stack);
		}
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("wolfarmor", new PageCrafting("main", wolfArmor)).setDisplayObject(wolfArmor));
		
		ItemStack cursedBone = new ItemStack(ModItems.cursedBone, 1, BoneType.BONE.getMetadata());
		ItemStack cursedBonemeal = new ItemStack(ModItems.cursedBone, 1, BoneType.BONEMEAL.getMetadata());
		NonNullList<ItemStack> boneList = NonNullList.create();
		boneList.add(cursedBone);
		boneList.add(cursedBonemeal);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("cursedbones", new PageIcon("bone", cursedBone), new PageCrafting("bonemeal", cursedBonemeal)).setDisplayObject(boneList));
		
		
		//Material Crops
		for(Entry<String, IMaterialCrop> entry : CrystalModAPI.getCropMap().entrySet()){
			IMaterialCrop crop = entry.getValue();
			ItemStack seed = ItemMaterialSeed.getSeed(crop);
			CropRecipe normalRecipe = CrystalModAPI.lookupRecipe(crop);
			SpecialCropRecipe specialRecipe = CrystalModAPI.lookupSpecialRecipe(crop);
			MaterialCropEditor editor = new MaterialCropEditor(crop);
			DefaultMaterialCropText defaultText = new DefaultMaterialCropText();
			DefaultMaterialCropTitle defaultTitle = new DefaultMaterialCropTitle(crop);
			if(normalRecipe !=null){
				CrystalModAPI.MATERIALCROPS.registerChapter(new GuideChapter("materialcrop."+crop.getUnlocalizedName(), new PageMaterialCropRecipe("recipe", normalRecipe).setTextEditor(editor).setTranslator(defaultText)).setTranslator(defaultTitle).setDisplayObject(seed));
			} else if(specialRecipe !=null){
				
			} else if(PageCrafting.getFirstRecipeForItem(seed) !=null){
				CrystalModAPI.MATERIALCROPS.registerChapter(new GuideChapter("materialcrop."+crop.getUnlocalizedName(), new PageCrafting("recipe", seed).setTextEditor(editor).setTranslator(defaultText)).setTranslator(defaultTitle).setDisplayObject(seed));
			} else {
				CrystalModAPI.MATERIALCROPS.registerChapter(new GuideChapter("materialcrop."+crop.getUnlocalizedName(), new PageIcon("main", seed).setTextEditor(editor).setTranslator(defaultText)).setTranslator(defaultTitle).setDisplayObject(seed));
			}
		}
		
		initEStorage();
	}
	
	public static class LookupResult {
		
		private final GuideChapter chapter;
		private final GuidePage page;
		
		public LookupResult(GuideChapter chapter, GuidePage page){
			this.chapter = chapter;
			this.page = page;
		}

		public GuideChapter getChapter() {
			return chapter;
		}

		public GuidePage getPage() {
			return page;
		}
		
	}
	
	public static LookupResult getGuideData(EntityPlayer player, Object object){
		GuideChapter finalChapter = null;
		GuidePage finalPage = null;
		all : for(GuideChapter chapter : CrystalModAPI.GUIDE_CHAPTERS){
			Map<String, List<?>> lookUpData = chapter.getLookUpData();
			for(Entry<String, List<?>> entry : lookUpData.entrySet()){
				GuidePage page = chapter.getPage(entry.getKey());
				if(page !=null){
					List<?> objects = entry.getValue();
					for(Object obj : objects){
						if(obj instanceof ItemStack){
							if(object instanceof ItemStack){
								if(ItemUtil.canCombine((ItemStack)obj, (ItemStack)object)){
									finalChapter = chapter;
									finalPage = page;
									break all;
								}
							}
						}
						if(obj instanceof String){
							if(object instanceof ItemStack){
								if(ItemUtil.itemStackMatchesOredict((ItemStack)object, (String)obj)){
									finalChapter = chapter;
									finalPage = page;
									break all;
								}
							}
						}
					}
				}
			}
		}
		
		return new LookupResult(finalChapter, finalPage);
	}
	
	public static void initEStorage(){
	}
	
	public static NonNullList<ItemStack> getEnumSpecial(Block obj, Enum<?>[] array){
		NonNullList<ItemStack> list = NonNullList.create();
		for(Enum<?> type : array){
			list.add(new ItemStack(obj, 1, type.ordinal()));
		}
		return list;
	}
	
	public static List<ItemStack> getEnumSpecial(Item obj, Enum<?>[] array){
		List<ItemStack> list = Lists.newArrayList();
		for(Enum<?> type : array){
			list.add(new ItemStack(obj, 1, type.ordinal()));
		}
		return list;
	}
	
	public static GuiComponentStandardRecipePage getRecipePage(String pageID, Object obj){
		GuiComponentStandardRecipePage recipe = GuiComponentBook.getStandardRecipeComp("guide.page."+pageID+".title", "guide.page."+pageID+".desc", obj);
		return recipe;
	}
	
	public static GuiComponentBasicItemPage getBasicPage(String pageID, Object obj){
		List<ItemStack> list = Lists.newArrayList();
		if (obj instanceof ItemStack) {
			list.add((ItemStack)obj);
		}
		if(obj instanceof List){
			@SuppressWarnings("unchecked")
			List<ItemStack> oList = (List<ItemStack>)obj;
			list.addAll(oList);
		}
		if (obj instanceof Item) {
			list.add(new ItemStack((Item)obj));
		} else if (obj instanceof Block) {
			list.add(new ItemStack((Block)obj));
		}
		GuiComponentBasicItemPage recipe = new GuiComponentBasicItemPage(Lang.localize("guide.page."+pageID+".title"), Lang.localize("guide.page."+pageID+".desc"), list);
		return recipe;
	}
	

	
	public static class ManualChapter{
		public String id;
		public String title;
		public Map<String, PageData> pages = new HashMap<String, PageData>();
	}

	public static class PageData{
		public String title;
		public String text;
	}
	public static Map<String, ManualChapter> CHAPTERTEXT = Maps.newHashMap();
	public static void loadGuideText(String lang){
		boolean useHost = Config.useRemoteManualFile;
		CHAPTERTEXT.clear();
		try {
            IResource iresource = FMLClientHandler.instance().getClient().getResourceManager().getResource(CrystalMod.resourceL("text/guide/"+lang+".txt"));
            InputStream inputstream = iresource.getInputStream();
            if(useHost){
            	ModLogger.info("Attempting to connect to manual server....");
            	if(Util.isInternetAvailable()){
            		try {
            			String branch = FileInfo.getValue("manual_branch_"+FMLClientHandler.instance().getClient().getVersion(), "master");
            			URL masterFile = new URL("https://raw.githubusercontent.com/Alec-WAM/CrystalMod/"+branch+"/hostedfiles/manual/"+lang+".txt");
            			inputstream = masterFile.openStream();
            			ModLogger.info("Online loading sucessful!");
            		} catch(Exception e){
            			e.printStackTrace();
            		}
            	} else {
            		ModLogger.info("Online loading failed! Falling back on the local guide file.");
            	}
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
            List<ManualChapter> chapters = new ArrayList<ManualChapter>();
            ManualChapter currentChapter = null;
            String currentPageID = null;
            PageData currentPage = null;
            String line = br.readLine();
            while (line != null) {
            	if (line.startsWith("{[")) {
            		if(currentChapter == null){
            			currentChapter = new ManualChapter();
            		}
            	} 
            	else if (line.startsWith(" {")) {
            		if(currentPage == null){
            			currentPage = new PageData();
            		}
            	} 
            	if(currentChapter !=null){
            		if(currentPage !=null){
            			if(line.startsWith("  [")){
            				String rest = line.substring(3);
            				int arrayEnd = line.indexOf("]");
            				String type = rest.substring(0, arrayEnd-2);
            				String value = rest.substring(arrayEnd-2);
            				if(type.startsWith("title")){
            					currentPage.title = value;
            				} else if(type.startsWith("text")){
            					currentPage.text = value;
            				}
            			}
            		} else {
            			if(line.startsWith(" [")){
            				String rest = line.substring(2);
            				int arrayEnd = line.indexOf("]");
            				String type = rest.substring(0, arrayEnd-2);
            				String value = rest.substring(arrayEnd-1);
            				if(type.startsWith("chapter")){
            					currentChapter.id = value;
            				} else if(type.startsWith("title")){
            					currentChapter.title = value;
            				} else if(type.startsWith("page")){
            					String pageId = type.substring(type.indexOf(":")+1);
            					currentPageID = pageId;
            				}
            			}
            		}
            	}
            	if (line.startsWith("]}")) {
            		if(currentChapter != null){
            			chapters.add(currentChapter);
            			currentChapter = null;
            		}
            	} else if (line.startsWith(" }")) {
            		if(currentPage !=null){
            			if(currentChapter !=null){
            				if(currentPageID !=null){
            					currentChapter.pages.put(currentPageID, currentPage);
            					currentPage = null; 
            					currentPageID = null;
            				}
            			}
            		}
            	}
            	line = br.readLine();
            }

            for(ManualChapter chapter : chapters){
            	CHAPTERTEXT.put(chapter.id, chapter);
            }
            inputstream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getTitle(GuideChapter chapter, GuidePage page){
		String title = chapter.getIndex(page) == 0 ? chapter.getLocalizedTitle() : "";
		ManualChapter mchapter = GuidePages.CHAPTERTEXT.get(chapter.getID());
		if(mchapter !=null){
			PageData data = mchapter.pages.get(page.getId());
			if(data !=null){
				if(!Strings.isNullOrEmpty(data.title))title = data.title;
			}
		}
		return title;
	}
	
	public static String getText(GuideChapter chapter, GuidePage page){
		return getText(chapter, page, true);
	}
	
	public static String getText(GuideChapter chapter, GuidePage page, boolean useTranslator){
		String lang = Lang.prefix+"guide.chapter."+chapter.getID()+".text."+page.getId();
		String text = "";
		if(I18n.canTranslate(lang))text = Lang.translateToLocal(lang);
		if(page.getTranslator() !=null && useTranslator) text = page.getTranslator().getTranslatedText();
		//Allow forced text
		ManualChapter mchapter = GuidePages.CHAPTERTEXT.get(chapter.getID());
		if(mchapter !=null){
			PageData data = mchapter.pages.get(page.getId());
			if(data !=null){
				text = data.text;
			}
		}
		text = text.replaceAll("<n>", "\n");
		if(page.getTextEditor() !=null){
			text = page.getTextEditor().editText(text);
		}
		return text;
	}
	
	public static class DefaultMaterialCropTitle implements TranslationHandler{

		private IMaterialCrop crop;
		public DefaultMaterialCropTitle(IMaterialCrop crop){
			this.crop = crop;
		}
		
		@Override
		public String getTranslatedText() {
			return CrystalModAPI.localizeCrop(crop);
		}
		
	}
	
	
	public static class DefaultMaterialCropText implements TranslationHandler{

		@Override
		public String getTranslatedText() {
			return Lang.localize("guide.text.defaultMaterialCrop");
		}
		
	}
	
	public static class MaterialCropEditor implements ITextEditor{

		public IMaterialCrop crop;
		public MaterialCropEditor(IMaterialCrop crop){
			this.crop = crop;
		}
		@Override
		public String editText(String text) {
			int secondsLeft = (crop == null) ? 0 : crop.getGrowthTime(null, null);
			int minutesLeft = secondsLeft / 60;
			int hoursLeft = minutesLeft / 60;
			int daysLeft = hoursLeft / 24;
			secondsLeft = secondsLeft % 60;
			minutesLeft = minutesLeft % 60;
			hoursLeft = hoursLeft % 24;
			String time = "";
			if(daysLeft > 0){
				time = daysLeft+"d "+hoursLeft+"h "+minutesLeft+"m "+secondsLeft+"s";
			}else if(hoursLeft > 0){
				time = hoursLeft+"h "+minutesLeft+"m "+secondsLeft+"s";
			}else if(minutesLeft > 0){
				time = minutesLeft+"m "+secondsLeft+"s";
			}else if(secondsLeft > 0){
				time = secondsLeft+"s";
			}
			String cropItemList = "Error";
			
			if(crop !=null){
				List<ItemStack> drops = crop.getDrops(null, null, crop.getMaxYield(null, null), 0);
				List<String> names = Lists.newArrayList();
				for(ItemStack stack : drops){
					names.add(stack.getDisplayName());
				}
				cropItemList = StringUtils.makeReadable(names);
			}
			
			return text.replaceAll("<growthTime>", time).replaceAll("<cropItems>", cropItemList);
		}
		
	}
}
