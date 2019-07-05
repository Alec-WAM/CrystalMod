package alec_wam.CrystalMod.world.features;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class RandomCrystalTreeFeature extends Feature<NoFeatureConfig> {
	public static final CrystalTreeFeature CRYSTAL_TREE_BLUE = new CrystalTreeFeature(NoFeatureConfig::deserialize, false, EnumCrystalColorSpecial.BLUE, false);
    public static final CrystalTreeFeature CRYSTAL_TREE_RED = new CrystalTreeFeature(NoFeatureConfig::deserialize, false, EnumCrystalColorSpecial.RED, false);
    public static final CrystalTreeFeature CRYSTAL_TREE_GREEN = new CrystalTreeFeature(NoFeatureConfig::deserialize, false, EnumCrystalColorSpecial.GREEN, false);
    public static final CrystalTreeFeature CRYSTAL_TREE_DARK = new CrystalTreeFeature(NoFeatureConfig::deserialize, false, EnumCrystalColorSpecial.DARK, false);
	
    public RandomCrystalTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49878_1_) {
		super(p_i49878_1_);
	}
	
    @Override
	public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      EnumCrystalColorSpecial bushType = EnumCrystalColorSpecial.values()[MathHelper.nextInt(p_212245_3_, 0, 3)]; //Choose a random color in the list
      CrystalTreeFeature treeFeature;
      switch(bushType){
      	case BLUE: default:{
      		treeFeature = CRYSTAL_TREE_BLUE;
      		break;
      	}
      	case RED: {
      		treeFeature = CRYSTAL_TREE_RED;
      		break;
      	}
      	case GREEN: {
      		treeFeature = CRYSTAL_TREE_GREEN;
      		break;
      	}
      	case DARK: {
      		treeFeature = CRYSTAL_TREE_DARK;
      		break;
      	}
      }
      if(treeFeature.place(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_, p_212245_5_)){
    	  //System.out.println("Generating a " + bushType + " tree at "+ (p_212245_4_.getX()+" "+ p_212245_4_.getY()+" "+p_212245_4_.getZ()));
    	  return true;
      }
      return false;
   }
}