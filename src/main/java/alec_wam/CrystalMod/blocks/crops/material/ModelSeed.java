package alec_wam.CrystalMod.blocks.crops.material;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.CrystalModAPI;
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

public final class ModelSeed extends ModelCustomLayers
{
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("crystalmod", "materialseed"), "inventory");

    public static final IModel MODEL = new ModelSeed();
    private final ISeedInfo info;
    private final ResourceLocation textureBorder = new ResourceLocation("crystalmod:items/crop/seed_background");
    private final ResourceLocation textureSeed = new ResourceLocation("crystalmod:items/crop/seed_overlay");
    
    public ModelSeed()
    {
        this(null);
    }

    public ModelSeed(ISeedInfo info)
    {
    	this.info = info;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        if (this.textureBorder !=null)
            builder.add(textureBorder);
        if (this.textureSeed !=null)
            builder.add(textureSeed);
        return builder.build();
    }
    
    public void addLayers(ImmutableList.Builder<BakedQuad> builder, VertexFormat format, Optional<TRSRTransformation> transform, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
    	if (textureBorder != null)
        {
        	TextureAtlasSprite base = bakedTextureGetter.apply(textureBorder);
            builder.addAll(getQuadsForSprite(0, base, format, transform));
        } 
        ISeedInfo seedInfo = info;
        
        if(seedInfo !=null){
	        TextureAtlasSprite base = bakedTextureGetter.apply(textureSeed);
	        int color = new Color(seedInfo.getSeedColor()).getRGB();
	        builder.addAll(getQuadsForSprite(color, base, format, transform));
	        if (seedInfo.getOverlayType() !=null && seedInfo.getOverlayType().getSpite() !=null)
	        {
	        	base = bakedTextureGetter.apply(seedInfo.getOverlayType().getSpite());
	        	color = new Color(seedInfo.getOverlayColor()).getRGB();
	        	builder.addAll(getQuadsForSprite(color, base, format, transform));
	        }
        }
    }
    
    public ItemOverrideList getOverrides(){
    	return BakedSeedOverrideHandler.INSTANCE;
    }

    @Override
    public ModelSeed process(ImmutableMap<String, String> customData)
    {
        String cropName = customData.get("crop");
        IMaterialCrop crop = CrystalModAPI.getCrop(cropName);

        if (crop == null) crop = ModCrops.DIRT;
        return new ModelSeed(crop.getSeedInfo());
    }

    public enum LoaderSeeds implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals("crystalmod") && modelLocation.getResourcePath().contains("materialseed");
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

    private static final class BakedSeedOverrideHandler extends ItemOverrideList
    {
        public static final BakedSeedOverrideHandler INSTANCE = new BakedSeedOverrideHandler();
        private final Map<String, IBakedModel> cache = Maps.newHashMap();
        private BakedSeedOverrideHandler()
        {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
        {
            IMaterialCrop crop = ItemMaterialSeed.getCrop(stack);

            // not a crop item
            if (crop == null)
            {
                return originalModel;
            }

            //BakedSeed model = (BakedSeed)originalModel;
            BakedItemModel model = (BakedItemModel)originalModel;
            String name = crop.getUnlocalizedName();
            String cacheName = name+"v11";
            if (!cache.containsKey(cacheName))
            {
                IModel parent = model.parent.process(ImmutableMap.of("crop", name));
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