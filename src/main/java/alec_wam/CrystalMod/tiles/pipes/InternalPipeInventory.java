package alec_wam.CrystalMod.tiles.pipes;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class InternalPipeInventory implements IInventory {
   private final ITextComponent inventoryTitle;
   private final int slotsCount;
   private final NonNullList<ItemStack> inventoryContents;
   private final Direction side;
   /**
    * The custom name of this inventory, or null if it doesn't have one and {@link inventoryTitle} should be used
    * instead
    */
   private ITextComponent customName;

   public InternalPipeInventory(Direction side, ITextComponent title, int slotCount) {
	  this.side = side;
      this.inventoryTitle = title;
      this.slotsCount = slotCount;
      this.inventoryContents = NonNullList.withSize(slotCount, ItemStack.EMPTY);
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventoryContents.size() ? this.inventoryContents.get(index) : ItemStack.EMPTY;
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
	  final ItemStack lastStack = this.inventoryContents.get(index).copy();
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, index, count);
      this.markDirty(index, lastStack);

      return itemstack;
   }

   public ItemStack addItem(ItemStack stack) {
      ItemStack itemstack = stack.copy();

      for(int i = 0; i < this.slotsCount; ++i) {
         ItemStack itemstack1 = this.getStackInSlot(i);
         if (itemstack1.isEmpty()) {
            this.setInventorySlotContents(i, itemstack);
            this.markDirty(i, itemstack);
            return ItemStack.EMPTY;
         }

         if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
            int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
            int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());
            if (k > 0) {
               itemstack1.grow(k);
               itemstack.shrink(k);
               if (itemstack.isEmpty()) {
                  this.markDirty(i, itemstack);
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (itemstack.getCount() != stack.getCount()) {
         this.markDirty();
      }

      return itemstack;
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      ItemStack itemstack = this.inventoryContents.get(index);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
    	  this.markDirty(index, itemstack);
          this.inventoryContents.set(index, ItemStack.EMPTY);
    	 return itemstack;
      }
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      final ItemStack lastStack = this.inventoryContents.get(index);
	  this.inventoryContents.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty(index, lastStack);
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.slotsCount;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.inventoryContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : this.inventoryTitle;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public void setCustomName(@Nullable ITextComponent name) {
      this.customName = name;
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return 64;
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty(int slot, ItemStack lastStack) {

   }

   @Override
   public void markDirty() {}

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      return true;
   }

   public void openInventory(PlayerEntity player) {
   }

   public void closeInventory(PlayerEntity player) {
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.inventoryContents.clear();
   }

   public Direction getSide() {
	   return side;
   }
}