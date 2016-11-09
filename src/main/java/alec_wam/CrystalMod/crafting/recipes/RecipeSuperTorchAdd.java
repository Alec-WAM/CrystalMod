package alec_wam.CrystalMod.crafting.recipes;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.tools.ItemSuperTorch;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public class RecipeSuperTorchAdd implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":supertorchadd", RecipeSuperTorchAdd.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack modifiedTorch = null;

    public RecipeSuperTorchAdd() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack storch = null;
        int inputTorchCount = 0;
        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(slot == null)
                continue;

            // is it the tool?
            if(slot.getItem() instanceof ItemSuperTorch){
            	if(storch !=null)return false;
                storch = slot;
            }
            // otherwise.. input material
            else{
            	if(slot.getItem() == Item.getItemFromBlock(Blocks.TORCH)){
            		inputTorchCount++;
            	} else {
            		return false;
            	}
            }
        }
        // no super torch found?
        if(storch == null || inputTorchCount < 1)
            return false;
        
        ItemStack torch = storch.copy();
        
        int torchCount = ItemNBTHelper.getInteger(torch, ItemSuperTorch.NBT_TORCH_COUNT, 0);
        
        torchCount+=inputTorchCount;
        
        if(torchCount > Config.superTorchMaxCount){
        	return false;
        }
        
        ItemNBTHelper.setInteger(torch, ItemSuperTorch.NBT_TORCH_COUNT, torchCount);
        
        modifiedTorch = torch;
        
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	return modifiedTorch;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return modifiedTorch;
    }

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}
}
