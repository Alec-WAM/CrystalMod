package alec_wam.CrystalMod.api.estorage;

import java.util.List;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import net.minecraft.entity.player.EntityPlayerMP;

public interface INetworkContainer {

	public void sendItemsToAll();
	
	public void sendItemsToAll(List<ItemStackData> dataList);
	
	public void sendItemsTo(EntityPlayerMP player);
	
	public void sendItemStackToNetwork(EntityPlayerMP player, int slot, ItemStackData data);
	
	public void grabItemStackFromNetwork(EntityPlayerMP player, int slot, int amount, ItemStackData data);
	
	public void sendCraftingItemsToAll(List<ItemStackData> dataList);
	
	public void sendFluidsToAll();
	
	public void sendFluidsToAll(List<FluidStackData> dataList);
	
	public void sendFluidsTo(EntityPlayerMP player);
	
	public void sendSecurityTo(EntityPlayerMP player);

	public EStorageNetwork getNetwork();
	
}
