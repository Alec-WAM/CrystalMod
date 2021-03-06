package alec_wam.CrystalMod.client.model.dynamic;

import java.awt.Color;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
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
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

@SuppressWarnings("deprecation")
public abstract class ModelCustomLayers implements IModel, IModelCustomData
{
	@Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of();
    }
    
	@Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }
	
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
    	ImmutableMap.Builder<TransformType, TRSRTransformation> mapbuilder = ImmutableMap.builder();
        {
	    	Vector3f vector3f = new Vector3f(0, 0, 0);
	        Vector3f vector3f1 = new Vector3f(4f, 4f, 4f);
	        vector3f1.scale(0.0625F);
	        Vector3f vector3f2 = new Vector3f(0.5f, 0.5f, 0.5f);
	        ItemTransformVec3f GROUND = new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
	    	
	        mapbuilder.put(TransformType.GROUND, new TRSRTransformation(GROUND));
        }
        {
        	Vector3f vector3f = new Vector3f(0, -90, 25);
            Vector3f vector3f1 = new Vector3f(15.13f, 3.2f, 5.13f);
            vector3f1.scale(0.0625F);
            Vector3f vector3f2 = new Vector3f(0.68f, 0.68f, 0.68f);
            ItemTransformVec3f RIGHT_HAND = new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
            mapbuilder.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TRSRTransformation(RIGHT_HAND));
            mapbuilder.put(TransformType.FIRST_PERSON_LEFT_HAND, new TRSRTransformation(RIGHT_HAND));
        }
        {
        	Vector3f vector3f = new Vector3f(0, 0, 0);
            Vector3f vector3f1 = new Vector3f(5.13f, 7f, 4.13f);
            vector3f1.scale(0.0625F);
            Vector3f vector3f2 = new Vector3f(0.55f, 0.55f, 0.55f);
            ItemTransformVec3f RIGHT_HAND = new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
            mapbuilder.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TRSRTransformation(RIGHT_HAND));
            vector3f = new Vector3f(0, 0, 0);
            vector3f1 = new Vector3f(2.13f, 7f, 4.13f);
            vector3f1.scale(0.0625F);
            vector3f2 = new Vector3f(0.55f, 0.55f, 0.55f);
            ItemTransformVec3f LEFT_HAND = new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
            mapbuilder.put(TransformType.THIRD_PERSON_LEFT_HAND, new TRSRTransformation(LEFT_HAND));
        }
        
        {
        	Vector3f vector3f = new Vector3f(0, 180, 0);
            Vector3f vector3f1 = new Vector3f(16, 0, 16);
            vector3f1.scale(0.0625F);
            Vector3f vector3f2 = new Vector3f(1, 1, 1);
            ItemTransformVec3f FIXED = new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
            mapbuilder.put(TransformType.FIXED, new TRSRTransformation(FIXED));
        }
    	
    	ImmutableMap<TransformType, TRSRTransformation> map = mapbuilder.build();
    	
    	Optional<TRSRTransformation> transform = state.apply(Optional.<IModelPart>absent());
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        addLayers(builder, format, transform, bakedTextureGetter);
        return new BakedItemModel(this, format, builder.build(), getParticle(), map, getOverrides(), null);
    }
    
    public TextureAtlasSprite getParticle(){
    	return RenderUtil.getMissingSprite();
    }
    
    public abstract void addLayers(ImmutableList.Builder<BakedQuad> builder, VertexFormat format, Optional<TRSRTransformation> transform, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter);
    
    public abstract ItemOverrideList getOverrides();
    
    public static ImmutableList<BakedQuad> getQuadsForSprite(int color, TextureAtlasSprite sprite, VertexFormat format, Optional<TRSRTransformation> transform)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        int uMax = sprite.getIconWidth();
        int vMax = sprite.getIconHeight();

        BitSet faces = new BitSet((uMax + 1) * (vMax + 1) * 4);
        for(int f = 0; f < sprite.getFrameCount(); f++)
        {
            int[] pixels = sprite.getFrameTextureData(f)[0];
            boolean ptu;
            boolean[] ptv = new boolean[uMax];
            Arrays.fill(ptv, true);
            for(int v = 0; v < vMax; v++)
            {
                ptu = true;
                for(int u = 0; u < uMax; u++)
                {
                    boolean t = isTransparent(pixels, uMax, vMax, u, v);
                    if(ptu && !t) // left - transparent, right - opaque
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.WEST, color, sprite, uMax, vMax, u, v);
                    }
                    if(!ptu && t) // left - opaque, right - transparent
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.EAST, color, sprite, uMax, vMax, u, v);
                    }
                    if(ptv[u] && !t) // up - transparent, down - opaque
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.UP, color, sprite, uMax, vMax, u, v);
                    }
                    if(!ptv[u] && t) // up - opaque, down - transparent
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.DOWN, color, sprite, uMax, vMax, u, v);
                    }
                    ptu = t;
                    ptv[u] = t;
                }
                if(!ptu) // last - opaque
                {
                    addSideQuad(builder, faces, format, transform, EnumFacing.EAST, color, sprite, uMax, vMax, uMax, v);
                }
            }
            // last line
            for(int u = 0; u < uMax; u++)
            {
                if(!ptv[u])
                {
                    addSideQuad(builder, faces, format, transform, EnumFacing.DOWN, color, sprite, uMax, vMax, u, vMax);
                }
            }
        }
        // front
        builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, color,
            0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
            0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(),
            1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
            1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()
        ));
        // back
        builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, color,
            0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
            1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(),
            1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
            0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()
        ));
        return builder.build();
    }

    private static boolean isTransparent(int[] pixels, int uMax, int vMax, int u, int v)
    {
        return (pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF) == 0;
    }

    private static void addSideQuad(ImmutableList.Builder<BakedQuad> builder, BitSet faces, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int uMax, int vMax, int u, int v)
    {
        int si = side.ordinal();
        if(si > 4) si -= 2;
        int index = (vMax + 1) * ((uMax + 1) * si + u) + v;
        if(!faces.get(index))
        {
            faces.set(index);
            builder.add(buildSideQuad(format, transform, side, tint, sprite, u, v));
        }
    }

    private static BakedQuad buildSideQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int u, int v)
    {
        final float eps0 = 30e-5f;
        final float eps1 = 45e-5f;
        final float eps2 = .5f;
        final float eps3 = .5f;
        float x0 = (float)u / sprite.getIconWidth();
        float y0 = (float)v / sprite.getIconHeight();
        float x1 = x0, y1 = y0;
        float z1 = 7.5f / 16f - eps1, z2 = 8.5f / 16f + eps1;
        switch(side)
        {
        case WEST:
            z1 = 8.5f / 16f + eps1;
            z2 = 7.5f / 16f - eps1;
        case EAST:
            y1 = (v + 1f) / sprite.getIconHeight();
            break;
        case DOWN:
            z1 = 8.5f / 16f + eps1;
            z2 = 7.5f / 16f - eps1;
        case UP:
            x1 = (u + 1f) / sprite.getIconWidth();
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }
        float u0 = 16f * (x0 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float u1 = 16f * (x1 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float v0 = 16f * (1f - y0 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        float v1 = 16f * (1f - y1 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        switch(side)
        {
        case WEST:
        case EAST:
            y0 -= eps1;
            y1 += eps1;
            v0 -= eps2 / sprite.getIconHeight();
            v1 += eps2 / sprite.getIconHeight();
            break;
        case DOWN:
        case UP:
            x0 -= eps1;
            x1 += eps1;
            u0 += eps2 / sprite.getIconWidth();
            u1 -= eps2 / sprite.getIconWidth();
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }
        switch(side)
        {
        case WEST:
            x0 += eps0;
            x1 += eps0;
            break;
        case EAST:
            x0 -= eps0;
            x1 -= eps0;
            break;
        case DOWN:
            y0 -= eps0;
            y1 -= eps0;
            break;
        case UP:
            y0 += eps0;
            y1 += eps0;
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }
        return buildQuad(
            format, transform, side.getOpposite(), sprite, tint, // getOpposite is related either to the swapping of V direction, or something else
            x0, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0),
            x1, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
            x1, y1, z2, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
            x0, y0, z2, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0)
        );
    }

    private static final BakedQuad buildQuad(
        VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, TextureAtlasSprite sprite, int color,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3)
    {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(-1);
        builder.setQuadOrientation(side);
        builder.setTexture(sprite);
        putVertex(builder, format, transform, side, color, x0, y0, z0, u0, v0);
        putVertex(builder, format, transform, side, color, x1, y1, z1, u1, v1);
        putVertex(builder, format, transform, side, color, x2, y2, z2, u2, v2);
        putVertex(builder, format, transform, side, color, x3, y3, z3, u3, v3);
        return builder.build();
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int color, float x, float y, float z, float u, float v)
    {
        Vector4f vec = new Vector4f();
        for(int e = 0; e < format.getElementCount(); e++)
        {
            switch(format.getElement(e).getUsage())
            {
            case POSITION:
                if(transform.isPresent())
                {
                    vec.x = x;
                    vec.y = y;
                    vec.z = z;
                    vec.w = 1;
                    transform.get().getMatrix().transform(vec);
                    builder.put(e, vec.x, vec.y, vec.z, vec.w);
                }
                else
                {
                    builder.put(e, x, y, z, 1);
                }
                break;
            case COLOR:
            	float r = ((color >> 16) & 0xFF) / 255f; // red
                float g = ((color >> 8) & 0xFF) / 255f; // green
                float b = ((color >> 0) & 0xFF) / 255f; // blue
                float a = ((color >> 24) & 0xFF) / 255f; // alpha
                builder.put(e, r, g, b, a);
                break;
            case UV: if(format.getElement(e).getIndex() == 0)
            {
                builder.put(e, u, v, 0f, 1f);
                break;
            }
            case NORMAL:
                builder.put(e, side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ(), 0f);
                break;
            default:
                builder.put(e);
                break;
            }
        }
    }
    
    // the dynamic bucket is based on the empty bucket
    public static final class BakedSeed implements IPerspectiveAwareModel
    {

        private final ImmutableMap<TransformType, TRSRTransformation> transforms;
        private final ImmutableList<BakedQuad> quads;
        private final ItemOverrideList overrides;
        public BakedSeed(ModelCustomLayers parent, ImmutableList<BakedQuad> quads, VertexFormat format, ItemOverrideList override, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache)
        {
            this.quads = quads;
            this.transforms = transforms;
            this.overrides = override;
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return overrides;
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

        @Override
		public boolean isAmbientOcclusion() { return true;  }
        @Override
		public boolean isGui3d() { return false; }
        @Override
		public boolean isBuiltInRenderer() { return false; }
        @Override
		public TextureAtlasSprite getParticleTexture() { return RenderUtil.getMissingSprite(); }
        @Override
		public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
    }
    
    public static final class BakedItemModel implements IPerspectiveAwareModel
    {
    	public final ModelCustomLayers parent;
    	public final VertexFormat format;
    	private final ImmutableList<BakedQuad> quads;
        private final TextureAtlasSprite particle;
        public final ImmutableMap<TransformType, TRSRTransformation> transforms;
        private final IBakedModel otherModel;
        private final boolean isCulled;
        private final ItemOverrideList overrides;

        public BakedItemModel(ModelCustomLayers parent, VertexFormat format, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<TransformType, TRSRTransformation> transforms, ItemOverrideList overrides, @Nullable IBakedModel otherModel)
        {
        	this.parent = parent;
        	this.format = format;
            this.quads = quads;
            this.particle = particle;
            this.transforms = transforms;
            this.overrides = overrides;
            if(otherModel != null)
            {
                this.otherModel = otherModel;
                this.isCulled = true;
            }
            else
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                for(BakedQuad quad : quads)
                {
                    if(quad.getFace() == EnumFacing.SOUTH)
                    {
                        builder.add(quad);
                    }
                }
                this.otherModel = new BakedItemModel(parent, format, builder.build(), particle, transforms, overrides, this);
                isCulled = false;
            }
        }

        @Override
		public boolean isAmbientOcclusion() { return true; }
        @Override
		public boolean isGui3d() { return false; }
        @Override
		public boolean isBuiltInRenderer() { return false; }
        @Override
		public TextureAtlasSprite getParticleTexture() { return particle; }
        @Override
		public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
        @Override
		public ItemOverrideList getOverrides() { return overrides; }
        @Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
        {
            if(side == null) return quads;
            return ImmutableList.of();
        }

        @Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type)
        {
            Pair<? extends IBakedModel, Matrix4f> pair = IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, type);
            if(type == TransformType.GUI && !isCulled && pair.getRight() == null)
            {
                return Pair.of(otherModel, null);
            }
            else if(type != TransformType.GUI && isCulled)
            {
                return Pair.of(otherModel, pair.getRight());
            }
            return pair;
        }
    }
}