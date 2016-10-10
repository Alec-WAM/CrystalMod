package com.alec_wam.CrystalMod.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class BasicItemHandler extends ItemStackHandler {
    private TileEntity tile;
    private IItemValidator[] validators;

    public BasicItemHandler(int size, TileEntity tile, IItemValidator validator) {
        super(size);

        this.tile = tile;
        this.validators = new IItemValidator[]{validator};
    }
    
    public BasicItemHandler(int size, TileEntity tile, IItemValidator... validators) {
        super(size);

        this.tile = tile;
        this.validators = validators;
    }

    public BasicItemHandler(int size, IItemValidator... validators) {
        this(size, null, validators);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        boolean mayInsert = validators.length > 0 ? false : true;

        for (IItemValidator validator : validators) {
            if (validator.valid(stack)) {
                mayInsert = true;

                break;
            }
        }

        if (mayInsert) {
            return super.insertItem(slot, stack, simulate);
        }

        return stack;
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (tile != null) {
            tile.markDirty();
        }
    }
}
