package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingWrapper extends InventoryCrafting {

  private final IInventory backing;
  private final Container eventHandler;

  public InventoryCraftingWrapper(IInventory backing, Container eventHandlerIn, int width, int height) {
    super(eventHandlerIn, width, height);
    this.backing = backing;
    this.eventHandler = eventHandlerIn;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return index >= this.getSizeInventory() ? ItemStackTools.getEmptyStack() : backing.getStackInSlot(index);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    return backing.removeStackFromSlot(index);
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    final ItemStack result = backing.decrStackSize(index, count);
    if (!ItemStackTools.isNullStack(result)) {
      this.eventHandler.onCraftMatrixChanged(this);
    }
    return result;
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    backing.setInventorySlotContents(index, stack);
    this.eventHandler.onCraftMatrixChanged(this);
  }

  @Override
  public void clear() {
  }

}
