package alec_wam.CrystalMod.client.gui.tabs;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2i;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.gui.tabs.GuiContainerTabbed.Tab;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityIOSides;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class IOTab extends Tab {

	TileEntityIOSides tile;
	int headerColour = 0xe1c92f;
	public IOTab(TileEntityIOSides tile) {
		this.tile = tile;
		maxHeight = 94;
		maxWidth = 100;
		overlayColor = 0xd46c1f;
	}
	
	@Override
	public boolean handleMouseClicked(double x, double y, int mouseButton)
	{
		if (!isFullyOpened()) {
			return false;
		}
		x -= this.currentShiftX;
		y -= this.currentShiftY;
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
			IOType type = tile.getIO(fixedDir);
			IOType newType = type.getNext();

			if(newType !=null){
				tile.setIO(fixedDir, newType);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("IOType", newType.getName());
				CrystalModNetwork.sendToServer(new PacketTileMessage(tile.getPos(), "IO."+fixedDir.name().toUpperCase(), nbt));
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
			Gui.drawRect(ioX, ioY, ioX+16, ioY+16, back);
			Gui.drawRect(ioX+2, ioY+2, ioX+6, ioY+14, Color.CYAN.darker().getRGB());
			Gui.drawRect(ioX+6, ioY+2, ioX+10, ioY+14, Color.RED.getRGB());
			Gui.drawRect(ioX+10, ioY+2, ioX+14, ioY+14, Color.GRAY.getRGB());
			GlStateManager.popMatrix();
		}

		if (!isFullyOpened()) {
			return;
		}
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(("Configuration"), x + 25, y + 8, this.headerColour);
		Map<EnumFacing, Point2i> pos = Maps.newHashMap();
		pos.put(EnumFacing.UP, new Point2i(40, 24));
		pos.put(EnumFacing.EAST, new Point2i(20, 44));
		pos.put(EnumFacing.NORTH, new Point2i(40, 44));
		pos.put(EnumFacing.WEST, new Point2i(60, 44));
		pos.put(EnumFacing.DOWN, new Point2i(40, 64));
		pos.put(EnumFacing.SOUTH, new Point2i(60, 64));
		GlStateManager.pushMatrix();
		for(EnumFacing face : pos.keySet()){
			IOType io = tile.getIO(face);
			Point2i point = pos.get(face);
			int ioX = x+point.x;
			int ioY = y+point.y;
			int back = Color.BLACK.getRGB();
			Gui.drawRect(ioX, ioY, ioX+20, ioY+20, back);
			Color color = Color.GRAY;
			if(io == IOType.IN){
				color = Color.CYAN.darker();
			}
			if(io == IOType.OUT){
				color = Color.RED;
			}
			Gui.drawRect(ioX+2, ioY+2, ioX+18, ioY+18, color.getRGB());
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
				letter = tile.fixFace(face).getName().substring(0, 1).toUpperCase();
			}
			GlStateManager.translated(0.5, 0.5, 0);
			Minecraft.getInstance().fontRenderer.drawString((letter), ioX+7, ioY+6, 0);
			GlStateManager.translated(-0.5, -0.5, 0);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public List<String> getTooltip(int mouseX, int mouseY) {
		if (isFullyOpened()) {
			return Lists.newArrayList();
		}
		return Lists.newArrayList("Configure Sides");
	}
}
