package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

//HUGE credit to https://github.com/raoulvdberge/refinedstorage way2muchnoise

public class BasicCraftingTask implements ICraftingTask {
    private CraftingPattern pattern;
    private int quantity;
    
    private ItemStack requested;
    private List<ItemStack> missing = new ArrayList<ItemStack>();
    private Deque<ItemStack> toInsertItems = new ArrayDeque<ItemStack>();
    private Deque<FluidStack> toInsertFluids = new ArrayDeque<FluidStack>();
    private List<ItemStack> toTake = new ArrayList<ItemStack>();
    private List<FluidStack> toTakeFluid = new ArrayList<FluidStack>();
    private FluidStackList fluidsTook = new FluidStackList();
    private List<CraftingProcessBase> toProcess = new ArrayList<CraftingProcessBase>();
    private boolean updatedOnce;
    private Set<CraftingPattern> usedPatterns = new HashSet<CraftingPattern>();
    private boolean recursiveFind;

    public BasicCraftingTask(ItemStack requested, CraftingPattern pattern, int quantity) {
    	this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;
    }
    
    public BasicCraftingTask(ItemStack requested, CraftingPattern pattern, int quantity, List<CraftingProcessBase> toProcess, Deque<ItemStack> toInsertItems, List<FluidStack> toTakeFluids, FluidStackList tookFluids, Deque<FluidStack> toInsertFluids) {
    	this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;
        this.toProcess = toProcess;
        this.toInsertItems = toInsertItems;
        this.toTakeFluid = toTakeFluids;
        this.fluidsTook = tookFluids;
        this.toInsertFluids = toInsertFluids;
    }
    
    public CraftingPattern getPattern() {
        return pattern;
    }

    public boolean update(final EStorageNetwork controller) {
        updatedOnce = true;
    	
        if (!this.missing.isEmpty()) {
        	ItemStackList list = new ItemStackList();
        	for (ItemStack missing : this.missing) {
        		list.add(missing);
        	}
            for (ItemStack missing : list.getStacks()) {
                if (!controller.getItemStorage().removeCheck(missing, missing.stackSize, ItemStorage.getExtractFilter(pattern.isOredict()), true)) {
                     return false;
                }
            }
            reschedule(controller);
            return false;
        }
        
    	for (FluidStack stack : toTakeFluid) {
    		if(stack.amount <=0)continue;
            FluidStack stackExtracted = controller.getFluidStorage().removeFluid(stack, false);
            if (stackExtracted != null) {
            	stack.amount-=stackExtracted.amount;
                fluidsTook.add(stackExtracted);
            }
        }

    	for (FluidStack stack : toTakeFluid) {
    		if(stack.amount <=0){
    			toTakeFluid.remove(stack);
    			break;
    		}
    	}
    	
    	for (CraftingProcessBase process : toProcess) {
            if (!process.started && process.canStartProcessing(controller.getItemStorage(), fluidsTook)) {
            	process.started = true;
            	process.update(toInsertItems, toInsertFluids);
            }
        }
    	
    	 int times = toInsertItems.size();
         for (int i = 0; i < times; i++) {
             ItemStack insert = toInsertItems.poll();
             if (insert != null) {
                 ItemStack remain = controller.getItemStorage().addItem(insert, false);
                 if (remain !=null) {
                	 toInsertItems.add(remain.copy());
                 }
             }
         }

         int startedCount = 0;
         Iterator<CraftingProcessBase> ip = toProcess.iterator();
         while(ip.hasNext()){
        	 CraftingProcessBase process = ip.next();
        	 if(process.started){
        		 startedCount++;
        	 }
         }
         
         /*if(startedCount == 0){
        	 reschedule(controller);
         }*/
         
         ip = toProcess.iterator();
         while(ip.hasNext()){
        	 CraftingProcessBase process = ip.next();
        	 if(process !=null && process.hasReceivedOutputs()){
        		 ip.remove();
        	 }
         }
    	return isFinished();
    }
    
    public void calculate(EStorageNetwork network) {
    	ItemStackList insertList = new ItemStackList();
    	ItemStackList networkList = new ItemStackList();
    	for(ItemStackData data : network.getItemStorage().getItemList()){
    		if(data.stack !=null && data.getAmount() > 0)
    		networkList.add(data.stack.copy());
    	}
    	
    	ItemStack requested = this.requested != null ? this.requested : pattern.getOutputs().get(0);
    	int newQuantity = quantity;
       	while (newQuantity > 0 && !recursiveFind) {
            calculate(network, networkList, pattern, insertList);
            newQuantity -= pattern.getQuantityPerRequest(requested, pattern.isOredict());
       	}
       	usedPatterns.clear();
    }

    private void calculate(EStorageNetwork network, ItemStackList networkList, CraftingPattern pattern, ItemStackList insertList) {
    	
    	recursiveFind = !usedPatterns.add(pattern);
        if (recursiveFind) {
            return;
        }

        List<ItemStack> inputs = Lists.newArrayList();
        
        List<ItemStack> usedStacks = new LinkedList<ItemStack>();
        ItemStackList actualInputs = new ItemStackList();
        
        for (List<ItemStack> oreInputs : pattern.getOreInputs()) {
        	 boolean added = false;
        	 for (ItemStack input : oreInputs) {
        		 
        		 ItemStackData data = network.getItemStorage().getItemData(input);
        		 
        		 if(data !=null){
        			 usedStacks.add(ItemStack.copyItemStack(input));
        			 inputs.add(ItemStack.copyItemStack(input));
        			 added = true;
        			 break;
        		 }
        		 if (!added) {
        			 ItemStack choice = null;
        			 if (!oreInputs.isEmpty()) {
        				 choice = oreInputs.get(0);
        				 inputs.add(ItemStack.copyItemStack(choice));
        			 }
        			 usedStacks.add(ItemStack.copyItemStack(choice));
        		 }
        	 }
        }
        
        for (int i = 0; i < inputs.size(); i++) {
        	ItemStack input = inputs.get(i);
            if(input == null)continue;

            ItemStack inputInNetwork = networkList.get(input, pattern.isOredict());
            
            while(input.stackSize > 0){
            	ItemStack extra = insertList.get(input, pattern.isOredict());
                if (extra != null && extra.stackSize > 0) {
                	int takeQuantity = Math.min(extra.stackSize, input.stackSize);
                    ItemStack extraToRemove = ItemHandlerHelper.copyStackWithSize(extra, takeQuantity);
                    if (!pattern.isProcessing()) {
                    	actualInputs.add(extraToRemove.copy());
                    }
                    input.stackSize-=takeQuantity;
                    insertList.remove(extraToRemove, true);
                } else if (inputInNetwork != null && inputInNetwork.stackSize > 0) {
                    int takeQuantity = Math.min(inputInNetwork.stackSize, input.stackSize);
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(inputInNetwork, takeQuantity);
                    actualInputs.add(inputStack.copy());
                    toTake.add(inputStack.copy());
                    input.stackSize -= takeQuantity;
                    networkList.remove(inputStack, true);
                } else {
                	CraftingPattern inputPattern = network.getPatternWithBestScore(input, pattern.isOredict());

                    if (inputPattern != null) {
                    	ItemStack actualCraft = inputPattern.getActualOutput(input, pattern.isOredict());
                    	int craftQuantity = Math.min(inputPattern.getQuantityPerRequest(input, pattern.isOredict()), input.stackSize);
                        ItemStack inputCrafted = ItemHandlerHelper.copyStackWithSize(actualCraft, craftQuantity);
                        actualInputs.add(inputCrafted.copy());
                        calculate(network, networkList, inputPattern, insertList);
                        input.stackSize -= craftQuantity;
                        ItemStack inserted = insertList.get(inputCrafted, pattern.isOredict());
                        insertList.remove(inserted, craftQuantity, true);
                    } else {
                    	
                    	ItemStack fluidCheck = ItemHandlerHelper.copyStackWithSize(input, 1);
                    	while (input.stackSize > 0 && doFluidCalculation(network, networkList, fluidCheck, insertList)) {
                             actualInputs.add(fluidCheck);
                             input.stackSize -= 1;
                    	}
                    	
                    	if (input.stackSize > 0) {
                    		missing.add(ItemStack.copyItemStack(input));
                    		input.stackSize = 0;
                    	}
                    }
                }
            }
        }

        if (pattern.isProcessing()) {
            toProcess.add(new CraftingProcessExternal(network, pattern));
        }else {
        	toProcess.add(new CraftingProcessNormal(network, pattern, usedStacks));
        }
        
        ItemStack[] took = new ItemStack[9];
        if (missing.isEmpty()) {
        	if (!pattern.isProcessing()) {
	            for (int i = 0; i < usedStacks.size(); i++) {
	                ItemStack input = usedStacks.get(i);
	                if (input != null) {
	                    ItemStack actualInput = actualInputs.get(input, pattern.isOredict());
	                    ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.stackSize);
	                    took[i] = taken;
	                    actualInputs.remove(taken, true);
	                }
	            }
        	}
    	}
        
        for (ItemStack byproduct : (!pattern.isProcessing() && pattern.isOredict() && missing.isEmpty() ? pattern.getByproducts(took) : pattern.getByproducts())) {
        	if(byproduct !=null)insertList.add(byproduct.copy());
        }

        for (ItemStack output : (!pattern.isProcessing() && pattern.isOredict() && missing.isEmpty() ? pattern.getOutputs(took) : pattern.getOutputs())) {
        	if(output !=null)insertList.add(output.copy());
        }
        usedPatterns.remove(pattern);
    }
    
    public boolean doFluidCalculation(EStorageNetwork network, ItemStackList networkList, ItemStack input, ItemStackList insertList){
    	FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(input);
        if (fluidInItem != null) {
        	ItemStack container = FluidUtil.getEmptyContainer(input);
        	if(container !=null){
                FluidStackData fluidInStorage = network.getFluidStorage().getFluidData(fluidInItem);
                
                if (fluidInStorage == null || fluidInStorage.getAmount() < fluidInItem.amount) {
                    missing.add(ItemStack.copyItemStack(input));
                } else {
                	ItemStack buk = networkList.get(container, false);
                    boolean hasBucket = buk !=null && buk.stackSize > 0;
                    CraftingPattern bucketPattern = network.getPatternWithBestScore(container, pattern.isOredict());

                    if (!hasBucket) {
                        if (bucketPattern == null) {
                            missing.add(ItemStack.copyItemStack(container));
                        } else {
                            calculate(network, networkList, bucketPattern, insertList);
                        }
                    }

                    if (hasBucket || bucketPattern != null) {
                    	if(hasBucket){
                    		//toTake.add(container);
                    		networkList.remove(container, true);
                    	}
                        toTakeFluid.add(fluidInItem.copy());
                    }
                }
                return true;
        	}
        } 
        return false;
    }
    
    public void reschedule(EStorageNetwork network) {
        List<CraftingProcessBase> mainProcesses = Lists.newArrayList();
        for(CraftingProcessBase process : toProcess){
        	if(process.pattern == pattern){
        		mainProcesses.add(process);
        	}
        }
        ModLogger.info("Missing: "+missing.toString());
        missing.clear();
        toProcess.clear();
        // if the list of main steps is empty there is no point in rescheduling
        if (!mainProcesses.isEmpty()) {
            quantity = 0;
            int quantityPerRequest = pattern.getQuantityPerRequest(requested, pattern.isOredict());
            for (CraftingProcessBase step : mainProcesses) {
                quantity += quantityPerRequest - step.getReceivedOutput(requested);
            }
            if (quantity > 0) {
            	calculate(network);
            }
        }
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    private boolean isFinished() {
        return hasProcessedItems();
    }
    
    private boolean hasProcessedItems() {
    	for(CraftingProcessBase process : toProcess){
    		if(!process.hasReceivedOutputs()){
    			return false;
    		}
    	}
        return true;
    }

    @Override
    public void onCancelled(EStorageNetwork controller) {
        for (ItemStack took : toInsertItems) {
        	controller.getItemStorage().addItem(took, false);
        }
        for (FluidStack took : fluidsTook.getStacks()) {
        	controller.getFluidStorage().addFluid(took, false);
        }
    }
    
    public static final String NBT_PROCESSES = "Processes";
    public static final String NBT_TO_TAKE_FLUIDS = "ToTakeFluids";
    public static final String NBT_TO_INSERT_ITEMS = "ToInsertItems";
    public static final String NBT_TO_INSERT_FLUIDS = "ToInsertFluids";
    public static final String NBT_TOOK_FLUIDS = "TookFluids";
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    	if(getPattern() !=null){
    		if(getPattern().getPatternStack() !=null)tag.setTag(NBT_PATTERN, getPattern().getPatternStack().serializeNBT());
    		if(getPattern().getCrafter() !=null){
    			NBTTagCompound posTag = BlockUtil.saveBlockPos(getPattern().getCrafter().getPos());
    			posTag.setInteger("Dim", getPattern().getCrafter().getDimension());
    			tag.setTag(NBT_CRAFTER, posTag);
    		}
    	}
    	if(requested !=null)tag.setTag(NBT_REQUESTED, requested.serializeNBT());
    	tag.setInteger(NBT_QUANTITY, quantity);
    	NBTTagList stepsList = new NBTTagList();

        for (CraftingProcessBase process : toProcess) {
            stepsList.appendTag(process.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_PROCESSES, stepsList);

        NBTTagList toInsertItemsList = new NBTTagList();

        for (ItemStack insert : toInsertItems) {
            toInsertItemsList.appendTag(insert.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT_ITEMS, toInsertItemsList);

        
        NBTTagList toTakeFluidList = new NBTTagList();

        for (FluidStack take : toTakeFluid) {
        	toTakeFluidList.appendTag(take.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag(NBT_TO_TAKE_FLUIDS, toTakeFluidList);

        NBTTagList toInsertFluidsList = new NBTTagList();

        for (FluidStack insert : toInsertFluids) {
            toInsertFluidsList.appendTag(insert.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_TO_INSERT_FLUIDS, toInsertFluidsList);

        NBTTagList fluidsTookList = new NBTTagList();

        for (FluidStack took : fluidsTook.getStacks()) {
        	toTakeFluidList.appendTag(took.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag(NBT_TOOK_FLUIDS, fluidsTookList);
        
        return tag;
    }

    @Override
    public String getInfo() {
    	if (!updatedOnce) {
            return "T="+Lang.prefix+"gui.crafting_monitor.not_started_yet";
        }

        StringBuilder builder = new StringBuilder();

        
        if (!toInsertItems.isEmpty()) {
        	boolean hasTitle = false;
        	String tString = "";
            Map<ItemStack, Integer> insertCount = Maps.newHashMap();
            Iterator<ItemStack> it = toInsertItems.iterator();
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
        } 
        if(!isFinished()){
        	boolean addMissing = !missing.isEmpty();
        	boolean addTaking = false;
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
	            for(CraftingProcessBase process : toProcess){
	            	if(!process.getPattern().isProcessing())continue;
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
	public List<CraftingProcessBase> getToProcess() {
		return toProcess;
	}
}