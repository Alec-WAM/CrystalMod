package alec_wam.CrystalMod.items.tools.blowdart;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemRenderBlowGun implements ICustomItemRenderer {

	public static final ResourceLocation SIDE_TEXTURE = CrystalMod.resourceL("textures/model/item/blowdart.png");
	public static final ResourceLocation END_TEXTURE = CrystalMod.resourceL("textures/model/item/blowdart_end.png");
	
	@Override
	public void render(ItemStack stack) {
		GlStateManager.pushMatrix();
		
		if(lastTransform == TransformType.GROUND || lastTransform == TransformType.HEAD)
		{
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.translate(-0.5, 0, -0.5);
		}
		else if(lastTransform == TransformType.GUI)
		{
			GlStateManager.translate(-0.5, -0.8, -0.5);
			GlStateManager.rotate(15, 1, 0, 0);
			GlStateManager.rotate(45, 0, 0, -1);
			GlStateManager.scale(0.2, 0.2, 0.2);
			/*GlStateManager.scale(0.18, 0.18, 0.18);
			GlStateManager.translate(-3, -3, -0.5);
			GlStateManager.rotate(45, 1, 1, 0);*/
		}
		else if(lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND)
		{
			GlStateManager.rotate(90, 0, 0, 1);
			GlStateManager.rotate(45, 1, 0, 0);
			GlStateManager.translate(-1, -0.3, -0.5);
		}
		else if(lastTransform == TransformType.FIRST_PERSON_LEFT_HAND)
		{
			GlStateManager.rotate(90, 0, 0, 1);
			GlStateManager.rotate(-45, 1, 0, 0);
			GlStateManager.translate(-1, -0.3, 0.5);
		}
		else if(lastTransform == TransformType.THIRD_PERSON_RIGHT_HAND)
		{
			GlStateManager.scale(0.2, 0.2, 0.2);
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.rotate(45, 0, 0, 1);
			GlStateManager.translate(0, -5, 0);
		}
		else if(lastTransform == TransformType.THIRD_PERSON_LEFT_HAND)
		{
			GlStateManager.scale(0.2, 0.2, 0.2);
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.rotate(-45, 0, 0, 1);
			GlStateManager.translate(0, -5, 0);
		}
		else if(lastTransform == TransformType.FIXED)
		{
			GlStateManager.scale(0.2, 0.2, 0.2);
			GlStateManager.translate(-0.5, -5, -0.5);
		}
		
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer buffer = tess.getBuffer();
		
		float pixel = 1.0F / 16.0F;
		float minTubeWidth = 0;
		float maxTubeWidth = 1F;
		float tubeHeight = 10.0F;
		
		//Sides
		Minecraft.getMinecraft().renderEngine.bindTexture(SIDE_TEXTURE);
		RenderUtil.startDrawing(tess);
		{
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, 0, minTubeWidth, 4 * pixel, 0 * pixel);
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, tubeHeight, minTubeWidth, 4 * pixel, 16 * pixel);			
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, tubeHeight, maxTubeWidth, 12 * pixel, 16 * pixel);
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, 0, maxTubeWidth, 12 * pixel, 0 * pixel);
			
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, 0, maxTubeWidth, 4 * pixel, 0 * pixel);
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, tubeHeight, maxTubeWidth, 4 * pixel, 16 * pixel);			
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, tubeHeight, minTubeWidth, 12 * pixel, 16 * pixel);
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, 0, minTubeWidth, 12 * pixel, 0 * pixel);
			
			RenderUtil.addVertexWithUV(buffer, minTubeWidth, 0, 1 - maxTubeWidth, 4 * pixel, 0 * pixel);
			RenderUtil.addVertexWithUV(buffer, minTubeWidth, tubeHeight, 1 - maxTubeWidth, 4 * pixel, 16 * pixel);			
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, tubeHeight, 1 - maxTubeWidth, 12 * pixel, 16 * pixel);
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, 0, 1 - maxTubeWidth, 12 * pixel, 0 * pixel);		
			
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, 0, maxTubeWidth, 4 * pixel, 0 * pixel);
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, tubeHeight, maxTubeWidth, 4 * pixel, 16 * pixel);			
			RenderUtil.addVertexWithUV(buffer, minTubeWidth, tubeHeight, maxTubeWidth, 12 * pixel, 16 * pixel);
			RenderUtil.addVertexWithUV(buffer, minTubeWidth, 0, maxTubeWidth, 12 * pixel, 0 * pixel);			
		}
		tess.draw();
		
		//Ends
		Minecraft.getMinecraft().renderEngine.bindTexture(END_TEXTURE);
		RenderUtil.startDrawing(tess);
		{
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, tubeHeight, maxTubeWidth, 4 * pixel, 4 * pixel);
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, tubeHeight, 1 - maxTubeWidth, 4 * pixel, 12 * pixel);			
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, tubeHeight, 1 - maxTubeWidth, 12 * pixel, 12 * pixel);
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, tubeHeight, maxTubeWidth, 12 * pixel, 4 * pixel);
			
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, 0, 1 - maxTubeWidth, 4 * pixel, 4 * pixel);
			RenderUtil.addVertexWithUV(buffer, maxTubeWidth, 0, maxTubeWidth, 4 * pixel, 12 * pixel);			
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, 0, maxTubeWidth, 12 * pixel, 12 * pixel);
			RenderUtil.addVertexWithUV(buffer, 1 - maxTubeWidth, 0, 1 - maxTubeWidth, 12 * pixel, 4 * pixel);
		}
		tess.draw();
		GlStateManager.popMatrix();
	}

	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
