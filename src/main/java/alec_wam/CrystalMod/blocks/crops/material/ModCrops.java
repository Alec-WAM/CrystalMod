package alec_wam.CrystalMod.blocks.crops.material;

import java.util.HashMap;
import java.util.Map;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.tiles.fusion.ModFusionRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrops {

	public static final int SECOND = 1;
	public static final int MINUTE = SECOND*60;
	public static final int HOUR = MINUTE*60;
	public static final int DAY = HOUR*24;
	public static final int WEEK = DAY*7;
	
	//Tier 1
	public static IMaterialCrop DIRT;
	public static IMaterialCrop COBBLESTONE; 
	public static IMaterialCrop SAND;
	public static IMaterialCrop GRAVEL;
	public static IMaterialCrop LOG_OAK;
	public static IMaterialCrop STRING;
	
	//TODO Liquid Crops
	
	//Tier 2
	public static IMaterialCrop STONE;
	public static IMaterialCrop FLINT;
	public static IMaterialCrop CLAY;
	public static IMaterialCrop REDSTONE;
	public static IMaterialCrop NETHERRACK;
	public static IMaterialCrop GRASS;
	public static IMaterialCrop GLASS;
	public static final IMaterialCrop LOG_SPRUCE = CrystalModAPI.createCrop("log_spruce", CrystalModAPI.createSeed(2, 0xae763a, 0x805E36, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 8*SECOND, new ItemStack(Blocks.LOG, 1, 1)); 
	public static final IMaterialCrop LOG_BIRCH = CrystalModAPI.createCrop("log_birch", CrystalModAPI.createSeed(2, 0xae763a, 0xd7cb8d, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 8*SECOND, new ItemStack(Blocks.LOG, 1, 2)); 
	public static final IMaterialCrop LOG_JUNGLE = CrystalModAPI.createCrop("log_jungle", CrystalModAPI.createSeed(2, 0xae763a, 0xb88764, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 8*SECOND, new ItemStack(Blocks.LOG, 1, 3)); 
	public static final IMaterialCrop LOG_ACACIA = CrystalModAPI.createCrop("log_acacia", CrystalModAPI.createSeed(2, 0xae763a, 0xBA683B, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 8*SECOND, new ItemStack(Blocks.LOG2, 1, 0)); 
	public static final IMaterialCrop LOG_BIG_OAK = CrystalModAPI.createCrop("log_big_oak", CrystalModAPI.createSeed(2, 0xae763a, 0x492F17, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 8*SECOND, new ItemStack(Blocks.LOG2, 1, 1)); 
	public static IMaterialCrop CACTUS;
	public static IMaterialCrop LEATHER;
	public static IMaterialCrop FEATHER;
	public static IMaterialCrop PAPER;
	
	//Tier 3
	public static IMaterialCrop GUNPOWDER;
	public static IMaterialCrop QUARTZ;
	public static IMaterialCrop COAL;
	public static final IMaterialCrop HARDENED_CLAY = CrystalModAPI.createCrop("hardened_clay", CrystalModAPI.createSeed(3, 0xcccccc, 0x915820, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 57*SECOND, new ItemStack(Blocks.HARDENED_CLAY)); 
	public static final IMaterialCrop BRICK = CrystalModAPI.createCrop("brick", CrystalModAPI.createSeed(3, 0xbf621e, 0xe8531f, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 57*SECOND, new ItemStack(Blocks.BRICK_BLOCK)); 
	public static IMaterialCrop SLIME;
	public static IMaterialCrop SNOW;
	public static IMaterialCrop EGG;
	public static IMaterialCrop NETHERWART;
	public static IMaterialCrop BOOK;
	
	//Tier 4
	public static IMaterialCrop IRON;
	public static IMaterialCrop GLOWSTONE;
	public static final IMaterialCrop NETHERBRICK = CrystalModAPI.createCrop("netherbrick", CrystalModAPI.createSeed(4, 0xbf621e, 0xe81f1f, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, 3*MINUTE + 4*SECOND, new ItemStack(Blocks.NETHER_BRICK)); 
	
	//Tier 5
	public static IMaterialCrop GOLD;
	
	//Tier 6
	public static IMaterialCrop DIAMOND;
	public static IMaterialCrop BLAZEROD;
	
	//Tier 7
	public static IMaterialCrop EMERALD;
	
	private static final Map<Integer, IMaterialCrop> DYE_CROPS = new HashMap<Integer, IMaterialCrop>();
	private static final Map<Integer, IMaterialCrop> HARDENED_CLAY_CROPS = new HashMap<Integer, IMaterialCrop>();
	private static final Map<Integer, IMaterialCrop> GLASS_CROPS = new HashMap<Integer, IMaterialCrop>();
	
	public static IMaterialCrop createHardenedClay(int damage, int color){
		EnumDyeColor dye = EnumDyeColor.byDyeDamage(damage);
		IMaterialCrop crop = CrystalModAPI.createCrop("hardened_clay_"+dye.getName().toLowerCase(), ItemUtil.getDyeName(dye) + " "+CrystalModAPI.localizeCrop(HARDENED_CLAY), CrystalModAPI.createSeed(4, 0x915820, color, CropOverlays.POKADOTS), 2, false, CropOverlays.PLANT_NORMAL, 3*MINUTE + 4*SECOND, new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, damage));
		HARDENED_CLAY_CROPS.put(damage, crop);
		return crop;
	}
	
	public static IMaterialCrop createGlassCrop(int damage, int color){
		EnumDyeColor dye = EnumDyeColor.byDyeDamage(damage);
		IMaterialCrop crop = CrystalModAPI.createCrop("glass_"+dye.getName().toLowerCase(), ItemUtil.getDyeName(dye) + " "+CrystalModAPI.localizeCrop(GLASS), CrystalModAPI.createSeed(3, 0x86D7EC, color, CropOverlays.ARROW), 2, false, CropOverlays.PLANT_NORMAL, 2*MINUTE, new ItemStack(Blocks.STAINED_GLASS, 1, 15-damage));
		GLASS_CROPS.put(damage, crop);
		return crop;
	}
	
	public static void init(){
		//TODO Add more crop types
		//FISH? SUGER COOKIE MAGMACREAM PRISMARINE CHORUS
		//Drop Crops: BONE SPIDEREYE ENDERPEARL GHASTTEAR
		DIRT = CrystalModAPI.createCrop("dirt", CrystalModAPI.createSeed(1, 0x603913, 0xa67c52, CropOverlays.STRIPE_LR), 2, true, CropOverlays.PLANT_SPRUCE, 30*SECOND, new ItemStack(Blocks.DIRT)); 
		CrystalModAPI.registerCrop(DIRT);
		COBBLESTONE = CrystalModAPI.createCrop("cobblestone", CrystalModAPI.createSeed(1, 0x808080, 0xc8c8c8, CropOverlays.LINES_LR_2), 2, true, CropOverlays.PLANT_SPRUCE, 30*SECOND, new ItemStack(Blocks.COBBLESTONE));
		CrystalModAPI.registerCrop(COBBLESTONE);
		SAND = CrystalModAPI.createCrop("sand", CrystalModAPI.createSeed(1, 0xe6d15a, 0xffffdd, CropOverlays.WAVE_LR), 2, false, CropOverlays.PLANT_SPRUCE, 30*SECOND, new ItemStack(Blocks.SAND)); 
		CrystalModAPI.registerCrop(SAND);
		GRAVEL = CrystalModAPI.createCrop("gravel", CrystalModAPI.createSeed(1, 0x817f7f, 0x5b5855, CropOverlays.WAVE_LR), 2, true, CropOverlays.PLANT_SPRUCE, 30*SECOND, new ItemStack(Blocks.GRAVEL)); 
		CrystalModAPI.registerCrop(GRAVEL);
		LOG_OAK = CrystalModAPI.createCrop("log_oak", CrystalModAPI.createSeed(1, 0xae763a, 0xbc9862, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE, new ItemStack(Blocks.LOG, 1, 0)); 
		CrystalModAPI.registerCrop(LOG_OAK);
		for(int d = 0; d < 16; d++){
			EnumDyeColor color = EnumDyeColor.byDyeDamage(d);
			
			IMaterialCrop dyeCrop = createDyeCrop(color, 1, 2, MINUTE);
			if(color == EnumDyeColor.BLUE){
				dyeCrop = createDyeCrop(color, 1, 5, 3*MINUTE);
			}
			if(color == EnumDyeColor.BROWN){
				dyeCrop = createDyeCrop(color, 2, 5, 2*MINUTE);
			}
			if(color == EnumDyeColor.PURPLE || color == EnumDyeColor.CYAN || color == EnumDyeColor.GRAY || color == EnumDyeColor.SILVER || color == EnumDyeColor.PINK || color == EnumDyeColor.LIME || color == EnumDyeColor.LIGHT_BLUE || color == EnumDyeColor.ORANGE){
				dyeCrop = createDyeCrop(color, 2, 2, MINUTE);
			}
			if(color == EnumDyeColor.MAGENTA){
				dyeCrop = createDyeCrop(color, 3, 2, MINUTE);
			}
			DYE_CROPS.put(d, dyeCrop);
			CrystalModAPI.registerCrop(dyeCrop);
		}
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.RED.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.GREEN.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.BROWN.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.RED.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.BLUE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.PURPLE.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.GREEN.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.BLUE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.CYAN.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.BLACK.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.GRAY.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.GRAY.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.SILVER.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.RED.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.PINK.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.GREEN.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.LIME.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.BLUE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.LIGHT_BLUE.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.PURPLE.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.PINK.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.MAGENTA.getDyeDamage()));
		CrystalModAPI.createAndRegRecipe(DYE_CROPS.get(EnumDyeColor.RED.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.YELLOW.getDyeDamage()), DYE_CROPS.get(EnumDyeColor.ORANGE.getDyeDamage()));
		STRING = CrystalModAPI.createCrop("string", CrystalModAPI.createSeed(1, 0xffffff, 0xdbdbdb, CropOverlays.LINES_UD), 5, true, CropOverlays.PLANT_SPRUCE, MINUTE, new ItemStack(Items.STRING));
		CrystalModAPI.registerCrop(STRING);
		
		STONE = CrystalModAPI.createCrop("stone", CrystalModAPI.createSeed(2, 0x808080, 0xc8c8c8, CropOverlays.HALF_RIGHT), 2, true, CropOverlays.PLANT_SPRUCE_THICK, MINUTE, new ItemStack(Blocks.STONE));
		CrystalModAPI.registerCrop(STONE);
		FLINT = CrystalModAPI.createCrop("flint", CrystalModAPI.createSeed(2, 0x333333, 0x161616, CropOverlays.ARROW), 2, true, CropOverlays.PLANT_NORMAL, MINUTE, new ItemStack(Items.FLINT));
		CrystalModAPI.registerCrop(FLINT);
		CrystalModAPI.createAndRegRecipe(GRAVEL, COBBLESTONE, FLINT);
		CLAY = CrystalModAPI.createCrop("clay", CrystalModAPI.createSeed(2, 0xcccccc, 0xdcdcdc, CropOverlays.POKADOTS), 2, true, CropOverlays.PLANT_NORMAL, MINUTE + 8*SECOND, new ItemStack(Items.CLAY_BALL)); 
		CrystalModAPI.registerCrop(CLAY);
		CrystalModAPI.createAndRegRecipe(SAND, DIRT, CLAY);
		REDSTONE = CrystalModAPI.createCrop("redstone", CrystalModAPI.createSeed(2, 0x720000, 0x490000, CropOverlays.STRIPE_LR), 2, false, CropOverlays.PLANT_NORMAL, 3*MINUTE, new ItemStack(Items.REDSTONE)); 
		CrystalModAPI.registerCrop(REDSTONE);
		CrystalModAPI.createAndRegRecipe(SAND, DYE_CROPS.get(EnumDyeColor.RED.getDyeDamage()), REDSTONE);
		NETHERRACK = CrystalModAPI.createCrop("netherrack", CrystalModAPI.createSeed(2, 0xD63E3E, 0x853030, CropOverlays.LINES_LR_2), 2, true, CropOverlays.PLANT_SPRUCE, MINUTE + 8*SECOND, new ItemStack(Blocks.NETHERRACK)); 
		CrystalModAPI.registerCrop(NETHERRACK);
		GLASS = CrystalModAPI.createCrop("glass", CrystalModAPI.createSeed(2, 0x86D7EC, 0x86D7EC, CropOverlays.ARROW), 2, true, CropOverlays.PLANT_LEAVES, MINUTE, new ItemStack(Blocks.GLASS)); 
		CrystalModAPI.registerCrop(GLASS);
		GRASS = CrystalModAPI.createCrop("grass", CrystalModAPI.createSeed(2, 0x499D2D, 0x87D959, CropOverlays.LINES_UD), 2, true, CropOverlays.PLANT_SPRUCE_THICK, 2*MINUTE, new ItemStack(Blocks.GRASS)); 
		CrystalModAPI.registerCrop(GRASS);
		CrystalModAPI.createAndRegRecipe(DIRT, DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage()), GRASS);
		CrystalModAPI.registerCrop(LOG_SPRUCE);
		CrystalModAPI.registerCrop(LOG_BIRCH);
		CrystalModAPI.registerCrop(LOG_JUNGLE);
		CrystalModAPI.registerCrop(LOG_ACACIA);
		CrystalModAPI.registerCrop(LOG_BIG_OAK);
		LEATHER = CrystalModAPI.createCrop("leather", CrystalModAPI.createSeed(2, 0xc65c35, 0x9e492a, CropOverlays.LINES_UD), 5, false, CropOverlays.PLANT_NORMAL, 3*MINUTE, new ItemStack(Items.LEATHER));
		CrystalModAPI.registerCrop(LEATHER);
		CrystalModAPI.createAndRegRecipe(STRING, DYE_CROPS.get(EnumDyeColor.BROWN.getDyeDamage()), LEATHER);
		FEATHER = CrystalModAPI.createCrop("feather", CrystalModAPI.createSeed(2, 0xd3d3d3, 0x898989, CropOverlays.ARROW), 5, false, CropOverlays.PLANT_LEAVES, 3*MINUTE, new ItemStack(Items.FEATHER));
		CrystalModAPI.registerCrop(FEATHER);
		CrystalModAPI.createAndRegSpecialRecipe(STRING, new ItemStack(Items.STICK), FEATHER);
		PAPER = CrystalModAPI.createCrop("paper", CrystalModAPI.createSeed(2, 0xd6d6d6, 0x515151, CropOverlays.STRIPE_LR), 5, false, CropOverlays.PLANT_NORMAL_THIN, 5*MINUTE, new ItemStack(Items.PAPER));
		CrystalModAPI.registerCrop(PAPER);
		CrystalModAPI.createAndRegRecipe(LOG_OAK, STONE, PAPER);
		
		CACTUS = CrystalModAPI.createCrop("cactus", CrystalModAPI.createSeed(3, 0x128620, 0x000000, CropOverlays.LINES_UD), 4, false, CropOverlays.PLANT_LEAVES, 2*MINUTE, new ItemStack(Blocks.CACTUS));
		CrystalModAPI.registerCrop(CACTUS);
		CrystalModAPI.createAndRegRecipe(SAND, GRASS, CACTUS);
		GUNPOWDER = CrystalModAPI.createCrop("gunpowder", CrystalModAPI.createSeed(3, 0x727272, 0x494949, CropOverlays.STRIPE_LR), 2, false, CropOverlays.PLANT_NORMAL, 2*MINUTE, new ItemStack(Items.GUNPOWDER));
		CrystalModAPI.registerCrop(GUNPOWDER);
		CrystalModAPI.createAndRegRecipe(SAND, FLINT, GUNPOWDER);
		QUARTZ = CrystalModAPI.createCrop("quartz", CrystalModAPI.createSeed(3, 0xD63E3E, 0xffffff, CropOverlays.LINES_LR_2), 2, true, CropOverlays.PLANT_NORMAL, 3*MINUTE, new ItemStack(Items.QUARTZ)); 
		CrystalModAPI.registerCrop(QUARTZ);
		CrystalModAPI.createAndRegRecipe(NETHERRACK, FLINT, QUARTZ);
		COAL = CrystalModAPI.createCrop("coal", CrystalModAPI.createSeed(3, 0x1b1b1b, 0x2c2c2c, CropOverlays.HALF_RIGHT), 2, false, CropOverlays.PLANT_NORMAL, 3*MINUTE, new ItemStack(Items.COAL)); 
		CrystalModAPI.registerCrop(COAL);
		CrystalModAPI.createAndRegRecipe(STONE, FLINT, COAL);
		int[] glassColors = new int[]{0x171717, 0x983232, 0x657F32, 0x654C32, 0x324CB1, 0x7F3FB1, 0x4C7F98, 0x989898, 0x4C4C4C, 0xF17FA5, 0x7FCB19, 0xE4E432, 0x6598D7, 0xB14CD7, 0xD77F32, 0xFEFEFE};
		for(int d = 0; d < 16; d++){
			IMaterialCrop glassCrop = createGlassCrop(d, glassColors[d]);
			GLASS_CROPS.put(d, glassCrop);
			CrystalModAPI.registerCrop(glassCrop);
			CrystalModAPI.createAndRegRecipe(GLASS, DYE_CROPS.get(d), glassCrop);
		}
		CrystalModAPI.registerCrop(HARDENED_CLAY);
		CrystalModAPI.registerCrop(BRICK);
		SLIME = CrystalModAPI.createCrop("slime", CrystalModAPI.createSeed(3, 0x508049, 0x82c873, CropOverlays.POKADOTS), 5, false, CropOverlays.PLANT_LEAVES, 5*MINUTE, new ItemStack(Items.SLIME_BALL));
		CrystalModAPI.registerCrop(SLIME);
		CrystalModAPI.createAndRegRecipe(CLAY, DYE_CROPS.get(EnumDyeColor.LIME.getDyeDamage()), SLIME);
		SNOW = CrystalModAPI.createCrop("snow", CrystalModAPI.createSeed(3, 0xeeffff, 0x9aa4a4, CropOverlays.POKADOTS), 5, false, CropOverlays.PLANT_LEAVES, 3*MINUTE, new ItemStack(Items.SNOWBALL));
		CrystalModAPI.registerCrop(SNOW);
		CrystalModAPI.createAndRegRecipe(CLAY, DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage()), SNOW);
		EGG = CrystalModAPI.createCrop("egg", CrystalModAPI.createSeed(3, 0xdfce9b, 0xb3a57d, CropOverlays.POKADOTS), 5, false, CropOverlays.PLANT_NORMAL, 5*MINUTE, new ItemStack(Items.EGG));
		CrystalModAPI.registerCrop(EGG);
		CrystalModAPI.createAndRegRecipe(CLAY, FEATHER, EGG);
		NETHERWART = CrystalModAPI.createCrop("netherwart", CrystalModAPI.createSeed(3, 0xa62530, 0xb5c151a, CropOverlays.POKADOTS), 8, false, CropOverlays.PLANT_NORMAL, 7*MINUTE, new ItemStack(Items.NETHER_WART));
		CrystalModAPI.registerCrop(NETHERWART);
		CrystalModAPI.createAndRegRecipe(NETHERRACK, DYE_CROPS.get(EnumDyeColor.BROWN.getDyeDamage()), NETHERWART);
		BOOK = CrystalModAPI.createCrop("book", CrystalModAPI.createSeed(3, 0xb7b7b7, 0x654b17, CropOverlays.HALF_RIGHT), 10, true, CropOverlays.PLANT_NORMAL, 10*MINUTE, new ItemStack(Items.BOOK), 1, 3);
		CrystalModAPI.registerCrop(BOOK);
		CrystalModAPI.createAndRegRecipe(LEATHER, PAPER, BOOK);
		
		CrystalModAPI.registerCrop(NETHERBRICK);
		IRON = CrystalModAPI.createCrop("iron", CrystalModAPI.createSeed(4, 0x969696, 0xd8d8d8, CropOverlays.HALF_RIGHT), 5, false, CropOverlays.PLANT_NORMAL, 5*MINUTE, new ItemStack(Items.IRON_INGOT), 1, 3); 
		CrystalModAPI.registerCrop(IRON);
		CrystalModAPI.createAndRegRecipe(COAL, DYE_CROPS.get(EnumDyeColor.GRAY.getDyeDamage()), IRON);
		GLOWSTONE = CrystalModAPI.createCrop("glowstone", CrystalModAPI.createSeed(4, 0x868600, 0xd2d200, CropOverlays.STRIPE_LR), 5, true, CropOverlays.PLANT_NORMAL, 3*MINUTE, new ItemStack(Items.GLOWSTONE_DUST)); 
		CrystalModAPI.registerCrop(GLOWSTONE);
		CrystalModAPI.createAndRegRecipe(QUARTZ, DYE_CROPS.get(EnumDyeColor.YELLOW.getDyeDamage()), GLOWSTONE);
		
		GOLD = CrystalModAPI.createCrop("gold", CrystalModAPI.createSeed(5, 0xffff8b, 0xdede00, CropOverlays.HALF_RIGHT), 10, true, CropOverlays.PLANT_NORMAL, 10*MINUTE, new ItemStack(Items.GOLD_INGOT), 1, 2); 
		CrystalModAPI.registerCrop(GOLD);
		CrystalModAPI.createAndRegRecipe(IRON, DYE_CROPS.get(EnumDyeColor.YELLOW.getDyeDamage()), GOLD);
		
		DIAMOND = CrystalModAPI.createCrop("diamond", CrystalModAPI.createSeed(6, 0x2ccdb1, 0x8cf4e2, CropOverlays.HALF_RIGHT), 20, false, CropOverlays.PLANT_NORMAL, 20*MINUTE, new ItemStack(Items.DIAMOND), 1, 2); 
		CrystalModAPI.registerCrop(DIAMOND);
		CrystalModAPI.createAndRegRecipe(GOLD, GLASS, DIAMOND);
		BLAZEROD = CrystalModAPI.createCrop("blazerod", CrystalModAPI.createSeed(6, 0xb9931c, 0xae3c00, CropOverlays.LINES_UD), 10, false, CropOverlays.PLANT_NORMAL, 10*MINUTE, new ItemStack(Items.BLAZE_ROD), 1, 3); 
		CrystalModAPI.registerCrop(BLAZEROD);
		CrystalModAPI.createAndRegRecipe(NETHERRACK, GOLD, BLAZEROD);
		
		EMERALD = CrystalModAPI.createCrop("emerald", CrystalModAPI.createSeed(7, 0x00b038, 0x17dd62, CropOverlays.HALF_RIGHT), 25, false, CropOverlays.PLANT_NORMAL, 25*MINUTE, new ItemStack(Items.EMERALD), 1, 2); 
		CrystalModAPI.registerCrop(EMERALD);
		CrystalModAPI.createAndRegRecipe(DIAMOND, DYE_CROPS.get(EnumDyeColor.GREEN.getDyeDamage()), EMERALD);
		
		createColorBlocks();
		for(int d = 0; d < 16; d++){
			CrystalModAPI.registerCrop(HARDENED_CLAY_CROPS.get(d));
			CrystalModAPI.createAndRegRecipe(HARDENED_CLAY, DYE_CROPS.get(d), HARDENED_CLAY_CROPS.get(d));
		}
	}
	
	public static void createColorBlocks(){
		/*HARDENED_CLAY_BLACK*/createHardenedClay(0, 0x251610); 
		/*HARDENED_CLAY_RED*/createHardenedClay(1, 0x8E3B2E); 
		/*HARDENED_CLAY_GREEN*/createHardenedClay(2, 0x4B5229); 
		/*HARDENED_CLAY_BROWN*/createHardenedClay(3, 0x4D3223); 
		/*HARDENED_CLAY_BLUE*/createHardenedClay(4, 0x4A3B5A);
		/*HARDENED_CLAY_PURPLE*/createHardenedClay(5, 0x764554);
		/*HARDENED_CLAY_CYAN*/createHardenedClay(6, 0x565A5A);
		/*HARDENED_CLAY_SILVER*/createHardenedClay(7, 0x876A60);
		/*HARDENED_CLAY_GREY*/createHardenedClay(8, 0x392A23);
		/*HARDENED_CLAY_PINK*/createHardenedClay(9, 0xA04D4D);
		/*HARDENED_CLAY_LIME*/createHardenedClay(10, 0x677433);
		/*HARDENED_CLAY_YELLOW*/createHardenedClay(11, 0xB98322);
		/*HARDENED_CLAY_LIGHT_BLUE*/createHardenedClay(12, 0x706C89);
		/*HARDENED_CLAY_MAGENTA*/createHardenedClay(13, 0x95566B);
		/*HARDENED_CLAY_ORANGE*/createHardenedClay(14, 0xA05224);
		/*HARDENED_CLAY_WHITE*/createHardenedClay(15, 0xD1B1A0);
	}
	
	public static IMaterialCrop createDyeCrop(EnumDyeColor color, int tier, int drop, int growtime){
		int seedColor = color.getMapColor().colorValue;
		ItemStack dyeStack = new ItemStack(Items.DYE, 1, color.getDyeDamage());
		return CrystalModAPI.createCrop("dye_"+color.getName(), dyeStack.getDisplayName(), CrystalModAPI.createSeed(tier, seedColor, seedColor, CropOverlays.HALF_RIGHT), drop, false, CropOverlays.PLANT_BLOB, growtime, dyeStack);
	}
	
	public static void recipes(){
		boolean useInfusion = Config.hardmode_MaterialCrops;
		
		if(useInfusion){
			Vec3d colorGreen = new Vec3d(0, 124, 16);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(DIRT), new Object[] {new ItemStack(Blocks.DIRT)}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(COBBLESTONE), new Object[] {"cobblestone"}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(SAND), new Object[] {"sand"}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(GRAVEL), new Object[] {"gravel"}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(LOG_OAK), new Object[] {new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata())}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(LOG_SPRUCE), new Object[] {new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(LOG_BIRCH), new Object[] {new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata())}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(LOG_JUNGLE), new Object[] {new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(LOG_ACACIA), new Object[] {new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4)}, colorGreen);
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(LOG_BIG_OAK), new Object[] {new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4)}, colorGreen);
			for(EnumDyeColor color : new EnumDyeColor[]{EnumDyeColor.WHITE, EnumDyeColor.YELLOW, EnumDyeColor.BLUE, EnumDyeColor.GREEN, EnumDyeColor.RED, EnumDyeColor.BLACK}){
				float[] array = EntitySheep.getDyeRgb(color);
				Vec3d dyeColor = new Vec3d(array[0], array[1], array[2]);
				ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(DYE_CROPS.get(color.getDyeDamage())), new Object[] {new ItemStack(Items.DYE, 1, color.getDyeDamage())}, dyeColor);
			}
			ModFusionRecipes.addRecipe(new ItemStack(Items.WHEAT_SEEDS), ItemMaterialSeed.getSeed(STRING), new Object[] {"woolWhite"}, colorGreen);
		} else {
			ModCrafting.addShapelessRecipe(ItemMaterialSeed.getSeed(DIRT), new Object[]{Items.WHEAT_SEEDS, Blocks.DIRT});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(COBBLESTONE), new Object[]{Items.WHEAT_SEEDS, "cobblestone"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(SAND), new Object[]{Items.WHEAT_SEEDS, "sand"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(GRAVEL), new Object[]{Items.WHEAT_SEEDS, "gravel"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(LOG_OAK), new Object[]{Items.WHEAT_SEEDS, new ItemStack(Blocks.LOG, 1, 0)});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(LOG_SPRUCE), new Object[]{ItemMaterialSeed.getSeed(LOG_OAK), new ItemStack(Blocks.LOG, 1, 1)});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(LOG_BIRCH), new Object[]{ItemMaterialSeed.getSeed(LOG_OAK), new ItemStack(Blocks.LOG, 1, 2)});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(LOG_JUNGLE), new Object[]{ItemMaterialSeed.getSeed(LOG_OAK), new ItemStack(Blocks.LOG, 1, 3)});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(LOG_ACACIA), new Object[]{ItemMaterialSeed.getSeed(LOG_OAK), new ItemStack(Blocks.LOG2, 1, 0)});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(LOG_BIG_OAK), new Object[]{ItemMaterialSeed.getSeed(LOG_OAK), new ItemStack(Blocks.LOG2, 1, 1)});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(DYE_CROPS.get(EnumDyeColor.WHITE.getDyeDamage())), new Object[]{Items.WHEAT_SEEDS, "dyeWhite"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(DYE_CROPS.get(EnumDyeColor.YELLOW.getDyeDamage())), new Object[]{Items.WHEAT_SEEDS, "dyeYellow"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(DYE_CROPS.get(EnumDyeColor.BLUE.getDyeDamage())), new Object[]{Items.WHEAT_SEEDS, "dyeBlue"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(DYE_CROPS.get(EnumDyeColor.GREEN.getDyeDamage())), new Object[]{Items.WHEAT_SEEDS, "dyeGreen"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(DYE_CROPS.get(EnumDyeColor.RED.getDyeDamage())), new Object[]{Items.WHEAT_SEEDS, "dyeRed"});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(DYE_CROPS.get(EnumDyeColor.BLACK.getDyeDamage())), new Object[]{Items.WHEAT_SEEDS, new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage())});
			ModCrafting.addShapelessOreRecipe(ItemMaterialSeed.getSeed(STRING), new Object[]{Items.WHEAT_SEEDS, "woolWhite"});
		}

		
		ModCrafting.addShapelessRecipe(ItemMaterialSeed.getSeed(STONE), new Object[]{Items.COAL, ItemMaterialSeed.getSeed(COBBLESTONE)});
		ModCrafting.addShapelessRecipe(ItemMaterialSeed.getSeed(GLASS), new Object[]{Items.COAL, ItemMaterialSeed.getSeed(SAND)});
		ModCrafting.addShapelessRecipe(ItemMaterialSeed.getSeed(HARDENED_CLAY), new Object[]{Items.COAL, ItemMaterialSeed.getSeed(CLAY)});
		
		
		CrystalInfusionManager.addRecipe(ItemMaterialSeed.getSeed(COBBLESTONE), new FluidStack(FluidRegistry.LAVA, 100), ItemMaterialSeed.getSeed(NETHERRACK), 1600);
	}
}
