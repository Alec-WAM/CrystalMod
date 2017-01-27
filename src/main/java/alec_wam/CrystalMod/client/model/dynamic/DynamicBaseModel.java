package alec_wam.CrystalMod.client.model.dynamic;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.Color;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import java.util.Collections;
import java.util.List;

/**
 * A model that can be used as a basis for flexible baked models.
 * @author rubensworks
 */
public abstract class DynamicBaseModel implements IPerspectiveAwareModel {

    // Rotation UV coordinates
    protected static final float[][] ROTATION_UV = {{1, 0}, {1, 1}, {0, 1}, {0, 0}};
    // A rotation offset fix for all sides
    protected static final int[] ROTATION_FIX = {2, 0, 2, 0, 1, 3};
    // u1, v1; u2, v2
    protected static final float[][] UVS = {{0, 0}, {1, 1}};

    /**
     * Rotate a given vector to the given side.
     * @param vec The vector to rotate.
     * @param side The side to rotate by.
     * @return The rotated vector.
     */
    protected static Vec3d rotate(Vec3d vec, EnumFacing side) {
        switch(side) {
            case DOWN:  return new Vec3d( vec.xCoord, -vec.yCoord, -vec.zCoord);
            case UP:    return new Vec3d( vec.xCoord,  vec.yCoord,  vec.zCoord);
            case NORTH: return new Vec3d( vec.xCoord,  vec.zCoord, -vec.yCoord);
            case SOUTH: return new Vec3d( vec.xCoord, -vec.zCoord,  vec.yCoord);
            case WEST:  return new Vec3d(-vec.yCoord,  vec.xCoord,  vec.zCoord);
            case EAST:  return new Vec3d( vec.yCoord, -vec.xCoord,  vec.zCoord);
        }
        return vec;
    }

    /**
     * Rotate a given vector inversely to the given side.
     * @param vec The vector to rotate.
     * @param side The side to rotate by.
     * @return The inversely rotated vector.
     */
    protected static Vec3d revRotate(Vec3d vec, EnumFacing side) {
        switch(side) {
            case DOWN:  return new Vec3d( vec.xCoord, -vec.yCoord, -vec.zCoord);
            case UP:    return new Vec3d( vec.xCoord,  vec.yCoord,  vec.zCoord);
            case NORTH: return new Vec3d( vec.xCoord, -vec.zCoord,  vec.yCoord);
            case SOUTH: return new Vec3d( vec.xCoord,  vec.zCoord, -vec.yCoord);
            case WEST:  return new Vec3d( vec.yCoord, -vec.xCoord,  vec.zCoord);
            case EAST:  return new Vec3d(-vec.yCoord,  vec.xCoord,  vec.zCoord);
        }
        return vec;
    }

    /**
     * Make an int array of the given information so that it can be fed into a
     * {@link BakedQuad}.
     * @param x X
     * @param y Y
     * @param z Z
     * @param color Color
     * @param texture Texture
     * @param u Icon U
     * @param v Icon V
     * @return The assembled int array.
     */
    protected static int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u,
                                        float v) {
        return new int[] {
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                0
        };
    }

    /**
     * Add a given quad to a list of quads.
     * @param quads The quads to append to.
     * @param x1 Start X
     * @param x2 End X
     * @param z1 Start Z
     * @param z2 End Z
     * @param y Y
     * @param texture The base texture
     * @param side The side to add render quad at.
     */
    protected static void addBakedQuad(List<BakedQuad> quads, float x1, float x2, float z1, float z2, float y,
                                     TextureAtlasSprite texture, EnumFacing side) {
        addBakedQuad(quads, x1, x2, z1, z2, y, texture, -1, side);
    }

    /**
     * Add a given quad to a list of quads.
     * @param quads The quads to append to.
     * @param x1 Start X
     * @param x2 End X
     * @param z1 Start Z
     * @param z2 End Z
     * @param y Y
     * @param texture The base texture
     * @param shadeColor shade color for the texture in BGR format
     * @param side The side to add render quad at.
     */
    protected static void addBakedQuad(List<BakedQuad> quads, float x1, float x2, float z1, float z2, float y,
                                       TextureAtlasSprite texture, int shadeColor, EnumFacing side) {
        addBakedQuad(quads, x1, x2, z1, z2, y, texture, shadeColor, side, false);
    }

    /**
     * Add a given colored quad to a list of quads.
     * @param quads The quads to append to.
     * @param x1 Start X
     * @param x2 End X
     * @param z1 Start Z
     * @param z2 End Z
     * @param y Y
     * @param texture The base texture
     * @param shadeColor shade color for the texture
     * @param side The side to add render quad at.
     */
    protected static void addColoredBakedQuad(List<BakedQuad> quads, float x1, float x2, float z1, float z2, float y,
                                              TextureAtlasSprite texture, Color shadeColor, EnumFacing side) {
        int color = RGBAToInt(shadeColor.getBlue(), shadeColor.getGreen(), shadeColor.getRed(), shadeColor.getAlpha());
        addColoredBakedQuad(quads, x1, x2, z1, z2, y, texture, color, side);
    }
    
    public static int RGBAToInt(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Add a given colored quad to a list of quads.
     * @param quads The quads to append to.
     * @param x1 Start X
     * @param x2 End X
     * @param z1 Start Z
     * @param z2 End Z
     * @param y Y
     * @param texture The base texture
     * @param shadeColor shade color for the texture in BGR format
     * @param side The side to add render quad at.
     */
    public static void addColoredBakedQuad(List<BakedQuad> quads, float x1, float x2, float z1, float z2, float y,
                                              TextureAtlasSprite texture, int shadeColor, EnumFacing side) {
        addBakedQuad(quads, x1, x2, z1, z2, y, texture, shadeColor, side, true);
    }

    /**
     * Add a given quad to a list of quads.
     * @param quads The quads to append to.
     * @param x1 Start X
     * @param x2 End X
     * @param z1 Start Z
     * @param z2 End Z
     * @param y Y
     * @param texture The base texture
     * @param shadeColor shade color for the texture in BGR format
     * @param side The side to add render quad at.
     * @param isColored When set to true a colored baked quad will be made, otherwise a regular baked quad is used.
     */
    private static void addBakedQuad(List<BakedQuad> quads, float x1, float x2, float z1, float z2, float y,
                                       TextureAtlasSprite texture, int shadeColor, EnumFacing side, boolean isColored) {
        addBakedQuadRotated(quads, x1, x2, z1, z2, y, texture, side, 0, isColored,
                shadeColor, new float[][]{{x1, z1}, {x1, z2}, {x2, z2}, {x2, z1}});
    }

    /**
     * Add a given rotated quad to a list of quads.
     * @param quads The quads to append to.
     * @param x1 Start X
     * @param x2 End X
     * @param z1 Start Z
     * @param z2 End Z
     * @param y Y
     * @param texture The base texture
     * @param side The side to add render quad at.
     * @param rotation The rotation index to rotate by.
     */
    protected static void addBakedQuadRotated(List<BakedQuad> quads, float x1, float x2, float z1, float z2, float y,
                                              TextureAtlasSprite texture, EnumFacing side, int rotation) {
        addBakedQuadRotated(quads, x1, x2, z1, z2, y, texture, side, rotation, false, -1, ROTATION_UV);
    }

    /**
     * Add a given rotated quad to a list of quads.
     * @param quads The quads to append to.
     * @param x1 Start X
     * @param x2 End X
     * @param z1 Start Z
     * @param z2 End Z
     * @param y Y
     * @param texture The base texture
     * @param side The side to add render quad at.
     * @param rotation The rotation index to rotate by.
     * @param isColored When set to true a colored baked quad will be made, otherwise a regular baked quad is used.
     * @param shadeColor The shade color
     * @param uvs A double array of 4 uv pairs
     */
    public static void addBakedQuadRotated(List<BakedQuad> quads, float x1, float x2, float z1, float z2, float y,
                                              TextureAtlasSprite texture, EnumFacing side, int rotation,
                                              boolean isColored, int shadeColor, float[][] uvs) {
        Vec3d v1 = rotate(new Vec3d(x1 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
        Vec3d v2 = rotate(new Vec3d(x1 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
        Vec3d v3 = rotate(new Vec3d(x2 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
        Vec3d v4 = rotate(new Vec3d(x2 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
        int[] data =  Ints.concat(
                vertexToInts((float) v1.xCoord, (float) v1.yCoord, (float) v1.zCoord, shadeColor, texture, uvs[(0 + rotation) % 4][0] * 16, uvs[(0 + rotation) % 4][1] * 16),
                vertexToInts((float) v2.xCoord, (float) v2.yCoord, (float) v2.zCoord, shadeColor, texture, uvs[(1 + rotation) % 4][0] * 16, uvs[(1 + rotation) % 4][1] * 16),
                vertexToInts((float) v3.xCoord, (float) v3.yCoord, (float) v3.zCoord, shadeColor, texture, uvs[(2 + rotation) % 4][0] * 16, uvs[(2 + rotation) % 4][1] * 16),
                vertexToInts((float) v4.xCoord, (float) v4.yCoord, (float) v4.zCoord, shadeColor, texture, uvs[(3 + rotation) % 4][0] * 16, uvs[(3 + rotation) % 4][1] * 16)
        );
        ForgeHooksClient.fillNormal(data, side); // This fixes lighting issues when item is rendered in hand/inventory
        quads.add(new BakedQuad(data, -1, side, texture, false, Attributes.DEFAULT_BAKED_FORMAT));
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return Collections.emptyList();
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
    	return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, DEFAULT_PERSPECTIVE_TRANSFORMS, cameraTransformType);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
    
    public static final TRSRTransformation THIRD_PERSON_RIGHT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(75, 45, 0)),
            new Vector3f(0.375f, 0.375f, 0.375f),
            null));
    public static final TRSRTransformation THIRD_PERSON_LEFT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(70, 45, 0)),
            new Vector3f(0.375f, 0.375f, 0.375f),
            null));
    public static final TRSRTransformation FIRST_PERSON_RIGHT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 45, 0)),
            new Vector3f(0.4F, 0.4F, 0.4F),
            null));
    public static final TRSRTransformation FIRST_PERSON_LEFT_HAND = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 0, 0),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 225, 0)),
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
    
}
