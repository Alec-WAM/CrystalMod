package alec_wam.CrystalMod.blocks.crops.material;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public final class ModelSeed implements IModel, IModelCustomData
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
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of();
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
    
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
    	ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);
        
        TRSRTransformation transform = state.apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity());
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        if (textureBorder != null)
        {
        	TextureAtlasSprite base = bakedTextureGetter.apply(textureBorder);
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, base, EnumFacing.NORTH, 0xffffff));
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, base, EnumFacing.SOUTH, 0xffffff));
        } 
        ISeedInfo seedInfo = info;
        
        if(seedInfo !=null){
	        TextureAtlasSprite base = bakedTextureGetter.apply(textureSeed);
	        //Have to fix alpha
	        int color = new Color(seedInfo.getSeedColor()).getRGB();
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, base, EnumFacing.NORTH, color));
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, base, EnumFacing.SOUTH, color));
	        if (seedInfo.getOverlayType() !=null && seedInfo.getOverlayType().getSpite() !=null)
	        {
	        	color = new Color(seedInfo.getOverlayColor()).getRGB();
	            base = bakedTextureGetter.apply(seedInfo.getOverlayType().getSpite());
	            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, base, EnumFacing.NORTH, color));
	            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, 0, base, EnumFacing.SOUTH, color));
	        }
        }
        return new BakedSeed(this, builder.build(), format, Maps.immutableEnumMap(transformMap), Maps.<String, IBakedModel>newHashMap());
    }
    
    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
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

            BakedSeed model = (BakedSeed)originalModel;

            String name = crop.getUnlocalizedName();
            String cacheName = name+"v17";
            if (!model.cache.containsKey(cacheName))
            {
                IModel parent = model.parent.process(ImmutableMap.of("crop", name));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
                {
                    public TextureAtlasSprite apply(ResourceLocation location)
                    {
                        return RenderUtil.getSprite(location);
                    }
                };

                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, textureGetter);
                model.cache.put(cacheName, bakedModel);
                return bakedModel;
            }

            return model.cache.get(cacheName);
        }
    }

    // the dynamic bucket is based on the empty bucket
    private static final class BakedSeed implements IPerspectiveAwareModel
    {

        private final ModelSeed parent;
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final ImmutableMap<TransformType, TRSRTransformation> transforms;
        private final ImmutableList<BakedQuad> quads;
        private final VertexFormat format;

        public BakedSeed(ModelSeed parent,
                              ImmutableList<BakedQuad> quads, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
                              Map<String, IBakedModel> cache)
        {
            this.quads = quads;
            this.format = format;
            this.parent = parent;
            this.transforms = transforms;
            this.cache = cache;
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return BakedSeedOverrideHandler.INSTANCE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
        {
            if(side == null) return quads;
            return ImmutableList.of();
        }

        public boolean isAmbientOcclusion() { return true;  }
        public boolean isGui3d() { return false; }
        public boolean isBuiltInRenderer() { return false; }
        public TextureAtlasSprite getParticleTexture() { return RenderUtil.getMissingSprite(); }
        public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
    }
}