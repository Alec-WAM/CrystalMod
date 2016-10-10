package com.alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SlotCraftingPanelResult extends SlotCrafting {
    private ContainerPanelCrafting container;
    private TileEntityPanelCrafting panel;

    public SlotCraftingPanelResult(ContainerPanelCrafting container, EntityPlayer player, TileEntityPanelCrafting panel, int id, int x, int y) {
        super(player, panel.getMatrix(), panel.getResult(), id, x, y);

        this.container = container;
        this.panel = panel;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, panel.getMatrix());

        onCrafting(stack);

        if (!player.worldObj.isRemote) {
        	panel.onCrafted(player);

            container.sendCraftingSlots();
        }
    }
}
