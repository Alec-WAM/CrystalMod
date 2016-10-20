package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.ItemUtil;

public class CraftingProcess{
	private static final String NBT_SATISFIED = "Satisfied_%d";
	private static final String NBT_TO_INSERT = "ToInsert";
	
    private CraftingPattern pattern;
    private List<ItemStack> toInsert = Lists.newArrayList();
    private boolean satisfied[];
	public boolean started;

    public CraftingProcess(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().size()];

        for (ItemStack input : pattern.getInputs()) {
            if (input != null) {
                toInsert.add(input.copy());
            }
        }
    }
    
    public CraftingProcess(CraftingPattern pattern, NBTTagCompound tag) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().size()];

        for (int i = 0; i < satisfied.length; ++i) {
            String id = String.format(NBT_SATISFIED, i);

            if (tag.hasKey(id)) {
                this.satisfied[i] = tag.getBoolean(id);
            }
        }

        NBTTagList toInsertList = tag.getTagList(NBT_TO_INSERT, Constants.NBT.TAG_COMPOUND);

        List<ItemStack> toInsert = new ArrayList<ItemStack>();

        for (int i = 0; i < toInsertList.tagCount(); ++i) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(toInsertList.getCompoundTagAt(i));

            if (stack != null) {
                toInsert.add(stack);
            }
        }

        this.toInsert = new ArrayList<ItemStack>(toInsert);
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public List<ItemStack> getToInsert() {
        return toInsert;
    }

    public boolean hasReceivedOutputs() {
        for (boolean item : satisfied) {
            if (!item) {
                return false;
            }
        }

        return true;
    }

    public boolean hasReceivedOutput(int i) {
        return satisfied[i];
    }

    public boolean onReceiveOutput(ItemStack stack) {
        for (int i = 0; i < pattern.getOutputs().size(); ++i) {
            if (!satisfied[i]) {
                ItemStack item = pattern.getOutputs().get(i);

                if (ItemUtil.canCombine(stack, item)) {
                    satisfied[i] = true;

                    return true;
                }
            }
        }

        return false;
    }
    
    public boolean canStartProcessing(ItemStorage storage){
        for (ItemStack stack : toInsert) {
            ItemStackData data = storage.getItemData(stack);
            if(data == null && pattern.isOredict()){
            	data = storage.getOreItemData(stack);
            }
            //list.get(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0));
            if (data == null || data.getAmount() == 0 || storage.removeItem(data, stack.stackSize, true) !=stack.stackSize) {
                return false;
            }
        }
        return true;
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        for (int i = 0; i < satisfied.length; ++i) {
            tag.setBoolean(String.format(NBT_SATISFIED, i), satisfied[i]);
        }

        NBTTagList toInsertList = new NBTTagList();

        for (ItemStack stack : new ArrayList<ItemStack>(toInsert)) {
            toInsertList.appendTag(stack.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT, toInsertList);

        return tag;
    }
}
