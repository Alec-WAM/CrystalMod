package alec_wam.CrystalMod.util;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.util.BlockUtil.BlockFilter;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlatformCalculator {

	public PlatformData getPlatform(World world, BlockPos pos, EnumFacing facing, @Nonnull BlockFilter filter){
		return getPlatform(world, pos, facing, filter, false);
	}
	
	public PlatformData getPlatform(World world, BlockPos pos, EnumFacing facing, @Nonnull BlockFilter filter, boolean canBeHollow){
		BlockPos facingPos = pos.offset(facing);
		if(world.getBlockState(facingPos).getBlock() == Blocks.STONE_SLAB){
			if(facing.getAxis().isHorizontal()){
				Map<EnumFacing, Integer> sizes = Maps.newHashMap();
				for(EnumFacing face : EnumFacing.HORIZONTALS){
					if(face == facing.getOpposite())continue;
					int index = 0;
					while(filter.isValid(world, facingPos.offset(face, 1+index), world.getBlockState(facingPos.offset(face, 1+index)))){
						index++;
					}
					sizes.put(face, index);
				}
				int width = 1; //Count the starting block
				int height = 1;
				EnumFacing.Axis dir = null;
				if(sizes.getOrDefault(EnumFacing.SOUTH, 0) > 0 && sizes.getOrDefault(EnumFacing.NORTH, 0) > 0){
					dir = EnumFacing.Axis.Z;
					//Z Not Corner
					width += sizes.get(EnumFacing.SOUTH) + sizes.get(EnumFacing.NORTH);
					BlockPos northPos = facingPos.offset(EnumFacing.NORTH, sizes.get(EnumFacing.NORTH)); 
					BlockPos southPos = facingPos.offset(EnumFacing.SOUTH, sizes.get(EnumFacing.SOUTH)); 
					
					int nSize = 0;
					//Keep going in the direction we are facing
					while(filter.isValid(world, northPos.offset(facing, 1+nSize), world.getBlockState(northPos.offset(facing, 1+nSize)))){
						nSize++;
					}
					int sSize = 0;
					//Keep going in the direction we are facing
					while(filter.isValid(world, southPos.offset(facing, 1+sSize), world.getBlockState(southPos.offset(facing, 1+sSize)))){
						sSize++;
					}
					//Smallest "height"
					height += Math.min(nSize, sSize);
				}
				if(sizes.getOrDefault(EnumFacing.WEST, 0) > 0 && sizes.getOrDefault(EnumFacing.EAST, 0) > 0){
					//X Not Corner
					dir = EnumFacing.Axis.X;
					height += sizes.get(EnumFacing.WEST) + sizes.get(EnumFacing.EAST);
					
					BlockPos westPos = facingPos.offset(EnumFacing.WEST, sizes.get(EnumFacing.WEST)); 
					BlockPos eastPos = facingPos.offset(EnumFacing.EAST, sizes.get(EnumFacing.EAST)); 
					
					int wSize = 0;
					//Keep going in the direction we are facing
					while(filter.isValid(world, westPos.offset(facing, 1+wSize), world.getBlockState(westPos.offset(facing, 1+wSize)))){
						wSize++;
					}
					int eSize = 0;
					//Keep going in the direction we are facing
					while(filter.isValid(world, eastPos.offset(facing, 1+eSize), world.getBlockState(eastPos.offset(facing, 1+eSize)))){
						eSize++;
					}
					//Smallest "width"
					width += Math.min(wSize, eSize);
				}
				BlockPos corner = null;	
				boolean passBorder = true;
				
				if(dir == EnumFacing.Axis.Z){
					corner = facingPos.offset(EnumFacing.NORTH, sizes.get(EnumFacing.NORTH));
					if(facing == EnumFacing.WEST){
						corner = corner.offset(EnumFacing.WEST, sizes.get(EnumFacing.WEST));							
					}
					//Check Opposite Edge
					for(int y = 0; y < width; y++){
						BlockPos lookPos = corner.add(height-1, 0, y);
						if(!filter.isValid(world, lookPos, world.getBlockState(lookPos))){
							passBorder = false;
							break;
						}
					}
				}
				if(dir == EnumFacing.Axis.X){
					corner = facingPos.offset(EnumFacing.WEST, sizes.get(EnumFacing.WEST));
					if(facing == EnumFacing.NORTH){
						corner = corner.offset(EnumFacing.NORTH, sizes.get(EnumFacing.NORTH));							
					}
					//Check Opposite Edge
					for(int x = 0; x < height; x++){
						BlockPos lookPos = corner.add(x, 0, width-1);
						if(!filter.isValid(world, lookPos, world.getBlockState(lookPos))){
							passBorder = false;
							break;
						}
					}
				}

				PlatformData data = new PlatformData().setSizeMap(sizes).setWidth(width).setHeight(height).setCompleteBorder(passBorder);
				if(passBorder && corner!=null){
					data.setCorner(corner);
					BlockPos failPos = null;
					if(!canBeHollow){
						search : for(int x = 0; x < height; x++){
							for(int y = 0; y < width; y++){
								BlockPos lookPos = corner.add(x, 0, y);
								if(!filter.isValid(world, lookPos, world.getBlockState(lookPos))){
									failPos = lookPos;
									break search;
								}
							}
						}
						if(failPos !=null){
							data.setFailPos(failPos);
						} 
						data.setPassedScan(failPos == null);
					}
				}
				
				return data;
			}
		}
		return null;
	}
	
	public static class PlatformData {
		private boolean completeBorder, passedScan;
		private int width, height;
		private BlockPos corner, failPos;
		private Map<EnumFacing, Integer> sizeMap = Maps.newHashMap();
		public PlatformData(){}
		
		public boolean hasCompleteBorder() {
			return completeBorder;
		}
		
		public PlatformData setCompleteBorder(boolean passed) {
			this.completeBorder = passed;
			return this;
		}
		
		public boolean hasPassedScan() {
			return passedScan;
		}
		
		public PlatformData setPassedScan(boolean passed) {
			this.passedScan = passed;
			return this;
		}

		public int getWidth() {
			return width;
		}

		public PlatformData setWidth(int width) {
			this.width = width;
			return this;
		}

		public int getHeight() {
			return height;
		}

		public PlatformData setHeight(int height) {
			this.height = height;
			return this;
		}

		public BlockPos getCorner() {
			return corner;
		}

		public PlatformData setCorner(BlockPos pos) {
			this.corner = pos;
			return this;
		}

		public BlockPos getFailPos() {
			return failPos;
		}

		public PlatformData setFailPos(BlockPos pos) {
			this.failPos = pos;
			return this;
		}

		public Map<EnumFacing, Integer> getSizeMap() {
			return sizeMap;
		}

		public PlatformData setSizeMap(Map<EnumFacing, Integer> sizeMap) {
			this.sizeMap = sizeMap;
			return this;
		}
	}
}
