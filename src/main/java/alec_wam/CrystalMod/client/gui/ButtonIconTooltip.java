package alec_wam.CrystalMod.client.gui;

import java.util.Arrays;
import java.util.List;

import alec_wam.CrystalMod.client.gui.GuiContainerBase.ITooltipProvider;
import net.minecraft.client.gui.widget.button.Button;

public class ButtonIconTooltip extends ButtonIcon implements ITooltipProvider {

	private final List<String> tooltip;
	
	public ButtonIconTooltip(int x, int y, int iconX, int iconY, String tooltip, Button.IPressable p_i51141_6_) {
		super(x, y, iconX, iconY, p_i51141_6_);
		this.tooltip = Arrays.asList(tooltip);
	}

	@Override
	public List<String> getInfo() {
		return tooltip;
	}

}
