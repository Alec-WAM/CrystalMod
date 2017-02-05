package alec_wam.CrystalMod.items.guide.page;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.client.util.comp.GuiComponentSprite;
import alec_wam.CrystalMod.client.util.comp.GuiComponentStandardRecipePage;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.items.guide.GuidePages.ManualChapter;
import alec_wam.CrystalMod.items.guide.GuidePages.PageData;
import alec_wam.CrystalMod.tiles.machine.ContainerNull;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class PageCrafting extends GuidePage {

	
	private static final int CRAZY_1 = 0x505000FF;
	private static final int CRAZY_2 = (CRAZY_1 & 0xFEFEFE) >> 1 | CRAZY_1 & -0xFF000000;
	private static final int CRAZY_3 = 0xF0100010;

	protected static RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
	public Object[] ingred;
	public ItemStack[] items;
	private IRecipe currentRecipe;
	private ItemStack output;
	private List<ItemStack> stacks;
	
	public PageCrafting(String id, ItemStack item){
		this(id, Collections.singletonList(item));
	}
	
	public PageCrafting(String id, List<ItemStack> resultingItem) {
		super(id);
		stacks = resultingItem;
		if(!resultingItem.isEmpty())updateItem(resultingItem.get(0));
	}

	public static IRecipe getFirstRecipeForItem(ItemStack resultingItem) {
		for (IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
			if (recipe == null) continue;

			ItemStack result = recipe.getRecipeOutput();
			if (ItemStackTools.isNullStack(result) || !ItemUtil.canCombine(result, resultingItem)) continue;

			return recipe;

		}
		return null;
	}

	private static Object[] getRecipeInput(IRecipe recipe) {
		if (recipe instanceof ShapelessOreRecipe) return ((ShapelessOreRecipe)recipe).getInput().toArray();
		else if (recipe instanceof ShapedOreRecipe) return ((ShapedOreRecipe)recipe).getInput();
		else if (recipe instanceof ShapedRecipes) return ((ShapedRecipes)recipe).recipeItems;
		else if (recipe instanceof ShapelessRecipes) return ((ShapelessRecipes)recipe).recipeItems.toArray();
		return null;
	}
	
	@SideOnly(Side.CLIENT)
    public void initGui(GuiGuideChapter gui, int startX, int startY){
		this.listIndex = 0;
    }

	public void updateItem(ItemStack out){
		/*IRecipe recipe = getFirstRecipeForItem(out);
		this.ingred = recipe !=null ? getRecipeInput(recipe) : new ItemStack[9];
		this.items = new ItemStack[ingred.length];
		this.currentRecipe = recipe;
		
		this.output = recipe !=null ? recipe.getRecipeOutput() : out;
		
		for(int i = 0; i < ingred.length; i++){
			Object obj = ingred[i];
			
			if(obj instanceof ItemStack){
				items[i] = ((ItemStack) obj).copy();
				ModLogger.info("Updateing "+i+" to "+items[i]);
				if(ItemStackTools.isValid(items[i]) && items[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)items[i].setItemDamage(0);
			} 
			if(obj instanceof String){
				String id = (String)obj;
				
				List<ItemStack> stacks = OreDictionary.getOres(id);
				if(stacks.isEmpty()){
					continue;
				}
				items[i] = stacks.get(0).copy();
				if(ItemStackTools.isValid(items[i]) && items[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)items[i].setItemDamage(0);
			}
			if(obj instanceof List<?>){
				List<?> list = ((List<?>)obj);
				if(!list.isEmpty()){
					Object objL = list.get(0);
					if(objL instanceof ItemStack){
						items[i] = ((ItemStack) objL).copy();
						if(ItemStackTools.isValid(items[i]) && items[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)items[i].setItemDamage(0);
					}
				}
			}
		}*/
		IRecipe recipe = getFirstRecipeForItem(out);
		this.ingred = new Object[9];
		this.items = new ItemStack[9];
		this.currentRecipe = recipe;
		
		this.output = recipe !=null ? recipe.getRecipeOutput() : out;
		ItemStack[] stacks = new ItemStack[9];
        int width = 3;
        int height = 3;

        if(recipe instanceof ShapedRecipes){
            ShapedRecipes shaped = (ShapedRecipes)recipe;
            width = shaped.recipeWidth;
            height = shaped.recipeHeight;
            this.ingred = stacks = shaped.recipeItems;
        }
        else if(recipe instanceof ShapelessRecipes){
            ShapelessRecipes shapeless = (ShapelessRecipes)recipe;
            for(int i = 0; i < shapeless.recipeItems.size(); i++){
            	ingred[i] = stacks[i] = shapeless.recipeItems.get(i);
            }
        }
        else if(recipe instanceof ShapedOreRecipe){
            ShapedOreRecipe shaped = (ShapedOreRecipe)recipe;
            try{
                width = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 4);
                height = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 5);
            }
            catch(Exception e){
            }
            for(int i = 0; i < shaped.getInput().length; i++){
                Object input = shaped.getInput()[i];
                if(input != null){
                	ingred[i] = input;
                    stacks[i] = input instanceof ItemStack ? (ItemStack)input : (((List<ItemStack>)input).isEmpty() ? ItemStackTools.getEmptyStack() : getRandomIngredient(((List<ItemStack>)input)));
                }
            }
        }
        else if(recipe instanceof ShapelessOreRecipe){
            ShapelessOreRecipe shapeless = (ShapelessOreRecipe)recipe;
            for(int i = 0; i < shapeless.getInput().size(); i++){
                Object input = shapeless.getInput().get(i);
                ingred[i] = input;
                stacks[i] = input instanceof ItemStack ? (ItemStack)input : (((List<ItemStack>)input).isEmpty() ? ItemStackTools.getEmptyStack() : getRandomIngredient(((List<ItemStack>)input)));
            }
        }
        
        for(int i = 0; i < stacks.length; i++){
        	ItemStack stack = stacks[i];
            if(ItemStackTools.isValid(stack)){
	        	ItemStack copy = ItemUtil.copy(stack, 1);
	            if(copy.getItemDamage() == OreDictionary.WILDCARD_VALUE){
	                copy.setItemDamage(0);
	            }
	        	int index = getCraftingIndex(i, width, height);
	            items[index] = copy;
            }
        }
       /* for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
            	int i = y*width+x;
                ItemStack stack = stacks[i];
                if(ItemStackTools.isValid(stack)){
                    ItemStack copy = ItemUtil.copy(stack, 1);
                    if(copy.getItemDamage() == OreDictionary.WILDCARD_VALUE){
                        copy.setItemDamage(0);
                    }
                    int index = getCraftingIndex(i, width, height);
                    items[index] = copy;
                }
            }
        }*/
	}
	
	/**Offsets index based on size of recipe to fix recipes that do not a specific column or row.**/
	private int getCraftingIndex(int i, int width, int height) {
		int index;
		if (width == 1) {
			if (height == 3) {
				index = (i * 3) + 1;
			} else if (height == 2) {
				index = (i * 3) + 1;
			} else {
				index = 4;
			}
		} else if (height == 1) {
			index = i + 3;
		} else if (width == 2) {
			index = i;
			if (i > 1) {
				index++;
				if (i > 3) {
					index++;
				}
			}
		} else if (height == 2) {
			index = i + 3;
		} else {
			index = i;
		}
		return index;
	}
	
	public int listIndex = 0;
	@SideOnly(Side.CLIENT)
    public void updateScreen(GuiGuideChapter gui, int startX, int startY, int timer){
		boolean shift = GuiScreen.isShiftKeyDown();
		if(!shift && Util.isMultipleOf(timer, 60)){
			ItemStack newStack = stacks.get(listIndex);
			updateItem(newStack);
			
			listIndex++;
			listIndex%=stacks.size();
		}
		/*if(!GuiScreen.isShiftKeyDown() && Util.isMultipleOf(timer, 20)){
			for(int i = 0; i < ingred.length; i++){
				Object obj = ingred[i];
				
				if(obj instanceof ItemStack){
					items[i] = ((ItemStack)obj).copy();
					if(items[i] !=null && items[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)items[i].setItemDamage(0);
				}
				
				if(obj instanceof String){
					String id = (String)obj;
					List<ItemStack> stacks = OreDictionary.getOres(id);
					if(stacks.isEmpty())continue;
					ItemStack newStack = getRandomIngredient(stacks);
					if(!ItemStackTools.isNullStack(newStack) && newStack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
						items[i] = newStack.copy();
						items[i].setItemDamage(0);
					}else items[i] = newStack;
				}
				else if(obj instanceof List<?>){
					@SuppressWarnings("unchecked")
					List<ItemStack> list = ((List<ItemStack>)obj);
					ItemStack newStack = getRandomIngredient(list);
					if(ItemStackTools.isValid(newStack) && newStack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
						items[i] = newStack.copy();
						items[i].setItemDamage(0);
					}else items[i] = newStack;
				}
			}
		}*/
	}

	
	public static ItemStack getRandomIngredient(List<ItemStack> ingredients) {
        if(ingredients == null || ingredients.isEmpty()) return null;
        return ingredients.get(Util.rand.nextInt(ingredients.size() > 1 ? ingredients.size()-1 : ingredients.size()));
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		super.drawBackground(gui, startX, startY, mouseX, mouseY, partialTicks);
		int x = 20;
		int y = 20;
		GuiComponentSprite.renderSprite(gui.mc, x, y, startX, startY, mouseX, mouseY, GuiComponentStandardRecipePage.iconCraftingGrid, GuiComponentStandardRecipePage.texture, 1f, 1f, 1f);
		GuiComponentSprite.renderSprite(gui.mc, 90, 40, startX, startY, mouseX, mouseY, GuiComponentStandardRecipePage.iconArrow, GuiComponentStandardRecipePage.texture, 1f, 1f, 1f);

	}
	
	@SideOnly(Side.CLIENT)
    public void drawForeground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		int x = 20;
		int y = 20;
		int relativeMouseX = mouseX - x/*+ startX - x*/;
		int relativeMouseY = mouseY - y/*+ startY - y*/;
		int gridOffsetX = 1;
		int gridOffsetY = 1;
		int itemBoxSize = 19;

		ItemStack outputStack = output;
		
		if(!ItemStackTools.isNullStack(outputStack)){
			drawItemStack(outputStack, startX + 150, startY+40, ""+(outputStack.stackSize > 1 ? outputStack.stackSize : ""));
			
			if (mouseX > startX + 150 - 2 && mouseX < startX + 150 - 2 + itemBoxSize &&
					mouseY > startY+40 - 2 && mouseY < startY+40 - 2 + itemBoxSize) {
				drawItemStackTooltip(outputStack, mouseX, mouseY);
			}
		}
		
		if(items != null){
			ItemStack tooltip = ItemStackTools.getEmptyStack();
			for (int i = 0; i < items.length; i++) {
				ItemStack input = items[i];
				/*if (ingred[i] !=null && ingred[i] instanceof ItemStack) {
					ItemStack input = (ItemStack)ingred[i];*/
					if(ItemStackTools.isValid(input)){
						int row = (i % 3);
						int column = i / 3;
						int itemX = startX + gridOffsetX + (row * itemBoxSize);
						int itemY = startY + gridOffsetY + (column * itemBoxSize);
						drawItemStack(input, x + itemX, y + itemY, "");
						if (relativeMouseX > itemX - 2 && relativeMouseX < itemX - 2 + itemBoxSize &&
								relativeMouseY > itemY - 2 && relativeMouseY < itemY - 2 + itemBoxSize) {
							tooltip = input;
						}
					}
				//}
			}
			if (ItemStackTools.isValid(tooltip)) {
				drawItemStackTooltip(tooltip, relativeMouseX + x, relativeMouseY + y);
			}
		}
		
		String text = GuidePages.getText(getChapter(), this);
		String title = GuidePages.getTitle(getChapter(), this);
		x = startX+6;
		if(!Strings.isNullOrEmpty(title)){
			GlStateManager.pushMatrix();
			boolean oldUnicode = gui.getFont().getUnicodeFlag();
			gui.getFont().setUnicodeFlag(false);

			gui.getFont().drawString(title, x, startY+10, 0, false);

			gui.getFont().setUnicodeFlag(oldUnicode);
			GlStateManager.popMatrix();
		}
		if(text != null && !text.isEmpty()){
			 float scale = 0.75f;
			 List<String> lines = gui.getFont().listFormattedStringToWidth(text, (int)(189/scale));
			 for(int i = 0; i < lines.size(); i++){
				 y = startY+80+(i*(int)(gui.getFont().FONT_HEIGHT*scale+3));
				 GlStateManager.pushMatrix();
				 GlStateManager.scale(scale, scale, scale);
				 boolean oldUnicode = gui.getFont().getUnicodeFlag();
				 gui.getFont().setUnicodeFlag(false);
	
				 gui.getFont().drawString(lines.get(i), x/scale, y/scale, 0, false);
	
				 gui.getFont().setUnicodeFlag(oldUnicode);
				 GlStateManager.popMatrix();
			 }
		 }
	}

	protected void drawItemStackTooltip(ItemStack stack, int x, int y) {
		final Minecraft mc = Minecraft.getMinecraft();
		FontRenderer font = Objects.firstNonNull(stack.getItem().getFontRenderer(stack), mc.fontRendererObj);

		GL11.glColor3f(1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

		List<String> colored = Lists.newArrayListWithCapacity(list.size());
		Iterator<String> it = list.iterator();
		colored.add(getRarityColor(stack) + it.next());

		while (it.hasNext())
			colored.add(TextFormatting.GRAY + it.next());

		drawHoveringText(colored, x, y, font);
		GL11.glEnable(GL11.GL_LIGHTING);
	}


	protected TextFormatting getRarityColor(ItemStack stack) {
		return TextFormatting.values()[stack.getRarity().rarityColor.ordinal()];
	}
	
	protected void drawHoveringText(List<String> lines, int x, int y, FontRenderer font) {
		
		final int lineCount = lines.size();
		if (lineCount == 0) return;

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int width = 0;

		for (String s : lines) {
			int l = font.getStringWidth(s);
			if (l > width) width = l;
		}

		final int left = x + 12;
		int top = y - 12;

		int height = 8;
		if (lineCount > 1) height += 2 + (lineCount - 1) * 10;
		RenderUtil.drawGradientRect(left - 3, top - 4, 350, left + width + 3, top - 3, CRAZY_3, CRAZY_3);
		RenderUtil.drawGradientRect(left - 3, top + height + 3, 350, left + width + 3, top + height + 4, CRAZY_3, CRAZY_3);
		RenderUtil.drawGradientRect(left - 3, top - 3, 350, left + width + 3, top + height + 3, CRAZY_3, CRAZY_3);
		RenderUtil.drawGradientRect(left - 4, top - 3, 350, left - 3, top + height + 3, CRAZY_3, CRAZY_3);
		RenderUtil.drawGradientRect(left + width + 3, top - 3, 350, left + width + 4, top + height + 3, CRAZY_3, CRAZY_3);

		RenderUtil.drawGradientRect(left - 3, top - 3 + 1, 350, left - 3 + 1, top + height + 3 - 1, CRAZY_1, CRAZY_2);
		RenderUtil.drawGradientRect(left + width + 2, top - 3 + 1, 350, left + width + 3, top + height + 3 - 1, CRAZY_1, CRAZY_2);
		RenderUtil.drawGradientRect(left - 3, top - 3, 350, left + width + 3, top - 3 + 1, CRAZY_1, CRAZY_1);
		RenderUtil.drawGradientRect(left - 3, top + height + 2, 350, left + width + 3, top + height + 3, CRAZY_2, CRAZY_2);

		for (int i = 0; i < lineCount; ++i) {
			String line = lines.get(i);
			font.drawStringWithShadow(line, left, top, -1);
			if (i == 0) top += 2;
			top += 10;
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
	private void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str)
	{
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		itemRenderer.zLevel = 200.0F;
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor3f(1f, 1f, 1f);
		FontRenderer font = null;
		if (!ItemStackTools.isNullStack(par1ItemStack)) font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		if (font == null) font = Minecraft.getMinecraft().fontRendererObj;
		GlStateManager.enableDepth();
		itemRenderer.renderItemAndEffectIntoGUI(par1ItemStack, par2, par3);
		itemRenderer.renderItemOverlayIntoGUI(font, par1ItemStack, par2, par3, par4Str);
		GlStateManager.disableDepth();
		itemRenderer.zLevel = 0.0F;
	}
	
}
