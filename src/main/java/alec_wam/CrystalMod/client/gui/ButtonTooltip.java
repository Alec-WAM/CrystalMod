package alec_wam.CrystalMod.client.gui;

import java.util.List;

import alec_wam.CrystalMod.client.gui.GuiContainerBase.ITooltipProvider;
import net.minecraft.client.gui.widget.button.Button;

public class ButtonTooltip extends Button implements ITooltipProvider {
	
	private final List<String> tooltip;
	public ButtonTooltip(int x, int y, int widthIn, int heightIn, String buttonText, List<String> tooltip, Button.IPressable p_i51141_6_) {
		super(x, y, widthIn, heightIn, buttonText, p_i51141_6_);
		this.tooltip = tooltip;
	}
	
	@Override
	public List<String> getInfo() {
		return tooltip;
	}

}
