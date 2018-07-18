package alec_wam.CrystalMod.tiles.enhancedEnchantmentTable;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityEnhancedEnchantmentTable extends TileEntityInventory implements IMessageHandler, IFacingTile {

	public int facing;
	
	public TileEntityEnhancedEnchantmentTable() {
		super("EnhancedEnchantmentTable", 3);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", facing);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = nbt.getInteger("Facing");
	}
	
	@Override
	public void onLoad(){
		super.onLoad();
		if(world.isBlockLoaded(getPos())){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(slot == 0){
			return ItemStackTools.isValid(stack) && stack.getItem().isEnchantable(stack);
		}
		if(slot == 1){
			return ItemStackTools.isValid(stack) &&stack.getItem() == Items.BOOK;
		}
		if(slot == 2){
			return ItemStackTools.isValid(stack) && stack.getItem() == Items.ENCHANTED_BOOK;
		}
		return super.isItemValidForSlot(slot, stack);
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack) {
		if(slot == 0){
			return stack.isItemEnchanted();
		}
		if(slot == 1){
			return stack.getItem() == Items.BOOK;
		}
		if(slot == 2){
			return false;
		}
		return super.canInsertItem(slot, stack);
	}
	
	@Override
	public boolean canExtract(int slot, int amt) {
		if(slot == 0){
			return true;
		}
		if(slot == 1){
			return false;
		}
		if(slot == 2){
			return true;
		}
		return super.canExtract(slot, amt);
	}
	
	public net.minecraftforge.items.IItemHandler handlerTop = new net.minecraftforge.items.wrapper.InvWrapper(this){
		@Override
		public int getSlots(){
			return 1;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot){
			return getStackInSlot(0);
		}
		
		@Override
	    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	    {
			if(!ItemStackTools.isEmpty(stack)){
				if(!canInsertItem(slot, stack)){
					return ItemStackTools.getEmptyStack();
				}
			}
			return super.insertItem(slot, stack, simulate);
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			if(amount > 0){
				if(!canExtract(slot, amount)){
					return ItemStackTools.getEmptyStack();
				}
			}
			return super.extractItem(slot, amount, simulate);
		}
	};
	
	public net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.InvWrapper(this){
		@Override
		public int getSlots(){
			return 1;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot){
			return getStackInSlot(1);
		}
		
		@Override
	    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	    {
			if(!ItemStackTools.isEmpty(stack)){
				if(!canInsertItem(slot, stack)){
					return ItemStackTools.getEmptyStack();
				}
			}
			return super.insertItem(slot, stack, simulate);
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			if(amount > 0){
				if(!canExtract(slot, amount)){
					return ItemStackTools.getEmptyStack();
				}
			}
			return super.extractItem(slot, amount, simulate);
		}
	};
	public net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.InvWrapper(this){
		@Override
		public int getSlots(){
			return 1;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot){
			return getStackInSlot(2);
		}
		
		@Override
	    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	    {
			if(!ItemStackTools.isEmpty(stack)){
				if(!canInsertItem(slot, stack)){
					return ItemStackTools.getEmptyStack();
				}
			}
			return super.insertItem(slot, stack, simulate);
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			if(amount > 0){
				if(!canExtract(slot, amount)){
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
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return facing == EnumFacing.UP ? (T) handlerTop : facing == EnumFacing.DOWN ? (T) handlerBottom : (T) handlerSide;
        }
        return super.getCapability(capability, facing);
    }

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("Update")){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		if(messageId.equalsIgnoreCase("Transfer")){
			if(ItemStackTools.isEmpty(getStackInSlot(2))){
				int[] selections = messageData.getIntArray("Selections");
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(getStackInSlot(0));
				@SuppressWarnings("unchecked")
				Entry<Enchantment, Integer>[] entries = (Entry<Enchantment, Integer>[]) enchantments.entrySet().toArray(new Entry[0]);
				Map<Enchantment, Integer> bookEnchants = Maps.newHashMap();
				for(int i : selections){
					Entry<Enchantment, Integer> entry = entries[i];
					if(entry !=null){
						enchantments.remove(entry.getKey());
						bookEnchants.put(entry.getKey(), entry.getValue());
					}
				}
				ItemStack newBook = new ItemStack(Items.ENCHANTED_BOOK);
				EnchantmentHelper.setEnchantments(bookEnchants, newBook);
				decrStackSize(1, 1);
				setInventorySlotContents(2, newBook);
				EnchantmentHelper.setEnchantments(enchantments, getStackInSlot(0));
			}
		}
	}

	@Override
	public void setFacing(int facing) {
		this.facing = facing;
	}

	@Override
	public int getFacing() {
		return facing;
	}
	
}
