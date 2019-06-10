package alec_wam.CrystalMod.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class ButtonIcon extends Button { 
	public static final ResourceLocation TEXTURE_ICONS = CrystalMod.resourceL("textures/gui/elements/icons.png");
	
	public ResourceLocation texture;
	public int iconX, iconY;
	
	public ButtonIcon(int x, int y, int iconX, int iconY, Button.IPressable p_i51141_6_) {
		this(x, y, iconX, iconY, p_i51141_6_, TEXTURE_ICONS);
	}
	
	public ButtonIcon(int x, int y, int iconX, int iconY, Button.IPressable p_i51141_6_, ResourceLocation iconTexture) {
		super(x, y, 20, 20, "", p_i51141_6_);
		this.texture = iconTexture;
		this.iconX = iconX;
		this.iconY = iconY;
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks){
		if (this.visible)
        {
            GlStateManager.pushMatrix();
			Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getYImage(this.isHovered);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            
            Minecraft.getInstance().getTextureManager().bindTexture(texture);
            this.blit(this.x + 2, this.y + 2, iconX, iconY, 16, 16);            
            GlStateManager.popMatrix();
        }
	}

}
