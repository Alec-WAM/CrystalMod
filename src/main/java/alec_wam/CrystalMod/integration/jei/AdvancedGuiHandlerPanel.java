package alec_wam.CrystalMod.integration.jei;

import java.awt.Rectangle;
import java.util.List;

import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import mezz.jei.api.gui.IAdvancedGuiHandler;

public class AdvancedGuiHandlerPanel implements IAdvancedGuiHandler<GuiPanel> {

	@Override
	public Class<GuiPanel> getGuiContainerClass() {
		return GuiPanel.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiPanel arg0) {
		return null;
	}

	@Override
	public Object getIngredientUnderMouse(GuiPanel panel, int mouseX, int mouseY) {
		ItemStackData data = panel.getDataUnderMouse(mouseX, mouseY);
		if(data !=null && !panel.searchBar.isFocused()){
			return data.stack;
		}
		return null;
	}

}
