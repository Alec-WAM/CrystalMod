package alec_wam.CrystalMod.tiles.machine.pump;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.compatibility.FluidConversion;
import alec_wam.CrystalMod.compatibility.FluidStackFixed;
import alec_wam.CrystalMod.compatibility.FluidTankFixed;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityPoweredInventory;
import alec_wam.CrystalMod.tiles.tank.TileEntityTank;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityFluidPump extends TileEntityPoweredInventory implements INamedContainerProvider {
	public int range;
	public BlockPos currentBlock;
	private int drainDelay;
	private Queue<BlockPos> columns = new LinkedList<BlockPos>();
	
	public boolean isRunning;
	private boolean wasRunning;
	public boolean isFinished;
	private int lastEnergyCost = -1;
	private boolean isFluidDirty;
	public int clientEnergyCost = -1;	

	public final FluidTankFixed tank;
	private final LazyOptional<IFluidHandler> fluidHolder;
	
	public TileEntityFluidPump() {
		super(ModBlocks.TILE_PUMP, "Pump", 1);
		holder = LazyOptional.of(() -> eStorage);
		this.tank = new FluidTankFixed(Fluid.BUCKET_VOLUME * TileEntityTank.TIER_BUCKETS[1]) {
			@Override
			public void onContentsChanged(){
				super.onContentsChanged();
				isFluidDirty = true;
			}
		};
		tank.setCanFill(false);
		this.fluidHolder = LazyOptional.of(() -> tank);
	}

	@Override
	public void setupEnergy() {
		eStorage = new CEnergyStorage(10000, 80) {
			@Override
			public boolean canExtract(){
				return false;
			}
		};
	}
	
	@Override
	public boolean canExtract(int slot, int amount) {
		return false;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 0){
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
		return true;
	}
	
	@Override 
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		if(this.currentBlock !=null){
			nbt.put("CurrentPos", NBTUtil.writeBlockPos(currentBlock));
		}
		nbt.putInt("Range", range);
		CompoundNBT tankNBT = new CompoundNBT();
		tank.writeToNBT(tankNBT);
		nbt.put("Tank", tankNBT);
	}
	
	@Override 
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		if(nbt.contains("CurrentPos")){
			this.currentBlock = NBTUtil.readBlockPos(nbt.getCompound("CurrentPos"));
		} else {
			this.currentBlock = null;
		}
		this.range = nbt.getInt("Range");
		this.tank.readFromNBT(nbt.getCompound("Tank"));
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
			
			if(isFluidDirty && this.shouldDoWorkThisTick(10)){
				CompoundNBT nbt = new CompoundNBT();
				tank.writeToNBT(nbt);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateTank", nbt), this);
				isFluidDirty = false;
			}
			
			if(powered && !isFinished){
				if(this.currentBlock == null && columns.isEmpty() && range == 0){
					System.out.println("Starting");
					buildColumns();		
					this.currentBlock = this.columns.poll();
				} else {
					drainBlock();
				}	
				isRunning = true;				
			} else {
				isRunning = false;
			}
			
			if(wasRunning != isRunning){
				sendRunningPacket();
				this.wasRunning = isRunning;
			}
			
			//Push to external tanks
			if(shouldDoWorkThisTick(10)){
				BlockPos otherPos = getPos().offset(Direction.UP);
				LazyOptional<IFluidHandler> handler = FluidUtil.getFluidHandler(getWorld(), otherPos, Direction.DOWN);
				if(handler.isPresent()){
					IFluidHandler otherTank = handler.orElse(null);
					if(otherTank !=null){
						FluidStack stack = tank.drain(250 + (250 * getEfficencyLevel()), false);
						int amt = otherTank.fill(stack, true);
						if(amt > 0){
							tank.drain(amt, true);
						}
					}
				}
			}
		}
	}
	
	public void reset(){
		this.currentBlock = null;
		this.columns.clear();
		this.isFinished = false;
		this.range = 0;
		sendRunningPacket();
	}
	
	public int getRadius() {
		return 3;//16;
	}
	
	public int getEfficencyLevel(){
		ItemStack stack = getStackInSlot(0);
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
	
	@Override
	public void onItemChanged(int slot){
		super.onItemChanged(slot);
	}
	
	public static final int BASE_POWER_PER_BLOCK = 10;
	public static final int EFFICENCY_POWER_COST = 5;
	public int getEnergyCost() {
		int cost = BASE_POWER_PER_BLOCK;
		int effi = getEfficencyLevel();
		cost+= effi * EFFICENCY_POWER_COST;
		return cost;
	}
	
	@SuppressWarnings("deprecation")
	public void drainBlock(){
		if(tank.getFluidAmount() > tank.getCapacity() - Fluid.BUCKET_VOLUME){
			//Full
			return;
		}
		
		if(drainDelay > 0){
			drainDelay--;
			return;
		}
		
		if(this.currentBlock !=null){
			IFluidState stateAtPos = getWorld().getFluidState(currentBlock);
			if(stateAtPos == null){
				BlockState state = getWorld().getBlockState(currentBlock);
				stateAtPos = state.getBlock().getFluidState(state);
			}
			
			if(stateAtPos == null){
				moveToNextBlock();				
				return;
			}
			
			boolean isValid = false;
			net.minecraftforge.fluids.Fluid converted = FluidConversion.getFluidFromState(stateAtPos);
			if(converted !=null){
				if(stateAtPos.isSource()){
					if(tank.getFluid() == null || tank.getFluid().getFluid() == converted){
						isValid = true;
					}
				}
			} 
			if(!isValid){
				moveToNextBlock();
				return;
			}
			
			final int energyCost = getEnergyCost();
			if(getEnergyStorage().getCEnergyStored() >= energyCost){
				FluidStackFixed drainStack = new FluidStackFixed(converted, 1000);
				
				if(tank.fillInternal(drainStack, false) == 1000){
					tank.fillInternal(drainStack, true);
					BlockState state = getWorld().getBlockState(currentBlock);
					
					if(!(state.getBlock() instanceof FlowingFluidBlock) && state.getBlock() instanceof IBucketPickupHandler){
						//Drain Watterlogged Blocks
						IBucketPickupHandler handler = (IBucketPickupHandler)state.getBlock();
						handler.pickupFluid(getWorld(), currentBlock, state);
					} else {
						getWorld().setBlockState(currentBlock, Blocks.STONE.getDefaultState(), 3);
					}
					
					this.eStorage.modifyEnergyStored(-energyCost);
					int delay = 25 - (5 * getEfficencyLevel());
					if(delay > 0)drainDelay = delay;
					moveToNextBlock();
				}
			}
		}
	}
	
	//Creates a Ring around the pump at that range (Credit goes to Ranged Pumps)
	private void buildColumns(){
		this.columns.clear();
		if(range == 0) {
			this.columns.add(getPos().down());
		} else {
			int offsetRange = range - 1; 
			
			int hl = 3 + 2 * offsetRange;
	        int vl = 1 + 2 * offsetRange;

	        for (int i = 0; i < hl; ++i) {
	        	columns.add(getPos().add(-offsetRange - 1 + i, -1, -offsetRange - 1));
	        }
	        for (int i = 0; i < vl; ++i) {
	        	columns.add(getPos().add(-offsetRange - 1 + vl + 1, -1, -offsetRange - 1 + i + 1));
	        }
	        for (int i = 0; i < hl; ++i) {
	        	columns.add(getPos().add(-offsetRange - 1 + hl - i - 1, -1, -offsetRange - 1 + hl - 1));
	        }
	        for (int i = 0; i < vl; ++i) {
	        	columns.add(getPos().add(-offsetRange - 1, -1, -offsetRange - 1 + vl - i));
	        }
		}
	}
	
	public void moveToNextBlock() {
		if(currentBlock !=null){
			if(currentBlock.getY() > 0){
				currentBlock = currentBlock.down();
				return;
			} else {
				currentBlock = null;
			}
		} 
		if(!this.columns.isEmpty()){
			this.currentBlock = this.columns.poll();
		} else {
			if(range < getRadius()){
				range++;
				buildColumns();		
				this.currentBlock = this.columns.poll();
			} else {
				isFinished = true;
				sendRunningPacket();
			}	
		}
	}
	
	public void sendRunningPacket(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("Running", isRunning);
		nbt.putBoolean("Finished", isFinished);
		CrystalModNetwork.sendToAllAround(new PacketTileMessage(this.getPos(), "UpdateRunning", nbt), this);
	}
	
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
		return new ContainerFluidPump(windowId, player, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return ModBlocks.miner.getNameTextComponent();
	}
	
	@Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            return fluidHolder.cast();
        }
        return super.getCapability(cap, side);
    }
	
	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("UpdateCurrentBlock")){
			this.currentBlock = NBTUtil.readBlockPos(messageData);
		}
		if(messageId.equalsIgnoreCase("UpdateRunning")){
			this.isRunning = messageData.getBoolean("Running");
			this.isFinished = messageData.getBoolean("Finished");
		}
		if(messageId.equalsIgnoreCase("UpdateEnergyCost")){
			this.clientEnergyCost = messageData.getInt("Cost");
		}
		if(messageId.equalsIgnoreCase("UpdateTank")){
			tank.readFromNBT(messageData);
		}
		if(messageId.equalsIgnoreCase("Reset")){
			reset();
		}
	}

}
