package alec_wam.CrystalMod.items.tools.backpack;

import java.util.Collections;
import java.util.UUID;

import com.enderio.core.common.util.ChatUtil;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerInventory;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.util.inventory.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BackpackUtil {

	public static void setOwner(ItemStack stack, UUID ownerUUID){
		if(!ItemNBTHelper.verifyExistance(stack, "Owner")){
			NBTUtils.setUUID(stack, ownerUUID, "Owner");
		}
	}
	
	public static UUID getOwner(ItemStack stack){
		return NBTUtils.getUUIDFromItemStack(stack, "Owner", false);
	}
	
	public static boolean canOpen(ItemStack itemStack, UUID uuid){
		UUID owner = getOwner(itemStack);
		return owner == null || UUIDUtils.areEqual(owner, uuid);
	}
	
	public static ActionResult<ItemStack> handleBackpackOpening(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand, boolean shift){
		if(!canOpen(itemStack, player.getUniqueID())){
			if(!world.isRemote){
				ChatUtil.sendNoSpam(player, "You do not own this backpack.");
			}
			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
		}
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

	public static void addRecipes() {
		
		ItemStack blueIngot = new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata());
		ItemStack redIngot = new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata());
		ItemStack greenIngot = new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata());
		ItemStack darkIngot = new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata());
		ItemStack pureIngot = new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata());
		ItemStack dIronIngot = new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata());
		
		ItemStack dIronNugget = new ItemStack(ModItems.crystals, 1, CrystalType.DIRON_NUGGET.getMetadata());
		ItemStack dIronPlate = new ItemStack(ModItems.plates, 1, PlateType.DARK_IRON.getMetadata());

		
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.NORMAL.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', "chestWood", 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK_IRON.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.DARKIRON.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.BLUE.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.BLUE.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.RED.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.RED.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.GREEN.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.GREEN.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.DARK.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.PURE.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.PURE.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK_IRON.ordinal()), Collections.emptyList(), new Object[]{"III", "IBI", "III", 'I', dIronIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.NORMAL.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.BLUE.ordinal()), Collections.emptyList(), new Object[]{"III", "IBI", "III", 'I', blueIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK_IRON.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.RED.ordinal()), Collections.emptyList(), new Object[]{"III", "IBI", "III", 'I', redIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.BLUE.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.GREEN.ordinal()), Collections.emptyList(), new Object[]{"III", "IBI", "III", 'I', greenIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.RED.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK.ordinal()), Collections.emptyList(), new Object[]{"III", "IBI", "III", 'I', darkIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.GREEN.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.PURE.ordinal()), Collections.emptyList(), new Object[]{"III", "IBI", "III", 'I', pureIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK.ordinal())});
		
		ModCrafting.addShapedRecipe(ModItems.backpackLock, new Object[]{" N ", "NPN", "NNN", 'N', dIronNugget, 'P', dIronPlate});

	}
}
