package alec_wam.CrystalMod.tiles.workbench;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.crafting.CrystalCraftingManager;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        	@Override
        	public ItemStack onTake(EntityPlayer playerIn, ItemStack stack)
            {
                //net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
                this.onCrafting(stack);
                //net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
                NonNullList<ItemStack> aitemstack = CrystalCraftingManager.getInstance().getRemainingItems(craftMatrix, playerIn.getEntityWorld());
                //net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

                for (int i = 0; i < aitemstack.size(); ++i)
                {
                    ItemStack itemstack = craftMatrix.getStackInSlot(i);
                    ItemStack itemstack1 = aitemstack.get(i);

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
                return stack;
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
    @Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.craftResult.setInventorySlotContents(0, CrystalCraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    @Override
	public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
    }

    @Override
	public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.worldObj.getBlockState(this.pos).getBlock() != ModBlocks.crystalWorkbench ? false : playerIn.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = this.inventorySlots.get(index);

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

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    @Override
	public boolean canMergeSlot(ItemStack stack, Slot p_94530_2_)
    {
        return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(stack, p_94530_2_);
    }
}