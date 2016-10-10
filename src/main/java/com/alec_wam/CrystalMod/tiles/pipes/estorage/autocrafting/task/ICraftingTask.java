package com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;

import net.minecraft.nbt.NBTTagCompound;

public interface ICraftingTask {
    CraftingPattern getPattern();

    boolean update(EStorageNetwork controller);

    void onDone(EStorageNetwork controller);

    void onCancelled(EStorageNetwork controller);

    void writeToNBT(NBTTagCompound tag);

    String getInfo();
}
