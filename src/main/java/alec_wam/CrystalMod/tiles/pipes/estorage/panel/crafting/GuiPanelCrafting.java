package alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.IPanelSource;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.PanelSourceNormal;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;

public class GuiPanelCrafting extends GuiPanel {

	public TileEntityPanel panelTile;
	
	public GuiPanelCrafting(InventoryPlayer player, TileEntityPanel panel) {
		super(player, new PanelSourceNormal(panel), new ContainerPanelCrafting(player, panel));
		this.panelTile = panel;
		xSize = 232;
		ySize = 256;
	}
	
	public void refreshButtons(){
		super.refreshButtons();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		this.buttonList.add(new GuiButton(3, sx + 120, sy+111, 10, 10, "X"));
	}
	
	public void actionPerformed(GuiButton button){
		super.actionPerformed(button);
		if(button.id == 3){
			CrystalModNetwork.sendToServer(new PacketTileMessage(panelTile.getPos(), "ClearGrid"));
		}
	}
	
	public String getTexture() {
		return "crystalmod:textures/gui/eStorage_panel_crafting2.png";
	}
	
	public int getItemsPerRow(){
		return super.getItemsPerRow();
	}
	
	public int getMaxRenderCount(){
		return 40;
	}
	
	public int getSearchBarX(){
		return super.getSearchBarX();
	}
	
	public int getSearchBarY(){
		return super.getSearchBarY();
	}
	
	public int getSearchBarWidth() {
		return super.getSearchBarWidth();
	}
	
	public int getCraftBoxX(){
		return super.getCraftBoxX();
	}
	
	public int getCraftBoxY(){
		return 34;
	}
	
	public int getListX(){
		return super.getListX();
	}
	
	public int getListY(){
		return super.getListY();
	}
	
	public int getListWidth() {
		return super.getListWidth();
	}
	
	public int getListHeight() {
		return 72;
	}
}
