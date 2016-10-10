package com.alec_wam.CrystalMod.items.backpack.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.AchievementList;

import net.minecraftforge.items.IItemHandler;

public class SlotItemHandlerCraftresult extends SlotItemHandlerGeneric
{
    private final EntityPlayer player;
    private final InventoryCrafting craftMatrix;
    private int amountCrafted;

    public SlotItemHandlerCraftresult(EntityPlayer player, InventoryCrafting craftMatrix, IItemHandler craftResult, int index, int xPosition, int yPosition)
    {
        super(craftResult, index, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftMatrix;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.amountCrafted += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount)
    {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    @Override
    protected void onCrafting(ItemStack stack)
    {
        if (this.amountCrafted > 0)
        {
            stack.onCrafting(this.player.worldObj, this.player, this.amountCrafted);
        }

        this.amountCrafted = 0;

        if (stack.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
        {
            this.player.addStat(AchievementList.BUILD_WORK_BENCH);
        }

        if (stack.getItem() instanceof ItemPickaxe)
        {
            this.player.addStat(AchievementList.BUILD_PICKAXE);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.FURNACE))
        {
            this.player.addStat(AchievementList.BUILD_FURNACE);
        }

        if (stack.getItem() instanceof ItemHoe)
        {
            this.player.addStat(AchievementList.BUILD_HOE);
        }

        if (stack.getItem() == Items.BREAD)
        {
            this.player.addStat(AchievementList.MAKE_BREAD);
        }

        if (stack.getItem() == Items.CAKE)
        {
            this.player.addStat(AchievementList.BAKE_CAKE);
        }

        if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe)stack.getItem()).getToolMaterial() != Item.ToolMaterial.WOOD)
        {
            this.player.addStat(AchievementList.BUILD_BETTER_PICKAXE);
        }

        if (stack.getItem() instanceof ItemSword)
        {
            this.player.addStat(AchievementList.BUILD_SWORD);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.ENCHANTING_TABLE))
        {
            this.player.addStat(AchievementList.ENCHANTMENTS);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF))
        {
            this.player.addStat(AchievementList.BOOKCASE);
        }

        if (stack.getItem() == Items.GOLDEN_APPLE && stack.getMetadata() == 1)
        {
            this.player.addStat(AchievementList.OVERPOWERED);
        }
    }

    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
        this.onCrafting(stack);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
        ItemStack[] aitemstack = CraftingManager.getInstance().getRemainingItems(this.craftMatrix, playerIn.worldObj);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            ItemStack itemstack1 = aitemstack[i];

            if (itemstack != null)
            {
                this.craftMatrix.decrStackSize(i, 1);
            }

            if (itemstack1 != null)
            {
                if (this.craftMatrix.getStackInSlot(i) == null)
                {
                    this.craftMatrix.setInventorySlotContents(i, itemstack1);
                }
                else if (!this.player.inventory.addItemStackToInventory(itemstack1))
                {
                    this.player.dropItem(itemstack1, false);
                }
            }
        }
    }
}
