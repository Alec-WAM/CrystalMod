package alec_wam.CrystalMod.tiles.pipes;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileEntityPipeRenderer extends TileEntitySpecialRenderer<TileEntityPipe> {

	@Override
    public void renderTileEntityAt(TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	GlStateManager.translate(x+0.5, y+0.5, z+0.5);
    	GlStateManager.scale(1, -1, -1);
    	GlStateManager.translate(-1, 0, -1);
    	//Tessellator tessellator = Tessellator.getInstance();
    	//WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.popMatrix();
    }
}
