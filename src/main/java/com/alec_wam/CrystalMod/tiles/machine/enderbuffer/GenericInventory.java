package com.alec_wam.CrystalMod.tiles.machine.enderbuffer;

import net.minecraftforge.items.ItemStackHandler;

public class GenericInventory extends ItemStackHandler
{
	private final IEnderBufferList list;
    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);
        list.setDirty();
    }

    public GenericInventory(int slots, IEnderBufferList list)
    {
        super(slots);
        this.list = list;
    }
}
