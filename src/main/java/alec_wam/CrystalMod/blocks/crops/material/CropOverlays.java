package alec_wam.CrystalMod.blocks.crops.material;

import alec_wam.CrystalMod.api.CrystalModAPI;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class CropOverlays {

	public static String PATH_PLANTS = "crystalmod:blocks/crop/";
	public static String PATH_PLANTS_TRUNK = PATH_PLANTS+"trunk_";
	public static String PATH_PLANTS_LEAVES = PATH_PLANTS+"leaves_";
	
	
	public static String PATH_SEEDS = "crystalmod:items/crop/";
	public static String PATH_SEEDS_STYLE = PATH_SEEDS+"seed_style_";
	
	
	public static final IPlantType PLANT_SPRUCE = CrystalModAPI.createPlantType(PATH_PLANTS_TRUNK+"spruce", PATH_PLANTS_LEAVES+"spruce");
	public static final IPlantType PLANT_SPRUCE_THICK = CrystalModAPI.createPlantType(PATH_PLANTS_TRUNK+"spruce_thick", PATH_PLANTS_LEAVES+"spruce");
	public static final IPlantType PLANT_NORMAL = CrystalModAPI.createPlantType(PATH_PLANTS_TRUNK+"normal", PATH_PLANTS_LEAVES+"normal");
	public static final IPlantType PLANT_NORMAL_THIN = CrystalModAPI.createPlantType(PATH_PLANTS_TRUNK+"normal_thin", PATH_PLANTS_LEAVES+"normal");
	public static final IPlantType PLANT_LEAVES = CrystalModAPI.createPlantType(PATH_PLANTS_TRUNK+"leaf", PATH_PLANTS_LEAVES+"leaf");
	public static final IPlantType PLANT_BLOB = CrystalModAPI.createPlantType(PATH_PLANTS_TRUNK+"blob", PATH_PLANTS_LEAVES+"blob");
	
	public static final ISeedOverlay STRIPE_LR = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"stripe_thick_lr");
	public static final ISeedOverlay WAVE_LR = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"wave_lr");
	public static final ISeedOverlay WAVE_UD = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"wave_ud");
	public static final ISeedOverlay POKADOTS = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"pokadots");
	public static final ISeedOverlay LINES_UD = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"lines_ud");
	public static final ISeedOverlay ARROW = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"arrow");
	public static final ISeedOverlay HALF_RIGHT = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"half_right");
	public static final ISeedOverlay LINES_LR_2 = CrystalModAPI.createOverlayReg(PATH_SEEDS_STYLE+"lines_lr_2");
	
	public static void registerIcons(TextureMap map){
		for(ISeedOverlay overlay : CrystalModAPI.getOverlays()){
			if(overlay.getSpite() !=null)
			map.registerSprite(overlay.getSpite());
		}
		registerTextures(map, PLANT_SPRUCE);
		registerTextures(map, PLANT_SPRUCE_THICK);
		registerTextures(map, PLANT_NORMAL);
		registerTextures(map, PLANT_NORMAL_THIN);
		registerTextures(map, PLANT_LEAVES);
		registerTextures(map, PLANT_BLOB);
	}
	
	public static void registerTextures(TextureMap map, IPlantType plant){
		map.registerSprite(plant.getRoot());
		for(ResourceLocation res : plant.getSpites()){
			map.registerSprite(res);
		}
	}
	
}
