package alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.PanelSourceNormal;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPanelCrafting extends GuiPanel {

	public TileEntityPanel panelTile;
	
	public GuiPanelCrafting(InventoryPlayer player, TileEntityPanel panel) {
		super(player, new PanelSourceNormal(panel), new ContainerPanelCrafting(player, panel));
		this.panelTile = panel;
		xSize = 232;
		ySize = 256;
	}
	
	@Override
	public void refreshButtons(){
		super.refreshButtons();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		this.buttonList.add(new GuiButton(3, sx + 120, sy+111, 10, 10, "X"));
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		super.actionPerformed(button);
		if(button.id == 3){
			CrystalModNetwork.sendToServer(new PacketTileMessage(panelTile.getPos(), "ClearGrid"));
		}
	}
	
	@Override
	public String getTexture() {
		return "crystalmod:textures/gui/eStorage_panel_crafting2.png";
	}
	
	@Override
	public int getItemsPerRow(){
		return super.getItemsPerRow();
	}
	
	@Override
	public int getMaxRenderCount(){
		return 40;
	}
	
	@Override
	public int getSearchBarX(){
		return super.getSearchBarX();
	}
	
	@Override
	public int getSearchBarY(){
		return super.getSearchBarY();
	}
	
	@Override
	public int getSearchBarWidth() {
		return super.getSearchBarWidth();
	}
	
	@Override
	public int getCraftBoxX(){
		return super.getCraftBoxX();
	}
	
	@Override
	public int getCraftBoxY(){
		return 34;
	}
	
	@Override
	public int getListX(){
		return super.getListX();
	}
	
	@Override
	public int getListY(){
		return super.getListY();
	}
	
	@Override
	public int getListWidth() {
		return super.getListWidth();
	}
	
	@Override
	public int getListHeight() {
		return 72;
	}
}
