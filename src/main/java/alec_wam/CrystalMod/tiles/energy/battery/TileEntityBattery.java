package alec_wam.CrystalMod.tiles.energy.battery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityIOSides;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileEntityBattery extends TileEntityIOSides implements INBTDrop, IInteractionObject, ISidedInventory {
	public static final int[] MAX_IO = { 80, 400, 2000, 10000, 50000, 50000};
	public static final int[] MAX_ENERGY = new int[]{500000, 2500000, 10000000, 25000000, 50000000, 100000000};
	
	private int lastEnergyAmount;
	public CEnergyStorage energyStorage;
	public int sendAmount;
	public int receiveAmount; 
	private int tier;
	@SuppressWarnings("unchecked")
	private final LazyOptional<ICEnergyStorage>[] holders = new LazyOptional[6];
	public NonNullList<ItemStack> inventory;
    
	public TileEntityBattery() {
		super(ModBlocks.TILE_BATTERY);
		this.energyStorage = new CEnergyStorage(MAX_ENERGY[tier], MAX_IO[tier]);
		for(EnumFacing facing : EnumFacing.values()){
    		holders[facing.getIndex()] = LazyOptional.of(() -> new BatteryEnergyStorage(this, facing));
    	}
		this.inventory = NonNullList.<ItemStack>withSize(2, ItemStackTools.getEmptyStack());
	}
	
	public TileEntityBattery(EnumCrystalColorSpecialWithCreative type) {
		super(ModBlocks.TILE_BATTERY);
		this.tier = type.ordinal();
		this.energyStorage = new CEnergyStorage(MAX_ENERGY[tier], MAX_IO[tier]);
		for(EnumFacing facing : EnumFacing.values()){
    		holders[facing.getIndex()] = LazyOptional.of(() -> new BatteryEnergyStorage(this, facing));
    	}
    	this.sendAmount = MAX_IO[tier];
    	this.receiveAmount = MAX_IO[tier];
		this.inventory = NonNullList.<ItemStack>withSize(2, ItemStackTools.getEmptyStack());
	}

	public int getTier() {
		return tier;
	}
    
	public EnumFacing getFacing(){
		return getBlockState().get(BlockBattery.FACING);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInt("Tier", tier);
		nbt.setInt("Send", sendAmount);
		nbt.setInt("Receive", receiveAmount);
		this.energyStorage.writeToNBT(nbt);
		ItemStackHelper.saveAllItems(nbt, inventory);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		tier = nbt.getInt("Tier");
		energyStorage = new CEnergyStorage(MAX_ENERGY[tier], MAX_IO[tier]);
		if(nbt.hasKey("Send")){
			this.sendAmount = Math.min(MAX_IO[tier], nbt.getInt("Send"));
		} else {
			this.sendAmount = MAX_IO[tier];
		}
		if(nbt.hasKey("Receive")){
			this.receiveAmount = Math.min(MAX_IO[tier], nbt.getInt("Receive"));
		}else{
			this.receiveAmount = MAX_IO[tier];
		}
		this.energyStorage.readFromNBT(nbt);
		ItemStackHelper.loadAllItems(nbt, inventory);
		updateAfterLoad();
	}

	public static final String NBT_DATA = "BatteryData";
	@Override
	public void writeToItemNBT(ItemStack stack) {
		NBTTagCompound nbt = new NBTTagCompound();
	    this.energyStorage.writeToNBT(nbt);
	    nbt.setInt("Send", sendAmount);
	    nbt.setInt("Receive", receiveAmount);
	    for(EnumFacing face : EnumFacing.values()){
			nbt.setByte("io."+face.name().toLowerCase(), (byte)getIO(face).ordinal());
		}
		ItemStackHelper.saveAllItems(nbt, inventory);
	    ItemNBTHelper.getCompound(stack).setTag(NBT_DATA, nbt);
	}
	
	@Override
	public void readFromItemNBT(ItemStack stack){
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompound(NBT_DATA);
		if(nbt.hasKey("Send")){
			this.sendAmount = Math.min(MAX_IO[tier], nbt.getInt("Send"));
		} else {
			this.sendAmount = MAX_IO[tier];
		}
		if(nbt.hasKey("Receive")){
			this.receiveAmount = Math.min(MAX_IO[tier], nbt.getInt("Receive"));
		}else{
			this.receiveAmount = MAX_IO[tier];
		}
		this.energyStorage.readFromNBT(nbt);
		for(EnumFacing face : EnumFacing.values()){
			String nbtT = "io."+face.name().toLowerCase();
			this.ioMap.put(face, IOType.values()[nbt.getByte(nbtT)]);
		}
		ItemStackHelper.loadAllItems(nbt, inventory);
	}
	
	public int getScaledEnergyStored(int paramInt)
	{
		return this.energyStorage.getCEnergyStored() * paramInt / this.energyStorage.getMaxCEnergyStored();
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("UpdateEnergy")){
			energyStorage.setEnergyStored(messageData.getInt("Energy"));
		}
		if(messageId.equalsIgnoreCase("UpdateSend")){
			this.sendAmount = messageData.getInt("Amount");
		}
		if(messageId.equalsIgnoreCase("UpdateReceive")){
			this.receiveAmount = messageData.getInt("Amount");
		}
	}
	
	public int getEnergySend(){
		return Math.min(MAX_IO[tier], sendAmount);
	}
	
	public int getEnergyReceive(){
		return Math.min(MAX_IO[tier], receiveAmount);
	}
	
	@Override
	public void tick(){
		super.tick();
		if(!getWorld().isRemote){
			boolean powerChanged = energyStorage.getCEnergyStored() != lastEnergyAmount && shouldDoWorkThisTick(10);
			if(powerChanged){
				lastEnergyAmount = energyStorage.getCEnergyStored();
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Energy", this.energyStorage.getCEnergyStored());
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateEnergy", nbt), this);
			}
			
			//External Blocks
			for(EnumFacing face : EnumFacing.values()){
				EnumFacing fix = fixFace(face);
				IOType io = getIO(fix);
				if(io == IOType.OUT){
					TileEntity tile = this.getWorld().getTileEntity(getPos().offset(face));
					if(tile !=null){
						LazyOptional<ICEnergyStorage> handler = tile.getCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite());
						if(handler.isPresent()){
							ICEnergyStorage rec = handler.orElse(null);
							boolean creative = isCreative();
							int drain = !rec.canReceive() ? 0 : rec.fillCEnergy(creative ? getEnergySend() : Math.min(getEnergySend(), energyStorage.getCEnergyStored()), false);
							if(!creative){
								this.energyStorage.modifyEnergyStored(-drain);
								if(drain > 0){
									this.markDirty();
								}
							}
						}
					}
				}
			}
			
			ItemStack stackToCharge = getStackInSlot(0);
			if(ItemStackTools.isValid(stackToCharge)){
				LazyOptional<ICEnergyStorage> handler = stackToCharge.getCapability(CapabilityCrystalEnergy.CENERGY, null);
				if(handler.isPresent()){
					ICEnergyStorage rec = handler.orElse(null);
					
					boolean creative = isCreative();
					int drain = !rec.canReceive() ? 0 : rec.fillCEnergy(creative ? getEnergySend() : Math.min(getEnergySend(), energyStorage.getCEnergyStored()), false);
					if(!creative){
						this.energyStorage.modifyEnergyStored(-drain);
						if(drain > 0){
							this.markDirty();
						}
					}
					
					if(rec.getCEnergyStored() >= rec.getMaxCEnergyStored()){
						this.setInventorySlotContents(1, stackToCharge);
						this.setInventorySlotContents(0, ItemStackTools.getEmptyStack());
					}
				}
			}
		}
	}
	
	public boolean isCreative(){
		return this.tier == EnumCrystalColorSpecialWithCreative.CREATIVE.ordinal();
	}

	private class BatteryEnergyStorage implements ICEnergyStorage {
		private EnumFacing facing;
		private TileEntityBattery battery;
		public BatteryEnergyStorage(TileEntityBattery battery, EnumFacing facing){
			this.facing = facing;
			this.battery = battery;
		}
		@Override
		public int fillCEnergy(int maxReceive, boolean simulate) {
			if(!canReceive())return 0;
			
			
			boolean creative = battery.isCreative();
			if(creative){
				return 0;
			}
			
			int fill = energyStorage.fillCEnergy(Math.min(getEnergyReceive(), maxReceive), simulate);
			return fill;
		}

		@Override
		public int drainCEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getCEnergyStored() {
			return battery.energyStorage.getCEnergyStored();
		}

		@Override
		public int getMaxCEnergyStored() {
			return battery.energyStorage.getMaxCEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			EnumFacing fixedFacing = battery.fixFace(facing);
			IOType io = battery.getIO(fixedFacing);
			return !(io == IOType.BLOCKED || io == IOType.OUT);
		}
    	
    };
	
	@Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
        if (side !=null && cap == CapabilityCrystalEnergy.CENERGY){
            return holders[side.getIndex()].cast();
        }
        return super.getCapability(cap, side);
    }

	@Override
	public ITextComponent getName() {
		return new TextComponentString("Battery");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getCustomName() {
		return null;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerBattery(playerIn, this);
	}

	@Override
	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}
	
	//Inventory Stuff
	
	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot < 0 || slot >= inventory.size()) {
			return ItemStackTools.getEmptyStack();
		}
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int quantity) {
		ItemStack itemStack = getStackInSlot(slot);

	    if(ItemStackTools.isNullStack(itemStack)) {
	      return ItemStackTools.getEmptyStack();
	    }

	    // whole itemstack taken out
	    if(ItemStackTools.getStackSize(itemStack) <= quantity) {
	      setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
	      onItemChanged(slot);
	      markDirty();
	      return itemStack;
	    }

	    // split itemstack
	    itemStack = itemStack.split(quantity);
	    // slot is empty, set to null
	    if(ItemStackTools.isEmpty(getStackInSlot(slot))) {
	      setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
	    }
	    onItemChanged(slot);
	    markDirty();
	    // return remainder
	    return itemStack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack itemStack = getStackInSlot(slot);
	    setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
	    return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		if(slot < 0 || slot >= inventory.size()) {
			return;
		}

		this.inventory.set(slot, itemstack);
		if(ItemStackTools.getStackSize(itemstack) > getInventoryStackLimit()) {
			ItemStackTools.setStackSize(itemstack, getInventoryStackLimit());
		}
		onItemChanged(slot);
		markDirty();
	}
	
	public void onItemChanged(int slot){
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	public boolean canCharge(ItemStack stack){
		return stack.getCapability(CapabilityCrystalEnergy.CENERGY).isPresent();
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return canCharge(stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] {0, 1};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 && canCharge(itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack stack : inventory){
			if(ItemStackTools.isValid(stack)){
				return false;
			}
		}
		return true;
	}

}
