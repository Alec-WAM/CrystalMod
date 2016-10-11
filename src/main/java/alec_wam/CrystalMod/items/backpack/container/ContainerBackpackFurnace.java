package alec_wam.CrystalMod.items.backpack.container;

import alec_wam.CrystalMod.items.backpack.BackpackUtils;
import alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;
import alec_wam.CrystalMod.util.ItemNBTHelper;

import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class ContainerBackpackFurnace extends ContainerBackpackSlotClick implements IContainerModularItem {

	public final InventoryBackpackModular inventoryItemModular;
	protected MergeSlotRange ingSlots;
	protected MergeSlotRange fuelSlots;
	protected MergeSlotRange outSlots;
	
	private int cookTime;
    private int totalCookTime;
    private int furnaceBurnTime;
    private int currentItemBurnTime;
	
	public ContainerBackpackFurnace(EntityPlayer player, ItemStack containerStack) {
		super(player, new InventoryBackpackModular(containerStack, BackpackUtils.INV_SIZE, true, player));
		
		this.inventoryItemModular = (InventoryBackpackModular)this.inventory;
        this.inventoryItemModular.setHostInventory(new PlayerMainInvWrapper(player.inventory));
        ingSlots = new MergeSlotRange(0, 0);
        fuelSlots = new MergeSlotRange(0, 0);
        outSlots = new MergeSlotRange(0, 0);
        
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
                    return stack.getItem().isValidArmor(stack, slotNum, ContainerBackpackFurnace.this.player);
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
        
        xOff = 71;
        yOff = 20;
        
        int ingStart = this.inventorySlots.size();
        this.addSlotToContainer(new SlotItemHandlerGeneric(this.inventory, BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE, xOff, yOff));
        this.ingSlots = new MergeSlotRange(ingStart, this.inventorySlots.size() - ingStart);
        
        yOff = 56;
        int fuelingStart = this.inventorySlots.size();
        this.addSlotToContainer(new SlotItemHandlerGeneric(this.inventory, BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE+1, xOff, yOff){
        	
        	public boolean isItemValid(ItemStack stack)
            {
                return TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack);
            }

            public int getItemStackLimit(ItemStack stack)
            {
                return SlotFurnaceFuel.isBucket(stack) ? 1 : super.getItemStackLimit(stack);
            }
            
        });
        this.fuelSlots = new MergeSlotRange(fuelingStart, this.inventorySlots.size() - fuelingStart);
        
        xOff = 131;
        yOff = 38;
        int outStart = this.inventorySlots.size();
        this.addSlotToContainer(new SlotItemhandlerFurnaceOutput(player, this.inventory, BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE+2, xOff, yOff));
        this.outSlots = new MergeSlotRange(outStart, this.inventorySlots.size() - outStart);
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
        else if(this.isSlotInRange(this.ingSlots, slotNum) == true || this.isSlotInRange(this.outSlots, slotNum) == true){

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
        	
        	if (TileEntityFurnace.isItemFuel(slot.getStack()) == true && this.transferStackToSlotRange(player, slotNum, this.fuelSlots, false) == true)
            {
                return true;
            }
            
            if (this.transferStackToSlotRange(player, slotNum, this.ingSlots, false) == true)
            {
                return true;
            }
            
            return this.transferStackToSlotRange(player, slotNum, this.playerMainSlots, false);
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
            
            if (TileEntityFurnace.isItemFuel(slot.getStack()) == true && this.transferStackToSlotRange(player, slotNum, this.fuelSlots, false) == true)
            {
                return true;
            }
            
            if (this.transferStackToSlotRange(player, slotNum, this.ingSlots, false) == true)
            {
                return true;
            }

            return this.transferStackToSlotRange(player, slotNum, this.customInventorySlots, false);
        }

        // From external inventory to player inventory
        return this.transferStackToSlotRange(player, slotNum, this.playerMainSlots, true);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        String tag = "";
    	if (id == 0)
        {
            tag = "Furnace.Burntime";
        }
    	if (id == 1)
        {
            tag = "Furnace.CurrentItemBurntime";
        }
    	if (id == 2)
        {
            tag = "Furnace.Cooktime";
        }
    	if (id == 3)
        {
            tag = "Furnace.TotalCooktime";
        }
    	if(!Strings.isNullOrEmpty(tag))
    	ItemNBTHelper.setInteger(getModularItem(), tag, data);
    }
    
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        int savedCookTime = ItemNBTHelper.getInteger(getModularItem(), "Furnace.Cooktime", 0);
        int savedBurnTime = ItemNBTHelper.getInteger(getModularItem(), "Furnace.Burntime", 0);
        int savedItemBurnTime = ItemNBTHelper.getInteger(getModularItem(), "Furnace.CurrentItemBurntime", 0);
        int savedTotalCookTime = ItemNBTHelper.getInteger(getModularItem(), "Furnace.TotalCooktime", 0);

        for (int i = 0; i < this.listeners.size(); ++i)
        {
        	IContainerListener icrafting = (IContainerListener)this.listeners.get(i);

            if (this.cookTime != savedCookTime)
            {
                icrafting.sendProgressBarUpdate(this, 2, savedCookTime);
            }

            if (this.furnaceBurnTime != savedBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 0, savedBurnTime);
            }

            if (this.currentItemBurnTime != savedItemBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 1, savedItemBurnTime);
            }

            if (this.totalCookTime != savedTotalCookTime)
            {
                icrafting.sendProgressBarUpdate(this, 3, savedTotalCookTime);
            }
        }

        this.cookTime = savedCookTime;
        this.furnaceBurnTime = savedBurnTime;
        this.currentItemBurnTime = savedItemBurnTime;
        this.totalCookTime = savedTotalCookTime;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
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

}
