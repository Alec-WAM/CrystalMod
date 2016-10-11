package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public interface INetworkContainer {

	public void sendItemsToAll();
	
	public void sendItemsToAll(List<ItemStackData> dataList);
	
	public void sendItemsTo(EntityPlayerMP player);
	
	public void sendItemStackToNetwork(EntityPlayerMP player, int slot, ItemStackData data);
	
	public void grabItemStackFromNetwork(EntityPlayerMP player, int slot, int amount, ItemStackData data);
	
	public void sendCraftingItemsToAll(List<ItemStackData> dataList);

	public EStorageNetwork getNetwork();
	
}
