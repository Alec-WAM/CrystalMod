package alec_wam.CrystalMod.tiles.machine.crafting.liquidizer;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.util.ElementDualScaled;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiLiquidizer extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/liquidizer.png");
	public TileEntityLiquidizer tileMachine;
	ElementDualScaled progress;
	public GuiLiquidizer(EntityPlayer player, TileEntityLiquidizer tilePart)
    {
        super(new ContainerLiquidizer(player, tilePart), TEXTURE);

        this.tileMachine = tilePart;
        this.name = "Crystal Liquidizer";
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Arrow_Right.png", 48, 16)));
	    
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		RenderUtil.renderGuiTank(tileMachine.tank, 112, 23, zLevel, 12, 40);
		int xAxis = (par1 - (width - xSize) / 2);
		int yAxis = (par2 - (height - ySize) / 2);
		if(xAxis > 111 && xAxis < (111)+(14) && yAxis >= 22 && yAxis <= 22+(42))
		{
			List<String> lines = Lists.newArrayList();
			if(tileMachine.tank.getFluid() == null){
				lines.add(Lang.localize("gui.empty"));
			}else{
				lines.add(tileMachine.tank.getFluid().getLocalizedName()+" "+(tileMachine.tank.getFluid().amount+" / "+tileMachine.tank.getCapacity()+"MB"));
			}
			drawHoveringText(lines, xAxis, yAxis);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    this.progress.setQuantity(this.tileMachine.getScaledProgress(24));
	}
}
