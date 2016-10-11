package alec_wam.CrystalMod.tiles.machine.crafting.infuser;

import java.util.List;

import alec_wam.CrystalMod.client.util.ElementDualScaled;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.ElementFluidScaled;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;

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
	
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 103, 33).setMode(2).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Arrow_Left.png", 48, 16)));
		this.progress2 = ((ElementFluidScaled)addElement(new ElementFluidScaled(this, 53, 34).setSize(24, 16)));

	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		RenderUtil.renderGuiTank(tileMachine.tank, 31, 22, zLevel, 14, 42);
		if(this.isPointInRegion(31, 22, 14, 42, par1, par2)){
			List<String> list = Lists.newArrayList();
			if(tileMachine.tank !=null){
				if(tileMachine.tank.getFluid() !=null){
					FluidStack stack = tileMachine.tank.getFluid();
					list.add(stack.getLocalizedName()+" ("+stack.amount+")");
				}else {
					list.add("Empty");
				}
			}
			this.drawTooltipHoveringText(list, par1-guiLeft, par2-guiTop, mc.fontRendererObj);
		}
	}
	
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    this.progress.setQuantity(this.tileMachine.getScaledProgress(24));
	    FluidStack fluid = tileMachine.tank.getFluid();
	    if(fluid !=null){
	    	progress2.setFluid(new FluidStack(fluid, 1));
	    	progress2.setSize(tileMachine.getScaledProgress(24), 16);
	    }else{
	    	progress2.setFluid(null);
	    	progress2.setSize(0, 16);
	    }
	}
}
