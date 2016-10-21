package alec_wam.CrystalMod.tiles.machine.power.battery;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point2i;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.util.GuiContainerTabbed;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class GuiBattery extends GuiContainerTabbed {

	public TileEntityBattery battery;
	
	public GuiBattery(EntityPlayer player, TileEntityBattery bat) {
		super(new ContainerBattery(player, bat), null);
		battery = bat;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int cu = battery.energyStorage.getCEnergyStored(); int maxCU = battery.energyStorage.getMaxCEnergyStored();
		
		BatteryType type = BlockBattery.fromMeta(battery.getBlockMetadata());
		
		if(type == BatteryType.CREATIVE){
			cu = maxCU = 1;
		}
		
		RenderUtil.renderPowerBar(80, 20, 0, 16, 45, cu, maxCU, Color.CYAN.getRGB(), Color.CYAN.darker().getRGB());
		
		if(type != BatteryType.CREATIVE){
			String in = "In: "+battery.energyStorage.getMaxReceive();
			String out = "Out: "+battery.energyStorage.getMaxExtract();
			drawString(fontRendererObj, in, (xSize/4)-(fontRendererObj.getStringWidth(in)/2), 60, Color.GRAY.getRGB());
			drawString(fontRendererObj, out, xSize-(xSize/4)-(fontRendererObj.getStringWidth(out)/2), 60, Color.GRAY.getRGB());
		}
		
		int xAxis = (par1 - (width - xSize) / 2);
		int yAxis = (par2 - (height - ySize) / 2);
		if(xAxis >= 80 && xAxis < 80+16 && yAxis >= 20 && yAxis < 20+45)
		{
			List<String> lines = Lists.newArrayList();
			if(type == BatteryType.CREATIVE){
				lines.add("Infinite "+Lang.localize("power.cu"));
			} else lines.add(cu > 0 ? cu +" / "+ maxCU + " "+Lang.localize("power.cu"): Lang.localize("gui.empty"));
			drawHoveringText(lines, xAxis, yAxis);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}
	
	public void drawGuiContainerBackgroundLayer(float var1, int var2,int var3) {
		BatteryType type = BlockBattery.fromMeta(battery.getBlockMetadata());
		if(type == BatteryType.BLUE){
			GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
		}
		if(type == BatteryType.RED){
			GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
		}
		if(type == BatteryType.GREEN){
			GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);
		}
		if(type == BatteryType.DARK){
			GlStateManager.color(0.2F, 0.2F, 0.2F, 1F);
		}
		if(type == BatteryType.PURE){
			GlStateManager.color(10.0F, 10.0F, 10.0F, 1F);
		}
		if(type == BatteryType.CREATIVE){
			GlStateManager.color(1.0F, 0.5F, 0.0F, 1F);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(CrystalMod.resource("textures/gui/machine/battery.png")));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
	}
	
	@Override
	protected void initTabs() {
		super.initTabs();
		tabManager.add(new IOTab(this, this.battery));
	}
	
	private class IOTab extends Tab {

		int headerColour = 0xe1c92f;
		TileEntityBattery bat;
		GuiBattery gui;
		public IOTab(GuiBattery gui, TileEntityBattery bat) {
			maxHeight = 94;
			maxWidth = 100;
			overlayColor = 0xd46c1f;
			this.bat = bat;
			this.gui = gui;
		}
		public boolean handleMouseClicked(int x, int y, int mouseButton)
		  {
		    if (!isFullyOpened()) {
		      return false;
		    }
		    x -= this.currentShiftX;
		    y -= this.currentShiftY;
		    //System.out.println(x+" "+y);
		    if ((x < 16) || (x >= 80) || (y < 20) || (y >= 84)) {
		      return false;
		    }
		      if ((40 <= x) && (x < 56) && (24 <= y) && (y < 40)) {
		    	  handleModeChange(EnumFacing.UP, mouseButton);
		      } else if ((20 <= x) && (x < 36) && (44 <= y) && (y < 60)) {
		    	  handleModeChange(EnumFacing.EAST, mouseButton);
		      } else if ((40 <= x) && (x < 56) && (44 <= y) && (y < 60)) {
		    	  handleModeChange(EnumFacing.NORTH, mouseButton);
		      } else if ((60 <= x) && (x < 76) && (44 <= y) && (y < 60)) {
		    	  handleModeChange(EnumFacing.WEST, mouseButton);
		      } else if ((40 <= x) && (x < 56) && (64 <= y) && (y < 80)) {
		    	  handleModeChange(EnumFacing.DOWN, mouseButton);
		      } else if ((60 <= x) && (x < 76) && (64 <= y) && (y < 80)) {
		    	  handleModeChange(EnumFacing.SOUTH, mouseButton);
		      }
		    return true;
		  }
		void handleModeChange(EnumFacing side, int mouseButton)
		  {
			if (mouseButton == 0) {
		    	EnumFacing fixedDir = side;
				IOType type = battery.getIO(fixedDir);
				IOType newType = type.getNext();
				
				if(newType !=null){
    				battery.setIO(fixedDir, newType);
    				NBTTagCompound nbt = new NBTTagCompound();
    				nbt.setString("IOType", newType.getName());
    				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "IO."+fixedDir.name().toUpperCase(), nbt));
				}
		    }
		  }
		@Override
		public void draw(int x, int y) {
			drawBackground(x,y);
			boolean drawIcon = true;
			if(drawIcon){
				GlStateManager.pushMatrix();
				int ioX = x+3;
			    int ioY = y+4;
			    int back = Color.BLACK.getRGB();
	            drawRect(ioX, ioY, ioX+16, ioY+16, back);
	            drawRect(ioX+2, ioY+2, ioX+6, ioY+14, Color.CYAN.darker().getRGB());
	            drawRect(ioX+6, ioY+2, ioX+10, ioY+14, Color.RED.getRGB());
	            drawRect(ioX+10, ioY+2, ioX+14, ioY+14, Color.GRAY.getRGB());
	            GlStateManager.popMatrix();
			}
			
		    if (!isFullyOpened()) {
		      return;
		    }
		    gui.fontRendererObj.drawStringWithShadow(("Configuration"), x + 25, y + 8, this.headerColour);
		    Map<EnumFacing, Point2i> pos = Maps.newHashMap();
		    pos.put(EnumFacing.UP, new Point2i(40, 24));
		    pos.put(EnumFacing.EAST, new Point2i(20, 44));
		    pos.put(EnumFacing.NORTH, new Point2i(40, 44));
		    pos.put(EnumFacing.WEST, new Point2i(60, 44));
		    pos.put(EnumFacing.DOWN, new Point2i(40, 64));
		    pos.put(EnumFacing.SOUTH, new Point2i(60, 64));
		    GlStateManager.pushMatrix();
		    for(EnumFacing face : pos.keySet()){
			    IOType io = battery.getIO(face);
			    Point2i point = pos.get(face);
			    int ioX = x+point.x;
			    int ioY = y+point.y;
			    int back = Color.BLACK.getRGB();
	            drawRect(ioX, ioY, ioX+20, ioY+20, back);
	            Color color = Color.GRAY;
	            if(io == IOType.IN){
	            	color = Color.CYAN.darker();
	            }
	            if(io == IOType.OUT){
	            	color = Color.RED;
	            }
	            drawRect(ioX+2, ioY+2, ioX+18, ioY+18, color.getRGB());
	            String letter = "";
		        if(!GuiScreen.isShiftKeyDown()){
	            	if(face == EnumFacing.UP){
		            	letter = "U";
		            }
		            if(face == EnumFacing.DOWN){
		            	letter = "D";
		            }
		            if(face == EnumFacing.EAST){
		            	letter = "L";
		            }
		            if(face == EnumFacing.NORTH){
		            	letter = "F";
		            }
		            if(face == EnumFacing.WEST){
		            	letter = "R";
		            }
		            if(face == EnumFacing.SOUTH){
		            	letter = "B";
		            }
	            }else {
	            	letter = battery.fixFace(face).getName().substring(0, 1).toUpperCase();
	            }
	            GlStateManager.translate(0.5, 0.5, 0);
	            gui.fontRendererObj.drawString((letter), ioX+7, ioY+6, 0);
	            GlStateManager.translate(-0.5, -0.5, 0);
		    }
		    GlStateManager.popMatrix();
		}

		@Override
		public String getTooltip() {
			if (isFullyOpened()) {
			      return "";
			}
			return String.format("Configure Sides");
		}
	}

}
