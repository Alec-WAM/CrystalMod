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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.api.estorage.IPanelSource;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageItemList.EnumListType;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageItemList;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

public class ContainerPanel extends Container implements INetworkContainer {

	public IPanelSource panel;
	public ContainerPanel(InventoryPlayer inventoryPlayer, IPanelSource inter){
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
	    if(panel.getNetwork() !=null){
			panel.getNetwork().watchers.remove(this);
		}
	}
	
	@Override
	public void addListener(IContainerListener crafter){
		super.addListener(crafter);
		if(panel.getNetwork() !=null){
			if(!panel.getNetwork().watchers.contains(this))
			panel.getNetwork().watchers.add(this);
		}
		if(crafter !=null && crafter instanceof EntityPlayerMP){
			sendItemsTo((EntityPlayerMP)crafter);	
		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
	    super.onContainerClosed(player);
	    if(panel.getNetwork() !=null){
	    	panel.getNetwork().watchers.remove(this);
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
                if(panel !=null && panel.getNetwork() !=null && slot6.getStack() !=null){
                	final ItemStack copy = slot6.getStack();
                	final int old = copy.stackSize;
        			ItemStack remain = panel.getNetwork().getItemStorage().addItem(copy, false);
        			int added = remain == null ? old : old - remain.stackSize;
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
		if(panel.getNetwork() !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPanelPos(), EnumListType.ITEM_ALL, panel.getNetwork().compressItems());
				for(Object crafter : listeners){
					if(crafter !=null && crafter instanceof EntityPlayerMP){
						CrystalModNetwork.sendTo(pil, (EntityPlayerMP)crafter);	
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<ItemStackData> data = Lists.newArrayList();
			for(CraftingPattern pattern : panel.getNetwork().getPatterns()){
				for(ItemStack stack : pattern.getOutputs()){
					if(stack !=null){
						ItemStack copy = stack.copy();
						copy.stackSize = 0;
						ItemStackData iData = new ItemStackData(copy);
						iData.isCrafting = true;
						data.add(iData);
					}
				}
			}
			sendCraftingItemsToAll(data);
		}
	}
	
	public void sendItemsToAll(List<ItemStackData> dataList){
		if(panel.getNetwork() !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPanelPos(), EnumListType.ITEM_ALL, EStorageNetwork.compressItems(dataList));
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
		if(panel.getNetwork() !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPanelPos(), EnumListType.ITEM, panel.getNetwork().compressItems());
				CrystalModNetwork.sendTo(pil, player);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<ItemStackData> data = Lists.newArrayList();
			for(CraftingPattern pattern : panel.getNetwork().getPatterns()){
				for(ItemStack stack : pattern.getOutputs()){
					if(stack !=null){
						ItemStack copy = stack.copy();
						copy.stackSize = 0;
						ItemStackData iData = new ItemStackData(copy);
						iData.isCrafting = true;
						data.add(iData);
					}
				}
			}
			if(data.size() > 0){
				try {
					PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPanelPos(), EnumListType.CRAFTING, EStorageNetwork.compressItems(data));
					CrystalModNetwork.sendTo(pil, player);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendItemStackToNetwork(EntityPlayerMP player, int slot, ItemStackData data) {
		if(panel.getNetwork() !=null && data.stack !=null){
			ItemStack insertStack = data.stack.copy();
			final int old = insertStack.stackSize;
			ItemStack remain = panel.getNetwork().getItemStorage().addItem(data.stack, false);
			int added = remain == null ? old : old - remain.stackSize;
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
		if(panel.getNetwork() !=null && data !=null && data.stack !=null){
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
			ItemStack grabStack = ItemHandlerHelper.copyStackWithSize(data.stack, 1);
			ItemStack removed = panel.getNetwork().getItemStorage().removeItem(grabStack, realAmount, ItemStorage.NORMAL, false);
			if(removed !=null){
				if(invSlot > -1){
					ItemStack remainder = ItemHandlerHelper.insertItem(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP), removed, false);

	                if (remainder != null) {
	                	panel.getNetwork().getItemStorage().addItem(remainder, false);
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
		if(panel.getNetwork() !=null){
			try {
				PacketEStorageItemList pil = new PacketEStorageItemList(panel.getPanelPos(), EnumListType.CRAFTING, EStorageNetwork.compressItems(dataList));
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
		if(panel !=null)return panel.getNetwork();
		return null;
	}

}
