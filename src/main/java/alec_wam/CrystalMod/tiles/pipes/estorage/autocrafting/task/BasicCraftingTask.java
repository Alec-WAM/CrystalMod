package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

import com.google.common.collect.Maps;

public class BasicCraftingTask implements ICraftingTask {
    public static final int ID = 0;

    public static final String NBT_SATISFIED = "Satisfied";
    public static final String NBT_CHECKED = "Checked";
    public static final String NBT_CHILD_TASKS = "ChildTasks";
    public static final String NBT_TOOK = "Took";
    public static final String NBT_TOOK_FLUID = "TookFluid";

    private CraftingPattern pattern;
    private boolean satisfied[];
    private boolean checked[];
    private CraftingPattern childTasks[];
    private List<ItemStack> itemsTook = new ArrayList<ItemStack>();
    private List<FluidStack> fluidsTook = new ArrayList<FluidStack>();
    private boolean updatedOnce;

    public BasicCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getInputs().length];
        this.checked = new boolean[pattern.getInputs().length];
        this.childTasks = new CraftingPattern[pattern.getInputs().length];
    }

    public BasicCraftingTask(NBTTagCompound tag, CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = readBooleanArray(tag, NBT_SATISFIED);
        this.checked = readBooleanArray(tag, NBT_CHECKED);
        this.childTasks = readPatternArray(tag, NBT_CHILD_TASKS);

        NBTTagList tookList = tag.getTagList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tookList.tagCount(); ++i) {
            itemsTook.add(ItemStack.loadItemStackFromNBT(tookList.getCompoundTagAt(i)));
        }
        
        NBTTagList tookFluidList = tag.getTagList(NBT_TOOK_FLUID, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tookFluidList.tagCount(); ++i) {
            fluidsTook.add(FluidStack.loadFluidStackFromNBT(tookFluidList.getCompoundTagAt(i)));
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

                ItemStack took = controller.getItemStorage().removeItem(input, false);
                
                if (took != null) {
                    itemsTook.add(took);

                    satisfied[i] = true;
                } else if (childTasks[i] == null) {
                    CraftingPattern pattern = controller.getPatternWithBestScore(input);

                    if (pattern != null) {
                        controller.addCraftingTask(controller.createCraftingTask(pattern));

                        childTasks[i] = pattern;
                    } else {
                    	FluidStack fluid = FluidUtil.getFluidTypeFromItem(input);
                    	if(fluid !=null){
                    		ItemStack emptyContainer = FluidUtil.EMPTY_BUCKET;
                    		FluidStackData storedFluid = controller.getFluidStorage().getFluidData(fluid);
                    		if(emptyContainer !=null && storedFluid !=null && storedFluid.getAmount() >= fluid.amount){
                    			boolean hasBucket = controller.getItemStorage().hasItem(emptyContainer);
                    			if(!hasBucket){
	                    			CraftingPattern patternBucket = controller.getPatternWithBestScore(emptyContainer);
	                                if (patternBucket != null) {
	                                	controller.addCraftingTask(controller.createCraftingTask(patternBucket));
	                                	childTasks[i] = patternBucket;
	                                }
                    			} else {
                    				ItemStack tookBucket = controller.getItemStorage().removeItem(emptyContainer, false);
                                    if (tookBucket != null) {
                                        itemsTook.add(tookBucket);
                                        FluidStack tookFluid = controller.getFluidStorage().removeFluid(fluid, false);
                                        fluidsTook.add(tookFluid);
                                        satisfied[i] = true;
                                    }
                    			}
                    		}
                    	}
                    }
                    //break;
                } else {
                    //break;
                }
            }
        }

        return done;
    }
    
    public void handleFluid(ItemStack input){
    	
    }

    @Override
    public void onDone(EStorageNetwork controller) {
        for (ItemStack output : pattern.getOutputs()) {
            controller.getItemStorage().addItem(output, false);
        }

        if (pattern.getByproducts() != null) {
            for (ItemStack byproduct : pattern.getByproducts()) {
                controller.getItemStorage().addItem(byproduct, false);
            }
        }
    }

    @Override
    public void onCancelled(EStorageNetwork controller) {
        for (ItemStack took : itemsTook) {
        	controller.getItemStorage().addItem(took, false);
        }
        for (FluidStack took : fluidsTook) {
        	controller.getFluidStorage().addFluid(took, false);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound patternTag = new NBTTagCompound();
        pattern.writeToNBT(patternTag);
        tag.setTag(CraftingPattern.NBT, patternTag);

        writeBooleanArray(tag, NBT_SATISFIED, satisfied);
        writeBooleanArray(tag, NBT_CHECKED, checked);
        writePatternArray(tag, NBT_CHILD_TASKS, childTasks);

        NBTTagList tookList = new NBTTagList();

        for (ItemStack took : itemsTook) {
            tookList.appendTag(took.serializeNBT());
        }

        tag.setTag(NBT_TOOK, tookList);
        
        NBTTagList tookFluidList = new NBTTagList();

        for (FluidStack took : fluidsTook) {
        	tookFluidList.appendTag(took.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_TOOK_FLUID, tookFluidList);

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

            if (checked[i] && !satisfied[i] && childTasks[i] == null) {
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

            if (!satisfied[i] && childTasks[i] !=null) {
            	ItemStack realItem = childTasks[i].getOutputs()[0];
            	if(realItem == null){
            		realItem = input;
            	}
                if (!areItemsCrafting) {
                    builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_crafting\n");

                    areItemsCrafting = true;
                }
                boolean found = false;
                search : for(ItemStack st : craftingCount.keySet()){
                	if(ItemUtil.canCombine(st, realItem)){
                		craftingCount.put(st, craftingCount.get(st)+realItem.stackSize);
                		found = true;
                		break search;
                	}
                }
                if(!found){
                	craftingCount.put(realItem, realItem.stackSize);
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
    
    public static void writePatternArray(NBTTagCompound tag, String name, CraftingPattern[] array) {
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < array.length; ++i) {
        	NBTTagCompound rtag = new NBTTagCompound();
        	if(array[i] !=null){
	        	NBTTagCompound patternTag = new NBTTagCompound();
	            array[i].writeToNBT(patternTag);
	            rtag.setTag("Pattern", patternTag);
        	}
            rtag.setInteger("Index", i);
            list.appendTag(rtag);
        }

        tag.setTag(name, list);
    }

    public static CraftingPattern[] readPatternArray(NBTTagCompound tag, String name) {
    	NBTTagList list = tag.getTagList(name, Constants.NBT.TAG_COMPOUND);

        CraftingPattern array[] = new CraftingPattern[list.tagCount()];

        for (int i = 0; i < array.length; ++i) {
        	NBTTagCompound rtag = list.getCompoundTagAt(i);
        	CraftingPattern pattern = null;
        	if(rtag.hasKey("Pattern")){
        		pattern = CraftingPattern.readFromNBT(rtag.getCompoundTag("Pattern"));
        	}
            array[rtag.getInteger("Index")] = pattern;
        }

        return array;
    }
}