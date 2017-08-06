package alec_wam.CrystalMod.client.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import scala.actors.threadpool.Arrays;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.util.Vector3d;

/**
 * Created by covers1624 on 8/20/2016.
 * Basically just a holder for quads before baking.
 */
public class CustomBakedQuad implements IVertexProducer {

    public VertexUV[] vertices = new VertexUV[4];
    public Vector3d[] normals = new Vector3d[4];
    public ColorData[] colours = new ColorData[4];
    public Integer[] lightMaps = new Integer[4];

    public EnumFacing face = null;
    public EnumFacing ogFace = null;
    public int tintIndex = -1;
    public boolean applyDifuseLighting = true;
    public TextureAtlasSprite sprite;
    public float[][][] rawData = null;
    public CustomBakedQuad() {
    }

    public CustomBakedQuad(VertexUV... vertices) {
        if (vertices.length > 4) {
            throw new IllegalArgumentException("CCQuad is a... Quad.. only 3 or 4 vertices allowed!");
        }
        for (int i = 0; i < 4; i++) {
            this.vertices[i] = vertices[i].copy();
        }
    }

    public CustomBakedQuad(BakedQuad quad) {
        this();

        VertexFormat format = quad.getFormat();
        ogFace = face = quad.getFace();        
        tintIndex = quad.getTintIndex();
        sprite = quad.getSprite();
        for(int i = 0; i < 4; i++)vertices[i] = new VertexUV();
        VertexUnpacker consumer = new VertexUnpacker(quad.getFormat());
        quad.pipe(consumer);
        float[][][] unpackedData = consumer.getUnpackedData();
        rawData = unpackedData;
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < format.getElementCount(); e++) {
                float[] data = unpackedData[v][e];
                switch (format.getElement(e).getUsage()) {
                    case POSITION:
                    	vertices[v].vec.set(data);
                        break;
                    case NORMAL:
                        normals[v] = new Vector3d(data);
                        break;
                    case COLOR:
                        colours[v] = new ColorDataRGBA(data[0], data[1], data[2], data[3]);
                        break;
                    case UV:
                        if (format.getElement(e).getIndex() == 0) {
                            vertices[v].uv.set(data[0], data[1]);
                        } else {
                            lightMaps[v] = (int) (data[1] * 65535 / 32) << 20 | (int) (data[0] * 65535 / 32) << 4;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (!format.hasColor()) {
        	for(int i = 0; i < 4; i++)colours[i] = new ColorDataRGBA(0xFFFFFFFF);
        }
        if (!format.hasUvOffset(1)) {
            Arrays.fill(lightMaps, 0);
        }
        if (!format.hasNormal()) {
            computeNormals();
        }
    }

    public CustomBakedQuad(CustomBakedQuad quad) {
        this();
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = quad.vertices[i].copy();
        }
        for (int i = 0; i < vertices.length; i++) {
            normals[i] = quad.normals[i].copy();
        }
        for (int i = 0; i < vertices.length; i++) {
            colours[i] = quad.colours[i].copy();
        }
        System.arraycopy(quad.lightMaps, 0, lightMaps, 0, vertices.length);
        ogFace = face = quad.face;
        tintIndex = quad.tintIndex;
        applyDifuseLighting = quad.applyDifuseLighting;
        sprite = quad.sprite;
    }

    public boolean isQuads() {
        int counter = Util.countNoNull(vertices);
        return counter == 4;
    }

    public boolean hasTint() {
        return tintIndex != -1;
    }

    /**
     * Quadulates the quad by copying any element at index 2 to index 3 only if there are 3 of any given element.
     */
    public void quadulate() {
        int verticesCount = Util.countNoNull(vertices);
        int normalCount = Util.countNoNull(normals);
        int colourCount = Util.countNoNull(colours);
        int lightMapCount = Util.countNoNull(lightMaps);
        if (verticesCount == 3) {
            vertices[3] = vertices[2].copy();
        }
        if (normalCount == 3) {
            normals[3] = normals[2].copy();
        }
        if (colourCount == 3) {
            colours[3] = colours[2].copy();
        }
        if (lightMapCount == 3) {
            lightMaps[3] = lightMaps[2];
        }
    }

    /**
     * Creates a set of normals for the quad.
     * Will attempt to Quadulate the model first.
     */
    public void computeNormals() {
        if (Util.countNoNull(normals) != 4) {
            quadulate();
            Vector3d normal = calculateNormal(vertices[0].vec, vertices[1].vec, vertices[3].vec);

            for (int i = 0; i < 4; i++) {
                normals[i] = normal.copy();
            }
        }
    }

    public static Vector3d calculateNormal(Vector3d... vertices) {
        Vector3d diff1 = vertices[1].copy().subtract(vertices[0]);
        Vector3d diff2 = vertices[2].copy().subtract(vertices[0]);
        return diff1.crossProduct(diff2).normal().copy();
    }
    
    public EnumFacing getQuadFace() {
        if (face == null) {
            if (Util.countNoNull(normals) != 4) {
                computeNormals();
            }
            face = calcNormalSide(normals[0]);
        }
        return face;
    }
    
    public static EnumFacing calcNormalSide(Vector3d normal) {
        if (normal.y <= -0.99) {
            return EnumFacing.DOWN;
        }
        if (normal.y >= 0.99) {
            return EnumFacing.UP;
        }
        if (normal.z <= -0.99) {
            return EnumFacing.NORTH;
        }
        if (normal.z >= 0.99) {
            return EnumFacing.SOUTH;
        }
        if (normal.x <= -0.99) {
            return EnumFacing.WEST;
        }
        if (normal.x >= 0.99) {
            return EnumFacing.EAST;
        }
        return null;
    }

    public BakedQuad bake() {
        return bake(DefaultVertexFormats.BLOCK);
    }

    public BakedQuad bake(VertexFormat format) {
    	quadulate();
        computeNormals();
        UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(format);
        quadBuilder.setApplyDiffuseLighting(applyDifuseLighting);
        quadBuilder.setTexture(sprite);
        quadBuilder.setQuadOrientation(getQuadFace());
        quadBuilder.setQuadTint(tintIndex);
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < format.getElementCount(); e++) {
                VertexFormatElement element = format.getElement(e);
                switch (element.getUsage()) {
                    case POSITION:
                        Vector3d pos = vertices[v].vec;
                        quadBuilder.put(e, (float) pos.x, (float) pos.y, (float) pos.z, 1);
                        break;
                    case NORMAL:
                        Vector3d normal = normals[v];
                        quadBuilder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0);
                        break;
                    case COLOR:
                        ColorData colour = colours[v];
                        quadBuilder.put(e, (colour.r & 0xFF) / 255, (colour.g & 0xFF) / 255, (colour.b & 0xFF) / 255, (colour.a & 0xFF) / 255);
                        break;
                    case UV:
                        if (element.getIndex() == 0) {
                            UVData uv = vertices[v].uv;
                            quadBuilder.put(e, (float) uv.u, (float) uv.v, 0, 1);
                        } else {
                            int brightness = lightMaps[v];
                            quadBuilder.put(e, (float) ((brightness >> 4) & 15 * 32) / 65535, (float) ((brightness >> 20) & 15 * 32) / 65535, 0, 1);
                        }
                        break;
                    case PADDING:
                    case GENERIC:
                    default:
                        quadBuilder.put(e);
                }
            }
        }
        return quadBuilder.build();
    }

    @Override
    public void pipe(IVertexConsumer consumer) {
    	quadulate();
        computeNormals();
        consumer.setApplyDiffuseLighting(applyDifuseLighting);
        consumer.setTexture(sprite);
        consumer.setQuadOrientation(getQuadFace());
        consumer.setQuadTint(tintIndex);
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < consumer.getVertexFormat().getElementCount(); e++) {
                VertexFormatElement element = consumer.getVertexFormat().getElement(e);
                switch (element.getUsage()) {
                    case POSITION:
                        Vector3d pos = vertices[v].vec;
                        consumer.put(e, (float) pos.x, (float) pos.y, (float) pos.z, 1);
                        break;
                    case NORMAL:
                        Vector3d normal = normals[v];
                        consumer.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0);
                        break;
                    case COLOR:
                        ColorData colour = colours[v];
                        consumer.put(e, (colour.r & 0xFF) / 255, (colour.g & 0xFF) / 255, (colour.b & 0xFF) / 255, (colour.a & 0xFF) / 255);
                        break;
                    case UV:
                        if (element.getIndex() == 0) {
                            UVData uv = vertices[v].uv;
                            consumer.put(e, (float) uv.u, (float) uv.v, 0, 1);
                        } else {
                            int brightness = lightMaps[v];
                            consumer.put(e, (float) ((brightness >> 4) & 15 * 32) / 65535, (float) ((brightness >> 20) & 15 * 32) / 65535, 0, 1);
                        }
                        break;
                    case PADDING:
                    case GENERIC:
                    default:
                        consumer.put(e);
                }
            }
        }
    }

    public static List<CustomBakedQuad> fromArray(List<BakedQuad> bakedQuads) {
        List<CustomBakedQuad> quads = new LinkedList<CustomBakedQuad>();
        for (BakedQuad quad : bakedQuads) {
            quads.add(new CustomBakedQuad(quad));
        }
        return quads;
    }

    public CustomBakedQuad copy() {
        return new CustomBakedQuad(this);
    }
}
