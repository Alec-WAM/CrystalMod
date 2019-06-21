package alec_wam.CrystalMod.compatibility.materials;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class ModelDust implements IUnbakedModel{

    public static final IUnbakedModel MODEL = new ModelDust();
    
    private final int color;
    
    public ModelDust(){
    	this(-1);
    }
    
    public ModelDust(int color){
    	this.color = color;
    }

	@Override
	public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
		IModelState state = sprite.getState();
		ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);
		TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite particleSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        
        TextureAtlasSprite texture = spriteGetter.apply(DUST_TEXTURE);
        builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, texture, Direction.NORTH, color != -1 ? color : 0xFFFFFFFF, 0));
        builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, texture, Direction.SOUTH, color != -1 ? color : 0xFFFFFFFF, 0));
        particleSprite = texture;
        
        return new BakedModelDust(bakery, this, builder.build(), particleSprite, format, Maps.immutableEnumMap(transformMap), Maps.newHashMap(), transform.isIdentity());
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	public static final ResourceLocation DUST_TEXTURE = new ResourceLocation("crystalmod:items/dust");
	
	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        if (DUST_TEXTURE != null)
            builder.add(DUST_TEXTURE);

        return builder.build();
	}
	
	@Override
    public ModelDust process(ImmutableMap<String, String> customData)
    {
        String name = customData.get("dust");
        ItemMaterial material = MaterialLoader.getMaterial(name);        
        int color = -1;
        if(material !=null){
        	color = material.getMaterialColor();
        }
        return new ModelDust(color);
    }
	
	private static final class BakedModelDust extends BakedItemModel
    {
        private final ModelDust parent;
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final VertexFormat format;
        public final ImmutableMap<TransformType, TRSRTransformation> transforms;
        BakedModelDust(ModelBakery bakery,
                       ModelDust parent,
                       ImmutableList<BakedQuad> quads,
                       TextureAtlasSprite particle,
                       VertexFormat format,
                       ImmutableMap<TransformType, TRSRTransformation> transforms,
                       Map<String, IBakedModel> cache,
                       boolean untransformed)
        {
            super(quads, particle, transforms, new BakedDustOverrideHandler(bakery), untransformed);
            this.transforms = transforms;
            this.format = format;
            this.parent = parent;
            this.cache = cache;
        }
    }
	
	private static final class BakedDustOverrideHandler extends ItemOverrideList
    {
        private final ModelBakery bakery;
        
        private BakedDustOverrideHandler(ModelBakery bakery)
        {
            this.bakery = bakery;
        }

        @Override
        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity)
        {
        	BakedModelDust model = (BakedModelDust)originalModel;
        	String name = "";
        	
        	ResourceLocation registryName = stack.getItem().getRegistryName();
        	String path = registryName.getPath();
        	if(path.startsWith("dust_")){
        		name = path.substring(5);
        	}        	
        	if(!name.isEmpty()){
	            if (!model.cache.containsKey(name))
	            {
	                IUnbakedModel parent = model.parent.process(ImmutableMap.of("dust", name));
	                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
	                textureGetter = location -> Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
	
	                IBakedModel bakedModel = parent.bake(bakery, textureGetter, new SimpleModelState(model.transforms), model.format);
	                model.cache.put(name, bakedModel);
	                return bakedModel;
	            }
	            return model.cache.get(name);
        	}
            return originalModel;
        }
    }

}
