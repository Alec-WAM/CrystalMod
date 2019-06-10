package alec_wam.CrystalMod.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class TileEntityInventory extends TileEntityMod implements IInventory {

	private String name;
	public NonNullList<ItemStack> inventory;
	
	public TileEntityInventory(TileEntityType<?> tileEntityTypeIn, String name, int size){
		super(tileEntityTypeIn);
		this.name = name;
		this.inventory = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		nbt.putString("InvName", name);
		ItemStackHelper.saveAllItems(nbt, inventory);
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		name = nbt.getString("InvName");
		ItemStackHelper.loadAllItems(nbt, inventory);
	}
	
	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot < 0 || slot >= inventory.size()) {
			return ItemStackTools.getEmptyStack();
		}

		//System.out.println("" + inventory);
		
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
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		if(getWorld().getTileEntity(pos) != this || getWorld().getBlockState(pos).getBlock() == Blocks.AIR) {
	      return false;
	    }

	    return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
	}

	@Override
	public void openInventory(PlayerEntity player) {}

	@Override
	public void closeInventory(PlayerEntity player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public void clear() {
		inventory.clear();
	}
	
	public final net.minecraftforge.items.IItemHandler handler = new net.minecraftforge.items.wrapper.InvWrapper(this){
		@Override
	    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	    {
			if(!ItemStackTools.isEmpty(stack)){
				if(!TileEntityInventory.this.canInsertItem(slot, stack)){
					return stack;
				}
			}
			return super.insertItem(slot, stack, simulate);
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			if(amount > 0){
				if(!TileEntityInventory.this.canExtract(slot, amount)){
					return ItemStackTools.getEmptyStack();
				}
			}
			return super.extractItem(slot, amount, simulate);
		}
	};
	private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> handler);
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(cap, side);
    }

	public boolean canExtract(int slot, int amount) {
		return true;
	}

	public boolean canInsertItem(int slot, ItemStack stack) {
		return true;
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
