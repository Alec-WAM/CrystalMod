package com.alec_wam.CrystalMod.items.backpack.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.items.backpack.BackpackUtils;
import com.alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;

public class ContainerBackpackEnderChest extends ContainerBackpackSlotClick implements IContainerModularItem {

	public final InventoryBackpackModular inventoryItemModular;
	protected MergeSlotRange enderSlots;
	
	public ContainerBackpackEnderChest(EntityPlayer player, ItemStack containerStack) {
		super(player, new InventoryBackpackModular(containerStack, BackpackUtils.INV_SIZE, true, player));
		
		this.inventoryItemModular = (InventoryBackpackModular)this.inventory;
        this.inventoryItemModular.setHostInventory(new PlayerMainInvWrapper(player.inventory));
        enderSlots = new MergeSlotRange(0, 0);
        this.addCustomInventorySlots();
        this.addPlayerInventorySlots(30, 174);
	}
	
	@Override
    protected void addPlayerInventorySlots(int posX, int posY)
    {
        super.addPlayerInventorySlots(posX, posY);

        int playerArmorStart = this.inventorySlots.size();

        IItemHandlerModifiable inv = new PlayerArmorInvWrapper(this.inventoryPlayer);
        // Player armor slots
        posX = 8;
        posY = 93;
        EntityEquipmentSlot[] slots = {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
        for (int i = 0; i < 4; i++)
        {
            final EntityEquipmentSlot slotNum = slots[i];
            this.addSlotToContainer(new SlotItemHandlerGeneric(inv, 3 - i, posX, posY + i * 18)
            {
                public int getSlotStackLimit()
                {
                    return 1;
                }

                public boolean isItemValid(ItemStack stack)
                {
                    if (stack == null) return false;
                    return stack.getItem().isValidArmor(stack, slotNum, ContainerBackpackEnderChest.this.player);
                }

                @SideOnly(Side.CLIENT)
                @Override
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[slotNum.getIndex()];
                }
            });
        }

        this.playerArmorSlots = new MergeSlotRange(playerArmorStart, 4);
        
        int enderSlotsStart = this.inventorySlots.size();
        posX = 30;
        posY = 24;
        
        IItemHandlerModifiable enderInv = new InvWrapper(player.getInventoryEnderChest());
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new SlotItemHandlerGeneric(enderInv, i * 9 + j, posX + j * 18, posY + i * 18){
                	@Override
                    public boolean isItemValid(ItemStack stack)
                    {
                        if(stack !=null && stack.getItem() == ModItems.backpack)return false;
                		return super.isItemValid(stack);
                    }
                });
            }
        }
        this.enderSlots = new MergeSlotRange(enderSlotsStart, 27);
    }

    @Override
    protected void addCustomInventorySlots()
    {
        int customInvStart = this.inventorySlots.size();
        int xOff = 30;
        int yOff = 102;

        // The top/middle section of the bag inventory
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new SlotItemHandlerGeneric(this.inventory, i * 9 + j, xOff + j * 18, yOff + i * 18));
            }
        }
        this.customInventorySlots = new MergeSlotRange(customInvStart, this.inventorySlots.size() - customInvStart);
    }
    
    public boolean transferStackFromSlot(EntityPlayer player, int slotNum)
    {
    	Slot slot = this.getSlot(slotNum);
        if (slot == null || slot.getHasStack() == false || slot.canTakeStack(player) == false)
        {
            return false;
        }

        // From player armor slot to player main inventory
        if (this.isSlotInRange(this.playerArmorSlots, slotNum) == true)
        {
            return this.transferStackToSlotRange(player, slotNum, this.playerMainSlots, false);
        }
        else if(this.isSlotInRange(this.enderSlots, slotNum) == true){
        	if (this.transferStackToSlotRange(player, slotNum, this.playerArmorSlots, false) == true)
            {
                return true;
            }

            if (this.transferStackToPrioritySlots(player, slotNum, false) == true)
            {
                return true;
            }
            
            if (this.transferStackToSlotRange(player, slotNum, this.playerMainSlots, false) == true)
            {
                return true;
            }

            return this.transferStackToSlotRange(player, slotNum, this.customInventorySlots, false);
        }
        else if(this.isSlotInRange(this.customInventorySlots, slotNum) == true){
        	if (this.transferStackToSlotRange(player, slotNum, this.playerArmorSlots, false) == true)
            {
                return true;
            }

        	if (this.transferStackToPrioritySlots(player, slotNum, false) == true)
            {
                return true;
            }
        	
            if (this.transferStackToSlotRange(player, slotNum, this.playerMainSlots, false) == true)
            {
                return true;
            }

            return this.transferStackToSlotRange(player, slotNum, this.enderSlots, false);
        }
        // From player main inventory to armor slot or the "external" inventory
        else if (this.isSlotInRange(this.playerMainSlots, slotNum) == true)
        {
            if (this.transferStackToSlotRange(player, slotNum, this.playerArmorSlots, false) == true)
            {
                return true;
            }

            if (this.transferStackToPrioritySlots(player, slotNum, false) == true)
            {
                return true;
            }
            
            if (this.transferStackToSlotRange(player, slotNum, this.enderSlots, false) == true)
            {
                return true;
            }

            return this.transferStackToSlotRange(player, slotNum, this.customInventorySlots, false);
        }

        // From external inventory to player inventory
        return this.transferStackToSlotRange(player, slotNum, this.playerMainSlots, true);
    }
    
    @Override
    public ItemStack getModularItem()
    {
        return this.inventoryItemModular.getModularItemStack();
    }

    public int getBagTier()
    {
        if (this.inventoryItemModular.getModularItemStack() != null)
        {
            return this.inventoryItemModular.getModularItemStack().getItemDamage() == 1 ? 1 : 0;
        }

        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
    }

    @Override
    public ItemStack slotClick(int slotNum, int key, ClickType type, EntityPlayer player)
    {
        ItemStack modularStackPre = this.inventoryItemModular.getModularItemStack();

        ItemStack stack = super.slotClick(slotNum, key, type, player);

        ItemStack modularStackPost = this.inventoryItemModular.getModularItemStack();

        // The Bag's stack changed after the click, re-read the inventory contents.
        if (modularStackPre != modularStackPost)
        {
            //System.out.println("slotClick() - updating container");
            this.inventoryItemModular.readFromContainerItemStack();
        }

        this.detectAndSendChanges();

        return stack;
    }

}
