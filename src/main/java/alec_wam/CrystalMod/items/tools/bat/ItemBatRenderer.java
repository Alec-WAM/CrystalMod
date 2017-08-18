package alec_wam.CrystalMod.items.tools.bat;

import java.util.Map;

import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemBatRenderer implements ICustomItemRenderer {

	@Override
	public void render(ItemStack stack) {
		GlStateManager.pushMatrix();
		TransformType type = lastTransform;
		double x = -0.5;
		double y = -1;
		double z = -0.5;
		
		if(type == TransformType.GUI){
			//RenderHelper.disableStandardItemLighting();
			GlStateManager.scale(0.7, 0.7, 0.7);
			y = -1.3;
		}
		
		if(type == TransformType.FIXED){
			x = -0.5;
			y = -1.3;
			z = -0.5;
		}
		
		if(type == TransformType.FIRST_PERSON_LEFT_HAND){
			z += 0.3;
		}
		
		if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
			GlStateManager.translate(4.1, -1.8, -5.1);
			GlStateManager.scale(1.5, 1.5, 1.5);
			GlStateManager.rotate(75, 1, 0, 1);
			x = 0;
			y = 3.65;
			z = 0;
		}
		if(type == TransformType.THIRD_PERSON_LEFT_HAND){
			GlStateManager.translate(-0.65, 0.8, -0.3);
			GlStateManager.scale(1.5, 1.5, 1.5);
			GlStateManager.rotate(70, 1, 0, -1);
			x = 0;
			y = -0.5;
			z = 0;
		}
		
		if(type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.FIRST_PERSON_RIGHT_HAND){
			y = -0.5;
		}
		//GlStateManager.disableLighting();
		//GlStateManager.pushAttrib();
        //RenderHelper.enableStandardItemLighting();
		renderWholeBat(stack, x, y, z);
		GlStateManager.enableBlend();
        //GlStateManager.popAttrib();
        //GlStateManager.enableLighting();
		if(type == TransformType.GUI){
			//RenderHelper.enableGUIStandardItemLighting();
		}
		GlStateManager.popMatrix();
	}

	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
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
        float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        renderBat(item, false);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = Minecraft.getSystemTime() % 4873L / 4873.0F / 8.0F;
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
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, min, minY, min, handle.getMinU(), handle.getMinV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, min, handle.getMinU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, min, handle.getMaxU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, min, handle.getMaxU(), handle.getMinV());
			tess.draw();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, max, minY, max, handle.getMinU(), handle.getMinV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, max, handle.getMinU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, max, handle.getMaxU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, max, handle.getMaxU(), handle.getMinV());
			tess.draw();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, max, minY, min, handle.getMinU(), handle.getMinV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, min, handle.getMinU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, max, handle.getMaxU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, max, handle.getMaxU(), handle.getMinV());
			tess.draw();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, min, minY, max, handle.getMinU(), handle.getMinV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, max, handle.getMinU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, min, handle.getMaxU(), handle.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, min, handle.getMaxU(), handle.getMinV());
			tess.draw();
		}
		if(drawHandleBase){
			double min = 0.35;
			double max = 0.65;
			double minY = 0.2;
			double maxY = 0.35;
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, min, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, min, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, min, icon.getMaxU(), icon.getMinV());
			
			RenderUtil.addVertexWithUV(buffer, max, minY, max, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, max, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, max, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, min, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, max, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMinV());
			
			RenderUtil.addVertexWithUV(buffer, min, minY, max, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, max, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, min, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, min, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, min, maxY, max, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, max, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, maxY, min, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, maxY, min, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
		}
		
		if(drawHead){
			double min = 0.39;
			double max = 0.61;
			double minTop = 0.35;
			double maxTop = 0.65;
			double minY = 0.85;
			double maxY = 2.5;
			
			
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, minTop, maxY, minTop, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, maxTop, maxY, minTop, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, min, icon.getMaxU(), icon.getMinV());
			
			RenderUtil.addVertexWithUV(buffer, max, minY, max, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, maxTop, maxY, maxTop, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, minTop, maxY, maxTop, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
			/*final int MAX_LIGHT_X = 0xF000F0;
	        final int MAX_LIGHT_Y = 0xF000F0;
	        
	        float red = 255/255F, green = 255/255F, blue = 255/255F;
			buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			buffer.pos(min, minY, min).tex(icon.getMinU(), icon.getMinV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(minTop, maxY, minTop).tex(icon.getMinU(), icon.getMaxV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(maxTop, maxY, minTop).tex(icon.getMaxU(), icon.getMaxV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(max, minY, min).tex(icon.getMaxU(), icon.getMinV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			
			buffer.pos(max, minY, max).tex(icon.getMinU(), icon.getMinV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(maxTop, maxY, maxTop).tex(icon.getMinU(), icon.getMaxV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(minTop, maxY, maxTop).tex(icon.getMaxU(), icon.getMaxV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(min, minY, max).tex(icon.getMaxU(), icon.getMinV()).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, 1.0F).endVertex();
			tess.draw();*/
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, maxTop, maxY, minTop, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, maxTop, maxY, maxTop, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMinV());
			
			RenderUtil.addVertexWithUV(buffer, min, minY, max, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, minTop, maxY, maxTop, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, minTop, maxY, minTop, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, min, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, min, minY, min, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, max, minY, min, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, max, minY, max, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, min, minY, max, icon.getMaxU(), icon.getMinV());
			tess.draw();
			
			icon.getMinU();
			icon.getMaxU();
			icon.getMinV();
			icon.getMaxU();
			
			RenderUtil.startDrawing(buffer);
			RenderUtil.addVertexWithUV(buffer, minTop, maxY, maxTop, icon.getMinU(), icon.getMinV());
			RenderUtil.addVertexWithUV(buffer, maxTop, maxY, maxTop, icon.getMinU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, maxTop, maxY, minTop, icon.getMaxU(), icon.getMaxV());
			RenderUtil.addVertexWithUV(buffer, minTop, maxY, minTop, icon.getMaxU(), icon.getMinV());
			tess.draw();
		}
		//GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

}
