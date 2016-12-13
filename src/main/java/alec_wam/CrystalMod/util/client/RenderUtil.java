package alec_wam.CrystalMod.util.client;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.util.Vector3d;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class RenderUtil {

	public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	public static void startDrawing(Tessellator tess){
		startDrawing(tess.getBuffer());
	}
	
	public static void startDrawing(VertexBuffer buffer){
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	}
	
	public static void addVertexWithUV(VertexBuffer buffer, Vector3d vec, double u, double v){
		addVertexWithUV(buffer, vec.x, vec.y, vec.z, u, v);
	}
	
	public static void addVertexWithUV(VertexBuffer buffer, double x, double y, double z, double u, double v){
		buffer.pos(x, y, z).tex(u, v).endVertex();
	}
	
	public static boolean isDrawing(Tessellator tess)
	{
		return isDrawing(tess.getBuffer());
	}
	
	public static boolean isDrawing(VertexBuffer buffer)
	{
		return ((Boolean)ReflectionUtils.getPrivateValue(buffer, VertexBuffer.class, ObfuscatedNames.VertexBuffer_isDrawing)).booleanValue();
	}
	
	public static void renderPowerBar(double x, double y, double z, double width, double height, int power, int powerMax, int lightColor, int darkColor){
		if(power > 0){
			int renderAmount = (int) Math.max(Math.min(height, power * height / powerMax), 1);
		    int posY = (int) (y + height - renderAmount);
		    drawGradientRect(x, posY, z, x+width, posY+renderAmount, lightColor, darkColor);
	    }
	}
	
	public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor)
    {
		drawGradientRect(left, top, 0, right, bottom, startColor, endColor);
    }
	
	public static void drawGradientRect(double left, double top, double zLevel, double right, double bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        vertexbuffer.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
	
	
	
	public static TextureAtlasSprite getMissingSprite(){
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}
	
	public static TextureAtlasSprite getStillTexture(FluidStack fluid) {
		if (fluid == null || fluid.getFluid() == null) {
			return getMissingSprite();
    	}
    	return getStillTexture(fluid.getFluid());
	}
	
	public static TextureAtlasSprite getStillTexture(Fluid fluid) {
	    ResourceLocation iconKey = fluid.getStill();
	    if (iconKey == null) {
	      return getMissingSprite();
	    }
	    return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
	}

	public static TextureAtlasSprite getTexture(IBlockState state){
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
	    if (sprite == null) {
	      return getMissingSprite();
	    }
	    return sprite;
	}
	
	public static TextureAtlasSprite getSprite(String string){
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(string);
	    if (sprite == null) {
	      return getMissingSprite();
	    }
	    return sprite;
	}
	
	public static void renderGuiTank(FluidTank tank, double x, double y, double zLevel, double width, double height) {
		renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
	}
	//ENDERIO tank rendering
	public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height) {
	    if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
	      return;
	    }

	    TextureAtlasSprite icon = getStillTexture(fluid);
	    if (icon == null) {
	      return;
	    }

	    int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
	    int posY = (int) (y + height - renderAmount);

	    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	    int color = fluid.getFluid().getColor(fluid);
	    GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
	    
	    GlStateManager.enableBlend();    
	    for (int i = 0; i < width; i += 16) {
	      for (int j = 0; j < renderAmount; j += 16) {
	        int drawWidth = (int) Math.min(width - i, 16);
	        int drawHeight = Math.min(renderAmount - j, 16);

	        int drawX = (int) (x + i);
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
	    GlStateManager.disableBlend();
	}
	
	
	public static float getPartialTick()
    {
    	return Minecraft.getMinecraft().getRenderPartialTicks();
    }
	
	public static float PIXEL = 1.0F/16F;
	
	public static List<BakedQuad> getTankQuads(FluidStack fluid, int capacity, float xzOffset, float ySize){
		List<BakedQuad> list = Lists.newArrayList();
		if(fluid !=null){
			Color color = new Color(fluid.getFluid().getColor(fluid));
			int fluidColor = DynamicBaseModel.RGBAToInt(color.getBlue(), color.getGreen(), color.getRed(), color.getAlpha());
			TextureAtlasSprite fluidTexture = getStillTexture(fluid);
			//DEBUG Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.BOOKSHELF.getDefaultState());
			
			float fluidLevel = (ySize/(capacity))*fluid.amount;
			
			boolean renderUp = fluidLevel < ySize, renderN = true, renderS = true, renderW = true, renderE = true;
			boolean gas = fluid.getFluid().isGaseous(fluid);
			ModelRotation rot = gas ? ModelRotation.X180_Y0 : ModelRotation.X0_Y0;
			
			if(renderUp){
				float x1 = xzOffset;	float x2 = 16.0F-xzOffset;
				float z1 = xzOffset;	float z2 = 16.0F-xzOffset;
				float y = fluidLevel;
				final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.UP, 0, "", uv);
				
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x1, y, z1), new Vector3f(x2, y, z2), face, fluidTexture, EnumFacing.UP, rot, (BlockPartRotation)null, true, fluidColor));
			}
			if(renderN){
				float x1 = xzOffset; float x2 = 16.0F-xzOffset;
				float z1 = xzOffset; float z2 = xzOffset; 
				float minY = 1f; float y = fluidLevel;
				final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 16F-(y+1), 16.0f, 16.0f }, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
				
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x1, minY, z1), new Vector3f(x2, y, z2), face, fluidTexture, EnumFacing.NORTH, rot, (BlockPartRotation)null, false, fluidColor));
			}
			if(renderS){
				float x1 = xzOffset; float x2 = 16.0F-xzOffset;
				float z1 = 16.0F-xzOffset; float z2 = 16.0F-xzOffset; 
				float minY = 1f; float y = fluidLevel;
				final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 16F-(y+1), 16.0f, 16.0f }, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
				
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x1, minY, z1), new Vector3f(x2, y, z2), face, fluidTexture, EnumFacing.SOUTH, rot, (BlockPartRotation)null, false, fluidColor));
			}
			if(renderW){
				float x1 = xzOffset; float x2 = xzOffset;
				float z1 = xzOffset; float z2 = 16.0F-xzOffset; 
				float minY = 1f; float y = fluidLevel;
				final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 16F-(y+1), 16.0f, 16.0f }, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.WEST, 0, "", uv);
				
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x1, minY, z1), new Vector3f(x2, y, z2), face, fluidTexture, EnumFacing.WEST, rot, (BlockPartRotation)null, false, fluidColor));
			}
			if(renderE){
				float x1 = 16.0F-xzOffset; float x2 = 16.0F-xzOffset;
				float z1 = xzOffset; float z2 = 16.0F-xzOffset; 
				float minY = 1f; float y = fluidLevel;
				final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 16F-(y+1), 16.0f, 16.0f }, 0);
				final BlockPartFace face = new BlockPartFace(EnumFacing.EAST, 0, "", uv);
				
				list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(x1, minY, z1), new Vector3f(x2, y, z2), face, fluidTexture, EnumFacing.EAST, rot, (BlockPartRotation)null, false, fluidColor));
			}
		}
		return list;
	}

	public static void renderQuarteredTexture(int textureWidth,	int textureHeight, int texStartX, int texStartY, int texUsedWidth, int texUsedHeight, int renderStartX, int renderStartY, int renderWidth, int renderHeight) {
		// perspective percent x, y
		float perX = 1.f / ((float) textureWidth);
		float perY = 1.f / ((float) textureHeight);
		float texMinX = ((float) texStartX) * perX;
		float texMinY = ((float) texStartY) * perY;
		float texMaxX = (float) (texStartX + texUsedWidth) * perX;
		float texMaxY = (float) (texStartY + texUsedHeight) * perY;
		float halfWidth = (((float) renderWidth) / 2.f) * perX;
		float halfHeight = (((float) renderHeight) / 2.f) * perY;
		float halfRenderWidth = ((float) renderWidth) * 0.5f;
		float halfRenderHeight = ((float) renderHeight) * 0.5f;

		// draw top-left quadrant
		renderTexturedQuad(renderStartX, renderStartY, renderStartX	+ halfRenderWidth, renderStartY + halfRenderHeight, texMinX, texMinY, texMinX + halfWidth, texMinY + halfHeight);

		// draw top-right quadrant
		renderTexturedQuad(renderStartX + halfRenderWidth, renderStartY, renderStartX + halfRenderWidth * 2, renderStartY + halfRenderHeight, texMaxX - halfWidth, texMinY,	texMaxX, texMinY + halfHeight);

		// draw bottom-left quadrant
		renderTexturedQuad(renderStartX, renderStartY + halfRenderHeight, renderStartX + halfRenderWidth, renderStartY + halfRenderHeight * 2, texMinX, texMaxY - halfHeight, texMinX + halfWidth, texMaxY);

		// draw bottom-right quadrant
		renderTexturedQuad(renderStartX + halfRenderWidth, renderStartY + halfRenderHeight, renderStartX + halfRenderWidth * 2,	renderStartY + halfRenderHeight * 2, texMaxX - halfWidth, texMaxY - halfHeight, texMaxX, texMaxY);
	}

	public static void renderTexturedQuad(float x1, float y1, float x2,	float y2, float u1, float v1, float u2, float v2) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(u1, v1);
		GL11.glVertex2f(x1, y1);
		GL11.glTexCoord2f(u1, v2);
		GL11.glVertex2f(x1, y2);
		GL11.glTexCoord2f(u2, v2);
		GL11.glVertex2f(x2, y2);
		GL11.glTexCoord2f(u2, v1);
		GL11.glVertex2f(x2, y1);
		GL11.glEnd();
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB bb, float r,
			float g, float b, float width) {
		GlStateManager.depthMask(false);
		GlStateManager.glLineWidth(width);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        RenderGlobal.func_189697_a(bb, r, g, b, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
		/*GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glColor4f(r, g, b, 0.4F);
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);  
	    float hw = width/2;  
	    drawCuboid((float)bb.minX, (float)bb.minY-hw, (float)bb.minZ-hw, (float)bb.maxX, (float)bb.minY+hw, (float)bb.minZ+hw);
	    drawCuboid((float)bb.minX, (float)bb.maxY-hw, (float)bb.minZ-hw, (float)bb.maxX, (float)bb.maxY+hw, (float)bb.minZ+hw);  
	    drawCuboid((float)bb.minX, (float)bb.minY-hw, (float)bb.maxZ-hw, (float)bb.maxX, (float)bb.minY+hw, (float)bb.maxZ+hw);
	    drawCuboid((float)bb.minX, (float)bb.maxY-hw, (float)bb.maxZ-hw, (float)bb.maxX, (float)bb.maxY+hw, (float)bb.maxZ+hw);
	    
	    drawCuboid((float)bb.minX-hw, (float)bb.minY, (float)bb.minZ-hw, (float)bb.minX+hw, (float)bb.maxY, (float)bb.minZ+hw);
	    drawCuboid((float)bb.maxX-hw, (float)bb.minY, (float)bb.minZ-hw, (float)bb.maxX+hw, (float)bb.maxY, (float)bb.minZ+hw);
	    drawCuboid((float)bb.minX-hw, (float)bb.minY, (float)bb.maxZ-hw, (float)bb.minX+hw, (float)bb.maxY, (float)bb.maxZ+hw);
	    drawCuboid((float)bb.maxX-hw, (float)bb.minY, (float)bb.maxZ-hw, (float)bb.maxX+hw, (float)bb.maxY, (float)bb.maxZ+hw);
	    
	    drawCuboid((float)bb.minX-hw, (float)bb.minY-hw, (float)bb.minZ, (float)bb.minX+hw, (float)bb.minY+hw, (float)bb.maxZ);
	    drawCuboid((float)bb.minX-hw, (float)bb.maxY-hw, (float)bb.minZ, (float)bb.minX+hw, (float)bb.maxY+hw, (float)bb.maxZ);  
	    drawCuboid((float)bb.maxX-hw, (float)bb.minY-hw, (float)bb.minZ, (float)bb.maxX+hw, (float)bb.minY+hw, (float)bb.maxZ);
	    drawCuboid((float)bb.maxX-hw, (float)bb.maxY-hw, (float)bb.minZ, (float)bb.maxX+hw, (float)bb.maxY+hw, (float)bb.maxZ);
	    GL11.glDisable(GL11.GL_BLEND);*/
	}

	public static void drawCuboid(float x, float y, float z, float mx,
			float my, float mz) {
		GL11.glBegin(GL11.GL_QUADS);
		// z+ side
		GL11.glNormal3f(0, 0, 1);
		GL11.glVertex3f(x, my, mz);
		GL11.glVertex3f(x, y, mz);
		GL11.glVertex3f(mx, y, mz);
		GL11.glVertex3f(mx, my, mz);

		// x+ side
		GL11.glNormal3f(1, 0, 0);
		GL11.glVertex3f(mx, my, mz);
		GL11.glVertex3f(mx, y, mz);
		GL11.glVertex3f(mx, y, z);
		GL11.glVertex3f(mx, my, z);

		// y+ side
		GL11.glNormal3f(0, 1, 0);
		GL11.glVertex3f(x, my, z);
		GL11.glVertex3f(x, my, mz);
		GL11.glVertex3f(mx, my, mz);
		GL11.glVertex3f(mx, my, z);

		// z- side
		GL11.glNormal3f(0, 0, -1);
		GL11.glVertex3f(x, my, z);
		GL11.glVertex3f(mx, my, z);
		GL11.glVertex3f(mx, y, z);
		GL11.glVertex3f(x, y, z);

		// x-side
		GL11.glNormal3f(-1, 0, 0);
		GL11.glVertex3f(x, y, mz);
		GL11.glVertex3f(x, my, mz);
		GL11.glVertex3f(x, my, z);
		GL11.glVertex3f(x, y, z);

		// y- side
		GL11.glNormal3f(0, -1, 0);
		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(mx, y, z);
		GL11.glVertex3f(mx, y, mz);
		GL11.glVertex3f(x, y, mz);

		GL11.glEnd();
	}
	
	/**
	   * Renders a fluid block, call from TESR. x/y/z is the rendering offset.
	   *
	   * @param fluid Fluid to render
	   * @param pos   BlockPos where the Block is rendered. Used for brightness.
	   * @param x     Rendering offset. TESR x parameter.
	   * @param y     Rendering offset. TESR x parameter.
	   * @param z     Rendering offset. TESR x parameter.
	   * @param w     Width. 1 = full X-Width
	   * @param h     Height. 1 = full Y-Height
	   * @param d     Depth. 1 = full Z-Depth
	   */
	public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double w, double h, double d, boolean useFlowing) {
	    double wd = (1d - w) / 2d;
	    double hd = (1d - h) / 2d;
	    double dd = (1d - d) / 2d;

	    renderFluidCuboid(fluid, pos, x, y, z, wd, hd, dd, 1d - wd, 1d - hd, 1d - dd, useFlowing);
	}

	public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, boolean useFlowing) {
	    int color = fluid.getFluid().getColor(fluid);
	    renderFluidCuboid(fluid, pos, x, y, z, x1, y1, z1, x2, y2, z2, color, useFlowing);
	}

	/**
	 * Renders block with offset x/y/z from x1/y1/z1 to x2/y2/z2 inside the
	 * block local coordinates, so from 0-1
	 */
	public static void renderFluidCuboid(FluidStack fluid, BlockPos pos,
			double x, double y, double z, double x1, double y1, double z1,
			double x2, double y2, double z2, int color, boolean useFlowing) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().renderEngine
				.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		// RenderUtil.setColorRGBA(color);
		int brightness = Minecraft.getMinecraft().theWorld.getCombinedLight(
				pos, fluid.getFluid().getLuminosity());

		pre(x, y, z);

		TextureAtlasSprite still = Minecraft.getMinecraft()
				.getTextureMapBlocks()
				.getTextureExtry(fluid.getFluid().getStill(fluid).toString());
		TextureAtlasSprite flowing = null;
		
		if(useFlowing)flowing = Minecraft.getMinecraft()
				.getTextureMapBlocks()
				.getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());
		else flowing = still;

		// x/y/z2 - x/y/z1 is because we need the width/height/depth
		putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1,
				EnumFacing.DOWN, color, brightness, false);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.NORTH, color, brightness, true);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.EAST, color, brightness, true);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.SOUTH, color, brightness, true);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.WEST, color, brightness, true);
		putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1,
				EnumFacing.UP, color, brightness, false);

		tessellator.draw();

		post();
	}

	public static void pre(double x, double y, double z) {
		GlStateManager.pushMatrix();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableBlend();
		GlStateManager
				.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(GL11.GL_SMOOTH);
		} else {
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		GlStateManager.translate(x, y, z);
	}

	public static void post() {
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	public static void putTexturedQuad(VertexBuffer renderer,
			TextureAtlasSprite sprite, double x, double y, double z, double w,
			double h, double d, EnumFacing face, int color, int brightness,
			boolean flowing) {
		int l1 = brightness >> 0x10 & 0xFFFF;
		int l2 = brightness & 0xFFFF;

		int a = color >> 24 & 0xFF;
		int r = color >> 16 & 0xFF;
		int g = color >> 8 & 0xFF;
		int b = color & 0xFF;

		putTexturedQuad(renderer, sprite, x, y, z, w, h, d, face, r, g, b, a,
				l1, l2, flowing);
	}

	// x and x+w has to be within [0,1], same for y/h and z/d
	public static void putTexturedQuad(VertexBuffer renderer,
			TextureAtlasSprite sprite, double x, double y, double z, double w,
			double h, double d, EnumFacing face, int r, int g, int b, int a,
			int light1, int light2, boolean flowing) {
		// safety
		if (sprite == null) {
			return;
		}
		double minU;
		double maxU;
		double minV;
		double maxV;

		double size = 16f;
		if (flowing) {
			size = 8f;
		}

		double x1 = x;
		double x2 = x + w;
		double y1 = y;
		double y2 = y + h;
		double z1 = z;
		double z2 = z + d;

		double xt1 = x1 % 1d;
		double xt2 = xt1 + w;
		while (xt2 > 1f)
			xt2 -= 1f;
		double yt1 = y1 % 1d;
		double yt2 = yt1 + h;
		while (yt2 > 1f)
			yt2 -= 1f;
		double zt1 = z1 % 1d;
		double zt2 = zt1 + d;
		while (zt2 > 1f)
			zt2 -= 1f;

		// flowing stuff should start from the bottom, not from the start
		if (flowing) {
			double tmp = 1d - yt1;
			yt1 = 1d - yt2;
			yt2 = tmp;
		}

		switch (face) {
		case DOWN:
		case UP:
			minU = sprite.getInterpolatedU(xt1 * size);
			maxU = sprite.getInterpolatedU(xt2 * size);
			minV = sprite.getInterpolatedV(zt1 * size);
			maxV = sprite.getInterpolatedV(zt2 * size);
			break;
		case NORTH:
		case SOUTH:
			minU = sprite.getInterpolatedU(xt2 * size);
			maxU = sprite.getInterpolatedU(xt1 * size);
			minV = sprite.getInterpolatedV(yt1 * size);
			maxV = sprite.getInterpolatedV(yt2 * size);
			break;
		case WEST:
		case EAST:
			minU = sprite.getInterpolatedU(zt2 * size);
			maxU = sprite.getInterpolatedU(zt1 * size);
			minV = sprite.getInterpolatedV(yt1 * size);
			maxV = sprite.getInterpolatedV(yt2 * size);
			break;
		default:
			minU = sprite.getMinU();
			maxU = sprite.getMaxU();
			minV = sprite.getMinV();
			maxV = sprite.getMaxV();
		}

		switch (face) {
		case DOWN:
			renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV)
					.lightmap(light1, light2).endVertex();
			break;
		case UP:
			renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV)
					.lightmap(light1, light2).endVertex();
			break;
		case NORTH:
			renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, maxV)
					.lightmap(light1, light2).endVertex();
			break;
		case SOUTH:
			renderer.pos(x1, y1, z2).color(r, g, b, a).tex(maxU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y1, z2).color(r, g, b, a).tex(minU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y2, z2).color(r, g, b, a).tex(minU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x1, y2, z2).color(r, g, b, a).tex(maxU, minV)
					.lightmap(light1, light2).endVertex();
			break;
		case WEST:
			renderer.pos(x1, y1, z1).color(r, g, b, a).tex(maxU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x1, y2, z1).color(r, g, b, a).tex(maxU, minV)
					.lightmap(light1, light2).endVertex();
			break;
		case EAST:
			renderer.pos(x2, y1, z1).color(r, g, b, a).tex(minU, maxV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y2, z1).color(r, g, b, a).tex(minU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, minV)
					.lightmap(light1, light2).endVertex();
			renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV)
					.lightmap(light1, light2).endVertex();
			break;
		}
	}

	public static void renderItem(ItemStack itemStack, TransformType type) {
		if(ItemStackTools.isNullStack(itemStack))return;
		GlStateManager.pushMatrix();
		Minecraft mc = Minecraft.getMinecraft();
		

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		//GlStateManager.disableLighting();
		//RenderHelper.enableStandardItemLighting();
		mc.getRenderItem().renderItem(itemStack, type);	
		//RenderHelper.disableStandardItemLighting();
		//GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

}
