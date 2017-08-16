package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.RecipeSorter;

public class RecipeWirelessChestMinecart implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":wirelesschestminecart", RecipeWirelessChestMinecart.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack modifiedMinecart = ItemStackTools.getEmptyStack();

    public RecipeWirelessChestMinecart() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack chest = ItemStackTools.getEmptyStack();
        int chestSlot = -1;
        int minecartSlot = -1;
        boolean hasMinecart = false;
        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(ItemStackTools.isNullStack(slot))
                continue;

            // is it the tool?
            if(slot.getItem() == Item.getItemFromBlock(ModBlocks.wirelessChest)){
            	if(!ItemStackTools.isNullStack(chest))return false;
            	chest = slot;
            	chestSlot = i;
            }
            // otherwise.. input material
            else{
            	if(slot.getItem() == Items.MINECART){
            		hasMinecart = true;
            		minecartSlot = i;
            	} else {
            		return false;
            	}
            }
        }
        // no super torch found?
        if(ItemStackTools.isNullStack(chest) || hasMinecart == false)
            return false;
        
        //Only on top of each other
        if(inventoryCrafting.getSizeInventory() == 9 && chestSlot != minecartSlot-3)return false;
        if(inventoryCrafting.getSizeInventory() == 4 && chestSlot != minecartSlot-2)return false;

        ItemStack minecart = new ItemStack(ModItems.wirelessChestMinecart);
        
        int code = ItemNBTHelper.getInteger(chest, WirelessChestHelper.NBT_CODE, 0);
        ItemNBTHelper.setInteger(minecart, WirelessChestHelper.NBT_CODE, code);
        
        String owner = ItemNBTHelper.getString(chest, WirelessChestHelper.NBT_OWNER, "");
        if(UUIDUtils.isUUID(owner)){
        	ItemNBTHelper.setString(minecart, WirelessChestHelper.NBT_OWNER, owner);
        }
        modifiedMinecart = minecart;
        
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	return modifiedMinecart;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return modifiedMinecart;
    }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
