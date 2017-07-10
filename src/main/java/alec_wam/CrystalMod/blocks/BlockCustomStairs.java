package alec_wam.CrystalMod.blocks;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomStairs extends BlockStairs implements ICustomModel {

	public BlockCustomStairs(IBlockState modelState) {
		super(modelState);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,	new ModelResourceLocation(getRegistryName(), "facing=east,half=bottom,shape=straight"));
	}

}
