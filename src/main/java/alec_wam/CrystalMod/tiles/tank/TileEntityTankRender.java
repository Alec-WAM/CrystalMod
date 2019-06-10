package alec_wam.CrystalMod.tiles.tank;

import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityTankRender extends TileEntityRenderer<TileEntityTank> {

	@Override
	public void render(TileEntityTank tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		GlStateManager.pushMatrix();
		if(tile.tank.getFluid() !=null){
			FluidStack stack = tile.tank.getFluid();
			int brightness = tile.getWorld().getCombinedLight(tile.getPos(), stack.getFluid().getLuminosity());
			GlStateManager.disableLighting();
        	renderTankFluid(stack, tile.tank.getCapacity(), x, y, z, brightness);
        	GlStateManager.enableLighting();
		}
		GlStateManager.popMatrix();
	}
	
	public static void renderTankFluid(FluidStack fluid, int capacity, double x, double y, double z, int brightness){
		if(fluid !=null){
			double pixel = 1.0 / 16.0;
			double edge = pixel * 1.005;
			double fluidPercent = ((double)fluid.amount / (double)capacity);
			double size = (pixel * 14) * fluidPercent;
			int color = fluid.getFluid().getColor();			
			RenderUtil.renderFluidCuboid(fluid.getFluid(), x, y, z, edge, edge, edge, 1.0 - edge, edge + size, 1.0 - edge, color, false, brightness);
		}
	}
}