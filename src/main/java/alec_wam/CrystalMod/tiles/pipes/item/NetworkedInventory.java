package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class NetworkedInventory {

  TileEntityPipeItem pip;
  EnumFacing pipDir;
  BlockPos location;
  EnumFacing inventorySide;

  List<Target> sendPriority = new ArrayList<Target>();
  RoundRobinIterator<Target> rrIter = new RoundRobinIterator<Target>(sendPriority);

  private int extractFromSlot = -1;

  int tickDeficit;
  
  World world;
  ItemPipeNetwork network;

  NetworkedInventory(ItemPipeNetwork network, IItemHandler inv, TileEntityPipeItem pip, EnumFacing pipDir, BlockPos location) {
    this.network = network;
    inventorySide = pipDir.getOpposite();

    this.pip = pip;
    this.pipDir = pipDir;
    this.location = location;
    world = pip.getWorld();
  }

  public boolean hasTarget(TileEntityPipeItem pipe, EnumFacing dir) {
    for (Target t : sendPriority) {
      if(t.inv.pip == pipe && t.inv.pipDir == dir) {
        return true;
      }
    }
    return false;
  }

  boolean canExtract() {
    ConnectionMode mode = pip.getConnectionMode(pipDir);
    return mode == ConnectionMode.INPUT || mode == ConnectionMode.IN_OUT;
  }

  boolean canInsert() {
    ConnectionMode mode = pip.getConnectionMode(pipDir);
    return mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
  }

  boolean isSticky() {
    return false;
  }

  int getPriority() {
    return pip.getOutputPriority(pipDir);
  }

  public void onTick() {
    if(tickDeficit > 0 || !canExtract() || !pip.isRedstoneModeMet(pipDir)) {
      //do nothing     
    } else {
      transferItems();
    }

    tickDeficit--;
    if(tickDeficit < -1) {
      //Sleep for a second before checking again.
      tickDeficit = 20;
    }
  }

  private int nextSlot(int numSlots) {
    ++extractFromSlot;
    if(extractFromSlot >= numSlots || extractFromSlot < 0) {
      extractFromSlot = 0;
    }
    return extractFromSlot;
  }

  private void setNextStartingSlot(int slot) {
    extractFromSlot = slot;
    extractFromSlot--;
  }

  private boolean transferItems() {

	  IItemHandler inventory = getInventory();
	  if (inventory == null) {
		  return false;
	  }
	  
	  int numSlots = inventory.getSlots();
	  if(numSlots < 1) {
		  return false;
	  }
	  
	  ItemStack extractItem = ItemStackTools.getEmptyStack();
	  int maxExtracted = pip.getMaximumExtracted(pipDir);

	  int slot = -1;
	  int slotChecksPerTick = Math.min(numSlots, ItemPipeNetwork.MAX_SLOT_CHECK_PER_TICK);
	  for (int i = 0; i < slotChecksPerTick; i++) {
		  slot = nextSlot(numSlots);      
		  ItemStack item = inventory.getStackInSlot(slot);
		  if(canExtractItem(item)) {
			  extractItem = item.copy();        
			  if (!ItemStackTools.isNullStack(inventory.extractItem(slot, ItemStackTools.getStackSize(extractItem), true))) {
				  if(doTransfer(extractItem, slot, maxExtracted)) {
					  setNextStartingSlot(slot);
					  return true;
				  }
			  }
		  }
	  }
	  return false;
  }

  private boolean canExtractItem(ItemStack itemStack) {
    if(ItemStackTools.isNullStack(itemStack)) {
      return false;
    }
    return pip.passesFilter(itemStack, pipDir);
  }

  private boolean doTransfer(ItemStack extractedItem, int slot, int maxExtract) {
	  if(ItemStackTools.isNullStack(extractedItem) || extractedItem.getItem() == null) {
		  return false;
	  }
	  ItemStack toExtract = extractedItem.copy();
	  ItemStackTools.setStackSize(toExtract, Math.min(maxExtract, ItemStackTools.getStackSize(toExtract)));
	  int numInserted = insertIntoTargets(toExtract);
	  
	  if(numInserted <= 0) {
		  return false;
	  }
	  itemExtracted(slot, numInserted);
	  return true;
  }

  public void itemExtracted(int slot, int numInserted) {
	  IItemHandler inventory = getInventory();
	  if (inventory != null) {
		  ItemStack curStack = inventory.getStackInSlot(slot);
		  if (!ItemStackTools.isNullStack(curStack)) {
			  ItemStack extracted = inventory.extractItem(slot, numInserted, false);
			  if (ItemStackTools.getStackSize(extracted) != numInserted) {
				  ModLogger.warning("NetworkedInventory.itemExtracted: Inserted " + numInserted + " " + curStack.getDisplayName() + " but only removed "
						  + (ItemStackTools.isNullStack(extracted) ? "null" : ItemStackTools.getStackSize(extracted)));
			  }
		  }
	  }
	  pip.itemsExtracted(numInserted, slot);
	  tickDeficit = Math.round(numInserted * pip.getTickTimePerItem(pipDir));
  }

  int insertIntoTargets(ItemStack toExtract) {
    if(ItemStackTools.isNullStack(toExtract)) {
      return 0;
    }

    int totalToInsert = ItemStackTools.getStackSize(toExtract);
    int leftToInsert = totalToInsert;
    boolean matchedStickyInput = false;

    Iterable<Target> targets = getTargetIterator();

    //for (Target target : sendPriority) {
    for (Target target : targets) {
    	//if(target.inv !=null)ModLogger.info("INV6 "+ location + " "+target.inv.location);
      if(target.stickyInput && !matchedStickyInput) {
      }
      if(target.stickyInput || !matchedStickyInput) {
        int inserted = target.inv.insertItem(toExtract);
        //ModLogger.info("INV8 "+ location + " "+target.inv.location+ " "+inserted);
        if(inserted > 0) {
        	ItemStackTools.incStackSize(toExtract, -inserted);
        	leftToInsert -= inserted;
        }
        if(leftToInsert <= 0) {
          return totalToInsert;
        }
      }
    }
    return totalToInsert - leftToInsert;
  }

  private Iterable<Target> getTargetIterator() {
    if(pip.isRoundRobinEnabled(pipDir)) {
      return rrIter;
    }
    return sendPriority;
  }

  private int insertItem(ItemStack item) {
    if(!canInsert() || ItemStackTools.isNullStack(item)) {
      return 0;
    }
    
    if(!pip.passesFilter(item, pipDir)){
    	return 0;
    }
    return ItemUtil.doInsertItem(getInventory(), item, inventorySide);
  }

  void updateInsertOrder() {
    sendPriority.clear();
    if(!canExtract()) {
      return;
    }
    List<Target> result = new ArrayList<NetworkedInventory.Target>();

    for (NetworkedInventory other : network.inventories) {
      if((pip.isSelfFeedEnabled(pipDir) || (other != this))
          && other.canInsert()
          /*&& con.getInputColor(conDir) == other.con.getOutputColor(other.conDir)*/) {

        /*if(Config.itemConduitUsePhyscialDistance) {
          sendPriority.add(new Target(other, distanceTo(other), other.isSticky(), other.getPriority()));
        } else {*/
          result.add(new Target(other, 9999999, other.isSticky(), other.getPriority()));
        //}
      }
    }

    /*if(Config.itemConduitUsePhyscialDistance) {
      Collections.sort(sendPriority);
    } else {*/
      if(!result.isEmpty()) {
        Map<BlockPos, Integer> visited = new HashMap<BlockPos, Integer>();
        List<BlockPos> steps = new ArrayList<BlockPos>();
        steps.add(pip.getPos());
        calculateDistances(result, visited, steps, 0);

        sendPriority.addAll(result);

        Collections.sort(sendPriority);
      }
    //}

  }

  private void calculateDistances(List<Target> targets, Map<BlockPos, Integer> visited, List<BlockPos> steps, int distance) {
    if(steps == null || steps.isEmpty()) {
      return;
    }

    ArrayList<BlockPos> nextSteps = new ArrayList<BlockPos>();
    for (BlockPos bc : steps) {
      TileEntityPipeItem con = network.pipMap.get(bc);
      if(con != null) {
        for (EnumFacing dir : con.getExternalConnections()) {
          Target target = getTarget(targets, con, dir);
          if(target != null && target.distance > distance) {
            target.distance = distance;
          }
        }

        if(!visited.containsKey(bc)) {
          visited.put(bc, distance);
        } else {
          int prevDist = visited.get(bc);
          if(prevDist <= distance) {
            continue;
          }
          visited.put(bc, distance);
        }

        for (EnumFacing dir : con.getPipeConnections()) {
          nextSteps.add(bc.offset(dir));
        }
      }
    }
    calculateDistances(targets, visited, nextSteps, distance + 1);
  }

  private Target getTarget(List<Target> targets, TileEntityPipeItem con, EnumFacing dir) {
    if(targets == null || con == null || con.getPos() == null) {
      return null;
    }
    for (Target target : targets) {
      BlockPos targetConLoc = null;
      if(target != null && target.inv != null && target.inv.pip != null) {
        targetConLoc = target.inv.pip.getPos();
      }
      if(targetConLoc != null && target.inv.pipDir == dir && targetConLoc.equals(pip.getPos())) {
        return target;
      }
    }
    return null;
  }

  public @Nullable IItemHandler getInventory() {
    return ItemUtil.getExternalItemHandler(world, location, inventorySide);
  }

  public EnumFacing getInventorySide() {
    return inventorySide;
  }

  public void setInventorySide(EnumFacing inventorySide) {
    this.inventorySide = inventorySide;
  }

  public String getLocalizedInventoryName() {
    String inventoryName = getInventory().toString();
    if(inventoryName == null) {
      return "null";
    } else {
      return Lang.translateToLocal(inventoryName);
    }
  }

  static class Target implements Comparable<Target> {
    NetworkedInventory inv;
    int distance;
    boolean stickyInput;
    int priority;

    Target(NetworkedInventory inv, int distance, boolean stickyInput, int priority) {
      this.inv = inv;
      this.distance = distance;
      this.stickyInput = stickyInput;
      this.priority = priority;
    }

    @Override
    public int compareTo(Target o) {
      if(stickyInput && !o.stickyInput) {
        return -1;
      }
      if(!stickyInput && o.stickyInput) {
        return 1;
      }
      if(priority != o.priority) {
        return ItemPipeNetwork.compare(o.priority, priority);
      }
      return ItemPipeNetwork.compare(distance, o.distance);
    }

  }
}
