package alec_wam.CrystalMod.integration.minecraft;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityBoat.Type;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemBoatRender implements ICustomItemRenderer {

	private Map<EntityBoat.Type, EntityBoat> boats = Maps.newHashMap();
	
	public EntityBoat getBoat(EntityBoat.Type type) {
		if(!boats.containsKey(type)){
			try {
				EntityBoat boat = new EntityBoat(CrystalMod.proxy.getClientWorld());
				boat.setBoatType(type);
				boats.put(type, boat);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return boats.get(type);
	}
	
	@Override
	public void render(ItemStack stack) {
		EntityBoat.Type type = Type.OAK;
		if(stack.getItem() == Items.BIRCH_BOAT){
			type = Type.BIRCH;
		}
		if(stack.getItem() == Items.SPRUCE_BOAT){
			type = Type.SPRUCE;
		}
		if(stack.getItem() == Items.JUNGLE_BOAT){
			type = Type.JUNGLE;
		}
		if(stack.getItem() == Items.ACACIA_BOAT){
			type = Type.ACACIA;
		}
		if(stack.getItem() == Items.DARK_OAK_BOAT){
			type = Type.DARK_OAK;
		}
		EntityBoat boat = getBoat(type);
		if(boat == null){
			return;
		}
		renderBoat(boat, lastTransform);
	}
	
	public static void renderBoat(EntityBoat boat, TransformType type){

		boolean atrib = false;
		GlStateManager.pushMatrix();
		if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (type == TransformType.GUI)
		{
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			float scale = 1.2f;
			//Vec3d offset = essence.getRenderOffset();
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.5, 0);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.pushAttrib();
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(boat, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		else if (type == TransformType.FIRST_PERSON_RIGHT_HAND || type == TransformType.FIRST_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 1.5f;
			GlStateManager.scale(0.8F*scale, 0.8F*scale, 0.8F*scale);
			GlStateManager.translate(2, 0.5, 0);
			if(type == TransformType.FIRST_PERSON_LEFT_HAND){
				GlStateManager.rotate(120+60, 0F, 1F, 0F);
			}
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);			
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(boat, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.enableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}
		else if (type == TransformType.THIRD_PERSON_RIGHT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 2.0f;
			GlStateManager.scale(1.5F*scale, 1.5F*scale, 1.5F*scale);
			if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
				GlStateManager.scale(0.5, 0.5, 0.5);
				GlStateManager.translate(-0.5, 0, 0.8);
			}else{
				GlStateManager.translate(0.3, -0.1, 0.3);
				GlStateManager.scale(0.5, 0.5, 0.5);
			}
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(boat, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND){
			GlStateManager.pushMatrix();
			float scale = 2.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.4, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(boat, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		} else if(type == TransformType.HEAD){
			GlStateManager.pushMatrix();
			float scale = 3f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.4, 0);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(boat, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}else if(type == TransformType.FIXED || type == null){
			GlStateManager.pushMatrix();
			float scale = 2.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.6, 0);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(boat, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}

		if(atrib)GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
	
	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
