package alec_wam.CrystalMod.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class NormalBlockStateMapper extends StateMapperBase
{
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		return new ModelResourceLocation(state.getBlock().getRegistryName(), "normal");
	}
}
