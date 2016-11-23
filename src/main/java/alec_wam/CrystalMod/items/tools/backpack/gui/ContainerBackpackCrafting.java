package alec_wam.CrystalMod.items.tools.backpack.gui;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
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

public class ContainerBackpackCrafting extends Container {

	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    private final InventoryBackpack backpackInventory;
    private final InventoryPlayer inventory;
    private final OpenType openType;
	
	public ContainerBackpackCrafting(InventoryPlayer playerInv, OpenType type){
		this.inventory = playerInv;
        this.backpackInventory = new InventoryBackpack(9);
        openType = type;
        
        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        int offset = 34;
        
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, (30+offset) + j * 18, 17 + i * 18));
            }
        }
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(inventory, j+i*9+9, (8+offset)+j*18, 84+i*18));
            }
        }
        for(int i = 0; i < 9; i++){
            if(type == OpenType.MAIN_HAND && i == inventory.currentItem){
                this.addSlotToContainer(new Slot(inventory, i, (8+offset)+i*18, 142){
                	@Override
                    public boolean isItemValid(ItemStack stack){
                        return false;
                    }

                    @Override
                    public void putStack(ItemStack stack){

                    }


                    @Override
                    public ItemStack decrStackSize(int i){
                        return null;
                    }

                    @Override
                    public boolean canTakeStack(EntityPlayer player){
                        return false;
                    }
                });
            }
            else{
                this.addSlotToContainer(new Slot(inventory, i, (8+offset)+i*18, 142));
            }
        }
        
        final EntityPlayer player = playerInv.player;
        for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET}[k];
            this.addSlotToContainer(new Slot(inventory, 36 + (3 - k), 8, 8 + k * 18)
            {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
                 */
                public boolean isItemValid(@Nullable ItemStack stack)
                {
                    if (stack == null)
                    {
                        return false;
                    }
                    else
                    {
                        return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
                    }
                }
                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }
        
        if(openType == OpenType.OFF_HAND){
            this.addSlotToContainer(new Slot(inventory, 40, 8, 147){
            	@Override
                public boolean isItemValid(ItemStack stack){
                    return false;
                }

                @Override
                public void putStack(ItemStack stack){

                }


                @Override
                public ItemStack decrStackSize(int i){
                    return null;
                }

                @Override
                public boolean canTakeStack(EntityPlayer player){
                    return false;
                }
            });
        } else {
	        this.addSlotToContainer(new Slot(inventory, 40, 8, 147)
	        {
	            /**
	             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	             */
	            public boolean isItemValid(@Nullable ItemStack stack)
	            {
	                return super.isItemValid(stack);
	            }
	            @Nullable
	            @SideOnly(Side.CLIENT)
	            public String getSlotTexture()
	            {
	                return "minecraft:items/empty_armor_slot_shield";
	            }
	        });
        }
        
        ItemStack stack = BackpackUtil.getItemStack(this.inventory, this.openType);
        if(stack !=null && stack.getItem() instanceof ItemBackpackBase){
        	ItemUtil.readInventoryFromNBT(backpackInventory, ItemNBTHelper.getCompound(stack));
        }
        for(int i = 0; i < backpackInventory.getSizeInventory(); i++){
        	if(i >= this.craftMatrix.getSizeInventory())continue;
        	this.craftMatrix.setInventorySlotContents(i, backpackInventory.getStackInSlot(i));
        }
        this.onCraftMatrixChanged(this.craftMatrix);
	}
	
	public void onCraftMatrixChanged(IInventory p_75130_1_)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, inventory.player.worldObj));
    }
	
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
	
	@Override
	 public ItemStack transferStackInSlot(EntityPlayer player, int index){
		ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        if(clickTypeIn == ClickType.SWAP && dragType == this.inventory.currentItem && openType == OpenType.MAIN_HAND){
            return null;
        }
        else{
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player){
        ItemStack stack = this.inventory.getCurrentItem();
        if(stack !=null && stack.getItem() instanceof ItemBackpackBase){
        	ItemStack backpack = BackpackUtil.getItemStack(inventory, openType);
            if(backpack !=null){
            	ItemUtil.writeInventoryToNBT(craftMatrix, ItemNBTHelper.getCompound(backpack));
            }
        }
        super.onContainerClosed(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return true;
    }

}
