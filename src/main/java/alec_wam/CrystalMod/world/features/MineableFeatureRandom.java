package alec_wam.CrystalMod.world.features;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;

public class MineableFeatureRandom extends Feature<MinableRandomConfig> {
	
	public MineableFeatureRandom(Function<Dynamic<?>, ? extends MinableRandomConfig> p_i49878_1_) {
		super(p_i49878_1_);
	}

	@Override
	public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random rand, BlockPos pos, MinableRandomConfig config) {
		return generate(world, rand, pos, config);
	}
	
	public boolean generate(IWorld worldIn, Random rand, BlockPos position, MinableRandomConfig config)
    {
		int numberOfBlocks = config.maxSize;
        float f = rand.nextFloat() * (float)Math.PI;
        double d0 = position.getX() + 8 + MathHelper.sin(f) * numberOfBlocks / 8.0F;
        double d1 = position.getX() + 8 - MathHelper.sin(f) * numberOfBlocks / 8.0F;
        double d2 = position.getZ() + 8 + MathHelper.cos(f) * numberOfBlocks / 8.0F;
        double d3 = position.getZ() + 8 - MathHelper.cos(f) * numberOfBlocks / 8.0F;
        double d4 = position.getY() + rand.nextInt(3) - 2;
        double d5 = position.getY() + rand.nextInt(3) - 2;

        int placedCount = 0;
        for (int i = 0; i < numberOfBlocks; ++i)
        {
            float f1 = (float)i / (float)numberOfBlocks;
            double d6 = d0 + (d1 - d0) * f1;
            double d7 = d4 + (d5 - d4) * f1;
            double d8 = d2 + (d3 - d2) * f1;
            double d9 = rand.nextDouble() * numberOfBlocks / 16.0D;
            double d10 = (MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int l1 = j; l1 <= i1; ++l1)
            {
                double d12 = (l1 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D)
                {
                    for (int i2 = k; i2 <= j1; ++i2)
                    {
                        double d13 = (i2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D)
                        {
                            for (int j2 = l; j2 <= k1; ++j2)
                            {
                                double d14 = (j2 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D)
                                {
                                    BlockPos blockpos = new BlockPos(l1, i2, j2);
                                    BlockState state = worldIn.getBlockState(blockpos);
                                    
                                    if (config.canReplace.func_214738_b().test(state))
                                    {
                                    	int type = MathHelper.nextInt(rand, 0, config.states.length-1);
                                		worldIn.setBlockState(blockpos, config.states[type], 2);
                                		placedCount++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return placedCount > 0;
    }
}