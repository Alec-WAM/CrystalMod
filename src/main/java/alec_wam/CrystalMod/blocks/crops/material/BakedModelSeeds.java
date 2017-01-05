package alec_wam.CrystalMod.blocks.crops.material;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BakedModelSeeds extends DynamicItemAndBlockModel {

	public static final BakedModelSeeds INSTANCE = new BakedModelSeeds();
	private final ItemStack stack;
	public BakedModelSeeds(){
		super(true, false);
		stack = null;
	}
	
	public BakedModelSeeds(ItemStack stack){
		super(false, true);
		this.stack = stack;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		if(side == null){
			List<BakedQuad> list = Lists.newArrayList();
			return list;
		}
        return ImmutableList.of();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return RenderUtil.getMissingSprite();
	}
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return null;
	}
	
	public Map<IMaterialCrop, BakedModelSeeds> itemModels = Maps.newHashMap();
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		if(!(stack.getItem() instanceof ItemMaterialSeed))return null;
		IMaterialCrop crop = ItemMaterialSeed.getCrop(stack);
		if(crop == null)return null;
		if(!itemModels.containsKey(crop)){
			itemModels.put(crop, new BakedModelSeeds(stack));
		}
		return itemModels.get(crop);
	}
}
