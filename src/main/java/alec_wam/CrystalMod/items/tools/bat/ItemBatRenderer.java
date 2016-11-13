package alec_wam.CrystalMod.items.tools.bat;

import java.util.Map;

import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemBatRenderer implements ICustomItemRenderer {

	public void render(ItemStack stack, TransformType type) {
		GlStateManager.pushMatrix();
		boolean render = false;
		
		double x = 0;
		double y = 0;
		double z = 0;
		
		if(type == TransformType.GUI){
			GlStateManager.scale(0.7, 0.7, 0.7);
			//GlStateManager.translate(0.3, -0.7, 0);
		}
		renderWholeBat(stack, x, y, z);
		
		if(render){
		
			switch (type) {
				case GROUND:
					GlStateManager.scale(0.5f, 0.5F, 0.5F);
					GlStateManager.translate(-0.5f, 0.0f, -0.5F);
					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.rotate(-135, 0, 1, 0);
					GlStateManager.translate(-0.5, -0.5, -0.5);
					renderWholeBat(stack, 0F, 0f, 0F);
					break;
				case THIRD_PERSON_RIGHT_HAND:
					GlStateManager.translate(-0.0f, 0.0F, 0.25F);
					renderWholeBat(stack, 0.0F, 0F, 0.0f);
					break;
				case THIRD_PERSON_LEFT_HAND:
					GlStateManager.translate(-0.0f, 0.0F, 0.25F);
					renderWholeBat(stack, 0.0F, 0F, 0.0f);
					break;
				case FIRST_PERSON_RIGHT_HAND:
					renderWholeBat(stack, 0F, 0F, 0f);
					break;
				case FIRST_PERSON_LEFT_HAND:
					renderWholeBat(stack, 0F, 0F, 0f);
					break;
				case GUI:
					/*GlStateManager.scale(0.7, 0.7, 0.7);
					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.rotate(-135, 0, 1, 0);
					GlStateManager.translate(-0.5, -0.5, -0.5);*/
					renderWholeBat(stack, 0f, -1.3f, 0.6f);
					break;
				default:
			}
		}
		GlStateManager.popMatrix();
	}
	
	public static void renderWholeBat(ItemStack bat, double x, double y, double z){
		GlStateManager.translate(x, y, z);
		
		boolean renderBat = true;
		
		if(renderBat)renderBat(bat, true);
		
		boolean renderEffect = false;
		if(renderEffect){
			renderItemEnchanted(bat);
		}
		
		Map<IBatUpgrade, UpgradeData> upgrades = BatHelper.getBatUpgrades(bat);
		for(IBatUpgrade upgrade : upgrades.keySet()){
			upgrade.render(bat, upgrades.get(upgrade));
		}
	}
	
	private static void renderItemEnchanted(ItemStack item){
		GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderUtil.RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        renderBat(item, false);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        renderBat(item, false);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}
	
	public static void renderBat(ItemStack item, boolean bind){
		GlStateManager.pushMatrix();
		//GlStateManager.disableLighting();
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer buffer = tess.getBuffer();
		IBatType type = BatHelper.getBat(item);
		
		TextureAtlasSprite icon = type.getBatTexture();
		if(icon == null)icon = RenderUtil.getMissingSprite();
		
		boolean drawHandle = true;
		boolean drawHandleBase = true;
		boolean drawHead = true;
		if(bind)Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if(drawHandle){
			double min = 0.4;
			double max = 0.6;
			double minY = 0.35;
			double maxY = 0.85;
			
			TextureAtlasSprite handle = type.getHandleTexture();
			if(handle == null)handle = RenderUtil.getMissingSprite();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, min, minY, min, handle.getMinU(), handle.getMinV());
			addVertexWithUV(buffer, min, maxY, min, handle.getMinU(), handle.getMaxV());
			addVertexWithUV(buffer, max, maxY, min, handle.getMaxU(), handle.getMaxV());
			addVertexWithUV(buffer, max, minY, min, handle.getMaxU(), handle.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, max, minY, max, handle.getMinU(), handle.getMinV());
			addVertexWithUV(buffer, max, maxY, max, handle.getMinU(), handle.getMaxV());
			addVertexWithUV(buffer, min, maxY, max, handle.getMaxU(), handle.getMaxV());
			addVertexWithUV(buffer, min, minY, max, handle.getMaxU(), handle.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, max, minY, min, handle.getMinU(), handle.getMinV());
			addVertexWithUV(buffer, max, maxY, min, handle.getMinU(), handle.getMaxV());
			addVertexWithUV(buffer, max, maxY, max, handle.getMaxU(), handle.getMaxV());
			addVertexWithUV(buffer, max, minY, max, handle.getMaxU(), handle.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, min, minY, max, handle.getMinU(), handle.getMinV());
			addVertexWithUV(buffer, min, maxY, max, handle.getMinU(), handle.getMaxV());
			addVertexWithUV(buffer, min, maxY, min, handle.getMaxU(), handle.getMaxV());
			addVertexWithUV(buffer, min, minY, min, handle.getMaxU(), handle.getMinV());
			tess.draw();
		}
		if(drawHandleBase){
			double min = 0.35;
			double max = 0.65;
			double minY = 0.2;
			double maxY = 0.35;
			
			startDrawing(buffer);
			addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, min, maxY, min, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, max, maxY, min, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, max, minY, min, icon.getMaxU(), icon.getMinV());
			
			addVertexWithUV(buffer, max, minY, max, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, max, maxY, max, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, min, maxY, max, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, max, maxY, min, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, max, maxY, max, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMinV());
			
			addVertexWithUV(buffer, min, minY, max, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, min, maxY, max, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, min, maxY, min, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, min, minY, min, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, min, maxY, max, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, max, maxY, max, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, max, maxY, min, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, min, maxY, min, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
		}
		
		if(drawHead){
			double min = 0.39;
			double max = 0.61;
			double minTop = 0.35;
			double maxTop = 0.65;
			double minY = 0.85;
			double maxY = 2.5;
			
			
			
			double minU = 0;//icon.getMinU();
			double maxU = 1;//icon.getMaxU();
			double minV = 0;//icon.getMinV();
			double maxV = 1;//icon.getMaxU();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, minTop, maxY, minTop, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, maxTop, maxY, minTop, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, max, minY, min, icon.getMaxU(), icon.getMinV());
			
			addVertexWithUV(buffer, max, minY, max, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, maxTop, maxY, maxTop, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, minTop, maxY, maxTop, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, maxTop, maxY, minTop, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, maxTop, maxY, maxTop, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMinV());
			
			addVertexWithUV(buffer, min, minY, max, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, minTop, maxY, maxTop, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, minTop, maxY, minTop, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, min, minY, min, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			minU = icon.getMinU();
			maxU = icon.getMaxU();
			minV = icon.getMinV();
			maxV = icon.getMaxU();
			
			startDrawing(buffer);
			addVertexWithUV(buffer, minTop, maxY, maxTop, icon.getMinU(), icon.getMinV());
			addVertexWithUV(buffer, maxTop, maxY, maxTop, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(buffer, maxTop, maxY, minTop, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(buffer, minTop, maxY, minTop, icon.getMaxU(), icon.getMinV());
			tess.draw();
		}
		//GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
	
	public static void startDrawing(VertexBuffer buffer){
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	}
	
	public static void addVertexWithUV(VertexBuffer buffer, double x, double y, double z, double u, double v){
		buffer.pos(x, y, z).tex(u, v).endVertex();
	}

}
