package alec_wam.CrystalMod.world.features;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import alec_wam.CrystalMod.blocks.plants.BlockCrystalBerryBush;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class CrystalBushFeature extends Feature<NoFeatureConfig> {
	
	public CrystalBushFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49878_1_) {
		super(p_i49878_1_);
	}

	@Override
	public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      boolean flag = false;

      for(int i = 0; i < 8; ++i) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && ModBlocks.crystalBerryBushGroup.getBlock(EnumCrystalColor.BLUE).getDefaultState().isValidPosition(p_212245_1_, blockpos)) {
        	EnumCrystalColor bushType = EnumCrystalColor.values()[MathHelper.nextInt(p_212245_3_, 0, 3)]; //Choose a random color in the list
			BlockCrystalBerryBush bush = ModBlocks.crystalBerryBushGroup.getBlock(bushType);
        	int age = MathHelper.nextInt(p_212245_3_, 0, 3);
        	BlockState state = bush.getDefaultState().with(BlockCrystalBerryBush.AGE, age);
        	p_212245_1_.setBlockState(blockpos, state, 3);
            flag = true;
         }
      }

      return flag;
   }
}