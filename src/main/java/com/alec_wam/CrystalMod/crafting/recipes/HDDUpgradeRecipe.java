package com.alec_wam.CrystalMod.crafting.recipes;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public class HDDUpgradeRecipe implements IRecipe {
	
    static {
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":hddupgrade", HDDUpgradeRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
    }

    private ItemStack modifiedTool = null;

    public HDDUpgradeRecipe() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        if(inventoryCrafting.getSizeInventory() < 9)
            return false;

        
        
        if(inventoryCrafting.getStackInSlot(4) !=null && inventoryCrafting.getStackInSlot(4).getItem() instanceof ItemHDD){
        	ItemStack hdd = inventoryCrafting.getStackInSlot(4).copy();
        	boolean crystal0 = inventoryCrafting.getStackInSlot(0) !=null && inventoryCrafting.getStackInSlot(0).getItem() == ModItems.ingots;
        	boolean crystal1 = inventoryCrafting.getStackInSlot(1) !=null && inventoryCrafting.getStackInSlot(1).getItem() == ModItems.ingots;
        	boolean crystal2 = inventoryCrafting.getStackInSlot(2) !=null && inventoryCrafting.getStackInSlot(2).getItem() == ModItems.ingots;
        	boolean plate3 = inventoryCrafting.getStackInSlot(3) !=null && inventoryCrafting.getStackInSlot(3).getItem() == Item.getItemFromBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        	boolean plate5 = inventoryCrafting.getStackInSlot(5) !=null && inventoryCrafting.getStackInSlot(5).getItem() == Item.getItemFromBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        	boolean crystal6 = inventoryCrafting.getStackInSlot(6) !=null && inventoryCrafting.getStackInSlot(6).getItem() == ModItems.ingots;
        	boolean crystal7 = inventoryCrafting.getStackInSlot(7) !=null && inventoryCrafting.getStackInSlot(7).getItem() == ModItems.ingots;
        	boolean crystal8 = inventoryCrafting.getStackInSlot(8) !=null && inventoryCrafting.getStackInSlot(8).getItem() == ModItems.ingots;
        	if(crystal0 && crystal1 && crystal2 && plate3 && plate5 && crystal6 && crystal7 && crystal8){
        		int meta = inventoryCrafting.getStackInSlot(0).getMetadata();
        		for(int slot : new int[]{1, 2, 6, 7, 8}){
        			if(inventoryCrafting.getStackInSlot(slot).getMetadata() !=meta){
        				return false;
        			}
        		}
        		if(meta == 1 && hdd.getMetadata() == 0){
        			hdd.setItemDamage(1);
                	ItemNBTHelper.setInteger(hdd, ItemHDD.NBT_ITEM_LIMIT, ItemHDD.getSizes()[1]);
                	modifiedTool = hdd.copy();
                    return true;
        		}
        		if(meta == 2 && hdd.getMetadata() == 1){
        			hdd.setItemDamage(2);
                	ItemNBTHelper.setInteger(hdd, ItemHDD.NBT_ITEM_LIMIT, ItemHDD.getSizes()[2]);
                	modifiedTool = hdd.copy();
                    return true;
        		}
        		if(meta == 3 && hdd.getMetadata() == 2){
        			hdd.setItemDamage(3);
                	ItemNBTHelper.setInteger(hdd, ItemHDD.NBT_ITEM_LIMIT, ItemHDD.getSizes()[3]);
                	modifiedTool = hdd.copy();
                    return true;
        		}
        		if(meta == 4 && hdd.getMetadata() == 3){
        			hdd.setItemDamage(4);
                	ItemNBTHelper.setInteger(hdd, ItemHDD.NBT_ITEM_LIMIT, ItemHDD.getSizes()[4]);
                	modifiedTool = hdd.copy();
                    return true;
        		}
        	}else{
        		return false;
        	}
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	return modifiedTool;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return modifiedTool;
    }

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}
}
