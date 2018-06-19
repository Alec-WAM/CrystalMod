package alec_wam.CrystalMod.crafting.recipes;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.RecipeSorter;

public class ChisledBlockRecipe implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":chisledblock", ChisledBlockRecipe.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack output = ItemStackTools.getEmptyStack();
    private int pickSlot;
    
    public ChisledBlockRecipe() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack pick = ItemStackTools.getEmptyStack();
        ItemStack inputStack = ItemStackTools.getEmptyStack();
        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(ItemStackTools.isEmpty(slot))
                continue;

            // is it the tool?
            if(slot.getItem() == Items.IRON_PICKAXE || slot.getItem() == Items.DIAMOND_PICKAXE || slot.getItem() == Items.GOLDEN_PICKAXE){
            	if(ItemStackTools.isValid(pick))return false;
            	pick = slot;
            	pickSlot = i;
            }
            // otherwise.. input material
            else{
            	if(slot.getItem() == Item.getItemFromBlock(ModBlocks.crystal) && canChisle(slot)){
            		if(ItemStackTools.isEmpty(inputStack)) {
            			inputStack = ItemUtil.copy(slot, 1);
            		} else if(ItemUtil.canCombine(inputStack, slot)){
            			ItemStackTools.incStackSize(inputStack, 1);
            		} else {
            			return false;
            		}
            	} else {
            		return false;
            	}
            }
        }
        // no super torch found?
        if(ItemStackTools.isEmpty(pick) || ItemStackTools.getStackSize(inputStack) < 1)
            return false;
        
        int damageLeft = pick.getMaxDamage() - pick.getItemDamage();
        if(damageLeft < ItemStackTools.getStackSize(inputStack)) return false;
        
        
        int meta = -1;
        Block block = null;
        
        if(inputStack.getMetadata() == CrystalBlockType.BLUE.getMeta()){
        	block = ModBlocks.crystal;
        	meta = CrystalBlockType.BLUE_CHISELED.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.BLUE_CHISELED.getMeta()){
        	block = ModBlocks.crystalEtched;
        	meta = CrystalColors.Special.BLUE.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.RED.getMeta()){
        	block = ModBlocks.crystal;
        	meta = CrystalBlockType.RED_CHISELED.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.RED_CHISELED.getMeta()){
        	block = ModBlocks.crystalEtched;
        	meta = CrystalColors.Special.RED.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.GREEN.getMeta()){
        	block = ModBlocks.crystal;
        	meta = CrystalBlockType.GREEN_CHISELED.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.GREEN_CHISELED.getMeta()){
        	block = ModBlocks.crystalEtched;
        	meta = CrystalColors.Special.GREEN.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.DARK.getMeta()){
        	block = ModBlocks.crystal;
        	meta = CrystalBlockType.DARK_CHISELED.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.DARK_CHISELED.getMeta()){
        	block = ModBlocks.crystalEtched;
        	meta = CrystalColors.Special.DARK.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.PURE.getMeta()){
        	block = ModBlocks.crystal;
        	meta = CrystalBlockType.PURE_CHISELED.getMeta();
        }
        if(inputStack.getMetadata() == CrystalBlockType.PURE_CHISELED.getMeta()){
        	block = ModBlocks.crystalEtched;
        	meta = CrystalColors.Special.PURE.getMeta();
        }
        
        if(block !=null && meta > -1){
        	output = new ItemStack(block, ItemStackTools.getStackSize(inputStack), meta);
        	return true;
        }
        return false;
    }

    public boolean canChisle(ItemStack stack){
    	return stack.getItem() == Item.getItemFromBlock(ModBlocks.crystal) && stack.getMetadata() <= CrystalBlockType.PURE_CHISELED.getMeta();
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	return output;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++)
        {
        	if(i == pickSlot){
        		ItemStack pick = inv.getStackInSlot(i);
        		int current = pick.getItemDamage();
        		int count = ItemStackTools.getStackSize(output);
        		if(count > 0)pick.setItemDamage(current+count);
        		
        		if(pick.getItemDamage() < pick.getMaxDamage()){
    				inv.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
        			ret.set(i, pick);
        		}
        	}
        	else ret.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
        }
		return ret;
	}
}
