package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockCrystalPlanks extends EnumBlock<CrystalColors.Basic>
{

	public BlockCrystalPlanks() {
		super(Material.WOOD, /*BlockCrystalLog.VARIANT*/CrystalColors.COLOR_BASIC, CrystalColors.Basic.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		setHardness(2.0F).setResistance(5.0F);
		setSoundType(SoundType.WOOD);
	}

}
