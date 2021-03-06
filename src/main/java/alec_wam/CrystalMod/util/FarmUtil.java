package alec_wam.CrystalMod.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalBerryBush;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.blocks.crops.ItemCorn.CornItemType;
import alec_wam.CrystalMod.blocks.crops.material.TileMaterialCrop;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.oredict.OreDictionary;

public class FarmUtil {

	public static Map<CropType, List<String>> crops = Maps.newHashMap();
	public static Map<String, IPlantable> seeds = Maps.newHashMap();
	
	public static void addSeed(ItemStack stack, IPlantable plant){
		if(ItemStackTools.isNullStack(stack))return;
		if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			for (int a = 0; a < 16; a++) {
				seeds.put(stack.getItem().getRegistryName().toString() + "|" + a, plant);
			}
		} else {
			seeds.put(stack.getItem().getRegistryName().toString() + "|" + stack.getItemDamage(), plant);
		}
	}
	
	public static boolean isSeed(ItemStack stack, boolean ignoreCrops){
		if(ItemStackTools.isNullStack(stack))return false;
		if(!ignoreCrops){
			Block bi = Block.getBlockFromItem(stack.getItem());
			if(bi !=null){
				if ((getCrops(CropType.NORMAL).contains(bi.getUnlocalizedName() + stack.getItemDamage())) || (getCrops(CropType.CLICKABLE).contains(bi.getUnlocalizedName() + stack.getItemDamage())) || (getCrops(CropType.STACKED).contains(bi.getUnlocalizedName() + stack.getItemDamage())))
				{
					return true;
				}
			}
		}

		if((stack.getItem() instanceof IPlantable) || (seeds.containsKey(stack.getItem().getRegistryName().toString() + "|" + stack.getItemDamage()))){
			return true;
		}
		return false;
	}
	
	public static IPlantable getSeedPlantable(ItemStack stack){
		if(stack.getItem() instanceof IPlantable){
			return (IPlantable)stack.getItem();
		}
		String tag = stack.getItem().getRegistryName().toString() + "|" + stack.getItemDamage();
		if(seeds.containsKey(tag)){
			return seeds.get(tag);
		}
		return null;
	}
	
	public static enum CropType {
		NORMAL, STACKED, CLICKABLE;
	}
	
	public static List<String> getCrops(CropType type){
		if(!crops.containsKey(type)){
			crops.put(type, Lists.newArrayList());
		}
		return crops.get(type);
	}
	
	public static void addStandardCrop(ItemStack stack, int grownMeta)
	{
	    if (Block.getBlockFromItem(stack.getItem()) == null) {
	      return;
	    }
	    if (grownMeta == OreDictionary.WILDCARD_VALUE) {
	      for (int a = 0; a < 16; a++) {
	    	 getCrops(CropType.NORMAL).add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + a);
	      }
	    } else {
	    	getCrops(CropType.NORMAL).add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + grownMeta);
	    }
	    if (((Block.getBlockFromItem(stack.getItem()) instanceof BlockCrops)) && (grownMeta != 7)) {
	    	getCrops(CropType.NORMAL).add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + "7");
	    }
	}
	  
	public static void addClickableCrop(ItemStack stack, int grownMeta)
	{
	    if (Block.getBlockFromItem(stack.getItem()) == null) {
	      return;
	    }
	    addClickableCrop(Block.getBlockFromItem(stack.getItem()), grownMeta);
	}
	
	public static void addClickableCrop(Block itemBlock, int grownMeta)
	{
	    if (grownMeta == OreDictionary.WILDCARD_VALUE) {
	      for (int a = 0; a < 16; a++) {
	    	  getCrops(CropType.CLICKABLE).add(itemBlock.getUnlocalizedName() + a);
	      }
	    } else {
	    	getCrops(CropType.CLICKABLE).add(itemBlock.getUnlocalizedName() + grownMeta);
	    }
	    if ((itemBlock instanceof BlockCrops) && (grownMeta != ((BlockCrops)itemBlock).getMaxAge())) {
	    	getCrops(CropType.CLICKABLE).add(itemBlock.getUnlocalizedName() + ((BlockCrops)itemBlock).getMaxAge());
	    }
	}
	  
	public static void addStackedCrop(ItemStack stack, int grownMeta)
	{
	    if (Block.getBlockFromItem(stack.getItem()) == null) {
	      return;
	    }
	    addStackedCrop(Block.getBlockFromItem(stack.getItem()), grownMeta);
	}
	  
	public static void addStackedCrop(Block block, int grownMeta)
	{
		if (grownMeta == OreDictionary.WILDCARD_VALUE) {
			for (int a = 0; a < 16; a++) {
				getCrops(CropType.STACKED).add(block.getUnlocalizedName() + a);
			}
		} else {
			getCrops(CropType.STACKED).add(block.getUnlocalizedName() + grownMeta);
		}
		if ((block instanceof BlockCrops) && (grownMeta != ((BlockCrops)block).getMaxAge())) {
	    	getCrops(CropType.STACKED).add(block.getUnlocalizedName() + ((BlockCrops)block).getMaxAge());
	    }
	}
	  
	public static boolean isGrownCrop(World world, BlockPos pos)
	{
		if (world.isAirBlock(pos)) {
	      return false;
	    }
	    boolean found = false;
	    IBlockState state = world.getBlockState(pos);
	    Block bi = state.getBlock();
	    for (int a = 0; a < 16; a++) {
	      if ((getCrops(CropType.NORMAL).contains(bi.getUnlocalizedName() + a)) || (getCrops(CropType.STACKED).contains(bi.getUnlocalizedName() + a)) || (getCrops(CropType.CLICKABLE).contains(bi.getUnlocalizedName() + a)))
	      {
	        found = true;
	        break;
	      }
	    }
	    Block biB = world.getBlockState(pos.down()).getBlock();
	    int md = bi.getMetaFromState(state);
	    
	    if(bi == ModBlocks.materialCrop){
	    	TileEntity tile = world.getTileEntity(pos);
	    	if(tile !=null && tile instanceof TileMaterialCrop){
				return ((TileMaterialCrop)tile).isGrown();
			}
	    	return false;
	    }
	    
	    if ((((bi instanceof IGrowable)) && (!((IGrowable)bi).canGrow(world, pos, state, world.isRemote)) && (!(bi instanceof BlockStem))) 
	    		|| (((bi instanceof BlockCrops)) && ((BlockCrops)bi).isMaxAge(state) && (!found)) 
	    		|| ((bi == Blocks.NETHER_WART) && (state.getValue(BlockNetherWart.AGE).intValue() >= 3)) 
	    		|| ((bi == Blocks.COCOA) && (state.getValue(BlockCocoa.AGE).intValue() >= 2)) 
	    		|| (getCrops(CropType.NORMAL).contains(bi.getUnlocalizedName() + md)) || (getCrops(CropType.STACKED).contains(bi.getUnlocalizedName() + md)) || ((getCrops(CropType.CLICKABLE).contains(bi.getUnlocalizedName() + md)) && (biB == bi))) {
	      return true;
	    }
	    return false;
	}
	  
	public static boolean isStackedCrop(World world, BlockPos pos)
	{
		if (world.isAirBlock(pos)) {
	      return false;
	    }
	    boolean found = false;
	    IBlockState state = world.getBlockState(pos);
	    Block bi = state.getBlock();
	    for (int a = 0; a < 16; a++) {
	      if ((getCrops(CropType.STACKED).contains(bi.getUnlocalizedName() + a)))
	      {
	        found = true;
	        break;
	      }
	    }
	    Block biB = world.getBlockState(pos).getBlock();
	    int md = bi.getMetaFromState(state);
	    if (found || (getCrops(CropType.STACKED).contains(bi.getUnlocalizedName() + md)) && (biB == bi)) {
	      return true;
	    }
	    return false;
	}
	  
	public static boolean isClickableCrop(World world, BlockPos pos){
		if (world.isAirBlock(pos)) {
	      return false;
	    }
	    boolean found = false;
	    IBlockState state = world.getBlockState(pos);
	    Block bi = state.getBlock();
	    for (int a = 0; a < 16; a++) {
	      if ((getCrops(CropType.CLICKABLE).contains(bi.getUnlocalizedName() + a)))
	      {
	        found = true;
	        break;
	      }
	    }
	    int md = bi.getMetaFromState(state);
	    if(bi == ModBlocks.materialCrop){
	    	return true;
	    }
	    if (found || (getCrops(CropType.CLICKABLE).contains(bi.getUnlocalizedName() + md))) {
	      return true;
	    }
		return false;
	}
	
	public static boolean isNormalCrop(World world, BlockPos pos)
	{
		if (world.isAirBlock(pos)) {
	      return false;
	    }
	    boolean found = false;
	    IBlockState state = world.getBlockState(pos);
	    Block bi = state.getBlock();
	    for (int a = 0; a < 16; a++) {
	      if ((getCrops(CropType.NORMAL).contains(bi.getUnlocalizedName() + a)))
	      {
	        found = true;
	        break;
	      }
	    }
	    int md = bi.getMetaFromState(state);
	    if ((((bi instanceof IGrowable)) && (!(bi instanceof BlockStem))) || (((bi instanceof BlockCrops)) && (!found)) || ((bi == Blocks.NETHER_WART)) || ((bi == Blocks.COCOA)) || (getCrops(CropType.NORMAL).contains(bi.getUnlocalizedName() + md))) {
	      return true;
	    }
	    return false;
	}
	
	public static boolean isCrop(World world, BlockPos pos){
		return isNormalCrop(world, pos) || isClickableCrop(world, pos) || isStackedCrop(world, pos);
	}
	
	public static boolean plant(World worldObj, BlockPos bc, IPlantable plantable) {
	    worldObj.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
	    IBlockState target = plantable.getPlant(worldObj, bc);    
	    worldObj.setBlockState(bc, target, 1 | 2);
	    return true;
	}
	
	public static boolean canPlant(World worldObj, BlockPos bc, IPlantable plantable) {
	    IBlockState target = plantable.getPlant(worldObj, bc);
	    BlockPos groundPos = bc.down();
	    IBlockState groundBS = worldObj.getBlockState(groundPos);
	    Block ground = groundBS.getBlock();
	    if(target != null && target.getBlock().canPlaceBlockAt(worldObj, bc) &&        
	        ground.canSustainPlant(groundBS, worldObj, groundPos, EnumFacing.UP, plantable)) {
	      return true;
	    }
	    return false;
	}

	public static void addDefaultCrops() {
		addClickableCrop(ModBlocks.materialCrop, 1);
	    IBlockState plantState = ModBlocks.crystalPlant.getDefaultState().withProperty(BlockCrystalPlant.AGE, 3);
		IBlockState bushState = ModBlocks.crystalBush.getDefaultState().withProperty(BlockCrystalBerryBush.AGE, 3);
	    for(PlantType type : PlantType.values()){
	    	addClickableCrop(ModBlocks.crystalPlant, ModBlocks.crystalPlant.getMetaFromState(plantState.withProperty(BlockCrystalPlant.TYPE, type)));
	    	addClickableCrop(ModBlocks.crystalBush, ModBlocks.crystalBush.getMetaFromState(bushState.withProperty(BlockCrystalBerryBush.TYPE, type)));
	    }
	    
	    addSeed(new ItemStack(ModItems.corn, 1, CornItemType.KERNELS.getMeta()), ModBlocks.corn);
	    addStackedCrop(Blocks.REEDS, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(Blocks.CACTUS, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsBlue, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsRed, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsGreen, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsDark, OreDictionary.WILDCARD_VALUE);
	}
	
}
