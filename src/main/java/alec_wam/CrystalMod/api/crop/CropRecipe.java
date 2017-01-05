package alec_wam.CrystalMod.api.crop;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.blocks.crops.material.IMaterialCrop;

public class CropRecipe {

	private IMaterialCrop input1, input2, output;
	
	public CropRecipe(IMaterialCrop crop1, IMaterialCrop crop2, IMaterialCrop output){
		this.input1 = crop1; this.input2 = crop2; this.output = output;
	}
	
	public boolean matches(IMaterialCrop crop1, IMaterialCrop crop2){
		return (crop1 == getInput1() && crop2 == getInput2()) || (crop2 == getInput1() && crop1 == getInput2());
	}
	
	public IMaterialCrop getInput1(){
		return input1;
	}
	
	public IMaterialCrop getInput2(){
		return input2;
	}
	
	public IMaterialCrop getOutput(){
		return output;
	}
	
	public String getRecipe(){
		return CrystalModAPI.localizeCrop(getInput1()) + " + " + CrystalModAPI.localizeCrop(getInput2()) + " = "+CrystalModAPI.localizeCrop(getOutput());
	}
	
}
