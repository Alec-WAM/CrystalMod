package alec_wam.CrystalMod.tiles.pipes.item;

import alec_wam.CrystalMod.client.gui.SlotGhostItem;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;

public class ContainerPipeFilter extends Container
{
    private ItemStack filter;
    private EnumHand hand;
    private InventoryBasic filterInventory;
    
    public ContainerPipeFilter(EntityPlayer player, ItemStack stack, EnumHand hand)
    {
    	this.filter = stack;
    	this.hand = hand;
    	this.filterInventory = new InventoryBasic(new TextComponentString("Filter"), 10);
    	NonNullList<ItemStack> stacks = TileEntityPipeItem.loadFilterStacks(filter);
    	for(int i = 0; i < 10; i++){
    		filterInventory.setInventorySlotContents(i, stacks.get(i));
    	}
    	
    	for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                addSlot(new SlotGhostItem(filterInventory, j + i * 3, 62 + j * 18, 19 + i * 18));
            }
        }
    	
        this.addPlayerInventory(player.inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    protected void addPlayerInventory(InventoryPlayer paramInventoryPlayer)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(paramInventoryPlayer, j + i * 9 + 9, 8 + j * 18, 62 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(paramInventoryPlayer, i, 8 + i * 18, 120));
    }

    @Override
    public void onContainerClosed(EntityPlayer player){
        super.onContainerClosed(player);
        
        ItemStack held = player.getHeldItem(hand);
        if(ItemStackTools.isValid(held)){
        	NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);
        	for(int i = 0; i < 10; i++)items.set(i, filterInventory.getStackInSlot(i));
        	NBTTagCompound inv = new NBTTagCompound();
        	ItemStackHelper.saveAllItems(inv, items);
        	ItemNBTHelper.getCompound(held).setTag("FilterItems", inv);
        }
	}
    
    @Override
	public ItemStack slotClick(int slotId, int clickedButton, ClickType mode, EntityPlayer player)
	{
    	InventoryPlayer inventoryPlayer = player.inventory;
    	if (slotId >= 0)
		{
    		if(hand == EnumHand.MAIN_HAND){
    			//Prevent Moving of current filter
    			if(slotId-37 == inventoryPlayer.currentItem){
    				return ItemStackTools.getEmptyStack();
    			}
    		}
    		Slot slot = this.inventorySlots.get(slotId);
    		if (slot instanceof SlotGhostItem)
			{
    			if ((mode == ClickType.PICKUP || mode == ClickType.QUICK_MOVE) && (clickedButton == 0 || clickedButton == 1))
				{
    				ItemStack slotStack = slot.getStack();
					ItemStack heldStack = inventoryPlayer.getItemStack();
					if (mode == ClickType.PICKUP)
					{
						if (ItemStackTools.isNullStack(heldStack) && ItemStackTools.isValid(slotStack))
						{
							if(slotStack.getItem() == ModItems.pipeFilter){
								inventoryPlayer.setItemStack(slotStack);
							}
							slot.putStack(ItemStackTools.getEmptyStack());
						}
						else if (ItemStackTools.isValid(heldStack))
						{
							if(ItemStackTools.isValid(slotStack)){
								if(slotStack.getItem() != ModItems.pipeFilter){
									slot.putStack(ItemUtil.copy(heldStack, 1));
								} 
							} else {
								slot.putStack(ItemUtil.copy(heldStack, 1));
								if(heldStack.getItem() == ModItems.pipeFilter){
									inventoryPlayer.setItemStack(ItemStackTools.incStackSize(heldStack, -1));
								}
							}							
						}
					} 
				}
			}
		}
		return super.slotClick(slotId, clickedButton, mode, player);
	}
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
    	ItemStack itemstack = ItemStackTools.getEmptyStack();
    	/*int par2 = i;
		Slot slot = this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 == 0)
			{
				if (!this.mergeItemStack(itemstack1, 1, 38, true))
				{
					return ItemStackTools.getEmptyStack();
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (par2 != 0)
			{
				if (TileEntityEngineFurnace.getItemEnergyValue(itemstack1) > 0)
				{
					if (!this.mergeItemStack(itemstack1, 0, 1, false))
					{
						
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 1 && par2 < 30)
				{
					if (!this.mergeItemStack(itemstack1, 30, 38, false))
					{
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 30 && par2 < 38 && !this.mergeItemStack(itemstack1, 1, 30, false))
				{
					return ItemStackTools.getEmptyStack();
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, 37, false))
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

			slot.onTake(player, itemstack1);
		}*/

		return itemstack;
    }
}
