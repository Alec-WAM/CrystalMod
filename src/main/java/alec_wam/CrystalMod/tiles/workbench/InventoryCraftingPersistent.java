package alec_wam.CrystalMod.tiles.workbench;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.InventoryCraftingSyncPacket;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;


// variant of InventoryCrafting that saves its itemstacks into the given inventory
public class InventoryCraftingPersistent extends InventoryCrafting {

  private final int length;
  private final Container eventHandler;
  private final IInventory parent;

  public InventoryCraftingPersistent(Container eventHandler, IInventory parent, int width, int height) {
    super(eventHandler, width, height);
    int k = width * height;

    assert (k == parent.getSizeInventory());

    this.parent = parent;
    this.length = k;
    this.eventHandler = eventHandler;
  }

  @Override
public int getSizeInventory() {
    return this.length;
  }

  @Override
public ItemStack getStackInSlot(int index) {
    return index >= this.getSizeInventory() ? ItemStackTools.getEmptyStack() : this.parent.getStackInSlot(index);
  }

  public String getCommandSenderName() {
    return "container.crafting";
  }

  @Override
public boolean hasCustomName() {
    return false;
  }

  @Override
public ItemStack decrStackSize(int index, int count) {
    if(!ItemStackTools.isNullStack(this.getStackInSlot(index))) {
      ItemStack itemstack;

      if(ItemStackTools.getStackSize(this.getStackInSlot(index)) <= count) {
        itemstack = this.getStackInSlot(index);
        this.setInventorySlotContents(index, ItemStackTools.getEmptyStack());
        this.eventHandler.onCraftMatrixChanged(this);
        return itemstack;
      }
      else {
        itemstack = this.getStackInSlot(index).splitStack(count);

        if(ItemStackTools.isEmpty(this.getStackInSlot(index))) {
          this.setInventorySlotContents(index, ItemStackTools.getEmptyStack());
        }

        this.eventHandler.onCraftMatrixChanged(this);
        return itemstack;
      }
    }
    else {
      return ItemStackTools.getEmptyStack();
    }
  }

  @Override
public void setInventorySlotContents(int index, ItemStack stack) {
    this.parent.setInventorySlotContents(index, stack);
    this.eventHandler.onCraftMatrixChanged(this);
  }

  @Override
  public void markDirty() {
    this.parent.markDirty();
    this.eventHandler.onCraftMatrixChanged(this);
    if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
    	CrystalModNetwork.sendToServer(new InventoryCraftingSyncPacket());
  }

  @Override
public void clear() {
    // inventory can't clear the tile container
  }
}
