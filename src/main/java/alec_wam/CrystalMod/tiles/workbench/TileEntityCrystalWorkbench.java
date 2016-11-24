package alec_wam.CrystalMod.tiles.workbench;

import java.util.Arrays;

import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityCrystalWorkbench extends TileEntityMod implements ISidedInventory{

	private ItemStack[] inventory = new ItemStack[10];
	
	@Override
	public void readCustomNBT(NBTTagCompound tags) {
		super.readCustomNBT(tags);
		readInventoryFromNBT(tags);
	}

	public void readInventoryFromNBT(NBTTagCompound tags) {
	    inventory = Arrays.copyOf(inventory, 10);
	    ItemUtil.readInventoryFromNBT(this, tags);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound tags) {
	    super.writeCustomNBT(tags);

	    ItemUtil.writeInventoryToNBT(this, tags);
	}
	
	@Override
	public String getName() {
		return "Crystal Workbench";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot < 0 || slot >= inventory.length) {
			return null;
		}

		return inventory[slot];
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
	      this.markDirty();
	      return itemStack;
	    }

	    // split itemstack
	    itemStack = itemStack.splitStack(quantity);
	    // slot is empty, set to null
	    if(ItemStackTools.isEmpty(getStackInSlot(slot))) {
	      setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
	    }

	    this.markDirty();
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
		if(slot < 0 || slot >= inventory.length) {
	      return;
	    }

	    inventory[slot] = itemstack;
	    if(!ItemStackTools.isNullStack(itemstack) && ItemStackTools.getStackSize(itemstack) > getInventoryStackLimit()) {
	    	ItemStackTools.setStackSize(itemstack, getInventoryStackLimit());
	    }
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(worldObj.getTileEntity(pos) != this || worldObj.getBlockState(pos).getBlock() == Blocks.AIR) {
	      return false;
	    }

	    return
	    	player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D)
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
		for(int i = 0; i < inventory.length; i++) {
	      inventory[i] = ItemStackTools.getEmptyStack();
	    }
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.UP ? new int[]{0} : new int[]{1,2,3,4,5,6,7,8,9};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return direction !=EnumFacing.UP && index > 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return direction == EnumFacing.UP && index == 0;
	}

}
