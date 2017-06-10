package alec_wam.CrystalMod.tiles.tooltable;

import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileEnhancementTable extends TileEntityInventory implements IMessageHandler {

	public TileEnhancementTable() {
		super("EnhancementTable", 1);
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equals("Add")){
			if(messageData.hasKey("ID")){
				ResourceLocation id = new ResourceLocation(messageData.getString("ID"));
				IEnhancement enhancement = EnhancementManager.getEnhancement(id);
				if(enhancement !=null){
					
				}
			}
		}
	}
	
	public void applyEnhancement(EntityPlayer player, IEnhancement enhancement){
		ItemStack currentTool = getStackInSlot(0);
		if(ItemStackTools.isValid(currentTool)){
			if(enhancement.canApply(currentTool, player)){
				if(enhancement.removeItemsFromPlayer(player)){
					setInventorySlotContents(0, enhancement.apply(currentTool, player));
				}
			}
		}
	}
	
	public void removeEnhancement(EntityPlayer player, IEnhancement enhancement){
		ItemStack currentTool = getStackInSlot(0);
		if(ItemStackTools.isValid(currentTool)){
			if(enhancement.isApplied(currentTool)){
				if(enhancement.returnItemsToPlayer(player)){
					ItemStack returnStack = enhancement.remove(currentTool, player);
					//Clean NBT
					if(returnStack.getTagCompound().hasNoTags()){
						returnStack.setTagCompound(null);
					}
					setInventorySlotContents(0, returnStack);
				}
			}
		}
	}

}
