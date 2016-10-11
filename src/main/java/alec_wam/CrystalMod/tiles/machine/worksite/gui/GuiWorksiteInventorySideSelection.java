package alec_wam.CrystalMod.tiles.machine.worksite.gui;

import java.util.EnumSet;

import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RotationType;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Button;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Label;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class GuiWorksiteInventorySideSelection extends GuiContainerWorksiteBase {

	ContainerWorksiteInventorySideSelection container;

	public GuiWorksiteInventorySideSelection(ContainerWorksiteBase par1Container) {
		super(par1Container, 128 + 55 + 8, 106, defaultBackground);
		container = (ContainerWorksiteInventorySideSelection) par1Container;
	}

	@Override
	public void initElements() {

	}

	@Override
	protected boolean onGuiCloseRequested() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("closeGUI", true);
		sendDataToContainer(tag);
		return false;
	}

	@Override
	public void setupElements() {
		this.clearElements();

		Label label;
		SideButton sideButton;
		RelativeSide accessed;
		int dir;

		label = new Label(8, 6, Lang.prefix+"guistrings.block_side");
		addGuiElement(label);
		label = new Label(74, 6, Lang.prefix+"guistrings.direction");
		addGuiElement(label);
		label = new Label(128, 6, Lang.prefix+"guistrings.inventory_accessed");
		addGuiElement(label);

		int height = 18;
		for (RelativeSide side : RotationType.FOUR_WAY.getValidSides()) {
			label = new Label(8, height, Lang.prefix+side.getTranslationKey());
			addGuiElement(label);

			dir = RelativeSide.getMCSideToAccess(RotationType.FOUR_WAY,
					container.worksite.getPrimaryFacing().ordinal(), side);
			label = new Label(74, height, Lang.prefix+"guistrings.inventory.direction."+EnumFacing.getFront(dir).name().toLowerCase());
			addGuiElement(label);

			accessed = container.sideMap.get(side);
			sideButton = new SideButton(128, height, side, accessed);
			addGuiElement(sideButton);

			height += 14;
		}
	}

	private class SideButton extends Button {
		RelativeSide side;// base side
		RelativeSide selection;// accessed side

		public SideButton(int topLeftX, int topLeftY, RelativeSide side,
				RelativeSide selection) {
			super(topLeftX, topLeftY, 55, 12, Lang.prefix+selection.getTranslationKey());
			if (side == null) {
				throw new IllegalArgumentException(
						"access side may not be null..");
			}
			this.side = side;
			this.selection = selection;
		}

		@Override
		protected void onPressed() {
			int ordinal = selection.ordinal();
			RelativeSide next;
			EnumSet<RelativeSide> validSides = container.worksite.inventory
					.getValidSides();
			for (int i = 0; i < RelativeSide.values().length; i++) {
				ordinal++;
				if (ordinal >= RelativeSide.values().length) {
					ordinal = 0;
				}
				next = RelativeSide.values()[ordinal];
				if (validSides.contains(next)) {
					selection = next;
					break;
				}
			}
			container.sideMap.put(side, selection);
			setText(Lang.prefix+selection.getTranslationKey());
			container.sendSlotChange(side, selection);
		}

	}

}
