package alec_wam.CrystalMod.blocks.underwater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.FakeBlockStateWithData;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

public class ModelCrossedWater implements IPerspectiveAwareModel 
{
	public static FaceBakery faceBakery;    
    static {
        faceBakery = new FaceBakery();
    }
    
    String texture;
    public ModelCrossedWater(String texture)
	{
    	this.texture = texture;
	}
    
    @Override
	public List<BakedQuad> getQuads( @Nullable IBlockState state, @Nullable EnumFacing side, long rand )
	{
    	if(side !=null)
		{
			return Collections.emptyList();
		}
    	boolean renderWater = false;
    	FakeBlockStateWithData cState = null;
    	if(state !=null){
			if(state instanceof FakeBlockStateWithData){
				cState = (FakeBlockStateWithData)state;
				renderWater = cState.blockAccess.isAirBlock(cState.pos.up());
			}
		}
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		TextureAtlasSprite sprite = getTexture();
		ModelRotation rot = ModelRotation.X0_Y0;
		boolean uvLocked = false;
		final BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
		final BlockPartFace faceN = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
		final BlockPartFace faceS = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
		final BlockPartFace faceE = new BlockPartFace(EnumFacing.EAST, 0, "", uv);
		final BlockPartFace faceW = new BlockPartFace(EnumFacing.WEST, 0, "", uv);
		BlockPartRotation rotationBF = new BlockPartRotation(new Vector3f(0.5F, 0.5F, 0.5F), EnumFacing.Axis.Y, 45.0F, true);
		
		if(state == null || MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT){
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0.8F, 0.0F, 8.0F), new Vector3f(15.2F, 16.0F, 8.0F), faceN, sprite, EnumFacing.NORTH, rot, rotationBF, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0.8F, 0.0F, 8.0F), new Vector3f(15.2F, 16.0F, 8.0F), faceS, sprite, EnumFacing.SOUTH, rot, rotationBF, uvLocked));
	
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(8.0F, 0.0F, 0.8F), new Vector3f(8.0F, 16.0F, 15.2F), faceW, sprite, EnumFacing.WEST, rot, rotationBF, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(8.0F, 0.0F, 0.8F), new Vector3f(8.0F, 16.0F, 15.2F), faceE, sprite, EnumFacing.EAST, rot, rotationBF, uvLocked));
		}
		
		if(cState !=null && renderWater && MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT){
			TextureAtlasSprite waterSprite = RenderUtil.getSprite("minecraft:blocks/water_still");
			float height = FluidUtil.getFluidHeight(cState.blockAccess, cState.pos, Material.WATER) * 16F;
			height -= 0.015F;
			
			final BlockFaceUV uvFull = new BlockFaceUV(new float[]{0, 0, 16, 16}, 0);
			final BlockPartFace faceFull = new BlockPartFace(EnumFacing.UP, -1, "", uvFull);
			final BlockPartFace faceFullDown = new BlockPartFace(EnumFacing.DOWN, -1, "", uvFull);
			//TODO Look into fluid height corners
			float f7 = FluidUtil.getFluidHeight(cState.blockAccess, cState.pos, Material.WATER) * 16F;
            float f8 = FluidUtil.getFluidHeight(cState.blockAccess, cState.pos.south(), Material.WATER) * 16F;
            float f9 = FluidUtil.getFluidHeight(cState.blockAccess, cState.pos.east().south(), Material.WATER) * 16F;
            float f10 = FluidUtil.getFluidHeight(cState.blockAccess, cState.pos.east(), Material.WATER) * 16F;
			
			
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, f7, 0), new Vector3f(16F, f9, 16F), faceFull, waterSprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, height-0.001f, 0), new Vector3f(16F, height-0.001f, 16F), faceFullDown, waterSprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
			
		}
		
		return quads;
	}

    public TextureAtlasSprite getTexture(){
    	return RenderUtil.getSprite(CrystalMod.resourceL("blocks/"+texture));
    }
    
	@Override
	public boolean isAmbientOcclusion() {
        return true;
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
        return getTexture();
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.NONE;
	}

	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS, cameraTransformType);
    }
	
}

