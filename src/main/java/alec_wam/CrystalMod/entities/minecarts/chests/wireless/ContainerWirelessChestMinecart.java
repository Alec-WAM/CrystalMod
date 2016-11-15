package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerWirelessChestMinecart extends Container {
	
    private EntityPlayer player;
    private EntityWirelessChestMinecart minecart;

    public ContainerWirelessChestMinecart(IInventory playerInventory, EntityWirelessChestMinecart minecart)
    {
        this.minecart = minecart;
        player = ((InventoryPlayer) playerInventory).player;
        if(minecart.getInventory() !=null){
        	minecart.getInventory().playerUsingCount++;
        	layoutContainer(playerInventory, minecart.getInventory(), CrystalChestType.DARKIRON, 184, 204);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return minecart.isOwner(player.getUniqueID());
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i < CrystalChestType.DARKIRON.size)
            {
                if (!mergeItemStack(itemstack1, CrystalChestType.DARKIRON.size, inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(itemstack1, 0, CrystalChestType.DARKIRON.size, false))
            {
                return null;
            }
            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
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
        if(minecart.getInventory() !=null){
        	minecart.getInventory().playerUsingCount--;
        }
    }

    protected void layoutContainer(IInventory playerInventory, WirelessInventory chestInventory, CrystalChestType type, int xSize, int ySize)
    {
        for (int chestRow = 0; chestRow < 5; chestRow++)
        {
            for (int chestCol = 0; chestCol < 9; chestCol++)
            {
                addSlotToContainer(new SlotItemHandler(chestInventory, chestCol + chestRow * 5, 12 + chestCol * 18, 18 + chestRow * 18));
            }
        }

        int inventoryY = 132;
        int leftCol = (xSize - 162) / 2 + 1;
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++)
        {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++)
            {
                addSlotToContainer(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, inventoryY + (playerInvRow) * 18
                        - 10));
            }

        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            addSlotToContainer(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, ySize - 24));
        }
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }
}
