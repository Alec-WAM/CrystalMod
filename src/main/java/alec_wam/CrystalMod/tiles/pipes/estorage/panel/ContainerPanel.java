package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageItemList;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

public class ContainerPanel extends Container implements INetworkContainer {

	public TileEntityPanel panel;
	public ContainerPanel(InventoryPlayer inventoryPlayer, TileEntityPanel inter){
		this.panel = inter;
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 39 + j * 18, 130 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventoryPlayer, i, 39 + i * 18, 188));
        }
	}
	
	@Override
	public void removeListener(IContainerListener crafting) {
	    super.removeListener(crafting);
	    if(panel.network !=null){
			panel.network.watchers.remove(this);
		}
	}
	
	@Override
	public void addListener(IContainerListener crafter){
		super.addListener(crafter);
		if(panel.network !=null){
			if(!panel.network.watchers.contains(this))
			panel.network.watchers.add(this);
		}
		if(crafter !=null && crafter instanceof EntityPlayerMP){
			sendItemsTo((EntityPlayerMP)crafter);	
		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
	    super.onContainerClosed(player);
	    if(panel.network !=null){
	    	panel.network.watchers.remove(this);
	    }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	//SLOT CLICK
	public ItemStack slotClick(int slotId, int clickedButton, ClickType mode, EntityPlayer playerIn)
    {
		//if(this instanceof ContainerPanelCrafting && (mode !=1 || clickedButton != 1))return super.slotClick(slotId, clickedButton, mode, playerIn);
		if(mode == ClickType.QUICK_MOVE && (clickedButton == 0 || clickedButton == 1)){
			if (slotId < 0)
            {
                return null;
            }

            Slot slot6 = (Slot)this.inventorySlots.get(slotId);

            if (slot6 != null && slot6.canTakeStack(playerIn))
            {
            	if(slot6 instanceof SlotCraftingWrapper){
            		if(this instanceof ContainerPanelCrafting && (clickedButton == 0)){
            			final ItemStack copy = slot6.getStack();
            			super.slotClick(slotId, clickedButton, mode, playerIn);
            			if (slot6.getStack() != null && slot6.getStack().getItem() == copy.getItem())
                        {
                            this.retrySlotClick(slotId, clickedButton, true, playerIn);
                        }
            			this.detectAndSendChanges();
            			return null;
            		}
            	}
                if(panel !=null && panel.network !=null && slot6.getStack() !=null){
                	final ItemStack copy = slot6.getStack();
        			int added = panel.network.getItemStorage().addItem(copy, false);
        			if(added > 0){
        				slot6.decrStackSize(added);
        				detectAndSendChanges();
        				if (slot6.getStack() != null && slot6.getStack().getItem() == copy.getItem())
                        {
                            this.retrySlotClick(slotId, clickedButton, true, playerIn);
                        }
        				return null;
        			}
        		}
            }
            return null;
		}
		return super.slotClick(slotId, clickedButton, mode, playerIn);
	}
	
	public void sendItemsToAll(){
		if(panel.network !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPos(), 0, panel.network.compressItems());
				for(Object crafter : listeners){
					if(crafter !=null && crafter instanceof EntityPlayerMP){
						CrystalModNetwork.sendTo(pil, (EntityPlayerMP)crafter);	
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<ItemStackData> data = Lists.newArrayList();
			for(CraftingPattern pattern : panel.network.getPatterns()){
				for(ItemStack stack : pattern.getOutputs()){
					if(stack !=null){
						ItemStack copy = stack.copy();
						copy.stackSize = 0;
						ItemStackData iData = new ItemStackData(copy, BlockPos.ORIGIN, 0);
						iData.isCrafting = true;
						data.add(iData);
					}
				}
			}
			sendCraftingItemsToAll(data);
		}
	}
	
	public void sendItemsToAll(List<ItemStackData> dataList){
		if(panel.network !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPos(), 1, EStorageNetwork.compressItems(dataList));
				for(Object crafter : listeners){
					if(crafter !=null && crafter instanceof EntityPlayerMP){
						CrystalModNetwork.sendTo(pil, (EntityPlayerMP)crafter);	
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendItemsTo(EntityPlayerMP player){
		if(panel.network !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPos(), 0, panel.network.compressItems());
				CrystalModNetwork.sendTo(pil, player);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<ItemStackData> data = Lists.newArrayList();
			/*for(ItemStack stack : panel.network.craftableItems.keySet()){
				if(stack !=null){
					ItemStack copy = stack.copy();
					copy.stackSize = 0;
					ItemStackData iData = new ItemStackData(copy, -1, BlockPos.ORIGIN, 0);
					iData.isCrafting = true;
					data.add(iData);
				}
			}*/
			for(CraftingPattern pattern : panel.network.getPatterns()){
				for(ItemStack stack : pattern.getOutputs()){
					if(stack !=null){
						ItemStack copy = stack.copy();
						copy.stackSize = 0;
						ItemStackData iData = new ItemStackData(copy, BlockPos.ORIGIN, 0);
						iData.isCrafting = true;
						data.add(iData);
					}
				}
			}
			if(data.size() > 0){
				try {
					PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPos(), 3, EStorageNetwork.compressItems(data));
					CrystalModNetwork.sendTo(pil, player);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendItemStackToNetwork(EntityPlayerMP player, int slot, ItemStackData data) {
		if(panel.network !=null && data.stack !=null){
			int added = panel.network.getItemStorage().addItem(data.stack, false);
			if(added > 0){
				if(slot < 0){
					if(player.inventory.getItemStack() !=null){
						player.inventory.getItemStack().stackSize-=added;
						if(player.inventory.getItemStack().stackSize <= 0)
						player.inventory.setItemStack(null);
					}
					
					player.updateHeldItem();
				}else{
					getSlot(slot).decrStackSize(added);
				}
			}
		}
	}
	
	public void grabItemStackFromNetwork(EntityPlayerMP player, int slot, int amount, ItemStackData data) {
		if(panel.network !=null && data !=null && data.stack !=null){
			int invSlot = -1;
			int realAmount = amount;
			if(slot < 0){
				boolean hasStack = player.inventory.getItemStack() !=null;
				realAmount = Math.min(amount, player.inventory.getInventoryStackLimit()-(hasStack ? player.inventory.getItemStack().stackSize : 0));
			}else{
				invSlot = slot;
				boolean hasStack = player.inventory.getStackInSlot(invSlot) !=null;
				realAmount = Math.min(amount, player.inventory.getInventoryStackLimit()-(hasStack ? player.inventory.getStackInSlot(invSlot).stackSize : 0));
			}
			ItemStack grabStack = data.stack.copy();
			grabStack.stackSize = realAmount;
			ItemStack removed = panel.network.getItemStorage().removeItem(grabStack, false);
			if(removed !=null){
				if(invSlot > -1){
					if(player.inventory.getStackInSlot(invSlot) == null){
						player.inventory.setInventorySlotContents(invSlot, removed);
					}else{
						ItemStack current = player.inventory.getStackInSlot(invSlot);
						current.stackSize+=removed.stackSize;
						player.inventory.setInventorySlotContents(invSlot, current);
					}
				}else{
					if(player.inventory.getItemStack() == null){
						player.inventory.setItemStack(removed);
					}else{
						ItemStack current = player.inventory.getItemStack();
						current.stackSize+=removed.stackSize;
						player.inventory.setItemStack(current);
					}
					player.updateHeldItem();
				}
			}
		}
	}

	public void sendCraftingItemsToAll(List<ItemStackData> dataList){
		if(panel.network !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPos(), 3, EStorageNetwork.compressItems(dataList));
				for(Object crafter : listeners){
					if(crafter !=null && crafter instanceof EntityPlayerMP){
						CrystalModNetwork.sendTo(pil, (EntityPlayerMP)crafter);	
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	@Override
	public void sendFluidsToAll() {}

	@Override
	public void sendFluidsToAll(List<FluidStackData> dataList) {}

	@Override
	public void sendFluidsTo(EntityPlayerMP player) {}
	
	@Override
	public EStorageNetwork getNetwork() {
		if(panel !=null)return panel.network;
		return null;
	}

}
