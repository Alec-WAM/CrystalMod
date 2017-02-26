package alec_wam.CrystalMod.tiles.machine.power.engine.furnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.tiles.machine.power.engine.TileEntityEngineBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

public class TileEntityEngineFurnace extends TileEntityEngineBase implements ISidedInventory {

	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	
	public TileEntityEngineFurnace(){
		super();
	}
	
	public TileEntityEngineFurnace(int multi){
		super(multi);
	}
	
	@Override
	public CEnergyStorage createStorage(int multi) {
		return new CEnergyStorage(60000*multi, 30*multi);
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		ItemUtil.writeInventoryToNBT(inventory, nbt);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		ItemUtil.readInventoryFromNBT(inventory, nbt);
	}

	public static int getItemEnergyValue(ItemStack fuel)
	{
	    if (!ItemStackTools.isValid(fuel)) {
	      return 0;
	    }
	    int amt = GameRegistry.getFuelValue(fuel);
	    if(amt == 0)amt = TileEntityFurnace.getItemBurnTime(fuel);
	    //{ 10000, 400000, 2000000, 10000000}
	    /*TE
	     * (COAL)1600
	     * (VALUE)1600*10*3/2 = 24000
	     * (LEADSTONE)24000/10000=2.4
	     * (HARD)24000/400000=0.06
	     * (REDSTONE)24000/2000000=0.012
	     * (RESONENT)24000/10000000=0.0024
	     * */
	    /*CU
	     * (NEW)24000/5=4800
	     * (BLUE)4800/10000=0.48 (+ADV) = 0.96
	     * (RED)4800/400000=0.012 (+ADV) = 0.024
	     * (GREEN)4800/2000000=0.0024 (+ADV) = 0.0048
	     * (DARK)4800/10000000=0.00048 (+ADV) = 0.00096
	     */
	    
	    return amt;
	}
	
	public void update(){
		super.update();
	}
	
	public void refuel(){
		ItemStack stack = getStackInSlot(0);
		int amt = (ItemStackTools.isEmpty(stack) || (getItemEnergyValue(stack) == 0)) ? 0 : Math.min(multi, ItemStackTools.getStackSize(stack));
		for(int m = 0; m < amt; m++){
			fuel.setValue(fuel.getValue()+getItemEnergyValue(stack));
			maxFuel.setValue(fuel.getValue());
			setInventorySlotContents(0, consumeItem(stack));
		}
	}
	
	public int getFuelValue(){
		return 30;
	}
	
	public static ItemStack consumeItem(ItemStack stack)
	{
	    if (ItemStackTools.getStackSize(stack) == 1)
	    {
	      if (stack.getItem().hasContainerItem(stack)) {
	        return stack.getItem().getContainerItem(stack);
	      }
	      return ItemStackTools.getEmptyStack();
	    }
	    stack.splitStack(1);
	    return stack;
	}
	
	@Override
	public int getSizeInventory() {
		return 1;
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
		if(slot < 0 || slot >= inventory.size()) {
	      return;
	    }

	    inventory.set(slot, itemstack);
	    if(ItemStackTools.getStackSize(itemstack) > getInventoryStackLimit()) {
	    	ItemStackTools.setStackSize(itemstack, getInventoryStackLimit());
	    }
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
	    	player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D)
	        <= 64D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == 0 ? getItemEnergyValue(stack) > 0 : true;
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
		return "FurnaceEngine";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return  getItemEnergyValue(itemStackIn) > 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return getItemEnergyValue(stack) == 0;
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
