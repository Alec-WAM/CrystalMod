package alec_wam.CrystalMod.items.tools.blockholder.advanced;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.tools.blockholder.advanced.ItemAdvancedBlockHolder.BlockStackData;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.network.packets.PacketItemNBT;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerAdvancedBlockHolder extends Container implements IMessageHandler 
{
    public ItemStack blockHolder;

    public ContainerAdvancedBlockHolder(IInventory playerInventory, ItemStack blockHolder)
    {
        this.blockHolder = blockHolder;
        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 90 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 148));
        }
    }
    
    @Override
	public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }
    
    //Prevent Moving the BlockHolder that is open
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
    	if(slotId >=0){
    		Slot slot = getSlot(slotId);
    		if(slot !=null){
    			if(slot.getHasStack()){
    				ItemStack slotStack = slot.getStack();
    				if(ItemUtil.canCombine(blockHolder, slotStack)){
    					return ItemStackTools.getEmptyStack();
    				}	
    		    	if(clickTypeIn == ClickType.QUICK_MOVE && (dragType == 0 || dragType == 1)){
    		    		BlockStackData currentData = ItemAdvancedBlockHolder.getSelectedData(blockHolder);
    		    		ItemStack blockStack = currentData.stack;
    		            if(ItemStackTools.isValid(blockStack)){
    		            	if(ItemUtil.canCombine(blockStack, slotStack)){
    		            		int added = ItemAdvancedBlockHolder.addBlocks(blockHolder, ItemAdvancedBlockHolder.getSelection(blockHolder), ItemStackTools.getStackSize(slotStack));
    		            		if(added > 0){
    		            			slot.decrStackSize(added);
    		        				detectAndSendChanges();
    		        				if (ItemStackTools.isValid(slot.getStack()) && slot.getStack().getItem() == slotStack.getItem())
    		                        {
    		                            this.retrySlotClick(slotId, dragType, true, player);
    		                        }
    		            		}
    		            	}
    		            }
    		            return ItemStackTools.getEmptyStack();
    		    	}
    			}
    		}    		  		
    	}
    	
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Override
	@Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        /*Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            ItemStack blockStack = ItemBlockHolder.getBlockStack(blockHolder);
            if(ItemStackTools.isValid(blockStack)){
            	if(ItemUtil.canCombine(blockStack, itemstack1)){
            		int added = ItemBlockHolder.addBlocks(blockHolder, ItemStackTools.getStackSize(itemstack1));
            		if(added > 0){
            			slot.putStack(ItemStackTools.incStackSize(itemstack1, -added));
            			slot.onSlotChanged();
            			return ItemStackTools.getEmptyStack();
            		}
            	}
            }
        }*/

        return itemstack;
    }

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateStack")){
			blockHolder = ItemStackTools.loadFromNBT(messageData.getCompoundTag("Stack"));
		}
		if(messageId.equalsIgnoreCase("ShiftTransfer")){
			BlockStackData currentData = ItemAdvancedBlockHolder.getSelectedData(blockHolder);
    		ItemStack blockStack = currentData.stack;
			if(ItemStackTools.isValid(blockStack)){
				int size = Math.min(64, currentData.count);
				ItemStack copy = ItemUtil.copy(blockStack, size);
				if(!this.mergeItemStack(copy, 0, 9, false)){
					this.mergeItemStack(copy, 9, 36, false);
				}
				if(ItemStackTools.getStackSize(copy) < size){
					int removed = size - ItemStackTools.getStackSize(copy);
					ItemAdvancedBlockHolder.removeBlocks(blockHolder, ItemAdvancedBlockHolder.getSelection(blockHolder), removed);
					for(IContainerListener listener : this.listeners){
						if(listener instanceof EntityPlayerMP){
							EntityPlayerMP player = (EntityPlayerMP)listener;
							CrystalModNetwork.sendTo(new PacketItemNBT(player.inventory.currentItem, ItemNBTHelper.getCompound(blockHolder)), player);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setTag("Stack", blockHolder.writeToNBT(new NBTTagCompound()));
							CrystalModNetwork.sendTo(new PacketGuiMessage("UpdateStack", nbt), player);
						}
					}
				}
			}
		}
	}
}