package alec_wam.CrystalMod.world.features;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import alec_wam.CrystalMod.blocks.plants.EnumBetterRoses;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class BetterRoseFeature extends Feature<NoFeatureConfig> {
	
	public BetterRoseFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49878_1_) {
		super(p_i49878_1_);
	}

	@Override
	public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      boolean flag = false;

      for(int i = 0; i < 64; ++i) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && blockpos.getY() < p_212245_1_.getWorld().getDimension().getHeight() - 2 && ModBlocks.betterRosesGroup.getBlock(EnumBetterRoses.WHITE).getDefaultState().isValidPosition(p_212245_1_, blockpos)) {
            EnumBetterRoses[] normalTypes = {EnumBetterRoses.ORANGE, EnumBetterRoses.MAGENTA, EnumBetterRoses.YELLOW, EnumBetterRoses.PINK, EnumBetterRoses.LIGHT_BLUE, EnumBetterRoses.PURPLE}; //Normal Colors
            EnumBetterRoses roseType = normalTypes[MathHelper.nextInt(p_212245_3_, 0, 5)]; //Choose a random color in the list
			if(p_212245_3_.nextInt(100) <= 12){ //12% chance
			    roseType = EnumBetterRoses.WHITE; //Yay! White!
			}
			DoublePlantBlock plant = ModBlocks.betterRosesGroup.getBlock(roseType);
        	
        	((DoublePlantBlock)plant.getBlock()).placeAt(p_212245_1_, blockpos, 2);
            flag = true;
         }
      }

      return flag;
   }
}