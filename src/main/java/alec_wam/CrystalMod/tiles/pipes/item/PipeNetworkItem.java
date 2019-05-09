package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.tiles.pipes.NetworkPos;
import alec_wam.CrystalMod.tiles.pipes.PipeNetworkBase;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipeBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public class PipeNetworkItem extends PipeNetworkBase<TileEntityPipeItem>{

	private boolean requiresSort;
	protected List<NetworkInventory> inventoryList; 
	private final Map<NetworkPos, List<NetworkInventory>> invMap;

	public PipeNetworkItem(){
		super();
		inventoryList = Lists.newArrayList();
		invMap = new HashMap<NetworkPos, List<NetworkInventory>>();
	}

	@Override
	public void resetNetwork(){
		super.resetNetwork();
		inventoryList.clear();
	}

	@Override
	public boolean addPipe(TileEntityPipeBase pipe){
		if(!(pipe instanceof TileEntityPipeItem)) return false;
		if(super.addPipe(pipe)){
			TileEntityPipeItem pip = (TileEntityPipeItem) pipe;
			for (EnumFacing facing : pip.getExternalConnections()) {
				IItemHandler extCon = pip.getExternalInventory(facing);
				if(extCon != null) {
					inventoryAdded(pip, facing, extCon);
				}
			}
		    return true;
		}
		return false;
	}
	
	public void inventoryAdded(TileEntityPipeItem itemPipe, EnumFacing direction, IItemHandler externalInventory) {
		NetworkPos pos = itemPipe.getNetworkPos().offset(direction);
		NetworkInventory inv = new NetworkInventory(this, externalInventory, itemPipe, direction, pos);
		inventoryList.add(inv);
		getOrCreate(pos).add(inv);
		requiresSort = true;
	}

	public NetworkInventory getInventory(TileEntityPipeItem pipe, EnumFacing dir) {
		for(NetworkInventory inv : inventoryList) {
			if(inv.pip == pipe && inv.pipDir == dir) {
				return inv;
			}
		}
		return null;
	}

	private List<NetworkInventory> getOrCreate(NetworkPos bc) {
		List<NetworkInventory> res = invMap.get(bc);
		if(res == null) {
			res = new ArrayList<NetworkInventory>();
			invMap.put(bc, res);
		}
		return res;
	}

	public void inventoryRemoved(TileEntityPipeItem itemPipe, EnumFacing facing) {
		NetworkPos pos = itemPipe.getNetworkPos().offset(facing);
		List<NetworkInventory> invs = getOrCreate(pos);
		NetworkInventory remove = null;
		for (NetworkInventory ni : invs) {
			if(ni.pip.getPos().equals(itemPipe.getPos())) {
				remove = ni;
				break;
			}
		}
		if(remove != null) {
			invs.remove(remove);
			inventoryList.remove(remove);
			requiresSort = true;
		}

	}

	@Override
	public void tick(){
		super.tick();
		for (NetworkInventory ni : inventoryList) {
			if(requiresSort) {
				ni.updateInsertOrder();
			}
			ni.onTick();
		}
		if(requiresSort) {
			requiresSort = false;
		}
	}
}
