package alec_wam.CrystalMod.tiles.spawner;

import java.util.concurrent.ConcurrentMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.model.TRSRTransformation;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class ItemRenderMobEssence implements ICustomItemRenderer {

	public static String ESSENCE_CACHE_ID = "MobEssence.";
    public static String MINION_CACHE_ID = "MobEssence.";
    public static final ConcurrentMap<String, EntityLivingBase> entityCache = ProfileUtil.buildCache(3 * 60 * 60, 0);
    public static EntityPig defaultPig;
    
    public static EntityLivingBase getRenderEntity(String name){
    	EntityLivingBase entity = entityCache.get(ESSENCE_CACHE_ID+name);
    	if(entity == null){
    		@SuppressWarnings("rawtypes")
			EntityEssenceInstance essence = ItemMobEssence.getEssence(name);
    		if(essence == null){
    			if(defaultPig == null)defaultPig = ItemMobEssence.DEFAULT_PIG.createRenderEntity(CrystalMod.proxy.getClientWorld());
    			return defaultPig;
    		}else{
	    		entity = essence.createRenderEntity(CrystalMod.proxy.getClientWorld());
	    		if(entity !=null)entityCache.put(ESSENCE_CACHE_ID+name, entity);
    		}
    	}
    	return entity;
    }
    
    public static EntityLivingBase getRenderEntityNullable(String name){
    	EntityLivingBase entity = entityCache.get(name);
    	if(entity == null){
    		EntityEssenceInstance<?> essence = ItemMobEssence.getEssence(name);
    		if(essence == null){
    			return null;
    		}else{
	    		entity = essence.createRenderEntity(CrystalMod.proxy.getClientWorld());
	    		if(entity !=null)entityCache.put(name, entity);
    		}
    	}
    	return entity;
    }
    
	@Override
	public void render(ItemStack stack) {
		TransformType type = lastTransform;
		String name = ItemNBTHelper.getString(stack, ItemMobEssence.NBT_ENTITYNAME, "Pig");
		EntityLivingBase entity = getRenderEntity(name);
		if(entity == null){
			return;
		}
		@SuppressWarnings("rawtypes")
		EntityEssenceInstance essence = ItemMobEssence.getEssence(name);
		if(essence == null){
			essence = ItemMobEssence.DEFAULT_PIG;
		}
		boolean atrib = false;
		GlStateManager.pushMatrix();
		if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		float partialTicks = 0.0f;
		
		if(type == null){
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.pushAttrib();
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	        
	        GlStateManager.popMatrix();
	        return;
		}
		
		if (type == TransformType.GUI)
		{
			GlStateManager.pushMatrix();
			float scale = essence.getRenderScale(type);
			Vec3d offset = essence.getRenderOffset(type);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(offset.xCoord, offset.yCoord, offset.zCoord);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.pushAttrib();
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
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
			float scale = essence.getRenderScale(type);
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
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
			GlStateManager.disableBlend();
			
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}
		else if (type == TransformType.THIRD_PERSON_RIGHT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = essence.getRenderScale(type);
			GlStateManager.scale(1.5F*scale, 1.5F*scale, 1.5F*scale);
			if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(-45, 1, 0, 0);
				GlStateManager.translate(0, 0, 0.5);
			}else{
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(45, 1, 0, 0);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.translate(0, 0, 0.5);
			}
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND){
			GlStateManager.pushMatrix();
			float scale = essence.getRenderScale(type);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -1, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		} else if(type == TransformType.FIXED){
			GlStateManager.pushMatrix();
			float scale = essence.getRenderScale(type);
			Vec3d offset = essence.getRenderOffset(type);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(offset.xCoord, offset.yCoord, offset.zCoord);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
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
