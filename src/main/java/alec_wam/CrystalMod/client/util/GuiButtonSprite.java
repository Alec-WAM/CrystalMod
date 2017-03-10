package alec_wam.CrystalMod.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonSprite extends GuiButton {

	public static final ResourceLocation TEXTURE_SPRITES = new ResourceLocation("crystalmod:textures/gui/buttonicons.png");
	public ButtonSprite sprite;
	
	public GuiButtonSprite(int buttonId, int x, int y, ButtonSprite sprite) {
		super(buttonId, x, y, 16, 16, "");
		this.sprite = sprite;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY){
		if (this.visible)
        {
            GlStateManager.pushMatrix();
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            
            mc.getTextureManager().bindTexture(TEXTURE_SPRITES);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 16*sprite.getX(), 16*sprite.getY(), 16, 16);            
            GlStateManager.popMatrix();
        }
	}

}
