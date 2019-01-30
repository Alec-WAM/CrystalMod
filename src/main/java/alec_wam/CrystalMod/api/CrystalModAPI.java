package alec_wam.CrystalMod.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.crop.CropRecipe;
import alec_wam.CrystalMod.api.crop.SpecialCropRecipe;
import alec_wam.CrystalMod.api.guide.GuideChapter;
import alec_wam.CrystalMod.api.guide.GuideIndex;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.api.pedistals.IFusionPedistal;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.api.recipe.IFusionRecipe;
import alec_wam.CrystalMod.blocks.crops.material.IMaterialCrop;
import alec_wam.CrystalMod.blocks.crops.material.IPlantType;
import alec_wam.CrystalMod.blocks.crops.material.ISeedInfo;
import alec_wam.CrystalMod.blocks.crops.material.ISeedOverlay;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CrystalModAPI {

	public static final List<GuideIndex> GUIDE_INDEXES = new ArrayList<GuideIndex>();
	public static final List<GuideChapter> GUIDE_CHAPTERS = new ArrayList<GuideChapter>();
	public static final List<GuidePage> GUIDE_PAGES = new ArrayList<GuidePage>();

	public static GuideIndex BLOCKS = null;
	public static GuideIndex ITEMS = null;
	public static GuideIndex MATERIALCROPS = null;
	public static GuideIndex ENTITES = null;
	public static GuideIndex MISC = null;
	
	public static GuideIndex regiterGuideIndex(GuideIndex index){
		GUIDE_INDEXES.add(index);
		return index;
	}
	
	public static GuideIndex getIndex(String id){
		final Iterator<GuideIndex> i = GUIDE_INDEXES.iterator();
		while(i.hasNext()){
			GuideIndex index = i.next();
			if(index !=null){
				if(index.getID() !=null && index.getID().equals(id)){
					return index;
				}
			}
		}
		return null;
	}

	//CROPS
	private static Map<String, IMaterialCrop> CROPS = Maps.newHashMap();
	private static List<ISeedOverlay> SEED_OVERLAYS = new ArrayList<ISeedOverlay>();
	private static List<CropRecipe> CROP_RECIPES = new ArrayList<CropRecipe>();
	private static List<SpecialCropRecipe> SPECIAL_CROP_RECIPES = new ArrayList<SpecialCropRecipe>();
	
	public static boolean registerCrop(IMaterialCrop crop){
		if(crop == null)return false;
		String id = crop.getUnlocalizedName();
		if(!CROPS.containsKey(id)){
			CROPS.put(id, crop);
			return true;
		}
		return false;
	}
	
	public static Map<String, IMaterialCrop> getCropMap(){
		return CROPS;
	}
	
	public static IMaterialCrop getCrop(String name) {
		return CROPS.get(name);
	}

	public static String localizeCrop(IMaterialCrop crop) {
		if(crop == null)return "null";
		if(crop.customLocalized()) return crop.getLocalizedName();
		return Lang.translateToLocal("materialcrop."+crop.getUnlocalizedName()+".name");
	}
	
	public static SpecialCropRecipe lookupSpecialRecipe(IMaterialCrop crop){
		for(SpecialCropRecipe recipe : SPECIAL_CROP_RECIPES){
			if(recipe.getOutput().getUnlocalizedName() == crop.getUnlocalizedName()){
				return recipe;
			}
		}
		return null;
	}
	
	public static void registerSpecialRecipe(SpecialCropRecipe recipe){
		SPECIAL_CROP_RECIPES.add(recipe);
	}
	
	public static SpecialCropRecipe createAndRegSpecialRecipe(IMaterialCrop input1, Object input2, IMaterialCrop output){
		SpecialCropRecipe recipe = createSpecialRecipe(input1, input2, output);
		registerSpecialRecipe(recipe);
		return recipe;
	}
	
	public static SpecialCropRecipe createSpecialRecipe(IMaterialCrop input1, Object input2, IMaterialCrop output){
		SpecialCropRecipe recipe = new SpecialCropRecipe(input1, input2, output);
		return recipe;
	}
	
	public static void registerRecipe(CropRecipe recipe){
		CROP_RECIPES.add(recipe);
	}
	
	public static CropRecipe lookupRecipe(IMaterialCrop crop){
		for(CropRecipe recipe : CROP_RECIPES){
			if(recipe.getOutput().getUnlocalizedName() == crop.getUnlocalizedName()){
				return recipe;
			}
		}
		return null;
	}
	
	public static List<CropRecipe> getRecipes(){
		return CROP_RECIPES;
	}
	
	public static List<SpecialCropRecipe> getSpecialRecipes(){
		return SPECIAL_CROP_RECIPES;
	}
	
	public static CropRecipe createAndRegRecipe(IMaterialCrop input1, IMaterialCrop input2, IMaterialCrop output){
		CropRecipe recipe = createRecipe(input1, input2, output);
		registerRecipe(recipe);
		return recipe;
	}
	
	public static CropRecipe createRecipe(IMaterialCrop input1, IMaterialCrop input2, IMaterialCrop output){
		CropRecipe recipe = new CropRecipe(input1, input2, output);
		return recipe;
	}
	
	public static ISeedOverlay createOverlayReg(String iconName){
		ISeedOverlay overlay = createOverlay(iconName);
		registerOverlay(overlay);
		return overlay;
	}
	
	public static ISeedOverlay createOverlay(final String iconName){
		ISeedOverlay overlay = new ISeedOverlay(){

			@Override
			public ResourceLocation getSpite() {
				return new ResourceLocation(iconName);
			}
			
		};
		return overlay;
	}

	public static void registerOverlay(ISeedOverlay overlay){
		SEED_OVERLAYS.add(overlay);
	}
	
	public static List<ISeedOverlay> getOverlays() {
		return SEED_OVERLAYS;
	}

	public static ISeedInfo createSeed(final int tier, final int color, final int overlayColor, final ISeedOverlay overlayType){
		ISeedInfo seed = new ISeedInfo(){

			@Override
			public int getTier() {
				return tier;
			}

			@Override
			public int getSeedColor() {
				return color;
			}

			@Override
			public int getOverlayColor() {
				return overlayColor;
			}

			@Override
			public ISeedOverlay getOverlayType() {
				return overlayType;
			}
			
		};
		return seed;
	}
	
	public static IMaterialCrop createCrop(final String name, final ISeedInfo seed, final int dropChance, final boolean useOverlayColor, final IPlantType plantStyle, final int growtime, final ItemStack cropStack){
		return createCrop(name, seed, dropChance, useOverlayColor, plantStyle, growtime, cropStack, 1, 5);
	}
	
	public static IMaterialCrop createCrop(final String name, String localized, final ISeedInfo seed, final int dropChance, final boolean useOverlayColor, final IPlantType plantStyle, final int growtime, final ItemStack cropStack){
		return createCrop(name, localized, seed, dropChance, useOverlayColor, plantStyle, growtime, cropStack, 1, 5);
	}
	
	public static IMaterialCrop createCrop(final String name, final ISeedInfo seed, final int dropChance, final boolean useOverlayColor, final IPlantType plantStyle, final int growtime, final ItemStack cropStack, final int min, final int max){
		IMaterialCrop crop = createCrop(name, "", seed, dropChance, useOverlayColor, plantStyle, growtime, cropStack, min, max);
		return crop;
	}
	
	public static IMaterialCrop createCrop(final String name, final String localized, final ISeedInfo seed, final int dropChance, final boolean useOverlayColor, final IPlantType plantStyle, final int growtime, final ItemStack cropStack, final int min, final int max){
		IMaterialCrop crop = new IMaterialCrop(){

			@Override
			public String getUnlocalizedName() {
				return name;
			}

			@Override
			public boolean customLocalized() {
				return !localized.isEmpty();
			}

			@Override
			public String getLocalizedName() {
				return localized;
			}

			@Override
			public int getGrowthTime(IBlockAccess world, BlockPos pos) {
				return growtime;
			}

			@Override
			public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, int yield, int fortune) {
				ItemStack copy = ItemUtil.copy(cropStack, yield);
				return Collections.singletonList(copy);
			}
			
			@Override
			public int getMinYield(IBlockAccess world, BlockPos pos) {
				return min;
			}
			
			@Override
			public int getMaxYield(IBlockAccess world, BlockPos pos) {
				return max;
			}

			@Override
			public ItemStack getRenderStack(IBlockAccess world, BlockPos pos) {
				return cropStack;
			}

			@Override
			public ISeedInfo getSeedInfo() {
				return seed;
			}

			@Override
			public IPlantType getPlantType() {
				return plantStyle;
			}

			@Override
			public int getPlantColor(IBlockAccess world, BlockPos pos, int pass) {
				return useOverlayColor ? seed.getOverlayColor() : seed.getSeedColor();
			}

			@Override
			public int getExtraSeedDropChance(IBlockAccess world, BlockPos pos) {
				return dropChance;
			}
			
		};
		return crop;
	}

	public static IPlantType createPlantType(final String root, final String leaves){
		IPlantType type = new IPlantType(){

			@Override
			public ResourceLocation[] getSpites() {
				ResourceLocation[] sprites = new ResourceLocation[1];
				sprites[0] = new ResourceLocation(leaves);
				return sprites;
			}

			@Override
			public ResourceLocation getRoot() {
				return new ResourceLocation(root);
			}
			
		};
		return type;
	}
	
	//Fusion
	private static final List<IFusionRecipe> FUSION_RECIPES = new ArrayList<IFusionRecipe>();

	public static IFusionRecipe registerFusion(IFusionRecipe recipe){
		FUSION_RECIPES.add(recipe);
		return recipe;
	}
	
	public static IFusionRecipe findFusionRecipe(IFusionPedistal pedistal, World world, List<IPedistal> linkedPedistals) {
		for(IFusionRecipe recipe : FUSION_RECIPES){
			if(recipe.matches(pedistal, world, linkedPedistals)){
				return recipe;
			}
		}
		return null;
	}
	
	public static List<IFusionRecipe> getFusionRecipes(){
		return FUSION_RECIPES;
	}
}
