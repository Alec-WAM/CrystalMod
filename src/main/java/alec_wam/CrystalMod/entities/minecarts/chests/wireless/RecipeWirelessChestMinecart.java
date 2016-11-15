package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.ItemSuperTorch;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public class RecipeWirelessChestMinecart implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":wirelesschestminecart", RecipeWirelessChestMinecart.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack modifiedMinecart = null;

    public RecipeWirelessChestMinecart() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack chest = null;
        boolean hasMinecart = false;
        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(slot == null)
                continue;

            // is it the tool?
            if(slot.getItem() == Item.getItemFromBlock(ModBlocks.wirelessChest)){
            	if(chest !=null)return false;
            	chest = slot;
            }
            // otherwise.. input material
            else{
            	if(slot.getItem() == Items.MINECART){
            		hasMinecart = true;
            	} else {
            		return false;
            	}
            }
        }
        // no super torch found?
        if(chest == null || hasMinecart == false)
            return false;
        
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
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}
}
