package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEtchedCrystal extends EnumBlock<CrystalColors.Special> {

	public BlockEtchedCrystal() {
		super(Material.ROCK, CrystalColors.COLOR_SPECIAL, CrystalColors.Special.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(CrystalColors.COLOR_SPECIAL, CrystalColors.Special.BLUE));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(CrystalColors.Special type : CrystalColors.Special.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), CrystalColors.COLOR_SPECIAL.getName()+"="+type.getName()));
	}

}
