package alec_wam.CrystalMod.tiles.machine.xpfountain;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiXPFountain extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/xpfountain.png");
	public TileEntityXPFountain tileMachine;
	public GuiXPFountain(EntityPlayer player, TileEntityXPFountain tilePart)
    {
        super(new ContainerXPFountain(player, tilePart), TEXTURE);

        this.tileMachine = tilePart;
        this.name = ModBlocks.xpFountain.getLocalizedName();
        drawTitle = false;
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);

		GlStateManager.pushMatrix();
		RenderUtil.renderGuiTank(tileMachine.tankXP, 40, 23, zLevel, 12, 40, true);
		
		RenderUtil.renderGuiTank(tileMachine.tankEnder, 125, 23, zLevel, 12, 40, true);
		
		
		double progress = tileMachine.getScaledProgress(28);
		if(progress > 0){
			RenderUtil.renderGuiTank(ModFluids.fluidXpJuice, 1, 1, 52, 57, zLevel, progress, 5);
			RenderUtil.renderGuiTank(ModFluids.fluidEnder, 1, 1, 125-((int)progress), 57, zLevel, progress, 5);
		}
		GlStateManager.popMatrix();
		
		bindTexture(texture);
		GlStateManager.color(1, 1, 1, 1);
		drawTexturedModalRect(52, 56, 176, 0, 73, 7);
		
		if(tileMachine.fountainTime.getValue() > 0){
			int offset = 45 - (45 * (TileEntityXPFountain.MAX_FOUNTAIN_TIME - tileMachine.fountainTime.getValue()) / TileEntityXPFountain.MAX_FOUNTAIN_TIME);
			drawModalRectWithCustomSizedTexture(76, 3+offset, 176, 7+offset, 25, 45-offset, 256, 256);
		}
		
		if(this.isPointInRegion(40, 23, 12, 40, par1, par2)){
			List<String> list = Lists.newArrayList();
			if(tileMachine.tankXP !=null){
				if(tileMachine.tankXP.getFluid() !=null){
					FluidStack stack = tileMachine.tankXP.getFluid();
					list.add(stack.getLocalizedName()+" ("+stack.amount+")");
				}else {
					list.add("Empty");
				}
			}
			this.drawTooltipHoveringText(list, par1-guiLeft, par2-guiTop, mc.fontRendererObj);
            RenderHelper.enableStandardItemLighting();
		}
		if(this.isPointInRegion(125, 23, 12, 40, par1, par2)){
			List<String> list = Lists.newArrayList();
			if(tileMachine.tankEnder !=null){
				if(tileMachine.tankEnder.getFluid() !=null){
					FluidStack stack = tileMachine.tankEnder.getFluid();
					list.add(stack.getLocalizedName()+" ("+stack.amount+")");
				}else {
					list.add("Empty");
				}
			}
			this.drawTooltipHoveringText(list, par1-guiLeft, par2-guiTop, mc.fontRendererObj);
            RenderHelper.enableStandardItemLighting();
		}
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	}
}
