package com.alec_wam.CrystalMod.client.util.comp;

import java.lang.reflect.Field;
import java.util.List;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.client.util.SpriteData;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.alec_wam.CrystalMod.util.Lang;
import com.alec_wam.CrystalMod.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;


public class GuiComponentStandardRecipePage extends BaseComponent {

	private static final ResourceLocation texture = new ResourceLocation("crystalmod:textures/gui/guide.png");

	public static SpriteData iconCraftingGrid = new SpriteData(0, 180, 56, 56);
	public static SpriteData iconArrow = new SpriteData(60, 197, 48, 15);

	private GuiComponentCraftingGrid craftingGrid;
	private GuiComponentSprite arrow;
	private GuiComponentBookDiscription lblDescription;
	private GuiComponentLabel lblTitle;
	private GuiComponentItemStackSpinner outputSpinner;
	private List<ItemStack> stacks;

	public GuiComponentStandardRecipePage(String title, String description, List<ItemStack> resultingItem) {
		super(0, 0);
		stacks = resultingItem;
		String translatedTitle = Lang.localize(title);
		lblTitle = new GuiComponentLabel((getWidth() - Minecraft.getMinecraft().fontRendererObj.getStringWidth(translatedTitle)) / 2, 12, translatedTitle);
		lblDescription = new GuiComponentBookDiscription(27, 95, 340, 51, description);
		arrow = new GuiComponentSprite(90, 50, iconArrow, texture);
		craftingGrid = new GuiComponentCraftingGrid(25, 30, getFirstRecipeForItem(resultingItem.get(0)), iconCraftingGrid, texture);

		outputSpinner = new GuiComponentItemStackSpinner(120, 30, resultingItem.get(0));

		addComponent(lblDescription);
		addComponent(lblTitle);
		addComponent(arrow);
		addComponent(outputSpinner);
		addComponent(craftingGrid);

	}

	public void setDescription(String desc, boolean isTranslated){
		this.lblDescription.setText(desc);
	}
	
	public String getDescription(){
		return lblDescription.getText();
	}
	
	public int tick = 0;
	public int listIndex = 0;
	public void updateComp(){
		super.updateComp();
		tick++;
		if(!CrystalMod.proxy.isShiftKeyDown() && Util.isMultipleOf(tick, 60)){
			listIndex++;
			listIndex%=stacks.size();
			ItemStack newStack = stacks.get(listIndex);
			outputSpinner.stack = newStack;
			craftingGrid.updateItem(getFirstRecipeForItem(newStack));
		}
	}
	
	
	
	private static Object[] getFirstRecipeForItem(ItemStack resultingItem) {
		Object[] recipeItems = new Object[9];
		for (IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
			if (recipe == null) continue;

			ItemStack result = recipe.getRecipeOutput();
			if (result == null || !ItemUtil.canCombine(result, resultingItem)) continue;

			Object[] input = getRecipeInput(recipe);
			if (input == null) continue;

			for (int i = 0; i < input.length; i++)
				recipeItems[i] = input[i];
			break;

		}
		return recipeItems;
	}

	private static Object[] getRecipeInput(IRecipe recipe) {
		if (recipe instanceof ShapelessOreRecipe) return ((ShapelessOreRecipe)recipe).getInput().toArray();
		else if (recipe instanceof ShapedOreRecipe) return ((ShapedOreRecipe)recipe).getInput();
		else if (recipe instanceof ShapedRecipes) return ((ShapedRecipes)recipe).recipeItems;
		else if (recipe instanceof ShapelessRecipes) return ((ShapelessRecipes)recipe).recipeItems.toArray();
		return null;
	}

	@Override
	public int getWidth() {
		return 220;
	}

	@Override
	public int getHeight() {
		return 200;
	}

}