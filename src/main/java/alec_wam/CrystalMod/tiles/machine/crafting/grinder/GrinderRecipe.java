package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import java.util.Optional;

import com.google.gson.JsonObject;

import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class GrinderRecipe implements IRecipe<IInventory> {
   private final ResourceLocation id;
   private final String group;
   private final Ingredient input;
   private final ItemStack output;
   private final ItemStack bonus;
   private final float bonusChance;
   private final int energy;

   public GrinderRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, ItemStack bonus, float bonusChance, int energy) {
      this.id = id;
      this.group = group;
      this.input = input;
      this.output = output;
      this.bonus = bonus;
      this.bonusChance = bonusChance;
      this.energy = energy;
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   @Override
   public boolean matches(IInventory inv, World worldIn) {
      return this.input.test(inv.getStackInSlot(0));
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   @Override
   public ItemStack getCraftingResult(IInventory inv) {
      return this.output.copy();
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   @Override
   public boolean canFit(int width, int height) {
      return true;
   }

   @Override
   public IRecipeSerializer<?> getSerializer() {
      return ModRecipes.GRINDER_SERIALIZER;
   }

   public Ingredient getInput() {
	   return this.input;
   }
   
   @Override
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.input);
      return nonnulllist;
   }

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   @Override
   public ItemStack getRecipeOutput() {
      return this.output;
   }
   
   public ItemStack getBonusOutput() {
	   return this.bonus;
   }

   /**
    * Recipes with equal group are combined into one button in the recipe book
    */
   @Override
   public String getGroup() {
      return this.group;
   }
   
   public float getBonusChance() {
	   return this.bonusChance;
   }

   public int getEnergy() {
      return this.energy;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public IRecipeType<?> getType() {
	   return ModRecipes.GRINDER_TYPE;
   }

   public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrinderRecipe> {
      
	  @SuppressWarnings("deprecation")
	  @Override
	  public GrinderRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JSONUtils.getString(json, "group", "");
         Ingredient ingredient;
         if (JSONUtils.isJsonArray(json, "ingredient")) {
            ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
         } else {
            ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
         }

         if (json.has("result")) {
        	ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            ItemStack secondStack = ItemStackTools.getEmptyStack();
            float bonusChance = 0.0f;
            if(json.has("bonus")){
            	JsonObject bonusJson = JSONUtils.getJsonObject(json, "bonus");
            	String s2 = JSONUtils.getString(bonusJson, "item");
            	Optional<Item> bonusItem = Registry.ITEM.getValue(new ResourceLocation(s2));
            	//Don't throw exception if item is null because we want to be able to support other mods items
            	if(bonusItem.isPresent()){
            		int count = JSONUtils.getInt(bonusJson, "count", 1);
            		secondStack = new ItemStack(bonusItem.orElse(Items.AIR), count);
            		bonusChance = JSONUtils.getFloat(bonusJson, "chance", 0.0f);
            	} 
            }
            
            int energy = JSONUtils.getInt(json, "energy", 1600);
            return new GrinderRecipe(recipeId, s, ingredient, itemstack, secondStack, bonusChance, energy);
         } else {
            throw new IllegalStateException("No output exists");
         }
      }

	  @Override
	  public GrinderRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         String s = buffer.readString(32767);
         Ingredient ingredient = Ingredient.read(buffer);
         ItemStack itemstack = buffer.readItemStack();
         ItemStack itemstack2 = buffer.readItemStack();
         float chance = buffer.readFloat();
         int energy = buffer.readVarInt();
         return new GrinderRecipe(recipeId, s, ingredient, itemstack, itemstack2, chance, energy);
      }

	  @Override
	  public void write(PacketBuffer buffer, GrinderRecipe recipe) {
         buffer.writeString(recipe.group);
         recipe.input.write(buffer);
         buffer.writeItemStack(recipe.output);
         buffer.writeItemStack(recipe.bonus);
         buffer.writeFloat(recipe.bonusChance);
         buffer.writeVarInt(recipe.energy);
      }
   }
}