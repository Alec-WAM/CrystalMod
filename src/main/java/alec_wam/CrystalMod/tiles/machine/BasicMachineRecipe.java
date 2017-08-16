package alec_wam.CrystalMod.tiles.machine;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class BasicMachineRecipe
{
    final Object input;
    final int inputSize;
    final ItemStack output;
    final int energy;
    
    public BasicMachineRecipe(ItemStack input, ItemStack output, int energy)
    {
    	this.input = input;
        this.inputSize = ItemStackTools.getStackSize(input);
        this.output = output;
        this.energy = energy;
    }
    
    public BasicMachineRecipe(String input, int size, ItemStack output, int energy)
    {
      this.input = input;
      this.inputSize = size;
      this.output = output;
      this.energy = energy;
    }
    
    public Object getInput()
    {
    	return input;
    }
    
    public int getInputSize(){
    	return inputSize;
    }
    
    public NonNullList<ItemStack> getInputs()
    {
    	if(this.input instanceof String){
    		return OreDictionary.getOres((String)input);
    	}
    	if(input instanceof ItemStack){
    		return NonNullList.withSize(1, (ItemStack)input);
    	}
    	return NonNullList.create();
    }
    
    public boolean matchesInput(ItemStack stack){
    	if(this.input instanceof String){
    		return ItemUtil.itemStackMatchesOredict(stack, (String)input);
    	}
    	if(input instanceof ItemStack){
    		return ItemUtil.canCombine(stack, (ItemStack)input);
    	}
    	return false;
    }
    
    public ItemStack getOutput()
    {
      return this.output.copy();
    }
    
    public int getEnergy()
    {
      return this.energy;
    }
    
    @Override
    public boolean equals(Object obj){
    	if(obj == null || !(obj instanceof BasicMachineRecipe)) return false;
    	BasicMachineRecipe other = (BasicMachineRecipe)obj;
    	
    	if(getInput() != other.getInput()) return false;
    	
    	return ItemUtil.canCombine(getOutput(), other.getOutput());
    }
}
