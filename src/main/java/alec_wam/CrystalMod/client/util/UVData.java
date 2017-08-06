package alec_wam.CrystalMod.client.util;

public class UVData {

    public double u;
    public double v;
    public int tex;

    public UVData() {
    }

    public UVData(double u, double v) {
        this(u, v, 0);
    }

    public UVData(double u, double v, int tex) {
        this.u = u;
        this.v = v;
        this.tex = tex;
    }

    public UVData(UVData uv) {
        this(uv.u, uv.v, uv.tex);
    }

    public UVData set(double u, double v, int tex) {
        this.u = u;
        this.v = v;
        this.tex = tex;
        return this;
    }

    public UVData set(double u, double v) {
        return set(u, v, tex);
    }

    public UVData set(UVData uv) {
        return set(uv.u, uv.v, uv.tex);
    }

    public UVData copy() {
        return new UVData(this);
    }

    public UVData add(UVData uv) {
        u += uv.u;
        v += uv.v;
        return this;
    }

    public UVData multiply(double d) {
        u *= d;
        v *= d;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UVData)) {
            return false;
        }
        UVData uv = (UVData) o;
        return u == uv.u && v == uv.v;
    }
}
