package alec_wam.CrystalMod.tiles.chests.metal;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMetalCrystalChest extends Container {
    private MetalCrystalChestType type;
    private EntityPlayer player;
    private IInventory chest;

    public ContainerMetalCrystalChest(IInventory playerInventory, IInventory chestInventory, MetalCrystalChestType type, int xSize, int ySize)
    {
        chest = chestInventory;
        player = ((InventoryPlayer) playerInventory).player;
        this.type = type;
        chestInventory.openInventory(player);
        
        for (int chestRow = 0; chestRow < type.getRowCount(); chestRow++)
        {
            for (int chestCol = 0; chestCol < type.getRowLength(); chestCol++)
            {
                addSlot(new Slot(chestInventory, chestCol + chestRow * type.getRowLength(), 12 + chestCol * 18, 8 + chestRow * 18));
            }
        }

        int leftCol = (xSize - 162) / 2 + 1;
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++)
        {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++)
            {
                addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, ySize - (4 - playerInvRow) * 18 - 10));
            }

        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, ySize - 24));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return chest.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int i)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i < type.size)
            {
                if (!mergeItemStack(itemstack1, type.size, inventorySlots.size(), true))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!mergeItemStack(itemstack1, 0, type.size, false))
            {
                return ItemStackTools.getEmptyStack();
            }
            if (ItemStackTools.isEmpty(itemstack1))
            {
                slot.putStack(ItemStackTools.getEmptyStack());
            }
            else
            {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);
        chest.closeInventory(entityplayer);
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }
    
    public int getNumColumns() {
        return type.getRowLength();
    }
}
