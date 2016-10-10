package com.alec_wam.CrystalMod.items.backpack.container;

public class MergeSlotRange extends SlotRange
{
    public final boolean existingOnly;

    public MergeSlotRange (int start, int numSlots)
    {
        this(start, numSlots, false);
    }

    public MergeSlotRange (int start, int numSlots, boolean existingOnly)
    {
        super(start, numSlots);
        this.existingOnly = existingOnly;
    }
}
