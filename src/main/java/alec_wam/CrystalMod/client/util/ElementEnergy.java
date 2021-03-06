package alec_wam.CrystalMod.client.util;


import java.text.NumberFormat;
import java.util.List;

import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ElementEnergy extends ElementBase {

	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("crystalmod:textures/gui/elements/" + "Energy.png");
	public static final int DEFAULT_SCALE = 42;

	protected ICEnergyStorage storage;

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

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		int amount = getScaled();

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		drawTexturedModalRect(posX, posY, 0, 0, sizeX, sizeY);
		drawTexturedModalRect(posX, posY + DEFAULT_SCALE - amount, 16, DEFAULT_SCALE - amount, sizeX, amount);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}
	public static final NumberFormat fmt = NumberFormat.getNumberInstance();
	@Override
	public void addTooltip(List<String> list) {

		if (storage.getMaxCEnergyStored() < 0) {
			list.add("Infinite " + Lang.localize("power.cu"));
		} else {
			String charge = String.format("%s%s%s / %s%s%s ", "", fmt.format(storage.getCEnergyStored()),
		              TextFormatting.RESET,
		              TextFormatting.WHITE, fmt.format(storage.getMaxCEnergyStored()), TextFormatting.RESET);
			list.add(charge + Lang.localize("power.cu"));
		}
	}

	protected int getScaled() {

		if (storage.getMaxCEnergyStored() <= 0) {
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