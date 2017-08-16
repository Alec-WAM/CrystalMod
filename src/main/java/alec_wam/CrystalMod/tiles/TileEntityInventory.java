package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class TileEntityInventory extends TileEntityMod implements IInventory {

	private String name;
	protected NonNullList<ItemStack> inventory;
	
	public TileEntityInventory(String name, int size){
		this.name = name;
		inventory = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setString("InvName", name);
		ItemStackHelper.saveAllItems(nbt, inventory);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
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

		return inventory.get(slot);
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
	    itemStack = itemStack.splitStack(quantity);
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

	    inventory.set(slot, itemstack);
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
	public boolean isUsableByPlayer(EntityPlayer player) {
		if(getWorld().getTileEntity(pos) != this || getWorld().getBlockState(pos).getBlock() == Blocks.AIR) {
	      return false;
	    }

	    return
	    	player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)
	        <= 64D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
		return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	public net.minecraftforge.items.IItemHandler handler = new net.minecraftforge.items.wrapper.InvWrapper(this){
		@Override
	    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	    {
			if(!ItemStackTools.isEmpty(stack)){
				if(!TileEntityInventory.this.canInsertItem(slot, stack)){
					return ItemStackTools.getEmptyStack();
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) handler;
        return super.getCapability(capability, facing);
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
