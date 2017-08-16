package alec_wam.CrystalMod.items.guide.page;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuiGuideIndex;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageIcon extends GuidePage {

	
	private static final int CRAZY_1 = 0x505000FF;
	private static final int CRAZY_2 = (CRAZY_1 & 0xFEFEFE) >> 1 | CRAZY_1 & -0xFF000000;
	private static final int CRAZY_3 = 0xF0100010;

	protected static RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
	private NonNullList<ItemStack> stacks;
	private ItemStack currentItem = ItemStackTools.getEmptyStack();
	
	public PageIcon(String id, ItemStack stack){
		this(id, NonNullList.withSize(1, stack));
	}
	
	public PageIcon(String id, NonNullList<ItemStack> resultingItem) {
		super(id);
		if(!resultingItem.isEmpty())currentItem = resultingItem.get(0);
		stacks = resultingItem;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initGui(GuiGuideChapter gui, int startX, int startY){
		this.listIndex = 0;
    }
	
	public int listIndex = 0;
	@Override
	@SideOnly(Side.CLIENT)
    public void updateScreen(GuiGuideChapter gui, int startX, int startY, int timer){
		if(!GuiScreen.isShiftKeyDown() && Util.isMultipleOf(timer, 20)){
			currentItem = stacks.get(listIndex);
			
			listIndex++;
			listIndex%=stacks.size();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		super.drawBackground(gui, startX, startY, mouseX, mouseY, partialTicks);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void drawForeground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		int x = 20;
		int y = 20;
		int itemBoxSize = 34;

		ItemStack outputStack = currentItem;
		
		if(ItemStackTools.isValid(outputStack)){
			GlStateManager.pushMatrix();
			GlStateManager.translate(startX+80, startY+30, 0);
			GlStateManager.scale(2, 2, 1);
			drawItemStack(outputStack, 0, 0, ""+(ItemStackTools.getStackSize(outputStack) > 1 ? ItemStackTools.getStackSize(outputStack) : ""));
			GlStateManager.popMatrix();
			if (mouseX > startX + 80 - 2 && mouseX < startX + 80 - 2 + itemBoxSize &&
					mouseY > startY+30 - 2 && mouseY < startY+30 - 2 + itemBoxSize) {
				drawItemStackTooltip(outputStack, mouseX, mouseY);
			}
		}
		
		String text = GuidePages.getText(getChapter(), this);
		String title = GuidePages.getTitle(getChapter(), this);
		x = startX+6;
		if(title != null && !title.isEmpty()){
			title = title.replaceAll("<n>", "\n");
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

	@Override
	public boolean matchesFilter(String filter) {
		for(ItemStack stack : stacks){
			if(Lang.translateToLocal(stack.getUnlocalizedName()).toLowerCase(GuiGuideIndex.getLocale()).contains(filter)){
				return true;
			}
		}
		return false;
	}
	
}
