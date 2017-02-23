package alec_wam.CrystalMod.tiles.cauldron;

import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderTileCrystalCauldron extends TileEntitySpecialRenderer<TileEntityCrystalCauldron> {

	@Override
    public void renderTileEntityAt(TileEntityCrystalCauldron te, double x, double y, double z, float partialTicks, int destroyStage) {
		if(te.crystalStack == null)return;
		GlStateManager.pushMatrix();
		float height = 0.3f+(0.7f*((float)(te.crystalStack.amount)/1000.0f));
		RenderUtil.renderFluidCuboid(te.crystalStack, te.getPos(), x, y, z, 0.13, 0.3, 0.13, 0.87, height, 0.87, false);
		GlStateManager.popMatrix();
	}
	
}
