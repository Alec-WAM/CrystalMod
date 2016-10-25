package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.BasicItemHandler;
import alec_wam.CrystalMod.tiles.BasicItemValidator;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TilePatternEncoder extends TileEntityMod implements IMessageHandler {

	private BasicItemHandler patterns = new BasicItemHandler(2, this, new BasicItemValidator(ModItems.craftingPattern));
	
	private Container craftingContainer = new Container() {
        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return false;
        }

        @Override
        public void onCraftMatrixChanged(IInventory inventory) {
            onCraftingMatrixChanged();
        }
    };
    private InventoryCrafting matrix = new InventoryCrafting(craftingContainer, 3, 3);
    private InventoryCraftResult result = new InventoryCraftResult();
	
    protected boolean isOreDict;
    
    public void writeCustomNBT(NBTTagCompound nbt){
    	super.writeCustomNBT(nbt);
    	nbt.setBoolean("isOreDict", isOreDict);
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
    	super.readCustomNBT(nbt);
    	this.isOreDict = nbt.getBoolean("isOreDict");
    	NBTTagList tagList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.tagCount(); i++) {
            int slot = tagList.getCompoundTagAt(i).getInteger("Slot");

            ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

            patterns.insertItem(slot, stack, false);
        }
    }
    
    public void onCraftingMatrixChanged() {
        markDirty();

        result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(matrix, worldObj));
    }
    
    public void clearMatrix(){
    	matrix.clear();
    	markDirty();
    }
    
    public void onCreatePattern() {
        if (mayCreatePattern()) {
            patterns.extractItem(0, 1, false);

            ItemStack pattern = new ItemStack(ModItems.craftingPattern);
            ItemPattern.setProcessing(pattern, false);
            ItemPattern.setOredict(pattern, this.isOreDict);
            
            for (int i = 0; i < 9; ++i) {
                ItemStack ingredient = matrix.getStackInSlot(i);
                if(ingredient !=null)ItemPattern.setInput(pattern, i, ingredient);
            }

            patterns.setStackInSlot(1, pattern);
        }
    }

    public boolean mayCreatePattern() {
        return result.getStackInSlot(0) != null && patterns.getStackInSlot(1) == null && patterns.getStackInSlot(0) != null;
    }
    
    public IItemHandler getDroppedItems() {
        return patterns;
    }

	public InventoryCrafting getMatrix() {
		return matrix;
	}

	public IInventory getResult() {
		return result;
	}

	public IItemHandler getPatterns() {
		return patterns;
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
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("Encode")){
			onCreatePattern();
		}
		
		if(messageId.equalsIgnoreCase("Ore")){
			boolean old = this.isOreDict;
			this.isOreDict = !old;
		}
	}
    
}
