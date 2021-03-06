package alec_wam.CrystalMod.util.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.util.Vector3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
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
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.client.config.GuiUtils;

public class RenderUtil {

	public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	public static final ResourceLocation WIDGETS = new ResourceLocation("crystalmod:textures/gui/widgets.png");
	
	public static void startDrawing(Tessellator tess){
		startDrawing(tess.getBuffer());
	}
	
	public static void startDrawing(VertexBuffer buffer){
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	}
	
	public static void startDrawing(VertexBuffer buffer, VertexFormat format){
		buffer.begin(7, format);
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
        float f = (startColor >> 24 & 255) / 255.0F;
        float f1 = (startColor >> 16 & 255) / 255.0F;
        float f2 = (startColor >> 8 & 255) / 255.0F;
        float f3 = (startColor & 255) / 255.0F;
        float f4 = (endColor >> 24 & 255) / 255.0F;
        float f5 = (endColor >> 16 & 255) / 255.0F;
        float f6 = (endColor >> 8 & 255) / 255.0F;
        float f7 = (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        vertexbuffer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
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
	
	public static TextureAtlasSprite getSprite(ResourceLocation res){
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(res.toString());
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
	
	public static void renderGuiTank(FluidTank tank, double x, double y, double zLevel, double width, double height, boolean renderBars) {
		renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height, renderBars);
	}
	//ENDERIO tank rendering
	public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height, boolean renderBars) {
	    
	    if (fluid != null && fluid.getFluid() != null && amount > 0) {
		    TextureAtlasSprite icon = getStillTexture(fluid);
		    if (icon != null) {	
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
		}
	    if(renderBars){
		    int barSize = capacity / Fluid.BUCKET_VOLUME;
		    int barIndent = 2;
		    if(barSize > 1){
			    for(int i = 1; i < barSize; i++){
			    	int lineX = (int)x;
			    	int lineY = (int)((y+height) - (i * (height/barSize)));
			    	double left = lineX + barIndent;
			    	double top = lineY;
			    	double right = (int) (lineX + width) - barIndent;
			    	double bottom = lineY + 1;
			    	int color = Color.BLACK.getRGB();
			    	float f3 = (float)(color >> 24 & 255) / 255.0F;
			        float f = (float)(color >> 16 & 255) / 255.0F;
			        float f1 = (float)(color >> 8 & 255) / 255.0F;
			        float f2 = (float)(color & 255) / 255.0F;
			        Tessellator tessellator = Tessellator.getInstance();
			        VertexBuffer vertexbuffer = tessellator.getBuffer();
			        GlStateManager.enableBlend();
			        GlStateManager.disableTexture2D();
			        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			        GlStateManager.color(f, f1, f2, f3);
			        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
			        vertexbuffer.pos((double)left, (double)bottom, 0.0D).endVertex();
			        vertexbuffer.pos((double)right, (double)bottom, 0.0D).endVertex();
			        vertexbuffer.pos((double)right, (double)top, 0.0D).endVertex();
			        vertexbuffer.pos((double)left, (double)top, 0.0D).endVertex();
			        tessellator.draw();
			        GlStateManager.enableTexture2D();
			        GlStateManager.disableBlend();
			    	
			    }		
		    }
	    }
	}
	
	public static void renderGuiTank(Fluid fluid, int amount, int capacity, double x, double y, double zLevel, double width, double height) {
	    if (fluid == null) {
	      return;
	    }

	    TextureAtlasSprite icon = getStillTexture(fluid);
	    if (icon == null) {
	      return;
	    }

	    int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
	    int posY = (int) (y + height - renderAmount);

	    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	    int color = fluid.getColor();
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
		float perX = 1.f / (textureWidth);
		float perY = 1.f / (textureHeight);
		float texMinX = (texStartX) * perX;
		float texMinY = (texStartY) * perY;
		float texMaxX = (texStartX + texUsedWidth) * perX;
		float texMaxY = (texStartY + texUsedHeight) * perY;
		float halfWidth = ((renderWidth) / 2.f) * perX;
		float halfHeight = ((renderHeight) / 2.f) * perY;
		float halfRenderWidth = (renderWidth) * 0.5f;
		float halfRenderHeight = (renderHeight) * 0.5f;

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
        RenderGlobal.drawSelectionBoundingBox(bb, r, g, b, 1.0f);
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
	public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1,	double x2, double y2, double z2, int color, boolean useFlowing) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int brightness = pos == null ? 15 : CrystalMod.proxy.getClientWorld().getCombinedLight(
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
	
	public static void renderFluidCuboid(Fluid fluid, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, boolean useFlowing, int brightness) {
		int color = fluid.getColor();
		renderFluidCuboid(fluid, x, y, z, x1, y1, z1, x2, y2, z2, color, useFlowing, brightness);
	}
	
	public static void renderFluidCuboid(Fluid fluid, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean useFlowing, int brightness) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		pre(x, y, z);

		TextureAtlasSprite still = Minecraft.getMinecraft()
				.getTextureMapBlocks()
				.getTextureExtry(fluid.getStill().toString());
		TextureAtlasSprite flowing = null;

		if(useFlowing)flowing = Minecraft.getMinecraft()
				.getTextureMapBlocks()
				.getTextureExtry(fluid.getFlowing().toString());
		else flowing = still;

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

	public static void renderCuboid(TextureAtlasSprite sprite, BlockPos pos, double x, double y, double z, double x1, double y1, double z1,	double x2, double y2, double z2, int color) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int brightness = pos == null ? 15 : CrystalMod.proxy.getClientWorld().getCombinedLight(
				pos, 15);

		pre(x, y, z);

		// x/y/z2 - x/y/z1 is because we need the width/height/depth
		putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1,
				EnumFacing.DOWN, color, brightness, false);
		putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.NORTH, color, brightness, true);
		putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.EAST, color, brightness, true);
		putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.SOUTH, color, brightness, true);
		putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, EnumFacing.WEST, color, brightness, true);
		putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1,
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
		GlStateManager.disableLighting();
		RenderHelper.enableStandardItemLighting();
		mc.getRenderItem().renderItem(itemStack, type);	
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
	
	/**
	 * Renders floating lines of text in the 3D world at a specific position.
	 * @param list The string list of text to render
	 * @param x X coordinate in the game world
	 * @param y Y coordinate in the game world
	 * @param z Z coordinate in the game world
	 * @param color
	 * @param renderBlackBox render a pretty black border behind the text?
	 * @param partialTickTime
	 */
    public static void renderFloatingText(List<String> list, double x, double y, double z, int color, boolean renderBlackBox, float partialTickTime, boolean ignorePitch)
    {
    	Minecraft mc = Minecraft.getMinecraft();
    	
    	RenderManager renderManager = mc.getRenderManager();
        FontRenderer fontRenderer = mc.fontRendererObj;
        EntityPlayer clientPlayer = CrystalMod.proxy.getClientPlayer();
        float playerX = (float) (clientPlayer.lastTickPosX + (clientPlayer.posX - clientPlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (clientPlayer.lastTickPosY + (clientPlayer.posY - clientPlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (clientPlayer.lastTickPosZ + (clientPlayer.posZ - clientPlayer.lastTickPosZ) * partialTickTime);

        double dx = x-playerX;
        double dy = y-playerY;
        double dz = z-playerZ;
        float distance = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float multiplier = distance / 120f;
        float scale = 0.45f * multiplier;
        
        GlStateManager.color(1f, 1f, 1f, 0.5f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        if(!ignorePitch)GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        
        int textWidth = 0;
        for (String thisMessage : list)
        {
            int thisMessageWidth = fontRenderer.getStringWidth(thisMessage);

            if (thisMessageWidth > textWidth)
            	textWidth = thisMessageWidth;
        }
        
        int lineHeight = 10;
        
        if(renderBlackBox)
        {
        	Tessellator tessellator = Tessellator.getInstance();
        	VertexBuffer buffer = tessellator.getBuffer();
        	GlStateManager.disableTexture2D();
        	buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            int stringMiddle = textWidth / 2;
            buffer.pos(-stringMiddle - 1, -1 + 0, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
            buffer.pos(-stringMiddle - 1, 8 + lineHeight*list.size()-lineHeight, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
            buffer.pos(stringMiddle + 1, 8 + lineHeight*list.size()-lineHeight, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
            buffer.pos(stringMiddle + 1, -1 + 0, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }
        
        int i = 0;
        for(String message : list)
        {
            fontRenderer.drawString(message, -textWidth / 2, i*lineHeight, color);
        	i++;
        }
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
    {
    	if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines)
            {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth)
                {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth)
            {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2)
                    {
                        tooltipTextWidth = mouseX - 12 - 8;
                    }
                    else
                    {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
            {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap)
            {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++)
                {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0)
                    {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine)
                    {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth)
                        {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                }
                else
                {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1)
            {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY + tooltipHeight + 6 > screenHeight)
            {
                tooltipY = screenHeight - tooltipHeight - 6;
            }

            final int zLevel = 300;
            final int backgroundColor = 0xF0100010;
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            final int borderColorStart = 0x505000FF;
            final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            int tooltipTop = tooltipY;
            
            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
            {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float)tooltipX, (float)tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount)
                {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
    
}
