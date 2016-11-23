package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBackpackNormal extends Container {

	private final InventoryBackpack backpackInventory;
    private final InventoryPlayer inventory;
    private final OpenType openType;
	
	public ContainerBackpackNormal(InventoryPlayer playerInv, OpenType type){
		this.inventory = playerInv;
        this.backpackInventory = new InventoryBackpack(27);
        openType = type;
        
        int offset = 34;
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(backpackInventory, j+i*9, (8+offset)+j*18, 17+i*18));
            }
        }
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(inventory, j+i*9+9, (8+offset)+j*18, 89+i*18));
            }
        }
        for(int i = 0; i < 9; i++){
            if(type == OpenType.MAIN_HAND && i == inventory.currentItem){
                this.addSlotToContainer(new Slot(inventory, i, (8+offset)+i*18, 147){
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
                this.addSlotToContainer(new Slot(inventory, i, (8+offset)+i*18, 147));
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
        List<ItemStack> list = new ArrayList<>();
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
	}
	
	 @Override
	 public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        int inventoryStart = this.backpackInventory.getSizeInventory();
        int inventoryEnd = inventoryStart+26;
        int hotbarStart = inventoryEnd+1;
        int hotbarEnd = hotbarStart+8;
        int armorStart = hotbarEnd+1;
        int armorEnd = armorStart+3;

        Slot theSlot = this.inventorySlots.get(slot);

        if(theSlot != null && theSlot.getHasStack()){
            ItemStack newStack = theSlot.getStack();
            ItemStack currentStack = newStack.copy();

            //Other Slots in Inventory excluded
            if(slot >= inventoryStart){
                //Shift from Inventory
                if(!this.mergeItemStack(newStack, 0, inventoryStart, false)){
                    if(slot >= inventoryStart && slot <= inventoryEnd){
                        if(!this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false)){
                            return null;
                        }
                    }
                    else if(slot >= inventoryEnd+1 && slot < hotbarEnd+1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
                        return null;
                    }
                    else if(slot >= hotbarEnd+1 && slot < armorEnd+1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
                        return null;
                    }
                }
                //

            }
            else if(!this.mergeItemStack(newStack, inventoryStart, armorEnd+1, false)){
                return null;
            }

            theSlot.onSlotChanged();

            if(newStack.stackSize == currentStack.stackSize){
                return null;
            }
            theSlot.onPickupFromSlot(player, newStack);

            return currentStack;
        }
        return null;
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
        	ItemStack backpack = BackpackUtil.getItemStack(this.inventory, this.openType);
            if(backpack !=null){
            	ItemUtil.writeInventoryToNBT(backpackInventory, ItemNBTHelper.getCompound(backpack));
            }
        }
        super.onContainerClosed(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return this.backpackInventory.isUseableByPlayer(player);
    }


}
