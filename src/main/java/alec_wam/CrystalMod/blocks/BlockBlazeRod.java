package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockBlazeRod extends BlockRotatedPillar
{
    public BlockBlazeRod()
    {
        super(Material.IRON, MapColor.SAND);
        this.setCreativeTab(CrystalMod.tabBlocks);
        this.setHardness(5.0F);
        this.setSoundType(SoundType.METAL);
    }
}