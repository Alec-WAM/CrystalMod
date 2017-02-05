package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.estorage.IAutoCrafter;
import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.api.estorage.INetworkPowerTile;
import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketCraftingInfo;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessBase;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessExternal;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessNormal;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;

public class TileCraftingController extends TileEntityMod implements INetworkPowerTile {

	private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
	private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
	private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
	private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();
	private List<NBTTagCompound> craftingTasksToRead = new ArrayList<NBTTagCompound>();
	
	protected int ticks;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		
		NBTTagList list = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            list.appendTag(task.writeToNBT(new NBTTagCompound()));
        }

        nbt.setTag("CraftingTasks", list);
        
        nbt.setBoolean("Connected", connected);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		 if (nbt.hasKey("CraftingTasks")) {
            NBTTagList taskList = nbt.getTagList("CraftingTasks", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                craftingTasksToRead.add(taskList.getCompoundTagAt(i));
            }
        }
		connected = nbt.getBoolean("Connected");
	}
	
	
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			if(network !=null){
				
				if (!craftingTasksToRead.isEmpty()) {
	                for (NBTTagCompound tag : craftingTasksToRead) {
	                    ICraftingTask task = readCraftingTask(network, tag);

	                    if (task != null) {
	                        addCraftingTask(task);
	                    }
	                }

	                craftingTasksToRead.clear();
	            }
				
				ticks++;
		
				for (ICraftingTask taskToCancel : craftingTasksToCancel) {
					taskToCancel.onCancelled(network);
				}
				craftingTasks.removeAll(craftingTasksToCancel);
				craftingTasksToCancel.clear();
		
				for (ICraftingTask task : craftingTasksToAdd) {
					craftingTasks.push(task);
				}
				craftingTasksToAdd.clear();
		
				for (ICraftingTask task : craftingTasksToAddAsLast) {
					craftingTasks.add(0, task);
				}
				craftingTasksToAddAsLast.clear();
		
				if(network.getEnergy() > 0){
					if (!craftingTasks.empty()) {
						ICraftingTask top = craftingTasks.peek();
						if (ticks % top.getPattern().getCrafter().getSpeed() == 0 && top.update(network)) {
							craftingTasks.pop();
						}
					}
				}
			}
		}
	}
	
	public boolean isCrafting(ItemStack stack) {
		for (ICraftingTask task : getCraftingTasks()) {
			CraftingPattern pattern = task.getPattern();
			if (pattern != null && pattern.getOutputs() != null) {
				for (ItemStack cStack : pattern.getOutputs()) {
					if (ItemUtil.canCombine(stack, cStack)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public List<ICraftingTask> getCraftingTasks() {
		return craftingTasks;
	}

	public void addCraftingTask(ICraftingTask task) {
		craftingTasksToAdd.add(task);
	}

	public void addCraftingTaskIfNotCrafting(ICraftingTask task) {
		CraftingPattern pattern = task.getPattern();
		for (ItemStack stack : pattern.getOutputs()) {
			if (isCrafting(stack))
				return;
		}
		addCraftingTask(task);
	}

	public void scheduleCraftingTaskIfUnscheduled(ItemStack stack, int toSchedule, boolean ore) {
		if(network == null)return;
		int alreadyScheduled = 0;

		for (ICraftingTask task : getCraftingTasks()) {
			for (ItemStack output : task.getPattern().getOutputs()) {
				if (ore ? ItemUtil.stackMatchUseOre(output, stack) : ItemUtil.canCombine(output, stack)) {
					alreadyScheduled+=ItemStackTools.getStackSize(output);
				}
			}
		}

		CraftingPattern pattern = network.getPatternWithBestScore(stack, ore);
		int craftAmount = toSchedule - alreadyScheduled;
		if (pattern != null && craftAmount > 0) {
			ICraftingTask task = createCraftingTask(stack, pattern, craftAmount);
			task.calculate(network);
			addCraftingTaskAsLast(task);
		}
	}

	public void addCraftingTaskAsLast(ICraftingTask task) {
		craftingTasksToAddAsLast.add(task);
	}

	public ICraftingTask createCraftingTask(ItemStack request, CraftingPattern pattern, int amt) {
		return new BasicCraftingTask(request, pattern, amt);
	}

	public void requestCraftingInfo(final EntityPlayerMP player, ItemStackData data, final int quantity){
		if(network == null)return;
		final ItemStack requested = data.stack.copy();
		final CraftingPattern pattern = network.getPatternWithBestScore(requested);
		Thread calculationThread = new Thread(requested.getUnlocalizedName()+"x"+quantity+" Calculate Thread"){
    		public void run()
            {
    			try{
    				ICraftingTask task = createCraftingTask(requested, pattern, quantity);
    				if(task !=null && task instanceof BasicCraftingTask){
    					task.calculate(network);
    					CrystalModNetwork.sendTo(new PacketCraftingInfo((BasicCraftingTask)task, requested, quantity), player);
    				}
    			} catch(Exception e){
    				ModLogger.warning(requested.getUnlocalizedName()+"x"+quantity+" Calculation Thread experienced the following exception");
    				e.printStackTrace();
    			}
            }
    	};
    	calculationThread.start();
	}
	
	public void handleCraftingRequest(ItemStackData data, int quantity) {
		if (network !=null && data != null && quantity > 0 && quantity <= 500) {
			ItemStack requested = data.stack;

			CraftingPattern pattern = network.getPatternWithBestScore(requested);

			if (pattern != null) {
				ICraftingTask task = createCraftingTask(requested, pattern, quantity);
				if(task !=null){
					task.calculate(network);
					addCraftingTaskAsLast(task);
				}
			}
		}
	}

	public void handleCraftingCancel(int id) {
		if (id >= 0 && id < getCraftingTasks().size()) {
			cancelCraftingTask(getCraftingTasks().get(id));
		} else if (id == -1) {
			for (ICraftingTask task : getCraftingTasks()) {
				cancelCraftingTask(task);
			}
		}
	}

	public void cancelCraftingTask(ICraftingTask task) {
		craftingTasksToCancel.add(task);
	}

	public void cancelAll(TileCrafter tileCrafter) {
		for (ICraftingTask task : getCraftingTasks()) {
            if (task.getPattern().getCrafter() == tileCrafter) {
            	cancelCraftingTask(task);
            }
        }
	}

	private EStorageNetwork network;
	public boolean connected;
	
	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
		this.connected = network !=null;
		markDirty();
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {
		if(network !=null){
			network.craftingController = null;
		}
		this.connected = false;
		markDirty();
	}
	
	public static ICraftingTask readCraftingTask(EStorageNetwork network, NBTTagCompound tag) {
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(BasicCraftingTask.NBT_PATTERN));

        if (!ItemStackTools.isNullStack(stack) && stack.getItem() instanceof ItemPattern) {
        	NBTTagCompound posTag = tag.getCompoundTag(BasicCraftingTask.NBT_CRAFTER);
        	World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(posTag.getInteger("Dim"));
            TileEntity container = world.getTileEntity(BlockUtil.loadBlockPos(posTag));

            if (container instanceof IAutoCrafter) {
                CraftingPattern pattern = ((IAutoCrafter)container).createPattern(stack);

                return create(network, world, tag.hasKey(BasicCraftingTask.NBT_REQUESTED) ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag(BasicCraftingTask.NBT_REQUESTED)) : null, pattern, tag.getInteger(BasicCraftingTask.NBT_QUANTITY), tag);
            }
        }

        return null;
    }
	
	private static ICraftingTask create(EStorageNetwork network, World world, @Nullable ItemStack stack, CraftingPattern pattern, int quantity, @Nullable NBTTagCompound tag) {
		if (tag != null) {
        	NBTTagList processList = tag.getTagList(BasicCraftingTask.NBT_PROCESSES, Constants.NBT.TAG_COMPOUND);
	
            List<CraftingProcessBase> processes = new ArrayList<CraftingProcessBase>();
	
            for (int i = 0; i < processList.tagCount(); ++i) {
                NBTTagCompound processTag = processList.getCompoundTagAt(i);
	
                CraftingProcessBase process = null;
	
                String type = processTag.getString(CraftingProcessBase.NBT_TYPE);
                if(type.equalsIgnoreCase(CraftingProcessNormal.ID)){
                	process = new CraftingProcessNormal(network);
                }
                if(type.equalsIgnoreCase(CraftingProcessExternal.ID)){
                	process = new CraftingProcessExternal(network);
                }

                if (process != null && process.readFromNBT(processTag)) {
                	processes.add(process);
                }
            }


            NBTTagList toInsertList = tag.getTagList(BasicCraftingTask.NBT_TO_INSERT_ITEMS, Constants.NBT.TAG_COMPOUND);

            Deque<ItemStack> toInsert = new ArrayDeque<ItemStack>();

            for (int i = 0; i < toInsertList.tagCount(); ++i) {
                ItemStack insertStack = ItemStackTools.loadFromNBT(toInsertList.getCompoundTagAt(i));

                if (!ItemStackTools.isNullStack(insertStack)) {
                    toInsert.add(insertStack);
                }
            }

            NBTTagList toTakeFluidsList = tag.getTagList(BasicCraftingTask.NBT_TO_TAKE_FLUIDS, Constants.NBT.TAG_COMPOUND);
            
            List<FluidStack> toTakeFluids = Lists.newArrayList();
            
            for (int i = 0; i < toTakeFluidsList.tagCount(); ++i) {
                FluidStack insertStack = FluidStack.loadFluidStackFromNBT(toTakeFluidsList.getCompoundTagAt(i));

                if (insertStack != null) {
                	toTakeFluids.add(insertStack);
                }
            }
            
            NBTTagList tookFluidsList = tag.getTagList(BasicCraftingTask.NBT_TOOK_FLUIDS, Constants.NBT.TAG_COMPOUND);
            
            FluidStackList tookFluids = new FluidStackList();
            
            for (int i = 0; i < tookFluidsList.tagCount(); ++i) {
                FluidStack insertStack = FluidStack.loadFluidStackFromNBT(tookFluidsList.getCompoundTagAt(i));

                if (insertStack != null) {
                	tookFluids.add(insertStack);
                }
            }
            
            NBTTagList toInsertFluidsList = tag.getTagList(BasicCraftingTask.NBT_TO_INSERT_FLUIDS, Constants.NBT.TAG_COMPOUND);

            Deque<FluidStack> toInsertFluids = new ArrayDeque<FluidStack>();

            for (int i = 0; i < toInsertFluidsList.tagCount(); ++i) {
                FluidStack tookStack = FluidStack.loadFluidStackFromNBT(toInsertFluidsList.getCompoundTagAt(i));

                if (tookStack != null) {
                    toInsertFluids.add(tookStack);
                }
            }

            return new BasicCraftingTask(stack, pattern, quantity, processes, toInsert, toTakeFluids, tookFluids, toInsertFluids);
        }
	
        return new BasicCraftingTask(stack, pattern, quantity);
   }

	@Override
	public int getEnergyUsage() {
		int usage = 0;
		final Iterator<ICraftingTask> i = craftingTasks.iterator();
		while(i.hasNext()){
			ICraftingTask task = i.next();
			if(task !=null){
				usage+=10*task.getToProcess().size();
			}
		}
		return usage;
	}
	
}
