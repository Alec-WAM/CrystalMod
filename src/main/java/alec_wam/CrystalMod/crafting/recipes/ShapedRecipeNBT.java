package alec_wam.CrystalMod.crafting.recipes;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public class ShapedRecipeNBT extends ShapedRecipes {
	
	static {
		RecipeSorter.register(CrystalMod.resource("shapednbt"),  ShapedRecipeNBT.class,  SHAPED,  "after:minecraft:shaped");
	}
	
	public ShapedRecipeNBT(int width, int height, ItemStack[] p_i1917_3_, ItemStack output){
		super(width, height, p_i1917_3_, output);
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
    {
        for (int i = 0; i <= 3 - this.recipeWidth; ++i)
        {
            for (int j = 0; j <= 3 - this.recipeHeight; ++j)
            {
                if (this.checkMatch(inv, i, j, true))
                {
                    return true;
                }

                if (this.checkMatch(inv, i, j, false))
                {
                    return true;
                }
            }
        }

        return false;
    }
	
    public boolean checkMatch(InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_)
    {
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                int k = i - p_77573_2_;
                int l = j - p_77573_3_;
                ItemStack itemstack = null;

                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight)
                {
                    if (p_77573_4_)
                    {
                        itemstack = this.recipeItems[this.recipeWidth - k - 1 + l * this.recipeWidth];
                    }
                    else
                    {
                        itemstack = this.recipeItems[k + l * this.recipeWidth];
                    }
                }

                ItemStack itemstack1 = p_77573_1_.getStackInRowAndColumn(i, j);

                if (itemstack1 != null || itemstack != null)
                {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null)
                    {
                        return false;
                    }

                    if (itemstack.getItem() != itemstack1.getItem())
                    {
                        return false;
                    }

                    if (itemstack.getMetadata() != 32767 && itemstack.getMetadata() != itemstack1.getMetadata())
                    {
                        return false;
                    }
                    
                    if(!ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
                    {
                    	return false;
                    }
                }
            }
        }

        return true;
    }

}
