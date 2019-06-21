package alec_wam.CrystalMod.tiles.xp;

import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.init.FixedFluidRegistry;
import alec_wam.CrystalMod.util.RenderUtil;
import alec_wam.CrystalMod.util.XPUtil;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class TileEntityXPTankRender extends TileEntityRenderer<TileEntityXPTank> {

	@Override
	public void render(TileEntityXPTank tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		GlStateManager.pushMatrix();
		if(tile.xpCon.getFluid() !=null){
			int brightness = tile.getWorld().getCombinedLight(tile.getPos(), 8);
			GlStateManager.disableLighting();
        	renderTankXP(tile.xpCon.getFluidAmount(), x, y, z, brightness);
        	GlStateManager.enableLighting();
		}
		GlStateManager.popMatrix();
	}
	
	public static void renderTankXP(int amount, double x, double y, double z, int brightness){
		if(amount > 0){
			double pixel = 1.0 / 16.0;
			double fluidPercent = ((double)amount / (double)XPUtil.getLiquidForLevel(TileEntityXPTank.maxLevels));
			double size = (pixel * 11.5) * fluidPercent;
			int color = FixedFluidRegistry.XP.getColor();			
			RenderUtil.renderFluidCuboid(FixedFluidRegistry.XP, x, y, z, 0.1, 0.1, 0.1, 0.9, 0.1 + size, 0.9, color, false, brightness);
		}
	}
}