package alec_wam.CrystalMod.crafting.recipes;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

import java.util.Iterator;
import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedOreRecipeNBT extends ShapedOreRecipe {

	static {
		RecipeSorter.register(CrystalMod.resource("shapedorenbt"),  ShapedOreRecipeNBT.class,  SHAPED,  "after:forge:shapedore before:minecraft:shapeless");
	}
	
	public ShapedOreRecipeNBT(Block     result, Object... recipe){ super(new ItemStack(result), recipe); }
    public ShapedOreRecipeNBT(Item      result, Object... recipe){ super(new ItemStack(result), recipe); }
    public ShapedOreRecipeNBT(ItemStack result, Object... recipe){ super(result, recipe); }
    
    @SuppressWarnings("unchecked")
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror)
    {
        for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++)
        {
            for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++)
            {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height)
                {
                    if (mirror)
                    {
                        target = input[width - subX - 1 + subY * width];
                    }
                    else
                    {
                        target = input[subX + subY * width];
                    }
                }

                ItemStack slot = inv.getStackInRowAndColumn(x, y);

                if (target instanceof ItemStack)
                {
                    if (!itemMatches((ItemStack)target, slot, false))
                    {
                        return false;
                    }
                }
                else if (target instanceof List)
                {
                    boolean matched = false;

                    Iterator<ItemStack> itr = ((List<ItemStack>)target).iterator();
                    while (itr.hasNext() && !matched)
                    {
                        matched = itemMatches(itr.next(), slot, false);
                    }

                    if (!matched)
                    {
                        return false;
                    }
                }
                else if (target == null && slot != null)
                {
                    return false;
                }
            }
        }

        return true;
    }
    
    public static boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
    {
        if (input == null && target != null || input != null && target == null)
        {
            return false;
        }
        return (target.getItem() == input.getItem() && ((target.getItemDamage() == OreDictionary.WILDCARD_VALUE && !strict) || target.getItemDamage() == input.getItemDamage()))  && ItemStack.areItemStackTagsEqual(target, input);
    }

}
