package alec_wam.CrystalMod.tiles.pipes.covers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alec_wam.CrystalMod.client.util.CustomBakedQuad;
import alec_wam.CrystalMod.client.util.VertexUV;
import alec_wam.CrystalMod.util.Vector3d;
import net.minecraft.util.math.MathHelper;

//Major Credit to TeamCOFH for a majority of this code. https://github.com/CoFH/ThermalDynamics
public class CoverCutter
{
    public static final ITransformer[] hollowPipe;
    public static final ITransformer[] hollowPipeLarge;
    public static final ITransformer[] hollowPipeTile;
    
    public static List<CustomBakedQuad> cut(final List<CustomBakedQuad> quads, final int side, final ITransformer[] transformers) {
        final List<Quad> tessQuads = loadFromQuads(quads);
        final List<CustomBakedQuad> transformedQuads = new ArrayList<CustomBakedQuad>();
        for (final Quad tessQuad : tessQuads) {
            transformedQuads.addAll(tessQuad.sliceStretchDraw(side, transformers));
        }
        return transformedQuads;
    }
    
    public static List<Quad> loadFromQuads(final List<CustomBakedQuad> CustomBakedQuads) {
        final List<Quad> quads = new ArrayList<Quad>();
        for (final CustomBakedQuad quad : CustomBakedQuads) {
            final Vertex8[] verts = new Vertex8[4];
            for (int v = 0; v < 4; ++v) {
                final VertexUV vert = quad.vertices[v];
                verts[v] = new Vertex8((float)vert.vec.x, (float)vert.vec.y, (float)vert.vec.z, (float)vert.uv.u, (float)vert.uv.v, quad.colours[v].rgba(), quad.normals[v].copy(), quad.lightMaps[v]);
            }
            quads.add(new Quad(verts, quad.copy()));
        }
        return quads;
    }
    
    public static ITransformer[] hollowCover(final float w) {
        return new ITransformer[] { new TransformSquare(0.0f, w, 0.0f, 1.0f), new TransformSquare(1.0f - w, 1.0f, 0.0f, 1.0f), new TransformSquare(w, 1.0f - w, 0.0f, w), new TransformSquare(w, 1.0f - w, 1.0f - w, 1.0f) };
    }
    
    static {
        hollowPipe = hollowCover(0.3125f);
        hollowPipeLarge = hollowCover(0.28125f);
        hollowPipeTile = hollowCover(0.115f);
    }
    
    public static class Quad
    {
        Vertex8[] verts;
        CustomBakedQuad originalQuad;
        
        public Quad(final Vertex8[] verts, final CustomBakedQuad quad) {
            this.originalQuad = quad;
            this.verts = verts;
        }
        
        public List<CustomBakedQuad> sliceStretchDraw(final int side, final ITransformer[] transformers) {
            final float[][] uvTransform = this.getUVTransform(this.verts, side);
            final List<CustomBakedQuad> quads = new ArrayList<CustomBakedQuad>();
            if (uvTransform == null) {
                quads.add(this.draw());
                return quads;
            }
            for (final ITransformer transformer : transformers) {
                final Quad slice = this.slice(side, transformer, uvTransform);
                if (slice.notEmpty()) {
                    quads.add(slice.draw());
                }
            }
            return quads;
        }
        
        private boolean notEmpty() {
            final Vertex8 a = this.verts[0];
            byte f = 0;
            boolean flagX = true;
            boolean flagY = true;
            boolean flagZ = true;
            for (int i = 1; i < 4; ++i) {
                final Vertex8 b = this.verts[i];
                if (flagX && Math.abs(a.x - b.x) > 1.0E-4f) {
                    flagX = false;
                    ++f;
                }
                if (flagY && Math.abs(a.y - b.y) > 1.0E-4f) {
                    flagY = false;
                    ++f;
                }
                if (flagZ && Math.abs(a.z - b.z) > 1.0E-4f) {
                    flagZ = false;
                    ++f;
                }
                if (f > 1) {
                    return true;
                }
            }
            return false;
        }
        
        public Quad slice(final int side, final ITransformer transformer, final float[][] uvTransform) {
            final Vertex8[] v = new Vertex8[this.verts.length];
            final int s = side >> 1;
            for (int i = 0; i < this.verts.length; ++i) {
                final Vertex8 copy = this.verts[i].copy();
                float dx;
                float dy;
                if (s == 0) {
                    dx = copy.x;
                    dy = copy.z;
                }
                else if (s == 1) {
                    dx = copy.x;
                    dy = copy.y;
                }
                else {
                    dx = copy.z;
                    dy = copy.y;
                }
                if (transformer.shouldTransform(dx, dy)) {
                    final float dx2 = transformer.transformX(dx, dy);
                    final float dy2 = transformer.transformY(dx, dy);
                    if (s == 0) {
                        copy.x = dx2;
                        copy.z = dy2;
                    }
                    else if (s == 1) {
                        copy.x = dx2;
                        copy.y = dy2;
                    }
                    else {
                        copy.z = dx2;
                        copy.y = dy2;
                    }
                    if (uvTransform != null) {
                        final float[] newTex = new float[8];
                        for (int j = 0; j < 8; ++j) {
                            newTex[j] = uvTransform[0][j] + uvTransform[1][j] * dx2 + uvTransform[2][j] * dy2;
                        }
                        copy.reloadTex(newTex);
                    }
                }
                v[i] = copy;
            }
            return new Quad(v, this.originalQuad.copy());
        }
        
        public CustomBakedQuad draw() {
            final CustomBakedQuad quad = this.originalQuad.copy();
            for (int i = 0; i < this.verts.length; ++i) {
                final Vertex8 vertex = this.verts[i];
                vertex.draw(quad, i);
            }
            return quad;
        }
        
        public float[][] getUVTransform(final Vertex8[] quads, final int side) {
            final int s = side >> 1;
            float n = 0.0f;
            float sx = 0.0f;
            float sy = 0.0f;
            float sxy = 0.0f;
            float sxx = 0.0f;
            float syy = 0.0f;
            final float[][] XY = new float[3][8];
            for (final Vertex8 vertex : quads) {
                ++n;
                float dx;
                float dy;
                if (s == 0) {
                    dx = vertex.x;
                    dy = vertex.z;
                }
                else if (s == 1) {
                    dx = vertex.x;
                    dy = vertex.y;
                }
                else {
                    dx = vertex.z;
                    dy = vertex.y;
                }
                sx += dx;
                sy += dy;
                sxy += dx * dy;
                syy += dy * dy;
                sxx += dx * dx;
                final float[] tex = vertex.buildTex();
                for (int j = 0; j < tex.length; ++j) {
                    final float[] array = XY[0];
                    final int n3 = j;
                    array[n3] += tex[j];
                    final float[] array2 = XY[1];
                    final int n4 = j;
                    array2[n4] += tex[j] * dx;
                    final float[] array3 = XY[2];
                    final int n5 = j;
                    array3[n5] += tex[j] * dy;
                }
            }
            final float v = sxx * syy - sxy * sxy;
            float determinant = n * v - (sxx * sy * sy + syy * sx * sx) + 2.0f * (sxy * sx * sy);
            if (Math.abs(determinant) <= 1.0E-4f) {
                return null;
            }
            determinant = 1.0f / determinant;
            final float cy_xy = (sxy * sy - syy * sx) * determinant;
            final float cx_xy = (sxy * sx - sxx * sy) * determinant;
            final float cx_y = (sx * sy - sxy * n) * determinant;
            final float[][] XXI = { { v * determinant, cy_xy, cx_xy }, { cy_xy, (syy * n - sy * sy) * determinant, cx_y }, { cx_xy, cx_y, (sxx * n - sx * sx) * determinant } };
            final float[][] beta = new float[3][8];
            for (int i = 0; i < 3; ++i) {
                for (int k = 0; k < 8; ++k) {
                    for (int l = 0; l < 3; ++l) {
                        final float[] array4 = beta[i];
                        final int n6 = k;
                        array4[n6] += XXI[i][l] * XY[l][k];
                    }
                }
            }
            return beta;
        }
        
        @Override
        public String toString() {
            return String.format("Quad{%s}", Arrays.toString(this.verts));
        }
    }
    
    public static class TransformSquare implements ITransformer
    {
        float x0;
        float x1;
        float y0;
        float y1;
        
        public TransformSquare(final float x0, final float x1, final float y0, final float y1) {
            this.x0 = x0;
            this.x1 = x1;
            this.y0 = y0;
            this.y1 = y1;
        }
        
        @Override
        public boolean shouldTransform(final float dx, final float dy) {
            return dx < this.x0 || dx > this.x1 || dy < this.y0 || dy > this.y1;
        }
        
        @Override
        public float transformX(final float dx, final float dy) {
            return MathHelper.clamp(dx, this.x0, this.x1);
        }
        
        @Override
        public float transformY(final float dx, final float dy) {
            return MathHelper.clamp(dy, this.y0, this.y1);
        }
        
        @Override
        public String toString() {
            return "TransformSquare{x0=" + this.x0 + ", x1=" + this.x1 + ", y0=" + this.y0 + ", y1=" + this.y1 + '}';
        }
    }
    
    public static class TriTransformer implements ITransformer
    {
        float m;
        float k;
        boolean flipX;
        boolean flipY;
        
        @Override
        public String toString() {
            return "TriTransformer{m=" + this.m + ", k=" + this.k + ", flipX=" + this.flipX + ", flipY=" + this.flipY + '}';
        }
        
        public TriTransformer(final float m, final float k, final boolean flipX, final boolean flipY) {
            this.m = m;
            this.k = k;
            this.flipX = flipX;
            this.flipY = flipY;
        }
        
        @Override
        public boolean shouldTransform(float dx, float dy) {
            if (this.flipX) {
                dx = 1.0f - dx;
            }
            if (this.flipY) {
                dy = 1.0f - dy;
            }
            return dx < this.m || dy < this.m || dx + dy > this.k;
        }
        
        @Override
        public float transformX(float dx, float dy) {
            if (this.flipX) {
                dx = 1.0f - dx;
            }
            if (dx < this.m) {
                return this.flipX ? (1.0f - this.m) : this.m;
            }
            if (this.flipY) {
                dy = 1.0f - dy;
            }
            if (dy < this.m) {
                final float d = MathHelper.clamp(dx, this.m, this.k - this.m);
                return this.flipX ? (1.0f - d) : d;
            }
            final float d = this.k * dx / (dx + dy);
            return this.flipX ? (1.0f - d) : d;
        }
        
        @Override
        public float transformY(float dx, float dy) {
            if (this.flipY) {
                dy = 1.0f - dy;
            }
            if (dy < this.m) {
                return this.flipY ? (1.0f - this.m) : this.m;
            }
            if (this.flipX) {
                dx = 1.0f - dx;
            }
            if (dx < this.m) {
                final float d = MathHelper.clamp(dy, this.m, this.k - this.m);
                return this.flipY ? (1.0f - d) : d;
            }
            final float d = this.k * dy / (dy + dx);
            return this.flipY ? (1.0f - d) : d;
        }
    }
    
    @Deprecated
    public static class Vertex8
    {
        public static final int TEX_NUM = 8;
        float x;
        float y;
        float z;
        float u;
        float v;
        int color;
        Vector3d normal;
        int brightness;
        
        @Override
        public String toString() {
            return String.format("V8{{%s,%s,%s},{%s,%s},c=%d,n=%s,b=%d}", this.x, this.y, this.z, this.u, this.v, this.color, this.normal.toString(), this.brightness);
        }
        
        public Vertex8(final float x, final float y, final float z, final float u, final float v, final int color, final Vector3d normal, final int brightness) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
            this.color = color;
            this.normal = normal;
            this.brightness = brightness;
        }
        
        public Vertex8 copy() {
            return new Vertex8(this.x, this.y, this.z, this.u, this.v, this.color, this.normal, this.brightness);
        }
        
        public void draw(final CustomBakedQuad quad, final int i) {
            quad.vertices[i].vec.set(this.x, this.y, this.z);
            quad.vertices[i].uv.set(this.u, this.v);
            quad.colours[i].set(this.color);
            quad.normals[i].set(this.normal);
            quad.lightMaps[i] = this.brightness;
        }
        
        public float[] buildTex() {
            return new float[] { this.u, this.v, this.color >> 24 & 0xFF, this.color >> 16 & 0xFF, this.color >> 8 & 0xFF, this.color & 0xFF, this.brightness & 0xFFFF, this.brightness >>> 16 & 0xFFFF };
        }
        
        public void reloadTex(final float[] tex) {
            this.u = tex[0];
            this.v = tex[1];
            this.color = ((int)MathHelper.clamp(tex[2], 0.0f, 255.0f) << 24 | (int)MathHelper.clamp(tex[3], 0.0f, 255.0f) << 16 | (int)MathHelper.clamp(tex[4], 0.0f, 255.0f) << 8 | (int)MathHelper.clamp(tex[5], 0.0f, 255.0f));
            this.brightness = ((int)MathHelper.clamp(tex[6], 0.0f, 65535.0f) | (int)MathHelper.clamp(tex[7], 0.0f, 65535.0f) << 16);
        }
    }
    
    public interface ITransformer
    {
        boolean shouldTransform(final float p0, final float p1);
        
        float transformX(final float p0, final float p1);
        
        float transformY(final float p0, final float p1);
    }
}