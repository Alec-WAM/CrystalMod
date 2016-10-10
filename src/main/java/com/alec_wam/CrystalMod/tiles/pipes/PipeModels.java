package com.alec_wam.CrystalMod.tiles.pipes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public class PipeModels {

	private final Set<ResourceLocation> models = new HashSet<ResourceLocation>();

	private boolean initialized = false;

	public void registerModels( Collection<ResourceLocation> partModels )
	{
		if( initialized )
		{
			throw new IllegalStateException( "Cannot register models after the pre-initialization phase!" );
		}

		models.addAll( partModels );
	}

	public Set<ResourceLocation> getModels()
	{
		return models;
	}

	public void setInitialized( boolean initialized )
	{
		this.initialized = initialized;
	}
	
}
