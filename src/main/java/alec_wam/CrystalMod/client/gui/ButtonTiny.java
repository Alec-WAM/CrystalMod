package alec_wam.CrystalMod.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;

public class ButtonTiny extends Button {

	public ButtonTiny(int x, int y, int widthIn, int heightIn, String buttonText, Button.IPressable p_i51141_6_) {
		super(x, y, widthIn, heightIn, buttonText, p_i51141_6_);
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			Minecraft minecraft = Minecraft.getInstance();
			FontRenderer fontrenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int i = this.getYImage(this.isHovered);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			blit(this.x, this.y, this.width / 2, this.height, 0, 46 + i * 20, 10, 20, 256, 256);
			blit(this.x + this.width / 2, this.y, this.width / 2, this.height, 190, 46 + i * 20, 10, 20, 256, 256);
			this.renderBg(minecraft, mouseX, mouseY);
			int j = 14737632;
			if (packedFGColor != 0)
			{
				j = packedFGColor;
			}
			else
				if (!this.active) {
					j = 10526880;
				} else if (this.isHovered) {
					j = 16777120;
				}

			this.drawCenteredString(fontrenderer, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
		}
	}

}
