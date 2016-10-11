package alec_wam.CrystalMod.tiles.machine.worksite.imp;

import alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksite;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.GuiWorksiteBase;

public class GuiWorksiteTreeFarm extends GuiWorksiteBase {

	public GuiWorksiteTreeFarm(ContainerWorksite par1Container) {
		super(par1Container);
	}

	@Override
	public void initElements() {
		addLabels();
		addSideSelectButton();
		addBoundsAdjustButton();
	}

	@Override
	public void setupElements() {

	}

}
