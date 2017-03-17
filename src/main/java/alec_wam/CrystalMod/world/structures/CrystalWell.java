package alec_wam.CrystalMod.world.structures;

import java.util.Random;

import alec_wam.CrystalMod.blocks.BlockCrystal;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.BlockMetalBars;
import alec_wam.CrystalMod.blocks.BlockMetalBars.EnumMetalBarType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.tiles.chest.BlockCrystalChest;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster.EnumClusterType;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class CrystalWell {

	
	public static void generateOverworldWell(World world, BlockPos pos, Random rand, int type){
		boolean placeLiquid = true;
		WoodType wood = WoodType.BLUE;
		CrystalBlockType brickType = CrystalBlockType.BLUE_BRICK;
		CrystalBlockType chisledType = CrystalBlockType.BLUE_CHISELED;
		Fluid fluid = ModFluids.fluidBlueCrystal;
		CrystalChestType chestType = CrystalChestType.BLUE;
		EnumMetalBarType barType = EnumMetalBarType.BLUE;
		EnumClusterType clusterType = EnumClusterType.BLUE;
		if(type == 1){
			wood = WoodType.RED;
			brickType = CrystalBlockType.RED_BRICK;
			chisledType = CrystalBlockType.RED_CHISELED;
			fluid = ModFluids.fluidRedCrystal;
			chestType = CrystalChestType.RED;
			barType = EnumMetalBarType.RED;
			clusterType = EnumClusterType.RED;
		} else if(type == 2){
			wood = WoodType.GREEN;
			brickType = CrystalBlockType.GREEN_BRICK;
			chisledType = CrystalBlockType.GREEN_CHISELED;
			fluid = ModFluids.fluidGreenCrystal;
			chestType = CrystalChestType.GREEN;
			barType = EnumMetalBarType.GREEN;
			clusterType = EnumClusterType.GREEN;
		} else if(type == 3){
			wood = WoodType.DARK;
			brickType = CrystalBlockType.DARK_BRICK;
			chisledType = CrystalBlockType.DARK_CHISELED;
			fluid = ModFluids.fluidDarkCrystal;
			chestType = CrystalChestType.DARK;
			barType = EnumMetalBarType.DARK;
			clusterType = EnumClusterType.DARK;
		}
		
		
		IBlockState log = ModBlocks.crystalLog.getDefaultState().withProperty(BlockCrystalLog.VARIANT, wood);
		IBlockState xLog = log.withProperty(BlockLog.LOG_AXIS, EnumAxis.X);
		IBlockState yLog = log.withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);
		IBlockState zLog = log.withProperty(BlockLog.LOG_AXIS, EnumAxis.Z);
		
		IBlockState planks = ModBlocks.crystalPlanks.getDefaultState().withProperty(BlockCrystalLog.VARIANT, wood);
		IBlockState chisled = ModBlocks.crystal.getDefaultState().withProperty(BlockCrystal.TYPE, chisledType);
		IBlockState bricks = ModBlocks.crystal.getDefaultState().withProperty(BlockCrystal.TYPE, brickType);
		IBlockState liquid = fluid.getBlock().getDefaultState();
		IBlockState chest = ModBlocks.crystalChest.getDefaultState().withProperty(BlockCrystalChest.VARIANT_PROP, chestType);
		IBlockState bars = ModBlocks.metalBars.getDefaultState().withProperty(BlockMetalBars.TYPE, barType);

		//Top
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.UP, 1), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.UP, 1).offset(EnumFacing.SOUTH, 4), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.UP, 1).offset(EnumFacing.EAST, 4), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.UP, 1).offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 4), bars, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.UP, 1).offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2), chisled, false);

		TileCrystalCluster.createRandomCluster(world, rand, pos.offset(EnumFacing.UP, 2).offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2), clusterType, 60, 100, 5, 10, false);
        
		//Main Layer
		setBlockAndNotifyAdequately(world, pos, yLog, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 4), yLog, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4), yLog, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 4), yLog, false);
		
		//RIGHT SIDE
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 1), zLog, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2), planks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3), zLog, false);
		//LEFT SIDE
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 4), zLog, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 4), planks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 4), zLog, false);

		//BACK
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1), xLog, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2), planks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3), xLog, false);
		//FRONT
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 4), xLog, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 4), planks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 4), xLog, false);
		
		//BRICKS
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 1), bricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 3), bricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 1), bricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 3), bricks, false);

		int depth = 3;
		
		//Liquid and Air
		for(int i = 0; i < depth; i++){
			if(placeLiquid){
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, i), liquid, false);
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN, i), liquid, false);
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, i), liquid, false);
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, i), liquid, false);
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, i), liquid, false);
			} else {
				world.setBlockToAir(pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, i));
				world.setBlockToAir(pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN, i));
				world.setBlockToAir(pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, i));
				world.setBlockToAir(pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, i));
				world.setBlockToAir(pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, i));
			}
		}
		
		
		//Inner Logs
		for(int i = 0; i < depth-1; i++){
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN, i+1), yLog, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, i+1), yLog, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN, i+1), yLog, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, i+1), yLog, false);
			
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, i+1), bricks, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 4).offset(EnumFacing.DOWN, i+1), bricks, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.DOWN, i+1), bricks, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 4).offset(EnumFacing.DOWN, i+1), bricks, false);
		}
		
		//Chest Layer
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, depth), bricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN, depth), bricks, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, depth), chest, true);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, depth), bricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, depth), bricks, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, depth+1), bricks, false);

	}
	
	public static void generateNetherWell(World world, BlockPos pos, Random rand){
		
	}
	
	public static void generateEndWell(World world, BlockPos pos, Random rand){
		
	}
	
	public static void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state, boolean notify)
    {
        if (notify)
        {
            worldIn.setBlockState(pos, state, 3);
        }
        else
        {
            worldIn.setBlockState(pos, state, 2);
        }
    }
	
}
