package alec_wam.CrystalMod.crafting.recipes;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.ItemSuperTorch;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.RecipeSorter;

public class CustomToolRepairRecipe implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":customtoolrepairrecipe", CustomToolRepairRecipe.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack returnTool = ItemStackTools.getEmptyStack();

    public CustomToolRepairRecipe() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack tool1 = ItemStackTools.getEmptyStack();
        ItemStack tool2 = ItemStackTools.getEmptyStack();
    	for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            if(ItemStackTools.isNullStack(slot))
                continue;
            
            if(isCustomTool(slot)){
            	if(ItemStackTools.isNullStack(tool1)){
            		tool1 = slot;
            	} else if(ItemStackTools.isNullStack(tool2)){
            		String color = ItemNBTHelper.getString(tool1, "Color", "error");
            		if(!color.equalsIgnoreCase("error")){
            			String color2 = ItemNBTHelper.getString(slot, "Color", "error");
            			if(color.equalsIgnoreCase(color2)){
            				tool2 = slot;
            			}
            		} else {
            			return false;
            		}
            	} else {
            		return false;
            	}
            	
            } else {
            	return false;
            }
        }
    	
    	if(ItemStackTools.isNullStack(tool1) || ItemStackTools.isNullStack(tool2)) return false;
    	
    	int j = tool1.getMaxDamage() - tool1.getItemDamage();
        int k = tool1.getMaxDamage() - tool2.getItemDamage();
        int l = j + k + tool1.getMaxDamage() * 5 / 100;
        int i1 = tool1.getMaxDamage() - l;

        if (i1 < 0)
        {
            i1 = 0;
        }
        
        ItemStack copy = ItemStackTools.safeCopy(tool1);
        copy.setItemDamage(i1);
        returnTool = copy;
        return true;
    }

    public static boolean isCustomTool(ItemStack stack){
    	if(stack.getItem() == null)return false;
    	if(stack.getItem() == ModItems.crystalPickaxe || stack.getItem() == ModItems.megaCrystalPickaxe)return true;
    	if(stack.getItem() == ModItems.crystalSword)return true;
    	if(stack.getItem() == ModItems.crystalShovel || stack.getItem() == ModItems.megaCrystalShovel)return true;
    	if(stack.getItem() == ModItems.crystalAxe || stack.getItem() == ModItems.megaCrystalAxe)return true;
    	if(stack.getItem() == ModItems.crystalHoe)return true;
    	return false;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	return returnTool;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return returnTool;
    }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
