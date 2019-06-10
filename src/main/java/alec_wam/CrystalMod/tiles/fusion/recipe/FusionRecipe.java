package alec_wam.CrystalMod.tiles.fusion.recipe;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.recipes.IFusionRecipe;
import alec_wam.CrystalMod.api.tile.IFusionPedestal;
import alec_wam.CrystalMod.api.tile.IPedestal;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.StringUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FusionRecipe implements IFusionRecipe {

	private final Object input;
	private final List<?> pedestalInputs;
	private final ItemStack output;
	private final Vec3d colorVec;
	
	public FusionRecipe(Object input, List<?> pedestalInputs, ItemStack output, Vec3d color){
		this.input = input;
		this.pedestalInputs = pedestalInputs;
		this.output = output;
		this.colorVec = color;
	}

	@Override
	public Object getMainInput() {
		return input;
	}

	@Override
	public List<?> getInputs() {
		return pedestalInputs;
	}

	@Override
	public ItemStack getOutput() {
		return output;
	}
	
	@Override
	public boolean matches(IFusionPedestal fpedestal, World world, List<IPedestal> pedestals) {
		if(ItemStackTools.isEmpty(fpedestal.getStack())) return false;
		
		if(ItemUtil.matches(getMainInput(), fpedestal.getStack())){
			List<IPedestal> pedestalCopy = Lists.newArrayList(pedestals);
			for(Object ingredient : getInputs()){
				boolean found = false;
				for(IPedestal pedestal : pedestalCopy){
					ItemStack stack = pedestal.getStack();
					if(ItemStackTools.isEmpty(stack))continue;
					if(ItemUtil.matches(ingredient, stack)){
						found = true;
						pedestalCopy.remove(pedestal);
						break;
					}
				}
				if (!found) {
	                return false;
	            }
			}
			//All need to be empty
			for (IPedestal pedestal : pedestalCopy) {
	            if (ItemStackTools.isValid(pedestal.getStack())) {
	            	return false;
	            }
	        }
			return true;
		}
		return false;
	}
	
	@Override
	public String canCraft(IFusionPedestal fpedestal, World world, List<IPedestal> pedestals) {
		if(ItemStackTools.isEmpty(fpedestal.getStack())) return Lang.localize("fusion.message.emptyinput");
		
		if(ItemUtil.matches(getMainInput(), fpedestal.getStack())){
			if(ItemStackTools.getStackSize(fpedestal.getStack()) > 1)return Lang.localize("fusion.message.tobiginput");
			List<IPedestal> pedestalCopy = Lists.newArrayList(pedestals);
			List<String> missing = Lists.newArrayList();
			for(Object ingredient : getInputs()){
				boolean found = false;
				for(IPedestal pedestal : pedestalCopy){
					ItemStack stack = pedestal.getStack();
					if(ItemStackTools.isEmpty(stack))continue;
					if(ItemUtil.matches(ingredient, stack)){
						found = true;
						pedestalCopy.remove(pedestal);
						break;
					}
				}
				if (!found) {
					missing.add(ingredient.toString());
	            }
			}
			if(!missing.isEmpty()){
                return Lang.localizeFormat("fusion.message.missing", StringUtils.makeReadable(missing));
			}
			//All need to be empty
			List<String> extraItems = Lists.newArrayList();
			for (IPedestal pedestal : pedestalCopy) {
	            if (ItemStackTools.isValid(pedestal.getStack())) {
	            	extraItems.add(pedestal.getStack().getDisplayName().getString());
	            }
	        }
			if(!extraItems.isEmpty()){
				return Lang.localizeFormat("fusion.message.extra", StringUtils.makeReadable(extraItems));
			}
			return "true";
		}
		return "false";
	}
	
	

	@Override
	public void finishCrafting(IFusionPedestal fpedestal, World world, List<IPedestal> linkedpedestals) {
		if(!matches(fpedestal, world, linkedpedestals)){
			return;
		}
		List<IPedestal> pedestalCopy = Lists.newArrayList(linkedpedestals);
		for(Object ingredient : getInputs()){
			search : for(IPedestal pedestal : pedestalCopy){
				if(ItemStackTools.isValid(pedestal.getStack()) && ItemUtil.matches(ingredient, pedestal.getStack())){
					final ItemStack stack = pedestal.getStack();
					pedestal.setStack(ItemUtil.consumeItem(stack));
					pedestalCopy.remove(pedestal);
					break search;
				}
			}
		}
		ItemStack output = ItemUtil.copy(getOutput(), 1);
		fpedestal.setStack(output);
	}

	@Override
	public Vec3d getRecipeColor() {
		return colorVec;
	}
	
}
