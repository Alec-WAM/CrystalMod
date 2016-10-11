package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory;

import java.util.List;

import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public interface INetworkInventory {

	public List<ItemStackData> getItems(EStorageNetwork network);
	
	public int insertItem(EStorageNetwork network, ItemStack stack, boolean matching, boolean sim, boolean sendUpdate);
	
	public int extractItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim, boolean sendUpdate);
	
	public void updateItems(EStorageNetwork network, int index);
	
}
