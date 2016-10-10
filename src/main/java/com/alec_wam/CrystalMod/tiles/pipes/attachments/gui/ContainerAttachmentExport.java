package com.alec_wam.CrystalMod.tiles.pipes.attachments.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageExport;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotDisabled;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotSpecimen;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotSpecimenLegacy;

public class ContainerAttachmentExport extends Container {

	public ContainerAttachmentExport(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		
		if(pipe.getAttachmentData(dir) == null || !(pipe.getAttachmentData(dir) instanceof AttachmentEStorageExport)){
			return;
		}
		AttachmentEStorageExport attachment = (AttachmentEStorageExport) pipe.getAttachmentData(dir);
		
		for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                addSlotToContainer(new SlotSpecimen(attachment.filters, j + i * 3, 45 + j * 18, 14 + i * 18));
            }
        }
		
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
    public ItemStack slotClick(int id, int clickedButton, ClickType clickType, EntityPlayer player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        if (slot instanceof SlotSpecimen) {
            if (((SlotSpecimen) slot).isWithSize()) {
                if (slot.getStack() != null) {
                    if (GuiScreen.isShiftKeyDown()) {
                        slot.putStack(null);
                    } else {
                        int amount = slot.getStack().stackSize;

                        if (clickedButton == 0) {
                            amount--;

                            if (amount < 1) {
                                amount = 1;
                            }
                        } else if (clickedButton == 1) {
                            amount++;

                            if (amount > 64) {
                                amount = 64;
                            }
                        }

                        slot.getStack().stackSize = amount;
                    }
                } else if (player.inventory.getItemStack() != null) {
                    int amount = player.inventory.getItemStack().stackSize;

                    if (clickedButton == 1) {
                        amount = 1;
                    }

                    ItemStack toPut = player.inventory.getItemStack().copy();
                    toPut.stackSize = amount;

                    slot.putStack(toPut);
                }
            } else if (player.inventory.getItemStack() == null) {
                slot.putStack(null);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotSpecimenLegacy) {
            if (player.inventory.getItemStack() == null) {
                slot.putStack(null);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotDisabled) {
            return null;
        }

        return super.slotClick(id, clickedButton, clickType, player);
    }

	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		return null;
	}
}
