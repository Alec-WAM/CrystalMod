package alec_wam.CrystalMod.client.gui;

import java.util.Arrays;
import java.util.List;

import alec_wam.CrystalMod.client.gui.GuiContainerBase.ITooltipProvider;

public class GuiButtonIconTooltip extends GuiButtonIcon implements ITooltipProvider {

	private final List<String> tooltip;
	
	public GuiButtonIconTooltip(int buttonId, int x, int y, int iconX, int iconY, String tooltip) {
		super(buttonId, x, y, iconX, iconY);
		this.tooltip = Arrays.asList(tooltip);
	}

	@Override
	public List<String> getInfo() {
		return tooltip;
	}

}
