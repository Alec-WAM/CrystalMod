package alec_wam.CrystalMod.tiles.machine.worksite.gui;

import alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Label;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.NumberInput;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.Lang;

public class GuiWorksiteAnimalControl extends GuiContainerWorksiteBase {

	ContainerWorksiteAnimalControl container;
	WorksiteAnimalFarm worksite;

	NumberInput pigCount, sheepCount, cowCount, chickenCount;

	public GuiWorksiteAnimalControl(ContainerWorksite par1Container) {
		super(par1Container, 168, 48 + 16, defaultBackground);
		container = (ContainerWorksiteAnimalControl) par1Container;
		worksite = container.worksite;
	}

	@Override
	public void initElements() {
		Label label;

		label = new Label(8, 8, Lang.prefix+"guistrings.inventory.max_pigs");
		addGuiElement(label);
		pigCount = new NumberInput(130, 8, 30, this.container.maxPigs, this) {
			@Override
			public void onValueUpdated(float value) {
				container.maxPigs = (int) value;
			}
		};
		pigCount.setIntegerValue();
		addGuiElement(pigCount);

		label = new Label(8, 20, Lang.prefix+"guistrings.inventory.max_sheep");
		addGuiElement(label);
		sheepCount = new NumberInput(130, 20, 30, this.container.maxSheep, this) {
			@Override
			public void onValueUpdated(float value) {
				container.maxSheep = (int) value;
			}
		};
		sheepCount.setIntegerValue();
		addGuiElement(sheepCount);

		label = new Label(8, 32, Lang.prefix+"guistrings.inventory.max_cows");
		addGuiElement(label);
		cowCount = new NumberInput(130, 32, 30, this.container.maxCows, this) {
			@Override
			public void onValueUpdated(float value) {
				container.maxCows = (int) value;
			}
		};
		cowCount.setIntegerValue();
		addGuiElement(cowCount);

		label = new Label(8, 44, Lang.prefix+"guistrings.inventory.max_chickens");
		addGuiElement(label);
		chickenCount = new NumberInput(130, 44, 30, this.container.maxChickens,
				this) {
			@Override
			public void onValueUpdated(float value) {
				container.maxChickens = (int) value;
			}
		};
		chickenCount.setIntegerValue();
		addGuiElement(chickenCount);
	}

	@Override
	public void setupElements() {
		pigCount.setValue(container.maxPigs);
		sheepCount.setValue(container.maxSheep);
		cowCount.setValue(container.maxCows);
		chickenCount.setValue(container.maxChickens);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		container.sendSettingsToServer();
		BlockUtil.openWorksiteGui(player, 0,
				worksite.getPos().getX(), worksite.getPos().getY(), worksite.getPos().getZ());
		return false;
	}

}
