package alec_wam.CrystalMod.items.tools.backpack;

import java.util.Collections;
import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerInventory;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.util.inventory.NBTUtils;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BackpackUtil {

	public static IBackpack getType(ItemStack stack){
		if(ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBackpackBase){
			return ((ItemBackpackBase)stack.getItem()).getBackpack();
		}
		return null;
	}
	
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
        if (!player.getEntityWorld().isRemote && ItemStackTools.isValid(backpack)) {
            ItemNBTHelper.updateUUID(backpack);
        }

        return backpack;
	}
	
	public static ItemStack findBackpack(EntityPlayer player, UUID uuid) {
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBackpackBase && ItemNBTHelper.hasUUID(stack)) {
            	UUID stackUUID = ItemNBTHelper.getUUID(stack);
            	if(UUIDUtils.areEqual(stackUUID, uuid)){
            		return player.inventory.getStackInSlot(i);
            	} 
            }
        }
        
        ItemStack backBackpack = BackpackUtil.getBackpackOnBack(player);
        if (ItemStackTools.isValid(backBackpack) && backBackpack.getItem() instanceof ItemBackpackBase && ItemNBTHelper.hasUUID(backBackpack)) {
        	if(UUIDUtils.areEqual(ItemNBTHelper.getUUID(backBackpack), uuid)){
        		return backBackpack;
        	}
        }
        return ItemStackTools.getEmptyStack();
	}

	public static InventoryBackpack getInventory(EntityPlayer player, ItemStack backpack){
		IBackpack type = getType(backpack);
		if(type !=null && type instanceof IBackpackInventory){
			return ((IBackpackInventory)type).getInventory(player, backpack);
		}
		return null;
	}
	
	public static InventoryBackpack getInventory(ItemStack backpack){
		IBackpack type = getType(backpack);
		if(type !=null && type instanceof IBackpackInventory){
			return ((IBackpackInventory)type).getInventory(backpack);
		}
		return null;
	}
	
	public static InventoryBackpackUpgrades getUpgradeInventory(EntityPlayer player, ItemStack backpack){
		IBackpack type = getType(backpack);
		if(type !=null){
			return type.getUpgradeInventory(player, backpack);
		}
		return null;
	}
	
	public static InventoryBackpackUpgrades getUpgradeInventory(ItemStack backpack){
		IBackpack type = getType(backpack);
		if(type !=null){
			return type.getUpgradeInventory(backpack);
		}
		return null;
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

		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.craftingBackpack), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', "workbench", 'L', "leather", '#', "ingotCrystal"});
		//EnderIO before default enderpearl
		String ender = ModCrafting.getBestOreID("ingotPulsatingIron", "enderpearl");
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.wirelessBackpack), new Object[]{" E ", "EBE", " E ", 'E', ender, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK_IRON.ordinal())});

		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.NORMAL.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', "chestWood", 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK_IRON.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.DARKIRON.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.BLUE.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.BLUE.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.RED.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.RED.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.GREEN.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.GREEN.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.DARK.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.PURE.ordinal()), new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.PURE.ordinal()), 'L', "leather", '#', "ingotCrystal"});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK_IRON.ordinal()), Collections.<String>emptyList(), new Object[]{"III", "IBI", "III", 'I', dIronIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.NORMAL.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.BLUE.ordinal()), Collections.<String>emptyList(), new Object[]{"III", "IBI", "III", 'I', blueIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK_IRON.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.RED.ordinal()), Collections.<String>emptyList(), new Object[]{"III", "IBI", "III", 'I', redIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.BLUE.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.GREEN.ordinal()), Collections.<String>emptyList(), new Object[]{"III", "IBI", "III", 'I', greenIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.RED.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK.ordinal()), Collections.<String>emptyList(), new Object[]{"III", "IBI", "III", 'I', darkIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.GREEN.ordinal())});
		ModCrafting.addNBTRecipe(new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.PURE.ordinal()), Collections.<String>emptyList(), new Object[]{"III", "IBI", "III", 'I', pureIngot, 'B', new ItemStack(ModItems.normalBackpack, 1, CrystalBackpackType.DARK.ordinal())});
	
		//Upgrades
		ModCrafting.addShapedRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.HOPPER.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', dIronNugget, 'M', Blocks.HOPPER});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.ENDER.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', dIronNugget, 'M', "chestEnder"});
		ModCrafting.addShapedRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.RESTOCKING.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', dIronNugget, 'M', Blocks.PISTON});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.VOID.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', dIronNugget, 'M', "obsidian"});
		ModCrafting.addShapedOreRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.POCKETS.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', "leather", 'M', "nuggetGold"});
		ModCrafting.addShapedRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.BOW.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', dIronNugget, 'M', Items.ARROW});
		ModCrafting.addShapedRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.DESPAWN.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', dIronNugget, 'M', Items.CLOCK});
		ModCrafting.addShapedRecipe(new ItemStack(ModItems.backpackupgrade, 1, BackpackUpgrade.DEATH.getMetadata()), new Object[]{"#!#", "!M!", "#!#", '#', dIronPlate, '!', dIronNugget, 'M', new ItemStack(Items.SKULL, 1, 1)});
	}

	public static boolean canSwapWeapons(EntityPlayer player){
		if(player !=null){
			ItemStack backpack = BackpackUtil.getBackpackOnBack(player);
			InventoryBackpack inventory = getInventory(player, backpack);
			InventoryBackpackUpgrades upgradeInv = getUpgradeInventory(backpack);
			if(inventory !=null && upgradeInv !=null){
				if(upgradeInv.hasUpgrade(BackpackUpgrade.POCKETS)){
					if(inventory instanceof NormalInventoryBackpack){
						int currentSlot = player.inventory.currentItem;
						final ItemStack current = player.inventory.getStackInSlot(currentSlot);
						if(ItemStackTools.isValid(current) && !ToolUtil.isWeapon(current))return false;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static void swapWeapons(EntityPlayer player) {
		if(player !=null){
			ItemStack backpack = BackpackUtil.getBackpackOnBack(player);
			InventoryBackpack inventory = getInventory(player, backpack);
			InventoryBackpackUpgrades upgradeInv = getUpgradeInventory(backpack);
			if(inventory !=null && upgradeInv !=null){
				if(!upgradeInv.hasUpgrade(BackpackUpgrade.POCKETS))return;
				if(inventory instanceof NormalInventoryBackpack){
					NormalInventoryBackpack normalInv = (NormalInventoryBackpack)inventory;
					int currentSlot = player.inventory.currentItem;
					final ItemStack current = player.inventory.getStackInSlot(currentSlot);
					if(ItemStackTools.isValid(current) && !ToolUtil.isWeapon(current))return;
					final ItemStack stored = normalInv.getToolStack(0); //Weapon Slot
					normalInv.setToolStack(0, current);
					player.inventory.setInventorySlotContents(currentSlot, stored);
					normalInv.markDirty();
					normalInv.guiSave(player);
					ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
					if(exPlayer !=null && exPlayer.getInventory() !=null){
						exPlayer.getInventory().setChanged(ExtendedPlayerInventory.BACKPACK_SLOT_ID, true);
					}
				}
			}
		}
	}
	
	public static boolean canSwapTools(EntityPlayer player){
		if(player !=null){
			ItemStack backpack = BackpackUtil.getBackpackOnBack(player);
			InventoryBackpack inventory = getInventory(player, backpack);
			InventoryBackpackUpgrades upgradeInv = getUpgradeInventory(backpack);
			if(inventory !=null && upgradeInv !=null){
				if(!upgradeInv.hasUpgrade(BackpackUpgrade.POCKETS))return false;
				if(inventory instanceof NormalInventoryBackpack){
					int currentSlot = player.inventory.currentItem;
					final ItemStack current = player.inventory.getStackInSlot(currentSlot);
					if(ItemStackTools.isValid(current) && !ToolUtil.isTool(current))return false;
					return true;
				}
			}
		}
		return false;
	}
	
	public static void swapTools(EntityPlayer player) {
		if(player !=null){
			ItemStack backpack = BackpackUtil.getBackpackOnBack(player);
			InventoryBackpack inventory = getInventory(player, backpack);
			InventoryBackpackUpgrades upgradeInv = getUpgradeInventory(backpack);
			if(inventory !=null && upgradeInv !=null){
				if(!upgradeInv.hasUpgrade(BackpackUpgrade.POCKETS))return;
				if(inventory instanceof NormalInventoryBackpack){
					NormalInventoryBackpack normalInv = (NormalInventoryBackpack)inventory;
					int currentSlot = player.inventory.currentItem;
					final ItemStack current = player.inventory.getStackInSlot(currentSlot);
					if(ItemStackTools.isValid(current) && !ToolUtil.isTool(current))return;
					final ItemStack stored = normalInv.getToolStack(1); //Tool Slot
					normalInv.setToolStack(1, current);
					player.inventory.setInventorySlotContents(currentSlot, stored);
					normalInv.markDirty();
					normalInv.guiSave(player);
					ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
					if(exPlayer !=null && exPlayer.getInventory() !=null){
						exPlayer.getInventory().setChanged(ExtendedPlayerInventory.BACKPACK_SLOT_ID, true);
					}
				}
			}
		}
	}
	
	public static void updateBackpack(EntityPlayer player){
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
		ExtendedPlayerInventory inventory = ePlayer.getInventory();
		boolean syncTick = player.ticksExisted % 10 == 0;
		
		if(syncTick){
			ItemStack backpack = getBackpackOnBack(player);
			InventoryBackpack inv = getInventory(player, backpack);
			InventoryBackpackUpgrades upgradeInv = getUpgradeInventory(backpack);
			if(inv !=null){
				if(upgradeInv !=null && upgradeInv.hasUpgrade(BackpackUpgrade.RESTOCKING)){
					boolean changed = false;
					for(int i = 0; i < 9; i++){
						ItemStack invStack = player.inventory.getStackInSlot(i);
						if(ItemStackTools.isValid(invStack)){
							int needed = invStack.getMaxStackSize() - ItemStackTools.getStackSize(invStack);
							if(needed > 0){
								search: for(int s = 0; s < inv.getSize(); s++){
									ItemStack bpStack = inv.getStackInSlot(s);
									if(ItemStackTools.isValid(bpStack) && ItemUtil.canCombine(invStack, bpStack)){
										int add = Math.min(needed, ItemStackTools.getStackSize(bpStack));
										inv.decrStackSize(s, add);
										ItemStackTools.incStackSize(invStack, add);
										needed-=add;
										changed = true;
									}
									if(needed <= 0){
										break search;
									}
								}
							}
						}
					}
					if(changed){
						inv.markDirty();
						inv.guiSave(player);
						inventory.setChanged(ExtendedPlayerInventory.BACKPACK_SLOT_ID, true);
					}
				}
			}
		}
	}
}
