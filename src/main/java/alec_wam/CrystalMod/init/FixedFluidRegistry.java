package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.compatibility.FluidStackFixed;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FixedFluidRegistry {

	public static final Fluid WATER = new Fluid("water", new ResourceLocation("block/water_still"), new ResourceLocation("block/water_flow")) {
        @Override
        public String getLocalizedName(FluidStack fs) {
            return Lang.translateToLocal("block.minecraft.water");
        }
    }.setBlock(Blocks.WATER).setUnlocalizedName(Blocks.WATER.getTranslationKey()).setColor(4020182);

    public static final Fluid LAVA = new Fluid("lava", new ResourceLocation("block/lava_still"), new ResourceLocation("block/lava_flow")) {
        @Override
        public String getLocalizedName(FluidStack fs) {
            return Lang.translateToLocal("block.minecraft.lava");
        }
    }.setBlock(Blocks.LAVA).setLuminosity(15).setDensity(3000).setViscosity(6000).setTemperature(1300).setUnlocalizedName(Blocks.LAVA.getTranslationKey());
    
    public static final Fluid MILK = new Fluid("milk", new ResourceLocation("crystalmod:block/fluid/milk_still"), new ResourceLocation("crystalmod:block/fluid/milk_flow")).setDensity(1030);
	
    public static final Fluid XP = new Fluid("xp", new ResourceLocation("crystalmod:block/fluid/xp_still"), new ResourceLocation("crystalmod:block/fluid/xp_flow"))
    		.setLuminosity(10).setDensity(800).setViscosity(1500);    
    
	
    public static FluidStack getBucketFluid(ItemStack stack){
    	if(stack.getItem() == Items.WATER_BUCKET){
    		return new FluidStackFixed(WATER, Fluid.BUCKET_VOLUME);
    	}
    	if(stack.getItem() == Items.LAVA_BUCKET){
    		return new FluidStackFixed(LAVA, Fluid.BUCKET_VOLUME);
    	}
    	if(stack.getItem() == Items.MILK_BUCKET){
    		return new FluidStackFixed(MILK, Fluid.BUCKET_VOLUME);
    	}
    	if(stack.getItem() == ModItems.xpBucket){
    		return new FluidStackFixed(XP, Fluid.BUCKET_VOLUME);
    	}
    	return null;
    }
    
    public static ItemStack getFilledBucket(Fluid fluid){
    	if(fluid == WATER){
    		return new ItemStack(Items.WATER_BUCKET);
    	}
    	if(fluid == LAVA){
    		return new ItemStack(Items.LAVA_BUCKET);
    	}
    	if(fluid == MILK){
    		return new ItemStack(Items.MILK_BUCKET);
    	}
    	if(fluid == XP){
    		return new ItemStack(ModItems.xpBucket);
    	}
    	return new ItemStack(Items.BUCKET);
    }
    
    public static Fluid getFluidFromName(String name){
    	if(name.equals(WATER.getUnlocalizedName())){
    		return WATER;
    	}
    	if(name.equals(LAVA.getUnlocalizedName())){
    		return LAVA;
    	}
    	if(name.equals(MILK.getUnlocalizedName())){
    		return MILK;
    	}
    	if(name.equals(XP.getUnlocalizedName())){
    		return XP;
    	}
    	return null;
    }
}
