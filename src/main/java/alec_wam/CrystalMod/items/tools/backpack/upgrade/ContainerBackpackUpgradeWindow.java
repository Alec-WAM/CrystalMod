package alec_wam.CrystalMod.items.tools.backpack.upgrade;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackInventory;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.inventory.SlotBauble;
import alec_wam.CrystalMod.util.inventory.SlotOffhand;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLiving;
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

public class ContainerBackpackUpgradeWindow extends Container {
	private final InventoryBackpack backpackInventory;
	private final InventoryBackpackUpgrades upgradeInventory;
	private final BackpackUpgrade upgrade;
	
	protected IBaublesItemHandler baubles;
	private final EntityEquipmentSlot[] entityequipmentslots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ContainerBackpackUpgradeWindow(InventoryPlayer playerInventory, InventoryBackpackUpgrades upgradeInventory, BackpackUpgrade upgrade)
    {
        this.upgradeInventory = upgradeInventory;
        this.upgrade = upgrade;
        baubles = BaublesIntegration.instance().getBaubles(playerInventory.player);
        
        if(ItemStackTools.isValid(upgradeInventory.getBackpack()) && upgradeInventory.getBackpack().getItem() instanceof ItemBackpackBase){
        	IBackpack type = ((ItemBackpackBase)upgradeInventory.getBackpack().getItem()).getBackpack();
        	if(type instanceof IBackpackInventory){
        		backpackInventory = ((IBackpackInventory)type).getInventory(upgradeInventory.getBackpack());
        	} else {
        		backpackInventory = null;
        	}
        } else {
        	backpackInventory = null;
        }
        boolean hasPockets = upgradeInventory.hasUpgrade(BackpackUpgrade.POCKETS);
        int gapLeft = hasPockets ? 34 : 0;
        int gapRight = hasPockets ? 34 : 0;
        
        int offsetArmor = 34;
        int offsetLeft = offsetArmor + gapLeft;
        int offsetRight = gapRight;
        int offsetTabs = 32;
        int offsetY = 0;
        if(upgrade == BackpackUpgrade.ENDER){
        	for (int k = 0; k < 3; ++k)
            {
                for (int l = 0; l < 9; ++l)
                {
                    this.addSlotToContainer(new Slot(playerInventory.player.getInventoryEnderChest(), l + k * 9, (8+offsetLeft) + l * 18, offsetTabs+17 + k * 18));
                }
            }
        }
        
        if(hasPockets && backpackInventory !=null){
	        int invEnd = backpackInventory.getSize()-1;
	        this.addSlotToContainer(new Slot(backpackInventory, invEnd+1, 16+offsetArmor, offsetTabs+17+(36)){
	        	@Override
	        	public boolean isItemValid(ItemStack stack){
	        		return ToolUtil.isWeapon(stack);
	        	}
	        	
	        	@Override
	        	public String getSlotTexture(){
	        		return "crystalmod:items/icon_sword";
	        	}
	        });
	        this.addSlotToContainer(new Slot(backpackInventory, invEnd+2, 8+offsetLeft+(9*18)+8, offsetTabs+17+(36)){
	        	@Override
	        	public boolean isItemValid(ItemStack stack){
	        		return ToolUtil.isTool(stack);
	        	}
	        	
	        	@Override
	        	public String getSlotTexture(){
	        		return "crystalmod:items/icon_pickaxe";
	        	}
	        });
		}
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(playerInventory, j+i*9+9, (8+offsetLeft)+j*18, offsetTabs+(89+offsetY)+i*18));
            }
        }
        
        for(int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(playerInventory, i, (8+offsetLeft)+i*18, offsetTabs+147+offsetY));
        }
        
        for (int k = 0; k < 4; ++k)
        {
        	final EntityEquipmentSlot entityequipmentslot = entityequipmentslots[k];
            this.addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 8, offsetTabs+8 + k * 18)
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
                        return stack.getItem().isValidArmor(stack, entityequipmentslot, playerInventory.player);
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
        
        this.addSlotToContainer(new SlotOffhand(playerInventory, 40, 8+gapLeft, offsetTabs+147+offsetY));
        
        if (hasBaublesSlots()) {
            for (int i = 0; i < baubles.getSlots(); i++) {
              addSlotToContainer(new SlotBauble(playerInventory.player, baubles, i, (176+34+10)+gapRight+gapLeft, offsetTabs+8 + i*18));
            }
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.upgradeInventory.isUseableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
    	if(upgrade != BackpackUpgrade.ENDER)return null;
    	boolean hasPockets = upgradeInventory.hasUpgrade(BackpackUpgrade.POCKETS);
    	int inventoryStart = 27 + (hasPockets ? 2 : 0);
        int inventoryEnd = inventoryStart+26;
        int hotbarStart = inventoryEnd+1;
        int hotbarEnd = hotbarStart+8;
        int armorStart = hotbarEnd+1;
        int armorEnd = armorStart+3;
        int offhandStart = armorEnd+1;
        int offhandEnd = offhandStart;
        int baublesStart = offhandEnd+1;
        int baublesEnd = hasBaublesSlots() ? baublesStart+(getBaublesSize()-1): 0;
        int slot = index;
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
            boolean skipTransfer = false;
            
            if(hasPockets && ToolUtil.isWeapon(currentStack) && !(this.inventorySlots.get(backpackInventory.getSize()).getHasStack())){
            	if(mergeItemStack(newStack, backpackInventory.getSize(), backpackInventory.getSize()+1, false)){
            		skipTransfer = true;
            	}
            }
            else if(hasPockets && ToolUtil.isTool(currentStack) && !(this.inventorySlots.get(backpackInventory.getSize()+1).getHasStack())){
            	if(this.mergeItemStack(newStack, backpackInventory.getSize()+1, backpackInventory.getSize()+2, false)){
            		skipTransfer = true;
            	}
            }
            
            if(skipTransfer){
            	
            }
            else if(entityequipmentslot == EntityEquipmentSlot.OFFHAND && !(this.inventorySlots.get(offhandStart).getHasStack())){
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
			                if (bauble.canEquip(newStack, playerIn) && !this.mergeItemStack(newStack, i, i + 1, false))
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
                return ItemStackTools.getEmptyStack();
            }
            theSlot.onPickupFromSlot(playerIn, newStack);

            return currentStack;
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
    	return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
    
    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        backpackInventory.guiSaveSafe(playerIn);
        upgradeInventory.guiSaveSafe(playerIn);
    }

    public int getBaublesSize() {
		return baubles !=null ? baubles.getSlots() : 0;
	}
	
	public boolean hasBaublesSlots() {
		return getBaublesSize() > 0;
	}
}
