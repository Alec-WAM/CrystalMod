package com.alec_wam.CrystalMod.items.backpack.container;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import org.apache.commons.lang3.StringUtils;

import com.alec_wam.CrystalMod.items.backpack.BackpackUtils;
import com.alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;

public class ContainerBackpackRepair extends ContainerBackpackSlotClick implements IContainerModularItem {

	public final InventoryBackpackModular inventoryItemModular;
	protected MergeSlotRange anvilSlots;
	
	/** The maximum cost of repairing/renaming in the anvil. */
    public int maximumCost;
    /** determined by damage of input item and stackSize of repair materials */
    public int materialCost;
    private String repairedItemName;
    /** Here comes out item you merged and/or renamed. */
    private IInventory outputSlot;
    /** The 2slots where you put your items in that you want to merge and/or rename. */
    private IInventory inputSlots;
	
	public ContainerBackpackRepair(EntityPlayer player, ItemStack containerStack) {
		super(player, new InventoryBackpackModular(containerStack, BackpackUtils.INV_SIZE, true, player));
		
		this.inventoryItemModular = (InventoryBackpackModular)this.inventory;
        this.inventoryItemModular.setHostInventory(new PlayerMainInvWrapper(player.inventory));
        anvilSlots = new MergeSlotRange(0, 0);
        
        this.outputSlot = new InventoryCraftResult();
        this.inputSlots = new InventoryBasic("Repair", true, 2)
        {
            /**
             * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't
             * think it hasn't changed and skip it.
             */
            public void markDirty()
            {
                super.markDirty();
                ContainerBackpackRepair.this.onCraftMatrixChanged(this);
            }
        };
        
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
            final int i2 = i;
            this.addSlotToContainer(new SlotItemHandlerGeneric(inv, 3 - i, posX, posY + i * 18)
            {
                public int getSlotStackLimit()
                {
                    return 1;
                }

                public boolean isItemValid(ItemStack stack)
                {
                    if (stack == null) return false;
                    return stack.getItem().isValidArmor(stack, slotNum, ContainerBackpackRepair.this.player);
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
        
        int anvilStart = this.inventorySlots.size();
        this.addSlotToContainer(new SlotItemHandlerGeneric(new InvWrapper(inputSlots), 0, 27+18, 47+14));
        this.addSlotToContainer(new SlotItemHandlerGeneric(new InvWrapper(inputSlots), 1, 76+18, 47+14));
        
        this.addSlotToContainer(new SlotItemHandlerGeneric(new InvWrapper(this.outputSlot), 2, 134+18, 47+14)
        {
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
            /**
             * Return whether this slot's stack can be taken from this slot.
             */
            public boolean canTakeStack(EntityPlayer playerIn)
            {
                return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerBackpackRepair.this.maximumCost) && ContainerBackpackRepair.this.maximumCost > 0 && this.getHasStack();
            }
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
            {
                if (!playerIn.capabilities.isCreativeMode)
                {
                    playerIn.addExperienceLevel(-ContainerBackpackRepair.this.maximumCost);
                }

                //float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(playerIn, stack, ContainerBackpackRepair.this.inputSlots.getStackInSlot(0), ContainerBackpackRepair.this.inputSlots.getStackInSlot(1));

                ContainerBackpackRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);

                if (ContainerBackpackRepair.this.materialCost > 0)
                {
                    ItemStack itemstack = ContainerBackpackRepair.this.inputSlots.getStackInSlot(1);

                    if (itemstack != null && itemstack.stackSize > ContainerBackpackRepair.this.materialCost)
                    {
                        itemstack.stackSize -= ContainerBackpackRepair.this.materialCost;
                        ContainerBackpackRepair.this.inputSlots.setInventorySlotContents(1, itemstack);
                    }
                    else
                    {
                        ContainerBackpackRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                    }
                }
                else
                {
                    ContainerBackpackRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                }

                ContainerBackpackRepair.this.maximumCost = 0;

                if (!ContainerBackpackRepair.this.player.worldObj.isRemote)
                {
                	int x = (int)ContainerBackpackRepair.this.player.posX;
                	int y = (int)ContainerBackpackRepair.this.player.posY;
                	int z = (int)ContainerBackpackRepair.this.player.posZ;
                	ContainerBackpackRepair.this.player.worldObj.playEvent(1021, new BlockPos(x, y, z), 0);
                }
            }
        });
        this.anvilSlots = new MergeSlotRange(anvilStart, 3); 
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
        else if(this.isSlotInRange(this.anvilSlots, slotNum) == true){
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

            return this.transferStackToSlotRange(player, slotNum, this.anvilSlots, false);
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
            
            if (this.transferStackToSlotRange(player, slotNum, this.anvilSlots, false) == true)
            {
                return true;
            }

            return this.transferStackToSlotRange(player, slotNum, this.customInventorySlots, false);
        }

        // From external inventory to player inventory
        return this.transferStackToSlotRange(player, slotNum, this.playerMainSlots, true);
    }
    
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        super.onCraftMatrixChanged(inventoryIn);

        if (inventoryIn == this.inputSlots)
        {
            this.updateRepairOutput();
        }
    }
    
    public static boolean onAnvilChange(ContainerBackpackRepair container, ItemStack left, ItemStack right, IInventory outputSlot, String name, int baseCost)
    {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput() == null) return true;

        outputSlot.setInventorySlotContents(0, e.getOutput());
        container.maximumCost = e.getCost();
        container.materialCost = e.getMaterialCost();
        return false;
    }
    
    /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    public void updateRepairOutput()
    {
    	ItemStack itemstack = this.inputSlots.getStackInSlot(0);
        this.maximumCost = 1;
        int i = 0;
        int j = 0;
        int k = 0;

        if (itemstack == null)
        {
            this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
            this.maximumCost = 0;
        }
        else
        {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
            j = j + itemstack.getRepairCost() + (itemstack2 == null ? 0 : itemstack2.getRepairCost());
            this.materialCost = 0;
            boolean flag = false;

            if (itemstack2 != null)
            {
                if (!onAnvilChange(this, itemstack, itemstack2, outputSlot, repairedItemName, j)) return;
                flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !Items.ENCHANTED_BOOK.getEnchantments(itemstack2).hasNoTags();

                if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2))
                {
                    int j2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);

                    if (j2 <= 0)
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    int k2;

                    for (k2 = 0; j2 > 0 && k2 < itemstack2.stackSize; ++k2)
                    {
                        int l2 = itemstack1.getItemDamage() - j2;
                        itemstack1.setItemDamage(l2);
                        ++i;
                        j2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
                    }

                    this.materialCost = k2;
                }
                else
                {
                    if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable()))
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    if (itemstack1.isItemStackDamageable() && !flag)
                    {
                        int l = itemstack.getMaxDamage() - itemstack.getItemDamage();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;

                        if (l1 < 0)
                        {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getMetadata())
                        {
                            itemstack1.setItemDamage(l1);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);

                    for (Enchantment enchantment1 : map1.keySet())
                    {
                        if (enchantment1 != null)
                        {
                            int i3 = map.containsKey(enchantment1) ? ((Integer)map.get(enchantment1)).intValue() : 0;
                            int j3 = ((Integer)map1.get(enchantment1)).intValue();
                            j3 = i3 == j3 ? j3 + 1 : Math.max(j3, i3);
                            boolean flag1 = enchantment1.canApply(itemstack);

                            if (this.player.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK)
                            {
                                flag1 = true;
                            }

                            for (Enchantment enchantment : map.keySet())
                            {
                                if (enchantment != enchantment1 && !(enchantment1.canApplyTogether(enchantment) && enchantment.canApplyTogether(enchantment1)))  //Forge BugFix: Let Both enchantments veto being together
                                {
                                    flag1 = false;
                                    ++i;
                                }
                            }

                            if (flag1)
                            {
                                if (j3 > enchantment1.getMaxLevel())
                                {
                                    j3 = enchantment1.getMaxLevel();
                                }

                                map.put(enchantment1, Integer.valueOf(j3));
                                int k3 = 0;

                                switch (enchantment1.getRarity())
                                {
                                    case COMMON:
                                        k3 = 1;
                                        break;
                                    case UNCOMMON:
                                        k3 = 2;
                                        break;
                                    case RARE:
                                        k3 = 4;
                                        break;
                                    case VERY_RARE:
                                        k3 = 8;
                                }

                                if (flag)
                                {
                                    k3 = Math.max(1, k3 / 2);
                                }

                                i += k3 * j3;
                            }
                        }
                    }
                }
            }

            if (flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2)) itemstack1 = null;

            if (StringUtils.isBlank(this.repairedItemName))
            {
                if (itemstack.hasDisplayName())
                {
                    k = 1;
                    i += k;
                    itemstack1.clearCustomName();
                }
            }
            else if (!this.repairedItemName.equals(itemstack.getDisplayName()))
            {
                k = 1;
                i += k;
                itemstack1.setStackDisplayName(this.repairedItemName);
            }

            this.maximumCost = j + i;

            if (i <= 0)
            {
                itemstack1 = null;
            }

            if (k == i && k > 0 && this.maximumCost >= 40)
            {
                this.maximumCost = 39;
            }

            if (this.maximumCost >= 40 && !this.player.capabilities.isCreativeMode)
            {
                itemstack1 = null;
            }

            if (itemstack1 != null)
            {
                int i2 = itemstack1.getRepairCost();

                if (itemstack2 != null && i2 < itemstack2.getRepairCost())
                {
                    i2 = itemstack2.getRepairCost();
                }

                if (k != i || k == 0)
                {
                    i2 = i2 * 2 + 1;
                }

                itemstack1.setRepairCost(i2);
                EnchantmentHelper.setEnchantments(map, itemstack1);
            }

            this.outputSlot.setInventorySlotContents(0, itemstack1);
            this.detectAndSendChanges();
        }
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendProgressBarUpdate(this, 0, this.maximumCost);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            this.maximumCost = data;
        }
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!playerIn.worldObj.isRemote)
        {
            for (int i = 0; i < this.inputSlots.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.inputSlots.removeStackFromSlot(i);

                if (itemstack != null)
                {
                	ItemStack ret = BackpackUtils.tryInsertItemStackToInventory(inventoryItemModular, itemstack);
                    if(ret !=null)playerIn.dropItem(ret, false);
                }
            }
        }
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
    
    /**
     * used by the Anvil GUI to update the Item Name being typed by the player
     */
    public void updateItemName(String newName)
    {
        this.repairedItemName = newName;

        if (this.getSlot(BackpackUtils.MAIN_SIZE+2).getHasStack())
        {
            ItemStack itemstack = this.getSlot(BackpackUtils.MAIN_SIZE+2).getStack();

            if (StringUtils.isBlank(newName))
            {
                itemstack.clearCustomName();
            }
            else
            {
                itemstack.setStackDisplayName(this.repairedItemName);
            }
        }

        this.updateRepairOutput();
    }

}
