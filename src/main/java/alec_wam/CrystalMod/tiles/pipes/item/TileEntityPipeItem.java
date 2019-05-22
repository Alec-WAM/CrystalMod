package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.items.ItemVariant;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeHitData;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipePart;
import alec_wam.CrystalMod.tiles.pipes.EnumPipeUpgrades;
import alec_wam.CrystalMod.tiles.pipes.InternalPipeInventory;
import alec_wam.CrystalMod.tiles.pipes.NetworkPos;
import alec_wam.CrystalMod.tiles.pipes.NetworkType;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipeBase;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;

public class TileEntityPipeItem extends TileEntityPipeBase {
	//TODO Update connections on insertion and extraction of cobble gen upgrade
	//TODO Add Hopper Upgrade
	//TODO Add Force upgrade with Hopper and Cobble Upgrade
	private Map<EnumFacing, InternalPipeInventory> pipeItems;
	private int[] priorities;
	
	public TileEntityPipeItem() {
		super(ModBlocks.TILE_PIPE_ITEM);
		pipeItems = Maps.newHashMap();
		for(final EnumFacing facing : EnumFacing.values()){
			InternalPipeInventory inv = new InternalPipeInventory(facing, new TextComponentString("Items"), 4) {
				@Override
				public void markDirty(int slot, ItemStack lastStack) {
					super.markDirty(slot, lastStack);
					internalInventoryChanged(facing, slot, lastStack);
				}
			};
			pipeItems.put(facing, inv);
		}
		priorities = new int[6];
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagCompound itemData = new NBTTagCompound();
		for(EnumFacing facing : EnumFacing.values()){
			NBTTagList nbttaglist = new NBTTagList();
			InternalPipeInventory internalInventory = getInternalInventory(facing);
			for(int s = 0; s < internalInventory.getSizeInventory(); s++) {
				ItemStack itemstack = internalInventory.getStackInSlot(s);
				if (!itemstack.isEmpty()) {
					NBTTagCompound data = new NBTTagCompound();
					NBTTagCompound stackNBT = itemstack.write(new NBTTagCompound());
					data.setInt("Slot", s);
					data.setTag("Stack", stackNBT);
					nbttaglist.add(data);
				}
			}
			itemData.setTag(facing.getName().toLowerCase(), nbttaglist);
		}

		nbt.setTag("Inventory", itemData);
		nbt.setIntArray("Priorities", priorities);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		NBTTagCompound connectionSettingData = nbt.getCompound("Inventory");
		for(EnumFacing facing : EnumFacing.values()){
			InternalPipeInventory internalInventory = getInternalInventory(facing);
			NBTTagList nbttaglist = connectionSettingData.getList(facing.getName().toLowerCase(), 10);
			for(int s = 0; s < nbttaglist.size(); s++) {
				NBTTagCompound data = nbttaglist.getCompound(s);
				int slot = data.getInt("Slot");
				ItemStack itemstack = ItemStack.read(data.getCompound("Stack"));
				if (!itemstack.isEmpty()) {
					internalInventory.setInventorySlotContents(slot, itemstack);
				}
			}
		}			
		
		if(nbt.hasKey("Priorities")){
			this.priorities = nbt.getIntArray("Priorities");
		} else {
			this.priorities = new int[6];
		}
	}
	
	@Override
	public NetworkType getNetworkType() {
		return NetworkType.ITEM;
	}

	@Override
	public PipeNetworkItem createNewNetwork() {
		return new PipeNetworkItem();
	}
	
	@Override
	public boolean canConnectToExternal(EnumFacing facing, boolean ignore){
		return getExternalInventory(facing) !=null;
	}

	@Override
	public void externalConnectionAdded(EnumFacing direction) {
		super.externalConnectionAdded(direction);
		if(network != null && network instanceof PipeNetworkItem) {
			PipeNetworkItem net = (PipeNetworkItem)network;
			if(getExternalInventory(direction) !=null && net.getInventory(this, direction) == null){
				((PipeNetworkItem)network).inventoryAdded(this, direction);
			}
		}
	}

	@Override
	public void externalConnectionRemoved(EnumFacing direction) {
		externalConnections.remove(direction);
		if(network != null && network instanceof PipeNetworkItem) {
			((PipeNetworkItem)network).inventoryRemoved(this, direction);
		}
	}

	public void internalInventoryChanged(EnumFacing facing, int slot, ItemStack lastStack) {
		//Upgrades
		if(slot == 2 || slot == 3){
			boolean isExternal = false;
			ItemStack currentStack = getInternalInventory(facing).getStackInSlot(slot);
			/*if(ModItems.pipeUpgrades.getItems().contains(currentStack.getItem())){
				@SuppressWarnings("unchecked")
				ItemVariant<EnumPipeUpgrades> upgrade = (ItemVariant<EnumPipeUpgrades>)currentStack.getItem();
				if(upgrade.type.isExternal()){
					isExternal = true;
				}
			}*/
			if(ModItems.pipeUpgrades.getItems().contains(lastStack.getItem())){
				@SuppressWarnings("unchecked")
				ItemVariant<EnumPipeUpgrades> upgrade = (ItemVariant<EnumPipeUpgrades>)lastStack.getItem();
				if(upgrade.type.isExternal()){
					isExternal = true;
				}
			}
			if(isExternal){
				this.rebuildConnections = true;
			}
		}
	}
	
	@Override
	public boolean onActivated(World world, EntityPlayer player, EnumHand hand, PipeHitData hitData) {
		ItemStack held = player.getHeldItem(hand);
		if(hitData !=null){
			if(hitData.part == null || hitData.part == PipePart.CENTER){
				EnumFacing side = hitData.face;
				if(externalUpgradeInsert(held, side, !world.isRemote)){
					if(!player.abilities.isCreativeMode){
						held.shrink(1);
						player.setHeldItem(hand, held);
					}
					return true;
				}
			}
		} 
		return false;
	}
	
	public boolean externalUpgradeInsert(ItemStack stack, EnumFacing side, boolean server){
		if(ModItems.pipeUpgrades.getItems().contains(stack.getItem())){
			@SuppressWarnings("unchecked")
			ItemVariant<EnumPipeUpgrades> upgrade = (ItemVariant<EnumPipeUpgrades>)stack.getItem();
			if(!getUpgrades(side).containsKey(upgrade) && upgrade.type.isExternal()){
				if(getUpgradeType(side, 0) == null){
					this.getInternalInventory(side).setInventorySlotContents(2, ItemUtil.copy(stack, 1));
					if(server){
						this.rebuildConnections = true;
					}
					return true;
				}
				else if(getUpgradeType(side, 1) == null){
					this.getInternalInventory(side).setInventorySlotContents(3, ItemUtil.copy(stack, 1));
					if(server){
						this.rebuildConnections = true;
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean canDoCobbleGen(EnumFacing direction){
		NetworkPos pos = getNetworkPos().offset(direction);
		Map<EnumPipeUpgrades, Integer> upgrades = getUpgrades(direction);
		if(BlockUtil.isCobbleGen(getWorld(), pos.getBlockPos(), direction)){
			if(upgrades.containsKey(EnumPipeUpgrades.COBBLE)){
				return true;
			}
		}
		return false;
	}
	
	public IItemHandler getExternalInventory(EnumFacing facing) {
		World world = getWorld();
	    if(world == null) {
	      return null;
	    }
	    BlockPos loc = getPos().offset(facing);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null) {
	      return ItemUtil.getItemHandler(te, facing.getOpposite());
	    }
	    
	    if(canDoCobbleGen(facing)){
	    	return NetworkInventory.COBBLE_GEN_INVENTORY;
	    }
	    
	    return null;
	}
	
	public InternalPipeInventory getInternalInventory(EnumFacing facing){
		return pipeItems.get(facing);
	}
	
	public ItemStack getInFilter(EnumFacing facing){
		InternalPipeInventory inv = getInternalInventory(facing);
		return inv.getStackInSlot(0);
	}
	
	public ItemStack getOutFilter(EnumFacing facing){
		InternalPipeInventory inv = getInternalInventory(facing);
		return inv.getStackInSlot(1);
	}
	
	@SuppressWarnings("unchecked")
	public Map<EnumPipeUpgrades, Integer> getUpgrades(EnumFacing facing){
		Map<EnumPipeUpgrades, Integer> map = Maps.newHashMap();
		InternalPipeInventory inv = getInternalInventory(facing);
		ItemStack upgrade1 = inv.getStackInSlot(2);
		ItemStack upgrade2 = inv.getStackInSlot(3);
		if(ModItems.pipeUpgrades.getItems().contains(upgrade1.getItem())){
			ItemVariant<EnumPipeUpgrades> upgrade = (ItemVariant<EnumPipeUpgrades>)upgrade1.getItem();
			map.put(upgrade.type, upgrade1.getCount());
		}
		if(ModItems.pipeUpgrades.getItems().contains(upgrade2.getItem())){
			ItemVariant<EnumPipeUpgrades> upgrade = (ItemVariant<EnumPipeUpgrades>)upgrade2.getItem();
			map.put(upgrade.type, upgrade2.getCount());
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public EnumPipeUpgrades getUpgradeType(EnumFacing facing, int upgradeSlot){
		ItemStack upgrade = getInternalInventory(facing).getStackInSlot(2 + upgradeSlot);
		if(ModItems.pipeUpgrades.getItems().contains(upgrade.getItem())){
			ItemVariant<EnumPipeUpgrades> upgradeItem = (ItemVariant<EnumPipeUpgrades>)upgrade.getItem();
			return upgradeItem.type;
		}
		return null;
	}

	public int getPriority(EnumFacing facing){
		return priorities[facing.getIndex()];
	}
	
	public void setPriority(EnumFacing facing, int value){
		priorities[facing.getIndex()] = value;
	}
	
	@Override
	public boolean openConnector(EntityPlayer player, EnumHand hand, EnumFacing side){
		if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
        {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;

            NetworkHooks.openGui(entityPlayerMP, new ConnectorGui(this, side), buf -> buf.writeBlockPos(pos).writeEnumValue(side));
            return true;
        }
		return false;
	}
	
	@Override
	public List<ItemStack> getDrops(){
		List<ItemStack> stacks = Lists.newArrayList();
		for(EnumFacing facing : EnumFacing.values()){
			InternalPipeInventory inv = getInternalInventory(facing);
			for(int i = 0; i < inv.getSizeInventory(); i++){
				ItemStack stack = inv.getStackInSlot(i);
				if(ItemStackTools.isValid(stack)){
					stacks.add(stack);
				}
			}
		}
		return stacks;
	}
	
	public class ConnectorGui implements IInteractionObject {
		public final EnumFacing facing;
		public final TileEntityPipeItem pipe;

		public ConnectorGui(TileEntityPipeItem pipe, EnumFacing facing) {
			this.pipe = pipe;
			this.facing = facing;
		}

		public ITextComponent getName() {
			return ModBlocks.pipeItem.getNameTextComponent();
		}

		public boolean hasCustomName() {
			return false;
		}

		@Nullable
		public ITextComponent getCustomName() {
			return null;
		}

		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new ContainerItemPipe(playerIn, pipe, facing);
		}

		public String getGuiID() {
			return GuiHandler.TILE_PIPE_CONNECTOR.toString();
		}
	}
	
	public static boolean passesFilter(ItemStack item, ItemStack filter){
		if(ItemStackTools.isNullStack(item) || item.getItem() == null || (ItemStackTools.isValid(filter) && filter.getItem() !=ModItems.pipeFilter))return false;
		if(ItemStackTools.isNullStack(filter))return true;	
		Map<ItemStack, FilterSettings> filteredList = Maps.newHashMap();
		buildFilterList(filter, filteredList);
		if(filteredList.isEmpty()){
			FilterSettings masterSettings = new FilterSettings(filter);
			return masterSettings.isBlacklist() ? true : false;
		}
		
		boolean matched = false;
		for(Entry<ItemStack, FilterSettings> filterData : filteredList.entrySet()){
			ItemStack filterStack = filterData.getKey();
			FilterSettings settings = filterData.getValue();
			if(ItemStackTools.isValid(filterStack)) {				
				if(item.getItem() == filterStack.getItem()){
					matched = true;
					if(settings.isDamage() && item.getDamage() != filterStack.getDamage()){
						matched = false;
					}
					else if(settings.isNBT()) {
						if(filterStack.getTag() == null || item.getTag() == null || !filterStack.getTag().equals(item.getTag())){
							matched = false;
						}
					}  
				}
				
				//Use tag data if the filter has that enabled
				if(settings.useTag() && !matched){
					if(ItemUtil.matchUsingTags(item, filterStack)){
						matched = true;
					}
				}
			}
			if(settings.isBlacklist()) {				
				if(matched)return false;
			}
			else {				
				if(!matched)return false;
			}
		}
		return true;
	}
	
	//TODO Look into capping depth of filters
	private static void buildFilterList(ItemStack filter, Map<ItemStack, FilterSettings> filterList){
		if(ItemNBTHelper.verifyExistance(filter, "FilterItems")){
			FilterSettings settings = new FilterSettings(filter);
			NonNullList<ItemStack> stacks = loadFilterStacks(filter);
			for(ItemStack stack : stacks){
				if(ItemStackTools.isValid(stack)){
					if(stack.getItem() == ModItems.pipeFilter){
						//Load that filter
						//Allows multi filter to filter more items
						buildFilterList(stack, filterList);
					} else {
						filterList.put(ItemUtil.copy(stack, 1), settings);
					}
				}
			}
		}
	}

	public static class FilterSettings {
		public static final String NBT_BLACKLIST = "Blacklist";
		public static final String NBT_DAMAGE_MATCH = "DamageMatch";
		public static final String NBT_NBT_MATCH = "NBTMatch";
		public static final String NBT_TAG_MATCH = "TagMatch";
		private boolean blacklist;
		private boolean damage;
		private boolean nbt;
		private boolean tag;
		
		public FilterSettings(ItemStack filter){
			this.blacklist = ItemNBTHelper.getBoolean(filter, NBT_BLACKLIST, true);
			this.damage = ItemNBTHelper.getBoolean(filter, NBT_DAMAGE_MATCH, false);
			this.nbt = ItemNBTHelper.getBoolean(filter, NBT_NBT_MATCH, false);
			this.tag = ItemNBTHelper.getBoolean(filter, NBT_TAG_MATCH, false);
		}
		
		public FilterSettings(boolean blacklist, boolean damage, boolean nbt, boolean tag){
			this.blacklist = blacklist;
			this.damage = damage;
			this.nbt = nbt;
			this.tag = tag;
		}
		
		public boolean isBlacklist() {
			return blacklist;
		}
		
		public void setBlackList(boolean value){
			this.blacklist = value;
		}
		
		public boolean isDamage() {
			return damage;
		}
		
		public void setIsDamage(boolean value){
			this.damage = value;
		}
		
		public boolean isNBT() {
			return nbt;
		}
		
		public void setIsNBT(boolean value){
			this.nbt = value;
		}
		
		public boolean useTag() {
			return tag;
		}
		
		public void setUseTag(boolean value){
			this.tag = value;
		}
		
		public void saveToItem(ItemStack stack){
			ItemNBTHelper.setBoolean(stack, NBT_BLACKLIST, blacklist);
			ItemNBTHelper.setBoolean(stack, NBT_DAMAGE_MATCH, damage);
			ItemNBTHelper.setBoolean(stack, NBT_NBT_MATCH, nbt);
			ItemNBTHelper.setBoolean(stack, NBT_TAG_MATCH, tag);
		}
	}
	
	public static NonNullList<ItemStack> loadFilterStacks(ItemStack filter) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(10, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(ItemNBTHelper.getCompound(filter).getCompound("FilterItems"), stacks);
		return stacks;
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client){
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("Priority")){
			EnumFacing facing = EnumFacing.byIndex(messageData.getInt("Facing"));
			setPriority(facing, messageData.getInt("Value"));
			if(getNetwork() !=null)((PipeNetworkItem)getNetwork()).updateSortOrder();
			serverDirty = true;
		}
	}

}
