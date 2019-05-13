package alec_wam.CrystalMod.client.gui;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonIcon extends GuiButton { 
	public static final ResourceLocation TEXTURE_ICONS = CrystalMod.resourceL("textures/gui/elements/icons.png");
	
	public ResourceLocation texture;
	public int iconX, iconY;
	
	public GuiButtonIcon(int buttonId, int x, int y, int iconX, int iconY) {
		this(buttonId, x, y, iconX, iconY, TEXTURE_ICONS);
	}
	
	public GuiButtonIcon(int buttonId, int x, int y, int iconX, int iconY, ResourceLocation iconTexture) {
		super(buttonId, x, y, 20, 20, "");
		this.texture = iconTexture;
		this.iconX = iconX;
		this.iconY = iconY;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		if (this.visible)
        {
            GlStateManager.pushMatrix();
			Minecraft.getInstance().getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            
            Minecraft.getInstance().getTextureManager().bindTexture(texture);
            this.drawTexturedModalRect(this.x + 2, this.y + 2, iconX, iconY, 16, 16);            
            GlStateManager.popMatrix();
        }
	}

}
