package alec_wam.CrystalMod.items.tools.projectiles;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderDagger extends Render<EntityDagger> {

	public static final ResourceLocation DAGGER_TEXTURE = new ResourceLocation("crystalmod:textures/entities/dagger.png");
	
	public RenderDagger(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityDagger entity, double x, double y, double z, float entityYaw, float partialTicks) {
		entity.getArrowStack();

		GlStateManager.pushMatrix();
		//GlStateManager.enableRescaleNormal();

		GlStateManager.translate(x, y, z);
		// mkae it smaller

        GlStateManager.scale(0.5F, 0.5F, 0.5F);


		float f11 = entity.arrowShake - partialTicks;
		if(f11 > 0.0F) {
			float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
			GlStateManager.rotate(f12, 0.0F, 0.0F, 1.0F);
		}

		if(renderManager == null || renderManager.renderEngine == null) {
			return;
		}

		customRendering(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.pushMatrix();
        renderManager.renderEngine.bindTexture(DAGGER_TEXTURE);

		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

        float min = -0.5f;
        float max = 0.5f;
        
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(min, min, 0).tex(0, 0).endVertex();
        vertexbuffer.pos(max, min, 0).tex(1, 0).endVertex();
        vertexbuffer.pos(max, max, 0).tex(1, 1).endVertex();
        vertexbuffer.pos(min, max, 0).tex(0, 1).endVertex();
        tessellator.draw();
        
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 1, 0);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(min, min, 0).tex(1, 0).endVertex();
        vertexbuffer.pos(max, min, 0).tex(0, 0).endVertex();
        vertexbuffer.pos(max, max, 0).tex(0, 1).endVertex();
        vertexbuffer.pos(min, max, 0).tex(1, 1).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90, 1, 0, 0);
        
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(min, min, 0).tex(0, 0).endVertex();
        vertexbuffer.pos(max, min, 0).tex(1, 0).endVertex();
        vertexbuffer.pos(max, max, 0).tex(1, 1).endVertex();
        vertexbuffer.pos(min, max, 0).tex(0, 1).endVertex();
        tessellator.draw();
        
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 1, 0);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(min, min, 0).tex(1, 0).endVertex();
        vertexbuffer.pos(max, min, 0).tex(0, 0).endVertex();
        vertexbuffer.pos(max, max, 0).tex(0, 1).endVertex();
        vertexbuffer.pos(min, max, 0).tex(1, 1).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        
        GlStateManager.popMatrix();
        
		//GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public void customRendering(EntityDagger entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.rotate(entity.rotationYaw, 0f, 1f, 0f);
		GlStateManager.rotate(-entity.rotationPitch, 1f, 0f, 0f);
		if(entity.getInGround()) {
			GlStateManager.translate(0, 0, -entity.getStuckDepth());
		}
		
		if(!entity.getInGround()) {
			entity.spin += 20 * partialTicks;
		}
		float r = entity.spin;

		GlStateManager.rotate(r, 0, 0, 1);
		
		
		GlStateManager.rotate(-90f, 0f, 1f, 0f);
		GlStateManager.rotate(-180, 0f, 0f, 1f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDagger entity) {
		return TextureMap.LOCATION_MISSING_TEXTURE;
	}
}
