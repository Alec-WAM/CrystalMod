package alec_wam.CrystalMod.blocks;

import net.minecraft.block.material.Material;

public class BlockCrystalPlanks extends EnumBlock<BlockCrystalLog.WoodType>
{

	public BlockCrystalPlanks() {
		super(Material.WOOD, BlockCrystalLog.VARIANT, BlockCrystalLog.WoodType.class);
	}

}
