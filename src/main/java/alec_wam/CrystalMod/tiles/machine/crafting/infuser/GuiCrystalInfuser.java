package alec_wam.CrystalMod.tiles.machine.crafting.infuser;

import java.util.List;

import alec_wam.CrystalMod.client.util.ElementDualScaled;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.ElementFluidScaled;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiCrystalInfuser extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/infuser.png");
	public TileEntityCrystalInfuser tileMachine;
	ElementDualScaled progress;
	ElementFluidScaled progress2;
	public GuiCrystalInfuser(EntityPlayer player, TileEntityCrystalInfuser tilePart)
    {
        super(new ContainerCrystalInfuser(player, tilePart), TEXTURE);

        this.tileMachine = tilePart;
        this.name = "Crystal Infuser";
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 103, 33).setMode(2).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Arrow_Left.png", 48, 16)));
		this.progress2 = ((ElementFluidScaled)addElement(new ElementFluidScaled(this, 49, 34).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Fluid_Right.png", 48, 16)));

	}
	
	@Override
	public void addTooltips(List<String> tooltip) {
		super.addTooltips(tooltip);
		if(this.isPointInRegion(32, 23, 12, 40, mouseX+guiLeft, mouseY+guiTop)){
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
		RenderUtil.renderGuiTank(tileMachine.tank, 32, 23, zLevel, 12, 40, true);
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
		int progress = this.tileMachine.getScaledProgress(24);
	    this.progress.setQuantity(progress);
	    FluidStack fluid = tileMachine.tank.getFluid();
	    if(fluid !=null){
	    	progress2.setFluid(new FluidStack(fluid, 1));
	    	progress2.setQuanitity(progress);
	    }else{
	    	progress2.setFluid(null);
	    	progress2.setQuanitity(0);
	    }
	}
}
