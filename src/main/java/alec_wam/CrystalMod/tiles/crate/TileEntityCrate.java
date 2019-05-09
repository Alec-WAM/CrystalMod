package alec_wam.CrystalMod.tiles.crate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityModVariant;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class TileEntityCrate extends TileEntityModVariant<EnumCrystalColor> implements IMessageHandler {

	public int rotation = 0;
	/** a timer used to determine if the player double clicked */
	private int doubleClickTimer, selectedSlot, mode;
	private int lastClicked, lastClickedExpected = -1;
	
	public int tier;
	private ItemStack storedStack = ItemStackTools.getEmptyStack();
	private final ItemHandlerCrate storage = new ItemHandlerCrate(this);
	private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> storage);
	
	public TileEntityCrate() {
		super(ModBlocks.TILE_CRATE);
	}
	
	public TileEntityCrate(EnumCrystalColor type) {
		super(ModBlocks.TILE_CRATE, type);
		this.tier = type.ordinal();
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInt("Rotation", rotation);
		if(ItemStackTools.isValid(storedStack)){
			NBTTagCompound stackNBT = storedStack.serializeNBT();
			stackNBT.setInt("StackSize", ItemStackTools.getStackSize(storedStack));
			nbt.setTag("StoredItem", stackNBT);
		}
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("Rotation"))rotation = nbt.getInt("Rotation");
		else {
			rotation = 0;
		}
		if(nbt.hasKey("StoredItem")){
			NBTTagCompound stackNBT = nbt.getCompound("StoredItem");
			storedStack = ItemStackTools.loadFromNBT(stackNBT);
			ItemStackTools.setStackSize(storedStack, stackNBT.getInt("StackSize"));
		}
		updateAfterLoad();
	}
	
	public void syncStack(){
		if(getWorld() !=null && !getWorld().isRemote && getPos() !=null){
			ItemStack stack = getStack();
			NBTTagCompound stackNBT = new NBTTagCompound();
			if(ItemStackTools.isValid(stack)){
				stackNBT = stack.serializeNBT();
				stackNBT.setInt("StackSize", ItemStackTools.getStackSize(stack));
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
	public void tick(){
		super.tick();
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
			ItemStackTools.setStackSize(storedStack, messageData.getInt("StackSize"));
		}
	}
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
        if (cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(cap, side);
    }

}
