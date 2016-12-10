package alec_wam.CrystalMod.tiles.workbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.crafting.CrystalCraftingManager;
import alec_wam.CrystalMod.util.ItemStackTools;

public class ContainerCrystalWorkbench extends Container
{
    /** The crafting matrix inventory (3x3). */
    public InventoryCraftingPersistent craftMatrix;
    public IInventory craftResult;
    private World worldObj;
    /** Position of the workbench */
    public BlockPos pos;
    public TileEntityCrystalWorkbench bench;

    public ContainerCrystalWorkbench(final InventoryPlayer playerInventory, World worldIn, TileEntityCrystalWorkbench tile)
    {
        this.worldObj = worldIn;
        bench = tile;
        this.pos = tile.getPos();
        
        craftResult = new InventoryCraftResult();
        craftMatrix = new InventoryCraftingPersistent(this, tile, 3, 3);
        
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35){
        	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
            {
                //net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
                this.onCrafting(stack);
                //net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
                ItemStack[] aitemstack = CrystalCraftingManager.getInstance().func_180303_b(craftMatrix, playerIn.worldObj);
                //net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

                for (int i = 0; i < aitemstack.length; ++i)
                {
                    ItemStack itemstack = craftMatrix.getStackInSlot(i);
                    ItemStack itemstack1 = aitemstack[i];

                    if (!ItemStackTools.isNullStack(itemstack))
                    {
                        craftMatrix.decrStackSize(i, 1);
                    }

                    if (!ItemStackTools.isNullStack(itemstack1))
                    {
                        if (ItemStackTools.isNullStack(craftMatrix.getStackInSlot(i)))
                        {
                            craftMatrix.setInventorySlotContents(i, itemstack1);
                        }
                        else if (!playerInventory.player.inventory.addItemStackToInventory(itemstack1))
                        {
                        	playerInventory.player.dropItem(itemstack1, false);
                        }
                    }
                }
            }
        });

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.craftResult.setInventorySlotContents(0, CrystalCraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.worldObj.getBlockState(this.pos).getBlock() != ModBlocks.crystalWorkbench ? false : playerIn.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 10, 46, false))
                {
                    return ItemStackTools.getEmptyStack();
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
            {
                return ItemStackTools.getEmptyStack();
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

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot p_94530_2_)
    {
        return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(stack, p_94530_2_);
    }
}