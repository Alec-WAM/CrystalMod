package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.List;

import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class TileWorksiteBoundedInventory extends TileWorksiteBounded implements ISidedInventory {

	public InventorySided inventory;
	
	public final boolean addStackToInventoryNoDrop(ItemStack stack, boolean sim, RelativeSide... sides)
	{
		for(RelativeSide side : sides){
		  int[] slots = inventory.getRawIndicesCombined(side);
		  ItemStack copy = stack.copy();
		  int added = ItemUtil.doInsertItemInvArray(inventory, copy, slots, !sim);
		  ItemStackTools.incStackSize(stack, -added);
		  if(ItemStackTools.isEmpty(stack)){
			  stack = ItemStackTools.getEmptyStack();
			  return true;
		  }
		}
		return stack.isEmpty();
	}
	
	public final void addStackToInventory(ItemStack stack, RelativeSide... sides)
	{
		if(!addStackToInventoryNoDrop(stack, false, sides))
		{
			ItemUtil.spawnItemInWorldWithoutMotion(getWorld(), stack, getPos().up());
		}
	}
	
	public boolean harvestBlock(BlockPos pos, int fortune, RelativeSide...relativeSides)
	{
		if(getWorld().isRemote || !(getWorld() instanceof WorldServer)) {
	        return false;
		}
		IBlockState state = getWorld().getBlockState(pos);
		if(getWorld().isAirBlock(pos)){
			return false;
		}
		EntityPlayer player = FakePlayerUtil.getPlayer((WorldServer)getWorld());
		float chance = 1.0f;
		List<ItemStack> drops = state.getBlock().getDrops(getWorld(), pos, state, fortune);
		chance = ForgeEventFactory.fireBlockHarvesting(drops, getWorld(), pos, state, fortune, chance, false, player);
		if(!ItemUtil.canInventoryHold(inventory, inventory.getRawIndicesCombined(relativeSides), drops))
		{
			return false;
		}
		getWorld().playEvent(player, 2001, pos, Block.getStateId(state));
		getWorld().setBlockToAir(pos);
		for(ItemStack stack : drops)
		{
			if(getWorld().rand.nextFloat() <= chance){
				addStackToInventory(stack, relativeSides);
			}
		}
		return true;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound tag)
	{
	  super.writeCustomNBT(tag);
	  if(inventory!=null)
	  {
	    NBTTagCompound invTag = new NBTTagCompound();
	    inventory.writeToNBT(invTag);
	    tag.setTag("inventory", invTag);    
	  }
	}

	@Override
	public void readCustomNBT(NBTTagCompound tag)
	{
	  super.readCustomNBT(tag);
	  if(tag.hasKey("inventory") && inventory!=null)
	  {
	    inventory.readFromNBT(tag.getCompoundTag("inventory"));
	  }
	}

	public void openAltGui(EntityPlayer player) {
	}
	
	
	@Override
	public int getSizeInventory()
	{
	  return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int var1)
	{
	  return inventory.getStackInSlot(var1);
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2)
	{
	  return inventory.decrStackSize(var1, var2);
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2)
	{
	  inventory.setInventorySlotContents(var1, var2);
	}

	@Override
	public String getName()
	{
	  return inventory.getName();
	}

	@Override
	public boolean hasCustomName()
	{
	  return inventory.hasCustomName();
	}

	@Override
	public int getInventoryStackLimit()
	{
	  return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer var1)
	{
	  return inventory.isUsableByPlayer(var1);
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
	  inventory.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
	  inventory.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2)
	{
	  return inventory.isItemValidForSlot(var1, var2);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing var1)
	{
	  return inventory.getSlotsForFace(var1);
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, EnumFacing var3)
	{
	  return inventory.canInsertItem(var1, var2, var3);
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, EnumFacing var3)
	{
	  return inventory.canExtractItem(var1, var2, var3);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public void clear() {
		inventory.clear();
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
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
            return (T) new net.minecraftforge.items.wrapper.SidedInvWrapper(this, facing);
        return super.getCapability(capability, facing);
    }
}
