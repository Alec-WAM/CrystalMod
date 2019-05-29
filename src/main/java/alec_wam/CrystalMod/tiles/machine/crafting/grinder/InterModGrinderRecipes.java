package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemTagHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
/**
 * Generates Grinder recipes using the new Tag system in Forge and scans for ingot -> dust conversions etc.
 * @author Alec_WAM
 *
 */
@SuppressWarnings("deprecation")
public class InterModGrinderRecipes implements IResourceManagerReloadListener {

	public static final InterModGrinderRecipes INSTANCE = new InterModGrinderRecipes();
	
	public void generateRecipes(){
		CrystalMod.LOGGER.info("Generating Grinder recipes...");
		int size = 0;
		boolean debugRecipes = ModConfig.GENERAL.Debug_Grinder_Recipes.get();
		
		List<Item> ores = new ArrayList<Item>(Tags.Items.ORES.getAllElements());
		for(Item item : ores){
			List<ResourceLocation> tags = ItemTagHelper.getTags(item);
			for(ResourceLocation tag : tags){
				if(tag.equals(Tags.Items.ORES.getId()))continue;
				try{
					String id = getTagID(tag, "ores/");
					ResourceLocation dustTag = new ResourceLocation("forge", "dusts/"+id);
					//Ore -> 2xDust
					if(ItemTagHelper.tagExists(dustTag)){
						if(debugRecipes)System.out.println("Ore -> 2xDust: " + id);
						size+= addRecipesForTag("ores", tag, dustTag, 2, 3200);
					}
					else {
						ResourceLocation gemTag = createTag("forge", "gems/", id);
						//Ore -> 2xGem
						if(ItemTagHelper.tagExists(gemTag)){
							if(debugRecipes)System.out.println("Ore -> 2xGem: " + id);
							size+= addRecipesForTag("ores", tag, gemTag, 2, 3200);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		List<Item> ingots = new ArrayList<Item>(Tags.Items.INGOTS.getAllElements());
		for(Item item : ingots){
			List<ResourceLocation> tags = ItemTagHelper.getTags(item);
			for(ResourceLocation tag : tags){
				if(tag.equals(Tags.Items.INGOTS.getId()))continue;
				try{
					String id = getTagID(tag, "ingots/");
					ResourceLocation dustTag = new ResourceLocation("forge", "dusts/"+id);
					//Ingot -> Dust
					if(ItemTagHelper.tagExists(dustTag)){
						if(debugRecipes)System.out.println("Ingot -> Dust: " + id);
						size+= addRecipesForTag("ingots", tag, dustTag, 1, 2400);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		List<Item> gems = new ArrayList<Item>(Tags.Items.GEMS.getAllElements());
		for(Item item : gems){
			List<ResourceLocation> tags = ItemTagHelper.getTags(item);
			for(ResourceLocation tag : tags){
				if(tag.equals(Tags.Items.GEMS.getId()) || !tag.toString().contains("gems/"))continue;
				try{
					String id = getTagID(tag, "gems/");
					ResourceLocation dustTag = new ResourceLocation("forge", "dusts/"+id);
					//Gem -> Dust
					if(ItemTagHelper.tagExists(dustTag)){
						if(debugRecipes)System.out.println("Gem -> Dust: " + id);
						size+= addRecipesForTag("gems", tag, dustTag, 1, 2400);
					}
				}catch(Exception e){
					System.out.println(tag);
					e.printStackTrace();
					
				}
			}
		}		
		CrystalMod.LOGGER.info("Generated " + size + " ore recipes " + FMLEnvironment.dist);		
	}
	
	private RecipeManager getRecipeManager() {
		if(FMLEnvironment.dist == Dist.DEDICATED_SERVER){
			return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
		} else {
			return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
		}
	}
	
	public boolean hasUserCreatedRecipe(Item item){
		if(getRecipeManager() == null) {
			return false;
		}
		List<GrinderRecipe> recipes = getRecipeManager().getRecipes(ModRecipes.GRINDER);
		for(GrinderRecipe recipe : recipes){
			for(ItemStack stack : recipe.getInput().getMatchingStacks()){
				if(stack.getItem() == item){
					return true;
				}
			}
		}
		return false;
	}
	
	private String getTagID(ResourceLocation tag, String category){
		return tag.getPath().substring(category.length());
	}
	
	private ResourceLocation createTag(String domain, String category, String id){
		return new ResourceLocation(domain, category+id);
	}
	
	private int addRecipesForTag(String category, ResourceLocation inputTag, ResourceLocation outputTag, int outputSize, int energy){
		int added = 0;
		List<Item> inputs = ItemTagHelper.getItemsInTag(inputTag);
		if(ItemTagHelper.tagExists(outputTag)){
			Item firstItem = ItemTagHelper.getItemsInTag(outputTag).get(0);
			if(firstItem !=null){
				ItemStack output = new ItemStack(firstItem, outputSize);
				for(Item item : inputs){
					if(addRecipeForItem(category, item, output, energy)){
						added++;
					}
				}
			}
		}
		return added;
	}
	
	private boolean addRecipeForItem(String category, Item item, ItemStack output, int energy){
		if(!hasUserCreatedRecipe(item)){
			if(getRecipeManager() == null){
				return false;
			}
			ResourceLocation id = new ResourceLocation("crystalmod_grinder_generated", category + "/" + output.getItem().getRegistryName().getPath().toLowerCase());
			if(getRecipeManager().getRecipe(id) !=null){
				return false;
			}
			Ingredient input = Ingredient.fromItems(item);
			GrinderRecipe recipe = new GrinderRecipe(id, "", input, output, ItemStackTools.getEmptyStack(), 0.0f, energy);
			getRecipeManager().addRecipe(recipe);
			return true;
		}
		return false;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		generateRecipes();
	}
	
}
