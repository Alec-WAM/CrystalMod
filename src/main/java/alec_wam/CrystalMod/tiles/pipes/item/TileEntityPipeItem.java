package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.pipes.EnumPipeUpgrades;
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
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
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

	private Map<EnumFacing, InventoryBasic> pipeItems;
	private int[] priorities;
	
	public TileEntityPipeItem() {
		super(ModBlocks.TILE_PIPE_ITEM);
		pipeItems = Maps.newHashMap();
		for(EnumFacing facing : EnumFacing.values()){
			pipeItems.put(facing, new InventoryBasic(new TextComponentString("Items"), 2));
		}
		priorities = new int[6];
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagCompound itemData = new NBTTagCompound();
		for(EnumFacing facing : EnumFacing.values()){
			NBTTagList nbttaglist = new NBTTagList();
			InventoryBasic internalInventory = getInternalInventory(facing);
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
			InventoryBasic internalInventory = getInternalInventory(facing);
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
	
	public boolean canDoCobbleGen(EnumFacing direction){
		NetworkPos pos = getNetworkPos().offset(direction);
		ItemStack upgrade = getUpgrade(direction);
		if(BlockUtil.isCobbleGen(getWorld(), pos.getBlockPos(), direction)){
			if(upgrade.getItem() == ModItems.pipeUpgrades.getItem(EnumPipeUpgrades.COBBLE)){
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
	
	public InventoryBasic getInternalInventory(EnumFacing facing){
		return pipeItems.get(facing);
	}
	
	public ItemStack getFilter(EnumFacing facing){
		InventoryBasic inv = getInternalInventory(facing);
		return inv.getStackInSlot(0);
	}
	
	public ItemStack getUpgrade(EnumFacing facing){
		InventoryBasic inv = getInternalInventory(facing);
		return inv.getStackInSlot(1);
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
			InventoryBasic inv = getInternalInventory(facing);
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
		List<ItemStack> filteredList = new ArrayList<ItemStack>();
		buildFilterList(filter, filteredList);
		boolean black = ItemNBTHelper.getBoolean(filter, "BlackList", false);
		
		
		if(filteredList.isEmpty()){
			return black ? false : true;
		}

		boolean meta = ItemNBTHelper.getBoolean(filter, "MetaMatch", false);
		boolean matchNBT = ItemNBTHelper.getBoolean(filter, "NBTMatch", false);
		boolean matched = false;
		for(ItemStack filterStack : filteredList){
			if(ItemStackTools.isValid(filterStack) && item.getItem() == filterStack.getItem()) {
				matched = true;
				if(meta && item.getDamage() != filterStack.getDamage()) {
					matched = false;
				} else if(matchNBT) {
					if(filterStack.getTag() == null || item.getTag() == null || !filterStack.getTag().equals(item.getTag()))
						matched = false;
				}   
			}
			if(matched) {
				break;
			}
		}
		return black ? matched == false : matched;
	}
	
	//TODO Look into capping depth of filters
	private static void buildFilterList(ItemStack filter, List<ItemStack> filterList){
		if(ItemNBTHelper.verifyExistance(filter, "FilterItems")){
			NonNullList<ItemStack> stacks = loadFilterStacks(filter);
			for(ItemStack stack : stacks){
				if(ItemStackTools.isValid(stack)){
					if(stack.getItem() == ModItems.pipeFilter){
						//Load that filter
						//Allows multi filter to filter more items
						buildFilterList(stack, filterList);
					} else {
						filterList.add(ItemUtil.copy(stack, 1));
					}
				}
			}
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
