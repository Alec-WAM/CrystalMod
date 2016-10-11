package alec_wam.CrystalMod.util;

import java.util.ArrayList;
import java.util.List;

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

	public static List<String> clickableCrops = new ArrayList<String>();
	public static List<String> standardCrops = new ArrayList<String>();
	public static List<String> stackedCrops = new ArrayList<String>();
	public static List<String> seeds = new ArrayList<String>();
	
	public static void addSeed(ItemStack stack){
		if(stack == null)return;
		if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			for (int a = 0; a < 16; a++) {
				seeds.add(stack.getUnlocalizedName() + a);
			}
		} else {
			seeds.add(stack.getUnlocalizedName() + stack.getItemDamage());
		}
	}
	
	public static boolean isSeed(ItemStack stack, boolean ignoreCrops){
		if(stack == null)return false;
		  
		if(!ignoreCrops){
			  Block bi = Block.getBlockFromItem(stack.getItem());
			  if(bi !=null){
				  if ((standardCrops.contains(bi.getUnlocalizedName() + stack.getItemDamage())) || (clickableCrops.contains(bi.getUnlocalizedName() + stack.getItemDamage())) || (stackedCrops.contains(bi.getUnlocalizedName() + stack.getItemDamage())))
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
	
	public static void addStandardCrop(ItemStack stack, int grownMeta)
	{
	    if (Block.getBlockFromItem(stack.getItem()) == null) {
	      return;
	    }
	    if (grownMeta == 32767) {
	      for (int a = 0; a < 16; a++) {
	        standardCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + a);
	      }
	    } else {
	      standardCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + grownMeta);
	    }
	    if (((Block.getBlockFromItem(stack.getItem()) instanceof BlockCrops)) && (grownMeta != 7)) {
	      standardCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + "7");
	    }
	}
	  
	public static void addClickableCrop(ItemStack stack, int grownMeta)
	{
	    if (Block.getBlockFromItem(stack.getItem()) == null) {
	      return;
	    }
	    if (grownMeta == 32767) {
	      for (int a = 0; a < 16; a++) {
	        clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + a);
	      }
	    } else {
	      clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + grownMeta);
	    }
	    if (((Block.getBlockFromItem(stack.getItem()) instanceof BlockCrops)) && (grownMeta != 7)) {
	      clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + "7");
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
		if (grownMeta == 32767) {
			for (int a = 0; a < 16; a++) {
				stackedCrops.add(block.getUnlocalizedName() + a);
			}
		} else {
			stackedCrops.add(block.getUnlocalizedName() + grownMeta);
		}
		if (((block instanceof BlockCrops)) && (grownMeta != 7)) {
			stackedCrops.add(block.getUnlocalizedName() + "7");
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
	      if ((standardCrops.contains(bi.getUnlocalizedName() + a)) || (clickableCrops.contains(bi.getUnlocalizedName() + a)) || (stackedCrops.contains(bi.getUnlocalizedName() + a)))
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
	    		|| (standardCrops.contains(bi.getUnlocalizedName() + md)) || (clickableCrops.contains(bi.getUnlocalizedName() + md)) || ((stackedCrops.contains(bi.getUnlocalizedName() + md)) && (biB == bi))) {
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
	      if ((stackedCrops.contains(bi.getUnlocalizedName() + a)))
	      {
	        found = true;
	        break;
	      }
	    }
	    Block biB = world.getBlockState(pos.down()).getBlock();
	    int md = bi.getMetaFromState(state);
	    if (found || (stackedCrops.contains(bi.getUnlocalizedName() + md)) && (biB == bi)) {
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
	      if ((clickableCrops.contains(bi.getUnlocalizedName() + a)))
	      {
	        found = true;
	        break;
	      }
	    }
	    int md = bi.getMetaFromState(state);
	    if (found || (clickableCrops.contains(bi.getUnlocalizedName() + md))) {
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
	      if ((standardCrops.contains(bi.getUnlocalizedName() + a)))
	      {
	        found = true;
	        break;
	      }
	    }
	    int md = bi.getMetaFromState(state);
	    if ((((bi instanceof IGrowable)) && (!(bi instanceof BlockStem))) || (((bi instanceof BlockCrops)) && (!found)) || ((bi == Blocks.NETHER_WART)) || ((bi == Blocks.COCOA)) || (standardCrops.contains(bi.getUnlocalizedName() + md))) {
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
		//TODO Add Seperate Colored Reeds
		//TODO Add Crystal Trees
		//TODO Add new Cocoa Type Crystal Plant
		addStandardCrop(new ItemStack(Blocks.WHEAT), 32767);
	    addStandardCrop(new ItemStack(Blocks.CARROTS), 32767);
	    addStandardCrop(new ItemStack(Blocks.POTATOES), 32767);
	    addSeed(new ItemStack(ModItems.crystalSeedsBlue));
	    addSeed(new ItemStack(ModItems.crystalSeedsRed));
	    addSeed(new ItemStack(ModItems.crystalSeedsGreen));
	    addSeed(new ItemStack(ModItems.crystalSeedsDark));
	    addStandardCrop(new ItemStack(ModBlocks.crystalPlantBlue), 3);
	    addStandardCrop(new ItemStack(ModBlocks.crystalPlantRed), 3);
	    addStandardCrop(new ItemStack(ModBlocks.crystalPlantGreen), 3);
	    addStandardCrop(new ItemStack(ModBlocks.crystalPlantDark), 3);
	    addStackedCrop(Blocks.REEDS, 32767);
	    addStackedCrop(Blocks.CACTUS, 32767);
	    addStackedCrop(ModBlocks.crystalReeds, 32767);
	}
	
}
