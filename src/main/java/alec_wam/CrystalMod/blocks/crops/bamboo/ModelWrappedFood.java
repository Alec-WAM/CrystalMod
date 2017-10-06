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

import alec_wam.CrystalMod.client.model.dynamic.ModelCustomLayers;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
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

    public static final IModel MODEL = new ModelWrappedFood();
    private ItemStack food = ItemStackTools.getEmptyStack();
    private final ResourceLocation textureOverlay = new ResourceLocation("crystalmod:items/food/eucalyptus_overlay_basic");
    
    public ModelWrappedFood()
    {
        this(ItemStackTools.getEmptyStack());
    }

    public ModelWrappedFood(ItemStack food)
    {
    	this.food = food;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        if (this.textureOverlay !=null)
            builder.add(textureOverlay);
        return builder.build();
    }
    
    public void addLayers(ImmutableList.Builder<BakedQuad> builder, VertexFormat format, Optional<TRSRTransformation> transform, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
    	if(ItemStackTools.isValid(food)){
     		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(food, null, null);
     		builder.addAll(model.getQuads(null, null, 0L));
     	}
    	if (textureOverlay != null)
        {
        	TextureAtlasSprite base = bakedTextureGetter.apply(textureOverlay);
            builder.addAll(getQuadsForSprite(Color.WHITE.getRGB(), base, format, transform));
        } 
    }
    
    public ItemOverrideList getOverrides(){
    	return BakedOverrideHandler.INSTANCE;
    }

    @Override
    public ModelWrappedFood process(ImmutableMap<String, String> customData)
    {
        String cropName = customData.get("food");
        ItemStack stack = ItemUtil.getStackFromString(cropName, true);

        if (!ItemStackTools.isValid(stack)) stack = new ItemStack(Items.APPLE);
        return new ModelWrappedFood(stack);
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
            ItemStack food = ItemWrappedFood.getFood(stack);

            // not a crop item
            if (!ItemStackTools.isValid(food))
            {
                return originalModel;
            }

            //BakedSeed model = (BakedSeed)originalModel;
            BakedItemModel model = (BakedItemModel)originalModel;
            String name = ItemUtil.getStringForItemStack(food, true, false);
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