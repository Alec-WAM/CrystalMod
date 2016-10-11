package alec_wam.CrystalMod.tiles.machine.mobGrinder;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import alec_wam.CrystalMod.tiles.machine.ContainerNull;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;

public class GuiMobGrinder extends GuiContainer {

	private TileEntityMobGrinder grinder;
	public GuiMobGrinder(EntityPlayer player, TileEntityMobGrinder grinder) {
		super(new ContainerNull());
		this.grinder = grinder;
	}
	
    public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		
		int cu = grinder.energyStorage.getCEnergyStored(); int maxCU = grinder.energyStorage.getMaxCEnergyStored();
		final int barWidth = 12;
		final int barHeight = 58;
		int cuX = 51;
		int xpX = 71;
		RenderUtil.renderPowerBar(cuX, 16, zLevel, barWidth, barHeight, cu, maxCU, Color.CYAN.getRGB(), Color.CYAN.darker().getRGB());
		RenderUtil.renderGuiTank(grinder.xpCon, xpX, 16, zLevel, barWidth, barHeight);

		int xAxis = (par1 - (width - xSize) / 2);
		int yAxis = (par2 - (height - ySize) / 2);
		if(xAxis > cuX-3 && xAxis < (cuX-3)+(barWidth+2) && yAxis >= 15 && yAxis <= 15+(barHeight)+2)
		{
			List<String> lines = Lists.newArrayList();
			lines.add(cu > 0 ? "CU: "+ cu +" / "+ maxCU +"CU": Lang.localize("gui.empty"));
			drawHoveringText(lines, xAxis, yAxis);
			RenderHelper.enableGUIStandardItemLighting();
		}
		if(xAxis > xpX-3 && xAxis < (xpX-3)+(barWidth+2) && yAxis >= 15 && yAxis <= 15+(barHeight)+2)
		{
			List<String> lines = Lists.newArrayList();
			if(grinder.xpCon.getFluid() == null){
				lines.add(Lang.localize("gui.empty"));
			}else{
				lines.add(grinder.xpCon.getFluid().getLocalizedName()+" "+(grinder.xpCon.getFluid().amount+" / "+grinder.xpCon.getCapacity()+"MB"));
				
				if(grinder.xpCon.getExperienceLevel() > 0)lines.add(TextFormatting.GREEN+""+grinder.xpCon.getExperienceLevel()+"L");
			}
			drawHoveringText(lines, xAxis, yAxis);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}
	public void drawGuiContainerBackgroundLayer(float var1, int var2,int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/machine/mobGrinder.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int cuX = 51;
		int xpX = 71;
		
		//drawTexturedModalRect(guiLeft+cuX-1, guiTop+15, 233, 196, 14, 61);
		drawScaledCustomSizeModalRect(guiLeft+cuX-1, guiTop+15, 233, 196, 12, 61, 14, 61, 256, 256);
		drawScaledCustomSizeModalRect(guiLeft+xpX-1, guiTop+15, 233, 196, 12, 61, 14, 61, 256, 256);
		//drawTexturedModalRect(guiLeft+xpX-1, guiTop+15, 233, 196, 14, 61);
		
		
		
		
	}
	
}
