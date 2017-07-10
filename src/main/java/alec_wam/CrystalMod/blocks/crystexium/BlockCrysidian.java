package alec_wam.CrystalMod.blocks.crystexium;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCrysidian extends Block {

	public BlockCrysidian() {
		super(Material.ROCK);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(15f);
	}

}
