package alec_wam.CrystalMod.tiles.machine.enderbuffer.gui;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.util.GuiContainerTabbed;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer.Mode;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class GuiEnderBuffer extends GuiContainerTabbed {

	private TileEntityEnderBuffer buffer;
	public GuiEnderBuffer(EntityPlayer player, TileEntityEnderBuffer buffer) {
		super(new ContainerEnderBuffer(player, buffer), new ResourceLocation("crystalmod:textures/gui/machine/buffer.png"));
		this.xSize = 256;
		this.buffer = buffer;
	}
	
    @Override
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		int cuX = 51;
		int rfX = 71;
		int flX = 91;
		
		if(buffer !=null){
			int id = buffer.code;
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
		
		if(buffer !=null && buffer.hasBuffer()){
			EnderBuffer ebuffer = buffer.getBuffer();
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
	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2,int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
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
	
	@Override
	protected void initTabs() {
		tabManager.add(new RedstoneLedger());
		tabManager.add(new ConfigLedger());
	}

	private class ConfigLedger extends Tab {

		public String tooltip = "";
		
		public ConfigLedger(){
			maxHeight = 55;
			overlayColor = 0xd46c1f;
		}
		@Override
		public boolean handleMouseClicked(int x, int y, int mouseButton)
		{
		    if (!isFullyOpened()) {
		      return false;
		    }
		    x -= this.currentShiftX;
		    y -= this.currentShiftY;
		    //System.out.println(x+" "+y);
		    if ((x < 16) || (x >= 100) || (y < 20) || (y >= 84)) {
		      return false;
		    }
		    if(x > 20 && x < 36){
		    	GuiEnderBuffer.this.buffer.incrsCUMode();
		    	return true;
		    }
		    if(x > 40 && x < 56){
		    	GuiEnderBuffer.this.buffer.incrsRFMode();
		    	return true;
		    }
		    if(x > 60 && x < 76){
		    	GuiEnderBuffer.this.buffer.incrsFluidMode();
		    	return true;
		    }
		    if(x > 80 && x < 96){
		    	GuiEnderBuffer.this.buffer.incrsInvMode();
		    	return true;
		    }
		    
		    return true;
		}
		@Override
		public void update() {
			super.update();
			if (!isFullyOpened()) {
			    return;
			}
			int startX = Mouse.getX() - ((GuiEnderBuffer.this.width - GuiEnderBuffer.this.xSize) / 2) + 12;
			int x = (startX-this.currentShiftX-GuiEnderBuffer.this.xSize-12);
			//this.tooltip = x+", "+y;
			if(x > 40 && x < 40+30){
				this.tooltip = "CU-Mode: "+GuiEnderBuffer.this.buffer.cuMode.name();
			}
			else if(x > 80 && x < 80+30){
				this.tooltip = "RF-Mode: "+GuiEnderBuffer.this.buffer.rfMode.name();
			}
			else if(x > 120 && x < 120+30){
				this.tooltip = "Fluid-Mode: "+GuiEnderBuffer.this.buffer.fluidMode.name();
			}
			else if(x > 160 && x < 160+30){
				this.tooltip = "Inventory-Mode: "+GuiEnderBuffer.this.buffer.invMode.name();
			}
			else{
				this.tooltip = "";
			}
		}
		
		@Override
		public void draw(int x, int y) {
			drawBackground(x,y);
			
			int[][] icons = new int[Mode.values().length][4];
		    icons[Mode.DISABLED.ordinal()] = new int[]{211+30, 166, 15, 15};
		    icons[Mode.RECIEVE.ordinal()] = new int[]{211+15, 166, 15, 15};
		    icons[Mode.SEND.ordinal()] = new int[]{211, 166, 15, 15};
		    icons[Mode.BOTH.ordinal()] = new int[]{211-15, 166, 15, 15};
		    
		    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/Machine/buffer.png"));
		    int[] both = icons[Mode.BOTH.ordinal()];
		    GuiEnderBuffer.this.drawTexturedModalRect(x + 4, y + 4, both[0], both[1], both[2], both[3]);
		    if (!isFullyOpened()) {
			     return;
			}
		    TileEntityEnderBuffer buffer = GuiEnderBuffer.this.buffer;
		    int[] cu = icons[buffer.cuMode.ordinal()];
		    int[] rf = icons[buffer.rfMode.ordinal()];
		    int[] fl = icons[buffer.fluidMode.ordinal()];
		    int[] inv = icons[buffer.invMode.ordinal()];
		    GuiEnderBuffer.this.drawTexturedModalRect(x + 20, y + 24, cu[0], cu[1], cu[2], cu[3]);
		    GuiEnderBuffer.this.drawTexturedModalRect(x + 40, y + 24, rf[0], rf[1], rf[2], rf[3]);
		    GuiEnderBuffer.this.drawTexturedModalRect(x + 60, y + 24, fl[0], fl[1], fl[2], fl[3]);
		    GuiEnderBuffer.this.drawTexturedModalRect(x + 80, y + 24, inv[0], inv[1], inv[2], inv[3]);
		    GuiEnderBuffer.this.fontRendererObj.drawStringWithShadow(("Configuration"), x + 23, y + 6, 0xe1c92f);
		    GL11.glColor4d(1, 1, 1, 1);
		}

		@Override
		public String getTooltip() {
			return tooltip;
		}
		
	}
	
	private class RedstoneLedger extends Tab {

		public String tooltip = "";
		
		public RedstoneLedger(){
			maxHeight = 55;
			overlayColor = 0xFF0000;
		}
		@Override
		public boolean handleMouseClicked(int x, int y, int mouseButton)
		{
		    if (!isFullyOpened()) {
		      return false;
		    }
		    x -= this.currentShiftX;
		    y -= this.currentShiftY;
		    //System.out.println(x+" "+y);
		    
		    TileEntityEnderBuffer buffer = GuiEnderBuffer.this.buffer;
		    if(x > 20 && x < 36){
		    	buffer.incrsRedstone("CU");
		    	return true;
		    }
		    if(x > 40 && x < 56){
		    	buffer.incrsRedstone("RF");
		    	return true;
		    }
		    if(x > 60 && x < 76){
		    	buffer.incrsRedstone("Fluid");
		    	return true;
		    }
		    if(x > 80 && x < 96){
		    	buffer.incrsRedstone("Inv");
		    	return true;
		    }
		    if ((x < 16) || (x >= 100) || (y < 20) || (y >= 84)) {
		    	return false;
			}
		    return true;
		}
		@Override
		public void update() {
			super.update();
			if (!isFullyOpened()) {
			    return;
			}
			int startX = Mouse.getX() - ((GuiEnderBuffer.this.width - GuiEnderBuffer.this.xSize) / 2) + 12;
			int x = (startX-this.currentShiftX-GuiEnderBuffer.this.xSize-12);
			//this.tooltip = x+", "+y;
			TileEntityEnderBuffer buffer = GuiEnderBuffer.this.buffer;
			if(x > 40 && x < 40+30){
				this.tooltip = "CU-Mode: "+Lang.localize("redstonemode."+buffer.cuRSMode.name().toLowerCase());
			}
			else if(x > 80 && x < 80+30){
				this.tooltip = "RF-Mode: "+Lang.localize("redstonemode."+buffer.rfRSMode.name().toLowerCase());
			}
			else if(x > 120 && x < 120+30){
				this.tooltip = "Fluid-Mode: "+Lang.localize("redstonemode."+buffer.fluidRSMode.name().toLowerCase());
			}
			else if(x > 160 && x < 160+30){
				this.tooltip = "Inventory-Mode: "+Lang.localize("redstonemode."+buffer.invRSMode.name().toLowerCase());
			}
			else{
				this.tooltip = "";
			}
		}
		
		@Override
		public void draw(int x, int y) {
			drawBackground(x,y);
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/icons.png"));
			int[][] icons = new int[RedstoneMode.values().length][4];
		    icons[RedstoneMode.NONE.ordinal()] = new int[]{0, 0, 16, 16};
		    icons[RedstoneMode.IGNORE.ordinal()] = new int[]{0, 16, 16, 16};
		    icons[RedstoneMode.OFF.ordinal()] = new int[]{0, 32, 16, 16};
		    icons[RedstoneMode.ON.ordinal()] = new int[]{0, 48, 16, 16};
		    int[] iconMain = icons[RedstoneMode.ON.ordinal()];
			GuiEnderBuffer.this.drawTexturedModalRect(x+3, y+1, iconMain[0], iconMain[1], iconMain[2], iconMain[3]);
			if (!isFullyOpened()) {
			     return;
			}
			TileEntityEnderBuffer buffer = GuiEnderBuffer.this.buffer;
			int[] cu = icons[buffer.cuRSMode.ordinal()];
			int[] rf = icons[buffer.rfRSMode.ordinal()];
			int[] fl = icons[buffer.fluidRSMode.ordinal()];
			int[] inv = icons[buffer.invRSMode.ordinal()];
			GuiEnderBuffer.this.drawTexturedModalRect(x + 20, y + 24, cu[0], cu[1], cu[2], cu[3]);
			GuiEnderBuffer.this.drawTexturedModalRect(x + 40, y + 24, rf[0], rf[1], rf[2], rf[3]);
			GuiEnderBuffer.this.drawTexturedModalRect(x + 60, y + 24, fl[0], fl[1], fl[2], fl[3]);
			GuiEnderBuffer.this.drawTexturedModalRect(x + 80, y + 24, inv[0], inv[1], inv[2], inv[3]);
		    
		    GuiEnderBuffer.this.fontRendererObj.drawStringWithShadow(("Redstone Config"), x + 20, y + 6, 0xe1c92f);
		    GL11.glColor4d(1, 1, 1, 1);
		}

		@Override
		public String getTooltip() {
			return tooltip;
		}
		
	}
	
}
