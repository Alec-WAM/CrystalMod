package alec_wam.CrystalMod.tiles.chests.wireless;

import alec_wam.CrystalMod.tiles.chests.metal.MetalCrystalChestType;
import alec_wam.CrystalMod.tiles.chests.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerWirelessChest extends Container {
    private EntityPlayer player;
    private IWirelessChestSource chest;

    public ContainerWirelessChest(IInventory playerInventory, IWirelessChestSource chest)
    {
        this.chest = chest;
        player = ((InventoryPlayer) playerInventory).player;
        if(chest.getInventory() !=null){
        	chest.getInventory().playerUsingCount++;
        	layoutContainer(playerInventory, chest.getInventory(), MetalCrystalChestType.DARKIRON, 184, 204);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return chest.isPrivate() ? chest.isOwner(player.getUniqueID()) : true;
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
            if (i < MetalCrystalChestType.DARKIRON.size)
            {
                if (!mergeItemStack(itemstack1, MetalCrystalChestType.DARKIRON.size, inventorySlots.size(), true))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!mergeItemStack(itemstack1, 0, MetalCrystalChestType.DARKIRON.size, false))
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
        if(chest.getInventory() !=null){
        	chest.getInventory().playerUsingCount--;
        }
    }

    protected void layoutContainer(IInventory playerInventory, WirelessInventory chestInventory, MetalCrystalChestType type, int xSize, int ySize)
    {
    	int index = 0;
        for (int chestRow = 0; chestRow < 5; chestRow++)
        {
            for (int chestCol = 0; chestCol < 9; chestCol++)
            {
                addSlot(new SlotItemHandler(chestInventory, index, 12 + chestCol * 18, 18 + chestRow * 18));
                index++;
            }
        }

        int inventoryY = 132;
        int leftCol = (xSize - 162) / 2 + 1;
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++)
        {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++)
            {
                addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, inventoryY + (playerInvRow) * 18
                        - 10));
            }

        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, ySize - 24));
        }
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }
}
