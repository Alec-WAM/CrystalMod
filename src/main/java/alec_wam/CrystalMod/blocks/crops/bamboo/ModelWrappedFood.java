package alec_wam.CrystalMod.blocks.crops.bamboo;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.crops.bamboo.ItemWrappedFood.WrappedFoodType;
import alec_wam.CrystalMod.client.model.dynamic.ModelCustomLayers;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public final class ModelWrappedFood extends ModelCustomLayers
{
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("crystalmod", "wrappedfood"), "inventory");

    public static final IModel MODEL = new ModelWrappedFood(WrappedFoodType.APPLE);
    public final WrappedFoodType foodType;
    private static final Map<WrappedFoodType, ResourceLocation> TEXTURE_MAP = Maps.newHashMap();
    
    static {
    	for(WrappedFoodType t : WrappedFoodType.values()){
    		TEXTURE_MAP.put(t, CrystalMod.resourceL("items/food/eucalyptus/"+t.getName()));
    	}
    }
    
    public ModelWrappedFood(WrappedFoodType type)
    {
       foodType = type;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        builder.add(TEXTURE_MAP.get(foodType));
        return builder.build();
    }
    
    public void addLayers(ImmutableList.Builder<BakedQuad> builder, VertexFormat format, Optional<TRSRTransformation> transform, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
    	TextureAtlasSprite base = bakedTextureGetter.apply(TEXTURE_MAP.get(foodType));
        builder.addAll(getQuadsForSprite(Color.WHITE.getRGB(), base, format, transform));
    }
    
    @Override
    public TextureAtlasSprite getParticle(){
    	return RenderUtil.getSprite(TEXTURE_MAP.get(foodType));
    }
    
    public ItemOverrideList getOverrides(){
    	return BakedOverrideHandler.INSTANCE;
    }

    @Override
    public ModelWrappedFood process(ImmutableMap<String, String> customData)
    {
        String cropName = customData.get("food");
        WrappedFoodType type = WrappedFoodType.byName(cropName);

        return new ModelWrappedFood(type);
    }

    public enum LoaderWrappedFood implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals("crystalmod") && modelLocation.getResourcePath().contains("wrappedfood");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation)
        {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // no need to clear cache since we create a new model instance
        }
    }

    private static final class BakedOverrideHandler extends ItemOverrideList
    {
        public static final BakedOverrideHandler INSTANCE = new BakedOverrideHandler();
        private final Map<String, IBakedModel> cache = Maps.newHashMap();
        private BakedOverrideHandler()
        {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
        {
            WrappedFoodType food = ItemWrappedFood.getFood(stack);

            // not a crop item
            if (food == null)
            {
                return originalModel;
            }
            //BakedSeed model = (BakedSeed)originalModel;
            BakedItemModel model = (BakedItemModel)originalModel;
            String name = food.getUnlocalizedName();
            String cacheName = name+"v1";
            if (!cache.containsKey(cacheName))
            {
                IModel parent = model.parent.process(ImmutableMap.of("food", name));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
                {
                    @Override
					public TextureAtlasSprite apply(ResourceLocation location)
                    {
                        return RenderUtil.getSprite(location);
                    }
                };

                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, textureGetter);
                cache.put(cacheName, bakedModel);
                return bakedModel;
            }

            return cache.get(cacheName);
        }
    }
}