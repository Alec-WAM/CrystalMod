package alec_wam.CrystalMod.tiles.pipes.covers;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.RecipeSorter;

public class RecipePipeCover implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":pipecover", RecipePipeCover.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack output = ItemStackTools.getEmptyStack();
    
    public RecipePipeCover() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack block = ItemStackTools.getEmptyStack();
        boolean hasSlime = false;
        boolean hasPlate = false;
        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(ItemStackTools.isEmpty(slot))
                continue;

            // is it the tool?
            if(slot.getItem() instanceof ItemBlock){
            	if(!ItemPipeCover.isItemValidForCover(slot) || ItemStackTools.isValid(block))return false;
            	block = slot;
            }
            // otherwise.. input material
            else{
            	if(slot.getItem() == ModItems.plates && slot.getMetadata() == PlateType.DARK_IRON.getMeta() && !hasPlate){
            		hasPlate = true;
            	} 
            	else if(slot.getItem() == Items.SLIME_BALL && !hasSlime){
            		hasSlime = true;
            	}
            	else {
            		return false;
            	}
            }
        }
        return ItemStackTools.isValid(block) && hasPlate && hasSlime;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	ItemStack block = ItemStackTools.getEmptyStack();
        boolean hasSlime = false;
        boolean hasPlate = false;
        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(ItemStackTools.isEmpty(slot))
                continue;

            // is it the tool?
            if(slot.getItem() instanceof ItemBlock){
            	if(!ItemPipeCover.isItemValidForCover(slot) || ItemStackTools.isValid(block)){
            		output = ItemStackTools.getEmptyStack();
            		return output;
            	}
            	block = slot;
            }
            // otherwise.. input material
            else{
            	if(slot.getItem() == ModItems.plates && slot.getMetadata() == PlateType.DARK_IRON.getMeta() && !hasPlate){
            		hasPlate = true;
            	} 
            	else if(slot.getItem() == Items.SLIME_BALL && !hasSlime){
            		hasSlime = true;
            	}
            	else {
            		output = ItemStackTools.getEmptyStack();
            		return output;
            	}
            }
        }
        return ItemUtil.copy(ItemPipeCover.getCoverFromItem(block), 6);
    }

    @Override
    public int getRecipeSize() {
        return 3;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
