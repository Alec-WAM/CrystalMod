package com.alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public interface INetworkGui {

	public ItemStackData getDataUnderMouse(int mouseX, int mouseY);

}
