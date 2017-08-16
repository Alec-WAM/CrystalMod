package alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup;

import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import net.minecraft.item.ItemStack;

public class CraftingInfoPopup extends Popup {

	public CraftingInfoPopup(ItemStack stack, BasicCraftingTask task, int quantity) {
	}

	@Override
	public void update(GuiPanel guiPanel) {
	}

	@Override
	public boolean clicked(GuiPanel guiPanel, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

	@Override
	public boolean keyTyped(GuiPanel guiPanel, char typedChar, int keyCode) {
		return false;
	}

	@Override
	public void render(GuiPanel guiPanel) {
		
	}

}
