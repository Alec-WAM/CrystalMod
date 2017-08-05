package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alec_wam.CrystalMod.api.estorage.IAutoCrafter;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CraftingPattern {

	private IRecipe recipe;
    private IAutoCrafter crafter;
    private ItemStack pattern;
    private NonNullList<ItemStack> inputs = NonNullList.create();
    private List<NonNullList<ItemStack>> oreInputs = Lists.newArrayList();
    private NonNullList<ItemStack> outputs = NonNullList.create();
    private NonNullList<ItemStack> byproducts = NonNullList.create();
    
    //Credit way2muchnoise
    private boolean mekanism;

    
    public CraftingPattern(World world, IAutoCrafter crafter, ItemStack pattern){
    	this.crafter = crafter;
    	this.pattern = pattern;
        this.inputs = NonNullList.create();
        this.outputs = NonNullList.create();
        this.byproducts = NonNullList.create();
        
        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
        	ItemStack slot = ItemPattern.getInput(pattern, i);
            inputs.add(slot);
            inv.setInventorySlotContents(i, slot);
        }

        if (!ItemPattern.isProcessing(pattern)) {
        	
        	IRecipe rec = null;
        	
        	for(IRecipe listRecipe : CraftingManager.getInstance().getRecipeList()){
        		if(listRecipe.matches(inv, world)){
        			rec = listRecipe;
        			break;
        		}
        	}
        	
        	if(rec !=null){
        		recipe = rec;
        		ItemStack output = recipe.getCraftingResult(inv);
	
	            if (!ItemStackTools.isNullStack(output)) {
	                boolean shapedOre = recipe instanceof ShapedOreRecipe;
	                mekanism = recipe.getClass().getName().equals("mekanism.common.recipe.ShapedMekanismRecipe");
	                outputs.add(fixItemStack(output));
	                if (shapedOre || mekanism) {
                        Object[] inputs = new Object[0];
                        if (shapedOre) {
                            inputs = ((ShapedOreRecipe) recipe).getInput();
                        }
                        else {
                            try {
                                inputs = (Object[]) recipe.getClass().getMethod("getInput").invoke(recipe);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e){
                            	e.printStackTrace();
                            } catch(NoSuchMethodException e){
                            	e.printStackTrace();
                            }
                        }
                        for (Object input : inputs) {
                            if (input == null) {
                                oreInputs.add(NonNullList.create());
                            }
                            else {
	                            if (input instanceof ItemStack) {
	                            	ItemStack in = (ItemStack)input;
	                            	if(ItemStackTools.isEmpty(in)){
	                            		oreInputs.add(NonNullList.create());
	                            	} else {
	                            		oreInputs.add(NonNullList.withSize(1, fixItemStack(in)));
	                            	}
	                            } else if(input instanceof List){
	                            	NonNullList<ItemStack> cleaned = NonNullList.create();
	                            	for (ItemStack in : (List<ItemStack>) input) {
	                            		cleaned.add(fixItemStack(in));
	                            	}
	                                oreInputs.add(cleaned);
	                            }
                            }
                        }
                    }
	                
	                for (ItemStack remaining : recipe.getRemainingItems(inv)) {
	                    if (!ItemStackTools.isNullStack(remaining)) {
	                        byproducts.add(fixItemStack(remaining));
	                    }
	                }
	            }
        	}
        } else {
            outputs = ItemPattern.getOutputs(pattern);
            
            if(isOredict()){
            	 for (ItemStack input : inputs) {
            		 oreInputs.add(ItemUtil.getMatchingOreStacks(input));
            	 }
            }
        }
    	
        if (oreInputs.isEmpty()) {
            for (ItemStack input : inputs) {
                if (ItemStackTools.isNullStack(input)) {
                    oreInputs.add(NonNullList.create());
                } else {
                    oreInputs.add(NonNullList.withSize(1, input));
                }
            }
        }
    }

    public ItemStack getPatternStack(){
    	return pattern;
    }
    
    public IAutoCrafter getCrafter() {
        return crafter;
    }

    public boolean isProcessing() {
        return ItemPattern.isProcessing(pattern);
    }
    
    public boolean isOredict() {
        return ItemPattern.isOredict(pattern);
    }

    public NonNullList<ItemStack> getInputs() {
        return inputs;
    }

    public List<NonNullList<ItemStack>> getOreInputs() {
        return oreInputs;
    }
    
    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }
    
    public NonNullList<ItemStack> getOutputs(NonNullList<ItemStack> took) {
    	NonNullList<ItemStack> outputs = NonNullList.create();

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            if(i < took.size()){
            	ItemStack stack = took.get(i);
            	inv.setInventorySlotContents(i, stack);
            }
        }
        
        ItemStack output = CraftingManager.getInstance().findMatchingRecipe(inv, crafter.getWorld());
        outputs.add(output);

        return outputs;
    }

    public NonNullList<ItemStack> getByproducts() {
        return byproducts;
    }
    
    public NonNullList<ItemStack> getByproducts(NonNullList<ItemStack> took) {
    	NonNullList<ItemStack> byproducts = NonNullList.create();

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
        	if(i < took.size())inv.setInventorySlotContents(i, took.get(i));
        }

        for (ItemStack remaining : CraftingManager.getInstance().getRemainingItems(inv, crafter.getWorld())) {
            if (ItemStackTools.isValid(remaining)) {
                byproducts.add(fixItemStack(remaining));
            }
        }

        return byproducts;
    }
    
    public boolean isValid() {
        return !inputs.isEmpty() && !outputs.isEmpty();
    }
    
    public int getQuantityPerRequest(ItemStack requested, boolean ore) {
        int quantity = 0;

        for (ItemStack output : outputs) {
            if (ore ? ItemUtil.stackMatchUseOre(requested, output) : ItemUtil.canCombine(requested, output)) {
                quantity += ItemStackTools.getStackSize(output);

                if (!ItemPattern.isProcessing(pattern)) {
                    break;
                }
            }
        }

        return quantity;
    }
    
    public ItemStack getActualOutput(ItemStack requested, boolean ore){
    	for (ItemStack output : outputs) {
            if (ore ? ItemUtil.stackMatchUseOre(requested, output) : ItemUtil.canCombine(requested, output)) {
                return output.copy();
            }
        }

        return ItemStackTools.getEmptyStack();
    }
    
   public ItemStack fixItemStack(ItemStack output){
    	ItemStack out = output.copy();
    	//way2muchnoise
    	if (mekanism && out.hasTagCompound()) {
    		out.getTagCompound().removeTag("mekData");
    	}
    	if (out.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
    		out.setItemDamage(0);
    	}
    	return out;
    }
   
   @Override
   public boolean equals(Object obj){
	   if(obj == null || !(obj instanceof CraftingPattern))return false;
	   CraftingPattern other = ((CraftingPattern)obj);
	   if(!ItemUtil.canCombine(other.getPatternStack(), getPatternStack())) return false;
	   if(!other.getInputs().equals(getInputs()))return false;
	   if(!other.getOutputs().equals(getOutputs()))return false;
	   return true;
   }
}
