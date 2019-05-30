package alec_wam.CrystalMod.world.features;

import java.util.Random;

import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class RandomCrystalTreeFeature extends Feature<NoFeatureConfig> {
	public static final CrystalTreeFeature CRYSTAL_TREE_BLUE = new CrystalTreeFeature(false, EnumCrystalColorSpecial.BLUE, false);
    public static final CrystalTreeFeature CRYSTAL_TREE_RED = new CrystalTreeFeature(false, EnumCrystalColorSpecial.RED, false);
    public static final CrystalTreeFeature CRYSTAL_TREE_GREEN = new CrystalTreeFeature(false, EnumCrystalColorSpecial.GREEN, false);
    public static final CrystalTreeFeature CRYSTAL_TREE_DARK = new CrystalTreeFeature(false, EnumCrystalColorSpecial.DARK, false);
	@Override
	public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
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
      if(treeFeature.func_212245_a(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_, p_212245_5_)){
    	  //System.out.println("Generating a " + bushType + " tree at "+ (p_212245_4_.getX()+" "+ p_212245_4_.getY()+" "+p_212245_4_.getZ()));
    	  return true;
      }
      return false;
   }
}