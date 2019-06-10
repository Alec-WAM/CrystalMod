package alec_wam.CrystalMod.util;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.util.math.Vector3d;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("deprecation")
public class RenderUtil {

	public static void renderItem(ItemStack itemStack, TransformType type) {
		if(ItemStackTools.isNullStack(itemStack))return;
		GlStateManager.pushMatrix();
		Minecraft mc = Minecraft.getInstance();
		

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		RenderHelper.enableStandardItemLighting();
		mc.getItemRenderer().renderItem(itemStack, type);	
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
	
	public static void renderModel(IBakedModel model, ItemStack stack) {
		renderModel(model, -1, stack);
	}
	
	public static void renderModel(IBakedModel model, int color, ItemStack stack) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
		Random random = new Random();
		
		for(Direction enumfacing : Direction.values()) {
			random.setSeed(42L);
			renderQuads(bufferbuilder, model.getQuads((BlockState)null, enumfacing, random), color, stack);
		}

		random.setSeed(42L);
		renderQuads(bufferbuilder, model.getQuads((BlockState)null, (Direction)null, random), color, stack);
		tessellator.draw();
	}
	
	public static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
		//boolean flag = color == -1 && !stack.isEmpty();
		int i = 0;

		for(int j = quads.size(); i < j; ++i) {
			BakedQuad bakedquad = quads.get(i);
			int k = color;
			/*if (flag && bakedquad.hasTintIndex()) {
				k = this.itemColors.getColor(stack, bakedquad.getTintIndex());
				k = k | -16777216;
			}*/

			net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
		}

	}
	
	public static boolean isGui3D(ItemStack stack){
		IBakedModel model = Minecraft.getInstance().getItemRenderer().getModelWithOverrides(stack);
		return model !=null && model.isGui3d();
	}

	public static void startDrawing(Tessellator tess){
		startDrawing(tess.getBuffer());
	}
	
	public static void startDrawing(BufferBuilder buffer){
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	}
	
	public static void startDrawing(BufferBuilder buffer, VertexFormat format){
		buffer.begin(7, format);
	}
	
	public static void addVertexWithUV(BufferBuilder buffer, Vector3d vec, double u, double v){
		addVertexWithUV(buffer, vec.x, vec.y, vec.z, u, v);
	}
	
	public static void addVertexWithUV(BufferBuilder buffer, double x, double y, double z, double u, double v){
		buffer.pos(x, y, z).tex(u, v).endVertex();
	}

	public static TextureAtlasSprite getMissingSprite(){
		return MissingTextureSprite.func_217790_a();
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
	    return Minecraft.getInstance().getTextureMap().getSprite(iconKey);
	}

	public static TextureAtlasSprite getTexture(BlockState state){
		TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
	    if (sprite == null) {
	      return getMissingSprite();
	    }
	    return sprite;
	}
	
	public static TextureAtlasSprite getSprite(ResourceLocation res){
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getSprite(res);
	    if (sprite == null) {
	      return getMissingSprite();
	    }
	    return sprite;
	}
	
	public static TextureAtlasSprite getSprite(String string){
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getAtlasSprite(string);
	    if (sprite == null) {
	      return getMissingSprite();
	    }
	    return sprite;
	}
	

	public static void pre(double x, double y, double z) {
		GlStateManager.pushMatrix();

		//RenderHelper.disableStandardItemLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(GL11.GL_SMOOTH);
		} else {
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		GlStateManager.translated(x, y, z);
	}
	
	public static void renderFluidCuboid(Fluid fluid, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, boolean useFlowing, int brightness) {
		int color = fluid.getColor();
		renderFluidCuboid(fluid, x, y, z, x1, y1, z1, x2, y2, z2, color, useFlowing, brightness);
	}
	
	public static void renderFluidCuboid(Fluid fluid, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean useFlowing, int brightness) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		pre(x, y, z);

		TextureAtlasSprite still = getSprite(fluid.getStill());
		TextureAtlasSprite flowing = null;

		if(useFlowing)flowing = getSprite(fluid.getFlowing());
		else flowing = still;

		putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1,
				Direction.DOWN, color, brightness, false);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, Direction.NORTH, color, brightness, true);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, Direction.EAST, color, brightness, true);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, Direction.SOUTH, color, brightness, true);
		putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2
				- z1, Direction.WEST, color, brightness, true);
		putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1,
				Direction.UP, color, brightness, false);

		tessellator.draw();

		post();
	}

	public static void post() {
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		//RenderHelper.enableStandardItemLighting();
	}
	
	public static void putTexturedQuad(BufferBuilder renderer,
			TextureAtlasSprite sprite, double x, double y, double z, double w,
			double h, double d, Direction face, int color, int brightness,
			boolean flowing) {
		int l1 = brightness >> 0x10 & 0xFFFF;
		int l2 = brightness & 0xFFFF;

		/*int a = color >> 24 & 0xFF;
		int r = color >> 16 & 0xFF;
		int g = color >> 8 & 0xFF;
		int b = color & 0xFF;*/
		
		int r = 0;
		int g = 0;
		int b = 0;
		int j1 = color;
		r += (j1 & 16711680) >> 16;
		g += (j1 & '\uff00') >> 8;
		b += j1 & 255;
		int a = 255;
		
		putTexturedQuad(renderer, sprite, x, y, z, w, h, d, face, r, g, b, a,
				l1, l2, flowing);
	}

	// x and x+w has to be within [0,1], same for y/h and z/d
	public static void putTexturedQuad(BufferBuilder renderer,
			TextureAtlasSprite sprite, double x, double y, double z, double w,
			double h, double d, Direction face, int r, int g, int b, int a,
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
}
