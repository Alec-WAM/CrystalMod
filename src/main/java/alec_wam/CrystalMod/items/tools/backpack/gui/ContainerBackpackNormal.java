package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.inventory.SlotBauble;
import alec_wam.CrystalMod.util.inventory.SlotLocked;
import alec_wam.CrystalMod.util.inventory.SlotOffhand;
import alec_wam.CrystalMod.util.inventory.WrapperBaubleInventory;
import alec_wam.CrystalMod.util.inventory.WrapperInventory;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBackpackNormal extends Container {

	private final InventoryBackpack backpackInventory;
    private final InventoryPlayer inventory;
    private final OpenType openType;
	
    protected IBaublesItemHandler baubles;
    
	public ContainerBackpackNormal(InventoryPlayer playerInv, OpenType type){
		this.inventory = playerInv;
        this.backpackInventory = new InventoryBackpack(27);
        baubles = BaublesIntegration.instance().getBaubles(playerInv.player);
        openType = type;
        
        if (baubles != null && BaublesIntegration.WhoAmI.whoAmI(playerInv.player.worldObj) == BaublesIntegration.WhoAmI.SPCLIENT) {
            baubles = new WrapperBaubleInventory(baubles);
        }
        
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
                this.addSlotToContainer(new SlotLocked(inventory, i, (8+offset)+i*18, 147));
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
        if(openType == OpenType.OFF_HAND){
            this.addSlotToContainer(new SlotLocked(inventory, 40, 8, 147));
        } else {
	        this.addSlotToContainer(new SlotOffhand(inventory, 40, 8, 147));
        }
        
        if (hasBaublesSlots()) {
            for (int i = 0; i < baubles.getSlots(); i++) {
              addSlotToContainer(new SlotBauble(playerInv.player, baubles, i, (176+34+10), 8 + i*18));
            }
        }
        
        ItemStack stack = BackpackUtil.getItemStack(this.inventory, this.openType);
        if(!ItemStackTools.isNullStack(stack) && stack.getItem() instanceof ItemBackpackBase){
        	ItemUtil.readInventoryFromNBT(backpackInventory, ItemNBTHelper.getCompound(stack));
        }
	}
	
	public int getBaublesSize() {
		return baubles !=null ? baubles.getSlots() : 0;
	}
	
	public boolean hasBaublesSlots() {
		return getBaublesSize() > 0;
	}
	
	 @Override
	 public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        int inventoryStart = this.backpackInventory.getSizeInventory();
        int inventoryEnd = inventoryStart+26;
        int hotbarStart = inventoryEnd+1;
        int hotbarEnd = hotbarStart+8;
        int armorStart = hotbarEnd+1;
        int armorEnd = armorStart+3;
        int offhandStart = armorEnd+1;
        int offhandEnd = offhandStart;
        int baublesStart = offhandEnd+1;
        int baublesEnd = hasBaublesSlots() ? baublesStart+(getBaublesSize()-1): 0;
        		
        Slot theSlot = this.inventorySlots.get(slot);

        if(theSlot != null && theSlot.getHasStack()){
            ItemStack newStack = theSlot.getStack();
            ItemStack currentStack = newStack.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(currentStack);
            
            boolean isArmor = entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR;
            boolean isArmorFull = false;
            if(isArmor){
            	isArmorFull = !((Slot)this.inventorySlots.get(armorEnd - entityequipmentslot.getIndex())).getHasStack();
            }
            
            boolean isBauble = hasBaublesSlots() && currentStack.getItem() instanceof IBauble;
            
            if(entityequipmentslot == EntityEquipmentSlot.OFFHAND && !(this.inventorySlots.get(offhandStart).getHasStack())){
            	if(!this.mergeItemStack(newStack, offhandStart, offhandEnd+1, false)){
            		
            		return ItemStackTools.getEmptyStack();
            	}
            }
            else if (isArmor && isArmorFull)
            {
                int i = armorEnd - entityequipmentslot.getIndex();

                if (!this.mergeItemStack(newStack, i, i + 1, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (isBauble && slot >= inventoryStart)
            {
            	if (slot >= baublesStart && slot < baublesEnd+1) {
	                if (!mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false) && !mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)) {
	                  return ItemStackTools.getEmptyStack();
	                }
	            } else {
	            	IBauble bauble = (IBauble)currentStack.getItem();
	            	BaubleType type = bauble.getBaubleType(currentStack);
	            	for(int s = 0; s < type.getValidSlots().length; s++){
		                int i = baublesStart + type.getValidSlots()[s];
		
		                boolean full = (this.inventorySlots.get(i).getHasStack());
		                if(full){
		                	if(!this.mergeItemStack(newStack, 0, inventoryStart, false)){
		                		return ItemStackTools.getEmptyStack();
		                	}
		                } else {
			                if (bauble.canEquip(newStack, player) && !this.mergeItemStack(newStack, i, i + 1, false))
			                {
			                	return ItemStackTools.getEmptyStack();
			                }
		                }
		                
		                if(ItemStackTools.isEmpty(newStack))break;
	            	}
	            }
            }
            else {
	            
	            //Other Slots in Inventory excluded
	            if(slot >= inventoryStart){
	                //Shift from Inventory
	            	
	            	boolean skip = false;
	            	
	            	if(isArmor){
	            		if(isArmorFull){
	            			skip = true;
	            		}
	            		if(slot >= hotbarEnd+1 && slot < armorEnd+1){
	            			skip = true;
	            		}
	            	}
	            	
	                if(skip || !this.mergeItemStack(newStack, 0, inventoryStart, false)){
	                    if(slot >= inventoryStart && slot <= inventoryEnd){
	                        if(!this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false)){
	                            return ItemStackTools.getEmptyStack();
	                        }
	                    }
	                    else if(slot >= inventoryEnd+1 && slot < hotbarEnd+1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
	                        return ItemStackTools.getEmptyStack();
	                    }
	                    else if(slot >= hotbarEnd+1 && slot < armorEnd+1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
	                        return ItemStackTools.getEmptyStack();
	                    }
	                }
	                //
	
	            }
	            else if(!this.mergeItemStack(newStack, inventoryStart, armorEnd+1, false)){
	                return ItemStackTools.getEmptyStack();
	            }
            }

            if(ItemStackTools.isEmpty(newStack)){
            	theSlot.putStack(ItemStackTools.getEmptyStack());
            }else {            
            	theSlot.onSlotChanged();
            }
            if(ItemStackTools.getStackSize(newStack) == ItemStackTools.getStackSize(currentStack)){
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
        if(!ItemStackTools.isNullStack(stack) && stack.getItem() instanceof ItemBackpackBase){
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
