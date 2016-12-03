package alec_wam.CrystalMod.items.tools.backpack;

import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerInventory;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BackpackUtil {

	public static ActionResult<ItemStack> handleBackpackOpening(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand, boolean shift){
        if (world.isRemote){ //client side
            ItemNBTHelper.updateUUID(itemStack);
            ExtendedPlayerProvider.getExtendedPlayer(player).setOpenBackpack(itemStack);
            //TODO Open sound
            //player.playSound(open_backpack, 1F, 1F);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
        } else {
        	ItemNBTHelper.updateUUID(itemStack);
        	ExtendedPlayerProvider.getExtendedPlayer(player).setOpenBackpack(itemStack);
            boolean sneaking = shift ? true : player.isSneaking();
            player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_BACKPACK, world, OpenType.ANY.ordinal(), 0, 0);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
        }
    }
	
	public static ItemStack getBackpack(EntityPlayer player, OpenType type){
		if(type == OpenType.BACK){
			return getBackpackOnBack(player);
		}		
		return getPlayerBackpack(player);
	}
	
	public static ItemStack getPlayerBackpack(EntityPlayer player){
		ItemStack backpack = ItemStackTools.getEmptyStack();
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
		ItemStack openBackpack = ePlayer.getOpenBackpack();
		ItemStack backBackpack = getBackpackOnBack(player);
		if(ItemStackTools.isValid(openBackpack)){
			backpack = openBackpack;
		} 
		else if(ItemStackTools.isValid(backBackpack)){
			backpack = backBackpack;
		} 
		else {
			backpack = findBackpackInventory(player);
		}
		
		if(!player.getEntityWorld().isRemote && ItemStackTools.isValid(backpack)){
			ItemNBTHelper.updateUUID(backpack);
		}
		
		return backpack;
	}
	
	public static ItemStack getBackpackOnBack(EntityPlayer player){
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
		if(ePlayer == null || ePlayer.getInventory() == null) return ItemStackTools.getEmptyStack();
		return ePlayer.getInventory().getStackInSlot(ExtendedPlayerInventory.BACKPACK_SLOT_ID);
	}

	public static ItemStack findBackpackInventory(EntityPlayer player) {
		ItemStack backpack = ItemStackTools.getEmptyStack();
        if (ItemStackTools.isValid(player.getHeldItemMainhand()) && player.getHeldItemMainhand().getItem() instanceof ItemBackpackBase) {
            backpack = player.getHeldItemMainhand();
        }
        else if (ItemStackTools.isValid(player.getHeldItemOffhand()) && player.getHeldItemOffhand().getItem() instanceof ItemBackpackBase) {
            backpack = player.getHeldItemOffhand();
        }
        else {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);

                if (ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBackpackBase) {
                    backpack = player.inventory.getStackInSlot(i);
                }
            }
        }
        if (!player.worldObj.isRemote && ItemStackTools.isValid(backpack)) {
            ItemNBTHelper.updateUUID(backpack);
        }

        return backpack;
	}
	
	public static ItemStack findBackpack(EntityPlayer player, UUID uuid) {
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBackpackBase && ItemNBTHelper.hasUUID(stack)) {
            	UUID stackUUID = ItemNBTHelper.getUUID(stack);
            	if(stackUUID.getLeastSignificantBits() == uuid.getLeastSignificantBits() && stackUUID.getMostSignificantBits() == uuid.getMostSignificantBits()){
            		return player.inventory.getStackInSlot(i);
            	} 
            }
        }
        
        ItemStack backBackpack = BackpackUtil.getBackpackOnBack(player);
        if (ItemStackTools.isValid(backBackpack) && backBackpack.getItem() instanceof ItemBackpackBase && ItemNBTHelper.hasUUID(backBackpack)) {
        	if(ItemNBTHelper.getUUID(backBackpack) == uuid){
        		return backBackpack;
        	}
        }
        return ItemStackTools.getEmptyStack();
	}
}
