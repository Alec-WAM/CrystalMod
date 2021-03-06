package alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

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
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagCompound nbtMatrix = new NBTTagCompound();
		ItemUtil.writeInventoryToNBT(matrix, nbtMatrix);
		nbt.setTag("InvMatrix", nbtMatrix);
		NBTTagCompound nbtResult = new NBTTagCompound();
		ItemUtil.writeInventoryToNBT(result, nbtResult);
		nbt.setTag("InvResult", nbtResult);
	}
	
	@Override
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
			if(!ItemStackTools.isNullStack(stack)){
				if(this.network !=null && !(network instanceof EStorageNetworkClient)){
					stack = network.getItemStorage().addItem(stack, false);
					if(!ItemStackTools.isValid(stack)){
						stack = ItemStackTools.getEmptyStack();
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

        result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(matrix, getWorld()));
    }
	
	public void onCrafted(EntityPlayer player) {
		//matrix.clear();
		NonNullList<ItemStack> remainder = CraftingManager.getInstance().getRemainingItems(matrix, getWorld());

        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            if (i < remainder.size() && ItemStackTools.isValid(remainder.get(i))) {
                if (ItemStackTools.isValid(slot) && ItemStackTools.getStackSize(slot) > 1) {
                	ItemStack copy = ItemStackTools.safeCopy(remainder.get(i));
                    if (!player.inventory.addItemStackToInventory(copy)) {
                    	ItemUtil.spawnItemInWorldWithoutMotion(player.getEntityWorld(), copy, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
                    }

                    matrix.decrStackSize(i, 1);
                } else {
                    matrix.setInventorySlotContents(i, ItemStackTools.safeCopy(remainder.get(i)));
                }
            } else {
                if (ItemStackTools.isValid(slot)) {
                    if (ItemStackTools.getStackSize(slot) == 1 && getNetwork() !=null) {
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

        boolean cancel = false;
        while (!cancel) {
            onCrafted(player);

            craftedItemsList.add(crafted.copy());

            craftedItems += ItemStackTools.getStackSize(crafted);

            if (!ItemUtil.canCombine(crafted, result.getStackInSlot(0)) || craftedItems + ItemStackTools.getStackSize(crafted) > crafted.getMaxStackSize()) {
            	cancel = true;
                break;
            }
        }

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
                ItemUtil.spawnItemInWorldWithoutMotion(player.getEntityWorld(), craftedItem, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
            }
        }

        container.sendCraftingSlots();
        container.detectAndSendChanges();
    }
}
