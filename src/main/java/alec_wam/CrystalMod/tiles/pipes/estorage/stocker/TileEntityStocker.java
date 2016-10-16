package alec_wam.CrystalMod.tiles.pipes.estorage.stocker;

import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.INetworkTile;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.IAutoCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.ICraftingTask;

public class TileEntityStocker extends TileEntityInventory implements INetworkTile, IAutoCrafter {

	public TileEntityStocker() {
		super("Stocker", 1);
	}

	public void update(){
		super.update();
		if(worldObj.isRemote || network == null || getStackInSlot(0) == null)return;
		if(RedstoneMode.ON.passes(getWorld(), getPos())){
			CraftingPattern pattern = new CraftingPattern(worldObj, this, getStackInSlot(0));
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
		for (ICraftingTask task : getNetwork().getCraftingTasks()) {
            if (task.getPattern().getCrafter() == this) {
            	getNetwork().cancelCraftingTask(task);
            }
        }
	}

	@Override
	public int getSpeed() {
		return 0;
	}

	@Override
	public IItemHandler getPatterns() {
		return handler;
	}

	@Override
	public boolean showPatterns() {
		return false;
	}
	
	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public int getDimension() {
		return worldObj !=null ? worldObj.provider.getDimension() : 0;
	}

	@Override
	public IItemHandler getFacingInventory() {
		return null;
	}

}
