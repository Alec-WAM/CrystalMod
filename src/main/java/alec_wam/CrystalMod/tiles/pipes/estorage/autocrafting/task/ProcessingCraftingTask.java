package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.IAutoCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCrafter;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

import com.google.common.collect.Maps;

public class ProcessingCraftingTask implements ICraftingTask {
    public static final int ID = 1;

    public static final String NBT_INSERTED = "Inserted";
    public static final String NBT_CHILD_TASKS = "ChildTasks";
    public static final String NBT_SATISFIED = "Satisfied";

    private CraftingPattern pattern;
    private boolean inserted[];
    private boolean childTasks[];
    private boolean satisfied[];
    private boolean updatedOnce;

    public ProcessingCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.inserted = new boolean[pattern.getInputs().length];
        this.childTasks = new boolean[pattern.getInputs().length];
        this.satisfied = new boolean[pattern.getOutputs().length];
    }

    public ProcessingCraftingTask(NBTTagCompound tag, CraftingPattern pattern) {
        this.pattern = pattern;
        this.inserted = readBooleanArray(tag, NBT_INSERTED);
        this.childTasks = readBooleanArray(tag, NBT_CHILD_TASKS);
        this.satisfied = readBooleanArray(tag, NBT_SATISFIED);
    }

    @Override
    public CraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean update(EStorageNetwork controller) {
    	
    	World world = DimensionManager.isDimensionRegistered(getPattern().crafterDim) ? FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(getPattern().crafterDim) : null;
    	if(world == null){
    		return false;
    	}
        this.updatedOnce = true;

        IAutoCrafter acrafter = pattern.getCrafter(world);
        if(!(acrafter instanceof TileCrafter))return false;
        TileCrafter crafter = (TileCrafter)acrafter;
        IItemHandler handler = ItemUtil.getItemHandler(crafter.getFacingTile(), crafter.getDirection().getOpposite());

        
            for (int i = 0; i < inserted.length; ++i) {
                if (!inserted[i]) {
                    ItemStack input = pattern.getInputs()[i];
                    ItemStack took = controller.removeItemFromNetwork(input, true);
                    
                    if (took != null) {
                    	if (handler != null) {
	                    	ItemStack sim = ItemHandlerHelper.insertItem(handler, took, true);
	                    	if ( sim == null) {
	                            ItemHandlerHelper.insertItem(handler, took, false);
	                            controller.removeItemFromNetwork(input, false);
	                            inserted[i] = true;
	                        }
                    	}
                    } else if (!childTasks[i]) {
                        CraftingPattern pattern = controller.getPatternWithBestScore(input);

                        if (pattern != null) {
                            childTasks[i] = true;

                            controller.addCraftingTask(controller.createCraftingTask(pattern));

                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
         /*else {
            return true;
        }*/

        for (int i = 0; i < satisfied.length; ++i) {
            if (!satisfied[i]) {
                return false;
            }
        }

        return true;
    }

    public void onPushed(ItemStack inserted) {
        for (int i = 0; i < pattern.getOutputs().length; ++i) {
            if (!satisfied[i] && ItemUtil.canCombine(inserted, pattern.getOutputs()[i])) {
                satisfied[i] = true;

                return;
            }
        }
    }

    @Override
    public void onDone(EStorageNetwork controller) {
        // NO OP
    }

    @Override
    public void onCancelled(EStorageNetwork controller) {
        // NO OP
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound patternTag = new NBTTagCompound();
        pattern.writeToNBT(patternTag);
        tag.setTag(CraftingPattern.NBT, patternTag);

        writeBooleanArray(tag, NBT_INSERTED, inserted);
        writeBooleanArray(tag, NBT_CHILD_TASKS, childTasks);
        writeBooleanArray(tag, NBT_SATISFIED, satisfied);

        tag.setInteger("Type", ID);
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

            if (!inserted[i] && !childTasks[i]) {
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
        	String unlocal = (entry.getValue() > 1 ? entry.getValue()+"x" : "")+(entry.getKey().getUnlocalizedName())+(".name");
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

            if (!inserted[i] && childTasks[i]) {
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
        	String unlocal = (entry.getValue() > 1 ? entry.getValue()+"x" : "")+(entry.getKey().getUnlocalizedName())+(".name");
            cString+=unlocal+"&";
        }
        
        if(!cString.isEmpty()){
        	builder.append("T="+cString+"\n");
        }
        boolean areItemsProcessing = false;
        String pString = "";
        Map<ItemStack, Integer> processCount = Maps.newHashMap();
        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (inserted[i]) {
                if (!areItemsProcessing) {
                    builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_processing\n");

                    areItemsProcessing = true;
                }

                boolean found = false;
                search : for(ItemStack st : processCount.keySet()){
                	if(ItemUtil.canCombine(st, input)){
                		processCount.put(st, processCount.get(st)+input.stackSize);
                		found = true;
                		break search;
                	}
                }
                if(!found){
                	processCount.put(input, input.stackSize);
                }
            }
        }
        
        for(Entry<ItemStack, Integer> entry : processCount.entrySet()){
        	String unlocal = (entry.getValue() > 1 ? entry.getValue()+"x" : "")+(entry.getKey().getUnlocalizedName())+(".name");
            pString+=unlocal+"&";
        }
        
        if(!pString.isEmpty()){
        	builder.append("T="+pString+"\n");
        }

        return builder.toString();
    }
}