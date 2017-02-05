package alec_wam.CrystalMod.items.guide.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.client.util.SpriteData;
import alec_wam.CrystalMod.client.util.comp.GuiComponentSprite;
import alec_wam.CrystalMod.client.util.comp.GuiComponentSpriteButton;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.items.guide.GuidePages.ManualChapter;
import alec_wam.CrystalMod.items.guide.GuidePages.PageData;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageFurnace extends GuidePage {

	
	private static final int CRAZY_1 = 0x505000FF;
	private static final int CRAZY_2 = (CRAZY_1 & 0xFEFEFE) >> 1 | CRAZY_1 & -0xFF000000;
	private static final int CRAZY_3 = 0xF0100010;

	protected static RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
	private List<ItemStack> outputs;
	private ItemStack output;
	private ItemStack input;
	
	public PageFurnace(String id, ItemStack output) {
		super(id);
		this.outputs = Collections.singletonList(output);
		this.output = output;
		this.input = getFirstRecipeForItem(output);
	}
	
	public PageFurnace(String id, List<ItemStack> outputs) {
		super(id);
		this.outputs = outputs;
		this.output = outputs.get(0);
		this.input = getFirstRecipeForItem(output);
	}

	private static ItemStack getFirstRecipeForItem(ItemStack resultingItem) {
		for (Entry<ItemStack, ItemStack> recipe : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
			if (recipe == null) continue;

			ItemStack result = recipe.getValue();
			if (result == null || !ItemUtil.canCombine(result, resultingItem)) continue;

			return recipe.getKey();
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
    public void initGui(GuiGuideChapter gui, int startX, int startY){
		outputIndex = 0;
    }
	
	private int outputIndex;
	private int arrowTimer;
	private int flameTimer;
	@SideOnly(Side.CLIENT)
    public void updateScreen(GuiGuideChapter gui, int startX, int startY, int timer){
		
		if(!GuiScreen.isShiftKeyDown() && Util.isMultipleOf(timer, 60)){
			ItemStack newStack = outputs.get(outputIndex);
			this.output = newStack;
			this.input = getFirstRecipeForItem(newStack);
			
			outputIndex++;
			outputIndex%=outputs.size();
		}
		
		if(arrowTimer < 200){
			arrowTimer++;
		} else {
			arrowTimer = 0;
		}
		if(flameTimer < 100){
			flameTimer++;
		} else {
			flameTimer = 0;
		}
	}
	
	private ResourceLocation furnaceLocation = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		super.drawBackground(gui, startX, startY, mouseX, mouseY, partialTicks);
		String title = GuidePages.getTitle(getChapter(), this);
		if(title != null && !title.isEmpty()){
			title = title.replaceAll("<n>", "\n");
			GlStateManager.pushMatrix();
			boolean oldUnicode = gui.getFont().getUnicodeFlag();
			gui.getFont().setUnicodeFlag(false);

			gui.getFont().drawString(title, startX+6, startY+10, 0, false);

			gui.getFont().setUnicodeFlag(oldUnicode);
			GlStateManager.popMatrix();
		}
		
		int x = 10;
		int y = 20;
		SpriteData guiSprite = new SpriteData(55-2, 16-2, 82+4, 54+4);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(startX+x, startY+y, 0);
		GlStateManager.scale(2, 2, 1);
		GuiComponentSprite.renderSprite(gui.mc, 0, 0, 0, 0, mouseX, mouseY, guiSprite, furnaceLocation, 1f, 1f, 1f);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(startX+x+52, startY+y+41, 0);
		GlStateManager.scale(2, 2, 1);
		SpriteData arrowSprite = new SpriteData(176, 14, (24.0d*((arrowTimer*1.0d)/200.0d)), 17);
		GuiComponentSprite.renderSprite(gui.mc, 0, 0, 0, 0, mouseX, mouseY, arrowSprite, furnaceLocation, 1f, 1f, 1f);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(startX+x+8, startY+y+44.5, 0);
		GlStateManager.scale(2, 2, 1);
		double shrink = (14-(14*((100.0d-(flameTimer*1.0d))/100.0d)));
		SpriteData flameSprite = new SpriteData(176, shrink, 14, 14-shrink);
		GuiComponentSprite.renderSprite(gui.mc, 0, shrink, 0, 0, mouseX, mouseY, flameSprite, furnaceLocation, 1f, 1f, 1f);
		GlStateManager.popMatrix();

	}
	
	@SideOnly(Side.CLIENT)
    public void drawForeground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		int itemBoxSize = 34;

		if(input !=null){
			GlStateManager.pushMatrix();
			GlStateManager.translate(startX + 16, startY+26, 0);
			GlStateManager.scale(2, 2, 1);
			drawItemStack(input, 0, 0, ""+(input.stackSize > 1 ? input.stackSize : ""));
        	GL11.glPopMatrix();
	        RenderHelper.enableStandardItemLighting();
			if (mouseX > startX + 16 - 2 && mouseX < startX + 16 - 2 + itemBoxSize &&
					mouseY > startY+26 - 2 && mouseY < startY+26 - 2 + itemBoxSize) {
				drawItemStackTooltip(input, mouseX, mouseY);
			}
		}
		if(output !=null){
			GlStateManager.pushMatrix();
			GlStateManager.translate(startX + 136, startY+61, 0);
			GlStateManager.scale(2, 2, 1);
			drawItemStack(output, 0, 0, ""+(output.stackSize > 1 ? output.stackSize : ""));
			GlStateManager.popMatrix();
			if (mouseX > startX + 136 - 2 && mouseX < startX + 136 - 2 + itemBoxSize &&
					mouseY > startY+61 - 2 && mouseY < startY+61 - 2 + itemBoxSize) {
				drawItemStackTooltip(output, mouseX, mouseY);
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
		if (par1ItemStack != null) font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		if (font == null) font = Minecraft.getMinecraft().fontRendererObj;
		GlStateManager.enableDepth();
		itemRenderer.renderItemAndEffectIntoGUI(par1ItemStack, par2, par3);
		itemRenderer.renderItemOverlayIntoGUI(font, par1ItemStack, par2, par3, par4Str);
		GlStateManager.disableDepth();
		itemRenderer.zLevel = 0.0F;
	}
	
}
