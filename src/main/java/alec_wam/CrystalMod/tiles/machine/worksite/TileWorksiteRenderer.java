package alec_wam.CrystalMod.tiles.machine.worksite;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevator;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class TileWorksiteRenderer<T extends TileWorksiteBase> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileWorksiteBase worksite, double x, double y, double z, float partialTicks, int destroyStage) {
		
		if (worksite.hasWorkBounds() && worksite.renderBounds()) {
			/*GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			BlockPos min = worksite.getWorkBoundsMin();
			BlockPos max = worksite.getWorkBoundsMax();
			if (min != null && max != null) {
				BlockPos pos = worksite.getPos();
				AxisAlignedBB bb = new AxisAlignedBB(
						min.getX() - pos.getX(),
						min.getY() - pos.getY(),
						min.getZ() - pos.getZ(),
						max.getX() - pos.getX() + 1,
						max.getY() - pos.getY() + 1,
						max.getZ() - pos.getZ() + 1
				);
				RenderUtil.drawOutlinedBoundingBox(bb, (float) 1, (float) 1, (float) 1, 0.0625f 3.5f);
			}
			//GL11.glPopAttrib();
			GlStateManager.popMatrix();*/
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);

			GlStateManager.pushMatrix();
	        //GlStateManager.disableDepth();
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
	        //GlStateManager.enableDepth();
	        GlStateManager.popMatrix();
	        
	        GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean isGlobalRenderer(TileWorksiteBase te) {
    	return true;
	}
	
	
	private void renderBoundingBox(BlockPos te, BlockPos min, BlockPos max, double r, double g, double b, double alpha,	float expansion) {
		//GlStateManager.disableLighting();
		//GlStateManager.color((float)r, (float)g, (float)b, (float)alpha);
		AxisAlignedBB bb = new AxisAlignedBB(min.subtract(te), max.subtract(te).add(1, 1, 1));
		//bb.offset(-te.getX(), -te.getY(), -te.getZ());
		RenderUtil.drawOutlinedBoundingBox(bb, (float) r, (float) g, (float) b, /*0.0625f*/2.0f);
		//GlStateManager.enableLighting();
	}

}
