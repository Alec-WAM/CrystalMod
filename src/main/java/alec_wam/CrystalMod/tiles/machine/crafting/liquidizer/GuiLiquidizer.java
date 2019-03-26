package alec_wam.CrystalMod.tiles.machine.crafting.liquidizer;

import java.util.List;

import alec_wam.CrystalMod.client.util.ElementDualScaled;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
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
	public void addTooltips(List<String> tooltip) {
		super.addTooltips(tooltip);
		if(this.isPointInRegion(111, 22, 12, 40, mouseX+guiLeft, mouseY+guiTop)){
			if(tileMachine.tank.getFluid() == null){
				tooltip.add(Lang.localize("gui.empty"));
			}else{
				tooltip.add(tileMachine.tank.getFluid().getLocalizedName()+" "+(tileMachine.tank.getFluid().amount+" / "+tileMachine.tank.getCapacity()+"MB"));
			}
		}
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		RenderUtil.renderGuiTank(tileMachine.tank, 112, 23, zLevel, 12, 40, true);
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    this.progress.setQuantity(this.tileMachine.getScaledProgress(24));
	}
}
