package alec_wam.CrystalMod.items.backpack.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import alec_wam.CrystalMod.items.backpack.BackpackUtils;
import alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;

public class ContainerBackpackCrafting extends ContainerBackpackSlotClick implements IContainerModularItem {

	public final InventoryBackpackModular inventoryItemModular;
	private final InventoryCraftingBackpack craftMatrix;
    private final InventoryBackPackCraftingResult craftResult;
	
	public ContainerBackpackCrafting(EntityPlayer player, ItemStack containerStack) {
		super(player, new InventoryBackpackModular(containerStack, BackpackUtils.INV_SIZE, true, player));
		
		this.inventoryItemModular = (InventoryBackpackModular)this.inventory;
        this.inventoryItemModular.setHostInventory(new PlayerMainInvWrapper(player.inventory));
        
        craftMatrix  = new InventoryCraftingBackpack(this, 3, 3, inventoryItemModular, BackpackUtils.MAIN_SIZE);
        craftResult  = new InventoryBackPackCraftingResult(inventoryItemModular, BackpackUtils.MAIN_SIZE);
        
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
                    return stack.getItem().isValidArmor(stack, slotNum, ContainerBackpackCrafting.this.player);
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

        
        
        xOff = 53;
        yOff = 24;
        
        this.addSlotToContainer(new SlotItemHandlerCraftresult(this.player, this.craftMatrix, this.inventory, BackpackUtils.MAIN_SIZE, 147, 42));
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                this.addSlotToContainer(new SlotItemHandlerGeneric(new InvWrapper(this.craftMatrix), (i * 3 + j), xOff + j * 18, yOff + i * 18));
            }
        }
        this.customInventorySlots = new MergeSlotRange(customInvStart, this.inventorySlots.size() - customInvStart);
        
        this.onCraftMatrixChanged(craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory p_75130_1_)
    {
    	this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.player.worldObj));
        //this.detectAndSendChanges();
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
