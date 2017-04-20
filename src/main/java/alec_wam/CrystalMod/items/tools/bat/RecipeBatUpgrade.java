package alec_wam.CrystalMod.items.tools.bat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.RecipeSorter;

public class RecipeBatUpgrade implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(CrystalMod.MODID.toLowerCase() + ":batupgrade", RecipeBatUpgrade.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack modifiedTool = ItemStackTools.getEmptyStack();

    public RecipeBatUpgrade() {
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack tool = ItemStackTools.getEmptyStack();
        ItemStack[] input = new ItemStack[inventoryCrafting.getSizeInventory()];
        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(ItemStackTools.isNullStack(slot))
                continue;

            // is it the tool?
            if(slot.getItem() instanceof ItemBat){
                tool = slot;
            }
            // otherwise.. input material
            else
                input[i] = slot;
        }
        // no tool found?
        if(ItemStackTools.isNullStack(tool))
            return false;
        
        ItemStack bat = tool.copy();
        
        if(!whiteList(input)){
        	return false;
        }
        
        // check if applicable, and save result for later
        if(upgrade(input, bat)) {
            modifiedTool = bat.copy();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
    	return modifiedTool;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return modifiedTool;
    }
    
    public boolean whiteList(ItemStack[] items){
    	boolean foundItem = false;
    	for(int s = 0; s < items.length; s++){
    		if(ItemStackTools.isNullStack(items[s])){
    			continue;
    		}else{
    			foundItem = true;
    			break;
    		}
    	}
    	if(!foundItem)return false;
    	
    	for(int i = 0; i < items.length; i++){
    		if(ItemStackTools.isNullStack(items[i]) || items[i] !=null && BatHelper.isIngrediant(items[i]))continue;
    		return false;
    	}
    	
    	return true;
    }
    
    public boolean upgrade(ItemStack[] items, ItemStack bat){
    	
    	List<IBatUpgrade> upgrades = BatHelper.getUpgradesFromItems(items);
    	if(upgrades.isEmpty())return false;
    	
    	Map<IBatUpgrade, UpgradeData> retData = Maps.newHashMap();
    	
    	for(IBatUpgrade upgrade : upgrades){
    		UpgradeData data = upgrade.handleUpgrade(bat, items);
    		if(data !=null){
    			retData.put(upgrade, data);
    		}
    	}
    	
    	List<IBatUpgrade> allUpgrades = new ArrayList<IBatUpgrade>(BatHelper.getBatUpgrades(bat).keySet());
    	
    	for(IBatUpgrade u : upgrades){
    		if(!allUpgrades.contains(u)){
    			allUpgrades.add(u);
    		}
    	}
    	
    	
    	for(Entry<IBatUpgrade, UpgradeData> entry : retData.entrySet()){
    		IBatUpgrade upgrade = entry.getKey();
    		UpgradeData data = entry.getValue();
    		UpgradeData existing = BatHelper.getUpgradeData(BatHelper.getBatUpgradeData(bat), data);
    		int amount = existing == null ? 0 : existing.getAmount();
    		
    		if(!upgrade.canBeAdded(bat, allUpgrades, data))return false;
    		amount+=data.getAmount();
    		if(amount > getMaxItems(upgrade)) return false;
    		
    		UpgradeData finalData = new UpgradeData(data.getUpgradeID(), amount);
    		BatHelper.setBatUpgrade(bat, finalData);
    		upgrade.afterUpgradeAdded(bat, items, data);
    	}
    	/*if (newLapis >= BatHelper.MLL)
        addEnchantment(bat, Enchantment.looting, 3);
    else if (newLapis >= BatHelper.LPL *2)
        addEnchantment(bat, Enchantment.looting, 2);
    else if (newLapis >= BatHelper.LPL *1)
        addEnchantment(bat, Enchantment.looting, 1);*/
    	return true;
    }
    
    
    public int getMaxItems(IBatUpgrade upgrade){
    	return upgrade.getItemsPerLevel() * upgrade.getMaxLevel();
    }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
