package alec_wam.CrystalMod.capability;

import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.inventory.SlotOffhand;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerExtendedInventory extends Container
{
    /**
     * The crafting matrix inventory.
     */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public ExtendedPlayerInventory inventory;
    /**
     * Determines if inventory manipulation should be handled.
     */
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;
    private static final EntityEquipmentSlot[] equipmentSlots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ContainerExtendedInventory(InventoryPlayer playerInv, boolean par2, EntityPlayer player)
    {
        this.isLocalWorld = par2;
        this.thePlayer = player;
        inventory = ExtendedPlayerProvider.getExtendedPlayer(player).getInventory();
                
        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28));
        
        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 116 + j * 18, 18 + i * 18));
            }
        }


		for (int k = 0; k < 4; k++) 
		{
			final EntityEquipmentSlot slot = equipmentSlots[k];
			this.addSlotToContainer(new Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18)
            {
                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }
                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    if (ItemStackTools.isEmpty(stack))
                    {
                        return false;
                    }
                    else
                    {
                        return stack.getItem().isValidArmor(stack, slot, thePlayer);
                    }
                }
                @Override
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
                }
            });
		}
        
        this.addSlotToContainer(new SlotItemHandler(inventory, ExtendedPlayerInventory.BACKPACK_SLOT_ID, 77, 8 + 2 * 18){
        	@Override
        	public boolean isItemValid(ItemStack stack){
        		return ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBackpackBase;
        	}
        });   

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
        
        this.addSlotToContainer(new SlotOffhand(playerInv, 40, 77, 62));

        this.onCraftMatrixChanged(this.craftMatrix);
        
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.getEntityWorld()));
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        for (int i = 0; i < 4; ++i)
        {
            ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

            if (ItemStackTools.isValid(itemstack))
            {
                player.dropItem(itemstack, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, ItemStackTools.getEmptyStack());
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
            
            int slotShift = inventory.getSlots();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, true))
                {
                    return ItemStackTools.getEmptyStack();
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 >= 1 && par2 < 5)
            {
                if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (par2 >= 5 && par2 < 9)
            {
                if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            
            // baubles -> inv
            else if (par2 >= 9 && par2 < 9+slotShift)
            {
                if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            
            // inv -> armor
            else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !this.inventorySlots.get(8 - entityequipmentslot.getIndex()).getHasStack())
            {
                int i = 8 - entityequipmentslot.getIndex();

                if (!this.mergeItemStack(itemstack1, i, i + 1, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            
            // inv -> offhand
            else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get(45+ slotShift).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 45+ slotShift, 46+ slotShift, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            
            // inv -> extendedInv
            else if (itemstack.getItem() instanceof ItemBackpackBase)
            {
            	int slotNum = ExtendedPlayerInventory.BACKPACK_SLOT_ID;
            	if (!this.inventorySlots.get(slotNum+9).getHasStack() &&	                		
	                		!this.mergeItemStack(itemstack1, slotNum+9, slotNum + 10, false))
                {
                    return ItemStackTools.getEmptyStack();
                } 
            }            
            
            else if (par2 >= 9+ slotShift && par2 < 36+ slotShift)
            {
                if (!this.mergeItemStack(itemstack1, 36+ slotShift, 45+ slotShift, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (par2 >= 36+ slotShift && par2 < 45+ slotShift)
            {
                if (!this.mergeItemStack(itemstack1, 9+ slotShift, 36+ slotShift, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
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

            slot.onTake(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot)
    {
        return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
    }

}
