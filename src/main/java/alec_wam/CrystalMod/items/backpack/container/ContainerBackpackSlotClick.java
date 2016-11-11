package alec_wam.CrystalMod.items.backpack.container;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class ContainerBackpackSlotClick extends ContainerBackpackBase
{
    protected boolean isDragging;
    protected boolean draggingRightClick;
    protected final Set<Integer> draggedSlots = new HashSet<Integer>();
    protected int selectedSlot = -1;

    public ContainerBackpackSlotClick(EntityPlayer player, IItemHandler inventory)
    {
        super(player, inventory);
    }

    public int getSelectedSlot()
    {
        return this.selectedSlot;
    }

    protected void startDragging(boolean isRightClick)
    {
        this.isDragging = true;
        this.draggingRightClick = isRightClick;
        this.draggedSlots.clear();
    }

    private void dragging(int slotNum)
    {
        this.draggedSlots.add(slotNum);
    }

    protected ItemStack putItemsToSlot(SlotItemHandlerGeneric slot, ItemStack stack, int amount)
    {
        if (stack == null)
        {
            return null;
        }

        if (amount < stack.stackSize)
        {
            ItemStack stackInsert = stack.splitStack(amount);
            stackInsert = slot.insertItem(stackInsert, false);

            if (stackInsert != null)
            {
                stack.stackSize += stackInsert.stackSize;
            }

            return stack;
        }

        return slot.insertItem(stack, false);
    }

    public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        if (stack1 == null || stack2 == null)
        {
            return stack1 == stack2;
        }

        return stack1.isItemEqual(stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
    
    public static boolean areItemStacksOreDictMatch(ItemStack stackTarget, ItemStack stackReference)
    {
        int[] ids = OreDictionary.getOreIDs(stackReference);

        for (int id : ids)
        {
            List<ItemStack> oreStacks = OreDictionary.getOres(OreDictionary.getOreName(id), false);

            for (ItemStack oreStack : oreStacks)
            {
                if (OreDictionary.itemMatches(stackTarget, oreStack, false) == true)
                {
                    return true;
                }
            }
        }

        return false;
    }
    
    protected boolean takeItemsFromSlotToCursor(SlotItemHandlerGeneric slot, int amount)
    {
        ItemStack stackCursor = this.inventoryPlayer.getItemStack();
        ItemStack stackSlot = slot.getStack();

        if (slot.canTakeStack(this.player) == false || stackSlot == null ||
            (stackCursor != null && areItemStacksEqual(stackCursor, stackSlot) == false))
        {
            return false;
        }

        amount = Math.min(amount, stackSlot.getMaxStackSize());
        int spaceAvailable = stackSlot.getMaxStackSize();

        if (stackCursor != null)
        {
            spaceAvailable = stackCursor.getMaxStackSize() - stackCursor.stackSize;
        }

        amount = Math.min(amount, spaceAvailable);

        if (amount <= 0 || (slot.isItemValid(stackSlot) == false && spaceAvailable < stackSlot.stackSize))
        //if (amount <= 0 || ((slot instanceof SlotItemHandlerCraftresult) == true && spaceAvailable < stackSlot.stackSize))
        {
            return false;
        }

        stackSlot = slot.decrStackSize(amount);

        if (stackSlot != null)
        {
            slot.onPickupFromSlot(this.player, stackSlot);

            if (stackCursor == null)
            {
                stackCursor = stackSlot;
            }
            else
            {
                stackCursor.stackSize += stackSlot.stackSize;
            }

            this.inventoryPlayer.setItemStack(stackCursor);

            return true;
        }

        return false;
    }

    protected boolean swapSlots(SlotItemHandlerGeneric slot1, SlotItemHandlerGeneric slot2)
    {
        if ((slot1.getHasStack() == true && slot1.canTakeStack(this.player) == false) ||
            (slot2.getHasStack() == true && slot2.canTakeStack(this.player) == false))
        {
            return false;
        }

        ItemStack stack1 = slot1.getStack();
        ItemStack stack2 = slot2.getStack();
        if ((stack1 != null && slot2.isItemValid(stack1) == false) || (stack2 != null && slot1.isItemValid(stack2) == false))
        {
            return false;
        }

        if (stack1 != null)
        {
            slot1.onPickupFromSlot(this.player, stack1);
        }

        if (stack2 != null)
        {
            slot2.onPickupFromSlot(this.player, stack2);
        }

        slot1.putStack(ItemStack.copyItemStack(stack2));
        slot2.putStack(ItemStack.copyItemStack(stack1));

        return true;
    }

    protected void endDragging()
    {
        ItemStack stackCursor = ItemStack.copyItemStack(this.inventoryPlayer.getItemStack());

        if (stackCursor != null)
        {
            int numSlots = this.draggedSlots.size();
            int itemsPerSlot = this.draggingRightClick == true ? 1 : (numSlots > 0 ? stackCursor.stackSize / numSlots : stackCursor.stackSize);

            for (int slotNum : this.draggedSlots)
            {
                if (stackCursor == null)
                {
                    break;
                }

                SlotItemHandlerGeneric slot = this.getSlotItemHandler(slotNum);
                if (slot != null)
                {
                    int amount = Math.min(itemsPerSlot, this.getMaxStackSizeFromSlotAndStack(slot, stackCursor));
                    amount = Math.min(amount, stackCursor.stackSize);
                    stackCursor = this.putItemsToSlot(slot, stackCursor, amount);
                }
            }

            this.inventoryPlayer.setItemStack(stackCursor);
        }

        this.isDragging = false;
    }

    protected void leftClickOutsideInventory(EntityPlayer player)
    {
        ItemStack stackCursor = this.inventoryPlayer.getItemStack();

        if (stackCursor != null)
        {
            int max = stackCursor.getMaxStackSize();
            while (stackCursor.stackSize > max)
            {
                ItemStack stackDrop = stackCursor.copy();
                stackDrop.stackSize = max;
                player.dropItem(stackDrop, true);
            }

            player.dropItem(stackCursor, true);
            this.inventoryPlayer.setItemStack(null);
        }
    }

    protected void rightClickOutsideInventory(EntityPlayer player)
    {
        ItemStack stackCursor = this.inventoryPlayer.getItemStack();

        if (stackCursor != null)
        {
            ItemStack stackDrop = stackCursor.splitStack(1);
            player.dropItem(stackDrop, true);
            this.inventoryPlayer.setItemStack(stackCursor.stackSize > 0 ? stackCursor : null);
        }
    }

    protected void leftClickSlot(int slotNum, EntityPlayer player)
    {
        SlotItemHandlerGeneric slot = this.getSlotItemHandler(slotNum);
        if (slot == null)
        {
            return;
        }

        ItemStack stackCursor = this.inventoryPlayer.getItemStack();
        ItemStack stackSlot = slot.getStack();

        // Items in cursor
        if (stackCursor != null)
        {
            // Empty slot or identical items: try to add to slot
            if (stackSlot == null || areItemStacksEqual(stackCursor, stackSlot) == true)
            {
                // Can put items into the slot
                if (slot.isItemValid(stackCursor) == true)
                {
                    this.inventoryPlayer.setItemStack(slot.insertItem(stackCursor, false));
                }
                // Can't put items into the slot (for example a crafting output slot); take items instead
                else if (stackSlot != null)
                {
                    this.takeItemsFromSlotToCursor(slot, stackSlot.stackSize);
                }
            }
            // Different items, try to swap the stacks
            else if (slot.canTakeStack(this.player) == true && slot.canTakeAll() == true && slot.isItemValid(stackCursor) == true)
            {
                this.inventoryPlayer.setItemStack(slot.decrStackSize(stackSlot.stackSize));
                slot.onPickupFromSlot(this.player, stackSlot);
                slot.insertItem(stackCursor, false);
            }
        }
        // Empty cursor, trying to take items from the slot into the cursor
        else if (stackSlot != null)
        {
            this.takeItemsFromSlotToCursor(slot, stackSlot.stackSize);
        }
    }

    protected void rightClickSlot(int slotNum, EntityPlayer player)
    {
        SlotItemHandlerGeneric slot = this.getSlotItemHandler(slotNum);
        if (slot == null)
        {
            return;
        }

        ItemStack stackCursor = this.inventoryPlayer.getItemStack();
        ItemStack stackSlot = slot.getStack();

        // Items in cursor
        if (stackCursor != null)
        {
            // Empty slot or identical items: try to add to slot
            if (stackSlot == null || areItemStacksEqual(stackCursor, stackSlot) == true)
            {
                // Can put items into the slot
                if (slot.isItemValid(stackCursor) == true)
                {
                    if (slot.insertItem(stackCursor.splitStack(1), false) != null)
                    {
                        stackCursor.stackSize++;
                    }

                    this.inventoryPlayer.setItemStack(stackCursor.stackSize > 0 ? stackCursor : null);
                }
                // Can't put items into the slot (for example a crafting output slot); take items instead
                else if (stackSlot != null)
                {
                    this.takeItemsFromSlotToCursor(slot, stackSlot.stackSize);
                }
            }
            // Different items, try to swap the stacks
            else if (slot.canTakeStack(this.player) == true && slot.canTakeAll() == true && slot.isItemValid(stackCursor) == true)
            {
                this.inventoryPlayer.setItemStack(slot.decrStackSize(stackSlot.stackSize));
                slot.onPickupFromSlot(this.player, stackSlot);
                slot.insertItem(stackCursor, false);
            }
        }
        // Empty cursor, trying to take items from the slot into the cursor
        else if (stackSlot != null)
        {
            int amount = stackSlot.stackSize; // default to the whole stack if it can't be returned to the slot

            if (slot.isItemValid(stackSlot) == true)
            {
                amount = Math.min((int)Math.ceil((double)stackSlot.stackSize / 2.0d), (int)Math.ceil((double)stackSlot.getMaxStackSize() / 2.0d));
            }

            this.takeItemsFromSlotToCursor(slot, amount);
        }
    }

    protected void middleClickSlot(int slotNum, EntityPlayer player)
    {
        SlotItemHandlerGeneric slot1 = this.getSlotItemHandler(slotNum);

        // Only allow swapping in this inventory (which supports the large stacks)
        // NOTE: This assumes that the swappable "main" inventory is the "inventory" reference in this Container
        if (slot1 != null && slot1.getItemHandler() == this.inventory)
        {
            if (this.selectedSlot >= 0 && this.selectedSlot < this.inventorySlots.size())
            {
                // Don't swap with self
                if (this.selectedSlot != slotNum)
                {
                    SlotItemHandlerGeneric slot2 = this.getSlotItemHandler(this.selectedSlot);
                    if (slot2 != null)
                    {
                        this.swapSlots(slot1, slot2);
                    }
                }

                this.selectedSlot = -1;
            }
            else
            {
                this.selectedSlot = slotNum;
            }
        }
    }

    protected void leftDoubleClickSlot(int slotNum, EntityPlayer player)
    {
        SlotItemHandlerGeneric slot = this.getSlotItemHandler(slotNum);
        ItemStack stackCursor = this.inventoryPlayer.getItemStack();

        if (slot != null && stackCursor != null && this.canMergeSlot(stackCursor, slot) == true)
        {
            ItemStack stackTmp = collectItemsFromInventory(slot.getItemHandler(), stackCursor, stackCursor.getMaxStackSize() - stackCursor.stackSize, true);
            if (stackTmp != null)
            {
                stackCursor.stackSize += stackTmp.stackSize;
                this.inventoryPlayer.setItemStack(stackCursor);
            }
        }
    }
    
    /**
     * Collects items from the inventory that are identical to stackTemplate and makes a new ItemStack
     * out of them, up to stackSize = maxAmount. If <b>reverse</b> is true, then the items are collected
     * starting from the end of the given inventory.
     * If no matching items are found, null is returned.
     */
    public static ItemStack collectItemsFromInventory(IItemHandler inv, ItemStack stackTemplate, int maxAmount, boolean reverse)
    {
        return collectItemsFromInventory(inv, stackTemplate, maxAmount, reverse, false);
    }

    /**
     * Collects items from the inventory that are identical to stackTemplate and makes a new ItemStack
     * out of them, up to stackSize = maxAmount. If <b>reverse</b> is true, then the items are collected
     * starting from the end of the given inventory.
     * If no matching items are found, null is returned.
     */
    public static ItemStack collectItemsFromInventory(IItemHandler inv, ItemStack stackTemplate, int maxAmount, boolean reverse, boolean useOreDict)
    {
        ItemStack stack = stackTemplate.copy();
        stack.stackSize = 0;

        int inc = (reverse == true ? -1 : 1);
        int start = (reverse == true ? (inv.getSlots() - 1) : 0);

        for (int slot = start; slot >= 0 && slot < inv.getSlots() && stack.stackSize < maxAmount; slot += inc)
        {
            ItemStack stackTmp = inv.getStackInSlot(slot);
            if (stackTmp == null)
            {
                continue;
            }

            if (areItemStacksEqual(stackTmp, stackTemplate) == true)
            {
                stackTmp = inv.extractItem(slot, maxAmount - stack.stackSize, false);
                if (stackTmp != null)
                {
                    stack.stackSize += stackTmp.stackSize;
                }
            }
            else if (useOreDict == true && areItemStacksOreDictMatch(stackTmp, stackTemplate) == true)
            {
                // This is the first match, and since it's an OreDictionary match ie. different actual
                // item, we convert the stack to the matched item.
                if (stack.stackSize == 0)
                {
                    stack = stackTmp.copy();
                    stack.stackSize = 0;
                }

                stackTmp = inv.extractItem(slot, maxAmount - stack.stackSize, false);
                if (stackTmp != null)
                {
                    stack.stackSize += stackTmp.stackSize;
                }
            }
        }

        return stack.stackSize > 0 ? stack : null;
    }

    protected void shiftClickSlot(int slotNum, EntityPlayer player)
    {
        SlotItemHandlerGeneric slot = this.getSlotItemHandler(slotNum);
        ItemStack stackSlot = slot != null ? slot.getStack() : null;
        if (stackSlot == null)
        {
            return;
        }

        /*if (slot instanceof SlotItemHandlerCraftresult)
        {
            ItemStack stackOrig = stackSlot.copy();
            // Craft up to one stack at a time
            //int num = stackSlot.getMaxStackSize() / stackSlot.stackSize;
            int num = 64;

            while (num-- > 0)
            {
                // Could not transfer the items, or ran out of some of the items, so the crafting result changed, bail out now
                if (this.transferStackFromSlot(player, slotNum) == false || areItemStacksEqual(stackOrig, slot.getStack()) == false)
                {
                    break;
                }
            }
        }
        // Only transfer a maximum of one regular stack
        else
        {*/
            this.transferStackFromSlot(player, slotNum);
            slot.onPickupFromSlot(player, stackSlot);
        //}
    }

    protected void pressDropKey(int slotNum, EntityPlayer player, boolean wholeStack)
    {
        SlotItemHandlerGeneric slot = this.getSlotItemHandler(slotNum);
        ItemStack stackSlot = slot != null ? slot.getStack() : null;

        if (stackSlot != null && slot.canTakeStack(this.player) == true)
        {
            ItemStack stackDrop = slot.decrStackSize(wholeStack == true ? stackSlot.stackSize : 1);

            if (stackDrop != null)
            {
                slot.onPickupFromSlot(player, stackDrop);
                player.dropItem(stackDrop, true);
            }
        }
    }

    protected void pressHotbarKey(int slotNum, int button, EntityPlayer player)
    {
        SlotItemHandlerGeneric slot = this.getSlotItemHandler(slotNum);
        if (slot == null)
        {
            return;
        }

        ItemStack stackSlot = slot != null ? slot.getStack() : null;
        ItemStack stackHotbar = this.inventoryPlayer.getStackInSlot(button);

        // The stack in the slot is null or the stack size is small enough to fit into one regular stack,
        // and the stack in the hotbar is null or the stack is small enough to fit to the slot
        if ((stackSlot == null || stackSlot.stackSize <= stackSlot.getMaxStackSize()) &&
             (stackHotbar == null || stackHotbar.stackSize <= this.getMaxStackSizeFromSlotAndStack(slot, stackHotbar)) &&
             slot.canTakeStack(this.player) == true && slot.isItemValid(stackHotbar) == true)
        {
            slot.putStack(stackHotbar);
            this.inventoryPlayer.setInventorySlotContents(button, stackSlot);

            if (stackSlot != null)
            {
                slot.onPickupFromSlot(player, stackSlot);
            }
        }
        // Hotbar slot is empty, but the whole stack doesn't fit into it
        else if (stackHotbar == null && stackSlot != null)
        {
            int num = Math.min(stackSlot.getMaxStackSize(), this.inventoryPlayer.getInventoryStackLimit());
            num = Math.min(num, stackSlot.stackSize);
            stackHotbar = slot.decrStackSize(num);
            slot.onPickupFromSlot(player, stackHotbar);
            this.inventoryPlayer.setInventorySlotContents(button, stackHotbar);
        }
        // Matching items in both slots
        else if (stackHotbar != null && stackSlot != null && areItemStacksEqual(stackHotbar, stackSlot) == true)
        {
            int num = Math.min(stackHotbar.getMaxStackSize() - stackHotbar.stackSize, stackSlot.stackSize);
            // Fill the hotbar stack if it has still space
            if (num > 0)
            {
                stackHotbar.stackSize += num;
                slot.decrStackSize(num);
                slot.onPickupFromSlot(player, stackSlot);
                this.inventoryPlayer.setInventorySlotContents(button, stackHotbar);
            }
            // ... otherwise take the stack from it
            else if (slot.isItemValid(stackHotbar) == true)
            {
                this.inventoryPlayer.setInventorySlotContents(button, this.putItemsToSlot(slot, stackHotbar, stackHotbar.stackSize));
            }
        }
    }

    @Override
    public ItemStack slotClick(int slotNum, int button, ClickType type, EntityPlayer player)
    {
        //String side = this.player.worldObj.isRemote ? "client" : "server";
        //ModLogger.info(String.format("slotClick(): side: %s slotNum: %d, button: %d type: %d", side, slotNum, button, type));

        // slotNum: real button: 0 type: 0 - regular left click - on button down with empty cursor, on button up with stack in cursor
        // slotNum: real button: 1 type: 0 - regular right click - on button down with empty cursor, on button up with stack in cursor

        // slotNum: real button: 0 type: 1 - shift + left click - on button down with empty cursor, on button up with stack in cursor
        // slotNum: real button: 1 type: 1 - shift + right click - on button down with empty cursor, on button up with stack in cursor

        // slotNum: -1 button: 0 type: 0 - left click with a stack in cursor inside inventory but not on a slot - on button up
        // slotNum: -1 button: 1 type: 0 - right click with a stack in cursor inside inventory but not on a slot - on button up

        // slotNum: -999 button: 0 type: 0 - left click outside of inventory screen with stack on cursor - on button up
        // slotNum: -999 button: 1 type: 0 - right click outside of inventory screen with stack on cursor - on button up

        // slotNum: real button: 0 type: 3 - pick block on any slot - on button down with empty cursor
        // slotNum: real button: 2 type: 3 - middle click on any slot - on button down with empty cursor, on button up with stack in cursor

        // slotNum: -999 button: 1 type: 4 - (shift +) right click outside the inventory with an empty cursor - on button down

        // slotNum: real button: 0 type: 4 - pressing Q over a slot which has items - on button down
        // slotNum: real button: 1 type: 4 - pressing ctrl + Q over a slot which has items - on button down

        // slotNum: real button: 0..8 type: 2 - hotbar number key over slot - on button down, only with empty cursor

        // slotNum: real button: 0 type: 6 - left double-click with an empty cursor on a slot with items

        // slotNum: -999 button: 0 type: 5 - left click drag with stack in cursor start - after drag ends, as the first call
        // slotNum: real button: 1 type: 5 - left click drag with stack in cursor - for each slot dragged over - after drag ends, in sequence
        // slotNUm: -999 button: 2 type: 5 - left click drag with stack in cursor end - after drag ends, as the last call

        // slotNum: -999 button: 4 type: 5 - right click drag with stack in cursor start - after drag ends, as the first call
        // slotNum: real button: 5 type: 5 - right click drag with stack in cursor - for each slot dragged over - after drag ends, in sequence
        // slotNUm: -999 button: 6 type: 5 - right click drag with stack in cursor end - after drag ends, as the last call

        if (this.isDragging == true)
        {
            // End of dragging
            if (type == ClickType.QUICK_MOVE && (button == 2 || button == 6))
            {
                this.endDragging();
            }
            // This gets called for each slot that was dragged over
            else if (type == ClickType.QUICK_MOVE && (button == 1 || button == 5))
            {
                this.dragging(slotNum);
            }
        }
        // Starting a left or right click drag
        else if (type == ClickType.QUICK_MOVE && (button == 0 || button == 4))
        {
            this.startDragging(button == 4);
        }
        // Left or right click outside inventory with a stack in cursor
        else if (slotNum == -999 && type == ClickType.THROW)
        {
            // Left click outside of inventory screen - drop the stack from the cursor
            if (button == 0)
            {
                this.leftClickOutsideInventory(player);
            }
            // Right click outside of inventory screen - drop one item from the cursor
            else if (button == 1)
            {
                this.rightClickOutsideInventory(player);
            }
        }
        // Regular left click on a slot
        else if (button == 0 && type == ClickType.PICKUP)
        {
            this.leftClickSlot(slotNum, player);
        }
        // Regular right click on a slot
        else if (button == 1 && type == ClickType.PICKUP)
        {
            this.rightClickSlot(slotNum, player);
        }
        // Shift left click or shift right click on a slot, they both do the same
        else if (type == ClickType.QUICK_MOVE && (button == 0 || button == 1))
        {
            this.shiftClickSlot(slotNum, player);
        }
        // Pressing the drop key (Q) while hovering over a stack
        else if ((button == 0 || button == 1) && type == ClickType.THROW)
        {
            this.pressDropKey(slotNum, player, button == 1);
        }
        // Pressing a hotbar hotkey over a slot
        else if (type == ClickType.SWAP && button >= 0 && button <= 8)
        {
            this.pressHotbarKey(slotNum, button, player);
        }
        // Left double-click with an empty cursor over a slot with items
        else if (button == 0 && type == ClickType.PICKUP_ALL)
        {
            this.leftDoubleClickSlot(slotNum, player);
        }
        // Middle click on a slot - select the slot for swapping, or swap the contents with the selected slot
        else if (button == 2 && type == ClickType.CLONE)
        {
        	this.middleClickSlot(slotNum, player);
        }
        // Pick block on a slot - create new stack
        else if (type == ClickType.CLONE && player.capabilities.isCreativeMode && player.inventory.getItemStack() == null && slotNum >= 0)
        {
            Slot slot4 = (Slot)this.inventorySlots.get(slotNum);

            if (slot4 != null && slot4.getHasStack())
            {
                ItemStack itemstack6 = slot4.getStack().copy();
                itemstack6.stackSize = itemstack6.getMaxStackSize();
                player.inventory.setItemStack(itemstack6);
            }
        }

        //ModLogger.info("button = "+button+" mode = "+type);
        
        return null;
    }
}
