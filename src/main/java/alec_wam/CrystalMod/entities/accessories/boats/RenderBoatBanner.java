package alec_wam.CrystalMod.entities.accessories.boats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderBoatBanner extends Render<EntityBoatBanner> {
 	
	public RenderBoatBanner(RenderManager renderManager) {
		super(renderManager);
	}
	
 	@Override
	public void doRender(EntityBoatBanner entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
 		if(!entity.isRiding())
			return;
 		Entity boat = entity.getRidingEntity();
		float rot = 180F - entityYaw;
		
		ItemStack stack = entity.getBanner();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(rot, 0F, 1F, 0F);
		GlStateManager.translate(0F, 0.8F, 0.0F);
		if(boat.getPassengers().size() == 1)
			GlStateManager.translate(0F, 0F, 0.6F);	
		
		float scale = 2.0F;
		GlStateManager.scale(scale, scale, scale);
		
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);		
		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBoatBanner entity) {
		return null;
	}
 }
