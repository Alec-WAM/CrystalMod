package alec_wam.CrystalMod.integration.minecraft;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartBase;

public class ItemMinecartRender implements ICustomItemRenderer {

	private Map<String, EntityMinecart> minecarts = Maps.newHashMap();
	
	public EntityMinecart getMinecart(String id, Class<? extends EntityMinecart> clazz) {
		if(!minecarts.containsKey(id)){
			try {
				minecarts.put(id, clazz.getConstructor(World.class).newInstance(CrystalMod.proxy.getClientWorld()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return minecarts.get(id);
	}
	
	@Override
	public void render(ItemStack stack) {
		String id = "empty";
		Class<? extends EntityMinecart> clazz = EntityMinecartEmpty.class;
		
		if(stack.getItem() == Items.CHEST_MINECART){
			id = "chest";
			clazz = EntityMinecartChest.class;
		}
		
		if(stack.getItem() == Items.FURNACE_MINECART){
			id = "furnace";
			clazz = EntityMinecartFurnace.class;
		}
		
		if(stack.getItem() == Items.TNT_MINECART){
			id = "tnt";
			clazz = EntityMinecartTNT.class;
		}
		
		if(stack.getItem() == Items.HOPPER_MINECART){
			id = "hopper";
			clazz = EntityMinecartHopper.class;
		}
		
		if(stack.getItem() == Items.COMMAND_BLOCK_MINECART){
			id = "commandblock";
			clazz = EntityMinecartCommandBlock.class;
		}
		
		EntityMinecart minecart = getMinecart(id, clazz);
		if(minecart == null){
			return;
		}
		renderMinecart(minecart, lastTransform);
	}
	
	public static void renderMinecart(EntityMinecart minecart, TransformType type){

		boolean atrib = true;
		GlStateManager.pushMatrix();
		if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (type == TransformType.GUI)
		{
			GlStateManager.pushMatrix();
			float scale = 1.8f;
			//Vec3d offset = essence.getRenderOffset();
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.5, 0);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
	        GlStateManager.disableRescaleNormal();
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
			if(type == TransformType.FIRST_PERSON_RIGHT_HAND){
				GlStateManager.rotate(120+60, 0F, 1F, 0F);
			}
			if(type == TransformType.FIRST_PERSON_LEFT_HAND){
				GlStateManager.rotate(120+60, 0F, 1F, 0F);
			}
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			
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
				//GlStateManager.rotate(90, 0, 1, 0);
				//GlStateManager.rotate(90-20, 0, 0, 1);
				//GlStateManager.rotate(-45, 1, 0, 0);
				GlStateManager.translate(2.7, -1.3, -2.7);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.scale(0.8, 0.8, 0.8);
			}else{
				GlStateManager.translate(0.3, -0.1, 0.3);
				GlStateManager.scale(0.8, 0.8, 0.8);
			}
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND || type == null){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.4, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		} else if(type == TransformType.FIXED){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.6, 0);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
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
