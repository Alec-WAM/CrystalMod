package alec_wam.CrystalMod.tiles.pipes.liquid.basic;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.client.model.dynamic.DelegatingDynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.machine.FakeTileState;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ModelBasicFluidPipe extends DelegatingDynamicItemAndBlockModel 
{
	static FaceBakery faceBakery;
    
    public ModelBasicFluidPipe() {
    	super();
    	state = null;
    }
    
    public FakeTileState<?> state;
    
    public ModelBasicFluidPipe(FakeTileState<?> state, EnumFacing facing, long rand) {
        super(state, facing, rand);
        this.state = state;
    }

    public ModelBasicFluidPipe(ItemStack itemStack, World world, EntityLivingBase entity) {
        super(itemStack, world, entity);
        state = null;
    }
    
    @Override
	public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        TextureAtlasSprite glass = getGlassTexture();
        BlockFaceUV uvFull = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        BlockPartFace faceFull = new BlockPartFace((EnumFacing)null, 0, "", uvFull);
        ModelRotation modelRot = ModelRotation.X0_Y0;
        
        
        
        return list;
    }
    
    public TextureAtlasSprite getGlassTexture(){
    	return RenderUtil.getSprite("crystalmod:blocks/pipe/fluid_basic");
    }
    
    @Override
	public boolean isAmbientOcclusion() {
        return false;
    }
    
    @Override
	public boolean isGui3d() {
        return true;
    }
    
    @Override
	public boolean isBuiltInRenderer() {
        return false;
    }
    
    @Override
	public TextureAtlasSprite getParticleTexture() {
    	return getGlassTexture();
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    public static final ModelBasicFluidPipe ITEMMODEL = new ModelBasicFluidPipe();
    
    static {
        ModelBasicFluidPipe.faceBakery = new FaceBakery();
    }

	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return (state instanceof FakeTileState) ? new ModelBasicFluidPipe((FakeTileState<?>)state, side, rand) : null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		return ITEMMODEL;
	}

	
}

