package alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityPanelCrafting extends TileEntityPanel {
	
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
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagCompound nbtMatrix = new NBTTagCompound();
		ItemUtil.writeInventoryToNBT(matrix, nbtMatrix);
		nbt.setTag("InvMatrix", nbtMatrix);
		NBTTagCompound nbtResult = new NBTTagCompound();
		ItemUtil.writeInventoryToNBT(result, nbtResult);
		nbt.setTag("InvResult", nbtResult);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("InvMatrix"))ItemUtil.readInventoryFromNBT(matrix, nbt.getCompoundTag("InvMatrix"));
		if(nbt.hasKey("InvResult"))ItemUtil.readInventoryFromNBT(result, nbt.getCompoundTag("InvResult"));
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		super.handleMessage(messageId, messageData, client);
		if(!client && messageId.equalsIgnoreCase("ClearGrid")){
			 clearGrid();
		}
	}
	
	public void clearGrid(){
		for(int i = 0; i < matrix.getSizeInventory(); i++){
			ItemStack stack = matrix.getStackInSlot(i);
			if(stack !=null){
				if(this.network !=null && !(network instanceof EStorageNetworkClient)){
					stack = network.getItemStorage().addItem(stack, false);
					if(stack != null && stack.stackSize <= 0){
						stack = null;
					}
					matrix.setInventorySlotContents(i, stack);
				}
			}
		}
		onCraftingMatrixChanged();
	}

	public InventoryCrafting getMatrix() {
        return matrix;
    }

    public InventoryCraftResult getResult() {
        return result;
    }
	
	public void onCraftingMatrixChanged() {
        markDirty();

        result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(matrix, worldObj));
    }
	
	public void onCrafted(EntityPlayer player) {
		//matrix.clear();
		ItemStack[] remainder = CraftingManager.getInstance().getRemainingItems(matrix, worldObj);

        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            if (i < remainder.length && remainder[i] != null) {
                if (slot != null && slot.stackSize > 1) {
                    if (!player.inventory.addItemStackToInventory(remainder[i].copy())) {
                    	ItemUtil.spawnItemInWorldWithoutMotion(player.worldObj, remainder[i].copy(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
                    }

                    matrix.decrStackSize(i, 1);
                } else {
                    matrix.setInventorySlotContents(i, remainder[i].copy());
                }
            } else {
                if (slot != null) {
                    if (slot.stackSize == 1 && getNetwork() !=null) {
                    	ItemStack copy = ItemUtil.copy(slot, 1);
                        matrix.setInventorySlotContents(i, getNetwork().getItemStorage().removeItem(copy, false));
                    } else {
                        matrix.decrStackSize(i, 1);
                    }
                }
            }
        }

        onCraftingMatrixChanged();
	}
	
	public void onCraftedShift(ContainerPanelCrafting container, EntityPlayer player) {
        List<ItemStack> craftedItemsList = new ArrayList<ItemStack>();
        int craftedItems = 0;
        ItemStack crafted = result.getStackInSlot(0);

        while (true) {
            onCrafted(player);

            craftedItemsList.add(crafted.copy());

            craftedItems += crafted.stackSize;

            if (!ItemUtil.canCombine(crafted, result.getStackInSlot(0)) || craftedItems + crafted.stackSize > crafted.getMaxStackSize()) {
                break;
            }
        }

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
                ItemUtil.spawnItemInWorldWithoutMotion(player.worldObj, craftedItem, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
            }
        }

        container.sendCraftingSlots();
        container.detectAndSendChanges();
    }
}
