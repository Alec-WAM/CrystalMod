package alec_wam.CrystalMod.tiles.darkinfection;

import alec_wam.CrystalMod.blocks.connected.ConnectedBlockState;
import alec_wam.CrystalMod.client.model.dynamic.ModelConnectedTexture;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

public class ModelDenseDarkness extends ModelConnectedTexture {

	public ModelDenseDarkness(){
		super();
	}
	public ModelDenseDarkness(ItemStack stack){
		super(stack);
	}
	
	public ModelDenseDarkness(ConnectedBlockState state){
		super(state);
	}
	
	@Override
	public TextureAtlasSprite getTexture(IBlockState state) {
		//return RenderUtil.getSprite("crystalmod:blocks/crystal_blue_glass_painted");
		return RenderUtil.getSprite("crystalmod:blocks/decorative/dense_darkness");
	}

	@Override
	public TextureAtlasSprite getCenterTexture(IBlockState state) {
		//return RenderUtil.getSprite("crystalmod:blocks/crystal_blue_glass_tinted");
		return RenderUtil.getSprite("crystalmod:blocks/decorative/dense_darkness");
	}

	@Override
	public TextureAtlasSprite getTexture(ItemStack stack) {
		return RenderUtil.getSprite("crystalmod:blocks/decorative/dense_darkness");
	}

	@Override
	public TextureAtlasSprite getCenterTexture(ItemStack stack) {
		return RenderUtil.getSprite("crystalmod:blocks/decorative/dense_darkness");
	}

	@Override
	public ModelConnectedTexture createNewModel(ItemStack stack) {
		return new ModelDenseDarkness(stack);
	}

	@Override
	public ModelConnectedTexture createNewModel(ConnectedBlockState state) {
		return new ModelDenseDarkness(state);
	}

	@Override
	public ModelConnectedTexture createNewModel() {
		return new ModelDenseDarkness();
	}
	
	@Override
	public boolean renderCenter(){
		return true;
	}

}
