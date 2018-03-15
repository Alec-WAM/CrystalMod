package alec_wam.CrystalMod.blocks.underwater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.connected.BlockConnectedTexture;
import alec_wam.CrystalMod.blocks.connected.ConnectedBlockState;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.BlockLiquid;
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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

public class ModelCoral implements IPerspectiveAwareModel 
{
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation("crystalmod:coral");
	public static FaceBakery faceBakery;    
    static {
        faceBakery = new FaceBakery();
    }
    private final EnumDyeColor color;
    
	public ModelCoral()
	{
		color = EnumDyeColor.WHITE;
	}
	
	public ModelCoral(EnumDyeColor color)
	{
		this.color = color;
	}
    
    @Override
	public List<BakedQuad> getQuads( @Nullable IBlockState state, @Nullable EnumFacing side, long rand )
	{
    	if(side !=null)
		{
			return Collections.emptyList();
		}
    	boolean renderU = false, renderD = false, renderN = false, renderS = false, renderW = false, renderE = false;
    	boolean renderWater = false;
    	ConnectedBlockState cState = null;
    	if(state !=null){
			renderU = state.getValue(BlockConnectedTexture.CONNECTED_UP);
			renderD = state.getValue(BlockConnectedTexture.CONNECTED_DOWN);
			renderN = state.getValue(BlockConnectedTexture.CONNECTED_NORTH);
			renderS = state.getValue(BlockConnectedTexture.CONNECTED_SOUTH);
			renderW = state.getValue(BlockConnectedTexture.CONNECTED_WEST);
			renderE = state.getValue(BlockConnectedTexture.CONNECTED_EAST);
			if(state instanceof ConnectedBlockState){
				cState = (ConnectedBlockState)state;
				renderWater = cState.blockAccess.isAirBlock(cState.pos.up());
			}
		}
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		TextureAtlasSprite sprite = getTexture();
		float min = 16.0F * 0.25f;
		float max = 16.0F * 0.75f;
		final BlockFaceUV uv = new BlockFaceUV(new float[]{min, min, max, max}, 0);
		final BlockPartFace faceU = new BlockPartFace(EnumFacing.UP, 0, "", uv);
		final BlockPartFace faceD = new BlockPartFace(EnumFacing.DOWN, 0, "", uv);
		final BlockPartFace faceN = new BlockPartFace(EnumFacing.NORTH, 0, "", uv);
		final BlockPartFace faceS = new BlockPartFace(EnumFacing.SOUTH, 0, "", uv);
		final BlockPartFace faceW = new BlockPartFace(EnumFacing.WEST, 0, "", uv);
		final BlockPartFace faceE = new BlockPartFace(EnumFacing.EAST, 0, "", uv);
		ModelRotation rot = ModelRotation.X0_Y0;
		boolean uvLocked = false;
		if(state == null || MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT){
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, uvLocked));
			
			float newMin = 16.0F * 0.36f;
			float newMax = 16.0F * 0.64f;
			
			if(renderU){
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, 16F, newMin), new Vector3f(newMax, 16F, newMax), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, max, newMin), new Vector3f(newMax, 16F, newMin), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, max, newMax), new Vector3f(newMax, 16F, newMax), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, max, newMin), new Vector3f(newMin, 16F, newMax), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMax, max, newMin), new Vector3f(newMax, 16F, newMax), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, uvLocked));
			}
			
			if(renderD){
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, 0F, newMin), new Vector3f(newMax, 0F, newMax), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, 0F, newMin), new Vector3f(newMax, min, newMin), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, 0F, newMax), new Vector3f(newMax, min, newMax), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, 0F, newMin), new Vector3f(newMin, min, newMax), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMax, 0F, newMin), new Vector3f(newMax, min, newMax), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, uvLocked));
			}
			
			if(renderN){
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMax, 0F), new Vector3f(newMax, newMax, min), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMin, 0F), new Vector3f(newMax, newMin, min), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMin, 0F), new Vector3f(newMax, newMax, 0F), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMin, 0F), new Vector3f(newMax, newMax, min), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMax, newMin, 0F), new Vector3f(newMax, newMax, min), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, uvLocked));
			}
			
			if(renderS){
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMax, max), new Vector3f(newMax, newMax, 16F), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMin, max), new Vector3f(newMax, newMin, 16F), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMin, 16F), new Vector3f(newMax, newMax, 16F), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMin, newMin, max), new Vector3f(newMax, newMax, 16F), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(newMax, newMin, max), new Vector3f(newMax, newMax, 16F), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, uvLocked));
			}
			
			if(renderW){
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0F, newMax, newMin), new Vector3f(min, newMax, newMax), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0F, newMin, newMin), new Vector3f(min, newMin, newMax), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0F, newMin, newMin), new Vector3f(min, newMax, newMin), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0F, newMin, newMax), new Vector3f(min, newMax, newMax), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0F, newMin, newMin), new Vector3f(0F, newMax, newMax), faceW, sprite, EnumFacing.WEST, rot, (BlockPartRotation)null, uvLocked));
			}
			
			if(renderE){
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(max, newMax, newMin), new Vector3f(16F, newMax, newMax), faceU, sprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(max, newMin, newMin), new Vector3f(16F, newMin, newMax), faceD, sprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(max, newMin, newMin), new Vector3f(16F, newMax, newMin), faceN, sprite, EnumFacing.NORTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(max, newMin, newMax), new Vector3f(16F, newMax, newMax), faceS, sprite, EnumFacing.SOUTH, rot, (BlockPartRotation)null, uvLocked));
				quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(16F, newMin, newMin), new Vector3f(16F, newMax, newMax), faceE, sprite, EnumFacing.EAST, rot, (BlockPartRotation)null, uvLocked));
			}
		}
		
		if(cState !=null && renderWater && MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT){
			TextureAtlasSprite waterSprite = RenderUtil.getSprite("minecraft:blocks/water_still");
			float height = FluidUtil.getFluidHeight(cState.blockAccess, cState.pos, Material.WATER) * 16F;
			height -= 0.015F;
			
			final BlockFaceUV uvFull = new BlockFaceUV(new float[]{0, 0, 16, 16}, 0);
			final BlockPartFace faceFull = new BlockPartFace(EnumFacing.UP, 0, "", uvFull);
			final BlockPartFace faceFullDown = new BlockPartFace(EnumFacing.DOWN, 0, "", uvFull);
			
			
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, height, 0), new Vector3f(16F, height, 16F), faceFull, waterSprite, EnumFacing.UP, rot, (BlockPartRotation)null, uvLocked));
			quads.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, height-0.001f, 0), new Vector3f(16F, height-0.001f, 16F), faceFullDown, waterSprite, EnumFacing.DOWN, rot, (BlockPartRotation)null, uvLocked));
		}
		
		return quads;
	}

    public TextureAtlasSprite getTexture(){
    	return RenderUtil.getSprite(CrystalMod.resourceL("blocks/coral/coral_"+color.getName()));
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

