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
	private int mode;
	private int quantity;

	public ElementFluidScaled(GuiElementContainer gui, int x, int y)
	{
	  super(gui, x, y);
	}

	public ElementFluidScaled setFluid(FluidStack fluid)
	{
	  this.fluid = fluid;
	  return this;
	}

	public ElementFluidScaled setMode(int mode) {

		this.mode = mode;
		return this;
	}

	public ElementFluidScaled setQuanitity(int quantity) {

		this.quantity = quantity;
		return this;
	}

	@Override
	public void drawBackground(int x, int y, float f)
	{
	  if (fluid == null || fluid.getFluid() == null) {
	      return;
	    }
	
	    TextureAtlasSprite icon = RenderUtil.getStillTexture(fluid);
	    if (icon == null) {
	      return;
	    }
	
	    int renderAmount = Math.max(this.sizeY, 1);
	    int posY = this.posY + this.sizeY - renderAmount;
	
	    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	    int color = fluid.getFluid().getColor(fluid);
	    GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
	    
	    GlStateManager.enableBlend();    
	    int drawX = this.posX;
	    int drawY = posY;
	
	    double minU = icon.getMinU();
	    double maxU = icon.getMaxU();
	    double minV = icon.getMinV();
	    double maxV = icon.getMaxV();
	
	    Tessellator tessellator = Tessellator.getInstance();
	    VertexBuffer tes = tessellator.getBuffer();
	    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	    if(mode == 0){
	    	tes.pos(drawX, drawY + sizeY, 0).tex(minU, maxV).endVertex();
	    	tes.pos(drawX + quantity, drawY + sizeY, 0).tex(minU, maxV).endVertex();
	    	tes.pos(drawX + quantity, drawY, 0).tex(maxU, minV).endVertex();
	    	tes.pos(drawX, drawY, 0).tex(minU, minV).endVertex();
	    }
	    if(mode == 1){
	    	tes.pos(drawX + sizeX - quantity, drawY + sizeY, 0).tex(minU, maxV).endVertex();
	    	tes.pos(drawX + sizeX, drawY + sizeY, 0).tex(minU, maxV).endVertex();
	    	tes.pos(drawX + sizeX, drawY, 0).tex(maxU, minV).endVertex();
	    	tes.pos(drawX + sizeX - quantity, drawY, 0).tex(minU, minV).endVertex();
	    }
	    tessellator.draw();
	}

	@Override
	public void drawForeground(int x, int y) {
		if(texture == null)return;
	    GlStateManager.color(1, 1, 1, 1);
	    Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	    drawTexturedModalRect(posX, posY, 24, 0, sizeX, sizeY);
	}
}


