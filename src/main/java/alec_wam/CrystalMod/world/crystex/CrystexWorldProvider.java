package alec_wam.CrystalMod.world.crystex;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.world.CustomBiomeProvider;
import alec_wam.CrystalMod.world.ModDimensions;
import alec_wam.CrystalMod.world.biomes.ModBiomes;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;

public class CrystexWorldProvider extends WorldProvider
{
	public CrystexWorldProvider()
	{
		super();
	}

	@Override
	protected void init()
    {
        this.hasSkyLight = true;
        List<Biome> spawnList = 
        		Lists.newArrayList(
        				ModBiomes.BLUE_CRYSTEX_FOREST, 
        				ModBiomes.RED_CRYSTEX_FOREST,
        				ModBiomes.GREEN_CRYSTEX_FOREST,
        				ModBiomes.DARK_CRYSTEX_FOREST
        		);
        List<Biome> commonList = 
        		Lists.newArrayList(
        				ModBiomes.BLUE_CRYSTEX_FOREST, 
        				ModBiomes.RED_CRYSTEX_FOREST,
        				ModBiomes.GREEN_CRYSTEX_FOREST,
        				ModBiomes.DARK_CRYSTEX_FOREST,
        				ModBiomes.BLUE_CRYSTEX_PLAINS, 
        				ModBiomes.RED_CRYSTEX_PLAINS,
        				ModBiomes.GREEN_CRYSTEX_PLAINS,
        				ModBiomes.DARK_CRYSTEX_PLAINS
        		);
        List<Biome> rareList = Lists.newArrayList(/*ModBiomes.BAMBOO_FOREST*/);
        this.biomeProvider = new CustomBiomeProvider(this.world, spawnList, commonList, rareList);
    }
	
	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new CrystexChunkProvider(this.world, this.world.getSeed());
	}

	@Override
	public DimensionType getDimensionType() {
		return DimensionType.getById(ModDimensions.CRYSTEX_ID);
	}
}
