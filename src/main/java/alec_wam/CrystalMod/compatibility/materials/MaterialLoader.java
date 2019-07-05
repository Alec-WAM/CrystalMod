package alec_wam.CrystalMod.compatibility.materials;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.compatibility.materials.ItemMaterial.DustRecipe;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.press.PressRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry.MissingFactory;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

public class MaterialLoader {
	public static final int PATH_PREFIX_LENGTH = "materials/".length();
	public static final int PATH_SUFFIX_LENGTH = ".json".length();

    public static final ResourceLocation MATERIAL_REGISTRY_ID = new ResourceLocation("crystalmod:materials");
	static {
		makeRegistry(MATERIAL_REGISTRY_ID, ItemMaterial.class).create();
	}
	
	public static final IForgeRegistry<ItemMaterial> ITEM_MATERIALS = RegistryManager.ACTIVE.getRegistry(ItemMaterial.class);
	
	public static final Map<String, Item> DUST_ITEMS = Maps.newHashMap();
	public static final Map<String, Item> PLATE_ITEMS = Maps.newHashMap();

	public static void registerMaterialItems(IForgeRegistry<Item> registry) {
		for(ItemMaterial material : ITEM_MATERIALS.getValues()){
			String materialName = material.getRegistryName().getPath();
			if(material.hasDust()){
				Item item = new Item(new Item.Properties().group(ItemGroup.MATERIALS)) {
					@Override
					public ITextComponent getDisplayName(ItemStack stack) {
						return new TranslationTextComponent("item.crystalmod.dust", Lang.translateToLocal("material."+materialName));
					} 
				};
				item.setRegistryName(CrystalMod.resourceL("dust_"+materialName));
				DUST_ITEMS.put(materialName, item);
				registry.register(item);
			}
			if(material.hasPlate()){
				Item item = new Item(new Item.Properties().group(ItemGroup.MATERIALS)) {
					@Override
					public ITextComponent getDisplayName(ItemStack stack) {
						return new TranslationTextComponent("item.crystalmod.plate", Lang.translateToLocal("material."+materialName));
					} 
				};
				item.setRegistryName(CrystalMod.resourceL("plate_"+materialName));
				PLATE_ITEMS.put(materialName, item);
				registry.register(item);
			}
		}
	}
	
	public static Item getDustItem(ItemMaterial mat){
		return getDustItem(mat.getRegistryName().getPath());
	}
	
	public static Item getDustItem(String name){
		return DUST_ITEMS.get(name);
	}
	
	public static Item getPlateItem(ItemMaterial mat){
		return getPlateItem(mat.getRegistryName().getPath());
	}
	
	public static Item getPlateItem(String name){
		return PLATE_ITEMS.get(name);
	}
	
	public static ItemMaterial getMaterial(String name){
		for(ItemMaterial mat : ITEM_MATERIALS.getValues()){
			if(mat.getRegistryName().getPath().equals(name)){
				return mat;
			}
		}
		return null;
	}
	
	public static void loadCustomMaterials(){	
		URL url = Thread.currentThread().getContextClassLoader().getResource("data/crystalmod");
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if(file !=null){
			int count2 = loadCustomMaterials("default", file);
			CrystalMod.LOGGER.info("Loaded {} default materials", count2);
		}
		
		File configFolder = FMLPaths.CONFIGDIR.get().toFile();
		int count = loadCustomMaterials("custom", configFolder);		
		CrystalMod.LOGGER.info("Loaded {} custom materials", count);
	}
	
	private static int loadCustomMaterials(String namespace, File file){
		int count = 0;
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		if(!file.exists()){
			CrystalMod.LOGGER.error("Could not find {}", file);
			return 0;
		}
		File materials = new File(file, "materials");
		if(materials.exists() && materials.isDirectory()){
			File[] files = materials.listFiles();
			
			
			for(File entry : files) {
				if(!entry.getName().endsWith(".json"))continue;
				String s = entry.getName();
				String fixed = s.substring(0, s.length() - PATH_SUFFIX_LENGTH);
				ResourceLocation resourcelocation1 = new ResourceLocation(CrystalMod.MODID, fixed);
				InputStreamReader input = null;
				try {
					input = new FileReader(entry);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					JsonObject jsonobject = JSONUtils.fromJson(gson, input, JsonObject.class);
					if (jsonobject == null) {
						CrystalMod.LOGGER.error("Couldn't load material {} as it's null or empty", (Object)resourcelocation1);
					} else {
						ItemMaterial mat = MaterialSerializer.read(resourcelocation1, jsonobject);
						if(ITEM_MATERIALS.containsKey(resourcelocation1)){
							CrystalMod.LOGGER.error("Duplicate {} material trying to be registered {}", namespace, resourcelocation1);
						} else {
							CrystalMod.LOGGER.info("Loading {} {} material", resourcelocation1, namespace);
							ITEM_MATERIALS.register(mat);
							count++;
						}
					}					
				} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
					CrystalMod.LOGGER.error("Parsing error loading {} material {}", namespace, resourcelocation1, jsonparseexception);
				} finally {
					if(input !=null)
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
			
		} else {
			CrystalMod.LOGGER.error("Could not find material folder {}", materials);
		}
		return count;
	}
	
	public static void generateMaterialRecipes(){
		RecipeManager recipeManager = ModRecipes.getRecipeManager();
		if(recipeManager == null)return;
		int dustRecipes = 0;
		int plateRecipes = 0;
		int dustSmeltRecipes = 0;
		CrystalMod.LOGGER.info("Generating ItemMaterial Recipes...");	
		for(ItemMaterial material : MaterialLoader.ITEM_MATERIALS.getValues()){
			try{
				if(material.hasDust()){
					Item dustItem = MaterialLoader.getDustItem(material);
					for(DustRecipe recipe : material.getDustRecipes()){
						ResourceLocation id = new ResourceLocation("crystalmod_grinder_dust", "dust/" + dustItem.getRegistryName().getPath().toLowerCase()+"_"+recipe.getName());
						if(recipeManager.getRecipe(id).isPresent()){
							continue;
						}
						Ingredient input = recipe.getIngredient();
						GrinderRecipe grinderRecipe = new GrinderRecipe(id, "", input, new ItemStack(dustItem, recipe.getCount()), ItemStackTools.getEmptyStack(), 0.0f, recipe.getEnergy());
						ModRecipes.addRecipeToManager(grinderRecipe);
						dustRecipes++;
					}
					ResourceLocation furnaceID = new ResourceLocation("crystalmod", "dust/" + "smelt_"+dustItem.getRegistryName().getPath().toLowerCase());
					if(/*!recipeManager.func_215367_a(furnaceID).isPresent() && */ItemStackTools.isValid(material.getDustSmeltOutput())){
						Ingredient input = Ingredient.fromItems(dustItem);
						FurnaceRecipe furnaceRecipe = new FurnaceRecipe(furnaceID, "crystalmod:dust_smelt", input, material.getDustSmeltOutput(), 0.0f, 200);
						ModRecipes.addRecipeToManager(furnaceRecipe);
						dustSmeltRecipes++;
					}
				}
				if(material.hasPlate()){
					Item plateItem = MaterialLoader.getPlateItem(material);
					if(material.getPlateIngredient() !=null){
						ResourceLocation id = new ResourceLocation("crystalmod_press_plate", "plate/" + plateItem.getRegistryName().getPath().toLowerCase());
						if(recipeManager.getRecipe(id).isPresent()){
							continue;
						}
						Ingredient input = material.getPlateIngredient();
						PressRecipe pressRecipe = new PressRecipe(id, "", input, new ItemStack(plateItem), 1600);
						ModRecipes.addRecipeToManager(pressRecipe);
						plateRecipes++;
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		CrystalMod.LOGGER.info("Generated " + dustRecipes + " dust recipes");	
		CrystalMod.LOGGER.info("Generated " + dustSmeltRecipes + " dust smelt recipes");	
		CrystalMod.LOGGER.info("Generated " + plateRecipes + " plate recipes");	
	}
	
	public static class DefaultMissing<T extends IForgeRegistryEntry<T>> implements MissingFactory<T> {

		@Override
		public T createMissing(ResourceLocation key, boolean isNetwork) {
			return null;
		}
		
	}
	
	public static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type)
    {
        return new RegistryBuilder<T>().setName(name).disableSaving().setType(type).setMaxID(Integer.MAX_VALUE - 1).set(new DefaultMissing<T>());
    }
	
}
