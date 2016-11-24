package alec_wam.CrystalMod.tiles.pipes.attachments.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageSensor;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotDisabled;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotSpecimen;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotSpecimenLegacy;
import alec_wam.CrystalMod.util.ItemStackTools;

public class ContainerAttachmentSensor extends Container {

	public ContainerAttachmentSensor(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		
		/*if(pipe.getAttachmentData(dir) == null || !(pipe.getAttachmentData(dir) instanceof AttachmentEStorageSensor)){
			return;
		}*/
		
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
		return super.slotClick(id, clickedButton, clickType, player);
    }

	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		return ItemStackTools.getEmptyStack();
	}
}
