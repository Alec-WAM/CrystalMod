package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.List;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import net.minecraft.nbt.NBTTagCompound;

public interface ICraftingTask {
	public static final String NBT_PATTERN = "PatternStack";
	public static final String NBT_CRAFTER = "CrafterPos";
	public static final String NBT_REQUESTED = "Quantity";
	public static final String NBT_QUANTITY = "Quantity";
	
    CraftingPattern getPattern();

    void calculate(EStorageNetwork network);
    
    boolean update(EStorageNetwork controller);

    void onCancelled(EStorageNetwork controller);

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    String getInfo();

	List<CraftingProcessBase> getToProcess();
}
