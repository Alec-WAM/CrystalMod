package alec_wam.CrystalMod.tiles.tank;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderTileEntityTank<T extends TileEntityTank> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileEntityTank tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x + 0.5F, (float)y, (float)z + 0.5F);
		GlStateManager.popMatrix();
	}
}
