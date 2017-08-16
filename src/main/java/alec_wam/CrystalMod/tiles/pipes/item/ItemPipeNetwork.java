package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.item.NetworkedInventory.Target;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public class ItemPipeNetwork extends AbstractPipeNetwork {
	final List<NetworkedInventory> inventories = new ArrayList<NetworkedInventory>();
	  private final Map<BlockPos, List<NetworkedInventory>> invMap = new HashMap<BlockPos, List<NetworkedInventory>>();

	  final Map<BlockPos, TileEntityPipeItem> pipMap = new HashMap<BlockPos, TileEntityPipeItem>();

	  private boolean requiresSort = true;

	  private boolean doingSend = false;

	  private int changeCount;

	  @Override
	  public void addPipe(TileEntityPipe dpip) {
	    super.addPipe(dpip);
	    if(!(dpip instanceof TileEntityPipeItem))return;
	    TileEntityPipeItem pip = (TileEntityPipeItem) dpip;
	    pipMap.put(pip.getPos(), pip);

	    TileEntity te = pip;
	    if(te != null) {
	      for (EnumFacing direction : pip.getExternalConnections()) {
	        IItemHandler extCon = pip.getExternalInventory(direction);
	        if(extCon != null) {
	          BlockPos p = te.getPos().offset(direction);
	          inventoryAdded(pip, direction, p.getX(), p.getY(), p.getZ(), extCon);
	        }
	      }
	    }
	  }

	  public void inventoryAdded(TileEntityPipeItem itemPipe, EnumFacing direction, int x, int y, int z, IItemHandler externalInventory) {
	    BlockPos bc = new BlockPos(x, y, z);
	    NetworkedInventory inv = new NetworkedInventory(this, externalInventory, itemPipe, direction, bc);
	    inventories.add(inv);
	    getOrCreate(bc).add(inv);
	    requiresSort = true;
	  }
	  
	  public NetworkedInventory getInventory(TileEntityPipeItem pipe, EnumFacing dir) {
	    for(NetworkedInventory inv : inventories) {
	      if(inv.pip == pipe && inv.pipDir == dir) {
	        return inv;
	      }
	    }
	    return null;
	  }

	  private List<NetworkedInventory> getOrCreate(BlockPos bc) {
	    List<NetworkedInventory> res = invMap.get(bc);
	    if(res == null) {
	      res = new ArrayList<NetworkedInventory>();
	      invMap.put(bc, res);
	    }
	    return res;
	  }

	  public void inventoryRemoved(TileEntityPipeItem itemConduit, int x, int y, int z) {
	    BlockPos bc = new BlockPos(x, y, z);
	    List<NetworkedInventory> invs = getOrCreate(bc);
	    NetworkedInventory remove = null;
	    for (NetworkedInventory ni : invs) {
	      if(ni.pip.getPos().equals(itemConduit.getPos())) {
	        remove = ni;
	        break;
	      }
	    }
	    if(remove != null) {
	      invs.remove(remove);
	      inventories.remove(remove);
	      requiresSort = true;
	    }

	  }

	  public void routesChanged() {
	    requiresSort = true;
	  }

	  public void inventoryPanelSourcesChanged() {
	    changeCount++;
	  }

	  public int getChangeCount() {
	    return changeCount;
	  }

	  @Override
	  public void destroyNetwork() {
	    super.destroyNetwork();
	  }

	  public ItemStack sendItems(TileEntityPipeItem itemConduit, ItemStack item, EnumFacing side) {
	    if(doingSend) {
	      return item;
	    }

	    if(ItemStackTools.isNullStack(item)) {
	      return item;
	    }

	    try {
	      doingSend = true;
	      BlockPos loc = itemConduit.getPos().offset(side);

	      ItemStack result = item.copy();
	      List<NetworkedInventory> invs = getOrCreate(loc);
	      for (NetworkedInventory inv : invs) {

	        if(inv.pip.getPos().equals(itemConduit.getPos())) {
	          int numInserted = inv.insertIntoTargets(item.copy());
	          if(numInserted >= ItemStackTools.getStackSize(item)) {
	            return null;
	          }
	          ItemStackTools.incStackSize(result, -numInserted);
	        }
	      }
	      return result;
	    } finally {
	      doingSend = false;
	    }
	  }

	  public List<String> getTargetsForExtraction(BlockPos extractFrom, TileEntityPipeItem con, ItemStack input) {
	    List<String> result = new ArrayList<String>();

	    List<NetworkedInventory> invs = getOrCreate(extractFrom);
	    for (NetworkedInventory source : invs) {

	      if(source.pip.getPos().equals(con.getPos())) {
	        if(source.sendPriority != null) {
	          for (Target t : source.sendPriority) {
	            if(ItemStackTools.isNullStack(input)) {
	              String s = t.inv.getLocalizedInventoryName() + " " + t.inv.location.toString() + " Distance [" + t.distance + "] ";
	              result.add(s);
	            }
	          }
	        }
	      }
	    }

	    return result;
	  }

	  public List<String> getInputSourcesFor(TileEntityPipeItem con, EnumFacing dir, ItemStack input) {
	    List<String> result = new ArrayList<String>();
	    for (NetworkedInventory inv : inventories) {
	      if(inv.hasTarget(con, dir)) {
	        if(ItemStackTools.isNullStack(input)) {
	          result.add(inv.getLocalizedInventoryName() + " " + inv.location.toString());
	        }
	      }
	    }
	    return result;
	  }

	  @Override
	  public void doNetworkTick() {
	    for (NetworkedInventory ni : inventories) {
	      if(requiresSort) {
	        ni.updateInsertOrder();
	      }
	      ni.onTick();
	    }
	    if(requiresSort) {
	      requiresSort = false;
	      changeCount++;
	    }
	  }

	  static int compare(int x, int y) {
	    return (x < y) ? -1 : ((x == y) ? 0 : 1);
	  }

	  static int MAX_SLOT_CHECK_PER_TICK = 64;

}
