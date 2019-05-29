package alec_wam.CrystalMod.tiles.machine.crafting.press;

import com.google.gson.JsonObject;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.init.ModRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PressRecipe implements IRecipe {
   private final ResourceLocation id;
   private final String group;
   private final Ingredient input;
   private final ItemStack output;
   private final int energy;

   public PressRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, int energy) {
      this.id = id;
      this.group = group;
      this.input = input;
      this.output = output;
      this.energy = energy;
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      return this.input.test(inv.getStackInSlot(0));
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(IInventory inv) {
      return this.output.copy();
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return true;
   }

   public IRecipeSerializer<?> getSerializer() {
      return ModRecipes.PRESS_SERIALIZER;
   }

   public Ingredient getInput() {
	   return this.input;
   }
   
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.input);
      return nonnulllist;
   }

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   public ItemStack getRecipeOutput() {
      return this.output;
   }

   /**
    * Recipes with equal group are combined into one button in the recipe book
    */
   public String getGroup() {
      return this.group;
   }

   public int getEnergy() {
      return this.energy;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public net.minecraftforge.common.crafting.RecipeType<PressRecipe> getType() {
      return ModRecipes.PRESS;
   }

   public static class Serializer implements IRecipeSerializer<PressRecipe> {
      private static ResourceLocation NAME = CrystalMod.resourceL("press");
      
      public PressRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JsonUtils.getString(json, "group", "");
         Ingredient ingredient;
         if (JsonUtils.isJsonArray(json, "ingredient")) {
            ingredient = Ingredient.fromJson(JsonUtils.getJsonArray(json, "ingredient"));
         } else {
            ingredient = Ingredient.fromJson(JsonUtils.getJsonObject(json, "ingredient"));
         }

         if (json.has("result")) {
        	ItemStack itemstack = ShapedRecipe.deserializeItem(JsonUtils.getJsonObject(json, "result"));
            int energy = JsonUtils.getInt(json, "energy", 1600);
            return new PressRecipe(recipeId, s, ingredient, itemstack, energy);
         } else {
            throw new IllegalStateException("No output exists");
         }
      }

      public PressRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         String s = buffer.readString(32767);
         Ingredient ingredient = Ingredient.fromBuffer(buffer);
         ItemStack itemstack = buffer.readItemStack();
         int energy = buffer.readVarInt();
         return new PressRecipe(recipeId, s, ingredient, itemstack, energy);
      }

      public void write(PacketBuffer buffer, PressRecipe recipe) {
         buffer.writeString(recipe.group);
         recipe.input.writeToBuffer(buffer);
         buffer.writeItemStack(recipe.output);
         buffer.writeVarInt(recipe.energy);
      }

      @Override
      public ResourceLocation getName() {
         return NAME;
      }
   }
}