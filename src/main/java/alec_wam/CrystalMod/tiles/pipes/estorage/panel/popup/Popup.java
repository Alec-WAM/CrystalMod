package alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup;

import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;

public abstract class Popup {

	public abstract void update(GuiPanel guiPanel);

	public abstract boolean clicked(GuiPanel guiPanel, int mouseX, int mouseY, int mouseButton);

	public abstract boolean keyTyped(GuiPanel guiPanel, char typedChar, int keyCode);
	
	public abstract void render(GuiPanel guiPanel);
}
