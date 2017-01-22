package alec_wam.CrystalMod.client.util.comp;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.util.SpriteData;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.Util;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class GuiComponentCraftingGrid extends GuiComponentSprite {

	private static final int CRAZY_1 = 0x505000FF;
	private static final int CRAZY_2 = (CRAZY_1 & 0xFEFEFE) >> 1 | CRAZY_1 & -0xFF000000;
	private static final int CRAZY_3 = 0xF0100010;

	protected static RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
	public Object[] ingred;
	public ItemStack[] items;
	
	public GuiComponentCraftingGrid(int x, int y, Object[] items, SpriteData icon, ResourceLocation texture) {
		super(x, y, icon, texture);
		updateItem(items);
	}
	
	public void updateItem(Object[] itemArray){
		this.ingred = itemArray;
		this.items = new ItemStack[ingred.length];
		for(int i = 0; i < ingred.length; i++){
			Object obj = ingred[i];
			
			if(obj instanceof ItemStack){
				items[i] = ((ItemStack) obj).copy();
				if(items[i] !=null && items[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)items[i].setItemDamage(0);
			} 
			if(obj instanceof String){
				String id = (String)obj;
				
				List<ItemStack> stacks = OreDictionary.getOres(id);
				if(stacks.isEmpty()){
					ModLogger.info("Ore DIc empty for "+id);
					continue;
				}
				items[i] = stacks.get(0).copy();
				if(items[i] !=null && items[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)items[i].setItemDamage(0);
			}
			if(obj instanceof List<?>){
				List<?> list = ((List<?>)obj);
				if(!list.isEmpty()){
					Object objL = list.get(0);
					if(objL instanceof ItemStack){
						items[i] = ((ItemStack) objL).copy();
						if(items[i] !=null && items[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)items[i].setItemDamage(0);
					}
				}
			}
		}
	}

	public int tick = (int)System.currentTimeMillis();
	public void updateComp(){
		super.updateComp();
		tick++;
        if(!CrystalMod.proxy.isShiftKeyDown() && Util.isMultipleOf(tick, 20)){
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
					ItemStack newStack = getCycledIngredients(stacks);
					if(newStack !=null && newStack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
						items[i] = newStack.copy();
						items[i].setItemDamage(0);
					}else items[i] = newStack;
				}
				else if(obj instanceof List<?>){
					@SuppressWarnings("unchecked")
					List<ItemStack> list = ((List<ItemStack>)obj);
					ItemStack newStack = getCycledIngredients(list);
					if(newStack !=null && newStack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
						items[i] = newStack.copy();
						items[i].setItemDamage(0);
					}else items[i] = newStack;
				}
			}
		}
	}
		
	public ItemStack getCycledIngredients(List<ItemStack> ingredients) {
        if(ingredients == null || ingredients.isEmpty()) return null;
        return ingredients.get(Util.rand.nextInt(ingredients.size() > 1 ? ingredients.size()-1 : ingredients.size()));
    }
	
	@Override
	public void renderOverlay(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderOverlay(minecraft, offsetX, offsetY, mouseX, mouseY);

		int relativeMouseX = mouseX + offsetX - x;
		int relativeMouseY = mouseY + offsetY - y;
		int gridOffsetX = 1;
		int gridOffsetY = 1;
		int itemBoxSize = 19;

		ItemStack tooltip = null;
		int i = 0;
		for (ItemStack input : items) {
			if (input != null) {
				int row = (i % 3);
				int column = i / 3;
				int itemX = offsetX + gridOffsetX + (row * itemBoxSize);
				int itemY = offsetY + gridOffsetY + (column * itemBoxSize);
				drawItemStack(input, x + itemX, y + itemY, "");
				if (relativeMouseX > itemX - 2 && relativeMouseX < itemX - 2 + itemBoxSize &&
						relativeMouseY > itemY - 2 && relativeMouseY < itemY - 2 + itemBoxSize) {
					tooltip = input;
				}
			}
			i++;
		}
		if (tooltip != null) {
			drawItemStackTooltip(tooltip, relativeMouseX + 25, relativeMouseY + 30);
		}
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

		this.zLevel = 350.0F;

		drawGradientRect(left - 3, top - 4, left + width + 3, top - 3, CRAZY_3, CRAZY_3);
		drawGradientRect(left - 3, top + height + 3, left + width + 3, top + height + 4, CRAZY_3, CRAZY_3);
		drawGradientRect(left - 3, top - 3, left + width + 3, top + height + 3, CRAZY_3, CRAZY_3);
		drawGradientRect(left - 4, top - 3, left - 3, top + height + 3, CRAZY_3, CRAZY_3);
		drawGradientRect(left + width + 3, top - 3, left + width + 4, top + height + 3, CRAZY_3, CRAZY_3);

		drawGradientRect(left - 3, top - 3 + 1, left - 3 + 1, top + height + 3 - 1, CRAZY_1, CRAZY_2);
		drawGradientRect(left + width + 2, top - 3 + 1, left + width + 3, top + height + 3 - 1, CRAZY_1, CRAZY_2);
		drawGradientRect(left - 3, top - 3, left + width + 3, top - 3 + 1, CRAZY_1, CRAZY_1);
		drawGradientRect(left - 3, top + height + 2, left + width + 3, top + height + 3, CRAZY_2, CRAZY_2);

		for (int i = 0; i < lineCount; ++i) {
			String line = lines.get(i);
			font.drawStringWithShadow(line, left, top, -1);
			if (i == 0) top += 2;
			top += 10;
		}

		this.zLevel = 0.0F;
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		/*if (lines.isEmpty()) return;

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		// RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int width = 0;

		for (String s : lines) {
			int l = font.getStringWidth(s);
			if (l > width) width = l;
		}

		final int i1 = x + 12;
		int j1 = y - 12;

		final int lineCount = lines.size();

		int height = 8;
		if (lineCount > 1) height += 2 + (lineCount - 1) * 10;

		this.zLevel = 350.0F;
		itemRenderer.zLevel = 350.0F;

		drawGradientRect(i1 - 3, j1 - 4, i1 + width + 3, j1 - 3, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 - 3, j1 + height + 3, i1 + width + 3, j1 + height + 4, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 - 3, j1 - 3, i1 + width + 3, j1 + height + 3, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + height + 3, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 + width + 3, j1 - 3, i1 + width + 4, j1 + height + 3, CRAZY_3, CRAZY_3);

		drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + height + 3 - 1, CRAZY_1, CRAZY_2);
		drawGradientRect(i1 + width + 2, j1 - 3 + 1, i1 + width + 3, j1 + height + 3 - 1, CRAZY_1, CRAZY_2);
		drawGradientRect(i1 - 3, j1 - 3, i1 + width + 3, j1 - 3 + 1, CRAZY_1, CRAZY_1);
		drawGradientRect(i1 - 3, j1 + height + 2, i1 + width + 3, j1 + height + 3, CRAZY_2, CRAZY_2);

		for (int i = 0; i < lineCount; ++i) {
			String s1 = lines.get(i);
			font.drawStringWithShadow(s1, i1, j1, -1);
			if (i == 0) j1 += 2;
			j1 += 10;
		}

		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);*/

	}

	protected void drawItemStackTooltip(ItemStack stack, int x, int y) {
		/*final Minecraft mc = Minecraft.getMinecraft();
		FontRenderer font = Objects.firstNonNull(stack.getItem().getFontRenderer(stack), mc.fontRenderer);

		@SuppressWarnings("unchecked")
		List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

		List<String> colored = Lists.newArrayListWithCapacity(list.size());
		colored.add(getRarityColor(stack) + list.get(0));
		for (String line : list)
			colored.add(EnumChatFormatting.GRAY + line);

		drawHoveringText(colored, x, y, font);*/
		final Minecraft mc = Minecraft.getMinecraft();
		FontRenderer font = Objects.firstNonNull(stack.getItem().getFontRenderer(stack), mc.fontRendererObj);

		GL11.glColor3f(1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		List<String> list = stack.getTooltip(CrystalMod.proxy.getClientPlayer(), mc.gameSettings.advancedItemTooltips);

		List<String> colored = Lists.newArrayListWithCapacity(list.size());
		Iterator<String> it = list.iterator();
		colored.add(getRarityColor(stack) + it.next());

		while (it.hasNext())
			colored.add(TextFormatting.GRAY + it.next());

		drawHoveringText(colored, x, y, font);
	}

	protected TextFormatting getRarityColor(ItemStack stack) {
		return TextFormatting.values()[stack.getRarity().rarityColor.ordinal()];
	}

	private void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str)
	{
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRenderer.zLevel = 200.0F;
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor3f(1f, 1f, 1f);
		FontRenderer font = null;
		if (par1ItemStack != null) font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		if (font == null) font = Minecraft.getMinecraft().fontRendererObj;
		GlStateManager.enableDepth();
		itemRenderer.renderItemAndEffectIntoGUI(par1ItemStack, par2, par3);
		itemRenderer.renderItemOverlayIntoGUI(font, par1ItemStack, par2, par3, par4Str);
		GlStateManager.disableDepth();
		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
	}
}