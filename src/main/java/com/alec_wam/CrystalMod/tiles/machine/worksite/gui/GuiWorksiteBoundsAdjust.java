package com.alec_wam.CrystalMod.tiles.machine.worksite.gui;

import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteUserBlocks;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Button;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.GuiElement;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Listener;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.elements.Rectangle;
import com.alec_wam.CrystalMod.util.BlockUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class GuiWorksiteBoundsAdjust extends GuiContainerWorksiteBase {

	boolean noTargetMode = false;
	ContainerWorksiteBoundsAdjust container;

	boolean boundsAdjusted = false, targetsAdjusted = false;
	byte[] checkedMap = new byte[16 * 16];

	public GuiWorksiteBoundsAdjust(ContainerWorksiteBase container) {
		super(container);
		this.container = (ContainerWorksiteBoundsAdjust) container;
		this.shouldCloseOnVanillaKeys = true;
		if (!this.container.worksite.userAdjustableBlocks()) {
			noTargetMode = true;
		}
	}

	private void setChecked(int x, int y, boolean checked) {
		if (!noTargetMode) {
			checkedMap[y * 16 + x] = checked ? (byte) 1 : (byte) 0;
		}
	}

	private boolean isChecked(int x, int y) {
		if (noTargetMode) {
			return false;
		}
		return checkedMap[y * 16 + x] == 1;
	}

	@Override
	public void initElements() {
		// read initial checked-map from container
	}

	@Override
	public void setupElements() {
		this.clearElements();
		Button b;

		b = new Button(48, 12, 40, 12, "NORTH") {
			@Override
			protected void onPressed() {
				if (container.max.getZ() >= container.pos.getZ()
						&& (container.min.getX() > container.pos.getX() || container.max.getX() < container.pos.getX())) {
					container.min = container.min.north();
					container.max = container.max.north();
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 40, 12, 40, 12, "SOUTH") {
			@Override
			protected void onPressed() {
				if (container.min.getZ() <= container.pos.getZ()
						&& (container.min.getX() > container.pos.getX() || container.max.getX() < container.pos.getX())) {
					container.min = container.min.south();
					container.max = container.max.south();
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 40 + 40, 12, 40, 12, "WEST") {
			@Override
			protected void onPressed() {
				if (container.max.getX() >= container.pos.getX()
						&& (container.min.getZ() > container.pos.getZ() || container.max.getZ() < container.pos.getZ())) {
					container.min = container.min.west();
					container.max = container.max.west();
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 40 + 40 + 40, 12, 40, 12, "EAST") {
			@Override
			protected void onPressed() {
				//if (container.min.getX() <= container.pos.getX()
						//&& (container.min.getZ() > container.pos.getZ() || container.max.getZ() < container.pos.getZ())) {
					container.min = container.min.east();
					container.max = container.max.east();
					boundsAdjusted = true;
					refreshGui();
				//}
			}
		};
		addGuiElement(b);

		b = new Button(48, 24, 40, 12, "XSIZE-") {
			@Override
			protected void onPressed() {
				if (container.max.getX() - container.min.getX() <= 0) {
					return;
				}
				if (container.min.getX() < container.pos.getX()) {
					container.min = container.min.add(1, 0, 0);
					boundsAdjusted = true;
					refreshGui();
				} else {
					container.max = container.max.add(-1, 0, 0);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 40, 24, 40, 12, "XSIZE+") {
			@Override
			protected void onPressed() {
				if (container.max.getX() - container.min.getX() + 1 >= container.worksite
						.getBoundsMaxWidth()) {
					return;
				}
				if (container.min.getX() < container.pos.getX()) {
					container.min = container.min.add(-1, 0, 0);
					boundsAdjusted = true;
					refreshGui();
				} else {
					container.max = container.max.add(1, 0, 0);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 80, 24, 40, 12, "ZSIZE-") {
			@Override
			protected void onPressed() {
				if (container.max.getZ() - container.min.getZ() <= 0) {
					return;
				}
				if (container.min.getZ() < container.pos.getZ()) {
					container.min = container.min.add(0, 0, 1);
					boundsAdjusted = true;
					refreshGui();
				} else {
					container.max = container.max.add(0, 0, -1);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 120, 24, 40, 12, "ZSIZE+") {
			@Override
			protected void onPressed() {
				if (container.max.getZ() - container.min.getZ() + 1 >= container.worksite
						.getBoundsMaxWidth()) {
					return;
				}
				if (container.min.getZ() < container.pos.getZ()) {
					container.min = container.min.add(0, 0, -1);
					boundsAdjusted = true;
					refreshGui();
				} else {
					container.max = container.max.add(0, 0, 1);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		addLayout();
	}

	private void addLayout() {
		int bits = (container.worksite.getBoundsMaxWidth() + 2);
		int size = (240 - 56) / bits;

		int tlx = (256 - (size * bits)) / 2 + size;
		int tly = 36 + 8 + size;

		BlockPos p = container.pos;
		BlockPos p1 = container.min;
		BlockPos p2 = container.max;

		BlockPos o = p.add(-p1.getX(), -p1.getY(), -p1.getZ());

		int w = p2.getX() - p1.getX();
		int l = p2.getZ() - p1.getZ();

		Rectangle r;

		r = new Rectangle(tlx + o.getX() * size, tly + o.getZ() * size, size, size,
				0x0000ffff, 0x0000ffff);
		addGuiElement(r);

		for (int x = 0; x <= w; x++) {
			final int x1 = x;
			for (int y = 0; y <= l; y++) {
				final int y1 = y;
				r = new ToggledRectangle(tlx + x * size, tly + y * size, size,
						size, 0x000000ff, 0x808080ff, 0xff0000ff, 0xff8080ff,
						isChecked(x, y)) {
					@Override
					public void clicked(ActivationEvent evt) {
						if (!noTargetMode) {
							super.clicked(evt);
							setChecked(x1, y1, checked);
							targetsAdjusted = true;
						}
					}
				};
				addGuiElement(r);
			}
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound data) {
		if (data.hasKey("checkedMap")) {
			checkedMap = data.getByteArray("checkedMap");
			refreshGui();
		}
	}

	@Override
	protected boolean onGuiCloseRequested() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("guiClosed", true);
		if (boundsAdjusted) {
			tag.setTag("min", BlockUtil.saveBlockPos(container.min));
			tag.setTag("max", BlockUtil.saveBlockPos(container.max));
		}
		if (targetsAdjusted
				&& container.worksite instanceof TileWorksiteUserBlocks) {
			if (!noTargetMode) {
				tag.setByteArray("checkedMap", checkedMap);
			}
		}
		sendDataToContainer(tag);
		BlockUtil.openWorksiteGui(player, 0,
				container.worksite.getPos().getX(), container.worksite.getPos().getY(), container.worksite.getPos().getZ());
		return super.onGuiCloseRequested();
	}

	private class ToggledRectangle extends Rectangle {
		boolean checked;
		int checkedColor;
		int hoverCheckedColor;

		public ToggledRectangle(int topLeftX, int topLeftY, int width,
				int height, int color, int hoverColor, int checkColor,
				int hoverCheckColor, boolean checked) {
			super(topLeftX, topLeftY, width, height, color, hoverColor);
			this.checked = checked;
			this.checkedColor = checkColor;
			this.hoverCheckedColor = hoverCheckColor;
			addNewListener(new Listener(Listener.MOUSE_DOWN) {
				@Override
				public boolean onEvent(GuiElement widget, ActivationEvent evt) {
					if (widget.isMouseOverElement(evt.mx, evt.my)) {
						clicked(evt);
					}
					return true;
				}
			});
		}

		public void clicked(ActivationEvent evt) {
			checked = !checked;
		}

		@Override
		protected int getColor(int mouseX, int mouseY) {
			if (checked) {
				return isMouseOverElement(mouseX, mouseY) ? hoverCheckedColor
						: checkedColor;
			}
			return super.getColor(mouseX, mouseY);
		}

	}

}
