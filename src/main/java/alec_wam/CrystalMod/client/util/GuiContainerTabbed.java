package alec_wam.CrystalMod.client.util;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.helpers.Strings;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiContainerTabbed extends GuiContainer {
	
	public final TabManager tabManager = new TabManager(this);
	public static final ResourceLocation TAB_TEXTURE = new ResourceLocation("crystalmod", "textures/gui/tab.png");
	public ResourceLocation texture;
	
	public GuiContainerTabbed(Container par1Container, ResourceLocation tex) {
		super(par1Container);
		texture = tex;
		initTabs();
	}
	
	protected void initTabs() {
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if(texture !=null){
			Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
			drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, this.zLevel);
		drawTabs(par1, par2);
		GL11.glPopMatrix();
	}

	protected void drawTabs(int x, int y) {
		tabManager.drawTabs(x, y);
	}
	
	// / MOUSE CLICKS
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		// / Handle ledger clicks
		tabManager.handleMouseClicked(mouseX, mouseY, mouseButton);
	}
		
	public class TabManager {

		protected ArrayList<Tab> tabs = new ArrayList<Tab>();
		protected List<Rectangle> tabBounds = Lists.newArrayList();
		private GuiContainerTabbed gui;

		public TabManager(GuiContainerTabbed gui) {
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

		public Tab getAtPosition(int mX, int mY) {

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
					tabBounds.add(new Rectangle(0, 0, 0, 0));
			    }
				int tabX = guiLeft+xSize;
				int tabY = guiTop+xPos;
				tabBounds.set(tabIndex, new Rectangle(tabX, tabY, tab.currentWidth, tab.getHeight()+3));
				xPos += tab.getHeight();
				tabIndex++;
			}

			Tab tab = getAtPosition(mouseX, mouseY);
			if (tab != null) {
				int startX = mouseX - ((gui.width - gui.xSize) / 2) + 12;
				int startY = mouseY - ((gui.height - gui.ySize) / 2) - 12;

				String tooltip = tab.getTooltip();
				if(Strings.isNotEmpty(tooltip)){
					int textWidth = fontRendererObj.getStringWidth(tooltip);
					drawGradientRect(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3, 0xc0000000, 0xc0000000);
					fontRendererObj.drawStringWithShadow(tooltip, startX, startY, -1);
				}
				
			}
		}

		public void handleMouseClicked(int x, int y, int mouseButton) {

			if (mouseButton == 0) {

				Tab tab = this.getAtPosition(x, y);

				// Default action only if the mouse click was not handled by the
				// ledger itself.
				if (tab != null && !tab.handleMouseClicked(x, y, mouseButton)) {

					for (Tab other : tabs) {
						if (other != tab && other.isOpen()) {
							other.toggleOpen();
						}
					}
					tab.toggleOpen();
				}
			}

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

		public abstract String getTooltip();

		public boolean handleMouseClicked(int x, int y, int mouseButton) {
			return false;
		}

		public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {

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
			GlStateManager.color(red, green, blue, 1.0F);
		}
		
		protected void drawBackground(int x, int y) {
            //GL11.glPushMatrix();
			setGLColorFromInt(overlayColor);

			Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
			drawTexturedModalRect(x, y, 0, 0, 256 - currentHeight, 4, currentHeight);
			drawTexturedModalRect(x + 4, y, 0, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
			// Add in top left corner again
			drawTexturedModalRect(x, y, 0, 0, 0, 4, 4);

			drawTexturedModalRect(x + 4, y + 4, 0, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);

			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
			
		}

		public void drawTexturedModalRect(int x, int y, int zLevel, int textureX, int textureY, int width, int height)
	    {
	        Tessellator tessellator = Tessellator.getInstance();
	        VertexBuffer vertexbuffer = tessellator.getBuffer();
	        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        vertexbuffer.pos(x + 0, y + height, zLevel).tex((textureX + 0) * 0.00390625F, (textureY + height) * 0.00390625F).endVertex();
	        vertexbuffer.pos(x + width, y + height, zLevel).tex((textureX + width) * 0.00390625F, (textureY + height) * 0.00390625F).endVertex();
	        vertexbuffer.pos(x + width, y + 0, zLevel).tex((textureX + width) * 0.00390625F, (textureY + 0) * 0.00390625F).endVertex();
	        vertexbuffer.pos(x + 0, y + 0, zLevel).tex((textureX + 0) * 0.00390625F, (textureY + 0) * 0.00390625F).endVertex();
	        tessellator.draw();
	    }
	}

	public List<Rectangle> getBlockingAreas() {
		return tabManager.tabBounds;
	}

		
}
