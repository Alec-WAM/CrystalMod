package alec_wam.CrystalMod.tiles.machine.worksite;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileWorksiteRenderer<T extends TileWorksiteBase> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileWorksiteBase worksite, double x, double y, double z, float partialTicks, int destroyStage) {
		
		if (worksite.hasWorkBounds() && worksite.renderBounds()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);

			GlStateManager.pushMatrix();
	        GlStateManager.disableTexture2D();
	        GlStateManager.disableCull();
	        GlStateManager.enableBlend();
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.glLineWidth(3.0F);
            BlockPos min = worksite.getWorkBoundsMin();
			BlockPos max = worksite.getWorkBoundsMax().add(1, 1, 1);
			BlockPos pos = worksite.getPos();
            AxisAlignedBB aabb = new AxisAlignedBB(min, max).offset(-pos.getX(), -pos.getY(), -pos.getZ());
            
	        Tessellator tessellator = Tessellator.getInstance();

	        double minX = aabb.minX;
	        double minY = aabb.minY;
	        double minZ = aabb.minZ;
	        double maxX = aabb.maxX;
	        double maxY = aabb.maxY;
	        double maxZ = aabb.maxZ;

	        float red = 0.0f;
	        float green = 0.0f;
	        float blue = 0.0f;
	        float alpha = 0.5f;
	        
	        tessellator.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

	        tessellator.getBuffer().pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.getBuffer().pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
	        tessellator.getBuffer().pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

	        tessellator.draw();
	        
	        GlStateManager.disableBlend();
	        GlStateManager.enableCull();
	        GlStateManager.enableTexture2D();
	        GlStateManager.popMatrix();
	        
	        GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean isGlobalRenderer(TileWorksiteBase te) {
    	return true;
	}

}
