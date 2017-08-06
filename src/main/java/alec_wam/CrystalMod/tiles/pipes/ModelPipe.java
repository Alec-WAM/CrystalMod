package alec_wam.CrystalMod.tiles.pipes;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;


/**
 * The built-in model for the cable bus block.
 */
public class ModelPipe implements IModel
{
	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		return ImmutableSet.of();
	}

	@Override
	public IBakedModel bake( IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
	{
		return new ModelPipeBaked();
	}

	@Override
	public IModelState getDefaultState()
	{
		return TRSRTransformation.identity();
	}
}
