package alec_wam.CrystalMod.client.gui;


import java.text.NumberFormat;
import java.util.List;

import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ElementEnergy extends ElementBase {

	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("crystalmod:textures/gui/elements/energy.png");
	public static final int DEFAULT_SCALE = 42;

	protected ICEnergyStorage storage;
	protected boolean isCreative;
	// If this is enabled, 1 pixel of energy will always show in the bar as long as it is non-zero.
	protected boolean alwaysShowMinimum = false;

	public ElementEnergy(GuiElementContainer gui, int posX, int posY, ICEnergyStorage storage) {

		super(gui, posX, posY);
		this.storage = storage;

		this.texture = DEFAULT_TEXTURE;
		this.sizeX = 16;
		this.sizeY = DEFAULT_SCALE;

		this.texW = 32;
		this.texH = 64;
	}

	public ElementEnergy setAlwaysShow(boolean show) {

		alwaysShowMinimum = show;
		return this;
	}
	
	public ElementEnergy setCreative(boolean creative) {

		isCreative = creative;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		int amount = getScaled();

		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(posX, posY, 0, 0, sizeX, sizeY);
		drawTexturedModalRect(posX, posY + DEFAULT_SCALE - amount, 16, DEFAULT_SCALE - amount, sizeX, amount);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}
	public static final NumberFormat fmt = NumberFormat.getNumberInstance();
	@Override
	public void addTooltip(List<String> list) {
		if(storage == null)return;
		if (storage.getMaxCEnergyStored() < 0 || isCreative) {
			list.add(Lang.localize("power.infinite"));
		} else {
			String charge = String.format("%s%s%s / %s%s%s ", "", fmt.format(storage.getCEnergyStored()),
		              TextFormatting.RESET,
		              TextFormatting.WHITE, fmt.format(storage.getMaxCEnergyStored()), TextFormatting.RESET);
			list.add(charge + Lang.localize("power.cu"));
		}
	}

	protected int getScaled() {
		if(storage == null)return sizeY;
		if (storage.getMaxCEnergyStored() <= 0 || isCreative) {
			return sizeY;
		}
		long fraction = (long) storage.getCEnergyStored() * sizeY / storage.getMaxCEnergyStored();

		return alwaysShowMinimum && storage.getCEnergyStored() > 0 ? Math.max(1, round(fraction)) : round(fraction);
	}

	public static int round(double d)
	{
		return (int)(d + 0.5D);
	}

}