package alec_wam.CrystalMod.client.gui.overlay;

import java.awt.Color;

import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.client.gui.overlay.IOvelayTile.InfoProvider;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.client.config.GuiUtils;

public class InfoProviderTank implements InfoProvider {

	public final IFluidTank tank;
	
	public InfoProviderTank(IFluidTank tank){
		this.tank = tank;
	}
	
	@Override
	public void render(ClientWorld world, TileEntity tile, BlockPos pos, Direction side) {
		FluidStack fluid = tank.getFluid();
		int capacity = tank.getCapacity();
		//TODO Add Fluid Name Box on bottom
		//String fluidname = fluid !=null ? fluid.getLocalizedName() + ": " + fluid.amount + " / "+ capacity +"MB": Lang.localize("empty");
		//list.add(fluidname);
		int offsetX = 0;
		GlStateManager.pushMatrix();
		GlStateManager.translated(HUDOverlayHandler.getOverlayX() + offsetX, HUDOverlayHandler.getOverlayY() - 5 - 58, 0);	    		
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);	    			    		
		int colorBorder = Color.GRAY.darker().getRGB();
		int colorInside = Color.GRAY.getRGB();
		GuiUtils.drawGradientRect(-1, -6, -11, 8, (58 - 9), colorBorder, colorBorder);
		GuiUtils.drawGradientRect(0, -5, -10, 7, (58 - 10), colorInside, colorInside);
		if(fluid !=null){
			RenderUtil.renderGuiTank(fluid, capacity, fluid.amount, -5, -10, 0, 12, 58, false);
		}
		GlStateManager.popMatrix();
	}

}
