package alec_wam.CrystalMod.tiles.machine.enderbuffer.gui;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.util.GuiContainerTabbed;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBufferClientData;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer.Mode;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

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
            if(buffer.isBoundToPlayer()){
            	back = Color.YELLOW.darker().getRGB();
            }
            
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
			EnderBuffer ebuffer = null;
			World world = CrystalMod.proxy.getClientWorld();
			String displayName = ModBlocks.enderBuffer.getLocalizedName(); 
			if(buffer.isBoundToPlayer()){
				UUID uuid = buffer.getPlayerBound();
				ebuffer = EnderBufferManager.get(world).getPrivate(uuid).getBuffer(buffer.code);
				displayName = ProfileUtil.getUsername(uuid);		
			} else {
				ebuffer = EnderBufferManager.get(world).getBuffer(buffer.code);
			}			
			int width = this.fontRendererObj.getStringWidth(displayName);
			this.fontRendererObj.drawString(displayName, 164 - (width / 2), 20, 0);	
			
			EnderBufferClientData data = ebuffer.clientData;
			
			int cu = 0; 
			int rf = 0; 
			FluidStack tankFluid = null;
			if(data !=null){
				cu = data.cu; 
				rf = data.rf; 
				tankFluid = data.fluid;
			}
			
			int maxCU = ebuffer.cuStorage.getMaxCEnergyStored();
			int maxRF = ebuffer.rfStorage.getMaxEnergyStored();
			final int barHeight = 58;
			
			RenderUtil.renderPowerBar(cuX, 16, (int)zLevel, 10, barHeight, cu, maxCU, Color.CYAN.getRGB(), Color.CYAN.darker().getRGB());
			RenderUtil.renderPowerBar(rfX, 16, (int)zLevel, 10, barHeight, rf, maxRF, Color.RED.getRGB(), Color.RED.darker().getRGB());
			if(tankFluid !=null)RenderUtil.renderGuiTank(tankFluid, ebuffer.tank.getCapacity(), data.fluid.amount, flX, 16, zLevel, 10, 58, false);

			int xAxis = par1 - guiLeft;
			int yAxis = par2 - guiTop;
			if(xAxis > cuX-3 && xAxis < (cuX-3)+12 && yAxis >= 15 && yAxis <= 62+12)
			{
				List<String> lines = Lists.newArrayList();
				lines.add("CU");
				lines.add(cu > 0 ? cu +" / "+ maxCU +"CU": Lang.localize("gui.empty"));
				drawHoveringText(lines, xAxis, yAxis);
				RenderHelper.enableGUIStandardItemLighting();
			}
			if(xAxis > rfX-3 && xAxis < (rfX-3)+12 && yAxis >= 15 && yAxis <= 62+12)
			{
				List<String> lines = Lists.newArrayList();
				lines.add("RF");
				lines.add(rf > 0 ? rf +" / "+ maxRF +"RF": Lang.localize("gui.empty"));
				drawHoveringText(lines, xAxis, yAxis);
				RenderHelper.enableGUIStandardItemLighting();
			}
			
			if(xAxis > flX-3 && xAxis < (flX-3)+12 && yAxis >= 15 && yAxis <= 62+12)
			{
				List<String> lines = Lists.newArrayList();
				lines.add("Tank");
				lines.add(tankFluid !=null ? tankFluid.getLocalizedName()+": "+ tankFluid.amount+" / "+ebuffer.tank.getCapacity()+"Mb": Lang.localize("gui.empty"));
				drawHoveringText(lines, xAxis, yAxis);
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
		tabManager.add(new RedstoneLedger(this));
		tabManager.add(new ConfigLedger(this));
	}

	private class ConfigLedger extends Tab {

		public GuiEnderBuffer masterGui;
		public ConfigLedger(GuiEnderBuffer masterGui){
			this.masterGui = masterGui;
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
		    	masterGui.buffer.incrsCUMode();
		    	return true;
		    }
		    if(x > 40 && x < 56){
		    	masterGui.buffer.incrsRFMode();
		    	return true;
		    }
		    if(x > 60 && x < 76){
		    	masterGui.buffer.incrsFluidMode();
		    	return true;
		    }
		    if(x > 80 && x < 96){
		    	masterGui.buffer.incrsInvMode();
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
		    masterGui.drawTexturedModalRect(x + 4, y + 4, both[0], both[1], both[2], both[3]);
		    if (!isFullyOpened()) {
			     return;
			}
		    TileEntityEnderBuffer buffer = masterGui.buffer;
		    int[] cu = icons[buffer.cuMode.ordinal()];
		    int[] rf = icons[buffer.rfMode.ordinal()];
		    int[] fl = icons[buffer.fluidMode.ordinal()];
		    int[] inv = icons[buffer.invMode.ordinal()];
		    masterGui.drawTexturedModalRect(x + 20, y + 24, cu[0], cu[1], cu[2], cu[3]);
		    masterGui.drawTexturedModalRect(x + 40, y + 24, rf[0], rf[1], rf[2], rf[3]);
		    masterGui.drawTexturedModalRect(x + 60, y + 24, fl[0], fl[1], fl[2], fl[3]);
		    masterGui.drawTexturedModalRect(x + 80, y + 24, inv[0], inv[1], inv[2], inv[3]);
		    masterGui.fontRendererObj.drawStringWithShadow(("Configuration"), x + 23, y + 6, 0xe1c92f);
		    GL11.glColor4d(1, 1, 1, 1);
		}

		@Override
		public List<String> getTooltip(int mouseX, int mouseY) {
			if (!isFullyOpened()) {
			      return Lists.newArrayList();
			}
			List<String> list = Lists.newArrayList();
			int x = mouseX - this.currentShiftX;
			if(x > 20 && x < 20+16){
				list.add("CU");
				list.add(masterGui.buffer.cuMode.name());
			}
			else if(x > 40 && x < 40+16){
				list.add("RF");
				list.add(masterGui.buffer.rfMode.name());
			}
			else if(x > 60 && x < 60+16){
				list.add("Fluid");
				list.add(masterGui.buffer.fluidMode.name());
			}
			else if(x > 80 && x < 80+16){
				list.add("Inventory");
				list.add(masterGui.buffer.invMode.name());
			}
			return list;
		}
		
	}
	
	private class RedstoneLedger extends Tab {

		public GuiEnderBuffer masterGui;
		public RedstoneLedger(GuiEnderBuffer masterGui){
			this.masterGui = masterGui;
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
		    
		    TileEntityEnderBuffer buffer = masterGui.buffer;
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
			masterGui.drawTexturedModalRect(x+3, y+1, iconMain[0], iconMain[1], iconMain[2], iconMain[3]);
			if (!isFullyOpened()) {
			     return;
			}
			TileEntityEnderBuffer buffer = masterGui.buffer;
			int[] cu = icons[buffer.cuRSMode.ordinal()];
			int[] rf = icons[buffer.rfRSMode.ordinal()];
			int[] fl = icons[buffer.fluidRSMode.ordinal()];
			int[] inv = icons[buffer.invRSMode.ordinal()];
			masterGui.drawTexturedModalRect(x + 20, y + 24, cu[0], cu[1], cu[2], cu[3]);
			masterGui.drawTexturedModalRect(x + 40, y + 24, rf[0], rf[1], rf[2], rf[3]);
			masterGui.drawTexturedModalRect(x + 60, y + 24, fl[0], fl[1], fl[2], fl[3]);
			masterGui.drawTexturedModalRect(x + 80, y + 24, inv[0], inv[1], inv[2], inv[3]);
		    
		    masterGui.fontRendererObj.drawStringWithShadow(("Redstone Config"), x + 20, y + 6, 0xe1c92f);
		    GL11.glColor4d(1, 1, 1, 1);
		}

		@Override
		public List<String> getTooltip(int mouseX, int mouseY) {
			if (!isFullyOpened()) {
			      return Lists.newArrayList();
			}
			List<String> list = Lists.newArrayList();
			int x = mouseX - this.currentShiftX;
			TileEntityEnderBuffer buffer = masterGui.buffer;
			if(x > 20 && x < 20+16){
				list.add("CU");
				list.add(Lang.localize("gui.redstone."+buffer.cuRSMode.name().toLowerCase()));
			}
			else if(x > 40 && x < 40+16){
				list.add("RF");
				list.add(Lang.localize("gui.redstone."+buffer.rfRSMode.name().toLowerCase()));
			}
			else if(x > 60 && x < 60+16){
				list.add("Fluid");
				list.add(Lang.localize("gui.redstone."+buffer.fluidRSMode.name().toLowerCase()));
			}
			else if(x > 80 && x < 80+16){
				list.add("Inventory");
				list.add(Lang.localize("gui.redstone."+buffer.invRSMode.name().toLowerCase()));
			}
			return list;
		}
		
	}
	
}
