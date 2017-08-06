package alec_wam.CrystalMod.client.util;

import alec_wam.CrystalMod.util.Vector3d;

public class VertexUV {

    public Vector3d vec;
    public UVData uv;

    public VertexUV() {
        this(new Vector3d(), new UVData());
    }

    public VertexUV(Vector3d vert, UVData uv) {
        this.vec = vert;
        this.uv = uv;
    }

    public VertexUV(Vector3d vert, double u, double v) {
        this(vert, new UVData(u, v));
    }

    public VertexUV(double x, double y, double z, double u, double v) {
        this(x, y, z, u, v, 0);
    }

    public VertexUV(double x, double y, double z, double u, double v, int tex) {
        this(new Vector3d(x, y, z), new UVData(u, v, tex));
    }

    public VertexUV set(double x, double y, double z, double u, double v) {
        vec.set(x, y, z);
        uv.set(u, v);
        return this;
    }

    public VertexUV set(double x, double y, double z, double u, double v, int tex) {
        vec.set(x, y, z);
        uv.set(u, v, tex);
        return this;
    }

    public VertexUV set(VertexUV vert) {
        vec.set(vert.vec);
        uv.set(vert.uv);
        return this;
    }

    public VertexUV(VertexUV vertex5) {
        this(vertex5.vec.copy(), vertex5.uv.copy());
    }

    public VertexUV copy() {
        return new VertexUV(this);
    }
}
