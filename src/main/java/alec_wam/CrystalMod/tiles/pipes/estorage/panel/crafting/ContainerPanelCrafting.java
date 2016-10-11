package alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting;

import alec_wam.CrystalMod.tiles.pipes.estorage.panel.ContainerPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.SlotCraftingPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.SlotCraftingPanelResult;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPanelCrafting extends ContainerPanel {

	public ContainerPanelCrafting(InventoryPlayer inventoryPlayer, TileEntityPanel inter) {
		super(inventoryPlayer, inter);
		this.inventorySlots.clear();
		inventoryItemStacks.clear();
		this.panel = inter;
		
		TileEntityPanelCrafting craft = (TileEntityPanelCrafting)inter;
		addSlotToContainer(new SlotCraftingPanelResult(this, inventoryPlayer.player, craft,
		        9, 157, 128));
		    
		for (int y = 0, i = 0; y < 3; y++) {
	      for (int x = 0; x < 3; x++, i++) {
	        addSlotToContainer(new SlotCraftingPanel(craft.getMatrix(), i, 63 + x * 18, 110 + y * 18));
	      }
	    }
		
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 39 + j * 18, 174 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventoryPlayer, i, 39 + i * 18, 232));
        }
	}
	
	public void onContainerClosed(EntityPlayer player) {
	    super.onContainerClosed(player);
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inv) {
	    //InventoryCrafting tmp = new InventoryCrafting(new ContainerNull(), 3, 3);

	    if(panel instanceof TileEntityPanelCrafting){
		    ((TileEntityPanelCrafting) panel).onCraftingMatrixChanged();
	    }
	}
	
	@Override
	public boolean canMergeSlot(ItemStack par1, Slot slot) {
	    return !(slot instanceof SlotCraftingPanelResult) && super.canMergeSlot(par1, slot);
	}
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
		ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 9)
            {
            	
                if (!this.mergeItemStack(itemstack1, 10, 46, false))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

	public void sendCraftingSlots() {
        for (int i = 0; i < inventorySlots.size(); ++i) {
            Slot slot = inventorySlots.get(i);

            if (slot instanceof SlotCraftingPanel || slot instanceof SlotCraftingPanelResult) {
                for (int j = 0; j < listeners.size(); ++j) {
                	listeners.get(j).sendSlotContents(this, i, slot.getStack());
                }
            }
        }
    }

}
