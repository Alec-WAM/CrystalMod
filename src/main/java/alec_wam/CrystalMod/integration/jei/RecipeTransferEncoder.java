package alec_wam.CrystalMod.integration.jei;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketRecipeTransfer;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ContainerPatternEncoder;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.gui.IGuiIngredient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public class RecipeTransferEncoder implements IRecipeTransferHandler {

	private IModRegistry registry;
	public RecipeTransferEncoder(IModRegistry registry){
		this.registry = registry;
	}
	
	@Override
	public Class<? extends Container> getContainerClass() {
		return ContainerPatternEncoder.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		
		if (!doTransfer) {
		      return null;
	    }
		
		if (! (container instanceof ContainerPatternEncoder)) {
		      return registry.getJeiHelpers().recipeTransferHandlerHelper().createInternalError();
	    }
		
		ContainerPatternEncoder encoderContainer = (ContainerPatternEncoder) container;
	    if(doTransfer) {
	    	IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

	        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();
	        NBTTagCompound recipe = new NBTTagCompound();
	        if(encoderContainer.isProcessing){
	        	Map<Integer, ItemStack> inputs = Maps.newHashMap();
	        	Map<Integer, ItemStack> outputs = Maps.newHashMap();
	        	for (IGuiIngredient<ItemStack> guiIngredient : guiIngredients.values()) {
	                if (guiIngredient != null && guiIngredient.getCurrentlyDisplayed() != null && guiIngredient.getCurrentlyDisplayed().getValue() !=null) {
	                    ItemStack ingredient = guiIngredient.getCurrentlyDisplayed().getValue().copy();
	                    int hash = ingredient.getItem().hashCode() * (ingredient.getItemDamage() + 1) * (ingredient.hasTagCompound() ? ingredient.getTagCompound().hashCode() : 1);
	                    if (guiIngredient.isInput()) {
	                        if (inputs.containsKey(hash)) {
	                            inputs.get(hash).stackSize++;
	                        } else {
	                            inputs.put(hash, ingredient);
	                        }
	                    } else {
	                        if (outputs.containsKey(hash)) {
	                            outputs.get(hash).stackSize++;
	                        } else {
	                            outputs.put(hash, ingredient);
	                        }
	                    }
	                }
	            }
	        	
	        	
	        	NBTTagList itags = new NBTTagList();
	        	int slot = 0;
	        	for (ItemStack is : inputs.values()) {
    				  NBTTagCompound tag = new NBTTagCompound();
    				  tag.setInteger("Slot", slot);
    				  is.writeToNBT(tag);
    				  itags.appendTag(tag);
    				  slot++;
	        	}
	        	recipe.setTag("Inputs", itags);
	        	slot = 0;
	        	NBTTagList otags = new NBTTagList();
	        	for (ItemStack is : outputs.values()) {
    				  NBTTagCompound tag = new NBTTagCompound();
    				  tag.setInteger("Slot", slot);
    				  is.writeToNBT(tag);
    				  otags.appendTag(tag);
    				  slot++;
	        	}
	        	recipe.setTag("Outputs", otags);
	        }else {
	        
	        ItemStack[][] ingredients = new ItemStack[9][];
	        
	        for (int i = 0; i < 9; i++) {
	  	      if (guiIngredients.containsKey(i + 1)) {
	  	        List<ItemStack> allIng = guiIngredients.get(i + 1).getAllIngredients();
	  	        if (!allIng.isEmpty()) {
	  	        	ingredients[i] = allIng.toArray(new ItemStack[allIng.size()]);
	  	        }
	  	      }
	        }
	        
	        
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
	        }
	        CrystalModNetwork.sendToServer(new PacketRecipeTransfer(recipe));
	    }
	            
	    return null;
	}
}
