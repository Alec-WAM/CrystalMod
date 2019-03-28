package alec_wam.CrystalMod.world.features;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class MinableRandomConfig implements IFeatureConfig {
   public static final Predicate<IBlockState> IS_ROCK = (p_210462_0_) -> {
      if (p_210462_0_ == null) {
         return false;
      } else {
         Block block = p_210462_0_.getBlock();
         return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
      }
   };
   public final Predicate<IBlockState> canReplace;
   public final int minSize;
   public final int maxSize;
   public final IBlockState[] states;

   public MinableRandomConfig(Predicate<IBlockState> canReplaceIn, IBlockState[] statesIn, int minSize, int maxSize) {
      this.minSize = minSize;
      this.maxSize = maxSize;
      this.states = statesIn;
      this.canReplace = canReplaceIn;
   }
}