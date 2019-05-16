package alec_wam.CrystalMod.client;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.model.BakedQuad;
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
	
}
