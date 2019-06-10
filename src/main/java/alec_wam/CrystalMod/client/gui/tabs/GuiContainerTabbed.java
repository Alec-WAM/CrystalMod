package alec_wam.CrystalMod.client.gui.tabs;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.client.gui.GuiElementContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiContainerTabbed<C extends Container> extends GuiElementContainer<C> {

	public final TabManager tabManager = new TabManager(this);
	public static final ResourceLocation TAB_TEXTURE = new ResourceLocation("crystalmod", "textures/gui/tab.png");
	public ResourceLocation texture;

	public GuiContainerTabbed(C par1Container, PlayerInventory inventory, ITextComponent title, ResourceLocation tex) {
		super(par1Container, inventory, title, tex);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, this.blitOffset);
		drawTabs(par1, par2);
		GL11.glPopMatrix();
	}

	protected void drawTabs(int x, int y) {
		tabManager.drawTabs(x, y);
	}

	// / MOUSE CLICKS
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if(tabManager.handleMouseClicked(mouseX, mouseY, mouseButton)){
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public class TabManager {

		protected ArrayList<Tab> tabs = new ArrayList<Tab>();
		protected List<Rectangle2d> tabBounds = Lists.newArrayList();
		private GuiContainerTabbed<C> gui;

		public TabManager(GuiContainerTabbed<C> gui) {
			this.gui = gui;
		}

		public ArrayList<Tab> getTabs(){
			return tabs;
		}

		public void add(Tab tab) {
			this.tabs.add(tab);
		}

		/**
		 * Inserts a ledger into the next-to-last position.
		 *
		 * @param ledger
		 */
		public void insert(Tab tab) {
			this.tabs.add(tabs.size() - 1, tab);
		}

		public Tab getAtPosition(double mX, double mY) {

			int xShift = ((gui.width - gui.xSize) / 2) + gui.xSize;
			int yShift = ((gui.height - gui.ySize) / 2) + 8;

			for (int i = 0; i < tabs.size(); i++) {
				Tab tab = tabs.get(i);
				if (!tab.isVisible()) {
					continue;
				}

				tab.currentShiftX = xShift;
				tab.currentShiftY = yShift;
				if (tab.intersectsWith(mX, mY, xShift, yShift)) {
					return tab;
				}

				yShift += tab.getHeight();
			}

			return null;
		}

		protected void drawTabs(int mouseX, int mouseY) {

			int xPos = 8;
			int tabIndex = 0;
			for (Tab tab : tabs) {

				tab.update();
				if (!tab.isVisible()) {
					continue;
				}

				tab.draw(xSize, xPos);
				while (tabBounds.size() <= tabIndex) {
					tabBounds.add(new Rectangle2d(0, 0, 0, 0));
				}
				int tabX = guiLeft+xSize;
				int tabY = guiTop+xPos;
				tabBounds.set(tabIndex, new Rectangle2d(tabX, tabY, tab.currentWidth, tab.getHeight()+3));
				xPos += tab.getHeight();
				tabIndex++;
			}

			Tab tab = getAtPosition(mouseX, mouseY);
			if (tab != null) {
				int startX = mouseX - ((gui.width - gui.xSize) / 2);
				int startY = mouseY - ((gui.height - gui.ySize) / 2);

				List<String> tooltip = tab.getTooltip(mouseX, mouseY);
				if(!tooltip.isEmpty()){
					gui.renderTooltip(tooltip, startX, startY);
				}
			}
		}

		public boolean handleMouseClicked(double x, double y, int mouseButton) {

			if (mouseButton == 0) {

				Tab tab = this.getAtPosition(x, y);

				// Default action only if the mouse click was not handled by the
				// ledger itself.
				if (tab != null) {
					if(tab.handleMouseClicked(x, y, mouseButton)){
						return true;
					}

					for (Tab other : tabs) {
						if (other != tab && other.isOpen()) {
							other.toggleOpen();
						}
					}
					tab.toggleOpen();
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Side ledger for guis
	 */
	public static abstract class Tab {
		public int currentShiftX = 0;
		public int currentShiftY = 0;
		protected int overlayColor = 0xffffff;
		protected int limitWidth = 128;
		protected int maxWidth = 124;
		protected int minWidth = 24;
		protected int currentWidth = minWidth;
		protected int maxHeight = 24;
		protected int minHeight = 24;
		protected int currentHeight = minHeight;
		private boolean open;

		public void update() {
			int speed = 12;
			// Width
			if (open && currentWidth < maxWidth) {
				currentWidth += speed;
				if(currentWidth > maxWidth)currentWidth = maxWidth;
			} else if (!open && currentWidth > minWidth) {
				currentWidth -= speed;
				if(currentWidth < minWidth)currentWidth = minWidth;
			}

			// Height
			if (open && currentHeight < maxHeight) {
				currentHeight += speed;
				if(currentHeight > maxHeight)currentHeight = maxHeight;
			} else if (!open && currentHeight > minHeight) {
				currentHeight -= speed;
				if(currentHeight < minHeight)currentHeight = minHeight;
			}
		}

		public int getHeight() {
			return currentHeight;
		}

		public abstract void draw(int x, int y);

		public abstract List<String> getTooltip(int mouseX, int mouseY);

		public boolean handleMouseClicked(double x, double y, int mouseButton) {
			return false;
		}

		public boolean intersectsWith(double mouseX, double mouseY, int shiftX, int shiftY) {

			if (mouseX >= shiftX && mouseX <= shiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + getHeight()) {
				return true;
			}

			return false;
		}

		public void setFullyOpen() {
			open = true;
			currentWidth = maxWidth;
			currentHeight = maxHeight;
		}

		public void toggleOpen() {
			if (open) {
				open = false;
			} else {
				open = true;
			}
		}

		public boolean isVisible() {
			return true;
		}

		public boolean isOpen() {
			return this.open;
		}

		protected boolean isFullyOpened() {
			return currentWidth >= maxWidth;
		}
		public void setGLColorFromInt(int color) {
			float red = (color >> 16 & 255) / 255.0F;
			float green = (color >> 8 & 255) / 255.0F;
			float blue = (color & 255) / 255.0F;
			GlStateManager.color4f(red, green, blue, 1.0F);
		}

		protected void drawBackground(int x, int y) {
			//GL11.glPushMatrix();
			setGLColorFromInt(overlayColor);

			Minecraft.getInstance().getTextureManager().bindTexture(TAB_TEXTURE);
			drawTexturedModalRect(x, y, 0, 0, 256 - currentHeight, 4, currentHeight);
			drawTexturedModalRect(x + 4, y, 0, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
			// Add in top left corner again
			drawTexturedModalRect(x, y, 0, 0, 0, 4, 4);

			drawTexturedModalRect(x + 4, y + 4, 0, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);

			GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0F);

		}

		public void drawTexturedModalRect(int x, int y, int zLevel, int textureX, int textureY, int width, int height)
		{
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0, y + height, zLevel).tex((textureX + 0) * 0.00390625F, (textureY + height) * 0.00390625F).endVertex();
			vertexbuffer.pos(x + width, y + height, zLevel).tex((textureX + width) * 0.00390625F, (textureY + height) * 0.00390625F).endVertex();
			vertexbuffer.pos(x + width, y + 0, zLevel).tex((textureX + width) * 0.00390625F, (textureY + 0) * 0.00390625F).endVertex();
			vertexbuffer.pos(x + 0, y + 0, zLevel).tex((textureX + 0) * 0.00390625F, (textureY + 0) * 0.00390625F).endVertex();
			tessellator.draw();
		}
	}

	public List<Rectangle2d> getBlockingAreas() {
		return tabManager.tabBounds;
	}


}
