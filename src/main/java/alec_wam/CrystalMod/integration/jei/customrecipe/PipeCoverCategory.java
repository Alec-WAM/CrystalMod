package alec_wam.CrystalMod.integration.jei.customrecipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.integration.jei.RecipeHandler;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PipeCoverCategory extends BlankRecipeCategory<PipeCoverCategory.CoverRecipeWrapper>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".PipeCover";
	
	public static class CoverRecipeWrapper extends BlankRecipeWrapper {
		protected final ItemStack coverStack;
		protected final ItemStack coverBlock;
		public static final ItemStack PLATE = new ItemStack(ModItems.plates, 1, PlateType.DARK_IRON.getMeta());
		public static final ItemStack SLIME = new ItemStack(Items.SLIME_BALL);
		
		public CoverRecipeWrapper(ItemStack cover) {

			coverStack = ItemUtil.copy(cover, 6);
			coverBlock = ItemPipeCover.getItemFromCover(coverStack);
		}

		@Override
		public void getIngredients(IIngredients ingredients) {

			ingredients.setInputs(ItemStack.class, ImmutableList.of(coverBlock, PLATE, SLIME));
			ingredients.setOutputs(ItemStack.class, ImmutableList.of(coverStack));
		}
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {	    
	    registry.addRecipeCategories(new PipeCoverCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<CoverRecipeWrapper>(CoverRecipeWrapper.class, PipeCoverCategory.UID));
	    registry.addRecipeCategoryCraftingItem(new ItemStack(Blocks.CRAFTING_TABLE), PipeCoverCategory.UID);

	    List<CoverRecipeWrapper> result = new ArrayList<CoverRecipeWrapper>();    
	    for (ItemStack rec : ItemPipeCover.allCovers) {
	      result.add(new CoverRecipeWrapper(rec));
	    }    
	    registry.addRecipes(result);
	}
	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot1 = 1;

	public static final int width = 116;
	public static final int height = 54;
	private final IDrawableStatic background;
	private final String localizedName;
	private final ICraftingGridHelper craftingGridHelper;

  	public PipeCoverCategory(IGuiHelper guiHelper) {
  		ResourceLocation location = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

		background = guiHelper.createDrawable(location, 29, 16, width, height);
		localizedName = Lang.localize("jei.pipecover.category");
		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}
	
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CoverRecipeWrapper recipeWrapper, IIngredients arg2) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(craftOutputSlot, false, 94, 18);

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				int index = craftInputSlot1 + x + (y * 3);
				guiItemStacks.init(index, true, x * 18, y * 18);
			}
		}
		craftingGridHelper.setInputs(guiItemStacks, arg2.getInputs(ItemStack.class));
		guiItemStacks.set(craftOutputSlot, arg2.getOutputs(ItemStack.class).get(0));
	}

}
