package alec_wam.CrystalMod.world.features;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class CrystalTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
	private final BlockState log;
	private final BlockState leaf;
	private final boolean useExtraRandomHeight;

	public CrystalTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> config, boolean notify, EnumCrystalColorSpecial type, boolean useExtraRandomHeightIn) {
		super(config, notify);
		log = ModBlocks.crystalLogGroup.getBlock(type).getDefaultState();
		leaf = ModBlocks.crystalLeavesGroup.getBlock(type).getDefaultState();
		this.useExtraRandomHeight = useExtraRandomHeightIn;
	}

	@Override
	public boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox p_208519_5_) {
		int i = rand.nextInt(3) + 5;
		if (this.useExtraRandomHeight) {
			i += rand.nextInt(7);
		}

		boolean flag = true;
		if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getMaxHeight()) {
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
						if (j >= 0 && j < worldIn.getMaxHeight()) {
							if (!func_214587_a(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
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
			} else if ((isSoil(worldIn, position.down(), getSapling())) && position.getY() < worldIn.getMaxHeight() - i - 1) {
				this.setDirtAt(worldIn, position.down(), position);

				for(int l1 = position.getY() - 3 + i; l1 <= position.getY() + i; ++l1) {
					int j2 = l1 - (position.getY() + i);
					int k2 = 1 - j2 / 2;

					for(int l2 = position.getX() - k2; l2 <= position.getX() + k2; ++l2) {
						int i3 = l2 - position.getX();

						for(int j1 = position.getZ() - k2; j1 <= position.getZ() + k2; ++j1) {
							int k1 = j1 - position.getZ();
							if (Math.abs(i3) != k2 || Math.abs(k1) != k2 || rand.nextInt(2) != 0 && j2 != 0) {
								BlockPos blockpos = new BlockPos(l2, l1, j1);
								if (isAirOrLeaves(worldIn, blockpos)) {
									this.setLogState(changedBlocks, worldIn, blockpos, leaf, p_208519_5_);
								}
							}
						}
					}
				}

				for(int i2 = 0; i2 < i; ++i2) {
					if (isAirOrLeaves(worldIn, position.up(i2))) {
						this.setLogState(changedBlocks, worldIn, position.up(i2), log, p_208519_5_);
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		/*int i = 4 + rand.nextInt(3);
		if (this.useExtraRandomHeight) {
			i += rand.nextInt(7);
		}
		boolean flag = true;
		if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getMaxHeight()) {
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
						if (j >= 0 && j < worldIn.getMaxHeight()) {
							if (!func_214587_a(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
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
				if (isSoil(worldIn, position.down(), sapling) && position.getY() < worldIn.getMaxHeight() - i - 1) {
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
									if (isAirOrLeaves(worldIn, blockpos)) {
										this.setBlockState(worldIn, blockpos, leaf);
									}
								}
							}
						}
					}

					for(int j3 = 0; j3 < i; ++j3) {
						if (isAirOrLeaves(worldIn, position.up(j3))) {
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
		}*/
	}
}