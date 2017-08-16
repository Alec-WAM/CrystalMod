package alec_wam.CrystalMod.client.util.comp;

import alec_wam.CrystalMod.client.util.SpriteData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiComponentSprite extends BaseComponent {

	protected SpriteData icon;
	protected ResourceLocation texture;
	protected float r = 1, g = 1, b = 1;
	protected boolean overlay_mode;

	public boolean isOverlay() {
		return overlay_mode;
	}

	public BaseComponent setOverlayMode(boolean isOverlay) {
		this.overlay_mode = isOverlay;
		return this;
	}

	public GuiComponentSprite(int x, int y, SpriteData icon, ResourceLocation texture) {
		super(x, y);
		this.texture = texture;
		this.icon = icon;
	}

	public GuiComponentSprite setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		if (!overlay_mode) {
			doRender(minecraft, offsetX, offsetY, mouseX, mouseY);
		}
	}

	@Override
	public void renderOverlay(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderOverlay(minecraft, offsetX, offsetY, mouseX, mouseY);
		if (overlay_mode) {
			doRender(minecraft, offsetX, offsetY, mouseX, mouseY);
		}
	}

	protected void doRender(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		renderSprite(minecraft, x, y, offsetX, offsetY, mouseX, mouseY, icon, texture, r, g, b);
	}
	
	public static void renderSprite(Minecraft minecraft, double x, double y, double offsetX, double offsetY, int mouseX, int mouseY, SpriteData icon, ResourceLocation texture, float r, float g, float b) {
		if (icon == null) { return; }
		if (texture != null) minecraft.renderEngine.bindTexture(texture);
		GlStateManager.color(r, g, b);
		double xCoord = x+offsetX;
		double yCoord = y+offsetY;
		boolean flipedX = icon.getU() < 0;
		float u = flipedX ? (float) -icon.getU() : (float) icon.getU();
		float v = (float) icon.getV();
		double width = icon.getWidth();
		double height = icon.getHeight();
		
		//GuiComponentSpriteButton.drawModalRectWithCustomSizedTexture(offsetX + x, offsetY + y, (float)u, (float)icon.getV(), icon.getWidth(), icon.getHeight(), 256, 256);

		
	    float f = flipedX ? -(1.0F / 256) : (1.0F / 256);
        float f1 = 1.0F / 256;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        double minU = (u * f);
        double maxU = ((u + (float)width) * f);
        vertexbuffer.pos(xCoord, yCoord + height, 0.0D).tex(minU, (v + (float)height) * f1).endVertex();
        vertexbuffer.pos(xCoord + width, yCoord + height, 0.0D).tex(maxU, (v + (float)height) * f1).endVertex();
        vertexbuffer.pos(xCoord + width, yCoord, 0.0D).tex(maxU, v * f1).endVertex();
        vertexbuffer.pos(xCoord, yCoord, 0.0D).tex(minU, v * f1).endVertex();
        tessellator.draw();
	}

	@Override
	public int getWidth() {
		if (icon != null) { return (int)icon.getWidth(); }
		return 0;
	}

	@Override
	public int getHeight() {
		if (icon != null) { return (int)icon.getHeight(); }
		return 0;
	}

	public void setIcon(SpriteData icon) {
		this.icon = icon;
	}
}