package com.alec_wam.CrystalMod.fluids;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.alec_wam.CrystalMod.items.ItemIngot;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import com.alec_wam.CrystalMod.util.ModLogger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Loader;

public class Fluids {

	public static final String XP_JUICE_NAME = "xpjuice";
	
	// EnderIO compatable liquid XP
	public static Fluid fluidXpJuice;
	
	public static Fluid fluidBlueCrystal, fluidRedCrystal, fluidGreenCrystal, fluidDarkCrystal, fluidPureCrystal, fluidDarkIron;
	
	public static void registerFluids(){
		if (!Loader.isModLoaded("EnderIO")) {
	      ModLogger.info("XP Juice registered by Crystal Mod.");
	      fluidXpJuice = new Fluid(XP_JUICE_NAME, new ResourceLocation("crystalmod:blocks/fluid/"+Fluids.XP_JUICE_NAME+"_still"), new ResourceLocation("crystalmod:blocks/fluid/"+Fluids.XP_JUICE_NAME+"_flowing"))
	          .setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.xpjuice");
	      FluidRegistry.registerFluid(fluidXpJuice);
	    } else {
	    	ModLogger.info("XP Juice regististration left to EnderIO.");
	    }
		
		fluidBlueCrystal = new FluidColored("crystal_blue", ItemIngot.RGB_BLUE).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.crystal.blue");
		FluidRegistry.registerFluid(fluidBlueCrystal);
		fluidRedCrystal = new FluidColored("crystal_red", ItemIngot.RGB_RED).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.crystal.red");
		FluidRegistry.registerFluid(fluidRedCrystal);
		fluidGreenCrystal = new FluidColored("crystal_green", ItemIngot.RGB_GREEN).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.crystal.green");
		FluidRegistry.registerFluid(fluidGreenCrystal);
		fluidDarkCrystal = new FluidColored("crystal_dark", ItemIngot.RGB_DARK).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.crystal.dark");
		FluidRegistry.registerFluid(fluidDarkCrystal);
		fluidPureCrystal = new FluidColored("crystal_pure", ItemIngot.RGB_PURE).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.crystal.pure");
		FluidRegistry.registerFluid(fluidPureCrystal);
		
		fluidDarkIron = new FluidColored("darkiron", ItemIngot.RGB_DARK_IRON).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.darkiron");
		FluidRegistry.registerFluid(fluidDarkIron);
		
		createBuckets();
	}
	
	public static void forgeRegisterXPJuice() {
	    fluidXpJuice = FluidRegistry.getFluid(getXPJuiceName());
	    if (fluidXpJuice == null) {
	    	ModLogger.warning("Liquid XP Juice registration left to ender io but could not be found.");
	    }
	}
	
	private static String getXPJuiceName() {
	    String enderIOXPJuiceName = null;

	    try {
	      Field getField = Class.forName("crazypants.enderio.config.Config").getField("xpJuiceName");
	      enderIOXPJuiceName = (String) getField.get(null);
	    } catch (Exception e) {
	    }

	    if (enderIOXPJuiceName != null && !XP_JUICE_NAME.equals(enderIOXPJuiceName)) {
	      ModLogger.info("Overwriting XP Juice name with '" + enderIOXPJuiceName + "' taken from EnderIO' config");
	      return enderIOXPJuiceName;
	    }

	    return XP_JUICE_NAME;
	}
	
	public static Map<Fluid, ItemStack> bucketList = Maps.newHashMap();
	
	public static void createBuckets() {
		List<Fluid> list = Lists.newArrayList();
		if(fluidXpJuice !=null)list.add(fluidXpJuice);
		list.add(fluidBlueCrystal);	list.add(fluidRedCrystal);list.add(fluidGreenCrystal);list.add(fluidDarkCrystal);list.add(fluidPureCrystal);
		list.add(fluidDarkIron);
		for(Fluid fluid : list){
		    if (FluidRegistry.isUniversalBucketEnabled()) {
		    	FluidRegistry.addBucketForFluid(fluid);
	    		bucketList.put(fluid, UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid));
		    } else {
		    	bucketList.put(fluid, new ItemStack(ItemBucketCrystalMod.create(null, fluid)));
		    }
		}
	}

	public static void registerBucket() {
		FluidRegistry.enableUniversalBucket();
	}

	

	public static FluidStack getCrystalFluid(ItemStack stack) {
		if(stack == null || stack.getItem() != ModItems.crystals) return null;
		Fluid shardColor = null;
		int amt = 0;
		int shardAmt = 100;
		if(stack.getMetadata() == CrystalType.BLUE_SHARD.getMetadata()){
			shardColor = Fluids.fluidBlueCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.RED_SHARD.getMetadata()){
			shardColor = Fluids.fluidRedCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.GREEN_SHARD.getMetadata()){
			shardColor = Fluids.fluidGreenCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.DARK_SHARD.getMetadata()){
			shardColor = Fluids.fluidDarkCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.PURE_SHARD.getMetadata()){
			shardColor = Fluids.fluidPureCrystal; amt = shardAmt;
		}
		return (shardColor !=null && amt > 0) ? new FluidStack(shardColor, amt) : null;
	}
}
