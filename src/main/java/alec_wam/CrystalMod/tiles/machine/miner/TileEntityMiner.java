package alec_wam.CrystalMod.tiles.machine.miner;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityPoweredInventory;
import alec_wam.CrystalMod.tiles.pipes.item.ItemPipeFilter;
import alec_wam.CrystalMod.tiles.pipes.item.ItemPipeFilter.FilterSettings;
import alec_wam.CrystalMod.util.FakePlayerUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityMiner extends TileEntityPoweredInventory implements INamedContainerProvider {
	public static ItemStack SILK_PICK;
	public static ItemStack[] FORTUNE_PICK;
	static {
		SILK_PICK = new ItemStack(Items.DIAMOND_PICKAXE);
		Map<Enchantment, Integer> map = Maps.newLinkedHashMap();
		map.put(Enchantments.SILK_TOUCH, Integer.valueOf(1));
		EnchantmentHelper.setEnchantments(map, SILK_PICK);
		FORTUNE_PICK = new ItemStack[3];
		for(int i = 0; i < 3; i++){
			FORTUNE_PICK[i] = new ItemStack(Items.DIAMOND_PICKAXE);
			map = Maps.newLinkedHashMap();
			map.put(Enchantments.FORTUNE, Integer.valueOf(1));
			EnchantmentHelper.setEnchantments(map, FORTUNE_PICK[i]);
		}
	}
	public int yLevel;
	public BlockPos cornerBlock;
	public BlockPos currentBlock;
	
	public boolean isRunning;
	private boolean wasRunning;
	public boolean isFinished;
	public boolean isAtBedrock;
	
	public float digProgess;
	
	public boolean onlyOre;
	private Map<ItemStack, FilterSettings> cachedFilteredList = Maps.newHashMap();
	
	private int lastEnergyCost = -1;
	public int clientEnergyCost = -1;
	
	public TileEntityMiner() {
		super(ModBlocks.TILE_MINER, "Miner", 12);
	}

	@Override
	public void setupEnergy() {
		eStorage = new CEnergyStorage(100000, 160) {
			@Override
			public boolean canExtract(){
				return false;
			}
		};
		holder = LazyOptional.of(() -> eStorage);
	}
	
	@Override
	public boolean canExtract(int slot, int amount) {
		return slot < 9;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack) {
		return slot < 9;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 9){
			if(stack.getItem() == Items.ENCHANTED_BOOK){
				ListNBT enchantData = EnchantedBookItem.getEnchantments(stack);
				if(enchantData.size() == 1){
					CompoundNBT compoundnbt = enchantData.getCompound(0);
			        ResourceLocation id = ResourceLocation.tryCreate(compoundnbt.getString("id"));
			        if(id !=null && id.equals(Enchantments.SILK_TOUCH.getRegistryName())){
			        	return true;
			        }
			        if(id !=null && id.equals(Enchantments.FORTUNE.getRegistryName())){
			        	return true;
			        }
				}
			}
			return false;
		}
		if(index == 10){
			if(stack.getItem() == Items.ENCHANTED_BOOK){
				ListNBT enchantData = EnchantedBookItem.getEnchantments(stack);
				if(enchantData.size() == 1){
					CompoundNBT compoundnbt = enchantData.getCompound(0);
			        ResourceLocation id = ResourceLocation.tryCreate(compoundnbt.getString("id"));
			        if(id !=null && id.equals(Enchantments.EFFICIENCY.getRegistryName())){
			        	return true;
			        }
				}
			}
			return false;
		}
		if(index == 11){
			return stack.getItem() == ModItems.itemFilter;
		}
		return true;
	}
	
	@Override 
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		if(this.currentBlock !=null){
			nbt.put("MiningPos", NBTUtil.writeBlockPos(currentBlock));
		}
		if(this.cornerBlock !=null){
			nbt.put("CornerPos", NBTUtil.writeBlockPos(cornerBlock));
		}
		nbt.putBoolean("isFinished", isFinished);
		nbt.putBoolean("isAtBedrock", isAtBedrock);
		nbt.putBoolean("OnlyOre", onlyOre);
	}
	
	@Override 
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		if(nbt.contains("MiningPos")){
			this.currentBlock = NBTUtil.readBlockPos(nbt.getCompound("MiningPos"));
		} else {
			this.currentBlock = null;
		}
		if(nbt.contains("CornerPos")){
			this.cornerBlock = NBTUtil.readBlockPos(nbt.getCompound("CornerPos"));
		} else {
			this.cornerBlock = null;
		}
		this.isFinished = nbt.getBoolean("isFinished");
		this.isAtBedrock = nbt.getBoolean("isAtBedrock");
		this.onlyOre = nbt.getBoolean("OnlyOre");
		lastEnergyCost = -1;
	}
	
	@Override
	public void tick(){
		super.tick();
		boolean powered = getWorld().isBlockPowered(getPos());
		if(!getWorld().isRemote){
			//System.out.println(""+lastEnergyCost);
			if((lastEnergyCost == -1 || lastEnergyCost != getEnergyCost())){
				int cost = getEnergyCost();
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Cost", cost);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateEnergyCost", nbt), this);
				this.lastEnergyCost = cost;
			}
			
			if(!powered){
				if(digProgess > 0.0f){
					FakePlayer player = FakePlayerUtil.getPlayer((ServerWorld)getWorld());
					getWorld().sendBlockBreakProgress(player.getEntityId(), currentBlock, -1);
					digProgess = 0.0f;
				}
			}
			if(powered && !isFinished){
				if(this.currentBlock == null){
					if(cornerBlock == null){
						this.cornerBlock = getPos().down().add(-getRadius(), 0, -getRadius());
					}
					this.currentBlock = this.cornerBlock;
				} else {
					mineBlock();
				}	
				isRunning = true;				
			} else {
				isRunning = false;
			}
			
			if(wasRunning != isRunning){
				sendRunningPacket();
				this.wasRunning = isRunning;
			}
			
			//Push to external inventories
			if(shouldDoWorkThisTick(10) && !isEmpty()){
				BlockPos otherPos = getPos().offset(Direction.UP);
				IItemHandler handler = ItemUtil.getExternalItemHandler(getWorld(), otherPos, Direction.DOWN);
				if(handler !=null){
					item : for(int i = 0; i < 9; i++){
						ItemStack stack = getStackInSlot(i);
						if(ItemStackTools.isValid(stack)){
							final int amt = Math.min(stack.getCount(), 4 + getEfficencyLevel());
							ItemStack insertStack = ItemUtil.copy(stack, amt);
							ItemStack inserted = ItemHandlerHelper.insertItem(handler, insertStack, false);
							int rem = amt - inserted.getCount();
							stack.shrink(rem);
							break item;
						}
					}
				}
			}
		}
	}
	
	public void reset(){
		if(digProgess > 0.0f){
			FakePlayer player = FakePlayerUtil.getPlayer((ServerWorld)getWorld());
			getWorld().sendBlockBreakProgress(player.getEntityId(), currentBlock, -1);
			digProgess = 0.0f;
		}
		this.cornerBlock = getPos().down().add(-getRadius(), 0, -getRadius());
		this.currentBlock = cornerBlock;
		this.yLevel = 0;
		this.isFinished = false;
		this.isAtBedrock = false;
		sendRunningPacket();
	}
	
	public int getRadius() {
		return 5;
	}
	
	public int getEfficencyLevel(){
		ItemStack stack = getStackInSlot(10);
		if(stack.getItem() == Items.ENCHANTED_BOOK){
			ListNBT enchantData = EnchantedBookItem.getEnchantments(stack);
			if(enchantData.size() == 1){
				CompoundNBT compoundnbt = enchantData.getCompound(0);
		        ResourceLocation id = ResourceLocation.tryCreate(compoundnbt.getString("id"));
		        if(id !=null && id.equals(Enchantments.EFFICIENCY.getRegistryName())){
		        	//Cap at 5
					return Math.min(compoundnbt.getInt("lvl"), 5);
		        }
			}
		}
		return 0;
	}
	
	public boolean hasSilkTouch(){
		ItemStack stack = getStackInSlot(9);
		if(stack.getItem() == Items.ENCHANTED_BOOK){
			ListNBT enchantData = EnchantedBookItem.getEnchantments(stack);
			if(enchantData.size() == 1){
				CompoundNBT compoundnbt = enchantData.getCompound(0);
		        ResourceLocation id = ResourceLocation.tryCreate(compoundnbt.getString("id"));
		        if(id !=null && id.equals(Enchantments.SILK_TOUCH.getRegistryName())){
		        	return true;
		        }
			}
		}
		return false;
	}
	
	public int getFortuneLevel(){
		ItemStack stack = getStackInSlot(9);
		if(stack.getItem() == Items.ENCHANTED_BOOK){
			ListNBT enchantData = EnchantedBookItem.getEnchantments(stack);
			if(enchantData.size() == 1){
				CompoundNBT compoundnbt = enchantData.getCompound(0);
		        ResourceLocation id = ResourceLocation.tryCreate(compoundnbt.getString("id"));
		        if(id !=null && id.equals(Enchantments.FORTUNE.getRegistryName())){
		        	return Math.min(compoundnbt.getInt("lvl"), 3);
		        }
			}
		}
		return 0;
	}
	
	public boolean passesFilter(BlockState state, BlockPos pos){
		ItemStack stack = getStackInSlot(11);
		if(ItemStackTools.isValid(stack) && !cachedFilteredList.isEmpty()){
			BlockRayTraceResult ray = new BlockRayTraceResult(Vec3d.ZERO, Direction.UP, pos, true);
			ItemStack blockStack = state.getPickBlock(ray, getWorld(), pos, FakePlayerUtil.getPlayer((ServerWorld)getWorld()));
			return ItemPipeFilter.passesFilter(blockStack, cachedFilteredList);
		}
		return true;
	}
	
	@Override
	public void onItemChanged(int slot){
		if(slot == 11){
			ItemStack stack = getStackInSlot(11);
			cachedFilteredList = Maps.newHashMap();
			if(ItemStackTools.isValid(stack)){
				ItemPipeFilter.buildFilterList(stack, cachedFilteredList);
			}
		}
	}
	
	public static final int BASE_POWER_PER_BLOCK = 10;
	public static final int EFFICENCY_POWER_COST = 5;
	public static final int FORTUNE_POWER_COST = 20;
	public static final int SILK_TOUCH_POWER_COST = 10;
	public int getEnergyCost() {
		int cost = BASE_POWER_PER_BLOCK;
		int effi = getEfficencyLevel();
		cost+= effi * EFFICENCY_POWER_COST;
		int fort = getFortuneLevel();
		cost+= fort * FORTUNE_POWER_COST;
		if(hasSilkTouch()){
			cost += SILK_TOUCH_POWER_COST;
		}
		if(onlyOre){
			cost += 10;
		}
		return cost;
	}
	
	public void mineBlock(){
		if(this.currentBlock !=null){
			FakePlayer player = FakePlayerUtil.getPlayer((ServerWorld)getWorld());
			BlockState stateAtPos = getWorld().getBlockState(currentBlock);
			Block block = stateAtPos.getBlock();
			boolean isValid = false;
			//TODO Create Mining Level Upgrades (or use pick item)
			if(onlyOre) {
				if(Tags.Blocks.ORES.contains(block)){
					isValid = passesFilter(stateAtPos, currentBlock);
				}
			} else {
				if(stateAtPos.getBlockHardness(getWorld(), currentBlock) >= 0.0F && !(block instanceof IFluidBlock) && !block.isAir(stateAtPos, getWorld(), currentBlock)) {
					TileEntity tile = getWorld().getTileEntity(currentBlock);
					if(tile == null){
						isValid = passesFilter(stateAtPos, currentBlock);
					}
				}
			}
			if(!isValid){
				moveToNextBlock();
				if(digProgess > 0.0f){
					getWorld().sendBlockBreakProgress(player.getEntityId(), currentBlock, -1);
					digProgess = 0.0f;
				}
				return;
			}
			
			final int energyCost = getEnergyCost();
			if(getEnergyStorage().getCEnergyStored() >= energyCost){
				BlockState currentState = getWorld().getBlockState(currentBlock);
				if(digProgess < 1.0F) {
					float hardness = currentState.getBlockHardness(getWorld(), currentBlock);
					float speed = ItemTier.DIAMOND.getEfficiency(); //Diamond Speed = 8.0
					float maxSpeedUpgrade = 7.0f;
					int effi = getEfficencyLevel();
					float upgrade = effi > 0 ? effi * (maxSpeedUpgrade / 5.0f) : 1.0f;					
					digProgess += (speed / hardness / 30) * upgrade;
					getWorld().sendBlockBreakProgress(player.getEntityId(), currentBlock, (int)(digProgess * 10.0F) - 1);
				} else {
					digProgess = 0.0F;
					getWorld().sendBlockBreakProgress(player.getEntityId(), currentBlock, -1);
					//Break block
					List<ItemStack> drops;
					if(hasSilkTouch()){
						drops = Block.getDrops(currentState, (ServerWorld)getWorld(), currentBlock, null, player, SILK_PICK);
					} else if(getFortuneLevel() > 0){
						drops = Block.getDrops(currentState, (ServerWorld)getWorld(), currentBlock, null, player, FORTUNE_PICK[getFortuneLevel() - 1]);
					} else {
						drops = Block.getDrops(currentState, (ServerWorld)getWorld(), currentBlock, null);
					}
					for(ItemStack stack : drops){
						ItemStack remaining = stack;
						BlockPos otherPos = getPos().offset(Direction.UP);
						IItemHandler otherHandler = ItemUtil.getExternalItemHandler(getWorld(), otherPos, Direction.DOWN);
						//Push to external first
						if(otherHandler !=null){
							ItemStack insert = ItemHandlerHelper.insertItem(otherHandler, remaining, false);
							int rem = remaining.getCount() - insert.getCount();
							remaining.shrink(rem);
						}
						//Then try internal
						if(ItemStackTools.isValid(remaining)){
							ItemStack insert = ItemHandlerHelper.insertItem(handler, remaining, false);
							int rem = remaining.getCount() - insert.getCount();
							remaining.shrink(rem);
						}
						//Finally pop into the world
						if(ItemStackTools.isValid(remaining)){
							ItemUtil.dropItemOnSide(getWorld(), getPos(), remaining, Direction.UP);
						}
					}
					this.eStorage.modifyEnergyStored(-energyCost);
					//Break Particles
					getWorld().removeBlock(currentBlock, false);
			        getWorld().playEvent(null, 2001, currentBlock, Block.getStateId(currentState));
					moveToNextBlock();
				}
			}
		}
	}
	
	public void moveToNextBlock() {
		boolean goDeeper = yLevel < 1;
		BlockPos currentDepth = cornerBlock.down(yLevel);
		if(currentDepth.getY() > 0){
			int nextX = currentBlock.getX() + 1;
			int nextZ = currentBlock.getZ();
			int nextY = currentDepth.getY();
			if(nextX - cornerBlock.getX() > getRadius() * 2){
				nextX = cornerBlock.getX();
				nextZ++;
				if(nextZ - cornerBlock.getZ() > getRadius() * 2){
					nextZ = currentBlock.getZ();
					if(goDeeper){
						nextY++;
						yLevel++;
					} else {
						isFinished = true;
						sendRunningPacket();
						return;
					}
				}
			}
			this.currentBlock = new BlockPos(nextX, nextY, nextZ);
		} else {
			isAtBedrock = true;
			isFinished = true;
			sendRunningPacket();
		}		
	}
	
	public void sendRunningPacket(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("Running", isRunning);
		nbt.putBoolean("Finished", isFinished);
		nbt.putBoolean("Bedrock", isAtBedrock);
		CrystalModNetwork.sendToAllAround(new PacketTileMessage(this.getPos(), "UpdateRunning", nbt), this);
	}
	
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
		return new ContainerMiner(windowId, player, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return ModBlocks.miner.getNameTextComponent();
	}
	
	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("UpdateCurrentBlock")){
			this.currentBlock = NBTUtil.readBlockPos(messageData);
			this.digProgess = messageData.getFloat("Dig");
		}
		if(messageId.equalsIgnoreCase("UpdateRunning")){
			this.isRunning = messageData.getBoolean("Running");
			this.isFinished = messageData.getBoolean("Finished");
			this.isAtBedrock = messageData.getBoolean("Bedrock");
		}
		if(messageId.equalsIgnoreCase("UpdateEnergyCost")){
			this.clientEnergyCost = messageData.getInt("Cost");
			System.out.println("Client Cost:"+ this.clientEnergyCost);
		}
		if(messageId.equalsIgnoreCase("Reset")){
			reset();
		}
	}

}
