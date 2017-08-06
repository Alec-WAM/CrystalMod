package alec_wam.CrystalMod.client.util;

public class ColorDataRGBA extends ColorData {

    public ColorDataRGBA(int colour) {
        super((colour >> 24) & 0xFF, (colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF);
    }

    public ColorDataRGBA(double r, double g, double b, double a) {
        super((int) (255 * r), (int) (255 * g), (int) (255 * b), (int) (255 * a));
    }

    public ColorDataRGBA(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public ColorDataRGBA(float[] data) {
        this(data[0], data[1], data[2], data[3]);
    }

    public ColorDataRGBA(ColorDataRGBA colour) {
        super(colour);
    }

    public int pack() {
        return pack(this);
    }

    public float[] packArray() {
        return new float[] { (r & 0xFF) / 255, (g & 0xFF) / 255, (b & 0xFF) / 255, (a & 0xFF) / 255 };
    }

    @Override
    public ColorData copy() {
        return new ColorDataRGBA(this);
    }

    @Override
    public ColorData set(int colour) {
        return set(new ColorDataRGBA(colour));
    }

    public static int pack(ColorData colour) {
        return (colour.r & 0xFF) << 24 | (colour.g & 0xFF) << 16 | (colour.b & 0xFF) << 8 | (colour.a & 0xFF);
    }

    public static int multiply(int c1, int c2) {
        if (c1 == -1) {
            return c2;
        }
        if (c2 == -1) {
            return c1;
        }
        int r = (((c1 >>> 24) * (c2 >>> 24)) & 0xFF00) << 16;
        int g = (((c1 >> 16 & 0xFF) * (c2 >> 16 & 0xFF)) & 0xFF00) << 8;
        int b = ((c1 >> 8 & 0xFF) * (c2 >> 8 & 0xFF)) & 0xFF00;
        int a = ((c1 & 0xFF) * (c2 & 0xFF)) >> 8;
        return r | g | b | a;
    }

    public static int multiplyC(int c, float f) {
        int r = (int) ((c >>> 24) * f);
        int g = (int) ((c >> 16 & 0xFF) * f);
        int b = (int) ((c >> 8 & 0xFF) * f);
        return r << 24 | g << 16 | b << 8 | c & 0xFF;
    }
}
