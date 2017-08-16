package alec_wam.CrystalMod.items.tools.projectiles;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderDarkarang extends Render<EntityDarkarang> {

	public RenderDarkarang(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityDarkarang entity, double x, double y, double z, float entityYaw, float partialTicks) {
		ItemStack item = entity.getArrowStack();

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();

		GlStateManager.translate(x, y, z);
		// mkae it smaller
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		customRendering(entity, x, y, z, entityYaw, partialTicks);

		float f11 = entity.arrowShake - partialTicks;
		if(f11 > 0.0F) {
			float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
			GlStateManager.rotate(f12, 0.0F, 0.0F, 1.0F);
		}

		if(renderManager == null || renderManager.renderEngine == null) {
			return;
		}

		renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if(ItemStackTools.isValid(item)) {
			Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.NONE);
		}
		else {
			ItemStack dummy = new ItemStack(Items.STICK);
			Minecraft.getMinecraft().getRenderItem().renderItem(dummy, Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel());
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public void customRendering(EntityDarkarang entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.rotate(entity.rotationYaw, 0f, 1f, 0f);
		GlStateManager.rotate(-entity.rotationPitch, 1f, 0f, 0f);

		if(entity.getInGround()) {
			GlStateManager.translate(0, 0, -entity.getStuckDepth());
		}

		//customCustomRendering(entity, x, y, z, entityYaw, partialTicks);

		GlStateManager.rotate(-90f, 0f, 1f, 0f);

		GlStateManager.rotate(-90, 1f, 0f, 0f);
		
		if(!entity.getInGround()) {
			entity.spin += 20 * partialTicks;
		}
		float r = entity.spin;

		GlStateManager.rotate(r, 0f, 0f, 1f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDarkarang entity) {
		return TextureMap.LOCATION_MISSING_TEXTURE;
	}
}
