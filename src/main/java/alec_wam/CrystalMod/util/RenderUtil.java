package alec_wam.CrystalMod.util;

import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.util.math.Vector3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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
		
		for(EnumFacing enumfacing : EnumFacing.values()) {
			random.setSeed(42L);
			renderQuads(bufferbuilder, model.getQuads((IBlockState)null, enumfacing, random), color, stack);
		}

		random.setSeed(42L);
		renderQuads(bufferbuilder, model.getQuads((IBlockState)null, (EnumFacing)null, random), color, stack);
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
		return MissingTextureSprite.getSprite();
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

	public static TextureAtlasSprite getTexture(IBlockState state){
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
}
