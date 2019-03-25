package alec_wam.CrystalMod.util.tool;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.BlockChorusPlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ChorusPlantUtil {

	
	public static class ChorusPlantData {
		public List<BlockPos> blockList;
		public List<BlockPos> flowerList;
		public List<BlockPos> plantList;
		public final BlockPos startPos;
		
		public ChorusPlantData(BlockPos pos){
			startPos = pos;
			blockList = Lists.newArrayList();
			flowerList = Lists.newArrayList();
			plantList = Lists.newArrayList();
		}
		
		public boolean isDoneGrowing(IBlockAccess world){
			int flowerCount = flowerList.size();
			for(BlockPos pos : flowerList){
				if(isFlowerGrown(world, pos)){
					flowerCount--;
				}
			}
			return flowerCount == 0;
		}
	}
	
	public static ChorusPlantData buildPlantData(IBlockAccess world, BlockPos start){
		ChorusPlantData data = new ChorusPlantData(start);
		scanPlant(world, start, EnumFacing.UP, data);
		return data;
	}
	
	public static void scanPlant(IBlockAccess world, BlockPos pos, EnumFacing from, ChorusPlantData data){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.CHORUS_PLANT){
			data.blockList.add(pos);
			data.plantList.add(pos);
			List<EnumFacing> directions = getDirections(state.getActualState(world, pos));
			for(EnumFacing dir : directions){
				if(from == null || dir !=from.getOpposite()){
					scanPlant(world, pos.offset(dir), dir, data);
				}
			}
		}
		if(state.getBlock() == Blocks.CHORUS_FLOWER){
			data.flowerList.add(pos);
			data.blockList.add(pos);
		}
	}
	
	public static List<EnumFacing> getDirections(IBlockState state){
		List<EnumFacing> list = Lists.newArrayList();
		if(state.getValue(BlockChorusPlant.NORTH)){
			list.add(EnumFacing.NORTH);
		}
		if(state.getValue(BlockChorusPlant.SOUTH)){
			list.add(EnumFacing.SOUTH);
		}
		if(state.getValue(BlockChorusPlant.EAST)){
			list.add(EnumFacing.EAST);
		}
		if(state.getValue(BlockChorusPlant.WEST)){
			list.add(EnumFacing.WEST);
		}
		if(state.getValue(BlockChorusPlant.UP)){
			list.add(EnumFacing.UP);
		}
		if(state.getValue(BlockChorusPlant.DOWN)){
			list.add(EnumFacing.DOWN);
		}
		return list;
	}
	
	public static boolean isChorusPlant(IBlockState state){
		return state.getBlock() == Blocks.CHORUS_PLANT;
	}
	
	public static boolean isFlowerGrown(IBlockAccess world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.CHORUS_FLOWER){
			return state.getValue(BlockChorusFlower.AGE) == 5;
		}
		return false;
	}
	
	public static boolean isFullyGrownPlant(IBlockAccess world, BlockPos pos){
		ChorusPlantData data = buildPlantData(world, pos);
		return data.isDoneGrowing(world);
	}
	
}
