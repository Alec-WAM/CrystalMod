package alec_wam.CrystalMod.integration.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketRecipeTransfer;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import com.google.common.collect.Lists;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.util.StackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class RecipeTransferHandler implements IRecipeTransferHandler {

	private IModRegistry registry;
	public RecipeTransferHandler(IModRegistry registry){
		this.registry = registry;
	}
	
	@Override
	public Class<? extends Container> getContainerClass() {
		return ContainerPanelCrafting.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer,
			boolean doTransfer) {
		if (! (container instanceof ContainerPanelCrafting)) {
		      return registry.getJeiHelpers().recipeTransferHandlerHelper().createInternalError();
	    }
		
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
	        		
	        		
	        		
    	      		if (stack != null && stack.stackSize >=s.stackSize+added) {
    	      			ingredients[i] = allIng.toArray(new ItemStack[allIng.size()]);
    	      			stacksPlayer.put(id, s.stackSize+added);
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
	        		
      	      		if (stack != null && stack.stack !=null && !stack.isCrafting && stack.getAmount() >=s.stackSize+added) {
      	      			ingredients[i] = allIng.toArray(new ItemStack[allIng.size()]);
      	      			stacksNet.put(id, s.stackSize+added);
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
                    actualRecipe[x][y] = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(y));
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
	                if (actualRecipe[i] != null) {
	                	if(failedSlots.contains(i))continue slot;
	                    ItemStack[] possibilities = actualRecipe[i];
	                    for (int i2 = 0; i2 < possibilities.length; ++i2) {
	                    	ItemStack poss = possibilities[i2];
	                    	if (poss != null) {
		                    	ItemStack copy = poss.copy();
		                        ItemStack took = ItemUtil.removeFromPlayerInventory(con, copy);
		                        if(took == null){
		                        	ItemStack ret = panel.getNetwork().getItemStorage().removeItem(copy, false);
		                        	if(ret !=null){
		                        		took = ret;
		                        	}
		                        }else if(took.stackSize < poss.stackSize){
		                        	ItemStack ret = panel.getNetwork().getItemStorage().removeItem(ItemUtil.copy(copy, poss.stackSize-took.stackSize), false);
		                        	if(ret !=null){
		                        		took.stackSize+=ret.stackSize;
		                        	}
		                        }
		
		                        if (took != null) {
		                        	if(panel.getMatrix().getStackInSlot(i) == null){
		                        		panel.getMatrix().setInventorySlotContents(i, took);
		                        	}else{
		                        		panel.getMatrix().getStackInSlot(i).stackSize+=took.stackSize;
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
	
	private boolean containerContainsIngredient(ContainerPanelCrafting panelContainer, List<ItemStack> allIng) {
		List<Slot> playerSlots = Lists.newArrayList();
		for(Slot slot : panelContainer.inventorySlots){
			if(slot.inventory instanceof InventoryPlayer){
				playerSlots.add(slot);
			}
		}
	    List<ItemStack> available = new ArrayList<ItemStack>();
	    for (Slot slot : playerSlots) {
	      if (slot.getHasStack()) {
	        available.add(slot.getStack());
	      } 
	    }
	    
	    StackHelper sh = (StackHelper)registry.getJeiHelpers().getStackHelper();
	    return sh.containsAnyStack(available, allIng) != null;
	}
}
