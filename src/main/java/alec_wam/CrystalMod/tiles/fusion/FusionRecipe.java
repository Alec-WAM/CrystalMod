package alec_wam.CrystalMod.tiles.fusion;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.pedistals.IFusionPedistal;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.api.recipe.IFusionRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.StringUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FusionRecipe implements IFusionRecipe {

	private final Object input;
	private final List<?> pedistalInputs;
	private final ItemStack output;
	private final Vec3d colorVec;
	
	public FusionRecipe(Object input, List<?> pedistalInputs, ItemStack output, Vec3d color){
		this.input = input;
		this.pedistalInputs = pedistalInputs;
		this.output = output;
		this.colorVec = color;
	}

	@Override
	public Object getMainInput() {
		return input;
	}

	@Override
	public List<?> getInputs() {
		return pedistalInputs;
	}

	@Override
	public ItemStack getOutput() {
		return output;
	}
	
	@Override
	public boolean matches(IFusionPedistal fpedistal, World world, List<IPedistal> pedistals) {
		if(ItemStackTools.isEmpty(fpedistal.getStack())) return false;
		
		if(ItemUtil.matches(getMainInput(), fpedistal.getStack())){
			List<IPedistal> pedistalCopy = Lists.newArrayList(pedistals);
			for(Object ingredient : getInputs()){
				boolean found = false;
				for(IPedistal pedistal : pedistalCopy){
					ItemStack stack = pedistal.getStack();
					if(ItemUtil.matches(ingredient, stack)){
						found = true;
						pedistalCopy.remove(pedistal);
						break;
					}
				}
				if (!found) {
	                return false;
	            }
			}
			//All need to be empty
			for (IPedistal pedestal : pedistalCopy) {
	            if (ItemStackTools.isValid(pedestal.getStack())) {
	            	return false;
	            }
	        }
			return true;
		}
		return false;
	}
	
	@Override
	public String canCraft(IFusionPedistal fpedistal, World world, List<IPedistal> pedistals) {
		if(ItemStackTools.isEmpty(fpedistal.getStack())) return Lang.localize("fusion.message.emptyinput");
		
		if(ItemUtil.matches(getMainInput(), fpedistal.getStack())){
			if(ItemStackTools.getStackSize(fpedistal.getStack()) > 1)return Lang.localize("fusion.message.tobiginput");
			List<IPedistal> pedistalCopy = Lists.newArrayList(pedistals);
			List<String> missing = Lists.newArrayList();
			for(Object ingredient : getInputs()){
				boolean found = false;
				for(IPedistal pedistal : pedistalCopy){
					ItemStack stack = pedistal.getStack();
					if(ItemUtil.matches(ingredient, stack)){
						found = true;
						pedistalCopy.remove(pedistal);
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
			for (IPedistal pedestal : pedistalCopy) {
	            if (ItemStackTools.isValid(pedestal.getStack())) {
	            	extraItems.add(pedestal.getStack().getDisplayName());
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
	public void finishCrafting(IFusionPedistal fpedistal, World world, List<IPedistal> linkedPedistals) {
		if(!matches(fpedistal, world, linkedPedistals)){
			return;
		}
		List<IPedistal> pedistalCopy = Lists.newArrayList(linkedPedistals);
		for(Object ingredient : getInputs()){
			search : for(IPedistal pedistal : pedistalCopy){
				if(ItemStackTools.isValid(pedistal.getStack()) && ItemUtil.matches(ingredient, pedistal.getStack())){
					final ItemStack stack = pedistal.getStack();
					pedistal.setStack(ItemUtil.consumeItem(stack));
					pedistalCopy.remove(pedistal);
					break search;
				}
			}
		}
		ItemStack output = ItemUtil.copy(getOutput(), 1);
		fpedistal.setStack(output);
	}

	@Override
	public Vec3d getRecipeColor() {
		return colorVec;
	}
	
}
