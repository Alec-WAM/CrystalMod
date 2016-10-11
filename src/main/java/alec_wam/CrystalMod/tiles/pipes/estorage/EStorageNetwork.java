package alec_wam.CrystalMod.tiles.pipes.estorage;

import io.netty.handler.codec.EncoderException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Stack;
import java.util.TreeMap;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;
import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.IAutoCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ItemPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.BasicCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.ICraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.ProcessingCraftingTask;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.INetworkItemProvider;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class EStorageNetwork extends AbstractPipeNetwork {
	public EStorageNetwork masterNetwork;
	public final List<NetworkedHDDInterface> masterInterfaces = Lists.newArrayList();
	public final NavigableMap<Integer, List<NetworkedHDDInterface>> interfaces = new TreeMap<Integer, List<NetworkedHDDInterface>>(PRIORITY_SORTER);

	final List<TileEntityPanel> panels = new ArrayList<TileEntityPanel>();
	
	final List<TileEntityWirelessPanel> wirelesspanels = new ArrayList<TileEntityWirelessPanel>();

	final Map<BlockPos, TileEntityPipeEStorage> pipMap = new HashMap<BlockPos, TileEntityPipeEStorage>();

	public final List<INetworkContainer> watchers = new ArrayList<INetworkContainer>();
	public final List<IInsertListener> listeners = new ArrayList<IInsertListener>();

	public List<ItemStackData> items = new ArrayList<ItemStackData>();

	public boolean updateItems = true;

	//AUTO CRAFTING
	final List<IAutoCrafter> crafters = new ArrayList<IAutoCrafter>();
	
	private List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();
	
	private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();
	
	
	public static class ItemStackData {
		public ItemStack stack;
		public int index;
		public BlockPos interPos;
		public int interDim;
		public boolean isCrafting;

		private String oreDictString;
		private String modIdString;
		
		public ItemStackData(){
			
		}
		
		public ItemStackData(ItemStack stack) {
			this.stack = stack;
			this.index = 0;
			this.interPos = BlockPos.ORIGIN;
			this.interDim = 0;
		}

		public ItemStackData(ItemStack stack, int index, BlockPos interPos, int dim) {
			this.stack = stack;
			this.index = index;
			this.interPos = interPos;
			this.interDim = dim;
		}

		public void toBytes(CompressedDataOutput cdo) throws IOException {
			if (stack == null) {
				cdo.writeShort(-1);
			} else {
				cdo.writeShort(Item.getIdFromItem(stack.getItem()));
				cdo.writeVariable(stack.stackSize);
				cdo.writeShort(stack.getMetadata());
				NBTTagCompound nbttagcompound = null;

				if (stack.getItem().isDamageable()
						|| stack.getItem().getShareTag()) {
					nbttagcompound = stack.getTagCompound();
				}

				if (nbttagcompound == null) {
					cdo.writeByte(0);
				} else {
					cdo.writeByte(1);
					try {
						CompressedStreamTools.write(nbttagcompound, cdo);
					} catch (IOException ioexception) {
						throw new EncoderException(ioexception);
					}
				}
			}
			cdo.writeInt(index);
			cdo.writeInt(interPos.getX());
			cdo.writeInt(interPos.getY());
			cdo.writeInt(interPos.getZ());
			cdo.writeInt(interDim);
			cdo.writeBoolean(isCrafting);
		}

		public static ItemStackData fromBytes(CompressedDataInput cdi)
				throws IOException {
			ItemStackData newData = new ItemStackData();
			ItemStack itemstack = null;
			int i = cdi.readShort();
			if (i >= 0) {
				int j = cdi.readVariable();
				int k = cdi.readShort();
				itemstack = new ItemStack(Item.getItemById(i), j, k);

				NBTTagCompound nbt = null;
				byte b0 = cdi.readByte();

				if (b0 == 0) {
					nbt = null;
				} else {
					try {
						nbt = CompressedStreamTools.read(cdi,
								new NBTSizeTracker(2097152L));
					} catch (Exception e) {
						System.out.println("Error Loading NBT to "
								+ itemstack.getDisplayName());
					}
				}
				itemstack.setTagCompound(nbt);
			}
			newData.stack = itemstack;
			newData.index = cdi.readInt();
			int x = cdi.readInt();
			int y = cdi.readInt();
			int z = cdi.readInt();
			newData.interPos = new BlockPos(x, y, z);
			newData.interDim = cdi.readInt();
			newData.isCrafting = cdi.readBoolean();
			return newData;
		}

		public String getUnlocName() {
			return findUnlocName();
		}

		public String getLowercaseUnlocName(Locale locale) {
			return Lang.translateToLocal(getUnlocName()).toLowerCase(
					locale);
		}

		private String findUnlocName() {
			if (stack == null)
				return "null";
			String name = "";
			try {
				name = stack.getDisplayName();
				if (name == null || name.isEmpty()) {
					name = stack.getItem().getUnlocalizedName();
					if (name == null || name.isEmpty()) {
						name = stack.getItem().getClass().getName();
					}
				}
			} catch (Throwable ex) {
				name = "Exception: " + ex.getMessage();
			}
			return name;
		}

		public String getModId() {
			return findModId();
		}

		private String findModId() {
			if(!Strings.isNullOrEmpty(modIdString)){
				return modIdString;
			}
			if (stack == null || stack.getItem() == null)
				return "";
			ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(stack.getItem());
			if (resourceInput != null
					&& resourceInput.getResourceDomain() != null) {
				return modIdString = resourceInput.getResourceDomain();
			} else {
				return "";
			}
		}
		
		public String getOreDic(){
			return findOreNames();
		}
		
		private String findOreNames(){
			if(!Strings.isNullOrEmpty(oreDictString)){
				return oreDictString;
			}
			if (stack == null)
				return "";
			StringBuilder oreDictStringBuilder = new StringBuilder();
			for (int oreId : OreDictionary.getOreIDs(stack)) {
				String oreName = OreDictionary.getOreName(oreId).toLowerCase(Locale.ENGLISH);
				oreDictStringBuilder.append(oreName).append(' ');
			}
			return oreDictString = oreDictStringBuilder.toString();
		}
		
		public int getAmount(){
			return stack == null ? 0 : stack.stackSize;
		}
	}
	
	public int addItemToNetwork(ItemStack stack, boolean sim){
		return this.addItemToNetwork(stack, sim, true);
	}
	
	public int addItemToNetwork(ItemStack stack, boolean sim, boolean sendUpdate) {
		if (stack == null)
			return 0;
		int inserted = 0;
		Iterator<List<NetworkedHDDInterface>> i1 = interfaces.values().iterator();
		while(i1.hasNext()){
			ItemStack insertCopy = stack.copy();
			final List<NetworkedHDDInterface> list = i1.next();
			Iterator<NetworkedHDDInterface> ii = list.iterator();
			//FIRST PASS
			while( ii.hasNext())
			{
				final NetworkedHDDInterface inter = ii.next();
				if (inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						int amt = inter.getInterface().getNetworkInventory().insertItem(this, insertCopy, true, sim, sendUpdate);
						insertCopy.stackSize-=amt;
						inserted+=amt;
						if(insertCopy.stackSize <= 0){
							return inserted;
						}
					}
				}
			}
			
			//SECOND PASS
			ii = list.iterator();
			while(ii.hasNext()){
				final NetworkedHDDInterface inter = ii.next();
				if (inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						int amt = inter.getInterface().getNetworkInventory().insertItem(this, insertCopy, false, sim, sendUpdate);
						insertCopy.stackSize-=amt;
						inserted+=amt;
						if(insertCopy.stackSize <= 0){
							return inserted;
						}
					}
				}
			}
		}

		if(this.masterNetwork !=null){
			return masterNetwork.addItemToNetwork(stack, sim, sendUpdate);
		}
		return inserted;
	}

	public ItemStack removeItemsFromNetwork(ItemStack[] itemStacks){
		for (ItemStack itemStack : itemStacks) {
			ItemStackData data = getData(itemStack);
			if(data == null || data.stack == null || data.isCrafting)continue;
			int removedFake = removeItemFromNetwork(data, itemStack.stackSize, true);
			if (removedFake >= itemStack.stackSize) {
				ItemStack stack = data.stack.copy();
				stack.stackSize = itemStack.stackSize;
				removeItemFromNetwork(getData(itemStack), itemStack.stackSize, false);
				return stack;
			}
			return null;
		}
		return null;
	}
	
	public ItemStack removeItemFromNetwork(ItemStack stack, boolean sim){
		return this.removeItemFromNetwork(stack, sim, true);
	}
	
	public ItemStack removeItemFromNetwork(ItemStack stack, boolean sim, boolean sendUpdate){
		ItemStackData data = getData(stack);
		if(data == null || data.stack == null || data.isCrafting)return null;
		ItemStack ret = data.stack.copy();
		int removedFake = removeItemFromNetwork(data, stack.stackSize, sim, sendUpdate);
		
		ret.stackSize = removedFake;
		if(ret.stackSize <=0){
			return null;
		}
		return ret;
	}
	
	public int removeItemFromNetwork(ItemStackData data, int amount, boolean sim){
		return this.removeItemFromNetwork(data, amount, sim, true);
	}
	
	public int removeItemFromNetwork(ItemStackData data, int amount, boolean sim, boolean sendUpdate) {
		if (data == null || data.stack == null)
			return 0;
		Iterator<List<NetworkedHDDInterface>> i1 = interfaces.values().iterator();
		while(i1.hasNext()){
			Iterator<NetworkedHDDInterface> ii = i1.next().iterator();
			while( ii.hasNext())
			{
				final NetworkedHDDInterface inter = ii.next();
				if (sameDimAndPos(inter, data.interPos, data.interDim)) {
					if(inter.getInterface().getNetworkInventory() !=null){
						int extract = inter.getInterface().getNetworkInventory().extractItem(this, data.stack, amount, true, sendUpdate);
						if(extract >= 0){
							return sim ? extract : inter.getInterface().getNetworkInventory().extractItem(this, data.stack, amount, false, sendUpdate);
						}
					}
				}
			}
		}

		if(this.masterNetwork !=null){
			return this.masterNetwork.removeItemFromNetwork(data, amount, sim, sendUpdate);
		}
		return 0;
	}

	public ItemStackData getData(ItemStack stack) {
		Iterator<ItemStackData> iData = items.iterator();
		while(iData.hasNext()) {
			ItemStackData data = iData.next();
			if (data.stack != null && ItemUtil.canCombine(stack, data.stack)) {
				return data;
			}
		}
		if(this.masterNetwork !=null){
			return masterNetwork.getData(stack);
		}
		return null;
	}
	
	public ItemStackData getDataOre(ItemStack stack) {
		Iterator<ItemStackData> iData = items.iterator();
		while(iData.hasNext()) {
			ItemStackData data = iData.next();
			if (data.stack != null && ItemUtil.stackMatchUseOre(stack, data.stack)) {
				return data;
			}
		}
		if(this.masterNetwork !=null){
			return masterNetwork.getDataOre(stack);
		}
		return null;
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
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			int count = items.size();
			cdo.writeVariable(count);
			for (ItemStackData entry : items) {
				entry.toBytes(cdo);
			}
			return cdo.getCompressed();
		} finally {
			cdo.close();
		}
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

	public void tileAdded(TileEntityPipeEStorage itemPipe, EnumFacing direction, BlockPos bc, TileEntity externalTile) {
		
		if(externalTile instanceof INetworkTile){
			((INetworkTile)externalTile).setNetwork(this);
		}
		
		if(externalTile instanceof IInsertListener){
			listeners.add((IInsertListener) externalTile);
		}
		
		if (externalTile instanceof INetworkItemProvider) {
			INetworkItemProvider inter = (INetworkItemProvider) externalTile;
			NetworkedHDDInterface inv = new NetworkedHDDInterface(inter, externalTile.getWorld(), bc);
			this.masterInterfaces.add(inv);
			this.updateInterfaces();
			if(inv.getInterface().getNetworkInventory() !=null){
				inv.getInterface().getNetworkInventory().updateItems(this, -1);
			}
		}
		
		if (externalTile instanceof TileEntityPanel) {
			panels.add((TileEntityPanel) externalTile);
		}
		if (externalTile instanceof TileEntityWirelessPanel) {
			wirelesspanels.add((TileEntityWirelessPanel) externalTile);
		}
		
		if(externalTile instanceof IAutoCrafter){
			crafters.add((IAutoCrafter)externalTile);
			updatePatterns();
		}
	}

	public List<NetworkedHDDInterface> getOrCreateInterfaces(int priority){
		List<NetworkedHDDInterface> list = interfaces.get( priority );
		if( list == null )
		{
			interfaces.put( priority, list = new ArrayList<NetworkedHDDInterface>() );
		}
		return list;
	}
	
	public NetworkedHDDInterface getInterface(BlockPos pos, int dim){
		Iterator<NetworkedHDDInterface> ii = masterInterfaces.iterator();
		//FIRST PASS
		while( ii.hasNext())
		{
			final NetworkedHDDInterface inter = ii.next();
			if(inter !=null){
				if(sameDimAndPos(inter, pos, dim)){
					return inter;
				}
			}
		}
		return null;
	}

	public boolean sameDimAndPos(IAutoCrafter crafter, BlockPos pos, int dim){
		if(crafter == null || (crafter.getPos() == null && pos != null))return false;
		return crafter.getDimension() == dim && (pos == null ? true : crafter.getPos().equals(pos));
	}
	
	public boolean sameDimAndPos(TileEntity tile, BlockPos pos, int dim){
		if(tile == null || (tile.getPos() == null && pos != null) || tile.getWorld() == null || tile.getWorld().provider == null)return false;
		return tile.getWorld().provider.getDimension() == dim && (pos == null ? true : tile.getPos().equals(pos));
	}
	
	public boolean sameDimAndPos(NetworkedHDDInterface tile, BlockPos pos, int dim){
		if(tile == null || (tile.location == null && pos != null) || tile.world == null || tile.world.provider == null)return false;
		return tile.world.provider.getDimension() == dim && (pos == null ? true : tile.location.equals(pos));
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
			inter.updateItems(this, -2);
			this.masterInterfaces.remove(inter);
			inter.getInterface().setNetwork(null);
			updateInterfaces();
		}

		Iterator<IInsertListener> iListeners = listeners.iterator();
		while(iListeners.hasNext()){
			IInsertListener listener = iListeners.next();
			if(listener instanceof TileEntity){
				TileEntity tile = (TileEntity) listener;
				if(sameDimAndPos(tile, bc, dim)){
					listeners.remove(listener);
					break;
				}
			}
		}
		
		TileEntity tile = itemConduit.getWorld().getTileEntity(bc);
		if(tile !=null && tile instanceof INetworkTile){
			((INetworkTile)tile).onDisconnected();
			((INetworkTile)tile).setNetwork(null);
		}
		
		if(tile !=null && tile instanceof IPipeWrapper){
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
		if(crafter2 !=null){
			crafters.remove(crafter2);
			updatePatterns();
		}
	}

	@Override
	public void destroyNetwork() {
		super.destroyNetwork();
		/*Iterator<NetworkedHDDInterface> ii = masterInterfaces.iterator();
		//FIRST PASS
		while( ii.hasNext())
		{
			final NetworkedHDDInterface inter = ii.next();
			if(inter !=null && inter.getInterface() != null) {
				inter.getInterface().setNetwork(null);
			}
		}*/
		masterInterfaces.clear();
		interfaces.clear();

		/*Iterator<TileEntityPanel> iP = panels.iterator();
		while (iP.hasNext()) {
			TileEntityPanel pan = iP.next();
			pan.network = null;
			CrystalModNetwork.sendToAllAround(
					new PacketTileMessage(pan.getPos(), "ResetNet"), pan);
		}*/
		panels.clear();
		
		/*Iterator<TileEntityWirelessPanel> iWP = wirelesspanels.iterator();
		while (iWP.hasNext()) {
			TileEntityWirelessPanel pan = iWP.next();
			pan.network = null;
			CrystalModNetwork.sendToAllAround(
					new PacketTileMessage(pan.getPos(), "ResetNet"), pan);
		}*/
		wirelesspanels.clear();
		
		/*Iterator<TileCrafter> iCR = crafters2.iterator();
		while (iCR.hasNext()) {
			iCR.next().setNetwork(null);
		}*/
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
            World world = DimensionManager.isDimensionRegistered(top.getPattern().crafterDim) ? FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(top.getPattern().crafterDim) : null;
            if(world !=null){
            	if (ticks % top.getPattern().getCrafter(world).getSpeed() == 0 && top.update(this)) {
	                top.onDone(this);
	
	                craftingTasks.pop();
	            }
            }
        }
		
		if (updateItems) {
			items.clear();
			Iterator<List<NetworkedHDDInterface>> i1 = interfaces.values().iterator();
			while(i1.hasNext()){
				Iterator<NetworkedHDDInterface> ii = i1.next().iterator();
				//FIRST PASS
				while( ii.hasNext())
				{
					final NetworkedHDDInterface inter = ii.next();
					if(inter !=null && inter.getInterface() != null) {
						if(inter.getInterface().getNetworkInventory() !=null){
							Iterator<ItemStackData> data = inter.getInterface().getNetworkInventory().getItems(this).iterator();
							while(data.hasNext()){
								items.add(data.next());
							}
						}
					}
				}
			}
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

		// erase list.
		this.interfaces.clear();
		for(NetworkedHDDInterface inter : this.masterInterfaces){
			getOrCreateInterfaces(inter.getPriority()).add(inter);
		}
	}
	
	static final Comparator<Integer> PRIORITY_SORTER = new Comparator<Integer>()
	{

		@Override
		public int compare( final Integer o1, final Integer o2 )
		{
			/*if( o2 == o1 )
			{
				return 0;
			}
			if( o2 < o1 )
			{
				return -1;
			}
			return 1;*/
			return o2.compareTo(o1);
		}
	};

	//AUTO CRAFTING
	
	public void updatePatterns(){
		patterns.clear();
		List<ItemStackData> data = Lists.newArrayList();
		for(IAutoCrafter crafter : crafters){
			if(!crafter.showPatterns())continue;
			for(int s = 0; s < crafter.getPatterns().getSlots(); s++){
				ItemStack patStack = crafter.getPatterns().getStackInSlot(s);
				
				if(patStack !=null && ItemPattern.isValid(patStack)){
					CraftingPattern pattern = new CraftingPattern(crafter,patStack);
					patterns.add(pattern);
					for(ItemStack stack : pattern.getOutputs()){
						if(stack !=null){
							ItemStack copy = stack.copy();
							copy.stackSize = 0;
							ItemStackData iData = new ItemStackData(copy, -1, BlockPos.ORIGIN, 0);
							iData.isCrafting = true;
							data.add(iData);
						}
					}
				}
			}
		}
		
		for(INetworkContainer watcher : this.watchers){
			if(watcher !=null){
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
        for(ItemStack stack : pattern.getOutputs()){
        	if(isCrafting(stack))return;
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

        for (int i = 0; i < toSchedule - alreadyScheduled; ++i) {
        	CraftingPattern pattern = getPatternWithBestScore(stack);

            if (pattern != null) {
                addCraftingTaskAsLast(createCraftingTask(pattern));
            }
        }
    }

    public void addCraftingTaskAsLast(ICraftingTask task) {
        craftingTasksToAddAsLast.add(task);
    }

    public ICraftingTask createCraftingTask(CraftingPattern pattern) {
        if (pattern.isProcessing()) {
            return new ProcessingCraftingTask(pattern);
        } else {
            return new BasicCraftingTask(pattern);
        }
    }

    public void cancelCraftingTask(ICraftingTask task) {
        craftingTasksToCancel.add(task);
    }

    public List<CraftingPattern> getPatterns() {
        return patterns;
    }

    /*public CraftingPattern getPattern(ItemStack pattern) {
        for (CraftingPattern craftingPattern : getPatterns()) {
            for (ItemStack output : craftingPattern.getOutputs()) {
                if (ItemUtil.canCombine(output, pattern)) {
                    return craftingPattern;
                }
            }
        }

        return null;
    }*/
    
    public boolean isCrafting(ItemStack stack){
    	for (ICraftingTask task : getCraftingTasks()) {
    		CraftingPattern pattern = task.getPattern();
    		if(pattern !=null && pattern.getOutputs() !=null){
    			for(ItemStack cStack : pattern.getOutputs()){
    				if(ItemUtil.canCombine(stack, cStack)){
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    public boolean canCraft(ItemStack stack){
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
                ItemStackData stored = getData(input);

                scores[i] += stored != null && stored.stack !=null ? stored.stack.stackSize : 0;
            }

            if (scores[i] > highestScore) {
                highestScore = scores[i];
                highestPattern = i;
            }
        }

        return patterns.get(highestPattern);
    }
    
    public void handleCraftingRequest(ItemStackData data, int quantity) {
        if (data !=null && quantity > 0 && quantity <= 500) {
            ItemStack requested = data.stack;

            int quantityPerRequest = 0;

            CraftingPattern pattern = getPatternWithBestScore(requested);

            if (pattern != null) {
                for (ItemStack output : pattern.getOutputs()) {
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

	public void notifyInsert(ItemStack stack) {
		for (int i = 0; i < stack.stackSize; ++i) {
            if (!craftingTasks.empty()) {
                ICraftingTask top = craftingTasks.peek();

                if (top instanceof ProcessingCraftingTask) {
                    ((ProcessingCraftingTask) top).onPushed(stack);
                }
            }
        }

		Iterator<IInsertListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().onItemInserted(stack);
		}
	}
}
