package com.alec_wam.CrystalMod.tiles.pipes.attachments.gui;

import com.alec_wam.CrystalMod.network.CrystalModNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageImport;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import com.alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import com.alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import com.alec_wam.CrystalMod.tiles.pipes.item.filters.FilterInventory;
import com.alec_wam.CrystalMod.tiles.pipes.item.filters.IItemStackInventory;
import com.alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter;
import com.alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import com.alec_wam.CrystalMod.util.ItemUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ContainerAttachmantImport extends Container {

	public class SlotGhostItem extends Slot {
		private IItemStackInventory itemInv;
		public final int xPos;
		public final int yPos;

        public SlotGhostItem(IItemStackInventory inventory, int slotIndex, int x, int y)
        {
            super(inventory, slotIndex, x, y);
            itemInv = inventory;
            xPos = x;
            yPos = y;
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return false;
        }	

        public boolean canBeAccessed()
        {
            return itemInv.canInventoryBeManipulated();
        }
	}

	public class SlotItemFilter extends Slot {

		public ContainerAttachmantImport container;
		
		public SlotItemFilter(ContainerAttachmantImport containerItemPipe, IInventory filterInv, int i, int j, int k) {
			super(filterInv, i, j, k);
			container = containerItemPipe;
		}
		
		@Override
        public boolean isItemValid(ItemStack itemStack)
        {
            return itemStack.getItem() instanceof ItemPipeFilter;
        }

        @Override
        public void onSlotChanged()
        {
            super.onSlotChanged();
            container.resetItemInventory(getStack());
            if(!filterInventory.hasGhostSlots())return;
            for (int i = 1; i <= this.inventory.getSizeInventory()+1; i++)
            {
                Slot slot = container.getSlot(i);
                slot.onSlotChanged();
            }
        }

	}
	
	private final IItemStackInventory filterInventory;
	private final EnumFacing dir;
	private final TileEntityPipeEStorage pipe;
	
	public ContainerAttachmantImport(EntityPlayer inventoryPlayer, TileEntityPipeEStorage pipe, EnumFacing dir) {
		this.dir = dir;
		this.pipe = pipe;
		
		if(pipe.getAttachmentData(dir) == null || !(pipe.getAttachmentData(dir) instanceof AttachmentEStorageImport)){
			filterInventory = new FilterInventory(null, 0, "");
			return;
		}
		AttachmentEStorageImport attachment = (AttachmentEStorageImport) pipe.getAttachmentData(dir);
		
		this.addSlotToContainer(new SlotItemFilter(this, attachment.filters, 0, 8, 14));
        ItemStack masterStack = attachment.getFilter();
        if(masterStack !=null && masterStack.getMetadata() == FilterType.NORMAL.ordinal()){
	        filterInventory = new FilterInventory(masterStack, 10, "");
	
	        for (int i = 0; i < 2; i++)
	        {
	            for (int j = 0; j < 5; j++)
	            {
	                addSlotToContainer(new SlotGhostItem(filterInventory, j + i * 3, 45 + j * 18, 14 + i * 18));
	            }
	        }
        }
        else filterInventory = new FilterInventory(masterStack, 0, "");

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventoryPlayer.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventoryPlayer.inventory, i, 8 + i * 18, 142));
        }
        //setGhostVisible(true);
	}

	public void resetItemInventory(ItemStack masterStack)
    {
        filterInventory.initializeInventory(masterStack);
    }
	
	public void setGhostVisible(boolean vis){
		if(!filterInventory.hasGhostSlots())return;
		for(int i = 1; i <= filterInventory.getSizeInventory()+1; i++){
			Slot slot = this.getSlot(i);
			if(slot instanceof SlotGhostItem){
	        	SlotGhostItem ghost = (SlotGhostItem)slot;
	        	if(!vis){
	        		ghost.xDisplayPosition = -3000;
	        		ghost.yDisplayPosition = -3000;
	        	}else{
	        		ghost.xDisplayPosition = ghost.xPos;
	        		ghost.yDisplayPosition = ghost.yPos;
	        	}
	        }
		}
	}
	
	 @Override
	    public ItemStack slotClick(int slotId, int clickedButton, ClickType mode, EntityPlayer player)
	    {
	        InventoryPlayer inventoryPlayer = player.inventory;
//	        if (!player.worldObj.isRemote)
	        {
	            if (slotId >= 0)
	            {
	                Slot slot = this.inventorySlots.get(slotId);

	                if (slot instanceof SlotGhostItem)
	                {
	                    if ((mode == ClickType.PICKUP || mode == ClickType.QUICK_MOVE) && (clickedButton == 0 || clickedButton == 1))
	                    {
	                        ItemStack slotStack = slot.getStack();
	                        ItemStack heldStack = inventoryPlayer.getItemStack();

	                        if (mode == ClickType.PICKUP)
	                        {
	                            if (clickedButton == 0)
	                            {
	                                if (heldStack == null && slotStack != null)
	                                {
	                                    GhostItemHelper.incrementGhostAmout(slotStack, 1);
	                                    slot.putStack(slotStack);
	                                    CrystalModNetwork.sendToAllAround(new PacketPipe(pipe, "FilterGhost", dir, slot+";"+GhostItemHelper.getItemGhostAmount(slotStack)), pipe);
	                                } else if (heldStack != null)
	                                {
	                                    if (!((SlotGhostItem) slot).canBeAccessed())
	                                    {
	                                        return super.slotClick(slotId, clickedButton, mode, player);
	                                    }
	                                    if (slotStack != null && ItemUtil.canCombine(slotStack, heldStack))
	                                    {
	                                        GhostItemHelper.incrementGhostAmout(slotStack, heldStack.stackSize);
	                                        slot.putStack(slotStack);
	                                        CrystalModNetwork.sendToAllAround(new PacketPipe(pipe, "FilterGhost", dir, slot+";"+GhostItemHelper.getItemGhostAmount(slotStack)), pipe);
	                                    } else
	                                    {
	                                        ItemStack copyStack = heldStack.copy();
	                                        GhostItemHelper.setItemGhostAmount(copyStack, copyStack.stackSize);
	                                        copyStack.stackSize = 1;
	                                        slot.putStack(copyStack);
	                                        CrystalModNetwork.sendToAllAround(new PacketPipe(pipe, "FilterGhost", dir, slot+";"+GhostItemHelper.getItemGhostAmount(copyStack)), pipe);
	                                    }
	                                }
	                            } else
	                            {
	                                if (slotStack != null)
	                                {
	                                    GhostItemHelper.setItemGhostAmount(slotStack, GhostItemHelper.getItemGhostAmount(slotStack) / 2);
	                                    if (GhostItemHelper.getItemGhostAmount(slotStack) <= 0)
	                                    {
	                                        slot.putStack(null);
	                                        CrystalModNetwork.sendToAllAround(new PacketPipe(pipe, "FilterGhost", dir, slot+";-1"), pipe);
	                                    } else
	                                    {
	                                        slot.putStack(slotStack);
	                                        CrystalModNetwork.sendToAllAround(new PacketPipe(pipe, "FilterGhost", dir, slot+";"+GhostItemHelper.getItemGhostAmount(slotStack)), pipe);
	                                    }
	                                }
	                            }
	                        } else
	                        {
	                            if (clickedButton == 0)
	                            {
	                                if (slotStack != null)
	                                {
	                                    GhostItemHelper.decrementGhostAmount(slotStack, 1);
	                                    if (GhostItemHelper.getItemGhostAmount(slotStack) < 0)
	                                    {
	                                        slot.putStack(null);
	                                        CrystalModNetwork.sendToAllAround(new PacketPipe(pipe, "FilterGhost", dir, slot+";-1"), pipe);
	                                    } else
	                                    {
	                                        slot.putStack(slotStack);
	                                        CrystalModNetwork.sendToAllAround(new PacketPipe(pipe, "FilterGhost", dir, slot+";"+GhostItemHelper.getItemGhostAmount(slotStack)), pipe);
	                                    }
	                                }
	                            } else
	                            {
	                                slot.putStack(null);
	                            }
	                        }
	                    }
	                }
	            }
	        }

	        return super.slotClick(slotId, clickedButton, mode, player);
	    }
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		return null;
	}

}
