package alec_wam.CrystalMod.compatibility.materials;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistryEntry;
 
public class ItemMaterial extends ForgeRegistryEntry<ItemMaterial> {

	private final int materialColor;
	private final DustRecipe[] dustRecipes;
	private final Ingredient plateIngredient;
	private boolean hasDust;
	private ItemStack dustSmeltStack;
	private boolean hasPlate;
	
	public ItemMaterial(DustRecipe[] dustRecipes, Ingredient plateIngredient, int color){
		this.dustRecipes = dustRecipes;
		this.plateIngredient = plateIngredient;
		this.materialColor = color;
		this.dustSmeltStack = ItemStackTools.getEmptyStack();
	}
	
	public ItemMaterial setDust(boolean value){
		this.hasDust = value;
		return this;
	}
	
	public boolean hasDust(){
		return hasDust;
	}
	
	public ItemMaterial setDustSmeltOutput(ItemStack stack){
		this.dustSmeltStack = stack;
		return this;
	}
	
	public ItemStack getDustSmeltOutput(){
		return dustSmeltStack;
	}
	
	public ItemMaterial setPlate(boolean value){
		this.hasPlate = value;
		return this;
	}
	
	public boolean hasPlate(){
		return hasPlate;
	}

	public DustRecipe[] getDustRecipes() {
		return dustRecipes;
	}

	public Ingredient getPlateIngredient() {
		return plateIngredient;
	}

	public int getMaterialColor() {
		return materialColor;
	}
	
	public static class DustRecipe {
		private final String name;
		private final int count;
		private final Ingredient ingredient;
		private final int energy;
		
		public DustRecipe(String name, Ingredient ingredient, int count, int energy){
			this.name = name;
			this.ingredient = ingredient;
			this.count = count;
			this.energy = energy;
		}
		
		public String getName(){
			return name;
		}
		
		public int getCount() {
			return count;
		}

		public Ingredient getIngredient() {
			return ingredient;
		}

		public int getEnergy() {
			return energy;
		}
	}
	
	public static class PlateRecipe {
		private final Ingredient ingredient;
		
		public PlateRecipe(Ingredient ingredient){
			this.ingredient = ingredient;
		}

		public Ingredient getIngredient() {
			return ingredient;
		}
	}
}
