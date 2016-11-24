package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.IAutoCrafter;
import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.api.estorage.IInsertListener;
import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.api.estorage.INetworkItemProvider;
import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.api.estorage.INetworkTileConnectable;
import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ItemPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCraftingController;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessBase;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessExternal;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessNormal;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

public class EStorageNetwork extends AbstractPipeNetwork {
	private final ItemStorage itemStorage = new ItemStorage(this);
	private final FluidStorage fluidStorage = new FluidStorage(this);

	public final Map<BlockPos, TileEntity> networkTiles = new HashMap<BlockPos, TileEntity>();
	public TileCraftingController craftingController;
	public final List<NetworkedItemProvider> masterInterfaces = Lists.newArrayList();
	public final NavigableMap<Integer, List<NetworkedItemProvider>> interfaces = new TreeMap<Integer, List<NetworkedItemProvider>>(
			PRIORITY_SORTER);

	final Map<BlockPos, TileEntityPipeEStorage> pipMap = new HashMap<BlockPos, TileEntityPipeEStorage>();

	public final List<INetworkContainer> watchers = new ArrayList<INetworkContainer>();
	public final List<IInsertListener> listeners = new ArrayList<IInsertListener>();

	public boolean updateItems = true;
	public boolean updateFluids = true;

	// AUTO CRAFTING
	final List<IAutoCrafter> crafters = new ArrayList<IAutoCrafter>();
	private List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();

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

	public boolean canConnect(TileEntity tile){
		if(tile instanceof TileCraftingController){
			return craftingController == null;
		}
		if(tile instanceof INetworkTileConnectable){
			return ((INetworkTileConnectable)tile).canConnect(this);
		}
		return true;
	}
	
	public void tileAdded(TileEntityPipeEStorage itemPipe,
			EnumFacing direction, BlockPos bc, TileEntity externalTile) {

		if(networkTiles.containsKey(bc)){
			return;
		}
		if(!canConnect(externalTile)){
			return;
		}
		
		networkTiles.put(bc, externalTile);
		
		if(externalTile instanceof TileCraftingController){
			TileCraftingController crafter = (TileCraftingController)externalTile;
			if(craftingController !=null)return;
			craftingController = crafter;
		}
		
		
		if (externalTile instanceof INetworkTile) {
			((INetworkTile) externalTile).setNetwork(this);
		}

		if (externalTile instanceof IInsertListener) {
			listeners.add((IInsertListener) externalTile);
		}

		if (externalTile instanceof INetworkItemProvider) {
			INetworkItemProvider inter = (INetworkItemProvider) externalTile;
			NetworkedItemProvider inv = new NetworkedItemProvider(inter, externalTile.getWorld(), bc);
			this.masterInterfaces.add(inv);
			this.updateInterfaces();
		}

		if (externalTile instanceof IAutoCrafter) {
			crafters.add((IAutoCrafter) externalTile);
			updatePatterns();
		}
	}

	public List<NetworkedItemProvider> getOrCreateInterfaces(int priority) {
		List<NetworkedItemProvider> list = interfaces.get(priority);
		if (list == null) {
			interfaces.put(priority,
					list = new ArrayList<NetworkedItemProvider>());
		}
		return list;
	}

	public NetworkedItemProvider getInterface(BlockPos pos, int dim) {
		Iterator<NetworkedItemProvider> ii = masterInterfaces.iterator();
		while (ii.hasNext()) {
			final NetworkedItemProvider inter = ii.next();
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

	public boolean sameDimAndPos(NetworkedItemProvider tile, BlockPos pos,
			int dim) {
		if (tile == null || (tile.location == null && pos != null)
				|| tile.world == null || tile.world.provider == null)
			return false;
		return tile.world.provider.getDimension() == dim
				&& (pos == null ? true : tile.location.equals(pos));
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
		networkTiles.remove(bc);
		int dim = itemConduit.getWorld().provider.getDimension();

		NetworkedItemProvider inter = getInterface(bc, dim);
		if (inter != null) {
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

		IAutoCrafter crafter2 = getCrafter(bc, dim);
		if (crafter2 != null) {
			crafters.remove(crafter2);
			updatePatterns();
		}
	}

	@Override
	public void destroyNetwork() {
		super.destroyNetwork();
		masterInterfaces.clear();
		interfaces.clear();
		crafters.clear();
		listeners.clear();

		super.destroyNetwork();
	}
	
	@Override
	public void doNetworkTick() {
		if (updateItems) {
			getItemStorage().invalidate();
			updateItems = false;
		}
	}

	static int compare(int x, int y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	public void updateInterfaces() {
		this.interfaces.clear();
		for (NetworkedItemProvider inter : this.masterInterfaces) {
			getOrCreateInterfaces(inter.getPriority()).add(inter);
		}
		getItemStorage().invalidate();
		getFluidStorage().invalidate();
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
				if (!ItemStackTools.isNullStack(patStack)) {
					CraftingPattern pattern = crafter.createPattern(patStack);
					if(pattern.isValid()){
						patterns.add(pattern);
						for (ItemStack stack : pattern.getOutputs()) {
							if (stack != null) {
								ItemStack copy = stack.copy();
								ItemStackTools.setStackSize(copy, 0);
								ItemStackData iData = new ItemStackData(copy);
								iData.isCrafting = true;
								data.add(iData);
							}
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

	public List<CraftingPattern> getPatterns() {
		return patterns;
	}

	public List<CraftingPattern> getPattern(ItemStack pattern, boolean ore) {
		List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();

		for (CraftingPattern craftingPattern : getPatterns()) {
			ItemStackList outputs = new ItemStackList();
			for (ItemStack output : craftingPattern.getOutputs()) {
				if (!ItemStackTools.isNullStack(output)) {
					outputs.add(output);
				}
			}
			
			ItemStack out = outputs.get(pattern, ore);
			if(ItemStackTools.isValid(out)){
				patterns.add(craftingPattern);
			}
		}

		return patterns;
	}

	public CraftingPattern getPatternWithBestScore(ItemStack pattern) {
		return getPatternWithBestScore(pattern, false);
	}
	
	public CraftingPattern getPatternWithBestScore(ItemStack pattern, boolean ore) {
		List<CraftingPattern> patterns = getPattern(pattern, ore);

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
				if(!ItemStackTools.isNullStack(input)){
					ItemStackData stored = getItemStorage().getItemData(input);
					
					if(stored == null && ore){
						stored = getItemStorage().getOreItemData(input);
					}
	
					scores[i] += stored != null && !stored.isCrafting ? stored.getAmount() : 0;
				}
			}

			if (scores[i] > highestScore) {
				highestScore = scores[i];
				highestPattern = i;
			}
		}

		return patterns.get(highestPattern);
	}

	public void notifyInsert(ItemStack stack) {
		Iterator<IInsertListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().onItemInserted(stack);
		}
	}
}
