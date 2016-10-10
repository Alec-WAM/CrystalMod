package com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.alec_wam.CrystalMod.util.Lang;
import com.google.common.collect.Maps;

public class BasicCraftingTask implements ICraftingTask {
    public static final int ID = 0;

    public static final String NBT_SATISFIED = "Satisfied";
    public static final String NBT_CHECKED = "Checked";
    public static final String NBT_CHILD_TASKS = "ChildTasks";
    public static final String NBT_TOOK = "Took";

    private CraftingPattern pattern;
    private boolean satisfied[];
    private boolean checked[];
    private boolean childTasks[];
    private List<ItemStack> itemsTook = new ArrayList<ItemStack>();
    private boolean updatedOnce;

    public BasicCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getInputs().length];
        this.checked = new boolean[pattern.getInputs().length];
        this.childTasks = new boolean[pattern.getInputs().length];
    }

    public BasicCraftingTask(NBTTagCompound tag, CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = readBooleanArray(tag, NBT_SATISFIED);
        this.checked = readBooleanArray(tag, NBT_CHECKED);
        this.childTasks = readBooleanArray(tag, NBT_CHILD_TASKS);

        NBTTagList tookList = tag.getTagList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tookList.tagCount(); ++i) {
            itemsTook.add(ItemStack.loadItemStackFromNBT(tookList.getCompoundTagAt(i)));
        }
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public boolean update(EStorageNetwork controller) {
        this.updatedOnce = true;

        boolean done = true;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            checked[i] = true;

            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i]) {
                done = false;

                ItemStack took = controller.removeItemFromNetwork(input, false);

                if (took != null) {
                    itemsTook.add(took);

                    satisfied[i] = true;
                } else if (!childTasks[i]) {
                    CraftingPattern pattern = controller.getPatternWithBestScore(input);

                    if (pattern != null) {
                        controller.addCraftingTask(controller.createCraftingTask(pattern));

                        childTasks[i] = true;
                    }

                    //break;
                } else {
                    //break;
                }
            }
        }

        return done;
    }

    @Override
    public void onDone(EStorageNetwork controller) {
        for (ItemStack output : pattern.getOutputs()) {
            controller.addItemToNetwork(output, false);
        }

        if (pattern.getByproducts() != null) {
            for (ItemStack byproduct : pattern.getByproducts()) {
                controller.addItemToNetwork(byproduct, false);
            }
        }
    }

    @Override
    public void onCancelled(EStorageNetwork controller) {
        for (ItemStack took : itemsTook) {
        	controller.addItemToNetwork(took, false);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound patternTag = new NBTTagCompound();
        pattern.writeToNBT(patternTag);
        tag.setTag(CraftingPattern.NBT, patternTag);

        writeBooleanArray(tag, NBT_SATISFIED, satisfied);
        writeBooleanArray(tag, NBT_CHECKED, checked);
        writeBooleanArray(tag, NBT_CHILD_TASKS, childTasks);

        NBTTagList tookList = new NBTTagList();

        for (ItemStack took : itemsTook) {
            tookList.appendTag(took.serializeNBT());
        }

        tag.setTag(NBT_TOOK, tookList);

        tag.setInteger("Type", ID);
    }

    @Override
    public String getInfo() {
    	if (!updatedOnce) {
            return "T="+Lang.prefix+"gui.crafting_monitor.not_started_yet";
        }

        StringBuilder builder = new StringBuilder();

        boolean hasMissingItems = false;

        String tString = "";
        Map<ItemStack, Integer> missingCount = Maps.newHashMap();
        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (checked[i] && !satisfied[i] && !childTasks[i]) {
                if (!hasMissingItems) {
                    builder.append("I="+Lang.prefix+"gui.crafting_monitor.missing_items\n");

                    hasMissingItems = true;
                }
                
                boolean found = false;
                search : for(ItemStack st : missingCount.keySet()){
                	if(ItemUtil.canCombine(st, input)){
                		missingCount.put(st, missingCount.get(st)+input.stackSize);
                		found = true;
                		break search;
                	}
                }
                if(!found){
                	missingCount.put(input, input.stackSize);
                }
            }
        }
        for(Entry<ItemStack, Integer> entry : missingCount.entrySet()){
        	String unlocal = entry.getValue()+"x"+(entry.getKey().getUnlocalizedName())+(".name");
            tString+=unlocal+"&";
        }
        
        if(!tString.isEmpty()){
        	builder.append("T="+tString+"\n");
        }
        boolean areItemsCrafting = false;
        String cString = "";
        Map<ItemStack, Integer> craftingCount = Maps.newHashMap();
        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i] && childTasks[i]) {
                if (!areItemsCrafting) {
                    builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_crafting\n");

                    areItemsCrafting = true;
                }
                boolean found = false;
                search : for(ItemStack st : craftingCount.keySet()){
                	if(ItemUtil.canCombine(st, input)){
                		craftingCount.put(st, craftingCount.get(st)+input.stackSize);
                		found = true;
                		break search;
                	}
                }
                if(!found){
                	craftingCount.put(input, input.stackSize);
                }
            }
        }
        for(Entry<ItemStack, Integer> entry : craftingCount.entrySet()){
        	String unlocal = entry.getValue()+"x"+(entry.getKey().getUnlocalizedName())+(".name");
            cString+=unlocal+"&";
        }
        
        if(!cString.isEmpty()){
        	builder.append("T="+cString+"\n");
        }

        return builder.toString();
    }
    
    public static void writeBooleanArray(NBTTagCompound tag, String name, boolean[] array) {
        int[] intArray = new int[array.length];

        for (int i = 0; i < intArray.length; ++i) {
            intArray[i] = array[i] ? 1 : 0;
        }

        tag.setTag(name, new NBTTagIntArray(intArray));
    }

    public static boolean[] readBooleanArray(NBTTagCompound tag, String name) {
        int[] intArray = tag.getIntArray(name);

        boolean array[] = new boolean[intArray.length];

        for (int i = 0; i < intArray.length; ++i) {
            array[i] = intArray[i] == 1 ? true : false;
        }

        return array;
    }
}