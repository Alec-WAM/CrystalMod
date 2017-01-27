package alec_wam.CrystalMod.items.tools.backpack.gui;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.inventory.SlotBauble;
import alec_wam.CrystalMod.util.inventory.SlotOffhand;
import alec_wam.CrystalMod.util.inventory.WrapperBaubleInventory;
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
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBackpackCrafting extends Container {

    private final EntityEquipmentSlot[] entityequipmentslots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
	
	private final InventoryBackpack backpackInventory;
	private final EntityPlayer player;
	
    protected IBaublesItemHandler baubles;
	
	public ContainerBackpackCrafting(InventoryBackpack backpackInventory){
		this.player = backpackInventory.getPlayer();
        this.backpackInventory = backpackInventory;
        baubles = BaublesIntegration.instance().getBaubles(player);
        
        if (baubles != null && BaublesIntegration.WhoAmI.whoAmI(player.getEntityWorld()) == BaublesIntegration.WhoAmI.SPCLIENT) {
            baubles = new WrapperBaubleInventory(baubles);
        }
        
        int offset = 34;
        this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 124+offset, 35));

        
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new SlotBackpack(this.craftMatrix, j + i * 3, (30+offset) + j * 18, 17 + i * 18));
            }
        }
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(player.inventory, j+i*9+9, (8+offset)+j*18, 84+i*18));
            }
        }
        for(int i = 0; i < 9; i++){
        	this.addSlotToContainer(new Slot(player.inventory, i, (8+offset)+i*18, 142));
        }
        
        for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = entityequipmentslots[k];
            this.addSlotToContainer(new Slot(player.inventory, 36 + (3 - k), 8, 8 + k * 18)
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
        
        this.addSlotToContainer(new SlotOffhand(player.inventory, 40, 8, 142));
        
        if (hasBaublesSlots()) {
            for (int i = 0; i < baubles.getSlots(); i++) {
              addSlotToContainer(new SlotBauble(player, baubles, i, (176+34+10), 8 + i*18));
            }
        }
        
        for(int i = 0; i < backpackInventory.getSizeInventory(); i++){
        	if(i >= this.craftMatrix.getSizeInventory())continue;
        	this.craftMatrix.setInventorySlotContents(i, backpackInventory.getStackInSlot(i));
        }
        this.onCraftMatrixChanged(this.craftMatrix);
	}
	
	public int getBaublesSize() {
		return baubles !=null ? baubles.getSlots() : 0;
	}
	
	public boolean hasBaublesSlots() {
		return getBaublesSize() > 0;
	}
	
	public void onCraftMatrixChanged(IInventory inventory)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.getEntityWorld()));
    }
	
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
	
	@Override
	 public ItemStack transferStackInSlot(EntityPlayer player, int index){
		ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = (Slot)this.inventorySlots.get(index);

        int inventoryStart = this.craftMatrix.getSizeInventory()+this.craftResult.getSizeInventory();
        int inventoryEnd = inventoryStart+26;
        int hotbarStart = inventoryEnd+1;
        int hotbarEnd = hotbarStart+8;
        int armorStart = hotbarEnd+1;
        int armorEnd = armorStart+3;
        int offhandStart = armorEnd+1;
        int offhandEnd = offhandStart;
        int baublesStart = offhandEnd+1;
        int baublesEnd = hasBaublesSlots() ? baublesStart+(getBaublesSize()-1): 0;
        
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
            
            boolean isArmor = entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR;
            boolean isArmorFull = false;
            if(isArmor){
            	isArmorFull = !((Slot)this.inventorySlots.get(armorEnd - entityequipmentslot.getIndex())).getHasStack();
            }
            
            if(slot instanceof SlotCrafting){
            	isArmorFull = false;
            }
            
            boolean isBauble = hasBaublesSlots() && itemstack1.getItem() instanceof IBauble;
            
            if(entityequipmentslot == EntityEquipmentSlot.OFFHAND && !(this.inventorySlots.get(offhandStart).getHasStack())){
            	if(!this.mergeItemStack(itemstack1, offhandStart, offhandEnd+1, false)){
            		
            		return ItemStackTools.getEmptyStack();
            	}
            }
            else if (isArmor && isArmorFull)
            {
                int i = armorEnd - entityequipmentslot.getIndex();

                if (!this.mergeItemStack(itemstack1, i, i + 1, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            } 
            else if (isBauble && index >= inventoryStart)
            {
            	if (index >= baublesStart && index < baublesEnd+1) {
	                if (!mergeItemStack(itemstack1, hotbarStart, hotbarEnd+1, false) && !mergeItemStack(itemstack1, inventoryStart, inventoryEnd+1, false)) {
	                  return ItemStackTools.getEmptyStack();
	                }
	            } else {
	            	IBauble bauble = (IBauble)itemstack.getItem();
	            	BaubleType type = bauble.getBaubleType(itemstack);
	            	for(int s = 0; s < type.getValidSlots().length; s++){
		                int i = baublesStart + type.getValidSlots()[s];
		
		                boolean full = (this.inventorySlots.get(i).getHasStack());
		                if(full){
		                	if(!this.mergeItemStack(itemstack1, 0, inventoryStart, false)){
		                		return ItemStackTools.getEmptyStack();
		                	}
		                } else {
			                if (bauble.canEquip(itemstack1, player) && !this.mergeItemStack(itemstack1, i, i + 1, false))
			                {
			                	return ItemStackTools.getEmptyStack();
			                }
		                }
		                
		                if(ItemStackTools.isEmpty(itemstack1))break;
	            	}
	            }
            }
            else {
            
		        if (index == 0)
		        {
		            if (!this.mergeItemStack(itemstack1, inventoryStart, hotbarEnd+1, false))
		            {
		                return ItemStackTools.getEmptyStack();
		            }
		
		            slot.onSlotChange(itemstack1, itemstack);
		        }
		        else if (index >= inventoryStart && index < hotbarStart)
		        {
		            if (!this.mergeItemStack(itemstack1, hotbarStart, hotbarEnd+1, false))
		            {
		                return ItemStackTools.getEmptyStack();
		            }
		        }
		        else if (index >= hotbarStart && index < hotbarEnd)
		        {
		            if (!this.mergeItemStack(itemstack1, inventoryStart, inventoryEnd+1, false))
		            {
		                return ItemStackTools.getEmptyStack();
		            }
		        }
		        else if (!this.mergeItemStack(itemstack1, inventoryStart, hotbarEnd+1, false))
		        {
		            return ItemStackTools.getEmptyStack();
		        }

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

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
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
    	for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
        	if(i >= this.backpackInventory.getSizeInventory())continue;
        	this.backpackInventory.setInventorySlotContents(i, craftMatrix.getStackInSlot(i));
        }
        this.backpackInventory.guiSaveSafe(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return backpackInventory.isUseableByPlayer(player);
    }

}
