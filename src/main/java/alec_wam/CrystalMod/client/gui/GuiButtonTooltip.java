package alec_wam.CrystalMod.client.gui;

import java.util.List;

import alec_wam.CrystalMod.client.gui.GuiContainerBase.ITooltipProvider;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonTooltip extends GuiButton implements ITooltipProvider {
	
	private final List<String> tooltip;
	public GuiButtonTooltip(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, List<String> tooltip) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.tooltip = tooltip;
	}
	
	@Override
	public List<String> getInfo() {
		return tooltip;
	}

}
