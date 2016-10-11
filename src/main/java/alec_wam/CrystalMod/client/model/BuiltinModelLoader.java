package alec_wam.CrystalMod.client.model;

import java.util.Map;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.pipes.ModelPipe;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;


/**
 * Manages built-in models.
 */
public class BuiltinModelLoader implements ICustomModelLoader
{

	private final Map<String, IModel> builtInModels;

	public BuiltinModelLoader( Map<String, IModel> builtInModels )
	{
		this.builtInModels = ImmutableMap.copyOf( builtInModels );
	}

	@Override
	public boolean accepts( ResourceLocation modelLocation )
	{
		
		if( !modelLocation.getResourceDomain().equals( CrystalMod.MODID.toLowerCase() ) )
		{
			return false;
		}
		String loc = ""+modelLocation;
		if(loc.contains("block/pipe") || loc.contains("builtin")){
			ModLogger.info("Built In: "+loc);
		}
		if(loc.equals("crystalmod:models/block/pipe") || loc.equals("crystalmod:models/block/builtin/pipe"))return true;
		return builtInModels.containsKey( modelLocation.getResourcePath() );
	}

	@Override
	public IModel loadModel( ResourceLocation modelLocation ) throws Exception
	{
		String loc = ""+modelLocation;
		if(loc.equals("crystalmod:models/block/pipe") || loc.equals("crystalmod:models/block/builtin/pipe"))return new ModelPipe();
		//if(loc.contains("block/pipe") || loc.contains("builtin"))return new ModelPipe();
		return builtInModels.get( modelLocation.getResourcePath() );
	}

	@Override
	public void onResourceManagerReload( IResourceManager resourceManager )
	{
		for( IModel model : builtInModels.values() )
		{
			if( model instanceof IResourceManagerReloadListener )
			{
				( (IResourceManagerReloadListener) model ).onResourceManagerReload( resourceManager );
			}
		}
	}
}
