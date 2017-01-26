package alec_wam.CrystalMod.items.guide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.guide.GuideChapter;
import alec_wam.CrystalMod.api.guide.GuideIndex;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBasicItemPage;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import alec_wam.CrystalMod.client.util.comp.GuiComponentStandardRecipePage;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.guide.page.PageCrafting;
import alec_wam.CrystalMod.items.guide.page.PageFurnace;
import alec_wam.CrystalMod.items.guide.page.PageIcon;
import alec_wam.CrystalMod.items.guide.page.PagePress;
import alec_wam.CrystalMod.items.guide.page.PageText;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.BlockEnderBuffer;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench.WorkbenchType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.Language;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuidePages implements IResourceManagerReloadListener {

	public static void createPages(){
		CrystalModAPI.GUIDE_INDEXES.clear();
		CrystalModAPI.BLOCKS = CrystalModAPI.regiterGuideIndex(new GuideIndex("blocks"));
		CrystalModAPI.ITEMS = CrystalModAPI.regiterGuideIndex(new GuideIndex("items"));
		CrystalModAPI.ENTITES = CrystalModAPI.regiterGuideIndex(new GuideIndex("entites"));
		CrystalModAPI.WORKBENCH = CrystalModAPI.regiterGuideIndex(new GuideIndex("workbench"));
		CrystalModAPI.MISC = CrystalModAPI.regiterGuideIndex(new GuideIndex("misc"));
		
		CrystalType[] crystalArray = new CrystalType[]{CrystalType.BLUE, CrystalType.RED, CrystalType.GREEN, CrystalType.DARK};
		List<ItemStack> crystalFullList = ItemUtil.getItemSubtypes(ModItems.crystals, crystalArray);
		
		List<ItemStack> oreList = ItemUtil.getBlockSubtypes(ModBlocks.crystalOre, CrystalOreType.values());
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
		NonNullList<ItemStack> glassDisplayList = NonNullList.create();
		glassDisplayList.addAll(glassList); glassDisplayList.addAll(paneList);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalglass", new PageCrafting("main", glassList), new PageCrafting("pane", paneList)).setDisplayObject(glassDisplayList));
		
		NonNullList<ItemStack> workbenchList = ItemUtil.getBlockSubtypes(ModBlocks.crystalWorkbench, WorkbenchType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalworkbench", new PageCrafting("main", workbenchList)).setDisplayObject(workbenchList));
		
		NonNullList<ItemStack> chestList = getEnumSpecial(ModBlocks.crystalChest, new CrystalChestType[]{CrystalChestType.DARKIRON, CrystalChestType.BLUE, CrystalChestType.RED, CrystalChestType.GREEN, CrystalChestType.DARK, CrystalChestType.PURE});
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalchest", new PageCrafting("main", chestList)).setDisplayObject(chestList));

		List<ItemStack> reedList = Lists.newArrayList();
		reedList.add(new ItemStack(ModItems.crystalReedsBlue));
		reedList.add(new ItemStack(ModItems.crystalReedsRed));
		reedList.add(new ItemStack(ModItems.crystalReedsGreen));
		reedList.add(new ItemStack(ModItems.crystalReedsDark));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalreeds", new PageIcon("main", reedList)).setDisplayObject(reedList));
		
		ItemStack lilyPad = new ItemStack(ModBlocks.flowerLilypad);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("flowerlilypad", new PageCrafting("main", lilyPad)).setDisplayObject(lilyPad));
		
		List<ItemStack> plantList = Lists.newArrayList();
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

		NonNullList<ItemStack> listTanks = ItemUtil.getBlockSubtypes(ModBlocks.crystalTank, TankType.BLUE, TankType.RED, TankType.GREEN, TankType.DARK);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystaltank", new PageCrafting("main", listTanks)).setDisplayObject(listTanks));

		ItemStack weather = new ItemStack(ModBlocks.weather);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("weatherforcaster", new PageCrafting("main", weather)).setDisplayObject(weather));

		ItemStack spawner = new ItemStack(ModBlocks.customSpawner);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("spawner", new PageCrafting("main", spawner), new PageText("upgrades")).setDisplayObject(spawner));

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
			ItemNBTHelper.getCompound(enderbuffer).setTag(BlockEnderBuffer.TILE_NBT_STACK, nbt);
			enderBuffers.add(enderbuffer);
		}
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("enderbuffer", new PageCrafting("main", enderBuffers)).setDisplayObject(enderBuffers));
		
		ItemStack advdispenser = new ItemStack(ModBlocks.advDispenser);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("advdispenser", new PageCrafting("main", advdispenser), new PageText("modes")).setDisplayObject(advdispenser));
		
		CrystalType[] nuggetArray = new CrystalType[]{CrystalType.BLUE_NUGGET, CrystalType.RED_NUGGET, CrystalType.GREEN_NUGGET, CrystalType.DARK_NUGGET};
		CrystalType[] shardArray = new CrystalType[]{CrystalType.BLUE_SHARD, CrystalType.RED_SHARD, CrystalType.GREEN_SHARD, CrystalType.DARK_SHARD};

		NonNullList<ItemStack> shardList = ItemUtil.getItemSubtypes(ModItems.crystals, shardArray);
		NonNullList<ItemStack> crystalList = ItemUtil.getItemSubtypes(ModItems.crystals, CrystalType.BLUE, CrystalType.BLUE_NUGGET, CrystalType.BLUE_SHARD);
		NonNullList<ItemStack> nuggetList = ItemUtil.getItemSubtypes(ModItems.crystals, nuggetArray);
		GuideChapter chapterCrystals = new GuideChapter("crystals", new PageIcon("shards", shardList), new PageCrafting("craftShard", shardList), new PageIcon("nuggets", nuggetList), new PageFurnace("smeltNugget", nuggetList), new PageIcon("crystal", crystalFullList), new PageFurnace("smeltCrystal", crystalFullList)).setDisplayObject(crystalList);
		CrystalModAPI.ITEMS.registerChapter(chapterCrystals);
		
		IngotType[] ingotArray = new IngotType[]{IngotType.BLUE, IngotType.RED, IngotType.GREEN, IngotType.DARK};
		List<ItemStack> ingotList = ItemUtil.getItemSubtypes(ModItems.ingots, ingotArray);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("crystalingots", new PageIcon("0", ingotList), new PageFurnace("smelt", ingotList)).setDisplayObject(ingotList));
		
		//TODO Move Plate Page to Press Page
		PlateType[] plateArray = new PlateType[]{PlateType.BLUE, PlateType.RED, PlateType.GREEN, PlateType.DARK};
		List<ItemStack> plateList = ItemUtil.getItemSubtypes(ModItems.plates, plateArray);
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

		ItemStack telePearl = new ItemStack(ModItems.telePearl);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("telepearl", new PageIcon("main", Collections.singletonList(telePearl))).setDisplayObject(telePearl));
		
		ItemStack superTorch = new ItemStack(ModItems.superTorch);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("supertorch", new PageCrafting("main", superTorch)).setDisplayObject(superTorch));
		
		ItemStack wings = new ItemStack(ModItems.wings);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("wings", new PageIcon("main", Collections.singletonList(wings))).setDisplayObject(wings));
		
		List<ItemStack> essenceList = Lists.newArrayList();
		essenceList.add(ItemMobEssence.createStack("Pig"));
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("mobessence", new PageIcon("main", essenceList)).setDisplayObject(essenceList));
		
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
		CHAPTERTEXT.clear();
		try {
            IResource iresource = FMLClientHandler.instance().getClient().getResourceManager().getResource(CrystalMod.resourceL("text/guide/"+lang+".txt"));
            InputStream inputstream = iresource.getInputStream();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		Language language = FMLClientHandler.instance().getClient().getLanguageManager().getCurrentLanguage();
		String lang = language.getJavaLocale().getLanguage();
		ModLogger.info("Loading guide text... ("+lang+")");
		loadGuideText(lang);
		ModLogger.info("("+CHAPTERTEXT.size()+") chapters loaded.");
	}
	
}
