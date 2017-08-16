package alec_wam.CrystalMod.items.tools.backpack.gui;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBackpackWirelessChest extends Container {
	
    private EntityPlayer player;
    private InventoryBackpack backpackInv;
    private WirelessInventory wirelessInventory;

    public ContainerBackpackWirelessChest(InventoryBackpack backpackInv, WirelessInventory wirelessInventory)
    {
        this.backpackInv = backpackInv;
        this.wirelessInventory = wirelessInventory;
        player = backpackInv.getPlayer();
        if(wirelessInventory !=null){
        	wirelessInventory.playerUsingCount++;
        	layoutContainer(backpackInv.getPlayer().inventory, wirelessInventory, CrystalChestType.DARKIRON, 184, 204);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
    	if(BackpackUtil.getOwner(backpackInv.getBackpack()) !=null){
    		return UUIDUtils.areEqual(player.getUniqueID(), BackpackUtil.getOwner(backpackInv.getBackpack()));
    	}
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int i)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = ItemStackTools.safeCopy(itemstack1);
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
        if(wirelessInventory !=null){
        	wirelessInventory.playerUsingCount--;
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
