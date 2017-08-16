package alec_wam.CrystalMod.crafting;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.crafting.ICrystalRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ShapelessCrystalRecipe implements ICrystalRecipe {

	/** Is the ItemStack that you get when craft the recipe. */
    private final ItemStack recipeOutput;
    public final List<ItemStack> recipeItems;

    public ShapelessCrystalRecipe(ItemStack output, List<ItemStack> inputList)
    {
        this.recipeOutput = output;
        this.recipeItems = inputList;
    }

    @Override
	public ItemStack getRecipeOutput()
    {
        return this.recipeOutput;
    }

    @Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
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
                        if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getMetadata() == 32767 || itemstack.getMetadata() == itemstack1.getMetadata()))
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

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return this.recipeOutput.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    @Override
	public int getRecipeSize()
    {
        return this.recipeItems.size();
    }
}
