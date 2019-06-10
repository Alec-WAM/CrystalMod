package alec_wam.CrystalMod.tiles.pipes.item;

import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.items.ItemVariant;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.pipes.EnumPipeUpgrades;
import alec_wam.CrystalMod.tiles.pipes.PipeConnectionMode;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class ContainerItemPipe extends Container implements IMessageHandler
{
    private TileEntityPipeItem pipe;
    private Direction face;
    private boolean twoFilters;
    private Slot slotInFilter;
    private Slot slotOutFilter;
    public ContainerItemPipe(int windowId, PlayerEntity player, TileEntityPipeItem tileEntity, Direction face)
    {
    	super(null, windowId);
    	this.pipe = tileEntity;
    	this.face = face;
    	this.twoFilters = tileEntity.getConnectionSetting(face) == PipeConnectionMode.BOTH;
    	this.addSlot(slotInFilter = new Slot(tileEntity.getInternalInventory(face), 0, 72, 35) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return stack.isEmpty() || stack.getItem() == ModItems.pipeFilter;
    		}
    		
    		@Override
    		public int getSlotStackLimit() {
    			return 1;
    		}
    	});
    	this.addSlot(slotOutFilter = new Slot(tileEntity.getInternalInventory(face), 1, 90, 35) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return stack.isEmpty() || stack.getItem() == ModItems.pipeFilter;
    		}
    		
    		@Override
    		public int getSlotStackLimit() {
    			return 1;
    		}
    	});
    	
    	this.addSlot(new SlotUpgrade(tileEntity.getInternalInventory(face), 2, 134, 58, pipe, face, 0));
    	this.addSlot(new SlotUpgrade(tileEntity.getInternalInventory(face), 3, 152, 58, pipe, face, 1));
        this.addPlayerInventory(player.inventory);
        updateSlots();
    }

    public static class SlotUpgrade extends Slot {
    	protected TileEntityPipeItem pipe;
    	protected Direction side;
    	protected int upgradeSlot;
    	public SlotUpgrade(IInventory inventory, int index, int xPosition, int yPosition, TileEntityPipeItem pipe, Direction side, int upgradeSlot) {
			super(inventory, index, xPosition, yPosition);
			this.pipe = pipe;
			this.side = side;
			this.upgradeSlot = upgradeSlot;
		}
    	
    	@SuppressWarnings("unchecked")
		@Override
		public boolean isItemValid(ItemStack stack){
    		//Prevent double upgrades
			if(ModItems.pipeUpgrades.getItems().contains(stack.getItem())){
				ItemVariant<EnumPipeUpgrades> item = (ItemVariant<EnumPipeUpgrades>)stack.getItem();
				int otherSlot = upgradeSlot == 0 ? 1 : 0;
				return pipe.getUpgradeType(side, otherSlot) != item.type;
			}
			return stack.isEmpty();
		}
		
		@Override
		public int getItemStackLimit(ItemStack stack) {
			if(stack.getItem() == ModItems.pipeUpgrades.getItem(EnumPipeUpgrades.SPEED)){
				return NetworkInventory.getMaxSpeedUpgrades();
			}
			return 1;
		}
		
		@Override
		public int getSlotStackLimit() {
			return 1;
		}
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return true;
    }

    protected void addPlayerInventory(PlayerInventory paramPlayerInventory)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(paramPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(paramPlayerInventory, i, 8 + i * 18, 142));
    }

    public void updateSlots(){
    	this.twoFilters = pipe.getConnectionSetting(face) == PipeConnectionMode.BOTH;
    	if(twoFilters){
    		this.slotInFilter.xPos = 72;
    		this.slotOutFilter.xPos = 90;
    		this.slotOutFilter.yPos = 35;
    	}
    	else {
    		this.slotInFilter.xPos = 80;
    		this.slotOutFilter.xPos = -3000;
    		this.slotOutFilter.yPos = -3000;
    	}
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int i)
    {
    	ItemStack itemstack = ItemStackTools.getEmptyStack();
    	int par2 = i;
		Slot slot = this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 > 4)
			{
				if(twoFilters){
					if (!this.mergeItemStack(itemstack1, 0, 4, false))
					{
						return ItemStackTools.getEmptyStack();
					}
				} else {
					if(itemstack1.getItem() == ModItems.pipeFilter){
						if (!this.mergeItemStack(itemstack1, 0, 1, false))
						{
							return ItemStackTools.getEmptyStack();
						}
					} else {
						if (!this.mergeItemStack(itemstack1, 2, 4, false))
						{
							return ItemStackTools.getEmptyStack();
						}
					}
				}
				
				return ItemStackTools.getEmptyStack();
			} 
			else if (!this.mergeItemStack(itemstack1, 2, 40, true))
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
		}

		return itemstack;
    }
    
    //Made the slot limit smarter
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
    	boolean flag = false;
    	int i = startIndex;
    	if (reverseDirection) {
    		i = endIndex - 1;
    	}

    	if (stack.isStackable()) {
    		while(!stack.isEmpty()) {
    			if (reverseDirection) {
    				if (i < startIndex) {
    					break;
    				}
    			} else if (i >= endIndex) {
    				break;
    			}

    			Slot slot = this.inventorySlots.get(i);
    			ItemStack itemstack = slot.getStack();
    			if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
    				int j = itemstack.getCount() + stack.getCount();
    				//Changed to getItemStackLimit(stack)
    				int maxSize = Math.min(slot.getItemStackLimit(stack), stack.getMaxStackSize());
    				if (j <= maxSize) {
    					stack.setCount(0);
    					itemstack.setCount(j);
    					slot.onSlotChanged();
    					flag = true;
    				} else if (itemstack.getCount() < maxSize) {
    					stack.shrink(maxSize - itemstack.getCount());
    					itemstack.setCount(maxSize);
    					slot.onSlotChanged();
    					flag = true;
    				}
    			}

    			if (reverseDirection) {
    				--i;
    			} else {
    				++i;
    			}
    		}
    	}

    	if (!stack.isEmpty()) {
    		if (reverseDirection) {
    			i = endIndex - 1;
    		} else {
    			i = startIndex;
    		}

    		while(true) {
    			if (reverseDirection) {
    				if (i < startIndex) {
    					break;
    				}
    			} else if (i >= endIndex) {
    				break;
    			}

    			Slot slot1 = this.inventorySlots.get(i);
    			ItemStack itemstack1 = slot1.getStack();
    			if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
    				//Changed to getItemStackLimit(stack)
    				if (stack.getCount() > slot1.getItemStackLimit(stack)) {
    					slot1.putStack(stack.split(slot1.getItemStackLimit(stack)));
    				} else {
    					slot1.putStack(stack.split(stack.getCount()));
    				}

    				slot1.onSlotChanged();
    				flag = true;
    				break;
    			}

    			if (reverseDirection) {
    				--i;
    			} else {
    				++i;
    			}
    		}
    	}

    	return flag;
    }

	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateSlots")){
			this.updateSlots();
		}
	}
}
