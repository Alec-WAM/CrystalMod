package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public interface INetworkGui {

	public ItemStackData getDataUnderMouse(int mouseX, int mouseY);

}
