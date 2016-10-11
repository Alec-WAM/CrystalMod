package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import net.minecraft.nbt.NBTTagCompound;

public interface ICraftingTask {
    CraftingPattern getPattern();

    boolean update(EStorageNetwork controller);

    void onDone(EStorageNetwork controller);

    void onCancelled(EStorageNetwork controller);

    void writeToNBT(NBTTagCompound tag);

    String getInfo();
}
