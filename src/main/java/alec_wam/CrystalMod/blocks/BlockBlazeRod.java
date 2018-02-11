package alec_wam.CrystalMod.blocks;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockBlazeRod extends BlockRotatedPillar
{
    public BlockBlazeRod()
    {
        super(Material.IRON, MapColor.SAND);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(5.0F);
        this.setSoundType(SoundType.METAL);
    }
}