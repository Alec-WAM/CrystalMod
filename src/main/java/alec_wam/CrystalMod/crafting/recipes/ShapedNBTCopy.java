package alec_wam.CrystalMod.crafting.recipes;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.RecipeSorter;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ItemNBTHelper;

public class ShapedNBTCopy extends ShapedRecipes {

	static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":shapedNBTCopy", ShapedNBTCopy.class, RecipeSorter.Category.SHAPED, "");
    }
	
	private List<String> tags;
	
	public ShapedNBTCopy(int width, int height, ItemStack[] p_i1917_3_,	ItemStack output, List<String> tags) {
		super(width, height, p_i1917_3_, output);
		this.tags = tags;
	}
	
	public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack itemstack = this.getRecipeOutput().copy();
        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack1 = inv.getStackInSlot(i);

            if (itemstack1 != null && itemstack1.hasTagCompound())
            {
            	if(this.tags.isEmpty()){
            		itemstack.setTagCompound(itemstack1.getTagCompound().copy());
            		return itemstack;
            	}
            	NBTTagCompound nbt = (NBTTagCompound)itemstack1.getTagCompound().copy();
                for(String tag : this.tags){
                	if(nbt.hasKey(tag))
                	ItemNBTHelper.getCompound(itemstack).setTag(tag, nbt.getTag(tag));
                }
            }
        }

        return itemstack;
    }

}
