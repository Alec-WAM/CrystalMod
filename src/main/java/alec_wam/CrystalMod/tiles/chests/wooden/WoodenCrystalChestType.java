package alec_wam.CrystalMod.tiles.chests.wooden;

import net.minecraft.util.IStringSerializable;

public enum WoodenCrystalChestType implements IStringSerializable
{
    BLUE(54, 9, 0),
    RED(72, 9, 1),
    GREEN(81, 9, 2),
    DARK(108, 12, 3);
    public int size;
    private int rowLength;
    private int textureRow;

    WoodenCrystalChestType(int size, int rowLength, int textureRow)
    {
        this.size = size;
        this.rowLength = rowLength;
        this.textureRow = textureRow;
    }
    
    @Override
    public String getName()
    {
        return name().toLowerCase();
    }

    public int getTextureRow()
    {
        return textureRow;
    }

    public int getRowCount()
    {
        return size / rowLength;
    }

    public int getRowLength()
    {
        return rowLength;
    }
}