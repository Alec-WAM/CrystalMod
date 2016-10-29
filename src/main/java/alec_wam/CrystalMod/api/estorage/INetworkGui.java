package alec_wam.CrystalMod.api.estorage;

import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;

public interface INetworkGui {

	public ItemStackData getDataUnderMouse(int mouseX, int mouseY);

}
