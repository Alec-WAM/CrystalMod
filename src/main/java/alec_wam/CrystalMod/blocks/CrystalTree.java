package alec_wam.CrystalMod.blocks;

import java.util.Random;

import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.world.features.CrystalTreeFeature;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class CrystalTree extends Tree {

	private EnumCrystalColorSpecial type;
	public CrystalTree(EnumCrystalColorSpecial type){
		this.type = type;
	}
	
	@Override
	protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
		return (AbstractTreeFeature<NoFeatureConfig>)(new CrystalTreeFeature(NoFeatureConfig::deserialize, true, type, false));
	}

}
