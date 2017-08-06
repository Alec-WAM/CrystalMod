package alec_wam.CrystalMod.client.util;

public class ColorDataARGB extends ColorData {

    public ColorDataARGB(int colour) {
        super((colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF, (colour >> 24) & 0xFF);
    }

    public ColorDataARGB(int a, int r, int g, int b) {
        super(r, g, b, a);
    }

    public ColorDataARGB(ColorDataARGB colour) {
        super(colour);
    }

    public ColorDataARGB copy() {
        return new ColorDataARGB(this);
    }

    @Override
    public ColorData set(int colour) {
        return set(new ColorDataARGB(colour));
    }

    public int pack() {
        return pack(this);
    }

    public float[] packArray() {
        return new float[] { (a & 0xFF) / 255, (r & 0xFF) / 255, (g & 0xFF) / 255, (b & 0xFF) / 255 };
    }

    public static int pack(ColorData colour) {
        return (colour.a & 0xFF) << 24 | (colour.r & 0xFF) << 16 | (colour.g & 0xFF) << 8 | (colour.b & 0xFF);
    }
}
