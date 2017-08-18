package alec_wam.CrystalMod.tiles.crate;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileCrate extends TileEntityMod implements IFacingTile, IMessageHandler {

	public EnumFacing facing = EnumFacing.NORTH;
	public int rotation = 0;
	/** a timer used to determine if the player double clicked */
	private int doubleClickTimer, selectedSlot, mode;
	private int lastClicked, lastClickedExpected = -1;
	
	public int tier;
	private ItemStack storedStack = ItemStackTools.getEmptyStack();
	private final ItemHandlerCrate storage = new ItemHandlerCrate(this);
	
	public TileCrate(){
		this(0);
	}
	
	public TileCrate(int meta) {
		tier = meta;
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", getFacing());
		nbt.setInteger("Rotation", rotation);
		if(ItemStackTools.isValid(storedStack)){
			NBTTagCompound stackNBT = storedStack.serializeNBT();
			stackNBT.setInteger("StackSize", ItemStackTools.getStackSize(storedStack));
			nbt.setTag("StoredItem", stackNBT);
		}
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("Facing"))setFacing(nbt.getInteger("Facing"));
		else {
			setFacing(EnumFacing.NORTH.getIndex());
		}
		if(nbt.hasKey("Rotation"))rotation = nbt.getInteger("Rotation");
		else {
			rotation = 0;
		}
		if(nbt.hasKey("StoredItem")){
			NBTTagCompound stackNBT = nbt.getCompoundTag("StoredItem");
			storedStack = ItemStackTools.loadFromNBT(stackNBT);
			ItemStackTools.setStackSize(storedStack, stackNBT.getInteger("StackSize"));
		}
		updateAfterLoad();
	}
	
	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}
	
	public void syncStack(){
		if(getWorld() !=null && !getWorld().isRemote && getPos() !=null){
			ItemStack stack = getStack();
			NBTTagCompound stackNBT = new NBTTagCompound();
			if(ItemStackTools.isValid(stack)){
				stackNBT = stack.serializeNBT();
				stackNBT.setInteger("StackSize", ItemStackTools.getStackSize(stack));
			}
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "StackSync", stackNBT), this);
		}
	}
	
	public int getLastClicked() {
		return lastClicked;
	}
	
	public void resetLastClicked() {
		lastClickedExpected = 1;
	}
	
	public void setLastClicked(int lastClicked) {
		this.lastClicked = lastClicked;
		resetLastClicked();
	}
	
	public void setClick(int selectedSlot, int mode, int time) {
		this.selectedSlot = selectedSlot;
		this.mode = mode;
		doubleClickTimer = time;
	}
	
	public int getSelectedSlot() {
		return selectedSlot;
	}
	
	public int getMode (){
		return mode;
	}
	
	public void resetTimer() {
		doubleClickTimer = 0;
	}
	
	public boolean isTimerActive() {
		return doubleClickTimer > 0;
	}
	
	public boolean isAbrupted() {
		return lastClickedExpected == -1;
	}
	
	@Override
	public void update(){
		super.update();
		if (this.isTimerActive()) {
			this.doubleClickTimer--;
		}
		
		if (lastClickedExpected >= 0){
			lastClickedExpected--;
			
			if (lastClicked > 0){
				lastClicked--;
			}
			
			if (lastClickedExpected == -1){
				lastClicked = 0;
			}
		}
	}
	
	public ItemStack getStack(){
		return storedStack;
	}
	
	public void setStack(@Nonnull ItemStack stack){
		storedStack = stack;
	    syncStack();
	    markDirty();
	}
	
	public int getCrateSize(){
		if(tier == 0)return 64;
		return (64*(16*tier));
	}
	
	public ItemStack addItem(ItemStack item) {
		if (ItemStackTools.isValid(item)) {
			ItemStack stored = getStack();
			if (ItemStackTools.isEmpty(stored)) {
				int allowedItems = item.getMaxStackSize() * getCrateSize();
				if (ItemStackTools.getStackSize(item) > allowedItems){
					setStack(ItemUtil.copy(item, allowedItems));
					ItemStackTools.incStackSize(item, -allowedItems);
					return item;
				}else{
					setStack(item.copy());
					return ItemStackTools.getEmptyStack();
				}
			} else if (ItemUtil.canCombine(item, stored)) {
				int allowedItems = stored.getMaxStackSize() * getCrateSize();
				if (ItemStackTools.getStackSize(stored) + ItemStackTools.getStackSize(item) > allowedItems){
					ItemStackTools.incStackSize(item, -(allowedItems - ItemStackTools.getStackSize(stored)));
					ItemStackTools.setStackSize(stored, allowedItems);
					setStack(stored);
				}else{
					ItemStackTools.incStackSize(stored, ItemStackTools.getStackSize(item));
					syncStack();
					return ItemStackTools.getEmptyStack();
				}
			} 
			return item;
		}
		return ItemStackTools.getEmptyStack();
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("StackSync")){
			storedStack = ItemStackTools.loadFromNBT(messageData);
			ItemStackTools.setStackSize(storedStack, messageData.getInteger("StackSize"));
		}
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
		return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) storage;
        return super.getCapability(capability, facing);
    }

}
