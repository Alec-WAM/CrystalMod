package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.StringUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

//HUGE credit to https://github.com/raoulvdberge/refinedstorage way2muchnoise who did a lot of the work for this system

public class BasicCraftingTask implements ICraftingTask {
    private CraftingPattern pattern;
    private int quantity;
    
    private ItemStack requested;
    private ItemStackList missing = new ItemStackList();
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
    
    @Override
	public CraftingPattern getPattern() {
        return pattern;
    }

    @Override
	public boolean update(final EStorageNetwork controller) {
        updatedOnce = true;
    	if (!this.missing.isEmpty()) {
        	ItemStackList list = missing;
            for (ItemStack missing : list.getStacks()) {
                if (!controller.getItemStorage().removeCheck(missing, ItemStackTools.getStackSize(missing), ItemStorage.getExtractFilter(pattern.isOredict()), true)) {
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
            	
            	try{
            		process.update(toInsertItems, toInsertFluids);
            	} catch(Exception e){
            		e.printStackTrace();
            	}
            	
            }
        }
    	
    	 int times = toInsertItems.size();
         for (int i = 0; i < times; i++) {
             ItemStack insert = toInsertItems.poll();
             if (ItemStackTools.isValid(insert)) {
                 ItemStack remain = controller.getItemStorage().addItem(insert, false);
                 if (ItemStackTools.isValid(remain)) {
                	 toInsertItems.add(remain.copy());
                 }
             }
         }

         /*int startedCount = 0;
         Iterator<CraftingProcessBase> ip = toProcess.iterator();
         while(ip.hasNext()){
        	 CraftingProcessBase process = ip.next();
        	 if(process.started){
        		 startedCount++;
        	 }
         }*/
         
         /*if(startedCount == 0){
        	 reschedule(controller);
         }*/
         Iterator<CraftingProcessBase> ip = toProcess.iterator();
         ip = toProcess.iterator();
         while(ip.hasNext()){
        	 CraftingProcessBase process = ip.next();
        	 if(process !=null && process.hasReceivedOutputs()){
        		 ip.remove();
        	 }
         }
    	return isFinished();
    }
    
    @Override
	public void calculate(EStorageNetwork network) {
    	ItemStackList insertList = new ItemStackList();
    	ItemStackList networkList = new ItemStackList();
    	for(ItemStackData data : network.getItemStorage().getItemList()){
    		if(ItemStackTools.isValid(data.stack) && data.getAmount() > 0){
    			networkList.add(ItemUtil.copy(data.stack, data.getAmount()));
    		}
    	}
    	
    	ItemStack requested = ItemStackTools.isValid(this.requested) ? this.requested : pattern.getOutputs().get(0);
    	int newQuantity = quantity;
       	while (newQuantity > 0 && !recursiveFind) {
            calculate(network, networkList, pattern, insertList);
            newQuantity -= pattern.getQuantityPerRequest(requested, pattern.isOredict());
       	}
       	usedPatterns.clear();
    }

    private void calculate(EStorageNetwork network, ItemStackList networkList, CraftingPattern pattern, ItemStackList insertList) {
    	boolean debugData = false;
    	
    	recursiveFind |= !usedPatterns.add(pattern);
        if (recursiveFind) {
        	if(debugData) ModLogger.info("Blocked recursive");
            return;
        }
        
        List<ItemStack> usedStacks = new LinkedList<ItemStack>();
        ItemStackList actualInputs = new ItemStackList();
        
        ItemStackList itemList = new ItemStackList();
        for(NonNullList<ItemStack> INPUT : pattern.getOreInputs()){
        	if(INPUT.isEmpty()){
        		usedStacks.add(ItemStackTools.getEmptyStack()); 
        		continue;
        	}
        	ItemStack stack = /*ItemStackTools.getEmptyStack();
        	
        	second : for(ItemStack stack2 : INPUT){
        		if(network.getItemStorage().hasItem(stack2, pattern.isOredict())){
        			stack = stack2;
        			break second;
        		}
        	}
        	
        	if(ItemStackTools.isEmpty(stack)){
        		stack = */INPUT.get(0);
        	//}
        	
        	if(ItemStackTools.isEmpty(stack)){
        		usedStacks.add(ItemStackTools.getEmptyStack());        		
        	}
        	else {
        		itemList.add(ItemStackTools.safeCopy(stack));
        		usedStacks.add(ItemStackTools.safeCopy(stack));
        	}
        }
        
        for(ItemStack input : itemList.getStacks()){
        	ItemStack lookup = ItemStackTools.safeCopy(input);
        	boolean useOre = pattern.isOredict();
        	
        	int sizeNeeded = ItemStackTools.getStackSize(input);
        	ItemStack extraStack = insertList.get(lookup, useOre);    
        	ItemStack networkStack = networkList.get(lookup, useOre);
        	
        	while(sizeNeeded > 0){
        		//Pull from extra items first
	        	if(ItemStackTools.isValid(extraStack) && ItemStackTools.getStackSize(extraStack) > 0){
	        		int takeQuantity = Math.min(ItemStackTools.getStackSize(extraStack), ItemStackTools.getStackSize(input));
	                ItemStack extraToRemove = ItemHandlerHelper.copyStackWithSize(extraStack, takeQuantity);
	                if (!pattern.isProcessing()) {
	                	actualInputs.add(extraToRemove.copy());
	                }
	                ItemStackTools.incStackSize(input, -takeQuantity);
	                sizeNeeded -= takeQuantity;
	                if(debugData) ModLogger.info("Extra: "+extraToRemove+" ("+takeQuantity+")");
	                insertList.remove(extraToRemove, true);
	                extraStack = insertList.get(lookup, useOre);
	        	} 
	        	//Then network
	        	else if(ItemStackTools.isValid(networkStack) && ItemStackTools.getStackSize(networkStack) > 0) {
	        		int takeQuantity = Math.min(ItemStackTools.getStackSize(networkStack), ItemStackTools.getStackSize(input));
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(networkStack, takeQuantity);
                    actualInputs.add(inputStack.copy());
                    toTake.add(inputStack.copy());
                    ItemStackTools.incStackSize(input, -takeQuantity);
                    sizeNeeded -= takeQuantity;
                    if(debugData) ModLogger.info("Network: "+inputStack+" ("+takeQuantity+")");
                    networkList.remove(inputStack, true);                    
                    if (sizeNeeded > 0) {
                        networkStack = networkList.get(lookup, useOre);
                    }
                } 
	        	//Finally Craft it
	        	else {
                	CraftingPattern inputPattern = network.getPatternWithBestScore(input, pattern.isOredict());

                    if (inputPattern != null) {
                    	ItemStack actualCraft = inputPattern.getActualOutput(input, pattern.isOredict());
                    	int craftQuantity = Math.min(inputPattern.getQuantityPerRequest(input, pattern.isOredict()), ItemStackTools.getStackSize(input));
                        ItemStack inputCrafted = ItemHandlerHelper.copyStackWithSize(actualCraft, craftQuantity);
                        actualInputs.add(inputCrafted.copy());
                        
                        if(debugData) ModLogger.info("Further Calculations at "+sizeNeeded);
                        calculate(network, networkList, inputPattern, insertList);
                        
                        ItemStackTools.incStackSize(input, -craftQuantity);
                        sizeNeeded -= craftQuantity;
                        ItemStack inserted = insertList.get(inputCrafted, pattern.isOredict());
                        insertList.remove(inserted, craftQuantity, true);
                    } else {
                    	
                    	ItemStack fluidCheck = ItemHandlerHelper.copyStackWithSize(input, 1);
                    	while (!ItemStackTools.isEmpty(input) && doFluidCalculation(network, networkList, fluidCheck, insertList)) {
                             actualInputs.add(fluidCheck);
                             ItemStackTools.incStackSize(input, -1);
                             sizeNeeded--;
                    	}
                    	
                    	if (sizeNeeded > 0) {
                    		if(debugData) ModLogger.info("Missing "+input+" at "+sizeNeeded+ "("+networkStack+")");
                    		missing.add(ItemUtil.copy(input, sizeNeeded));
                    		ItemStackTools.setStackSize(input, 0);
                    		sizeNeeded = 0;
                    	}
                    }
                }   	
        	}
        	if(debugData) ModLogger.info("We still need "+sizeNeeded+" of "+lookup);
        }
        
        
        if (pattern.isProcessing()) {
            toProcess.add(new CraftingProcessExternal(network, pattern));
        }else {
        	toProcess.add(new CraftingProcessNormal(network, pattern, usedStacks));
        }
        if(debugData) ModLogger.info("Used Log: "+StringUtils.makeListReadable(usedStacks));
        
        NonNullList<ItemStack> took = NonNullList.withSize(9, ItemStackTools.getEmptyStack());
        if (missing.isEmpty()) {
        	if (!pattern.isProcessing()) {
	            for (int i = 0; i < usedStacks.size(); i++) {
	                ItemStack input = usedStacks.get(i);
	                if (ItemStackTools.isValid(input)) {
	                	ItemStack actualInput = actualInputs.get(input, pattern.isOredict());
	                    ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, Math.max(1, ItemStackTools.getStackSize(input)));
	                    if(debugData) ModLogger.info("Took "+taken+" "+input+" "+actualInput);
	                    took.set(i, taken);
	                    actualInputs.remove(taken, true);
	                } else {
	                	if(debugData) ModLogger.info("Took Air");
	                    took.set(i, ItemStackTools.getEmptyStack());
	                }
	            }
        	}
    	}
        
        for (ItemStack byproduct : (!pattern.isProcessing() && missing.isEmpty() ? pattern.getByproducts(took) : pattern.getByproducts())) {
        	if(ItemStackTools.isValid(byproduct)){
        		if(debugData) ModLogger.info("Adding insert list B:"+byproduct);
        		insertList.add(byproduct.copy());
        	}
        }

        for (ItemStack output : (!pattern.isProcessing() && missing.isEmpty() ? pattern.getOutputs(took) : pattern.getOutputs())) {
        	if(ItemStackTools.isValid(output)){
        		if(debugData) ModLogger.info("Adding insert list O: "+output);        		
        		insertList.add(output.copy());
        	}
        }
        usedPatterns.remove(pattern);
    }
    
    public boolean doFluidCalculation(EStorageNetwork network, ItemStackList networkList, ItemStack input, ItemStackList insertList){
    	FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(input);
        if (fluidInItem != null) {
        	ItemStack container = FluidUtil.getEmptyContainer(input);
        	if(ItemStackTools.isValid(container)){
                FluidStackData fluidInStorage = network.getFluidStorage().getFluidData(fluidInItem);
                
                if (fluidInStorage == null || fluidInStorage.getAmount() < fluidInItem.amount) {
                    missing.add(ItemStackTools.safeCopy(input));
                } else {
                	ItemStack buk = networkList.get(container, false);
                    boolean hasBucket = ItemStackTools.isValid(buk);
                    CraftingPattern bucketPattern = network.getPatternWithBestScore(container, pattern.isOredict());

                    if (!hasBucket) {
                        if (bucketPattern == null) {
                            missing.add(ItemStackTools.safeCopy(container));
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
    		if(ItemStackTools.isValid(getPattern().getPatternStack()))tag.setTag(NBT_PATTERN, getPattern().getPatternStack().serializeNBT());
    		if(getPattern().getCrafter() !=null){
    			NBTTagCompound posTag = BlockUtil.saveBlockPos(getPattern().getCrafter().getPos());
    			posTag.setInteger("Dim", getPattern().getCrafter().getDimension());
    			tag.setTag(NBT_CRAFTER, posTag);
    		}
    	}
    	if(ItemStackTools.isValid(requested))tag.setTag(NBT_REQUESTED, requested.serializeNBT());
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
                		insertCount.put(st, insertCount.get(st)+ItemStackTools.getStackSize(input));
                		found = true;
                		break search;
                	}
                }
                if(!found){
                	insertCount.put(input, ItemStackTools.getStackSize(input));
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
	            Iterator<ItemStack> it = missing.getStacks().iterator();
	            while (it.hasNext()) {
	                ItemStack input = it.next();
	
	                if (!hasTitle) {
	                	builder.append("I="+Lang.prefix+"gui.crafting_monitor.items_missing\n");
	
	                    hasTitle = true;
	                }
	                
	                boolean found = false;
	                search : for(ItemStack st : insertCount.keySet()){
	                	if(ItemUtil.canCombine(st, input)){
	                		insertCount.put(st, insertCount.get(st)+ItemStackTools.getStackSize(input));
	                		found = true;
	                		break search;
	                	}
	                }
	                if(!found){
	                	insertCount.put(input, ItemStackTools.getStackSize(input));
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
	                		insertCount.put(st, insertCount.get(st)+ItemStackTools.getStackSize(input));
	                		found = true;
	                		break search;
	                	}
	                }
	                if(!found){
	                	insertCount.put(input, ItemStackTools.getStackSize(input));
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
			                		insertCount.put(st, insertCount.get(st)+ItemStackTools.getStackSize(input));
			                		found = true;
			                		break search;
			                	}
			                }
			                if(!found){
			                	insertCount.put(input, ItemStackTools.getStackSize(input));
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