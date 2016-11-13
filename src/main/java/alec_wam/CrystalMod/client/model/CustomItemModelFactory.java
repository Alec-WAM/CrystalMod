package alec_wam.CrystalMod.client.model;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class CustomItemModelFactory implements IBakedModel {

	private IBakedModel model;
	private ICustomItemRenderer render;
	
	public CustomItemModelFactory(IBakedModel model, ICustomItemRenderer render){
		this.model = model;
		this.render = render;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side,	long rand) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAmbientOcclusion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isGui3d() {
		return model.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		throw new UnsupportedOperationException();
	}

	private ModelOverride override = new ModelOverride();
	
	@Override
	public ItemOverrideList getOverrides() {
		return override;
	}
	
	private class ModelOverride extends ItemOverrideList {

		public ModelOverride() {
			super(new ArrayList<ItemOverride>());
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
	    {
			return new BakedCustomItemModel(model, render, stack);
	    }
		
	}

}
