package alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup;

import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.ICraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;

public class CraftingInfoPopup extends Popup {

	private ItemStack requested;
	private BasicCraftingTask task;
	private int quantity;
	
	public CraftingInfoPopup(ItemStack stack, BasicCraftingTask task, int quantity) {
		this.requested = stack;
		this.task = task;
		this.quantity = quantity;
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
