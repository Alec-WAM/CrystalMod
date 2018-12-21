package alec_wam.CrystalMod.tiles.machine.worksite.imp;

import java.util.EnumSet;

import com.google.common.collect.Sets;

import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksite;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.GuiWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Upgrades;

public class GuiWorksiteAnimalFarm extends GuiWorksiteBase {

	public GuiWorksiteAnimalFarm(ContainerWorksite par1Container) {
		super(par1Container);
	}

	@Override
	public void initElements() {
		addLabels();
		addSideSelectButton();
		addBoundsAdjustButton();
		addAltControlsButton();
		Upgrades upgrade = new Upgrades(80, container.bottomLabel + 12, worksite.getUpgrades());
		this.addGuiElement(upgrade);
	}

	@Override
	public void setupElements() {

	}

}
