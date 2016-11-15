package alec_wam.CrystalMod.entities.minecarts.chests;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;

public class ItemCrystalChestMinecartRender implements ICustomItemRenderer {

	private Map<CrystalChestType, EntityCrystalChestMinecartBase> minecarts = Maps.newHashMap();
	
	public EntityCrystalChestMinecartBase getMinecart(CrystalChestType type) {
		if(type == CrystalChestType.WOOD)return null;
		if(!minecarts.containsKey(type)){
			minecarts.put(type, EntityCrystalChestMinecartBase.makeMinecart(CrystalMod.proxy.getClientWorld(), type));
		}
		return minecarts.get(type);
	}
	
	@Override
	public void render(ItemStack stack, TransformType type) {
		CrystalChestType chesttype = CrystalChestType.values()[CrystalChestType.validateMeta(stack.getMetadata())];
		EntityCrystalChestMinecartBase minecart = getMinecart(chesttype);
		if(minecart == null){
			return;
		}
		
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
				GlStateManager.rotate(60F, 0F, 1F, 0F);
			}
			if(type == TransformType.FIRST_PERSON_LEFT_HAND){
				GlStateManager.rotate(120F, 0F, 1F, 0F);
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
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(-45, 1, 0, 0);
				GlStateManager.translate(0, -5, 0.5);
			}else{
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(45, 1, 0, 0);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.translate(0, -5, 0.5);
			}
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -1, 0);
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

}
