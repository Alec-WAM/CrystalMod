package alec_wam.CrystalMod.api.crop;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.blocks.crops.material.IMaterialCrop;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SpecialCropRecipe {

	private IMaterialCrop input1, output;
	private List<ItemStack> inputs;
	
	public SpecialCropRecipe(IMaterialCrop crop1, Object input, IMaterialCrop output){
		this.input1 = crop1; this.output = output;
		inputs = Lists.newArrayList();
		if(input !=null){
			if(input instanceof ItemStack){
				inputs.add((ItemStack)input);
			}
			if(input instanceof String){
				inputs.addAll(OreDictionary.getOres((String)input));
			}
		}
	}
	
	public boolean matches(IMaterialCrop crop1, ItemStack crop2){
		if(crop1 == getInput1()){
			for(ItemStack stack : this.getInputList()){
				if(ItemUtil.canCombine(stack, crop2))return true;
			}
		}
		return false;
	}
	
	public IMaterialCrop getInput1(){
		return input1;
	}
	
	public List<ItemStack> getInputList(){
		return inputs;
	}
	
	public IMaterialCrop getOutput(){
		return output;
	}
	
	public String getRecipe(){
		return CrystalModAPI.localizeCrop(getInput1()) + " + " + (getInputList().isEmpty() ? "NULL" : getInputList().get(0).getDisplayName()) + " = "+CrystalModAPI.localizeCrop(getOutput());
	}
	
}
