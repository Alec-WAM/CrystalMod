package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class GuiBackpackEnderBuffer extends GuiContainer {

	private InventoryBackpack backpackInv;
	private EnderBuffer buffer;
	
	public GuiBackpackEnderBuffer(InventoryBackpack backpackInv, EnderBuffer buffer) {
		super(new ContainerBackpackEnderBuffer(backpackInv, buffer));
		this.xSize = 256;
		this.backpackInv = backpackInv;
		this.buffer = buffer;
	}
	
	@Override
    public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		int cuX = 51;
		int rfX = 71;
		int flX = 91;
		
		if(ItemStackTools.isValid(backpackInv.getBackpack())){
			int id = ItemNBTHelper.getInteger(backpackInv.getBackpack(), "Code", 0);
			int color1 = id & 15;
            int color2 = (id >> 4) & 15;
            int color3 = (id >> 8) & 15;
            EnumDyeColor c1 = EnumDyeColor.byMetadata(color1);
            EnumDyeColor c2 = EnumDyeColor.byMetadata(color2);
            EnumDyeColor c3 = EnumDyeColor.byMetadata(color3);
            
            int back = Color.BLACK.getRGB();
            
            drawRect(10, 20, 30, 40, back);
            drawRect(10, 42, 30, 62, back);
            drawRect(10, 64, 30, 84, back);
            
            float[] afloat = EntitySheep.getDyeRgb(c1);
            float[] afloat1 = EntitySheep.getDyeRgb(c2);
            float[] afloat2 = EntitySheep.getDyeRgb(c3);
            
            drawRect(12, 22, 28, 38, new Color(afloat[0], afloat[1], afloat[2]).getRGB());
            drawRect(12, 44, 28, 60, new Color(afloat1[0], afloat1[1], afloat1[2]).getRGB());
            drawRect(12, 66, 28, 82, new Color(afloat2[0], afloat2[1], afloat2[2]).getRGB());
		}
		
		if(buffer !=null){
			EnderBuffer ebuffer = buffer;
			int cu = ebuffer.cuStorage.getCEnergyStored(); int maxCU = ebuffer.cuStorage.getMaxCEnergyStored();
			int rf = ebuffer.rfStorage.getEnergyStored(); int maxRF = ebuffer.rfStorage.getMaxEnergyStored();
			final int barHeight = 58;
			
			RenderUtil.renderPowerBar(cuX, 16, (int)zLevel, 10, barHeight, cu, maxCU, Color.CYAN.getRGB(), Color.CYAN.darker().getRGB());
			RenderUtil.renderPowerBar(rfX, 16, (int)zLevel, 10, barHeight, rf, maxRF, Color.RED.getRGB(), Color.RED.darker().getRGB());
			RenderUtil.renderGuiTank(ebuffer.tank, flX, 15, zLevel, 10, 58, true);

			int xAxis = (par1 - (width - xSize) / 2);
			int yAxis = (par2 - (height - ySize) / 2);
			if(xAxis > cuX-3 && xAxis < (cuX-3)+12 && yAxis >= 15 && yAxis <= 62+12)
			{
				List<String> lines = Lists.newArrayList();
				lines.add(cu > 0 ? "CU: "+ cu +" / "+ maxCU +"CU": Lang.localize("gui.empty"));
				drawHoveringText(lines, xAxis, yAxis);
				RenderHelper.enableGUIStandardItemLighting();
			}
			if(xAxis > rfX-3 && xAxis < (rfX-3)+12 && yAxis >= 15 && yAxis <= 62+12)
			{
				drawCreativeTabHoveringText(rf > 0 ? "RF: "+ rf +" / "+ maxRF +"RF": Lang.localize("gui.empty"), xAxis, yAxis);
				RenderHelper.enableGUIStandardItemLighting();
			}
			
			if(xAxis > flX-3 && xAxis < (flX-3)+12 && yAxis >= 15 && yAxis <= 62+12)
			{
				drawCreativeTabHoveringText(ebuffer.tank.getFluid() !=null ? ebuffer.tank.getFluid().getLocalizedName()+": "+ ebuffer.tank.getFluid().amount+" / "+ebuffer.tank.getCapacity()+"Mb": Lang.localize("gui.empty"), xAxis, yAxis);
				RenderHelper.enableGUIStandardItemLighting();
			}
	    }
	}
	
	public static final ResourceLocation texture = new ResourceLocation("crystalmod:textures/gui/machine/buffer.png");
	
	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2,int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		drawTexturedModalRect(guiLeft+46, guiTop+85, 24, 180, 162, 76);
		
		GlStateManager.pushMatrix();
		int x = 120-1;
	    int y = 32-1;
	    int i;
	    for (i = 0; i < 5; i++) {
	      drawTexturedModalRect(guiLeft+x, guiTop+y, 24, 180, 18, 18);
	      x += 18;
	    }
	    x = 120-1;
	    y = 32 + 18-1;
	    for (; i < 10; i++) {      
	      drawTexturedModalRect(guiLeft+x, guiTop+y, 24, 180, 18, 18);
	      x += 18;
	    }
	    GlStateManager.popMatrix();
		
		int cuX = 51;
		int rfX = 71;
		int flX = 91;
		
		drawTexturedModalRect(guiLeft+cuX-1, guiTop+15, 233, 196, 12, 61);
		drawTexturedModalRect(guiLeft+rfX-1, guiTop+15, 233, 196, 12, 61);
		drawTexturedModalRect(guiLeft+flX-1, guiTop+15, 233, 196, 12, 61);
	}
	
}
