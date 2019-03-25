package alec_wam.CrystalMod.items.tools.backpack.block;

import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackCrafting;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackCrafting;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBackpackCrafting extends TileEntityBackpack {

	public InventoryBackpackTile inventory;
	
	public TileEntityBackpackCrafting(){
		super();
	}
	
	public static class InventoryBackpackTile extends InventoryBackpack {

		public TileEntityBackpack backpackTile;
		
		public InventoryBackpackTile(ItemStack backpack, TileEntityBackpack tile, int size) {
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
	
	@Override
	public void loadFromStack(ItemStack stack) {
		super.loadFromStack(stack);
		inventory = new InventoryBackpackTile(stack, this, 9);
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
				inventory = new InventoryBackpackTile(backpack, this, 9);
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

	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world) {
		return new GuiBackpackCrafting(inventory, player.inventory);
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world) {
		return new ContainerBackpackCrafting(inventory, player.inventory);
	}

}
