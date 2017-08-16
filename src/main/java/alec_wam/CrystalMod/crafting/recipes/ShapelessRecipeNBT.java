package alec_wam.CrystalMod.crafting.recipes;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

public class ShapelessRecipeNBT extends ShapelessRecipes {

	static {
		RecipeSorter.register(CrystalMod.resource("shapelessnbt"),  ShapelessRecipeNBT.class,  SHAPELESS,  "after:minecraft:shapeless");
	}
	
	public ShapelessRecipeNBT(ItemStack output, List<ItemStack> inputList)
    {
        super(output, inputList);
    }
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
    {
        List<ItemStack> list = Lists.newArrayList(this.recipeItems);

        for (int i = 0; i < inv.getHeight(); ++i)
        {
            for (int j = 0; j < inv.getWidth(); ++j)
            {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null)
                {
                    boolean flag = false;

                    for (ItemStack itemstack1 : list)
                    {
                        if (itemMatches(itemstack, itemstack1, false))
                        {
                            flag = true;
                            list.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag)
                    {
                        return false;
                    }
                }
            }
        }

        return list.isEmpty();
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
