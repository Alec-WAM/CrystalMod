package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Maps;

public class BasicCraftingTask implements ICraftingTask {
    public static final int ID = 0;

    public static final String NBT_SATISFIED = "Satisfied";
    public static final String NBT_CHECKED = "Checked";
    public static final String NBT_CHILD_TASKS = "ChildTasks";
    public static final String NBT_TOOK = "Took";
    public static final String NBT_TOOK_FLUID = "TookFluid";

    private CraftingPattern pattern;
    private int quantity;
    
    private ItemStack requested;
    private List<ItemStack> extras = new ArrayList<ItemStack>();
    private List<ItemStack> missing = new ArrayList<ItemStack>();
    private Deque<ItemStack> toInsert = new ArrayDeque<ItemStack>();
    private List<ItemStack> toTake = new ArrayList<ItemStack>();
    private List<FluidStack> toTakeFluid = new ArrayList<FluidStack>();
    private List<ItemStack> itemsTook = new ArrayList<ItemStack>();
    private List<FluidStack> fluidsTook = new ArrayList<FluidStack>();
    private List<CraftingProcess> toProcess = new ArrayList<CraftingProcess>();
    private boolean updatedOnce;

    public BasicCraftingTask(ItemStack requested, CraftingPattern pattern, int quantity) {
    	this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;
    }

    public BasicCraftingTask(NBTTagCompound tag, CraftingPattern pattern) {
        this.pattern = pattern;

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

    public boolean update(final EStorageNetwork controller) {
        if(updatedOnce == false){
        	Thread calculationThread = new Thread(requested.getUnlocalizedName()+"x"+quantity+" Calculate Thread"){
        		public void run()
                {
        			try{
        				calculate(controller);
        			} catch(Exception e){
        				ModLogger.warning(requested.getUnlocalizedName()+"x"+quantity+" Calculation Thread experienced the following exception");
        				e.printStackTrace();
        			}
                }
        	};
        	calculate(controller);
        	//calculationThread.start();
        }
    	updatedOnce = true;
    	for (CraftingProcess process : toProcess) {
            IItemHandler inventory = process.getPattern().getCrafter().getFacingInventory();

            if (!process.started && inventory !=null && canProcess(controller, process)) {
            	process.started = true;
            	for (ItemStack insertStack : process.getToInsert()) {
	            	ItemStackData data = controller.getItemStorage().getItemData(insertStack);
	            	if(data == null && pattern.isOredict()){
	            		data = controller.getItemStorage().getOreItemData(insertStack);
	            	}
	            	if(data !=null){
		            	ItemStack toInsert = controller.getItemStorage().removeItemSpecial(data, 1, false);
		
		                if (ItemHandlerHelper.insertItem(inventory, toInsert, true) == null) {
		                    ItemHandlerHelper.insertItem(inventory, toInsert, false);
		                }
	            	}
            	}
            }
        }
    	
    	for (ItemStack stack : toTake) {
        	ItemStackData data = controller.getItemStorage().getItemData(stack);
        	
        	if(data == null && getPattern().isOredict()){
        		data = controller.getItemStorage().getOreItemData(stack);
        	}
        	
        	ItemStack stackExtracted = controller.getItemStorage().removeItemSpecial(data, Math.min(stack.stackSize, 64), false);

            if (stackExtracted != null) {
                stack.stackSize-=stackExtracted.stackSize;
                if(stack.stackSize <=0){
                	toTake.remove(stack);
                }

                itemsTook.add(stackExtracted);
                break;
            }
        }
    	
    	if (toTake.isEmpty()) {
            for (FluidStack stack : toTakeFluid) {
                FluidStack stackExtracted = controller.getFluidStorage().removeFluid(stack, false);

                if (stackExtracted != null) {
                	toTakeFluid.remove(stack);
                    fluidsTook.add(stackExtracted);
                    break;
                }
            }
        }

        if (isFinished()) {
        	if(!toInsert.isEmpty()){
	            ItemStack insert = toInsert.peek();
	
	            if (controller.getItemStorage().addItem(insert, true) == insert.stackSize) {
	            	controller.getItemStorage().addItem(insert, false);
	
	                toInsert.pop();
	            }
        	}
            return toInsert.isEmpty();
        }
    	return false;
    }
    
    private boolean canProcess(EStorageNetwork network, CraftingProcess process) {
        for (ICraftingTask otherTask : network.getCraftingTasks()) {
            for (CraftingProcess otherProcess : otherTask.getToProcess()) {
                if (otherProcess != process && !otherProcess.hasReceivedOutputs() && otherProcess.started) {
                    if (!isPatternsEqual(process.getPattern(), otherProcess.getPattern())) {
                        if (process.getPattern().getCrafter().getFacingPos().equals(otherProcess.getPattern().getCrafter().getFacingPos())) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
    
    private boolean isPatternsEqual(CraftingPattern left, CraftingPattern right) {
        for (int i = 0; i < 9; ++i) {
            ItemStack leftStack = i >= left.getInputs().size() ? null : left.getInputs().get(i);
            ItemStack rightStack = i >= right.getInputs().size() ? null : right.getInputs().get(i);

            if (!ItemUtil.canCombine(leftStack, rightStack)) {
                return false;
            }
        }

        return true;
    }
    
    public void calculate(EStorageNetwork network) {
    	int newQuantity = quantity;
       	while (newQuantity > 0) {
            calculate(network, pattern, true);
            
            if(!pattern.isProcessing()){
	            for (ItemStack output : pattern.getOutputs()) {
	            	ItemStack add = output.copy();
	                toInsert.add(add);
	            }
            }
	
            newQuantity -= requested == null ? newQuantity : pattern.getQuantityPerRequest(requested);
       	}

	    for (ItemStack extra : extras) {
	        toInsert.add(extra);
	    }
    }

    private void calculate(EStorageNetwork network, CraftingPattern pattern, boolean basePattern) {
        ItemStack[] took = new ItemStack[9];

        if (pattern.isProcessing()) {
            toProcess.add(new CraftingProcess(pattern));
        }

        if (!basePattern) {
        	for(ItemStack out : pattern.getOutputs()){
        		if(out !=null && out.stackSize > 1){
        			extras.add(ItemHandlerHelper.copyStackWithSize(out, out.stackSize - 1));
        		}
        	}
        }

        for (int i = 0; i < pattern.getInputs().size(); ++i) {
            ItemStack input = pattern.getInputs().get(i);

            ItemStackData inputInNetwork = network.getItemStorage().getItemData(input);

            if(inputInNetwork == null && pattern.isOredict()){
            	inputInNetwork = network.getItemStorage().getOreItemData(input);
            }
            
            if (inputInNetwork == null || inputInNetwork.getAmount() == 0) {
            	ItemStack extra = null;//extras.get(input, compare);

                search : for(ItemStack ex : extras){
                	if(pattern.isOredict() ? ItemUtil.stackMatchUseOre(ex, input) : ItemUtil.canCombine(ex, input)){
                		extra = ex;
                		break search;
                	}
                }
                
                if (extra != null) {
                    ItemStack extraToRemove = ItemHandlerHelper.copyStackWithSize(extra, 1);

                    if (!pattern.isProcessing()) {
                        took[i] = extraToRemove;
                    }

                    extras.remove(extraToRemove);
                } else {
                    CraftingPattern inputPattern = network.getPatternWithBestScore(input);

                    if (inputPattern != null) {
                        calculate(network, inputPattern, false);
                    } else {
                        FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(input);
                        if (fluidInItem != null) {
                        	ItemStack container = FluidUtil.getEmptyContainer(input);
                        	if(container !=null){
	                            FluidStackData fluidInStorage = network.getFluidStorage().getFluidData(fluidInItem);
	                            
	                            if (fluidInStorage == null || fluidInStorage.getAmount() < fluidInItem.amount) {
	                                missing.add(input);
	                            } else {
	                                boolean hasBucket = network.getItemStorage().hasItem(container);
	                                CraftingPattern bucketPattern = network.getPatternWithBestScore(container);
	
	                                if (!hasBucket) {
	                                    if (bucketPattern == null) {
	                                        missing.add(container.copy());
	                                    } else {
	                                        calculate(network, bucketPattern, false);
	                                    }
	                                }
	
	                                if (hasBucket || bucketPattern != null) {
	                                	//if(hasBucket)toTake.add(container);
	                                    toTakeFluid.add(fluidInItem.copy());
	                                }
	                            }
                        	}
                        } else {
                            missing.add(input);
                        }
                    }
                }
            } else {
                if (!pattern.isProcessing()) {
                    ItemStack take = ItemHandlerHelper.copyStackWithSize(inputInNetwork.stack, 1);

                    toTake.add(take);

                    took[i] = take;
                }
            }
        }

        for (ItemStack byproduct : (pattern.isOredict() ? pattern.getByproducts(took) : pattern.getByproducts())) {
        	extras.add(byproduct.copy());
        }
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    private boolean isFinished() {
        return toTake.isEmpty() && toTakeFluid.isEmpty() && missing.isEmpty() && hasProcessedItems();
    }
    
    private boolean hasProcessedItems() {
    	for(CraftingProcess process : toProcess){
    		if(!process.hasReceivedOutputs()){
    			return false;
    		}
    	}
        return true;
    }

    @Override
    public void onDone(EStorageNetwork controller) {
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

        
        if (isFinished()) {
        	boolean hasTitle = false;
        	String tString = "";
            Map<ItemStack, Integer> insertCount = Maps.newHashMap();
            Iterator<ItemStack> it = toInsert.iterator();
            while (it.hasNext()) {
                ItemStack input = it.next();

                if (!hasTitle) {
                    builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_inserting\n");

                    hasTitle = true;
                }
                
                boolean found = false;
                search : for(ItemStack st : insertCount.keySet()){
                	if(ItemUtil.canCombine(st, input)){
                		insertCount.put(st, insertCount.get(st)+input.stackSize);
                		found = true;
                		break search;
                	}
                }
                if(!found){
                	insertCount.put(input, input.stackSize);
                }
            }
            for(Entry<ItemStack, Integer> entry : insertCount.entrySet()){
            	String unlocal = entry.getValue()+"x"+(entry.getKey().getUnlocalizedName())+(".name");
                tString+=unlocal+"&";
            }
            
            if(!tString.isEmpty()){
            	builder.append("T="+tString+"\n");
            }
        } else {
        	boolean addMissing = true;
        	boolean addTaking = true;
        	boolean addTakingFluid = true;
        	boolean addProcessing = true;
        	if(addMissing){
	        	boolean hasTitle = false;
	        	String tString = "";
	            Map<ItemStack, Integer> insertCount = Maps.newHashMap();
	            Iterator<ItemStack> it = missing.iterator();
	            while (it.hasNext()) {
	                ItemStack input = it.next();
	
	                if (!hasTitle) {
	                	builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_missing\n");
	
	                    hasTitle = true;
	                }
	                
	                boolean found = false;
	                search : for(ItemStack st : insertCount.keySet()){
	                	if(ItemUtil.canCombine(st, input)){
	                		insertCount.put(st, insertCount.get(st)+input.stackSize);
	                		found = true;
	                		break search;
	                	}
	                }
	                if(!found){
	                	insertCount.put(input, input.stackSize);
	                }
	            }
	            for(Entry<ItemStack, Integer> entry : insertCount.entrySet()){
	            	String unlocal = entry.getValue()+"x"+(entry.getKey().getUnlocalizedName())+(".name");
	                tString+=unlocal+"&";
	            }
	            
	            if(!tString.isEmpty()){
	            	builder.append("T="+tString+"\n");
	            }
        	}
        	if(addTaking){
	        	boolean hasTitle = false;
	        	String tString = "";
	            Map<ItemStack, Integer> insertCount = Maps.newHashMap();
	            Iterator<ItemStack> it = toTake.iterator();
	            while (it.hasNext()) {
	                ItemStack input = it.next();
	
	                if (!hasTitle) {
	                	builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_takeing\n");
	
	                    hasTitle = true;
	                }
	                
	                boolean found = false;
	                search : for(ItemStack st : insertCount.keySet()){
	                	if(ItemUtil.canCombine(st, input)){
	                		insertCount.put(st, insertCount.get(st)+input.stackSize);
	                		found = true;
	                		break search;
	                	}
	                }
	                if(!found){
	                	insertCount.put(input, input.stackSize);
	                }
	            }
	            for(Entry<ItemStack, Integer> entry : insertCount.entrySet()){
	            	String unlocal = entry.getValue()+"x"+(entry.getKey().getUnlocalizedName())+(".name");
	                tString+=unlocal+"&";
	            }
	            
	            if(!tString.isEmpty()){
	            	builder.append("T="+tString+"\n");
	            }
        	}
        	if(addTakingFluid){
	        	boolean hasTitle = false;
	        	String tString = "";
	            Map<FluidStack, Integer> insertCount = Maps.newHashMap();
	            Iterator<FluidStack> it = toTakeFluid.iterator();
	            while (it.hasNext()) {
	                FluidStack input = it.next();
	
	                if (!hasTitle) {
	                    builder.append("I="+Lang.prefix+"gui.crafting_monitor.fluids_takeing\n");
	
	                    hasTitle = true;
	                }
	                
	                boolean found = false;
	                search : for(FluidStack st : insertCount.keySet()){
	                	if(FluidUtil.canCombine(st, input)){
	                		insertCount.put(st, insertCount.get(st)+input.amount);
	                		found = true;
	                		break search;
	                	}
	                }
	                if(!found){
	                	insertCount.put(input, input.amount);
	                }
	            }
	            for(Entry<FluidStack, Integer> entry : insertCount.entrySet()){
	            	String unlocal = (entry.getKey().getUnlocalizedName())+(".name")+"x "+entry.getValue()+"mB";
	                tString+=unlocal+"&";
	            }
	            
	            if(!tString.isEmpty()){
	            	builder.append("T="+tString+"\n");
	            }
        	}
        	if(addProcessing){
	        	boolean hasTitle = false;
	        	String tString = "";
	            Map<ItemStack, Integer> insertCount = Maps.newHashMap();
	            for(CraftingProcess process : toProcess){
		            for (int i = 0; i < process.getPattern().getOutputs().size(); ++i) {
		            	if (!process.hasReceivedOutput(i)) {
		            		ItemStack input = process.getPattern().getOutputs().get(i);
			                if (!hasTitle) {
			                    builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_processing\n");
			
			                    hasTitle = true;
			                }
			                
			                boolean found = false;
			                search : for(ItemStack st : insertCount.keySet()){
			                	if(ItemUtil.canCombine(st, input)){
			                		insertCount.put(st, insertCount.get(st)+input.stackSize);
			                		found = true;
			                		break search;
			                	}
			                }
			                if(!found){
			                	insertCount.put(input, input.stackSize);
			                }
		            	}
		            }
	            }
	            for(Entry<ItemStack, Integer> entry : insertCount.entrySet()){
	            	String unlocal = entry.getValue()+"x"+(entry.getKey().getUnlocalizedName())+(".name");
	                tString+=unlocal+"&";
	            }
	            
	            if(!tString.isEmpty()){
	            	builder.append("T="+tString+"\n");
	            }
        	}
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

	@Override
	public List<CraftingProcess> getToProcess() {
		return toProcess;
	}
}