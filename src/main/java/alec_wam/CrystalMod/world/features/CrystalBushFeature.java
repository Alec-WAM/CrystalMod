package alec_wam.CrystalMod.world.features;

import java.util.Random;

import alec_wam.CrystalMod.blocks.plants.BlockCrystalBerryBush;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class CrystalBushFeature extends Feature<NoFeatureConfig> {
	@Override
	public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      boolean flag = false;

      for(int i = 0; i < 8; ++i) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && ModBlocks.crystalBerryBushGroup.getBlock(EnumCrystalColor.BLUE).getDefaultState().isValidPosition(p_212245_1_, blockpos)) {
        	EnumCrystalColor bushType = EnumCrystalColor.values()[MathHelper.nextInt(p_212245_3_, 0, 3)]; //Choose a random color in the list
			BlockCrystalBerryBush bush = ModBlocks.crystalBerryBushGroup.getBlock(bushType);
        	int age = MathHelper.nextInt(p_212245_3_, 0, 3);
        	IBlockState state = bush.getDefaultState().with(BlockCrystalBerryBush.AGE, age);
        	p_212245_1_.setBlockState(blockpos, state, 3);
            flag = true;
         }
      }

      return flag;
   }
}