package alec_wam.CrystalMod.tiles.energy.engine.furnace;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.energy.engine.TileEntityEngineBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityEngineFurnace extends TileEntityEngineBase implements ISidedInventory {

	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	private final LazyOptional<IItemHandlerModifiable>[] holder = SidedInvWrapper.create(this, EnumFacing.values());
	
	public TileEntityEngineFurnace(){
		super(ModBlocks.TILE_ENGINE_FURNACE);
	}
	
	public TileEntityEngineFurnace(int multi){
		super(ModBlocks.TILE_ENGINE_FURNACE, multi);
	}

	@Override
	public ITextComponent getName() {
		return new TextComponentString("FurnaceEngine");
	}

	@Override
	public ITextComponent getCustomName() {
		return null;
	}
	
	@Override
	public CEnergyStorage createStorage(int multi) {
		return new CEnergyStorage(60000*multi, 30*multi);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
	    ItemStackHelper.saveAllItems(nbt, this.inventory);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
	    ItemStackHelper.loadAllItems(nbt, this.inventory);
	}

	public static int getItemEnergyValue(ItemStack fuel)
	{
	    if (!ItemStackTools.isValid(fuel)) {
	      return 0;
	    }
	    int amt = ItemUtil.getFurnaceFuelValue(fuel);
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
	
	@Override
	public void tick(){
		super.tick();
		if(getWorld().isRemote){
			if(this.isActive()){
				Random rand = getWorld().rand;
				if(this.shouldDoWorkThisTick(10)){
					EnumFacing enumfacing = getFacing();
		            double d0 = (double)pos.getX() + 0.5D;
		            double d1 = (double)pos.getY() + 0.6D;
		            double d2 = (double)pos.getZ() + 0.5D;
		            double d3 = 0.52D;
		            double d4 = rand.nextDouble() * 0.4D - 0.2D;
	
		            if (rand.nextDouble() < 0.1D)
		            {
		                getWorld().playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
		            }
	
		            double x = d0;
		            double y = d1;
		            double z = d2;
		            switch (enumfacing)
		            {
		                case WEST:
		                    x = d0 - d3;
		                    y = d1; 
		                    z = d2 + d4;
		                    break;
		                case EAST:
		                	x = d0 + d3;
		                    y = d1; 
		                    z = d2 + d4;
		                    break;
		                default : case NORTH:
		                	x = d0 + d4;
		                    y = d1; 
		                    z = d2 - d3;
		                    break;
		                case SOUTH:
		                	x = d0 + d4;
		                    y = d1; 
		                    z = d2 + d3;
		            }
		            this.world.spawnParticle(Particles.SMOKE, x, y, z, 0, 0, 0);
				}
			}
		}
	}
	
	@Override
	public void refuel(){
		ItemStack stack = getStackInSlot(0);
		int amt = (ItemStackTools.isEmpty(stack) || (getItemEnergyValue(stack) == 0)) ? 0 : Math.min(multi, ItemStackTools.getStackSize(stack));
		for(int m = 0; m < amt; m++){
			fuel.setValue(fuel.getValue()+getItemEnergyValue(stack));
			maxFuel.setValue(fuel.getValue());
			setInventorySlotContents(0, consumeItem(stack));
		}
	}
	
	@Override
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
	    stack.split(1);
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
	    itemStack = itemStack.split(quantity);
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
	    	player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)
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
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
        if (cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
        	if(side == null){
        		return holder[0].cast();
        	}
            return holder[side.getIndex()].cast();
        }
        return super.getCapability(cap, side);
    }

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerEngineFurnace(playerIn, this);
	}

	@Override
	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}

}
