package com.alec_wam.CrystalMod.tiles.machine.worksite.imp;

import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksite;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.GuiWorksiteBase;

public class GuiWorksiteCropFarm extends GuiWorksiteBase {

	public GuiWorksiteCropFarm(ContainerWorksite par1Container) {
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
