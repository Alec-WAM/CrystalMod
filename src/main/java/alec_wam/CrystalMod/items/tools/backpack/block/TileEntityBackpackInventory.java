package alec_wam.CrystalMod.items.tools.backpack.block;

import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityBackpackInventory extends TileEntityBackpack {

	public NormalInventoryBackpackTile inventory;
	
	public TileEntityBackpackInventory(){
		super();
	}
	
	public static class NormalInventoryBackpackTile extends NormalInventoryBackpack {

		public TileEntityBackpack backpackTile;
		
		public NormalInventoryBackpackTile(ItemStack backpack, TileEntityBackpack tile, int size) {
			super(backpack, size);
			//this.backpackTile = tile;
		}
		
		@Override
		public void guiSaveSafe(EntityPlayer player){
			save();
	    }
		
		@Override
	    public void guiSave(EntityPlayer player){
			save();
	    }
	    
		@Override
	    public void save(){
	    	if(backpackTile !=null)backpackTile.markDirty();
	    }
		
		public void writeToStack(ItemStack stack){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
	    	writeToNBT(nbt);
	    	stack.setTagCompound(nbt);
		}
	}
	
	public abstract int getSizeOfBackpackInventory();
	
	@Override
	public void loadFromStack(ItemStack stack) {
		super.loadFromStack(stack);
		inventory = new NormalInventoryBackpackTile(stack, this, getSizeOfBackpackInventory());
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(inventory !=null){
			NBTTagCompound invNBT = new NBTTagCompound();
			inventory.writeToNBT(invNBT);
			nbt.setTag("Inventory", invNBT);
		}
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("Inventory")){
			NBTTagCompound invNBT = nbt.getCompoundTag("Inventory");
			if(inventory == null){
				inventory = new NormalInventoryBackpackTile(backpack, this, getSizeOfBackpackInventory());
			}
			if(inventory !=null){
				inventory.readFromNBTNoPlayer(invNBT);
			}
		}
	}

	@Override
	public ItemStack getDroppedBackpack() {
		if(inventory !=null){
			inventory.writeToStack(getBackpack());
		}
		return getBackpack();
	}
}
