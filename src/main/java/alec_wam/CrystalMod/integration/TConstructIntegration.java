package alec_wam.CrystalMod.integration;

import java.util.Map;
import java.util.Map.Entry;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.fluid.FluidMolten;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.entities.animals.EntityCrystalCow;
import alec_wam.CrystalMod.fluids.Fluids;
import alec_wam.CrystalMod.items.ItemIngot;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;

import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TConstructIntegration {

	private static ResourceLocation moltenMetal = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");
	private static ResourceLocation moltenMetalFlowing = new ResourceLocation(
			"tconstruct:blocks/fluids/molten_metal_flow");
	
	public static FluidMolten moltenCrystalBlue = new FluidMolten("moltenCrystalBlue", ItemIngot.RGB_BLUE, moltenMetal, moltenMetalFlowing);
	public static FluidMolten moltenCrystalRed = new FluidMolten("moltenCrystalRed", ItemIngot.RGB_RED, moltenMetal, moltenMetalFlowing);
	public static FluidMolten moltenCrystalGreen = new FluidMolten("moltenCrystalGreen", ItemIngot.RGB_GREEN, moltenMetal, moltenMetalFlowing);
	public static FluidMolten moltenCrystalDark = new FluidMolten("moltenCrystalDark", ItemIngot.RGB_DARK, moltenMetal, moltenMetalFlowing);
	public static FluidMolten moltenCrystalPure = new FluidMolten("moltenCrystalPure", ItemIngot.RGB_PURE, moltenMetal, moltenMetalFlowing);

	public static FluidMolten moltenDarkIron = new FluidMolten("moltenDarkIron", ItemIngot.RGB_DARK_IRON, moltenMetal, moltenMetalFlowing);
	
	public static void preInit(){
		
		oreDictionary();
		
		FluidRegistry.registerFluid(moltenCrystalBlue);
		FluidRegistry.addBucketForFluid(moltenCrystalBlue);
		addToSmeltery(moltenCrystalBlue, "CrystalBlue", true);
		
		FluidRegistry.registerFluid(moltenCrystalRed);
		FluidRegistry.addBucketForFluid(moltenCrystalRed);
		addToSmeltery(moltenCrystalRed, "CrystalRed", true);
		
		FluidRegistry.registerFluid(moltenCrystalGreen);
		FluidRegistry.addBucketForFluid(moltenCrystalGreen);
		addToSmeltery(moltenCrystalGreen, "CrystalGreen", true);
		
		FluidRegistry.registerFluid(moltenCrystalDark);
		FluidRegistry.addBucketForFluid(moltenCrystalDark);
		addToSmeltery(moltenCrystalDark, "CrystalDark", true);
		
		FluidRegistry.registerFluid(moltenCrystalPure);
		FluidRegistry.addBucketForFluid(moltenCrystalPure);
		addToSmeltery(moltenCrystalPure, "CrystalPure", true);
		
		FluidRegistry.registerFluid(moltenDarkIron);
		FluidRegistry.addBucketForFluid(moltenDarkIron);
		addToSmeltery(moltenDarkIron, "IronDark", true);
		
		
		registerAlloy(new FluidStack(moltenDarkIron, 144), new FluidStack(FluidRegistry.getFluid("iron"), 144), new FluidStack(Fluids.fluidDarkCrystal, 16));
		registerAlloy(new FluidStack(moltenCrystalPure, 144), new FluidStack(moltenCrystalBlue, 144), new FluidStack(moltenCrystalRed, 144), new FluidStack(moltenCrystalGreen, 144), new FluidStack(moltenCrystalDark, 144));
	}
	
	public static void init(){
		TinkerRegistry.registerMelting(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata()), moltenCrystalBlue, 144);
		TinkerRegistry.registerMelting(new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata()), moltenCrystalRed, 144);
		TinkerRegistry.registerMelting(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata()), moltenCrystalGreen, 144);
		TinkerRegistry.registerMelting(new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata()), moltenCrystalDark, 144);
		TinkerRegistry.registerMelting(new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata()), moltenCrystalPure, 144);

		/*TinkerRegistry.registerBasinCasting(new OreCastingRecipe(Lists.newArrayList(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.BLUE.getMeta())), null, moltenCrystalBlue, 144*9));
		TinkerRegistry.registerBasinCasting(new OreCastingRecipe(Lists.newArrayList(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.RED.getMeta())), null, moltenCrystalRed, 144*9));
		TinkerRegistry.registerBasinCasting(new OreCastingRecipe(Lists.newArrayList(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.GREEN.getMeta())), null, moltenCrystalGreen, 144*9));
		TinkerRegistry.registerBasinCasting(new OreCastingRecipe(Lists.newArrayList(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARK.getMeta())), null, moltenCrystalDark, 144*9));
		TinkerRegistry.registerBasinCasting(new OreCastingRecipe(Lists.newArrayList(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.PURE.getMeta())), null, moltenCrystalPure, 144*9));*/
	}
	
	/*TinkerRegistry.registerBasinCasting(new OreCastingRecipe(blockOre.getLeft(),
                                                             null, // no cast
                                                             fluid,
                                                             blockOre.getRight()));*/
	
	public static void oreDictionary(){
		Map<String, int[]> map = Maps.newHashMap();
		map.put("Blue", new int[] {CrystalType.BLUE_NUGGET.getMetadata(), IngotType.BLUE.getMetadata(), CrystalIngotBlockType.BLUE.getMeta()});
		map.put("Red", new int[] {CrystalType.RED_NUGGET.getMetadata(), IngotType.RED.getMetadata(), CrystalIngotBlockType.RED.getMeta()});
		map.put("Green", new int[] {CrystalType.GREEN_NUGGET.getMetadata(), IngotType.GREEN.getMetadata(), CrystalIngotBlockType.GREEN.getMeta()});
		map.put("Dark", new int[] {CrystalType.DARK_NUGGET.getMetadata(), IngotType.DARK.getMetadata(), CrystalIngotBlockType.DARK.getMeta()});
		map.put("Pure", new int[] {CrystalType.PURE_NUGGET.getMetadata(), IngotType.PURE.getMetadata(), CrystalIngotBlockType.PURE.getMeta()});

		for(Entry<String, int[]> entry : map.entrySet()){
			String color = entry.getKey();
			int nugget = entry.getValue()[0];
			int ingot = entry.getValue()[1];
			int ingotBlock = entry.getValue()[2];
			ModCrafting.oredict(new ItemStack(ModItems.crystals, 1, nugget), "nuggetCrystal"+color);
			ModCrafting.oredict(new ItemStack(ModItems.ingots, 1, ingot),  "ingotCrystal"+color);
			ModCrafting.oredict(new ItemStack(ModBlocks.crystalIngot, 1, ingotBlock),  "blockCrystal"+color);
		}
	}
	
	public static void addToSmeltery(Fluid fluid, String oreDicName, boolean toolForge){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("fluid", fluid.getName());
		tag.setString("ore", oreDicName);
		tag.setBoolean("toolforge", toolForge);
		FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
	}
	
	/**
	 * 
	 * @param outputFluid
	 */
	public static void registerAlloy(FluidStack outputFluid, FluidStack... inputFluids)
	{
		if(Loader.isModLoaded("tconstruct")) {
			NBTTagList tagList = new NBTTagList();
			
			tagList.appendTag(outputFluid.writeToNBT(new NBTTagCompound()));
		
			for(FluidStack fluidS : inputFluids){
				tagList.appendTag(fluidS.writeToNBT(new NBTTagCompound()));
			}

			if(tagList.tagCount() >= 3){
				NBTTagCompound message = new NBTTagCompound();
				message.setTag("alloy", tagList);
				FMLInterModComms.sendMessage("tconstruct", "alloy", message);
			}
		}
	}
	
}
