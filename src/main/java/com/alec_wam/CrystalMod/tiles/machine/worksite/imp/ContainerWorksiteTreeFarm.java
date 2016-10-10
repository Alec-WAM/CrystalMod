package com.alec_wam.CrystalMod.tiles.machine.worksite.imp;

import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksite;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerWorksiteTreeFarm extends ContainerWorksite {

	public ContainerWorksiteTreeFarm(EntityPlayer player, WorksiteTreeFarm farm) {
		super(player, farm);

		int layerY = 8;
		int labelGap = 12;

		topLabel = layerY;
		layerY += labelGap;
		layerY = addSlots(8, layerY, 0, 27) + 4;
		frontLabel = layerY;
		layerY += labelGap;
		layerY = addSlots(8, layerY, 27, 3) + 4;
		bottomLabel = layerY;
		layerY += labelGap;
		layerY = addSlots(8, layerY, 30, 3) + 4;
		playerLabel = layerY;
		layerY += labelGap;
		guiHeight = addPlayerSlots(player, 8, layerY, 4) + 8;
	}

}
