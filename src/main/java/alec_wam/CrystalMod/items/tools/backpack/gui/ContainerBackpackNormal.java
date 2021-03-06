package alec_wam.CrystalMod.items.tools.backpack.gui;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.inventory.SlotBauble;
import alec_wam.CrystalMod.util.inventory.SlotOffhand;
import alec_wam.CrystalMod.util.inventory.WrapperBaubleInventory;
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
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBackpackNormal extends Container {

	private final EntityEquipmentSlot[] entityequipmentslots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	
	private final NormalInventoryBackpack backpackInventory;
	private final EntityPlayer player;
	private boolean hasPockets;
	
    protected IBaublesItemHandler baubles;
    
	public ContainerBackpackNormal(NormalInventoryBackpack backpackInventory, InventoryPlayer invPlayer){
		this.player = invPlayer.player;
        this.backpackInventory = backpackInventory;
        baubles = BaublesIntegration.instance().getBaubles(player);
        boolean hasTabs = false;
        if(ItemStackTools.isValid(backpackInventory.getBackpack())){
        	InventoryBackpackUpgrades upgrades = BackpackUtil.getUpgradeInventory(backpackInventory.getBackpack());
        	this.hasPockets = (upgrades !=null ? upgrades.hasUpgrade(BackpackUpgrade.POCKETS) : false);
        	hasTabs = (upgrades !=null ? upgrades.getTabs() !=null ? upgrades.getTabs().length > 0 : false : false);
        }
        
        if (baubles != null && BaublesIntegration.WhoAmI.whoAmI(player.getEntityWorld()) == BaublesIntegration.WhoAmI.SPCLIENT) {
            baubles = new WrapperBaubleInventory(baubles);
        }
        int slotRows = backpackInventory.getSize()/9;
        int pocketOffset = 0;
        if(hasPockets){
        	if(slotRows < 6){
        		pocketOffset = 34;
        	}
        }
        int gapLeft = pocketOffset;
        int gapRight = hasPockets ? 34 : 0;
        
        int offsetArmor = 34;
        int offsetLeft = 34 + gapLeft;
        int offsetTabs = hasTabs ? 32 : 0;
        int offsetY = 18*(slotRows-3);
        for(int i = 0; i < slotRows; i++){
            for(int j = 0; j < 9; j++){
            	this.addSlotToContainer(new SlotBackpack(backpackInventory, j+i*9, (8+offsetLeft)+j*18, offsetTabs+17+i*18));
            }
        }
        if(hasPockets){
	        int invEnd = backpackInventory.getSize()-1;
	        this.addSlotToContainer(new Slot(backpackInventory, invEnd+1, 16+pocketOffset, offsetTabs+17+((slotRows-1)*18)){
	        	@Override
	        	public boolean isItemValid(ItemStack stack){
	        		return ToolUtil.isWeapon(stack);
	        	}
	        	
	        	@Override
	        	public String getSlotTexture(){
	        		return "crystalmod:items/icon_sword";
	        	}
	        });
	        this.addSlotToContainer(new Slot(backpackInventory, invEnd+2, 8+offsetLeft+(9*18)+8, offsetTabs+17+((slotRows-1)*18)){
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
                this.addSlotToContainer(new Slot(player.inventory, j+i*9+9, (8+offsetLeft)+j*18, offsetTabs+(89+offsetY)+i*18));
            }
        }
        
        for(int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(player.inventory, i, (8+offsetLeft)+i*18, offsetTabs+147+offsetY));
        }
        
        for (int k = 0; k < 4; ++k)
        {
        	final EntityEquipmentSlot entityequipmentslot = entityequipmentslots[k];
            this.addSlotToContainer(new Slot(player.inventory, 36 + (3 - k), 8, offsetTabs+8 + k * 18)
            {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                @Override
				public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
                 */
                @Override
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
                @Override
				@Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }
        
        this.addSlotToContainer(new SlotOffhand(player.inventory, 40, 8+gapLeft, offsetTabs+147+offsetY));
        
        if (hasBaublesSlots()) {
            for (int i = 0; i < baubles.getSlots(); i++) {
              addSlotToContainer(new SlotBauble(player, baubles, i, (176+34+10)+gapRight+gapLeft, offsetTabs+8 + i*18));
            }
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
		int inventoryStart = this.backpackInventory.getSize() + (hasPockets ? 2 : 0);
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
            	isArmorFull = !this.inventorySlots.get(armorEnd - entityequipmentslot.getIndex()).getHasStack();
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
	            	
	            	if(isArmor){
	            		//Send to hot-bar
                    	if(!this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false)){
                    		//Send to inventory
                        	if(!this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
                        		//Send to backpack
                            	if(!this.mergeItemStack(newStack, 0, inventoryStart, false)){
                    				return ItemStackTools.getEmptyStack();
                    			}
                    		}
                    	}
                    }
	            	
	            	//Send to backpack
                	if(!this.mergeItemStack(newStack, 0, inventoryStart, false)){
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
	
	            }
	            else if(!this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false)){
	            	if(!this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
	            		return ItemStackTools.getEmptyStack();
	            	}
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
            theSlot.onTake(player, newStack);

            return currentStack;
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
    	ItemStack openBackpack = ExtendedPlayerProvider.getExtendedPlayer(player).getOpenBackpack();
    	
    	if(slotId >=0){
    		Slot slot = getSlot(slotId);
    		if(slot !=null){
    			if(slot.getHasStack()){
    				ItemStack slotStack = slot.getStack();
    				if(ItemUtil.canCombine(openBackpack, slotStack)){
    					return ItemStackTools.getEmptyStack();
    				}
    				
    				//Right-Click
    				if(dragType == 1){
    					if(slotStack.getItem() instanceof ItemBackpackBase){
    						if(ItemStackTools.isValid(slotStack)){
    							EnumHand openhand = EnumHand.MAIN_HAND;
    							
    							if(ItemUtil.canCombine(openBackpack, player.getHeldItemMainhand())){
    								openhand = EnumHand.OFF_HAND;
    							}
    							
    							slotStack.useItemRightClick(player.getEntityWorld(), player, openhand);
    							return ItemStackTools.getEmptyStack();
    						}
    					}
    				}    				
    			}
    		}
    	}
    	
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player){
        super.onContainerClosed(player);
        this.backpackInventory.guiSaveSafe(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return this.backpackInventory.isUsableByPlayer(player);
    }


}
