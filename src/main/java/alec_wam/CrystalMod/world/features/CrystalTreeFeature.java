package alec_wam.CrystalMod.world.features;

import java.util.Random;
import java.util.Set;

import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class CrystalTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
	private final IBlockState log;
	private final IBlockState leaf;
	private final boolean useExtraRandomHeight;

	public CrystalTreeFeature(boolean notify, EnumCrystalColorSpecial type, boolean useExtraRandomHeightIn) {
		super(notify);
		log = ModBlocks.crystalLogGroup.getBlock(type).getDefaultState();
		leaf = ModBlocks.crystalLeavesGroup.getBlock(type).getDefaultState();
		this.useExtraRandomHeight = useExtraRandomHeightIn;
	}

	public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
		int i = 4 + rand.nextInt(3);
		if (this.useExtraRandomHeight) {
			i += rand.nextInt(7);
		}
		boolean flag = true;
		if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getWorld().getHeight()) {
			for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
				int k = 1;
				if (j == position.getY()) {
					k = 0;
				}

				if (j >= position.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

				for(int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
					for(int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
						if (j >= 0 && j < worldIn.getWorld().getHeight()) {
							if (!this.canGrowInto(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
								flag = false;
							}
						} else {
							flag = false;
						}
					}
				}
			}

			if (!flag) {
				return false;
			} else {
				if (worldIn.getBlockState(position.down()).canSustainPlant(worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.OAK_SAPLING) && position.getY() < worldIn.getWorld().getHeight() - i - 1) {
					this.setDirtAt(worldIn, position.down(), position);
					for(int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
						int i4 = i3 - (position.getY() + i);
						int j1 = 1 - i4 / 2;

						for(int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
							int l1 = k1 - position.getX();

							for(int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
								int j2 = i2 - position.getZ();
								if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
									BlockPos blockpos = new BlockPos(k1, i3, i2);
									IBlockState iblockstate = worldIn.getBlockState(blockpos);
									if (iblockstate.canBeReplacedByLeaves(worldIn, blockpos)) {
										this.setBlockState(worldIn, blockpos, leaf);
									}
								}
							}
						}
					}

					for(int j3 = 0; j3 < i; ++j3) {
						IBlockState iblockstate1 = worldIn.getBlockState(position.up(j3));
						if (iblockstate1.canBeReplacedByLeaves(worldIn, position.up(j3))) {
							BlockPos pos = position.up(j3);
							if (this.doBlockNotify) {
								worldIn.setBlockState(pos, log, 19);
							} else {
								worldIn.setBlockState(pos, log, 18);
							}
							changedBlocks.add(pos.toImmutable());
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
}