package alec_wam.CrystalMod.tiles.machine.worksite.gui;

import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Button;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Label;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.Lang;

public abstract class GuiWorksiteBase extends GuiContainerWorksiteBase {

	TileWorksiteBoundedInventory worksite;
	ContainerWorksite container;

	public GuiWorksiteBase(ContainerWorksite par1Container) {
		super(par1Container, 178, 240, defaultBackground);
		worksite = ((ContainerWorksite) inventorySlots).worksite;
		this.container = par1Container;
		this.ySize = container.guiHeight + 12;
	}

	protected void addLabels() {
		Label label;
		if (container.topLabel > 0) {
			label = new Label(8, container.topLabel,
					Lang.prefix+"guistrings.inventory.side.top");
			addGuiElement(label);
		}
		if (container.frontLabel > 0) {
			label = new Label(8, container.frontLabel,
					Lang.prefix+"guistrings.inventory.side.front");
			addGuiElement(label);
		}
		if (container.bottomLabel > 0) {
			label = new Label(8, container.bottomLabel,
					Lang.prefix+"guistrings.inventory.side.bottom");
			addGuiElement(label);
		}
		if (container.rearLabel > 0) {
			label = new Label(8, container.rearLabel,
					Lang.prefix+"guistrings.inventory.side.rear");
			addGuiElement(label);
		}
		if (container.leftLabel > 0) {
			label = new Label(8, container.leftLabel,
					Lang.prefix+"guistrings.inventory.side.left");
			addGuiElement(label);
		}
		if (container.rightLabel > 0) {
			label = new Label(8, container.rightLabel,
					Lang.prefix+"guistrings.inventory.side.right");
			addGuiElement(label);
		}
		if (container.playerLabel > 0) {
			label = new Label(8, container.playerLabel,
					Lang.prefix+"guistrings.inventory.player");
			addGuiElement(label);
		}
	}

	protected void addSideSelectButton() {
		Button button = new Button(8, ySize - 8 - 12, 50, 12, Lang.prefix+"guistrings.inventory.setsides") {
			@Override
			protected void onPressed() {
				BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_WORK_CONFIG, container.worksite.getPos().getX(), container.worksite.getPos().getY(), container.worksite.getPos().getZ());
			}
		};
		addGuiElement(button);
	}

	protected void addBoundsAdjustButton() {
		Button button = new Button(58, ySize - 8 - 12, 50, 12,
				Lang.prefix+"guistrings.inventory.adjust_bounds") {
			@Override
			protected void onPressed() {
				BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_WORK_BOUNDS, container.worksite.getPos().getX(), container.worksite.getPos().getY(),
						container.worksite.getPos().getZ());
			}
		};
		addGuiElement(button);
	}

	protected void addAltControlsButton() {
		Button button = new Button(108, ySize - 8 - 12, 50, 12,
				Lang.prefix+"guistrings.inventory.alt_controls") {
			@Override
			protected void onPressed() {
				worksite.openAltGui(player);
			}
		};
		addGuiElement(button);
	}

}
