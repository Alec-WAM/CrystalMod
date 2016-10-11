package alec_wam.CrystalMod.integration.jei;

import java.util.List;
import java.util.Map;

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

	        /*for (int i = 0; i < 9; i++) {
	          if (i < encoderContainer.encoder.getMatrix().getSizeInventory()) {
	            if (guiIngredients.containsKey(i + 1)) {
	              IGuiIngredient<ItemStack> guiIngredient = guiIngredients.get(i + 1);
	              List<ItemStack> allIngredients = guiIngredient.getAllIngredients();
	              if (!allIngredients.isEmpty()) {
	            	  encoderContainer.encoder.getMatrix().setInventorySlotContents(i, allIngredients.get(MathHelper.getRandomIntegerInRange(player.getRNG(), 0, allIngredients.size()-1)));
	              } 
	            } 
	          }
	        }*/
	        ItemStack[][] ingredients = new ItemStack[9][];
	        
	        for (int i = 0; i < 9; i++) {
	  	      if (guiIngredients.containsKey(i + 1)) {
	  	        List<ItemStack> allIng = guiIngredients.get(i + 1).getAllIngredients();
	  	        if (!allIng.isEmpty()) {
	  	        	ingredients[i] = allIng.toArray(new ItemStack[allIng.size()]);
	  	        }
	  	      }
	        }
	        
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
	    	  CrystalModNetwork.sendToServer(new PacketRecipeTransfer(recipe));
	    }
	            
	    return null;
	}
}
