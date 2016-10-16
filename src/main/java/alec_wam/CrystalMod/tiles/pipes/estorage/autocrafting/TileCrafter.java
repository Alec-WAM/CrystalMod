package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.BasicItemHandler;
import alec_wam.CrystalMod.tiles.IItemValidator;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.INetworkTile;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.ICraftingTask;
import alec_wam.CrystalMod.util.ItemUtil;

public class TileCrafter extends TileEntityMod implements INetworkTile, IAutoCrafter {

	private BasicItemHandler patterns = new BasicItemHandler(16, this, new IItemValidator() {
        @Override
        public boolean valid(ItemStack stack) {
            return stack.getItem() == ModItems.craftingPattern && ItemPattern.isValid(stack);
        }
    }){
	    @Override
	    public void onContentsChanged(int slot) {
	        super.onContentsChanged(slot);
	        if(getNetwork() !=null){
	        	getNetwork().updatePatterns();
	        }
	    }
    };
	
	private EStorageNetwork network;
	
	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {
		if(getNetwork() == null)return;
		for (ICraftingTask task : getNetwork().getCraftingTasks()) {
            if (task.getPattern().getCrafter() == this) {
            	getNetwork().cancelCraftingTask(task);
            }
        }
	}

	public int getSpeed() {
		return 20 - (0/*TIER*/ * 4);
	}

	public static final String NBT_DIRECTION = "Direction";

    private EnumFacing direction = EnumFacing.NORTH;
	
    public void writeCustomNBT(NBTTagCompound nbt){
    	nbt.setInteger(NBT_DIRECTION, direction.ordinal());
    	
    	NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < patterns.getSlots(); i++) {
            if (patterns.getStackInSlot(i) != null) {
                NBTTagCompound compoundTag = new NBTTagCompound();

                compoundTag.setInteger("Slot", i);

                patterns.getStackInSlot(i).writeToNBT(compoundTag);

                tagList.appendTag(compoundTag);
            }
        }

        nbt.setTag("Inventory", tagList);
    }
    
    public void readCustomNBT(NBTTagCompound nbt){
    	this.direction = EnumFacing.getFront(nbt.getInteger(NBT_DIRECTION));
    	
    	NBTTagList tagList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.tagCount(); i++) {
            int slot = tagList.getCompoundTagAt(i).getInteger("Slot");

            ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

            patterns.insertItem(slot, stack, false);
        }
        updateAfterLoad();
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
	public TileEntity getFacingTile() {
		return worldObj.getTileEntity(pos.offset(direction));
	}

	public EnumFacing getDirection() {
		return direction;
	}

	public void setDirection(EnumFacing facing) {
		direction = facing;
		
		markDirty();
	}

	public IItemHandler getDroppedItems() {
		return patterns;
	}
	
	public IItemHandler getPatterns() {
        return patterns;
    }
	
	public boolean showPatterns(){
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) patterns;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

	@Override
	public int getDimension() {
		return worldObj !=null ? worldObj.provider.getDimension() : 0;
	}
	
	public IItemHandler getFacingInventory(){
		return ItemUtil.getItemHandler(getFacingTile(), getDirection().getOpposite());
	}

}
