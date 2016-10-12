package alec_wam.CrystalMod.tiles.machine.worksite;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class TileWorksiteRenderer<T extends TileWorksiteBase> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileWorksiteBase worksite, double x, double y, double z, float partialTicks, int destroyStage) {
		
		if (worksite.hasWorkBounds() && worksite.renderBounds()) {
			GlStateManager.pushMatrix();
			//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,	0.f, 240.f);
			//GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			//GlStateManager.translate(x, y, z);
			BlockPos min = worksite.getWorkBoundsMin();
			BlockPos max = worksite.getWorkBoundsMax();
			if (min != null && max != null) {
				BlockPos pos = worksite.getPos();
				AxisAlignedBB bb = new AxisAlignedBB(
						min.getX() - pos.getX() + x,
						min.getY() - pos.getY() + y,
						min.getZ() - pos.getZ() + z,
						max.getX() - pos.getX() + x + 1,
						max.getY() - pos.getY() + y + 1,
						max.getZ() - pos.getZ() + z + 1
				);
				
				/*GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            GlStateManager.glLineWidth(10.0F);
	            GlStateManager.disableTexture2D();
	            GlStateManager.depthMask(false);
	            RenderGlobal.func_189697_a(bb.expandXyz(0.0020000000949949026D), 1.0F, 1.0F, 1.0F, 0.4F);
	            GlStateManager.depthMask(true);
	            GlStateManager.enableTexture2D();
	            GlStateManager.disableBlend();*/
				
				//GlStateManager.disableAlpha();
				
				/*GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            GlStateManager.glLineWidth(6.0F);
	            GlStateManager.disableTexture2D();
	            GlStateManager.depthMask(false);

	            RenderGlobal.func_189697_a(bb, 1.0F, 1.0F, 1.0F, 1F);

	            GlStateManager.depthMask(true);
	            GlStateManager.enableTexture2D();
	            GlStateManager.disableBlend();*/
				RenderUtil.drawOutlinedBoundingBox(bb, (float) 1, (float) 1, (float) 1, /*0.0625f*/2.0f);
				//GlStateManager.enableAlpha();
				//renderBoundingBox(worksite.getPos(), min, max, worksite.boundsColor().x, worksite.boundsColor().y, worksite.boundsColor().z, worksite.boundsColor().w, 0.0f);
			}
			//GL11.glPopAttrib();
			GlStateManager.popMatrix();
		}
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
