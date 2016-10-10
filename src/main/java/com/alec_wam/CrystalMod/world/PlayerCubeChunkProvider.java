package com.alec_wam.CrystalMod.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

public class PlayerCubeChunkProvider implements IChunkGenerator
{
	World worldObj;

	public PlayerCubeChunkProvider(World worldObj)
	{
		this.worldObj = worldObj;
	}

	@Override
	public Chunk provideChunk(int x, int z)
	{
		ChunkPrimer chunkprimer = new ChunkPrimer();

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public void populate(int p_73153_2_, int p_73153_3_)
	{
	}

	@Override
	public boolean generateStructures(Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
	{
		return false;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		return new ArrayList<SpawnListEntry>();
	}

	@Override
	public BlockPos getStrongholdGen(World worldIn, String p_180513_2_, BlockPos p_180513_3_)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
	{
	}

}
