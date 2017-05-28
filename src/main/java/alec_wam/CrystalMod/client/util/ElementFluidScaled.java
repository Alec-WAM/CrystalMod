package alec_wam.CrystalMod.client.util;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;

public class ElementFluidScaled
extends ElementBase
{
public FluidStack fluid;

public ElementFluidScaled(GuiElementContainer gui, int x, int y)
{
  super(gui, x, y);
}

public ElementFluidScaled setFluid(FluidStack fluid)
{
  this.fluid = fluid;
  return this;
}

public void drawBackground(int x, int y, float f)
{
  if (fluid == null || fluid.getFluid() == null) {
      return;
    }

    TextureAtlasSprite icon = RenderUtil.getStillTexture(fluid);
    if (icon == null) {
      return;
    }

    int renderAmount = (int) Math.max(this.sizeY, 1);
    int posY = (int) (this.posY + this.sizeY - renderAmount);

    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    int color = fluid.getFluid().getColor(fluid);
    GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
    
    GlStateManager.enableBlend();    
    for (int i = 0; i < this.sizeX; i += 16) {
      for (int j = 0; j < renderAmount; j += 16) {
        int drawWidth = (int) Math.min(this.sizeX - i, 16);
        int drawHeight = Math.min(renderAmount - j, 16);

        int drawX = (int) (this.posX + i);
        int drawY = posY + j;

        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        tes.pos(drawX, drawY + drawHeight, 0).tex(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
        tes.pos(drawX + drawWidth, drawY + drawHeight, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
        tes.pos(drawX + drawWidth, drawY, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
        tes.pos(drawX, drawY, 0).tex(minU, minV).endVertex();
        tessellator.draw();
      }
    }
    //GlStateManager.disableBlend();
}

public void drawForeground(int x, int y) {
	if(texture == null)return;
    GlStateManager.color(1, 1, 1, 1);
    Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    drawTexturedModalRect(posX, posY, 24, 0, sizeX, sizeY);
}
}


