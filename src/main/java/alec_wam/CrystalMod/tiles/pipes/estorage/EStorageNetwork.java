package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Stack;
import java.util.TreeMap;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.IAutoCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ItemPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.ICraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.INetworkItemProvider;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

public class EStorageNetwork extends AbstractPipeNetwork {
	public EStorageNetwork masterNetwork;

	private final ItemStorage itemStorage = new ItemStorage(this);
	private final FluidStorage fluidStorage = new FluidStorage(this);

	public final List<NetworkedHDDInterface> masterInterfaces = Lists
			.newArrayList();
	public final NavigableMap<Integer, List<NetworkedHDDInterface>> interfaces = new TreeMap<Integer, List<NetworkedHDDInterface>>(
			PRIORITY_SORTER);

	final List<TileEntityPanel> panels = new ArrayList<TileEntityPanel>();

	final List<TileEntityWirelessPanel> wirelesspanels = new ArrayList<TileEntityWirelessPanel>();

	final Map<BlockPos, TileEntityPipeEStorage> pipMap = new HashMap<BlockPos, TileEntityPipeEStorage>();

	public final List<INetworkContainer> watchers = new ArrayList<INetworkContainer>();
	public final List<IInsertListener> listeners = new ArrayList<IInsertListener>();

	public boolean updateItems = true;
	public boolean updateFluids = true;

	// AUTO CRAFTING
	final List<IAutoCrafter> crafters = new ArrayList<IAutoCrafter>();

	private List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();

	private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
	private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
	private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
	private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

	public ItemStorage getItemStorage() {
		return itemStorage;
	}

	public FluidStorage getFluidStorage() {
		return fluidStorage;
	}

	public static byte[] compressItem(ItemStackData data) throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			data.toBytes(cdo);
			return cdo.getCompressed();
		} finally {
			cdo.close();
		}
	}

	public byte[] compressItems() throws IOException {
		return compressItems(getItemStorage().getItemList());
	}

	public static byte[] compressItems(List<ItemStackData> data)
			throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			int count = data.size();
			cdo.writeVariable(count);
			for (ItemStackData entry : data) {
				entry.toBytes(cdo);
			}
			return cdo.getCompressed();
		} finally {
			cdo.close();
		}
	}

	public static ItemStackData decompressItem(byte[] compressed)
			throws IOException {
		CompressedDataInput cdi = new CompressedDataInput(compressed);
		try {
			ItemStackData data = ItemStackData.fromBytes(cdi);
			return data;
		} finally {
			cdi.close();
		}
	}

	public static List<ItemStackData> decompressItems(byte[] compressed)
			throws IOException {
		CompressedDataInput cdi = new CompressedDataInput(compressed);
		try {
			int count = cdi.readVariable();
			List<ItemStackData> list = Lists.newArrayList();
			for (int i = 0; i < count; i++) {
				ItemStackData data = ItemStackData.fromBytes(cdi);
				list.add(data);
			}
			return list;
		} finally {
			cdi.close();
		}
	}

	public static byte[] compressFluid(FluidStackData data) throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			data.toBytes(cdo);
			return cdo.getCompressed();
		} finally {
			cdo.close();
		}
	}

	public byte[] compressFluids() throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			int count = getFluidStorage().getFluidList().size();
			cdo.writeVariable(count);
			for (FluidStackData entry : getFluidStorage().getFluidList()) {
				entry.toBytes(cdo);
			}
			return cdo.getCompressed();
		} finally {
			cdo.close();
		}
	}

	public static byte[] compressFluids(List<FluidStackData> data)
			throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			int count = data.size();
			cdo.writeVariable(count);
			for (FluidStackData entry : data) {
				entry.toBytes(cdo);
			}
			return cdo.getCompressed();
		} finally {
			cdo.close();
		}
	}

	public static FluidStackData decompressFluid(byte[] compressed)
			throws IOException {
		CompressedDataInput cdi = new CompressedDataInput(compressed);
		try {
			FluidStackData data = FluidStackData.fromBytes(cdi);
			return data;
		} finally {
			cdi.close();
		}
	}

	public static List<FluidStackData> decompressFluids(byte[] compressed)
			throws IOException {
		CompressedDataInput cdi = new CompressedDataInput(compressed);
		try {
			int count = cdi.readVariable();
			List<FluidStackData> list = Lists.newArrayList();
			for (int i = 0; i < count; i++) {
				FluidStackData data = FluidStackData.fromBytes(cdi);
				list.add(data);
			}
			return list;
		} finally {
			cdi.close();
		}
	}

	@Override
	public void addPipe(TileEntityPipe dpip) {
		super.addPipe(dpip);
		if (!(dpip instanceof TileEntityPipeEStorage))
			return;
		TileEntityPipeEStorage pip = (TileEntityPipeEStorage) dpip;
		pipMap.put(pip.getPos(), pip);

		TileEntity te = pip;
		if (te != null) {
			for (EnumFacing direction : pip.getExternalConnections()) {
				TileEntity extCon = pip.getExternalTile(direction);
				if (extCon != null) {
					BlockPos p = te.getPos().offset(direction);
					tileAdded(pip, direction, p, extCon);
				}
			}
		}
	}

	public void tileAdded(TileEntityPipeEStorage itemPipe,
			EnumFacing direction, BlockPos bc, TileEntity externalTile) {

		if (externalTile instanceof INetworkTile) {
			((INetworkTile) externalTile).setNetwork(this);
		}

		if (externalTile instanceof IInsertListener) {
			listeners.add((IInsertListener) externalTile);
		}

		if (externalTile instanceof INetworkItemProvider) {
			INetworkItemProvider inter = (INetworkItemProvider) externalTile;
			NetworkedHDDInterface inv = new NetworkedHDDInterface(inter,
					externalTile.getWorld(), bc);
			this.masterInterfaces.add(inv);
			this.updateInterfaces();
			if (inv.getInterface().getNetworkInventory() != null) {
				inv.getInterface().getNetworkInventory().updateItems(this, -1);
				inv.getInterface().getNetworkInventory().updateFluids(this, -1);
			}
		}

		if (externalTile instanceof TileEntityPanel) {
			panels.add((TileEntityPanel) externalTile);
		}
		if (externalTile instanceof TileEntityWirelessPanel) {
			wirelesspanels.add((TileEntityWirelessPanel) externalTile);
		}

		if (externalTile instanceof IAutoCrafter) {
			crafters.add((IAutoCrafter) externalTile);
			updatePatterns();
		}
	}

	public List<NetworkedHDDInterface> getOrCreateInterfaces(int priority) {
		List<NetworkedHDDInterface> list = interfaces.get(priority);
		if (list == null) {
			interfaces.put(priority,
					list = new ArrayList<NetworkedHDDInterface>());
		}
		return list;
	}

	public NetworkedHDDInterface getInterface(BlockPos pos, int dim) {
		Iterator<NetworkedHDDInterface> ii = masterInterfaces.iterator();
		// FIRST PASS
		while (ii.hasNext()) {
			final NetworkedHDDInterface inter = ii.next();
			if (inter != null) {
				if (sameDimAndPos(inter, pos, dim)) {
					return inter;
				}
			}
		}
		return null;
	}

	public boolean sameDimAndPos(IAutoCrafter crafter, BlockPos pos, int dim) {
		if (crafter == null || (crafter.getPos() == null && pos != null))
			return false;
		return crafter.getDimension() == dim
				&& (pos == null ? true : crafter.getPos().equals(pos));
	}

	public boolean sameDimAndPos(TileEntity tile, BlockPos pos, int dim) {
		if (tile == null || (tile.getPos() == null && pos != null)
				|| tile.getWorld() == null || tile.getWorld().provider == null)
			return false;
		return tile.getWorld().provider.getDimension() == dim
				&& (pos == null ? true : tile.getPos().equals(pos));
	}

	public boolean sameDimAndPos(NetworkedHDDInterface tile, BlockPos pos,
			int dim) {
		if (tile == null || (tile.location == null && pos != null)
				|| tile.world == null || tile.world.provider == null)
			return false;
		return tile.world.provider.getDimension() == dim
				&& (pos == null ? true : tile.location.equals(pos));
	}

	public TileEntityPanel getPanel(BlockPos bc, int dim) {
		Iterator<TileEntityPanel> iPanel = panels.iterator();
		while (iPanel.hasNext()) {
			TileEntityPanel pan = iPanel.next();
			if (sameDimAndPos(pan, bc, dim)) {
				return pan;
			}
		}
		return null;
	}

	public TileEntityWirelessPanel getWirelessPanel(BlockPos bc, int dim) {
		Iterator<TileEntityWirelessPanel> iWPanel = wirelesspanels.iterator();
		while (iWPanel.hasNext()) {
			TileEntityWirelessPanel wpan = iWPanel.next();
			if (sameDimAndPos(wpan, bc, dim)) {
				return wpan;
			}
		}
		return null;
	}

	public IAutoCrafter getCrafter(BlockPos bc, int dim) {
		Iterator<IAutoCrafter> iCrafter = crafters.iterator();
		while (iCrafter.hasNext()) {
			IAutoCrafter cr = iCrafter.next();
			if (sameDimAndPos(cr, bc, dim)) {
				return cr;
			}
		}
		return null;
	}

	public void tileRemoved(TileEntityPipeEStorage itemConduit, BlockPos bc) {
		int dim = itemConduit.getWorld().provider.getDimension();

		NetworkedHDDInterface inter = getInterface(bc, dim);
		if (inter != null) {
			if (inter.getInterface().getNetworkInventory() != null) {
				inter.getInterface().getNetworkInventory()
						.updateItems(this, -2);
				inter.getInterface().getNetworkInventory()
						.updateFluids(this, -2);
			}
			this.masterInterfaces.remove(inter);
			inter.getInterface().setNetwork(null);
			updateInterfaces();
		}

		Iterator<IInsertListener> iListeners = listeners.iterator();
		while (iListeners.hasNext()) {
			IInsertListener listener = iListeners.next();
			if (listener instanceof TileEntity) {
				TileEntity tile = (TileEntity) listener;
				if (sameDimAndPos(tile, bc, dim)) {
					listeners.remove(listener);
					break;
				}
			}
		}

		TileEntity tile = itemConduit.getWorld().getTileEntity(bc);
		if (tile != null && tile instanceof INetworkTile) {
			((INetworkTile) tile).onDisconnected();
			((INetworkTile) tile).setNetwork(null);
		}

		if (tile != null && tile instanceof IPipeWrapper) {
			destroyNetwork();
		}

		TileEntityPanel panel = getPanel(bc, dim);
		if (panel != null) {
			panels.remove(panel);
		}

		TileEntityWirelessPanel wirelessPanel = getWirelessPanel(bc, dim);
		if (wirelessPanel != null) {
			wirelesspanels.remove(wirelessPanel);
		}

		IAutoCrafter crafter2 = getCrafter(bc, dim);
		if (crafter2 != null) {
			crafters.remove(crafter2);
			updatePatterns();
		}
	}

	@Override
	public void destroyNetwork() {
		super.destroyNetwork();
		/*
		 * Iterator<NetworkedHDDInterface> ii = masterInterfaces.iterator();
		 * //FIRST PASS while( ii.hasNext()) { final NetworkedHDDInterface inter
		 * = ii.next(); if(inter !=null && inter.getInterface() != null) {
		 * inter.getInterface().setNetwork(null); } }
		 */
		masterInterfaces.clear();
		interfaces.clear();

		/*
		 * Iterator<TileEntityPanel> iP = panels.iterator(); while
		 * (iP.hasNext()) { TileEntityPanel pan = iP.next(); pan.network = null;
		 * CrystalModNetwork.sendToAllAround( new
		 * PacketTileMessage(pan.getPos(), "ResetNet"), pan); }
		 */
		panels.clear();

		/*
		 * Iterator<TileEntityWirelessPanel> iWP = wirelesspanels.iterator();
		 * while (iWP.hasNext()) { TileEntityWirelessPanel pan = iWP.next();
		 * pan.network = null; CrystalModNetwork.sendToAllAround( new
		 * PacketTileMessage(pan.getPos(), "ResetNet"), pan); }
		 */
		wirelesspanels.clear();

		/*
		 * Iterator<TileCrafter> iCR = crafters2.iterator(); while
		 * (iCR.hasNext()) { iCR.next().setNetwork(null); }
		 */
		crafters.clear();

		listeners.clear();

		super.destroyNetwork();
	}

	protected int ticks;

	@Override
	public void doNetworkTick() {
		ticks++;

		for (ICraftingTask taskToCancel : craftingTasksToCancel) {
			taskToCancel.onCancelled(this);
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

		if (!craftingTasks.empty()) {
			ICraftingTask top = craftingTasks.peek();
			if (ticks % top.getPattern().getCrafter().getSpeed() == 0 && top.update(this)) {
				craftingTasks.pop();
			}
		}

		if (updateItems) {
			getItemStorage().scanNetworkForItems();
			for (INetworkContainer panel : watchers) {
				panel.sendItemsToAll();
			}
			updateItems = false;
		}
	}

	static int compare(int x, int y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	public void updateInterfaces() {
		this.interfaces.clear();
		for (NetworkedHDDInterface inter : this.masterInterfaces) {
			getOrCreateInterfaces(inter.getPriority()).add(inter);
		}
	}

	static final Comparator<Integer> PRIORITY_SORTER = new Comparator<Integer>() {

		@Override
		public int compare(final Integer o1, final Integer o2) {
			/*
			 * if( o2 == o1 ) { return 0; } if( o2 < o1 ) { return -1; } return
			 * 1;
			 */
			return o2.compareTo(o1);
		}
	};

	// AUTO CRAFTING

	public void updatePatterns() {
		patterns.clear();
		List<ItemStackData> data = Lists.newArrayList();
		for (IAutoCrafter crafter : crafters) {
			if (!crafter.showPatterns())
				continue;
			for (int s = 0; s < crafter.getPatterns().getSlots(); s++) {
				ItemStack patStack = crafter.getPatterns().getStackInSlot(s);

				if (patStack != null && ItemPattern.isValid(patStack)) {
					CraftingPattern pattern = new CraftingPattern(crafter.getWorld(), crafter, patStack);
					patterns.add(pattern);
					for (ItemStack stack : pattern.getOutputs()) {
						if (stack != null) {
							ItemStack copy = stack.copy();
							copy.stackSize = 0;
							ItemStackData iData = new ItemStackData(copy, -1,
									BlockPos.ORIGIN, 0);
							iData.isCrafting = true;
							data.add(iData);
						}
					}
				}
			}
		}

		for (INetworkContainer watcher : this.watchers) {
			if (watcher != null) {
				watcher.sendCraftingItemsToAll(data);
			}
		}
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

	public void scheduleCraftingTaskIfUnscheduled(ItemStack stack, int toSchedule) {
		int alreadyScheduled = 0;

		for (ICraftingTask task : getCraftingTasks()) {
			for (ItemStack output : task.getPattern().getOutputs()) {
				if (ItemUtil.canCombine(output, stack)) {
					alreadyScheduled++;
				}
			}
		}

		CraftingPattern pattern = getPatternWithBestScore(stack);

		if (pattern != null) {
			addCraftingTaskAsLast(createCraftingTask(stack, pattern, toSchedule - alreadyScheduled));
		}
	}

	public void addCraftingTaskAsLast(ICraftingTask task) {
		craftingTasksToAddAsLast.add(task);
	}

	public ICraftingTask createCraftingTask(ItemStack request, CraftingPattern pattern, int amt) {
		return new BasicCraftingTask(request, pattern, amt);
	}

	public void cancelCraftingTask(ICraftingTask task) {
		craftingTasksToCancel.add(task);
	}

	public List<CraftingPattern> getPatterns() {
		return patterns;
	}

	/*
	 * public CraftingPattern getPattern(ItemStack pattern) { for
	 * (CraftingPattern craftingPattern : getPatterns()) { for (ItemStack output
	 * : craftingPattern.getOutputs()) { if (ItemUtil.canCombine(output,
	 * pattern)) { return craftingPattern; } } }
	 * 
	 * return null; }
	 */

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

	public boolean canCraft(ItemStack stack) {
		return !getPattern(stack).isEmpty();
	}

	public List<CraftingPattern> getPattern(ItemStack pattern) {
		List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();

		for (CraftingPattern craftingPattern : getPatterns()) {
			for (ItemStack output : craftingPattern.getOutputs()) {
				if (ItemUtil.canCombine(output, pattern)) {
					patterns.add(craftingPattern);
				}
			}
		}

		return patterns;
	}

	public CraftingPattern getPatternWithBestScore(ItemStack pattern) {
		List<CraftingPattern> patterns = getPattern(pattern);

		if (patterns.isEmpty()) {
			return null;
		} else if (patterns.size() == 1) {
			return patterns.get(0);
		}

		int[] scores = new int[patterns.size()];
		int highestScore = 0;
		int highestPattern = 0;

		for (int i = 0; i < patterns.size(); ++i) {
			for (ItemStack input : patterns.get(i).getInputs()) {
				ItemStackData stored = getItemStorage().getItemData(input);

				scores[i] += stored != null && stored.stack != null
						&& !stored.isCrafting ? stored.stack.stackSize : 0;
			}

			if (scores[i] > highestScore) {
				highestScore = scores[i];
				highestPattern = i;
			}
		}

		return patterns.get(highestPattern);
	}

	public void handleCraftingRequest(ItemStackData data, int quantity) {
		if (data != null && quantity > 0 && quantity <= 500) {
			ItemStack requested = data.stack;

			CraftingPattern pattern = getPatternWithBestScore(requested);

			if (pattern != null) {
				addCraftingTaskAsLast(createCraftingTask(requested, pattern, quantity));
				/*for (ItemStack output : pattern.getOutputs()) {
					if (ItemUtil.canCombine(requested, output)) {
						quantityPerRequest += output.stackSize;
						if (!pattern.isProcessing()) {
							break;
						}
					}
				}

				while (quantity > 0) {
					addCraftingTaskAsLast(createCraftingTask(pattern));

					quantity -= quantityPerRequest;
				}*/
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

	public void notifyInsert(ItemStack stack) {
		Iterator<IInsertListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().onItemInserted(stack);
		}
	}
}
