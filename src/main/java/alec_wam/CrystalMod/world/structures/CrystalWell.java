package alec_wam.CrystalMod.world.structures;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.BlockCrystal;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.BlockMetalBars;
import alec_wam.CrystalMod.blocks.BlockMetalBars.EnumMetalBarType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemCrystalSap.SapType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.ItemEnhancementKnowledge;
import alec_wam.CrystalMod.tiles.chest.BlockCrystalChest;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.TileEntityBlueCrystalChest;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster.EnumClusterType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
		for(int x = 0; x < 5; x++){
			for(int z = 0; z < 5; z++){
				setBlockAndNotifyAdequately(world, pos.add(x, 0, z), Blocks.AIR.getDefaultState(), false);
			}
		}
		
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
		
		BlockPos chestPos = pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, depth);
		setBlockAndNotifyAdequately(world, chestPos, chest, true);
		TileEntity tile = world.getTileEntity(chestPos);
		if(tile !=null && tile instanceof TileEntityBlueCrystalChest){
			createCrystalLootChest((TileEntityBlueCrystalChest)tile, rand, type);
		}
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, depth), bricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, depth), bricks, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, depth+1), bricks, false);

	}
	
	public static void createCrystalLootChest(TileEntityBlueCrystalChest chest, Random rand, int color){
		int maxItems = MathHelper.getInt(rand, 5, 15);
		List<ItemStack> lootList = Lists.newArrayList();
		for(int i = 0; i < maxItems; i++){
			lootList.add(getRandomLootItem(rand, color));
		}
		
		for(ItemStack loot : lootList){
			int slot = ItemUtil.getRandomEmptySlot(ItemUtil.getItemHandler(chest, EnumFacing.UP), 3);
			if(slot >= 0){
				chest.setInventorySlotContents(slot, loot);
			}
		}
	}
	
	public static ItemStack getRandomLootItem(Random rand, int color){
		List<ItemStack> stacks = Lists.newArrayList();
		
		if(color == 0){
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_NUGGET.getMetadata()));
			stacks.add(new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalSap, 1, SapType.BLUE.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalTreeSeedsBlue));
			stacks.add(new ItemStack(ModItems.crystalReedsBlue));
			stacks.add(new ItemStack(ModItems.crystalSeedsBlue));
			stacks.add(new ItemStack(ModBlocks.crystalSapling, 1, WoodType.BLUE.getMeta()));
			
			for(String type : new String[]{"axe", "hoe", "pick", "shovel", "sword"}){
				ItemStack stack = new ItemStack(ModItems.toolParts);
				ItemNBTHelper.setString(stack, "Type", type);
            	ItemNBTHelper.setString(stack, "Color", "blue");
				stacks.add(stack);
			}
		}
		if(color == 1){
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.RED_NUGGET.getMetadata()));
			stacks.add(new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalSap, 1, SapType.RED.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalTreeSeedsRed));
			stacks.add(new ItemStack(ModItems.crystalReedsRed));
			stacks.add(new ItemStack(ModItems.crystalSeedsRed));
			stacks.add(new ItemStack(ModBlocks.crystalSapling, 1, WoodType.RED.getMeta()));
			
			for(String type : new String[]{"axe", "hoe", "pick", "shovel", "sword"}){
				ItemStack stack = new ItemStack(ModItems.toolParts);
				ItemNBTHelper.setString(stack, "Type", type);
            	ItemNBTHelper.setString(stack, "Color", "red");
				stacks.add(stack);
			}
		}
		if(color == 2){
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_NUGGET.getMetadata()));
			stacks.add(new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalSap, 1, SapType.GREEN.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalTreeSeedsGreen));
			stacks.add(new ItemStack(ModItems.crystalReedsGreen));
			stacks.add(new ItemStack(ModItems.crystalSeedsGreen));
			stacks.add(new ItemStack(ModBlocks.crystalSapling, 1, WoodType.GREEN.getMeta()));
			
			for(String type : new String[]{"axe", "hoe", "pick", "shovel", "sword"}){
				ItemStack stack = new ItemStack(ModItems.toolParts);
				ItemNBTHelper.setString(stack, "Type", type);
            	ItemNBTHelper.setString(stack, "Color", "green");
				stacks.add(stack);
			}
		}
		if(color == 3){
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_NUGGET.getMetadata()));
			stacks.add(new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalSap, 1, SapType.DARK.getMetadata()));
			stacks.add(new ItemStack(ModItems.crystalTreeSeedsDark));
			stacks.add(new ItemStack(ModItems.crystalReedsDark));
			stacks.add(new ItemStack(ModItems.crystalSeedsDark));
			stacks.add(new ItemStack(ModBlocks.crystalSapling, 1, WoodType.DARK.getMeta()));
			
			for(String type : new String[]{"axe", "hoe", "pick", "shovel", "sword"}){
				ItemStack stack = new ItemStack(ModItems.toolParts);
				ItemNBTHelper.setString(stack, "Type", type);
            	ItemNBTHelper.setString(stack, "Color", "dark");
				stacks.add(stack);
			}
		}
		
		stacks.add(new ItemStack(ModItems.guide));
		stacks.add(ItemEnhancementKnowledge.createRandomBook(rand));
		
		ItemStack stack = ItemStackTools.getEmptyStack();
		stack = stacks.get(MathHelper.getInt(rand, 0, stacks.size()-1));
		return stack;
	}
	
	public static void generateNetherWell(World world, BlockPos pos, Random rand){
		IBlockState chisled = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
		IBlockState quartz = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.DEFAULT);
		IBlockState bars = ModBlocks.metalBars.getDefaultState().withProperty(BlockMetalBars.TYPE, BlockMetalBars.EnumMetalBarType.PURE);
		IBlockState glass = ModBlocks.crystalGlass.getDefaultState().withProperty(BlockCrystalGlass.TYPE, BlockCrystalGlass.GlassType.PURE);
		IBlockState liquid = ModFluids.fluidPureCrystal.getBlock().getDefaultState();
		IBlockState pureBlock = ModBlocks.crystal.getDefaultState().withProperty(BlockCrystal.TYPE, CrystalBlockType.PURE);
		IBlockState pureChiseled = ModBlocks.crystal.getDefaultState().withProperty(BlockCrystal.TYPE, CrystalBlockType.PURE_CHISELED);
		
		setBlockAndNotifyAdequately(world, pos, pureChiseled, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 4), pureChiseled, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST, 4), pureChiseled, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4), pureChiseled, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH), pureBlock, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST), pureBlock, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3), pureBlock, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST), pureBlock, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 4), pureBlock, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3), pureBlock, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 4), pureBlock, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST, 3), pureBlock, false);
		
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 3), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 3), bars, false);

		for(int i = 0; i < 9; i++){
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST).offset(EnumFacing.DOWN, 1+i), quartz, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST).offset(EnumFacing.DOWN, 1+i), quartz, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 3).offset(EnumFacing.DOWN, 1+i), quartz, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 3).offset(EnumFacing.DOWN, 1+i), quartz, false);
			
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN, 1+i), chisled, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 4).offset(EnumFacing.DOWN, 1+i), chisled, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST, 2).offset(EnumFacing.DOWN, 1+i), chisled, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.DOWN, 1+i), chisled, false);
		}
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST).offset(EnumFacing.DOWN, 5), glass, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH).offset(EnumFacing.DOWN, 5), glass, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 3).offset(EnumFacing.DOWN, 5), glass, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, 5), glass, false);

		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST).offset(EnumFacing.DOWN, 9), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH).offset(EnumFacing.DOWN, 9), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 3).offset(EnumFacing.DOWN, 9), bars, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, 9), bars, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST).offset(EnumFacing.DOWN, 10), quartz, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH).offset(EnumFacing.DOWN, 10), quartz, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2).offset(EnumFacing.DOWN, 10), chisled, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 3).offset(EnumFacing.DOWN, 10), quartz, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, 10), quartz, false);

		//Liquid
		//TODO Make Molten
		for(int i = 0; i < 9; i++){
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2).offset(EnumFacing.DOWN, 9-i), liquid, false);
			if(i != 0 && i !=4){
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST).offset(EnumFacing.DOWN, 9-i), liquid, false);
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH).offset(EnumFacing.DOWN, 9-i), liquid, false);
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 3).offset(EnumFacing.DOWN, 9-i), liquid, false);
				setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN, 9-i), liquid, false);
			}
		}
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2), Blocks.AIR.getDefaultState(), false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST), Blocks.AIR.getDefaultState(), false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH), Blocks.AIR.getDefaultState(), false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 3), Blocks.AIR.getDefaultState(), false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3), Blocks.AIR.getDefaultState(), false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 4), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2), liquid, false);
	}
	
	public static void generateEndWell(World world, BlockPos pos, Random rand){
		
		IBlockState endBricks = Blocks.END_BRICKS.getDefaultState();
		IBlockState purpur = Blocks.PURPUR_BLOCK.getDefaultState();
		IBlockState purpurPillar = Blocks.PURPUR_PILLAR.getDefaultState().withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.Y);
		IBlockState endRod = Blocks.END_ROD.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP);
		IBlockState liquid = ModFluids.fluidEnder.getBlock().getDefaultState();

		IBlockState basicStairs = Blocks.PURPUR_STAIRS.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP).withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.STRAIGHT);
		IBlockState eastStairs = basicStairs.withProperty(BlockStairs.FACING, EnumFacing.EAST);
		IBlockState westStairs = basicStairs.withProperty(BlockStairs.FACING, EnumFacing.WEST);
		IBlockState northStairs = basicStairs.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
		IBlockState southStairs = basicStairs.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
		
		//Ring
		for(int i = 0; i < 5; i++){
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1+i), endBricks, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1+i).offset(EnumFacing.SOUTH, 6), endBricks, false);
		}
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 1), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 5), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 1), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 5), endBricks, false);

		for(int i = 0; i < 5; i++){
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 1+i), endBricks, false);
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 1+i).offset(EnumFacing.EAST, 6), endBricks, false);
		}
		/////
		
		//Bottom
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 4).offset(EnumFacing.DOWN), endBricks, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 4).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.DOWN), endBricks, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 4).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.DOWN), purpur, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 4).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.DOWN), endBricks, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.DOWN), endBricks, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.DOWN), purpur, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 4).offset(EnumFacing.DOWN), endBricks, false);
		////
		
		//Pillar
		for(int i = 0; i < 4; i++){
			setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.UP, i), purpurPillar, false);
		}
		BlockPos topPos = pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.UP, 4);
		EntityShulker shulker = new EntityShulker(world);
		shulker.setPosition(topPos.getX() + 0.5d, topPos.getY() + 0.5d, topPos.getZ() + 0.5d);
		if(!world.isRemote){
			world.spawnEntity(shulker);
		}
		////
		
		//Side
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.UP), purpurPillar, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP), purpurPillar, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.UP), purpurPillar, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP), purpurPillar, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.UP), eastStairs, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP), eastStairs, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 6).offset(EnumFacing.SOUTH).offset(EnumFacing.UP), westStairs, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 6).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP), westStairs, false);

		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.UP), southStairs, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.UP), southStairs, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 6).offset(EnumFacing.UP), northStairs, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 6).offset(EnumFacing.UP), northStairs, false);
		////
		
		//Rods
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.UP, 2), endRod, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP, 2), endRod, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 6).offset(EnumFacing.SOUTH).offset(EnumFacing.UP, 2), endRod, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 6).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP, 2), endRod, false);

		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.UP, 2), endRod, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.UP, 2), endRod, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 6).offset(EnumFacing.UP, 2), endRod, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 6).offset(EnumFacing.UP, 2), endRod, false);
		////
		
		//Liquid
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.UP, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.UP, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 5).offset(EnumFacing.UP, 2), liquid, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 3), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 1).offset(EnumFacing.SOUTH, 4), liquid, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 1), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 3), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 4), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 5), liquid, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 1), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 2), liquid, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 4), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 3).offset(EnumFacing.SOUTH, 5), liquid, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 1), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 3), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 4), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 5), liquid, false);
		
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 2), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 3), liquid, false);
		setBlockAndNotifyAdequately(world, pos.offset(EnumFacing.EAST, 5).offset(EnumFacing.SOUTH, 4), liquid, false);
		////
		
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
