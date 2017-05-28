package alec_wam.CrystalMod.tiles.machine.power.engine.lava;

import java.util.List;

import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.ElementFluidScaled;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiEngineLava extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/enginelava.png");
	public TileEntityEngineLava tileEngine;
	private ElementFluidScaled progressFluid;
	public GuiEngineLava(EntityPlayer player, TileEntityEngineLava tilePart)
    {
        super(new ContainerEngineLava(player, tilePart), TEXTURE);

        this.tileEngine = tilePart;
        this.name = "Lava Engine";
    }
	
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8+100, 24, this.tileEngine.energyStorage);
		addElement(energyElement);
		FluidStack fluid = tileEngine.renderFluid;
		progressFluid = ((ElementFluidScaled)addElement(new ElementFluidScaled(this, 76, 34).setFluid(fluid).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/progress_fluid_right.png", 34, 16)));;
		//this.speed = ((ElementDualScaled)addElement(new ElementDualScaled(this, 56+24, 44).setSize(16, 16).setTexture("crystalmod:textures/gui/elements/Scale_Flame.png", 32, 16)));
	    
	}
	
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    FluidStack fluid = tileEngine.renderFluid;
	    if(fluid !=null){
	    	progressFluid.setFluid(new FluidStack(fluid, 1));
	    	progressFluid.setSize(tileEngine.getScaledFuel(24), 16);
	    }else{
	    	progressFluid.setFluid(null);
	    	progressFluid.setSize(0, 16);
	    }
	}
	
	 public void drawGuiContainerForegroundLayer(int par1, int par2){
			super.drawGuiContainerForegroundLayer(par1, par2);
			
			final int barWidth = 12;
			final int barHeight = 58;
			int xpX = 53;
			int xpY = 21;
			RenderUtil.renderGuiTank(tileEngine.tank, xpX, xpY, zLevel, barWidth, barHeight);
			int xAxis = (par1 - (width - xSize) / 2);
			int yAxis = (par2 - (height - ySize) / 2);
			if(xAxis > xpX && xAxis < (xpX)+(barWidth+2) && yAxis >= xpY && yAxis <= xpY+(barHeight)+2)
			{
				List<String> lines = Lists.newArrayList();
				if(tileEngine.tank.getFluid() == null){
					lines.add(Lang.localize("gui.empty"));
				}else{
					lines.add(tileEngine.tank.getFluid().getLocalizedName()+" "+(tileEngine.tank.getFluid().amount+" / "+tileEngine.tank.getCapacity()+"MB"));
				}
				drawHoveringText(lines, xAxis, yAxis);
				RenderHelper.enableGUIStandardItemLighting();
			}
		}
}
