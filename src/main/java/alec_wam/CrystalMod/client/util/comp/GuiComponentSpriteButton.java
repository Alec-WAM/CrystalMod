package alec_wam.CrystalMod.client.util.comp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
		drawModalRectWithCustomSizedTexture(offsetX + x + offset, offsetY + y + offset, (float)useIcon.getU(), (float)useIcon.getV(), useIcon.getWidth(), useIcon.getHeight(), 256, 256);
	}
	
	public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, double width, double height, float textureWidth, float textureHeight)
    {
		boolean flipedX = u < 0;
        float f = flipedX ? -(1.0F / textureWidth) : (1.0F / textureWidth);
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex();
        vertexbuffer.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
    }
}