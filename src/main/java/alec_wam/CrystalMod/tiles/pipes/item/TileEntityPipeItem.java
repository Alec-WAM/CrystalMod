package alec_wam.CrystalMod.tiles.pipes.item;

import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.pipes.NetworkType;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipeBase;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class TileEntityPipeItem extends TileEntityPipeBase {

	public TileEntityPipeItem() {
		super(ModBlocks.TILE_PIPE_ITEM);
	}

	@Override
	public NetworkType getNetworkType() {
		return NetworkType.ITEM;
	}

	@Override
	public PipeNetworkItem createNewNetwork() {
		return new PipeNetworkItem();
	}
	
	@Override
	public boolean canConnectToExternal(EnumFacing facing, boolean ignore){
		return getExternalInventory(facing) !=null;
	}

	@Override
	public void externalConnectionAdded(EnumFacing direction) {
		super.externalConnectionAdded(direction);
		if(network != null && network instanceof PipeNetworkItem) {
			if(getExternalInventory(direction) !=null){
				((PipeNetworkItem)network).inventoryAdded(this, direction, getExternalInventory(direction));
			}
		}
	}

	@Override
	public void externalConnectionRemoved(EnumFacing direction) {
		externalConnections.remove(direction);
		if(network != null && network instanceof PipeNetworkItem) {
			((PipeNetworkItem)network).inventoryRemoved(this, direction);
		}
	}
	
	public IItemHandler getExternalInventory(EnumFacing facing) {
		World world = getWorld();
	    if(world == null) {
	      return null;
	    }
	    BlockPos loc = getPos().offset(facing);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null) {
	      return ItemUtil.getItemHandler(te, facing.getOpposite());
	    }
	    return null;
	}

}
