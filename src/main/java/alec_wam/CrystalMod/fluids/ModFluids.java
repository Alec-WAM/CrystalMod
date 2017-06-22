package alec_wam.CrystalMod.fluids;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystalFluid;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ItemIngot;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModFluids {

	public static final String XP_JUICE_NAME = "xpjuice";
	
	// EnderIO compatable liquid XP
	public static Fluid fluidXpJuice;
	
	public static Fluid fluidBlueCrystal, fluidRedCrystal, fluidGreenCrystal, fluidDarkCrystal, fluidPureCrystal, fluidDarkIron;
	
	public static Fluid fluidEnder;
	
	public static Fluid fluidInk;
	public static Fluid fluidTears;
	
	public static void registerFluids(){
		if (!Loader.isModLoaded("EnderIO")) {
	      ModLogger.info("XP Juice registered by Crystal Mod.");
	      fluidXpJuice = new Fluid(XP_JUICE_NAME, new ResourceLocation("crystalmod:blocks/fluid/"+ModFluids.XP_JUICE_NAME+"_still"), new ResourceLocation("crystalmod:blocks/fluid/"+ModFluids.XP_JUICE_NAME+"_flowing"))
	          .setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("crystalmod.xpjuice");
	      registerFluid(fluidXpJuice);
	      registerClassicBlock(fluidXpJuice);
	    } else {
	    	ModLogger.info("XP Juice regististration left to EnderIO.");
	    }
		
		fluidBlueCrystal = new FluidColored("crystal_blue", ItemIngot.RGB_BLUE).setUnlocalizedName("crystalmod.crystal.blue");
		registerFluid(fluidBlueCrystal);
		registerClassicBlock(fluidBlueCrystal);
		
		fluidRedCrystal = new FluidColored("crystal_red", ItemIngot.RGB_RED).setUnlocalizedName("crystalmod.crystal.red");
		registerFluid(fluidRedCrystal);
		registerClassicBlock(fluidRedCrystal);
		
		fluidGreenCrystal = new FluidColored("crystal_green", ItemIngot.RGB_GREEN).setUnlocalizedName("crystalmod.crystal.green");
		registerFluid(fluidGreenCrystal);
		registerClassicBlock(fluidGreenCrystal);
		
		fluidDarkCrystal = new FluidColored("crystal_dark", ItemIngot.RGB_DARK).setUnlocalizedName("crystalmod.crystal.dark");
		registerFluid(fluidDarkCrystal);
		registerClassicBlock(fluidDarkCrystal);
		
		fluidPureCrystal = new FluidColored("crystal_pure", ItemIngot.RGB_PURE).setLuminosity(10).setUnlocalizedName("crystalmod.crystal.pure");
		registerFluid(fluidPureCrystal);
		registerClassicBlock(fluidPureCrystal);
		
		fluidDarkIron = new FluidColored("darkiron", ItemIngot.RGB_DARK_IRON).setUnlocalizedName("crystalmod.darkiron");
		registerFluid(fluidDarkIron);
		registerClassicBlock(fluidDarkIron);
		
		fluidEnder = new FluidColored("ender", 0x063931).setUnlocalizedName("crystalmod.ender");
		registerFluid(fluidEnder);
		ModBlocks.registerBlock(new BlockFluidEnder(fluidEnder, net.minecraft.block.material.Material.WATER), fluidEnder.getName());
		
		fluidInk = new Fluid("ink", CrystalMod.resourceL("blocks/fluid/ink_still"), CrystalMod.resourceL("blocks/fluid/ink_flowing")).setUnlocalizedName("crystalmod.ink");
		registerFluid(fluidInk);
		registerClassicBlock(fluidInk);
		
		fluidTears = new Fluid("tears", CrystalMod.resourceL("blocks/fluid/tears_still"), CrystalMod.resourceL("blocks/fluid/tears_flowing")).setUnlocalizedName("crystalmod.tears");
		registerFluid(fluidTears);
		registerClassicBlock(fluidTears);
		
		createBuckets();
	}
	
	public static BlockFluidBase registerClassicBlock(Fluid fluid) {
	    BlockFluidBase block = new BlockCrystalFluid(fluid, net.minecraft.block.material.Material.WATER);
	    return ModBlocks.registerBlock(block, fluid.getName());
	}
	
	public static BlockFluidBase registerMoltenBlock(Fluid fluid) {
	    BlockFluidBase block = new BlockCrystalFluid(fluid, net.minecraft.block.material.Material.LAVA);
	    return ModBlocks.registerBlock(block, fluid.getName());
	}
	
	public static void registerFluid(Fluid fluid){
		registerFluid(fluid, true);
	}
	public static void registerFluid(Fluid fluid, boolean addBucket){
		FluidRegistry.registerFluid(fluid);
		if(addBucket)bucketFluids.add(fluid);
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
	private static final List<Fluid> bucketFluids = Lists.newArrayList();
	
	public static void createBuckets() {
		for(Fluid fluid : bucketFluids){
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
			shardColor = ModFluids.fluidBlueCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.RED_SHARD.getMetadata()){
			shardColor = ModFluids.fluidRedCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.GREEN_SHARD.getMetadata()){
			shardColor = ModFluids.fluidGreenCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.DARK_SHARD.getMetadata()){
			shardColor = ModFluids.fluidDarkCrystal; amt = shardAmt;
		}else if(stack.getMetadata() == CrystalType.PURE_SHARD.getMetadata()){
			shardColor = ModFluids.fluidPureCrystal; amt = shardAmt;
		}
		return (shardColor !=null && amt > 0) ? new FluidStack(shardColor, amt) : null;
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerFluidModels(Fluid fluid) {
	    if(fluid == null) {
	      return;
	    }

	    Block block = fluid.getBlock();
	    if(block != null) {
	      Item item = Item.getItemFromBlock(block);
	      FluidStateMapper mapper = new FluidStateMapper(fluid);

	      // item-model
	      if(item != null) {
	        ModelLoader.registerItemVariants(item);
	        ModelLoader.setCustomMeshDefinition(item, mapper);
	      }
	      // block-model
	      ModelLoader.setCustomStateMapper(block, mapper);
	    }
	  }

	@SideOnly(Side.CLIENT)
	public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

		public final Fluid fluid;
		public final ModelResourceLocation location;

		public FluidStateMapper(Fluid fluid) {
			this.fluid = fluid;
			this.location = new ModelResourceLocation(CrystalMod.resource("fluid_block"), fluid.getName());
		}

		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			return location;
		}

		@Nonnull
		@Override
		public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
			return location;
		}
	}
	
	private static final ResourceLocation RES_UNDERWATER_XP = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_xpjuice.png");
	private static final ResourceLocation RES_UNDERWATER_BLUE = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_blue.png");
	private static final ResourceLocation RES_UNDERWATER_RED = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_red.png");
	private static final ResourceLocation RES_UNDERWATER_GREEN = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_green.png");
	private static final ResourceLocation RES_UNDERWATER_DARK = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_dark.png");
	private static final ResourceLocation RES_UNDERWATER_PURE = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_pure.png");
	private static final ResourceLocation RES_UNDERWATER_DIRON = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_diron.png");
	private static final ResourceLocation RES_UNDERWATER_ENDER = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_ender.png");
	private static final ResourceLocation RES_UNDERWATER_INK = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_ink.png");
	private static final ResourceLocation RES_UNDERWATER_TEARS = new ResourceLocation("crystalmod:textures/gui/overlay/underwater/underwater_tears.png");
	
	public static ResourceLocation getOverlayTexture(Fluid fluid){
		if(fluid == fluidBlueCrystal){
			return RES_UNDERWATER_BLUE;
		}
		if(fluid == fluidRedCrystal){
			return RES_UNDERWATER_RED;
		}
		if(fluid == fluidGreenCrystal){
			return RES_UNDERWATER_GREEN;
		}
		if(fluid == fluidDarkCrystal){
			return RES_UNDERWATER_DARK;
		}
		if(fluid == fluidPureCrystal){
			return RES_UNDERWATER_PURE;
		}
		if(fluid == fluidDarkIron){
			return RES_UNDERWATER_DIRON;
		}
		if(fluidXpJuice !=null && fluid == fluidXpJuice){
			return RES_UNDERWATER_XP;
		}
		if(fluid == fluidEnder){
			return RES_UNDERWATER_ENDER;
		}
		if(fluid == fluidInk){
			return RES_UNDERWATER_INK;
		}
		if(fluid == fluidTears){
			return RES_UNDERWATER_TEARS;
		}
		return null;
	}

}
