package alec_wam.CrystalMod.blocks.glass;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ModelTintedGlass extends ModelGlass {
	
	public static final ModelTintedGlass INSTANCE = new ModelTintedGlass(GlassType.BLUE);
	
	public ModelTintedGlass(GlassType type){
		super(type);
	}
	
	public ModelTintedGlass(ItemStack stack){
		super(stack);
	}
	
	public ModelTintedGlass(GlassBlockState state){
		super(state);
	}
	
	@Override
	public boolean renderCenter(){
		return true;
	}
	
	@Override
	public TextureAtlasSprite getTexture(GlassType type){
		return RenderUtil.getSprite("crystalmod:blocks/crystal_"+type.getName()+"_glass_tinted");
	}
	
	@Override
	public TextureAtlasSprite getCenterTexture(GlassType type){
		return RenderUtil.getSprite("crystalmod:blocks/crystal_"+type.getName()+"_glass_tinted");
	}
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return (state !=null && state instanceof GlassBlockState) ? new ModelTintedGlass((GlassBlockState)state) : null;
	}
	
	public Map<Integer, ModelTintedGlass> itemModels = Maps.newHashMap();
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		if(!itemModels.containsKey(stack.getMetadata())){
			itemModels.put(stack.getMetadata(),	new ModelTintedGlass(stack));
		}
		return itemModels.get(stack.getMetadata());
	}
	
}
