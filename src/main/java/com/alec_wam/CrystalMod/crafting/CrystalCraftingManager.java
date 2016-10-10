package com.alec_wam.CrystalMod.crafting;

import com.alec_wam.CrystalMod.api.crafting.ICrystalRecipe;
import com.alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import com.alec_wam.CrystalMod.items.ItemIngot.IngotType;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.items.tools.ItemToolParts.PartType;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class CrystalCraftingManager
{
    /** The static instance of this class */
    private static final CrystalCraftingManager instance = new CrystalCraftingManager();
    private final List<ICrystalRecipe> recipes = Lists.<ICrystalRecipe>newArrayList();

    /**
     * Returns the static instance of this class
     */
    public static CrystalCraftingManager getInstance()
    {
        /** The static instance of this class */
        return instance;
    }

    private CrystalCraftingManager()
    {
    	ItemStack toolRod = new ItemStack(ModItems.toolParts);
    	ItemNBTHelper.setString(toolRod, "Type", PartType.ROD.getName());
    	for(PartType part : PartType.values()){
        	if(part.colored){
        		for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
        			ItemStack stack = new ItemStack(ModItems.toolParts);
                	ItemNBTHelper.setString(stack, "Type", part.getName()+(part.colored ? "_head" : ""));
        			ItemNBTHelper.setString(stack, "Color", color);
        			ItemStack crystal = new ItemStack(ModItems.crystals, 1, color == "blue" ? CrystalType.BLUE.getMetadata() : color == "red" ? CrystalType.RED.getMetadata() : color == "green" ? CrystalType.GREEN.getMetadata() : color == "dark" ? CrystalType.DARK.getMetadata() : CrystalType.PURE.getMetadata());
        			ItemStack ingot = new ItemStack(ModItems.ingots, 1, color == "blue" ? IngotType.BLUE.getMetadata() : color == "red" ? IngotType.RED.getMetadata() : color == "green" ? IngotType.GREEN.getMetadata() : color == "dark" ? IngotType.DARK.getMetadata() : IngotType.PURE.getMetadata());
        			
        			
        			if(part == PartType.AXE)this.addRecipe(stack, new Object[] {"###","#X ", '#', ingot, 'X', crystal});
        			else if(part == PartType.HOE)this.addRecipe(stack, new Object[] {"###","X  ", '#', ingot, 'X', crystal});
        	    	else if(part == PartType.SHOVEL)this.addRecipe(stack, new Object[] {" # ","X X","# #", '#', ingot, 'X', crystal});
        	    	else if(part == PartType.PICK)this.addRecipe(stack, new Object[] {"###","X X", '#', ingot, 'X', crystal});
        	    	else if(part == PartType.SWORD)this.addRecipe(stack, new Object[] {"##", "XX","##", '#', ingot, 'X', crystal});
        		}
        	}else{
        		ItemStack stack = new ItemStack(ModItems.toolParts);
            	ItemNBTHelper.setString(stack, "Type", part.getName());
        		if(part == PartType.ROD)this.addRecipe(new ShapedOreCrystalRecipe(stack, new Object[] {"  #"," X ", "#  ", '#', "ingotCrystal", 'X', "gemCrystal"}));
        		else if(part == PartType.COVER)this.addRecipe(new ShapedOreCrystalRecipe(stack, new Object[] {"#X#"," # ", '#', "ingotCrystal", 'X', "gemCrystal"}));
        	}
        }
    	
    	for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
    		ItemStack axeHead = new ItemStack(ModItems.toolParts);
        	ItemNBTHelper.setString(axeHead, "Type", PartType.AXE.getName()+("_head"));
        	ItemNBTHelper.setString(axeHead, "Color", color);
        	
        	ItemStack axe = new ItemStack(ModItems.crystalAxe);
        	ItemNBTHelper.setString(axe, "Color", color);
        	
        	this.addRecipeNBT(axe, new Object[]{"  #", " R ", "R  ", '#', axeHead, 'R', toolRod});
        	
        	ItemStack hoeHead = new ItemStack(ModItems.toolParts);
        	ItemNBTHelper.setString(hoeHead, "Type", PartType.HOE.getName()+("_head"));
        	ItemNBTHelper.setString(hoeHead, "Color", color);
        	
        	ItemStack hoe = new ItemStack(ModItems.crystalHoe);
        	ItemNBTHelper.setString(hoe, "Color", color);
        	
        	this.addRecipeNBT(hoe, new Object[]{"  #", " R ", "R  ", '#', hoeHead, 'R', toolRod});
        	
        	ItemStack shovelHead = new ItemStack(ModItems.toolParts);
        	ItemNBTHelper.setString(shovelHead, "Type", PartType.SHOVEL.getName()+("_head"));
        	ItemNBTHelper.setString(shovelHead, "Color", color);
        	
        	ItemStack shovel = new ItemStack(ModItems.crystalShovel);
        	ItemNBTHelper.setString(shovel, "Color", color);
        	
        	this.addRecipeNBT(shovel, new Object[]{"  #", " R ", "R  ", '#', shovelHead, 'R', toolRod});
        	
        	ItemStack pickHead = new ItemStack(ModItems.toolParts);
        	ItemNBTHelper.setString(pickHead, "Type", PartType.PICK.getName()+("_head"));
        	ItemNBTHelper.setString(pickHead, "Color", color);
        	
        	ItemStack pick = new ItemStack(ModItems.crystalPickaxe);
        	ItemNBTHelper.setString(pick, "Color", color);
        	
        	this.addRecipeNBT(pick, new Object[]{"  #", " R ", "R  ", '#', pickHead, 'R', toolRod});
        	
        	ItemStack swordHead = new ItemStack(ModItems.toolParts);
        	ItemNBTHelper.setString(swordHead, "Type", PartType.SWORD.getName()+("_head"));
        	ItemNBTHelper.setString(swordHead, "Color", color);
        	
        	ItemStack cover = new ItemStack(ModItems.toolParts);
        	ItemNBTHelper.setString(cover, "Type", PartType.COVER.getName());
        	
        	ItemStack sword = new ItemStack(ModItems.crystalSword);
        	ItemNBTHelper.setString(sword, "Color", color);
        	
        	this.addRecipeNBT(sword, new Object[]{"  #", " C ", "R  ", '#', swordHead, 'C', cover, 'R', toolRod});
    	}
    	
    	this.addRecipe(new ShapedOreCrystalRecipe(new ItemStack(ModItems.wrench), new Object[] {"# #"," R "," R ", '#', "ingotCrystal", 'R', "gemCrystal"}));
    	
        Collections.sort(this.recipes, new Comparator<ICrystalRecipe>()
        {
            public int compare(ICrystalRecipe p_compare_1_, ICrystalRecipe p_compare_2_)
            {
                return p_compare_1_ instanceof ShapelessCrystalRecipe && p_compare_2_ instanceof ShapedCrystalRecipe ? 1 : (p_compare_2_ instanceof ShapelessCrystalRecipe && p_compare_1_ instanceof ShapedCrystalRecipe ? -1 : (p_compare_2_.getRecipeSize() < p_compare_1_.getRecipeSize() ? -1 : (p_compare_2_.getRecipeSize() > p_compare_1_.getRecipeSize() ? 1 : 0)));
            }
        });
    }

    public ShapedNBTRecipe addRecipeNBT(ItemStack stack, Object... recipeComponents)
    {
    	String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = (String[])((String[])recipeComponents[i++]);

            for (int l = 0; l < astring.length; ++l)
            {
                String s2 = astring[l];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1)
        {
            char c0 = s.charAt(i1);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[i1] = ((ItemStack)map.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[i1] = null;
            }
        }

        ShapedNBTRecipe shapedrecipes = new ShapedNBTRecipe(j, k, aitemstack, stack);
        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }
    
    /**
     * Adds a shaped recipe to the games recipe list.
     */
    public ShapedCrystalRecipe addRecipe(ItemStack stack, Object... recipeComponents)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = (String[])((String[])recipeComponents[i++]);

            for (int l = 0; l < astring.length; ++l)
            {
                String s2 = astring[l];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1)
        {
            char c0 = s.charAt(i1);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[i1] = ((ItemStack)map.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[i1] = null;
            }
        }

        ShapedCrystalRecipe shapedrecipes = new ShapedCrystalRecipe(j, k, aitemstack, stack);
        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    /**
     * Adds a shapeless crafting recipe to the the game.
     */
    public void addShapelessRecipe(ItemStack stack, Object... recipeComponents)
    {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (Object object : recipeComponents)
        {
            if (object instanceof ItemStack)
            {
                list.add(((ItemStack)object).copy());
            }
            else if (object instanceof Item)
            {
                list.add(new ItemStack((Item)object));
            }
            else
            {
                if (!(object instanceof Block))
                {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                list.add(new ItemStack((Block)object));
            }
        }

        this.recipes.add(new ShapelessCrystalRecipe(stack, list));
    }

    /**
     * Adds an IRecipe to the list of crafting recipes.
     */
    public void addRecipe(ICrystalRecipe recipe)
    {
        this.recipes.add(recipe);
    }

    /**
     * Retrieves an ItemStack that has multiple recipes for it.
     */
    public ItemStack findMatchingRecipe(InventoryCrafting p_82787_1_, World worldIn)
    {
        for (ICrystalRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(p_82787_1_, worldIn))
            {
                return irecipe.getCraftingResult(p_82787_1_);
            }
        }
        
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList())
        {
            if (irecipe.matches(p_82787_1_, worldIn))
            {
                return irecipe.getCraftingResult(p_82787_1_);
            }
        }

        return null;
    }

    public ItemStack[] func_180303_b(InventoryCrafting p_180303_1_, World worldIn)
    {
        for (ICrystalRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(p_180303_1_, worldIn))
            {
                return irecipe.getRemainingItems(p_180303_1_);
            }
        }
        
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList())
        {
            if (irecipe.matches(p_180303_1_, worldIn))
            {
                return irecipe.getRemainingItems(p_180303_1_);
            }
        }

        ItemStack[] aitemstack = new ItemStack[p_180303_1_.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            aitemstack[i] = p_180303_1_.getStackInSlot(i);
        }

        return aitemstack;
    }

    public List<ICrystalRecipe> getRecipeList()
    {
        return this.recipes;
    }
}