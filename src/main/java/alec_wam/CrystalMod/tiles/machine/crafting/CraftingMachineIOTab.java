package alec_wam.CrystalMod.tiles.machine.crafting;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2i;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.client.gui.tabs.GuiContainerTabbed.Tab;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachineIO.ItemIOType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class CraftingMachineIOTab extends Tab {

	TileEntityCraftingMachine tile;
	int headerColour = 0xe1c92f;
	public CraftingMachineIOTab(TileEntityCraftingMachine tile) {
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
			handleModeChange(Direction.UP, mouseButton);
		} else if ((20 <= x) && (x < 36) && (44 <= y) && (y < 60)) {
			handleModeChange(Direction.EAST, mouseButton);
		} else if ((40 <= x) && (x < 56) && (44 <= y) && (y < 60)) {
			handleModeChange(Direction.NORTH, mouseButton);
		} else if ((60 <= x) && (x < 76) && (44 <= y) && (y < 60)) {
			handleModeChange(Direction.WEST, mouseButton);
		} else if ((40 <= x) && (x < 56) && (64 <= y) && (y < 80)) {
			handleModeChange(Direction.DOWN, mouseButton);
		} else if ((60 <= x) && (x < 76) && (64 <= y) && (y < 80)) {
			handleModeChange(Direction.SOUTH, mouseButton);
		}
		return true;
	}
	
	void handleModeChange(Direction side, int mouseButton)
	{
		if (mouseButton == 0) {
			Direction fixedDir = side;
			ItemIOType type = tile.getIO(fixedDir);
			ItemIOType newType = type.getNext();

			if(newType !=null){
				tile.setIO(fixedDir, newType);
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString("IOType", newType.getName());
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
			Screen.fill(ioX, ioY, ioX+16, ioY+16, back);
			Screen.fill(ioX+2, ioY+2, ioX+6, ioY+14, Color.CYAN.darker().getRGB());
			Screen.fill(ioX+6, ioY+2, ioX+10, ioY+14, Color.RED.getRGB());
			Screen.fill(ioX+10, ioY+2, ioX+14, ioY+14, Color.GRAY.getRGB());
			GlStateManager.popMatrix();
		}

		if (!isFullyOpened()) {
			return;
		}
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(("Configuration"), x + 25, y + 8, this.headerColour);
		Map<Direction, Point2i> pos = Maps.newHashMap();
		pos.put(Direction.UP, new Point2i(40, 24));
		pos.put(Direction.EAST, new Point2i(20, 44));
		pos.put(Direction.NORTH, new Point2i(40, 44));
		pos.put(Direction.WEST, new Point2i(60, 44));
		pos.put(Direction.DOWN, new Point2i(40, 64));
		pos.put(Direction.SOUTH, new Point2i(60, 64));
		GlStateManager.pushMatrix();
		for(Direction face : pos.keySet()){
			ItemIOType io = tile.getIO(face);
			Point2i point = pos.get(face);
			int ioX = x+point.x;
			int ioY = y+point.y;
			int back = Color.BLACK.getRGB();
			Screen.fill(ioX, ioY, ioX+20, ioY+20, back);
			if(io != ItemIOType.BOTH){
				Color color = Color.GRAY.darker().darker();
				if(io == ItemIOType.NOTHING){
					color = Color.GRAY;
				}
				if(io == ItemIOType.IN){
					color = Color.CYAN.darker();
				}
				if(io == ItemIOType.OUT){
					color = Color.RED;
				}
				Screen.fill(ioX+2, ioY+2, ioX+18, ioY+18, color.getRGB());
			} else {
				Screen.fill(ioX+2, ioY+2, ioX+10, ioY+18, Color.CYAN.darker().getRGB());
				Screen.fill(ioX+10, ioY+2, ioX+18, ioY+18, Color.RED.getRGB());
			}
			String letter = "";
			if(!Screen.hasShiftDown()){
				if(face == Direction.UP){
					letter = "U";
				}
				if(face == Direction.DOWN){
					letter = "D";
				}
				if(face == Direction.EAST){
					letter = "L";
				}
				if(face == Direction.NORTH){
					letter = "F";
				}
				if(face == Direction.WEST){
					letter = "R";
				}
				if(face == Direction.SOUTH){
					letter = "B";
				}
			}else {
				letter = tile.fixDirection(face).getName().substring(0, 1).toUpperCase();
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
		return Lists.newArrayList("Configure IO");
	}
}
