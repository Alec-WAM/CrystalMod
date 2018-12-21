package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.blocks.ICustomModel;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLanternDyed extends BlockLantern implements ICustomModel {

	public BlockLanternDyed(){
		super();
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(EnumDyeColor type : EnumDyeColor.values())
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMetadata(), new ModelResourceLocation(this.getRegistryName(), "color="+type.getName()));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(EnumDyeColor type : EnumDyeColor.values()) {
			list.add(new ItemStack(this, 1, type.getMetadata()));
		}
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockStainedGlass.COLOR);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockStainedGlass.COLOR).getMetadata();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
}
