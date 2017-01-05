package alec_wam.CrystalMod.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.ModBlocks;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.oredict.OreDictionary;

public class FarmUtil {

	public static Map<CropType, List<String>> crops = Maps.newHashMap();
	public static List<String> seeds = new ArrayList<String>();
	
	public static void addSeed(ItemStack stack){
		if(ItemStackTools.isNullStack(stack))return;
		if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			for (int a = 0; a < 16; a++) {
				seeds.add(stack.getUnlocalizedName() + a);
			}
		} else {
			seeds.add(stack.getUnlocalizedName() + stack.getItemDamage());
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
		  
		  boolean found = false;
	      for (int a = 0; a < 16; a++) {
	        if ((seeds.contains(stack.getItem().getUnlocalizedName() + a)))
	        {
	          found = true;
	          break;
	        }
	      }
		  if((stack.getItem() instanceof IPlantable) || found || (seeds.contains(stack.getItem().getUnlocalizedName() + stack.getItemDamage()))){
			  return true;
		  }
		  return false;
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
	    Block itemBlock = Block.getBlockFromItem(stack.getItem());
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
	    
	    if ((((bi instanceof IGrowable)) && (!((IGrowable)bi).canGrow(world, pos, state, world.isRemote)) && (!(bi instanceof BlockStem))) 
	    		|| (((bi instanceof BlockCrops)) && ((BlockCrops)bi).isMaxAge(state) && (!found)) 
	    		|| ((bi == Blocks.NETHER_WART) && (((Integer)state.getValue(BlockNetherWart.AGE)).intValue() >= 3)) 
	    		|| ((bi == Blocks.COCOA) && (((Integer)state.getValue(BlockCocoa.AGE)).intValue() >= 2)) 
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
	    Block biB = world.getBlockState(pos.down()).getBlock();
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
		addClickableCrop(new ItemStack(ModBlocks.crystalPlantBlue), 3);
	    addClickableCrop(new ItemStack(ModBlocks.crystalPlantRed), 3);
	    addClickableCrop(new ItemStack(ModBlocks.crystalPlantGreen), 3);
	    addClickableCrop(new ItemStack(ModBlocks.crystalPlantDark), 3);
	    addClickableCrop(new ItemStack(ModBlocks.materialCrop), 1);
	    addSeed(new ItemStack(ModItems.materialSeed));
	    addStackedCrop(Blocks.REEDS, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(Blocks.CACTUS, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsBlue, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsRed, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsGreen, OreDictionary.WILDCARD_VALUE);
	    addStackedCrop(ModBlocks.crystalReedsDark, OreDictionary.WILDCARD_VALUE);
	}
	
}
