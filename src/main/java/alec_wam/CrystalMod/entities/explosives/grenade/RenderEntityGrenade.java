package alec_wam.CrystalMod.entities.explosives.grenade;

import java.awt.Color;

import alec_wam.CrystalMod.util.RotatedAxes;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class RenderEntityGrenade extends Render<EntityGrenade> {
	
	public RenderEntityGrenade(RenderManager renderManager) 
	{
		super(renderManager);
		shadowSize = 0.0F;
	}

	@Override
	public void doRender(EntityGrenade grenade, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);
		float dYaw = (grenade.axes.getYaw() - grenade.prevRotationYaw);
		for(; dYaw > 180F; dYaw -= 360F) {}
		for(; dYaw <= -180F; dYaw += 360F) {}
		float dPitch = (grenade.axes.getPitch() - grenade.prevRotationPitch);
		for(; dPitch > 180F; dPitch -= 360F) {}
		for(; dPitch <= -180F; dPitch += 360F) {}
		float dRoll = (grenade.axes.getRoll() - grenade.prevRotationRoll);
		for(; dRoll > 180F; dRoll -= 360F) {}
		for(; dRoll <= -180F; dRoll += 360F) {}
		GlStateManager.rotate(180F - grenade.prevRotationYaw - dYaw * partialTicks, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(grenade.prevRotationPitch + dPitch * partialTicks, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(grenade.prevRotationRoll + dRoll * partialTicks, 1.0F, 0.0F, 0.0F);
		RenderUtil.renderCuboid(RenderUtil.getTexture(Blocks.IRON_BLOCK.getDefaultState()), null, -0.5, -0.40, -0.5, 0.40, 0.40, 0.40, 0.60, 0.60, 0.60, Color.WHITE.getRGB());
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGrenade entity) {
		return null;
	}
}
