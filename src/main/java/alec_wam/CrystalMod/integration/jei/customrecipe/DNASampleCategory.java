package alec_wam.CrystalMod.integration.jei.customrecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.integration.jei.RecipeHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.dna.ItemDNA.DNAItemType;
import alec_wam.CrystalMod.tiles.machine.dna.PlayerDNA;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.PlayerUtil;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DNASampleCategory extends BlankRecipeCategory<DNASampleCategory.DNARecipeWrapper>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".DNASample";
	
	public static class DNARecipeWrapper extends BlankRecipeWrapper {
		protected final UUID uuid;
		protected final ItemStack sample, syringe;
		protected static final ItemStack EMPTY_SYRINGE = new ItemStack(ModItems.dnaItems, 1, DNAItemType.EMPTY_SYRINGE.getMeta());
		
		public DNARecipeWrapper(UUID uuid) {
			this.uuid = uuid;
			this.sample = new ItemStack(ModItems.dnaItems, 1, DNAItemType.SAMPLE_FULL.getMeta());
			//PlayerDNA.savePlayerDNA(sample, uuid);
			this.syringe = new ItemStack(ModItems.dnaItems, 1, DNAItemType.FILLED_SYRINGE.getMeta());
			//PlayerDNA.savePlayerDNA(syringe, uuid);
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputs(ItemStack.class, ImmutableList.of(EMPTY_SYRINGE, sample));
			ingredients.setOutputs(ItemStack.class, ImmutableList.of(syringe));
		}
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {	    
	    registry.addRecipeCategories(new DNASampleCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<DNARecipeWrapper>(DNARecipeWrapper.class, DNASampleCategory.UID));
	    registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.dnaMachine), DNASampleCategory.UID);

	    List<DNARecipeWrapper> result = new ArrayList<DNARecipeWrapper>();    
	    result.add(new DNARecipeWrapper(PlayerUtil.Alec_WAM));
	    registry.addRecipes(result);
	}
	
	@Nonnull
	private final IDrawable background;
	private final String localizedName;
	private static int xOff = 22;
	private static int yOff = 40;
	
	@Nonnull
	protected final IDrawableAnimated arrow;

  	public DNASampleCategory(IGuiHelper guiHelper) {
  		localizedName = Lang.localize("jei.dnasample.category");
		ResourceLocation backgroundLocation = new ResourceLocation("crystalmod", "textures/gui/machine/dnamachine.png");
	    background = guiHelper.createDrawable(backgroundLocation, 52, 10, 72, 45);

	    ResourceLocation overlay = new ResourceLocation("crystalmod", "textures/gui/elements/progress_arrow_right.png");
	    IDrawableStatic arrowDrawable = guiHelper.createDrawable(overlay, 24, 0, 24, 16, 48, 16);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
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
	public void drawExtras(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 68-xOff, 57-yOff);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, DNARecipeWrapper recipeWrapper, IIngredients arg2) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 45 - xOff-1, 58 - yOff-1);
		guiItemStacks.init(1, true, 72 - xOff-1, 31 - yOff-1);
		guiItemStacks.init(2, false, 99 - xOff-1, 58 - yOff-1);
		guiItemStacks.set(arg2);
	}

}
