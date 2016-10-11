package alec_wam.CrystalMod.client.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import alec_wam.CrystalMod.util.Lang;

/**
 * Base class for a modular GUIs. Works with Elements {@link ElementBase} and Tabs {@link TabBase} which are both modular elements.
 *
 * @author King Lemming
 */
public abstract class GuiElementContainer extends GuiContainer {

	public static final SoundHandler guiSoundManager = FMLClientHandler.instance().getClient().getSoundHandler();

	protected boolean drawTitle = true;
	protected boolean drawInventory = true;
	protected int mouseX = 0;
	protected int mouseY = 0;

	protected int lastIndex = -1;

	protected String name;
	protected ResourceLocation texture;

	protected ArrayList<ElementBase> elements = new ArrayList<ElementBase>();

	protected List<String> tooltip = new LinkedList<String>();
	protected boolean tooltips = true;

	/*public static void playSound(String name, float volume, float pitch) {

		guiSoundManager.playSound(new SoundBase(name, volume, pitch));
	}*/

	public GuiElementContainer(Container container) {

		super(container);
	}

	public GuiElementContainer(Container container, ResourceLocation texture) {

		super(container);
		this.texture = texture;
	}

	@Override
	public void initGui() {

		super.initGui();
		elements.clear();
	}

	@Override
	public void drawScreen(int x, int y, float partialTick) {

		updateElementInformation();

		super.drawScreen(x, y, partialTick);

		if (tooltips && mc.thePlayer.inventory.getItemStack() == null) {
			addTooltips(tooltip);
			drawTooltip(tooltip);
		}
		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		updateElements();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (drawTitle) {
			fontRendererObj.drawString(Lang.translateToLocal(name), getCenteredOffset(Lang.translateToLocal(name)), 6, 0x404040);
		}
		if (drawInventory) {
			fontRendererObj.drawString(Lang.translateToLocal("container.inventory"), 8, ySize - 96 + 3, 0x404040);
		}
		drawElements(0, true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(texture !=null){
		bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		drawElements(partialTick, false);
		GL11.glPopMatrix();
	}

	@Override
	protected void keyTyped(char characterTyped, int keyPressed) {

		for (int i = elements.size(); i-- > 0;) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled()) {
				continue;
			}
			if (c.onKeyTyped(characterTyped, keyPressed)) {
				return;
			}
		}
		try {
			super.keyTyped(characterTyped, keyPressed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleMouseInput() {

		int x = Mouse.getEventX() * width / mc.displayWidth;
		int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		int wheelMovement = Mouse.getEventDWheel();

		if (wheelMovement != 0) {
			for (int i = elements.size(); i-- > 0;) {
				ElementBase c = elements.get(i);
				if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
					continue;
				}
				if (c.onMouseWheel(mouseX, mouseY, wheelMovement)) {
					return;
				}
			}
		}
		try {
			super.handleMouseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mouseButton) {

		mX -= guiLeft;
		mY -= guiTop;

		for (int i = elements.size(); i-- > 0;) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mX, mY)) {
				continue;
			}
			if (c.onMousePressed(mX, mY, mouseButton)) {
				return;
			}
		}

		mX += guiLeft;
		mY += guiTop;
		try {
			super.mouseClicked(mX, mY, mouseButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void mouseReleased(int mX, int mY, int mouseButton) {
		mX -= guiLeft;
		mY -= guiTop;

		if (mouseButton >= 0 && mouseButton <= 2) { // 0:left, 1:right, 2: middle
			for (int i = elements.size(); i-- > 0;) {
				ElementBase c = elements.get(i);
				if (!c.isVisible() || !c.isEnabled()) { // no bounds checking on mouseUp events
					continue;
				}
				c.onMouseReleased(mX, mY);
			}
		}
		mX += guiLeft;
		mY += guiTop;

		super.mouseReleased(mX, mY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mX, int mY, int lastClick, long timeSinceClick) {

		lastIndex = -1;
	    super.mouseClickMove(mX, mY, lastClick, timeSinceClick);
	}

	public Slot getSlotAtPosition(int xCoord, int yCoord) {

		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k) {
			Slot slot = (Slot) this.inventorySlots.inventorySlots.get(k);

			if (this.isMouseOverSlot(slot, xCoord, yCoord)) {
				return slot;
			}
		}
		return null;
	}

	public boolean isMouseOverSlot(Slot theSlot, int xCoord, int yCoord) {

		return this.isPointInRegion(theSlot.xDisplayPosition, theSlot.yDisplayPosition, 16, 16, xCoord, yCoord);
	}

	/**
	 * Draws the elements for this GUI.
	 */
	protected void drawElements(float partialTick, boolean foreground) {

		if (foreground) {
			for (int i = 0; i < elements.size(); i++) {
				ElementBase element = elements.get(i);
				if (element.isVisible()) {
					element.drawForeground(mouseX, mouseY);
				}
			}
		} else {
			for (int i = 0; i < elements.size(); i++) {
				ElementBase element = elements.get(i);
				if (element.isVisible()) {
					element.drawBackground(mouseX, mouseY, partialTick);
				}
			}
		}
	}

	/**
	 * Called by NEI if installed
	 */
	// @Override
	public List<String> handleTooltip(int mousex, int mousey, List<String> tooltip) {

		if (mc.thePlayer.inventory.getItemStack() == null) {
			addTooltips(tooltip);
		}
		return tooltip;
	}

	public void addTooltips(List<String> tooltip) {

		ElementBase element = getElementAtPosition(mouseX, mouseY);

		if (element != null && element.isVisible()) {
			element.addTooltip(tooltip);
		}
	}

	/* ELEMENTS */
	public ElementBase addElement(ElementBase element) {

		elements.add(element);
		return element;
	}

	protected ElementBase getElementAtPosition(int mX, int mY) {

		for (int i = elements.size(); i-- > 0;) {
			ElementBase element = elements.get(i);
			if (element.intersectsWith(mX, mY)) {
				return element;
			}
		}
		return null;
	}

	protected final void updateElements() {

		for (int i = elements.size(); i-- > 0;) {
			ElementBase c = elements.get(i);
			if (c.isVisible() && c.isEnabled()) {
				c.update(mouseX, mouseY);
			}
		}
	}

	protected void updateElementInformation() {

	}

	public void handleElementButtonClick(String buttonName, int mouseButton) {

	}

	/* HELPERS */
	public void bindTexture(ResourceLocation texture) {

		mc.renderEngine.bindTexture(texture);
	}

	public void drawTooltip(List<String> list) {

		drawTooltipHoveringText(list, mouseX + guiLeft, mouseY + guiTop, fontRendererObj);
		tooltip.clear();
	}

	@SuppressWarnings("rawtypes")
	protected void drawTooltipHoveringText(List list, int x, int y, FontRenderer font) {

		if (list == null || list.isEmpty()) {
			return;
		}
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int k = 0;
		Iterator iterator = list.iterator();

		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			int l = font.getStringWidth(s);

			if (l > k) {
				k = l;
			}
		}
		int i1 = x + 12;
		int j1 = y - 12;
		int k1 = 8;

		if (list.size() > 1) {
			k1 += 2 + (list.size() - 1) * 10;
		}
		if (i1 + k > this.width) {
			i1 -= 28 + k;
		}
		if (j1 + k1 + 6 > this.height) {
			j1 = this.height - k1 - 6;
		}
		this.zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int l1 = -267386864;
		this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
		this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
		this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
		this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
		this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
		int i2 = 1347420415;
		int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
		this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
		this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
		this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
		this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

		for (int k2 = 0; k2 < list.size(); ++k2) {
			String s1 = (String) list.get(k2);
			font.drawStringWithShadow(s1, i1, j1, -1);

			if (k2 == 0) {
				j1 += 2;
			}
			j1 += 10;
		}
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	/**
	 * Passthrough method for tab use.
	 */
	public void mouseClicked(int mouseButton) {

		try {
			super.mouseClicked(guiLeft + mouseX, guiTop + mouseY, mouseButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FontRenderer getFontRenderer() {

		return fontRendererObj;
	}

	protected int getCenteredOffset(String string) {

		return this.getCenteredOffset(string, xSize);
	}

	protected int getCenteredOffset(String string, int xWidth) {

		return (xWidth - fontRendererObj.getStringWidth(string)) / 2;
	}

	public int getGuiLeft() {

		return guiLeft;
	}

	public int getGuiTop() {

		return guiTop;
	}

	public int getMouseX() {

		return mouseX;
	}

	public int getMouseY() {

		return mouseY;
	}

	public void overlayRecipe() {

	}

}