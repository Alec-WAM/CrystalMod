package alec_wam.CrystalMod.tiles.pipes.covers;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.util.CustomBakedQuad;
import alec_wam.CrystalMod.tiles.pipes.PipeBlockAccessWrapper;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;

//Major Credit to TeamCOFH for a majority of this code. https://github.com/CoFH/ThermalDynamics
public class CoverRender {

	public static final ThreadLocal<VertexLighterFlat> lighterFlat;
    public static final ThreadLocal<VertexLighterFlat> lighterSmooth;
    static final int[] sideOffsets;
    static final float[] sideSoftBounds;
    private static final int[][] sides;
    
    static {
        sideOffsets = new int[] { 1, 1, 2, 2, 0, 0 };
        sideSoftBounds = new float[] { 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f };
        lighterFlat = ThreadLocal.withInitial(() -> new VertexLighterFlat(Minecraft.getMinecraft().getBlockColors()));
        lighterSmooth = ThreadLocal.withInitial(() -> new VertexLighterSmoothAo(Minecraft.getMinecraft().getBlockColors()));
        sides = new int[][] { { 4, 5 }, { 0, 1 }, { 2, 3 } };
    }
	
    public static VertexLighterFlat setupLighter(VertexBuffer buffer, final IBlockState state, final IBlockAccess access, final BlockPos pos, final IBakedModel model) {
        final boolean renderAO = Minecraft.isAmbientOcclusionEnabled() && state.getLightValue(access, pos) == 0 && (model == null ? false : model.isAmbientOcclusion());
        final VertexLighterFlat lighter = renderAO ? lighterSmooth.get() : lighterFlat.get();
        VertexBufferConsumer consumer = new VertexBufferConsumer(buffer);
        lighter.setParent(consumer);
        consumer.setOffset(pos);
        return lighter;
    }
    
    public static boolean renderBlockQuads(final VertexLighterFlat lighter, final IBlockAccess access, final IBlockState state, final List<CustomBakedQuad> quads, final BlockPos pos) {
        if (!quads.isEmpty()) {
            lighter.setWorld(access);
            lighter.setState(state);
            lighter.setBlockPos(pos);
            for (final CustomBakedQuad quad : quads) {
                lighter.updateBlockInfo();
                quad.pipe(lighter);
            }
            return true;
        }
        return false;
    }
    
	public static List<CustomBakedQuad> buildCoverQuads(IBlockAccess world, BlockPos pos, IBlockState state, int side, AxisAlignedBB bounds, final CoverCutter.ITransformer[] cutType){
		final EnumFacing face = EnumFacing.getFront(side);
        final IBlockAccess coverAccess = new PipeBlockAccessWrapper(world, pos, face);
        final BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        try {
            state = state.getActualState(coverAccess, pos);
        }
        catch (Exception ex) {}
        final IBakedModel model = dispatcher.getModelForState(state);
        try {
            state = state.getBlock().getExtendedState(state, coverAccess, pos);
        }
        catch (Exception ex2) {}
        final List<BakedQuad> bakedQuads = new LinkedList<BakedQuad>();
        final long posRand = MathHelper.getPositionRandom(pos);
        bakedQuads.addAll(model.getQuads(state, (EnumFacing)null, posRand));
        for (final EnumFacing face2 : EnumFacing.VALUES) {
            bakedQuads.addAll(model.getQuads(state, face2, posRand));
        }
        List<CustomBakedQuad> quads = CustomBakedQuad.fromArray(bakedQuads);
        if (cutType != null) {
            quads = CoverCutter.cut(quads, side, cutType);
        }
        quads = sliceQuads(quads, side, bounds);
        return quads;
	}
	
	public static List<BakedQuad> getBakedCoverQuads(IBlockAccess world, BlockPos pos, IBlockState state, int side, AxisAlignedBB bounds, final CoverCutter.ITransformer[] cutType){
		List<CustomBakedQuad> quads = buildCoverQuads(world, pos, state, side, bounds, cutType);
		List<BakedQuad> list = Lists.newArrayList();
		for(CustomBakedQuad quad : quads)list.add(quad.bake(DefaultVertexFormats.ITEM));
		return list;
	}
	
	public static boolean renderBakedCoverQuads(VertexBuffer buffer, IBlockAccess world, BlockPos pos, IBlockState state, int side, AxisAlignedBB bounds, final CoverCutter.ITransformer[] cutType){
		final EnumFacing face = EnumFacing.getFront(side);
        final IBlockAccess coverAccess = new PipeBlockAccessWrapper(world, pos, face);
        final BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        try {
            state = state.getActualState(coverAccess, pos);
        }
        catch (Exception ex) {}
        final IBakedModel model = dispatcher.getModelForState(state);
        try {
            state = state.getBlock().getExtendedState(state, coverAccess, pos);
        }
        catch (Exception ex2) {}
        final List<BakedQuad> bakedQuads = new LinkedList<BakedQuad>();
        final long posRand = MathHelper.getPositionRandom(pos);
        bakedQuads.addAll(model.getQuads(state, (EnumFacing)null, posRand));
        for (final EnumFacing face2 : EnumFacing.VALUES) {
            bakedQuads.addAll(model.getQuads(state, face2, posRand));
        }
        List<CustomBakedQuad> quads = CustomBakedQuad.fromArray(bakedQuads);
        if (cutType != null) {
            quads = CoverCutter.cut(quads, side, cutType);
        }
        quads = sliceQuads(quads, side, bounds);
		if (!quads.isEmpty()) {
            final VertexLighterFlat lighter = setupLighter(buffer, state, coverAccess, pos, model);
            return renderBlockQuads(lighter, coverAccess, state, quads, pos);
        }
        return false;
	}
	
	public static final TextureAtlasSprite sideSprite = RenderUtil.getSprite(CrystalMod.resourceL("blocks/machine/machine_side"));
    
	public static List<CustomBakedQuad> sliceQuads(final List<CustomBakedQuad> quads, final int side, final AxisAlignedBB bounds) {
        final float[][] quadPos = new float[4][3];
        final float[] vecPos = new float[3];
        final boolean[] flat = new boolean[3];
        final List<CustomBakedQuad> finalQuads = new LinkedList<CustomBakedQuad>();
        for (final CustomBakedQuad quad : quads) {
            boolean flag3;
            boolean flag2 = flag3 = false;
            for (int i = 0; i < 3; ++i) {
                flat[i] = true;
            }
            for (int v = 0; v < 4; ++v) {
                quadPos[v][0] = (float)quad.vertices[v].vec.x;
                quadPos[v][1] = (float)quad.vertices[v].vec.y;
                quadPos[v][2] = (float)quad.vertices[v].vec.z;
                flag3 = (flag3 || quadPos[v][sideOffsets[side]] != sideSoftBounds[side]);
                flag2 = (flag2 || quadPos[v][sideOffsets[side]] != 1.0f - sideSoftBounds[side]);
                if (v == 0) {
                    System.arraycopy(quadPos[v], 0, vecPos, 0, 3);
                }
                else {
                    for (int vi = 0; vi < 3; ++vi) {
                        flat[vi] = (flat[vi] && quadPos[v][vi] == vecPos[vi]);
                    }
                }
            }
            int s = -1;
            if (flag3 && flag2) {
                for (int vi = 0; vi < 3; ++vi) {
                    if (flat[vi]) {
                        if (vi != sideOffsets[side]) {
                            s = vi;
                            break;
                        }
                        flag3 = false;
                    }
                }
            }
            for (int k2 = 0; k2 < 4; ++k2) {
                final boolean flag4 = quadPos[k2][sideOffsets[side]] != sideSoftBounds[side];
                for (int j = 0; j < 3; ++j) {
                    if (j == sideOffsets[side]) {
                        quadPos[k2][j] = clampF(quadPos[k2][j], bounds, j);
                    }
                    else if (flag3 && flag2 && flag4) {
                        quadPos[k2][j] = MathHelper.clamp(quadPos[k2][j], 0.001953125f, 0.9980469f);
                    }
                }
                if (s != -1) {
                    float u;
                    float v2;
                    if (s == 0) {
                        u = quadPos[k2][1];
                        v2 = quadPos[k2][2];
                    }
                    else if (s == 1) {
                        u = quadPos[k2][0];
                        v2 = quadPos[k2][2];
                    }
                    else {
                        u = quadPos[k2][0];
                        v2 = quadPos[k2][1];
                    }
                    u = MathHelper.clamp(u, 0.0f, 1.0f) * 16.0f;
                    v2 = MathHelper.clamp(v2, 0.0f, 1.0f) * 16.0f;
                    u = sideSprite.getInterpolatedU(u);
                    v2 = sideSprite.getInterpolatedV(v2);
                    quad.vertices[k2].uv.set(u, v2);
                    quad.tintIndex = -1;
                }
                quad.vertices[k2].vec.set(quadPos[k2]);
            }
            finalQuads.add(quad);
        }
        return finalQuads;
    }
	
    private static float clampF(final float x, final AxisAlignedBB b, final int j) {
        final float l = (float)getSide(b, sides[j][0]);
        final float u = (float)getSide(b, sides[j][1]);
        if (x < l) {
            return l - (l - x) * 0.001953125f;
        }
        if (x > u) {
            return u + (x - u) * 0.001953125f;
        }
        return x;
    }
    
    public static double getSide(AxisAlignedBB bb, int s) {
        switch (s) {
            case 0:
                return bb.minY;
            case 1:
                return bb.maxY;
            case 2:
                return bb.minZ;
            case 3:
                return bb.maxZ;
            case 4:
                return bb.minX;
            case 5:
                return bb.maxX;
        }
        throw new IndexOutOfBoundsException("Switch Falloff");
    }

}
