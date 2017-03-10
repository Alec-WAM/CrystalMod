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
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
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
	@Override
	public ItemStack slotClick(int slotId, int clickedButton, ClickType mode, EntityPlayer playerIn)
    {
		//if(this instanceof ContainerPanelCrafting && (mode !=1 || clickedButton != 1))return super.slotClick(slotId, clickedButton, mode, playerIn);
		if(mode == ClickType.QUICK_MOVE && (clickedButton == 0 || clickedButton == 1)){
			if (slotId < 0)
            {
                return ItemStackTools.getEmptyStack();
            }

            Slot slot6 = (Slot)this.inventorySlots.get(slotId);

            if (slot6 != null && slot6.canTakeStack(playerIn))
            {
            	if(slot6 instanceof SlotCraftingWrapper){
            		if(this instanceof ContainerPanelCrafting && (clickedButton == 0)){
            			final ItemStack copy = slot6.getStack();
            			super.slotClick(slotId, clickedButton, mode, playerIn);
            			if (ItemStackTools.isValid(slot6.getStack()) && slot6.getStack().getItem() == copy.getItem())
                        {
                            this.retrySlotClick(slotId, clickedButton, true, playerIn);
                        }
            			this.detectAndSendChanges();
            			return ItemStackTools.getEmptyStack();
            		}
            	}
                if(panel !=null && panel.getNetwork() !=null && ItemStackTools.isValid(slot6.getStack())){
                	final ItemStack copy = slot6.getStack();
                	final int old = ItemStackTools.getStackSize(copy);
        			ItemStack remain = panel.getNetwork().getItemStorage().addItem(copy, false);
        			int added = ItemStackTools.isEmpty(remain) ? old : old - ItemStackTools.getStackSize(remain);
        			if(added > 0){
        				slot6.decrStackSize(added);
        				detectAndSendChanges();
        				if (ItemStackTools.isValid(slot6.getStack()) && slot6.getStack().getItem() == copy.getItem())
                        {
                            this.retrySlotClick(slotId, clickedButton, true, playerIn);
                        }
        				return ItemStackTools.getEmptyStack();
        			}
        		}
            }
            return ItemStackTools.getEmptyStack();
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
					if(!ItemStackTools.isNullStack(stack)){
						ItemStack copy = stack.copy();
						//Dont set empty because it updates the actual empty state
						ItemStackTools.setStackSize(copy, 0);
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
					if(ItemStackTools.isValid(stack)){
						ItemStack copy = stack.copy();
						//Dont set empty because it updates the actual empty state
						ItemStackTools.setStackSize(copy, 0);
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
		if(panel.getNetwork() !=null && ItemStackTools.isValid(data.stack)){
			ItemStack insertStack = data.stack.copy();
			final int old = ItemStackTools.getStackSize(insertStack);
			ItemStack remain = panel.getNetwork().getItemStorage().addItem(data.stack, false);
			int added = ItemStackTools.isEmpty(remain) ? old : old - ItemStackTools.getStackSize(remain);
			if(added > 0){
				if(slot < 0){
					if(ItemStackTools.isValid(player.inventory.getItemStack())){
						ItemStackTools.incStackSize(player.inventory.getItemStack(), -added);
						if(ItemStackTools.isEmpty(player.inventory.getItemStack()))
							player.inventory.setItemStack(ItemStackTools.getEmptyStack());
					}
					
					player.updateHeldItem();
				}else{
					getSlot(slot).decrStackSize(added);
				}
			}
		}
	}
	
	public void grabItemStackFromNetwork(EntityPlayerMP player, int slot, int amount, ItemStackData data) {
		if(panel.getNetwork() !=null && data !=null && ItemStackTools.isValid(data.stack)){
			int invSlot = -1;
			ItemStack grabStack = ItemStackTools.safeCopy(data.stack);
			int realAmount = amount;
			if(slot < 0){
				realAmount = Math.min(amount, player.inventory.getInventoryStackLimit()-ItemStackTools.getStackSize(player.inventory.getItemStack()));
			}else{
				invSlot = slot;
				ItemStack testStack = ItemUtil.copy(grabStack, amount);
				ItemStack remainder = ItemHandlerHelper.insertItem(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP), testStack, true);
				realAmount = ItemStackTools.getStackSize(testStack)-ItemStackTools.getStackSize(remainder);
			}
			ItemStack removed = panel.getNetwork().getItemStorage().removeItem(grabStack, realAmount, ItemStorage.NORMAL, false);
			if(ItemStackTools.isValid(removed)){
				if(invSlot > -1){
					ItemStack remainder = ItemHandlerHelper.insertItem(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP), removed, false);

	                if (ItemStackTools.isValid(remainder)) {
	                	panel.getNetwork().getItemStorage().addItem(remainder, false);
	                }
				}else{
					if(ItemStackTools.isEmpty(player.inventory.getItemStack())){
						player.inventory.setItemStack(removed);
					}else{
						ItemStack current = player.inventory.getItemStack();
						ItemStackTools.incStackSize(current, ItemStackTools.getStackSize(removed));
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
