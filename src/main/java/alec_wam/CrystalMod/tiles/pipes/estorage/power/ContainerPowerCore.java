package alec_wam.CrystalMod.tiles.pipes.estorage.power;

import java.util.List;

import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerPowerCore extends Container implements INetworkContainer {

	private TileNetworkPowerCore core;
	
	public ContainerPowerCore(EntityPlayer player, TileNetworkPowerCore core){
		this.core = core;
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 148 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 148+58));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public EStorageNetwork getNetwork() {
		if(core !=null)return core.getNetwork();
		return null;
	}

	@Override
	public void sendItemsToAll() {
		// NO-OP
	}

	@Override
	public void sendItemsToAll(List<ItemStackData> dataList) {
		// NO-OP
	}

	@Override
	public void sendItemsTo(EntityPlayerMP player) {
		// NO-OP
	}

	@Override
	public void sendItemStackToNetwork(EntityPlayerMP player, int slot, ItemStackData data) {
		// NO-OP
	}

	@Override
	public void grabItemStackFromNetwork(EntityPlayerMP player, int slot, int amount, ItemStackData data) {
		// NO-OP
	}

	@Override
	public void sendCraftingItemsToAll(List<ItemStackData> dataList) {
		// NO-OP
	}

	@Override
	public void sendFluidsToAll() {
		// NO-OP
	}

	@Override
	public void sendFluidsToAll(List<FluidStackData> dataList) {
		// NO-OP
	}

	@Override
	public void sendFluidsTo(EntityPlayerMP player) {
		// NO-OP
	}

}
