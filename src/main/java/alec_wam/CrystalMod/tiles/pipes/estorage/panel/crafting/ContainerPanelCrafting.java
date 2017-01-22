package alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting;

import alec_wam.CrystalMod.tiles.pipes.estorage.panel.ContainerPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.PanelSourceNormal;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.SlotCraftingPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.SlotCraftingPanelResult;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPanelCrafting extends ContainerPanel {

	public TileEntityPanel panelTile;
	
	public ContainerPanelCrafting(InventoryPlayer inventoryPlayer, TileEntityPanel inter) {
		super(inventoryPlayer, new PanelSourceNormal(inter));
		panelTile = inter;
		this.inventorySlots.clear();
		inventoryItemStacks.clear();
		
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

	    if(panelTile instanceof TileEntityPanelCrafting){
		    ((TileEntityPanelCrafting) panelTile).onCraftingMatrixChanged();
	    }
	}
	
	@Override
	public boolean canMergeSlot(ItemStack par1, Slot slot) {
	    return !(slot instanceof SlotCraftingPanelResult) && super.canMergeSlot(par1, slot);
	}
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
		ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 9)
            {
            	
                if (!this.mergeItemStack(itemstack1, 10, 46, false))
                {
                    return ItemStackTools.getEmptyStack();
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
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

            if (ItemStackTools.getStackSize(itemstack1) == ItemStackTools.getStackSize(itemstack))
            {
                return ItemStackTools.getEmptyStack();
            }

            slot.onTake(playerIn, itemstack1);
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
