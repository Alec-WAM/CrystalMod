package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;
import java.util.Map;

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
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.IItemHandler;

public class TileEntityPipeItem extends TileEntityPipeBase {
	//TODO Add Hopper Upgrade
	private Map<Direction, InternalPipeInventory> pipeItems;
	private int[] priorities;
	private boolean[] roundRobin;
	
	public TileEntityPipeItem() {
		super(ModBlocks.TILE_PIPE_ITEM);
		pipeItems = Maps.newHashMap();
		for(final Direction facing : Direction.values()){
			InternalPipeInventory inv = new InternalPipeInventory(facing, new StringTextComponent("Items"), 4) {
				@Override
				public void markDirty(int slot, ItemStack lastStack) {
					super.markDirty(slot, lastStack);
					internalInventoryChanged(facing, slot, lastStack);
				}
			};
			pipeItems.put(facing, inv);
		}
		priorities = new int[6];
		roundRobin = new boolean[6];
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		CompoundNBT itemData = new CompoundNBT();
		for(Direction facing : Direction.values()){
			ListNBT nbttaglist = new ListNBT();
			InternalPipeInventory internalInventory = getInternalInventory(facing);
			for(int s = 0; s < internalInventory.getSizeInventory(); s++) {
				ItemStack itemstack = internalInventory.getStackInSlot(s);
				if (!itemstack.isEmpty()) {
					CompoundNBT data = new CompoundNBT();
					CompoundNBT stackNBT = itemstack.write(new CompoundNBT());
					data.putInt("Slot", s);
					data.put("Stack", stackNBT);
					nbttaglist.add(data);
				}
			}
			itemData.put(facing.getName().toLowerCase(), nbttaglist);
		}

		nbt.put("Inventory", itemData);
		nbt.putIntArray("Priorities", priorities);
		byte[] rr = new byte[6];
		for(int i = 0; i < 6; i++){
			rr[i] = (byte)(roundRobin[i] ? 1 : 0);
		}
		nbt.putByteArray("RoundRobin", rr);
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		CompoundNBT connectionSettingData = nbt.getCompound("Inventory");
		for(Direction facing : Direction.values()){
			InternalPipeInventory internalInventory = getInternalInventory(facing);
			ListNBT nbttaglist = connectionSettingData.getList(facing.getName().toLowerCase(), 10);
			for(int s = 0; s < nbttaglist.size(); s++) {
				CompoundNBT data = nbttaglist.getCompound(s);
				int slot = data.getInt("Slot");
				ItemStack itemstack = ItemStack.read(data.getCompound("Stack"));
				if (!itemstack.isEmpty()) {
					internalInventory.setInventorySlotContents(slot, itemstack);
				}
			}
		}			
		
		if(nbt.contains("Priorities")){
			this.priorities = nbt.getIntArray("Priorities");
		} else {
			this.priorities = new int[6];
		}
		if(nbt.contains("RoundRobin")){
			byte[] rr = nbt.getByteArray("RoundRobin");
			for(int i = 0; i < 6; i++){
				roundRobin[i] = rr[i] == 1 ? true : false;
			}
		} else {
			roundRobin = new boolean[6];
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
	public boolean canConnectToExternal(Direction facing, boolean ignore){
		return getExternalInventory(facing) !=null;
	}

	@Override
	public void externalConnectionAdded(Direction direction) {
		super.externalConnectionAdded(direction);
		if(network != null && network instanceof PipeNetworkItem) {
			PipeNetworkItem net = (PipeNetworkItem)network;
			if(getExternalInventory(direction) !=null && net.getInventory(this, direction) == null){
				((PipeNetworkItem)network).inventoryAdded(this, direction);
			}
		}
	}

	@Override
	public void externalConnectionRemoved(Direction direction) {
		externalConnections.remove(direction);
		if(network != null && network instanceof PipeNetworkItem) {
			((PipeNetworkItem)network).inventoryRemoved(this, direction);
		}
	}

	public void internalInventoryChanged(Direction facing, int slot, ItemStack lastStack) {
		//Upgrades
		if(slot == 2 || slot == 3){
			boolean isExternal = false;
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
	public boolean onActivated(World world, PlayerEntity player, Hand hand, PipeHitData hitData) {
		ItemStack held = player.getHeldItem(hand);
		if(hitData !=null){
			if(hitData.part == null || hitData.part == PipePart.CENTER){
				Direction side = hitData.face;
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
	
	public boolean externalUpgradeInsert(ItemStack stack, Direction side, boolean server){
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
	
	public boolean canDoCobbleGen(Direction direction){
		NetworkPos pos = getNetworkPos().offset(direction);
		Map<EnumPipeUpgrades, Integer> upgrades = getUpgrades(direction);
		if(BlockUtil.isCobbleGen(getWorld(), pos.getBlockPos(), direction)){
			if(upgrades.containsKey(EnumPipeUpgrades.COBBLE)){
				return true;
			}
		}
		return false;
	}
	
	public IItemHandler getExternalInventory(Direction facing) {
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
	
	public InternalPipeInventory getInternalInventory(Direction facing){
		return pipeItems.get(facing);
	}
	
	public ItemStack getInFilter(Direction facing){
		InternalPipeInventory inv = getInternalInventory(facing);
		return inv.getStackInSlot(0);
	}
	
	public ItemStack getOutFilter(Direction facing){
		InternalPipeInventory inv = getInternalInventory(facing);
		return inv.getStackInSlot(1);
	}
	
	@SuppressWarnings("unchecked")
	public Map<EnumPipeUpgrades, Integer> getUpgrades(Direction facing){
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
	public EnumPipeUpgrades getUpgradeType(Direction facing, int upgradeSlot){
		ItemStack upgrade = getInternalInventory(facing).getStackInSlot(2 + upgradeSlot);
		if(ModItems.pipeUpgrades.getItems().contains(upgrade.getItem())){
			ItemVariant<EnumPipeUpgrades> upgradeItem = (ItemVariant<EnumPipeUpgrades>)upgrade.getItem();
			return upgradeItem.type;
		}
		return null;
	}

	public int getPriority(Direction facing){
		return priorities[facing.getIndex()];
	}
	
	public void setPriority(Direction facing, int value){
		priorities[facing.getIndex()] = value;
	}

	public boolean isRoundRobinEnabled(Direction facing) {
		return roundRobin[facing.getIndex()];
	}
	
	public void setRoundRobin(Direction facing, boolean value){
		roundRobin[facing.getIndex()] = value;
	}
	
	@Override
	public boolean openConnector(PlayerEntity player, Hand hand, Direction side){
		if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer))
        {
            ServerPlayerEntity entityPlayerMP = (ServerPlayerEntity) player;

            GuiHandler.openCustomGui(GuiHandler.TILE_PIPE_CONNECTOR, entityPlayerMP, new ConnectorGui(this, side), buf -> buf.writeBlockPos(pos).writeEnumValue(side));
            return true;
        }
		return false;
	}
	
	@Override
	public List<ItemStack> getDrops(){
		List<ItemStack> stacks = Lists.newArrayList();
		for(Direction facing : Direction.values()){
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
	
	public class ConnectorGui implements INamedContainerProvider {
		public final Direction facing;
		public final TileEntityPipeItem pipe;

		public ConnectorGui(TileEntityPipeItem pipe, Direction facing) {
			this.pipe = pipe;
			this.facing = facing;
		}

		@Override
		public ITextComponent getDisplayName() {
			return ModBlocks.pipeItem.getNameTextComponent();
		}

		@Override
		public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerIn) {
			return new ContainerItemPipe(i, playerIn, pipe, facing);
		}
	}
	
	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client){
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("Priority")){
			Direction facing = Direction.byIndex(messageData.getInt("Facing"));
			setPriority(facing, messageData.getInt("Value"));
			if(getNetwork() !=null)((PipeNetworkItem)getNetwork()).updateSortOrder();
			serverDirty = true;
		}
		if(messageId.equalsIgnoreCase("RoundRobin")){
			Direction facing = Direction.byIndex(messageData.getInt("Facing"));
			setRoundRobin(facing, messageData.getBoolean("Value"));
			serverDirty = true;
		}
	}

}
