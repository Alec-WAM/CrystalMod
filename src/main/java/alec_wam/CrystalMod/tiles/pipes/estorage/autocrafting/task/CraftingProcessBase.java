package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.estorage.IAutoCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class CraftingProcessBase{
	public static final String NBT_TYPE = "Type";
	private static final String NBT_PATTERN = "Pattern";
	private static final String NBT_SATISFIED = "Satisfied_%d";
	private static final String NBT_PATTERN_CONTAINER = "PatternContainer";
	private static final String NBT_STARTED = "Started";
	
	protected EStorageNetwork network;
    protected CraftingPattern pattern;
    protected Map<Integer, Integer> satisfied;
	public boolean started;

    public CraftingProcessBase(EStorageNetwork network, CraftingPattern pattern) {
    	this.network = network;
        this.pattern = pattern;
        this.satisfied = new HashMap<Integer, Integer>(pattern.getOutputs().size());
    }
    
    public CraftingProcessBase(EStorageNetwork network) {
    	this.network = network;
    }
    
    public void setNetwork(EStorageNetwork network){
    	this.network = network;
    }
    
    public boolean readFromNBT(NBTTagCompound tag) {
    	ItemStack patternStack = ItemStackTools.loadFromNBT(tag.getCompoundTag(NBT_PATTERN));
    	if(ItemStackTools.isValid(patternStack)){
    		NBTTagCompound nbtContainer = tag.getCompoundTag(NBT_PATTERN_CONTAINER);
    		World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(nbtContainer.getInteger("Dim"));
    		if(world == null)return false;
    		TileEntity tile = world.getTileEntity(BlockUtil.loadBlockPos(nbtContainer));
    		if(tile == null || !(tile instanceof IAutoCrafter))return false;
    		IAutoCrafter crafter = (IAutoCrafter)tile;
    		this.pattern = crafter.createPattern(patternStack);
    		int listSize = pattern.getOutputs().size();
	        this.satisfied = new HashMap<Integer, Integer>(listSize);
	
	        for (int i = 0; i < listSize; ++i) {
	            String id = String.format(NBT_SATISFIED, i);
	
	            if (tag.hasKey(id)) {
	                this.satisfied.put(i, tag.getInteger(id));
	            }
	        }
	        this.started = tag.getBoolean(NBT_STARTED);
	        return true;
    	}
    	
    	return false;
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public List<ItemStack> getToInsert() {
    	List<ItemStack> list = Lists.newArrayList();
    	for(ItemStack stack : pattern.getInputs()){
    		if(ItemStackTools.isValid(stack)){
    			list.add(stack);
    		}
    	}
        return list;
    }

    public boolean hasReceivedOutputs() {
    	for (int i = 0; i < pattern.getOutputs().size(); ++i) {
    		if(!hasReceivedOutput(i)){
    			return false;
    		}
    	}
        return true;
    }

    public boolean hasReceivedOutput(int i) {
    	if(i < pattern.getOutputs().size()){
	    	ItemStack stack = pattern.getOutputs().get(i);
			Integer rec = satisfied.get(i);
			if(rec == null || (ItemStackTools.isValid(stack) && ItemStackTools.getStackSize(stack) > rec)){
				return false;
			}
			return true;
    	}
        return false;
    }

    private Integer getSatisfied(ItemStack stack){
    	for (int i = 0; i < pattern.getOutputs().size(); ++i) {
        	Integer rec = satisfied.get(i);
        	if (rec == null) {
                 rec = 0;
            }
        	ItemStack item = pattern.getOutputs().get(i);
            if (pattern.isOredict() ? ItemUtil.stackMatchUseOre(stack, item) : ItemUtil.canCombine(stack, item)) {
            	 return rec;
            }
        }
    	return null;
    }
    
    public boolean onReceiveOutput(ItemStack stack) {
    	
        for (int i = 0; i < pattern.getOutputs().size(); ++i) {
        	Integer rec = satisfied.get(i);
        	if (rec == null) {
                 rec = 0;
            }
        	ItemStack item = pattern.getOutputs().get(i);
            if (pattern.isOredict() ? ItemUtil.stackMatchUseOre(stack, item) : ItemUtil.canCombine(stack, item)) {
            	 if (rec < ItemStackTools.getStackSize(item)) {
            		 satisfied.put(i, rec + ItemStackTools.getStackSize(stack));
            		 return true;
            	 }
            }
        }
        return false;
    }
    
    public abstract void update(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids);
    
    public boolean canStartProcessing(ItemStorage iStorage, FluidStackList list){
        for (ItemStack stack : getToInsert()) {
           if (!iStorage.removeCheck(stack, ItemStackTools.getStackSize(stack), ItemStorage.getExtractFilter(pattern.isOredict()), true)) {
        	   return false;
           }
        }
        return true;
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        for (int i = 0; i < satisfied.size(); ++i) {
            tag.setInteger(String.format(NBT_SATISFIED, i), satisfied.get(i));
        }
        tag.setTag(NBT_PATTERN, pattern.getPatternStack().serializeNBT());
        NBTTagCompound nbtCrafter = BlockUtil.saveBlockPos(pattern.getCrafter().getPos());
        nbtCrafter.setInteger("Dim", pattern.getCrafter().getDimension());
        tag.setTag(NBT_PATTERN_CONTAINER, nbtCrafter);
        tag.setBoolean(NBT_STARTED, started);
        return tag;
    }

	public int getReceivedOutput(ItemStack requested) {
		Integer rec = getSatisfied(requested);
		return rec == null ? 0 : rec;
	}
}
