package alec_wam.CrystalMod.world.features;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class MinableRandomConfig implements IFeatureConfig {
   public final OreFeatureConfig.FillerBlockType canReplace;
   public final int minSize;
   public final int maxSize;
   public final BlockState[] states;

   public MinableRandomConfig(OreFeatureConfig.FillerBlockType canReplaceIn, BlockState[] statesIn, int minSize, int maxSize) {
      this.minSize = minSize;
      this.maxSize = maxSize;
      this.states = statesIn;
      this.canReplace = canReplaceIn;
   }
	
   @Override
   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
	   T t = p_214634_1_.createList(Lists.newArrayList(states).stream().map((state) -> {
	         return BlockState.serialize(p_214634_1_, state).getValue();
	   }));
	   return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("minSize"), p_214634_1_.createInt(this.minSize), p_214634_1_.createString("maxSize"), p_214634_1_.createInt(this.maxSize), p_214634_1_.createString("target"), p_214634_1_.createString(this.canReplace.func_214737_a()), p_214634_1_.createString("states"), t)));
   }

   public static MinableRandomConfig load(Dynamic<?> p_214641_0_) {
	   int minSize = p_214641_0_.get("minSize").asInt(0);
	   int maxSize = p_214641_0_.get("maxSize").asInt(0);
	   OreFeatureConfig.FillerBlockType orefeatureconfig$fillerblocktype = OreFeatureConfig.FillerBlockType.func_214736_a(p_214641_0_.get("target").asString(""));
	   List<BlockState> states = p_214641_0_.get("states").asList(BlockState::deserialize);
	   return new MinableRandomConfig(orefeatureconfig$fillerblocktype, states.toArray(new BlockState[0]), minSize, maxSize);
   }
}