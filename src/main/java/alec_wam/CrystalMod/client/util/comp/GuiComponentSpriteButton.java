package alec_wam.CrystalMod.client.util.comp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.client.util.SpriteData;

public class GuiComponentSpriteButton extends GuiComponentSprite {

	private final SpriteData hoverIcon;

	public GuiComponentSpriteButton(int x, int y, SpriteData icon, SpriteData hoverIcon, ResourceLocation texture) {
		super(x, y, icon, texture);
		this.hoverIcon = hoverIcon;
	}

	@Override
	protected void doRender(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (icon == null) { return; }
		if (texture != null) minecraft.renderEngine.bindTexture(texture);
		GL11.glColor3f(r, g, b);
		boolean mouseOver = isMouseOver(mouseX, mouseY);
		boolean pressed = mouseOver && Mouse.isButtonDown(0);
		int offset = pressed? 1 : 0;
		SpriteData useIcon = hoverIcon != null && mouseOver? hoverIcon : icon;
		Gui.drawModalRectWithCustomSizedTexture(offsetX + x + offset, offsetY + y + offset, (float)useIcon.getU(), (float)useIcon.getV(), useIcon.getWidth(), useIcon.getHeight(), 256, 256);
	}
}