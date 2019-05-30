package alec_wam.CrystalMod.client;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.vecmath.Vector3f;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.Face;
import net.minecraftforge.client.model.obj.OBJModel.Group;
import net.minecraftforge.client.model.obj.OBJModel.Normal;
import net.minecraftforge.client.model.obj.OBJModel.TextureCoordinate;
import net.minecraftforge.client.model.obj.OBJModel.Vertex;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;

@SuppressWarnings("deprecation")
public class BakedModelHelper {

    public static ImmutableList<BakedQuad> buildQuads(OBJModel model, VertexFormat format, IModelState modelState, ImmutableMap<String, TextureAtlasSprite> textures)
    {
        List<BakedQuad> quads = Lists.newArrayList();
        Collections.synchronizedSet(new LinkedHashSet<BakedQuad>());
        Set<Face> faces = Collections.synchronizedSet(new LinkedHashSet<Face>());
        Optional<TRSRTransformation> transform = Optional.empty();
        for (Group g : model.getMatLib().getGroups().values())
        {
        	if(modelState.apply(Optional.of(Models.getHiddenModelPart(ImmutableList.of(g.getName())))).isPresent())
            {
                continue;
            }
            transform = modelState.apply(Optional.empty());
            faces.addAll(g.applyTransform(transform));
        }
        for (Face f : faces)
        {
            TextureAtlasSprite sprite;
        	if (model.getMatLib().getMaterial(f.getMaterialName()).isWhite())
            {
                for (Vertex v : f.getVertices())
                {//update material in each vertex
                    if (!v.getMaterial().equals(model.getMatLib().getMaterial(v.getMaterial().getName())))
                    {
                        v.setMaterial(model.getMatLib().getMaterial(v.getMaterial().getName()));
                    }
                }
                sprite = ModelLoader.White.INSTANCE;
            }
            else sprite = textures.get(f.getMaterialName());
            UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
            builder.setContractUVs(true);
            builder.setQuadOrientation(EnumFacing.getFacingFromVector(f.getNormal().x, f.getNormal().y, f.getNormal().z));
            builder.setTexture(sprite);
            Normal faceNormal = f.getNormal();
            putVertexData(model, format, false, builder, f.getVertices()[0], faceNormal, TextureCoordinate.getDefaultUVs()[0], sprite);
            putVertexData(model, format, false, builder, f.getVertices()[1], faceNormal, TextureCoordinate.getDefaultUVs()[1], sprite);
            putVertexData(model, format, false, builder, f.getVertices()[2], faceNormal, TextureCoordinate.getDefaultUVs()[2], sprite);
            putVertexData(model, format, false, builder, f.getVertices()[3], faceNormal, TextureCoordinate.getDefaultUVs()[3], sprite);
            quads.add(builder.build());
        }
        return ImmutableList.copyOf(quads);
    }

    public static void putVertexData(OBJModel model, VertexFormat format, boolean flip, UnpackedBakedQuad.Builder builder, Vertex v, Normal faceNormal, TextureCoordinate defUV, TextureAtlasSprite sprite)
    {
        for (int e = 0; e < format.getElementCount(); e++)
        {
            switch (format.getElement(e).getUsage())
            {
                case POSITION:
                    builder.put(e, v.getPos().x, v.getPos().y, v.getPos().z, v.getPos().w);
                    break;
                case COLOR:
                    if (v.getMaterial() != null)
                        builder.put(e,
                                v.getMaterial().getColor().x,
                                v.getMaterial().getColor().y,
                                v.getMaterial().getColor().z,
                                v.getMaterial().getColor().w);
                    else
                        builder.put(e, 1, 1, 1, 1);
                    break;
                case UV:
                    if (!v.hasTextureCoordinate())
                        builder.put(e,
                                sprite.getInterpolatedU(defUV.u * 16),
                                sprite.getInterpolatedV((flip ? 1 - defUV.v: defUV.v) * 16),
                                0, 1);
                    else
                        builder.put(e,
                                sprite.getInterpolatedU(v.getTextureCoordinate().u * 16),
                                sprite.getInterpolatedV((flip ? 1 - v.getTextureCoordinate().v : v.getTextureCoordinate().v) * 16),
                                0, 1);
                    break;
                case NORMAL:
                    if (!v.hasNormal())
                        builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                    else
                        builder.put(e, v.getNormal().x, v.getNormal().y, v.getNormal().z, 0);
                    break;
                default:
                    builder.put(e);
            }
        }
    }
    
    public static final TRSRTransformation THIRD_PERSON_RIGHT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 0, 0)),
            new Vector3f(0.375f, 0.375f, 0.375f),
            null));
    public static final TRSRTransformation THIRD_PERSON_LEFT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 0, 0)),
            new Vector3f(0.375f, 0.375f, 0.375f),
            null));
    public static final TRSRTransformation FIRST_PERSON_RIGHT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 0, 0)),
            new Vector3f(0.4F, 0.4F, 0.4F),
            null));
    public static final TRSRTransformation FIRST_PERSON_LEFT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 0, 0)),
            new Vector3f(0.4F, 0.4F, 0.4F),
            null));
    public static final TRSRTransformation GROUND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 0, 0)),
            new Vector3f(0.25f, 0.25f, 0.25f),
            null));
    public static final TRSRTransformation FIXED = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 0, 0)),
            new Vector3f(0.5f, 0.5f, 0.5f),
            null));
    public static final TRSRTransformation GUI = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(30, 225, 0)),
            new Vector3f(0.625f, 0.625f, 0.625f),
            null));
    
    public static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation>
    DEFAULT_PERSPECTIVE_TRANSFORMS = new ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation>()
    .put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND)
    .put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND)
    .put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_RIGHT_HAND)
    .put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, FIRST_PERSON_LEFT_HAND)
    .put(ItemCameraTransforms.TransformType.GROUND, GROUND)
    .put(ItemCameraTransforms.TransformType.FIXED, FIXED)
    .put(ItemCameraTransforms.TransformType.GUI, GUI)
    .build();
    
    public static final ItemCameraTransforms DEFAULT_BLOCK_TRANSFORM = new ItemCameraTransforms(THIRD_PERSON_LEFT_HAND.toItemTransform(), THIRD_PERSON_RIGHT_HAND.toItemTransform(), FIRST_PERSON_LEFT_HAND.toItemTransform(), FIRST_PERSON_RIGHT_HAND.toItemTransform(), ItemCameraTransforms.DEFAULT.head, GUI.toItemTransform(), GROUND.toItemTransform(), FIXED.toItemTransform());
	
}
