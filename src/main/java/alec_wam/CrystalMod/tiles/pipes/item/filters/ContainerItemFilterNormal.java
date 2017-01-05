package alec_wam.CrystalMod.tiles.pipes.item.filters;

import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.pipes.item.ContainerItemPipe.SlotGhostItem;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class ContainerItemFilterNormal extends Container implements IMessageHandler {

	private InventoryPlayer playerInv;
	private final FilterInventory filterInventory;
	public EnumHand hand;
	
	public ContainerItemFilterNormal(EntityPlayer player, ItemStack stack, EnumHand hand){
		this.playerInv = player.inventory;
		this.hand = hand;
		if(stack.getMetadata() == FilterType.NORMAL.ordinal()){
			this.filterInventory = new FilterInventory(stack, 10, "");
			for (int i = 0; i < 2; i++)
	        {
	            for (int j = 0; j < 5; j++)
	            {
	                addSlotToContainer(new SlotGhostItem(filterInventory, j + i * 3, 45 + j * 18, 14 + i * 18));
	            }
	        }
		} else if(stack.getMetadata() == FilterType.MOD.ordinal()){
			filterInventory = new FilterInventory(stack, 3, "");
			
	        addSlotToContainer(new SlotGhostItem(filterInventory, 0, 45 + 0 * 18, 14 + 0 * 18));
	        addSlotToContainer(new SlotGhostItem(filterInventory, 1, 45 + 0 * 18, 14 + 1 * 18));
	        addSlotToContainer(new SlotGhostItem(filterInventory, 2, 45 + 0 * 18, 14 + 2 * 18));
		} else {
			filterInventory = new FilterInventory(stack, 0, "");
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
								if (ItemStackTools.isNullStack(heldStack) && !ItemStackTools.isNullStack(slotStack))
								{
									GhostItemHelper.incrementGhostAmout(slotStack, 1);
									slot.putStack(slotStack);
								} else if (!ItemStackTools.isNullStack(heldStack))
								{
									if (!((SlotGhostItem) slot).canBeAccessed())
									{
										return super.slotClick(slotId, clickedButton, mode, player);
									}
									if (!ItemStackTools.isNullStack(slotStack) && ItemUtil.canCombine(slotStack, heldStack))
									{
										GhostItemHelper.incrementGhostAmout(slotStack, ItemStackTools.getStackSize(heldStack));
										slot.putStack(slotStack);
									} else
									{
										ItemStack copyStack = heldStack.copy();
										GhostItemHelper.setItemGhostAmount(copyStack, ItemStackTools.getStackSize(copyStack));
										ItemStackTools.setStackSize(copyStack, 1);
										slot.putStack(copyStack);
									}
								}
							} else
							{
								if (!ItemStackTools.isNullStack(slotStack))
								{
									GhostItemHelper.setItemGhostAmount(slotStack, GhostItemHelper.getItemGhostAmount(slotStack) / 2);
									if (GhostItemHelper.getItemGhostAmount(slotStack) <= 0)
									{
										slot.putStack(ItemStackTools.getEmptyStack());
									} else
									{
										slot.putStack(slotStack);
									}
								}
							}
						} else
						{
							if (clickedButton == 0)
							{
								if (!ItemStackTools.isNullStack(slotStack))
								{
									GhostItemHelper.decrementGhostAmount(slotStack, 1);
									if (GhostItemHelper.getItemGhostAmount(slotStack) < 0)
									{
										slot.putStack(ItemStackTools.getEmptyStack());
									} else
									{
										slot.putStack(slotStack);
									}
								}
							} else
							{
								slot.putStack(ItemStackTools.getEmptyStack());
							}
						}
					}
				}
			}
		}

		return super.slotClick(slotId, clickedButton, mode, player);
	}
	
	@Override
    public void onContainerClosed(EntityPlayer player){
        super.onContainerClosed(player);
        
        ItemStack held = playerInv.player.getHeldItem(hand);
        if(ItemStackTools.isValid(held))filterInventory.writeToStack(held);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("Settings")){
			ItemStack filter = filterInventory.getMasterStack();
			if(ItemStackTools.isValid(filter)){
				if(messageData.hasKey("BlackList")){
					ItemNBTHelper.setBoolean(filter, "BlackList", messageData.getBoolean("BlackList"));
				}
				if(messageData.hasKey("Meta")){
					ItemNBTHelper.setBoolean(filter, "MetaMatch", messageData.getBoolean("Meta"));
				}
				if(messageData.hasKey("NBTMatch")){
					ItemNBTHelper.setBoolean(filter, "NBTMatch", messageData.getBoolean("NBTMatch"));
				}
				if(messageData.hasKey("Ore")){
					ItemNBTHelper.setBoolean(filter, "OreMatch", messageData.getBoolean("Ore"));
				}
			}
		}
	}
}
