package alec_wam.CrystalMod.tiles.pipes.estorage.stocker;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import alec_wam.CrystalMod.api.estorage.IAutoCrafter;
import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;

public class TileEntityStocker extends TileEntityInventory implements INetworkTile {

	public TileEntityStocker() {
		super("Stocker", 1);
	}

	public void update(){
		super.update();
		if(worldObj.isRemote || network == null || getStackInSlot(0) == null)return;
		if(RedstoneMode.ON.passes(getWorld(), getPos())){
			//CraftingPattern pattern = new CraftingPattern(worldObj, this, getStackInSlot(0));
			//network.addCraftingTaskIfNotCrafting(network.createCraftingTask(pattern));
		}
	}

	private EStorageNetwork network;
	
	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {
		if(getNetwork() == null)return;
	}
}
