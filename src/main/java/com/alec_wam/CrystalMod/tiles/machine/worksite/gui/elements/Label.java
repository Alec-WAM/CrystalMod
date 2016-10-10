package com.alec_wam.CrystalMod.tiles.machine.worksite.gui.elements;

import com.alec_wam.CrystalMod.util.Lang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Label extends GuiElement {

	FontRenderer fr;
	String text;
	boolean renderCentered = false;

	public Label(int topLeftX, int topLeftY, String text) {
		super(topLeftX, topLeftY);
		this.text = Lang.translateToLocal(text);
		this.height = 8;
		fr = Minecraft.getMinecraft().fontRendererObj;
		this.width = fr.getStringWidth(text);
	}

	public Label setRenderCentered() {
		this.renderCentered = true;
		return this;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			if (renderCentered) {
				int len = fr.getStringWidth(text) / 2;
				fr.drawStringWithShadow(text, renderX - len, renderY,
						0xffffffff);
			} else {
				fr.drawStringWithShadow(text, renderX, renderY, 0xffffffff);
			}
		}
	}

	public void setText(String text) {
		if (text == null) {
			text = "";
		}
		this.text = Lang.translateToLocal(text);
	}

	public String getText() {
		return text;
	}

}
