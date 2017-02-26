package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.material.Material;

public class BlockCrystalPlanks extends EnumBlock<BlockCrystalLog.WoodType>
{

	public BlockCrystalPlanks() {
		super(Material.WOOD, BlockCrystalLog.VARIANT, BlockCrystalLog.WoodType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		setHardness(2.0F).setResistance(5.0F);
	}

}
