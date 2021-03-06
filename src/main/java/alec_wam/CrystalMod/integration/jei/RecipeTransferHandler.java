package alec_wam.CrystalMod.integration.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketRecipeTransfer;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class RecipeTransferHandler implements IRecipeTransferHandler<ContainerPanelCrafting> {

	private IModRegistry registry;
	public RecipeTransferHandler(IModRegistry registry){
		this.registry = registry;
	}
	
	@Override
	public Class<ContainerPanelCrafting> getContainerClass() {
		return ContainerPanelCrafting.class;
	}

	@Override
	public IRecipeTransferError transferRecipe(ContainerPanelCrafting container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer,
			boolean doTransfer) {
		ContainerPanelCrafting panelContainer = (ContainerPanelCrafting) container;
	    if(doTransfer) {
	      if(panelContainer.panel instanceof TileEntityPanelCrafting)((TileEntityPanelCrafting)panelContainer.panel).clearGrid();
	    }
		
	    EStorageNetwork network = panelContainer.getNetwork();
	    
	    if(network == null){
	    	return registry.getJeiHelpers().recipeTransferHandlerHelper().createInternalError();
	    }
	    
	    List<Integer> missingItemSlots = new ArrayList<Integer>();
	    ItemStack[][] ingredients = new ItemStack[9][];

	    IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
	    Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();
	    //Map<ItemStack, Integer> stacks = new HashMap<ItemStack, Integer>();
	    Map<ItemStack, Integer> stacksPlayer = new HashMap<ItemStack, Integer>();
	    Map<ItemStack, Integer> stacksNet = new HashMap<ItemStack, Integer>();
	    for (int i = 0; i < 9; i++) {
	      if (guiIngredients.containsKey(i + 1)) {
	        List<ItemStack> allIng = guiIngredients.get(i + 1).getAllIngredients();
	        if (!allIng.isEmpty()) {
	          boolean passPlayer = false;
	          
	          
	          ingd : for(ItemStack s : allIng){
	        		int slot = player.inventory.getSlotFor(s);
	        		if(slot == -1)continue ingd;
	        		
	        		ItemStack stack = player.inventory.getStackInSlot(slot);
	        		
	        		int added = 0;
	        		ItemStack id = s;
	        		ck : for(ItemStack s2 : stacksPlayer.keySet()){
	        			if(ItemUtil.canCombine(s, s2)){
	        				id = s2;
	        				added = stacksPlayer.get(s2);
	        				break ck;
	        			}
	        		}
	        		
	        		
	        		
    	      		if (ItemStackTools.isValid(stack) && ItemStackTools.getStackSize(stack) >=ItemStackTools.getStackSize(s)+added) {
    	      			ingredients[i] = allIng.toArray(new ItemStack[allIng.size()]);
    	      			stacksPlayer.put(id, ItemStackTools.getStackSize(s)+added);
    	      			passPlayer = true;
    	      			break ingd;
    	      		}
	        	}
	          
	          if (passPlayer) {
	            ingredients[i] = allIng.toArray(new ItemStack[allIng.size()]);          
	          } else {
	        	boolean passNetwork = false;  
	        	
	        	ingd : for(ItemStack s : allIng){
	        		ItemStackData stack = network.getItemStorage().getItemData(s);
	        		
	        		int added = 0;
	        		ItemStack id = s;
	        		ck : for(ItemStack s2 : stacksNet.keySet()){
	        			if(ItemUtil.canCombine(s, s2)){
	        				id = s2;
	        				added = stacksNet.get(s2);
	        				break ck;
	        			}
	        		}
	        		
      	      		if (stack != null && stack.stack !=null && !stack.isCrafting && stack.getAmount() >=ItemStackTools.getStackSize(s)+added) {
      	      			ingredients[i] = allIng.toArray(new ItemStack[allIng.size()]);
      	      			stacksNet.put(id, ItemStackTools.getStackSize(s)+added);
      	      			passNetwork = true;
      	      			break ingd;
      	      		}
	        	}
	        	  
	            if(!passNetwork)missingItemSlots.add(i + 1);
	          }
	        }
	      }

	    }
	    if(missingItemSlots.isEmpty()) {
	      if(doTransfer) {
	    	  NBTTagCompound recipe = new NBTTagCompound();
	    	  for(int i = 0; i < 9; i++){
	    		  ItemStack[] ingredient = ingredients[i];
	    		  if (ingredient != null) {
	    			  NBTTagList tags = new NBTTagList();
	    			  for (ItemStack is : ingredient) {
	    				  NBTTagCompound tag = new NBTTagCompound();
	    				  is.writeToNBT(tag);
	    				  tags.appendTag(tag);
	    			  }
	    			  recipe.setTag("#" + i, tags);
	    		  }
	    	  }
	    	  recipe.setBoolean("MaxTransfer", maxTransfer);
	    	  CrystalModNetwork.sendToServer(new PacketRecipeTransfer(recipe));
	      }      
	      return null;
	    }
	            
	    return registry.getJeiHelpers().recipeTransferHandlerHelper().createUserErrorForSlots(Lang.translateToLocal("jei.tooltip.error.recipe.transfer.missing"), missingItemSlots);
	}
	
	public static void transferItems(ContainerPanelCrafting con, TileEntityPanelCrafting panel, NBTTagCompound recipeNBT){
		panel.clearGrid();
		
		ItemStack[][] actualRecipe = new ItemStack[9][];

        for (int x = 0; x < actualRecipe.length; x++) {
            NBTTagList list = recipeNBT.getTagList("#" + x, Constants.NBT.TAG_COMPOUND);

            if (list.tagCount() > 0) {
                actualRecipe[x] = new ItemStack[list.tagCount()];

                for (int y = 0; y < list.tagCount(); y++) {
                    actualRecipe[x][y] = ItemStackTools.loadFromNBT(list.getCompoundTagAt(y));
                }
            }
        }
        
        int validRecipeInputs = 0;
        for (int i = 0; i < actualRecipe.length; ++i) {
            if (actualRecipe[i] != null) {
            	validRecipeInputs++;
            }
        }
        
        boolean max = recipeNBT.getBoolean("MaxTransfer");
        List<Integer> failedSlots = Lists.newArrayList();
        if (panel.getNetwork() !=null) {
        	int tries = max ? 64 : 1;
        	trys : for(int t = 0; t < tries; t++){
        		
	            slot : for (int i = 0; i < actualRecipe.length; ++i) {
	                if (actualRecipe[i] !=null) {
	                	if(failedSlots.contains(i))continue slot;
	                    ItemStack[] possibilities = actualRecipe[i];
	                    for (int i2 = 0; i2 < possibilities.length; ++i2) {
	                    	ItemStack poss = possibilities[i2];
	                    	if (ItemStackTools.isValid(poss)) {
		                    	ItemStack copy = poss.copy();
		                        ItemStack took = ItemUtil.removeFromPlayerInventory(con, copy);
		                        if(ItemStackTools.isNullStack(took)){
		                        	ItemStack ret = panel.getNetwork().getItemStorage().removeItem(copy, false);
		                        	if(ItemStackTools.isValid(ret)){
		                        		took = ret;
		                        	}
		                        }else if(ItemStackTools.getStackSize(took) < ItemStackTools.getStackSize(poss)){
		                        	ItemStack ret = panel.getNetwork().getItemStorage().removeItem(ItemUtil.copy(copy, ItemStackTools.getStackSize(poss)-ItemStackTools.getStackSize(took)), false);
		                        	if(ItemStackTools.isValid(ret)){
		                        		ItemStackTools.incStackSize(took, ItemStackTools.getStackSize(ret));
		                        	}
		                        }
		
		                        if (ItemStackTools.isValid(took)) {
		                        	if(ItemStackTools.isNullStack(panel.getMatrix().getStackInSlot(i))){
		                        		panel.getMatrix().setInventorySlotContents(i, took);
		                        	}else{
		                        		ItemStackTools.incStackSize(panel.getMatrix().getStackInSlot(i), ItemStackTools.getStackSize(took));
		                        	}
		                            continue slot;
		                        }
	                    	}
	                    }
	                    failedSlots.add(i);
	                }
	            }
    			if(failedSlots.size() >= validRecipeInputs){
    				break trys;
    			}
        	}
        }
	}
}
