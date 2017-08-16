package alec_wam.CrystalMod.items.guide.page;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.crop.CropRecipe;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.blocks.crops.material.ItemMaterialSeed;
import alec_wam.CrystalMod.client.util.SpriteData;
import alec_wam.CrystalMod.client.util.comp.GuiComponentSprite;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuiGuideIndex;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageMaterialCropRecipe extends GuidePage {

	private static final int CRAZY_1 = 0x505000FF;
	private static final int CRAZY_2 = (CRAZY_1 & 0xFEFEFE) >> 1 | CRAZY_1 & -0xFF000000;
	private static final int CRAZY_3 = 0xF0100010;

	protected static RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
	private CropRecipe recipe;
	
	public PageMaterialCropRecipe(String id, CropRecipe recipe) {
		super(id);
		this.recipe = recipe;
	}
	
	private ResourceLocation guideTexture = new ResourceLocation("crystalmod", "textures/gui/guide.png");
	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		super.drawBackground(gui, startX, startY, mouseX, mouseY, partialTicks);
		String text = GuidePages.getText(getChapter(), this);
		String title = GuidePages.getTitle(getChapter(), this);
		int x = startX+6;
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
				 int y = startY+80+(i*(int)(gui.getFont().FONT_HEIGHT*scale+3));
				 GlStateManager.pushMatrix();
				 GlStateManager.scale(scale, scale, scale);
				 boolean oldUnicode = gui.getFont().getUnicodeFlag();
				 gui.getFont().setUnicodeFlag(false);
	
				 gui.getFont().drawString(lines.get(i), x/scale, y/scale, 0, false);
	
				 gui.getFont().setUnicodeFlag(oldUnicode);
				 GlStateManager.popMatrix();
			 }
		 }
		
		x = 10;
		int y = 20;
		SpriteData arrowSprite = new SpriteData(60, 196, 48, 16);
		GlStateManager.pushMatrix();
		GlStateManager.translate(startX+x+37, startY+y+33, 0);
		GlStateManager.scale(0.5, 0.5, 1);
		GuiComponentSprite.renderSprite(gui.mc, 0, 0, 0, 0, mouseX, mouseY, arrowSprite, guideTexture, 1f, 1f, 1f);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(startX+x+124, startY+y+41.5, 0);
		GlStateManager.scale(0.5, 0.5, 1);
		GlStateManager.rotate(180, 0, 0, 1);
		GuiComponentSprite.renderSprite(gui.mc, 0, 0, 0, 0, mouseX, mouseY, arrowSprite, guideTexture, 1f, 1f, 1f);
		GlStateManager.popMatrix();

	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void drawForeground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		int itemBoxSize = 34;
		int rowY = 41;
		ItemStack tooltipStack = ItemStackTools.getEmptyStack();
		if(recipe !=null){
			if(recipe.getInput1() !=null){
				ItemStack input = ItemMaterialSeed.getSeed(recipe.getInput1());
				GlStateManager.pushMatrix();
				GlStateManager.translate(startX + 16, startY+rowY, 0);
				GlStateManager.scale(2, 2, 1);
				drawItemStack(input, 0, 0, ""+(ItemStackTools.getStackSize(input) > 1 ? ItemStackTools.getStackSize(input) : ""));
	        	GL11.glPopMatrix();
		        RenderHelper.enableStandardItemLighting();
				if (mouseX > startX + 16 - 2 && mouseX < startX + 16 - 2 + itemBoxSize &&
						mouseY > startY+rowY - 2 && mouseY < startY+rowY - 2 + itemBoxSize) {
					tooltipStack = input;
				}
			}
			
			if(recipe.getOutput() !=null){
				ItemStack input = ItemMaterialSeed.getSeed(recipe.getOutput());
				GlStateManager.pushMatrix();
				GlStateManager.translate(startX + 76, startY+rowY, 0);
				GlStateManager.scale(2, 2, 1);
				drawItemStack(input, 0, 0, ""+(ItemStackTools.getStackSize(input) > 1 ? ItemStackTools.getStackSize(input) : ""));
	        	GL11.glPopMatrix();
		        RenderHelper.enableStandardItemLighting();
				if (mouseX > startX + 76 - 2 && mouseX < startX + 76 - 2 + itemBoxSize &&
						mouseY > startY+rowY - 2 && mouseY < startY+rowY - 2 + itemBoxSize) {
					tooltipStack = input;
				}
			}
			
			if(recipe.getInput2() !=null){
				ItemStack input = ItemMaterialSeed.getSeed(recipe.getInput2());
				GlStateManager.pushMatrix();
				GlStateManager.translate(startX + 136, startY+rowY, 0);
				GlStateManager.scale(2, 2, 1);
				drawItemStack(input, 0, 0, ""+(ItemStackTools.getStackSize(input) > 1 ? ItemStackTools.getStackSize(input) : ""));
	        	GL11.glPopMatrix();
		        RenderHelper.enableStandardItemLighting();
				if (mouseX > startX + 136 - 2 && mouseX < startX + 136 - 2 + itemBoxSize &&
						mouseY > startY+rowY - 2 && mouseY < startY+rowY - 2 + itemBoxSize) {
					tooltipStack = input;
				}
			}
		}
		if(ItemStackTools.isValid(tooltipStack)){
			drawItemStackTooltip(tooltipStack, mouseX, mouseY);
		}
	}

	protected void drawItemStackTooltip(ItemStack stack, int x, int y) {
		final Minecraft mc = Minecraft.getMinecraft();
		FontRenderer font = Objects.firstNonNull(stack.getItem().getFontRenderer(stack), mc.fontRendererObj);

		GL11.glColor3f(1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		List<String> list = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips);

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
		if (par1ItemStack != null) font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		if (font == null) font = Minecraft.getMinecraft().fontRendererObj;
		GlStateManager.enableDepth();
		itemRenderer.renderItemAndEffectIntoGUI(par1ItemStack, par2, par3);
		itemRenderer.renderItemOverlayIntoGUI(font, par1ItemStack, par2, par3, par4Str);
		GlStateManager.disableDepth();
		itemRenderer.zLevel = 0.0F;
	}

	private static ItemStack cacheCropSeed = ItemStackTools.getEmptyStack();
	
	@Override
	public boolean matchesFilter(String filter) {
		if(recipe !=null && recipe.getOutput() !=null){
			ItemStack stack = cacheCropSeed;
			
			if(ItemStackTools.isEmpty(stack)){
				stack = cacheCropSeed = ItemMaterialSeed.getSeed(recipe.getOutput());
			}
			
			if(Lang.translateToLocal(stack.getUnlocalizedName()).toLowerCase(GuiGuideIndex.getLocale()).contains(filter)){
				return true;
			}
		}
		return false;
	}
	
}
