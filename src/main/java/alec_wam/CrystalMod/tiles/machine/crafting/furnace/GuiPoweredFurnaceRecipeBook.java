package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPoweredFurnaceRecipeBook extends GuiRecipeBook {
   protected boolean func_201521_f() {
      boolean flag = !this.recipeBook.func_202884_d();
      this.recipeBook.setFurnaceFilteringCraftable(flag);
      return flag;
   }

   public boolean isVisible() {
      return this.recipeBook.func_202883_c();
   }

   protected void setVisible(boolean p_193006_1_) {
      this.recipeBook.setFurnaceGuiOpen(p_193006_1_);
      if (!p_193006_1_) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   protected void func_205702_a() {
      this.toggleRecipesBtn.initTextureValues(152, 182, 28, 18, RECIPE_BOOK);
   }

   protected String func_205703_f() {
      return I18n.format(this.toggleRecipesBtn.isStateTriggered() ? "gui.recipebook.toggleRecipes.smeltable" : "gui.recipebook.toggleRecipes.all");
   }

   public void slotClicked(@Nullable Slot slotIn) {
      super.slotClicked(slotIn);
   }

   public void setupGhostRecipe(IRecipe p_193951_1_, List<Slot> p_193951_2_) {
      ItemStack itemstack = p_193951_1_.getRecipeOutput();
      this.ghostRecipe.setRecipe(p_193951_1_);
      this.ghostRecipe.addIngredient(Ingredient.fromStacks(itemstack), (p_193951_2_.get(1)).xPos, (p_193951_2_.get(1)).yPos);
      NonNullList<Ingredient> nonnulllist = p_193951_1_.getIngredients();
      
      Iterator<Ingredient> iterator = nonnulllist.iterator();

      for(int i = 0; i < 1; ++i) {
         if (!iterator.hasNext()) {
            return;
         }

         Ingredient ingredient = iterator.next();
         if (!ingredient.hasNoMatchingItems()) {
            Slot slot = p_193951_2_.get(i);
            this.ghostRecipe.addIngredient(ingredient, slot.xPos, slot.yPos);
         }
      }

   }

   public void renderGhostRecipe(int p_191864_1_, int p_191864_2_, boolean p_191864_3_, float p_191864_4_) {
      super.renderGhostRecipe(p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
      /*if (this.field_201527_n != null) {
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         int i = this.field_201527_n.xPos + p_191864_1_;
         int j = this.field_201527_n.yPos + p_191864_2_;
         Gui.drawRect(i, j, i + 16, j + 16, 822018048);
         this.mc.getItemRenderer().renderItemAndEffectIntoGUI(this.mc.player, this.func_201523_i().getDefaultInstance(), i, j);
         GlStateManager.depthFunc(516);
         Gui.drawRect(i, j, i + 16, j + 16, 822083583);
         GlStateManager.depthFunc(515);
         GlStateManager.enableLighting();
         RenderHelper.disableStandardItemLighting();
      }*/
   }
}