package com.alec_wam.CrystalMod.crafting.recipes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.items.ItemDragonWings;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.items.ItemIngot.IngotType;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.alec_wam.CrystalMod.util.ModLogger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public class UpgradeItemRecipe implements IRecipe {
	
    static {
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":itemupgrade", UpgradeItemRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    private ItemStack modifiedItem = null;
    private Map<Integer, ItemStack> byproducts = Maps.newHashMap();
    
    public UpgradeItemRecipe() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
    	return updateRecipeData(inventoryCrafting, true);
    }
    
    public boolean emptyExcept(InventoryCrafting inventoryCrafting, List<Integer> ignore){
    	for(int s = 0; s < inventoryCrafting.getSizeInventory(); s++){
    		ItemStack stack = inventoryCrafting.getStackInSlot(s);
    		if(stack !=null){
    			if(ignore.contains(s))continue;
    			return false;
    		}
    	}
    	return true;
    }
    
    public SlotStack findStack(ItemStack stack, InventoryCrafting inventoryCrafting, boolean ore){
    	for(int s = 0; s < inventoryCrafting.getSizeInventory(); s++){
    		ItemStack stackI = inventoryCrafting.getStackInSlot(s);
    		if(stackI !=null){
    			if(ore ? ItemUtil.stackMatchUseOre(stack, stackI) : ItemUtil.canCombine(stack, stackI)){
    				return new SlotStack(stackI, s);
    			}
    		}
    	}
    	return null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	updateRecipeData(inventoryCrafting, false);
    	return modifiedItem;
    }
    
    public boolean updateRecipeData(InventoryCrafting inventoryCrafting, boolean justCheck){

    	SlotStack chestPlate = null;
    	SlotStack armorSlot = null;
    	
    	for(int s = 0; s < inventoryCrafting.getSizeInventory(); s++){
    		ItemStack stack = inventoryCrafting.getStackInSlot(s);
    		if(stack !=null){
    			Item item = stack.getItem();
    			if(item !=null){
    				if(item instanceof ItemArmor){
    					ItemArmor armor = (ItemArmor)item;
    					if(armor.armorType == EntityEquipmentSlot.CHEST){
    						chestPlate = new SlotStack(stack, s);
    					}
    					armorSlot = new SlotStack(stack, s);
    					break;
    				}
    			}
    		}
    	}
    	
    	
    	
    	if(armorSlot !=null){
    		String NBT_INVISIBLE = "CrystalMod.InvisArmor";
    		boolean invis = ItemNBTHelper.verifyExistance(armorSlot.getStack(), NBT_INVISIBLE);
    		if(invis){
    			List<Integer> ignore = Lists.newArrayList();
    			ignore.add(armorSlot.getSlot());
    			if(emptyExcept(inventoryCrafting, ignore)){
    				if(!justCheck){
	    				ItemStack copy = armorSlot.getStack().copy();
		    			ItemNBTHelper.getCompound(copy).removeTag(NBT_INVISIBLE);
		    			
		    			modifiedItem = copy;
    				}
    				byproducts.put(0, new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata()));
	    			byproducts.put(1, new ItemStack(Items.GOLDEN_CARROT));
	    			return true;
    			}
    		} else {
    			SlotStack slotIngot = findStack(new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata()), inventoryCrafting, false);
    			SlotStack slotCarrot = findStack(new ItemStack(Items.GOLDEN_CARROT), inventoryCrafting, false);
    			if(slotIngot !=null && slotCarrot !=null){
    				List<Integer> ignore = Lists.newArrayList();
    				ignore.add(armorSlot.getSlot());
        			ignore.add(slotIngot.getSlot());
        			ignore.add(slotCarrot.getSlot());
        			if(emptyExcept(inventoryCrafting, ignore)){
        				if(!justCheck){
	        				ItemStack copy = armorSlot.getStack().copy();
		        			ItemNBTHelper.setBoolean(copy, NBT_INVISIBLE, true);
		        			modifiedItem = copy;
        				}
	        			return true;
        			}
    			}
    		}
    	}
    	
    	if(chestPlate !=null){
    		boolean wings = ItemNBTHelper.verifyExistance(chestPlate.getStack(), ItemDragonWings.UPGRADE_NBT);
    		if(wings){
    			List<Integer> ignore = Lists.newArrayList();
    			ignore.add(chestPlate.getSlot());
    			if(emptyExcept(inventoryCrafting, ignore)){
    				if(!justCheck){
	    				ItemStack copy = chestPlate.getStack().copy();
		    			ItemNBTHelper.getCompound(copy).removeTag(ItemDragonWings.UPGRADE_NBT);
		    			
		    			modifiedItem = copy;
    				}
    				byproducts.put(0, new ItemStack(ModItems.wings));
	    			return true;
    			}
    		} else {
    			SlotStack slot = findStack(new ItemStack(ModItems.wings), inventoryCrafting, false);
    			if(slot !=null){
    				List<Integer> ignore = Lists.newArrayList();
    				ignore.add(chestPlate.getSlot());
        			ignore.add(slot.getSlot());
        			if(emptyExcept(inventoryCrafting, ignore)){
	        			if(!justCheck){
	        				ItemStack copy = chestPlate.getStack().copy();
	        				ItemNBTHelper.setBoolean(copy, ItemDragonWings.UPGRADE_NBT, true);
	        				modifiedItem = copy;
	        			}
	        			return true;
        			}
    			}
    		}
    	}
    	return false;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return modifiedItem;
    }

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] array = new ItemStack[inv.getSizeInventory()];
		for(Entry<Integer, ItemStack> entry : byproducts.entrySet()){
			int slot = entry.getKey();
			ItemStack stack = entry.getValue();
			if(slot >= 0 && slot < array.length){
				array[slot] = stack.copy();
			}
		}
		byproducts.clear();
		return array;
	}
	
	public class SlotStack{
		private ItemStack stack;
		private int slot;
		
		public SlotStack(ItemStack stack, int slot){
			this.stack = stack;
			this.slot = slot;
		}
		
		public ItemStack getStack(){
			return stack;
		}
		
		public int getSlot(){
			return slot;
		}
	}
}
