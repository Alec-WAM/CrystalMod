package alec_wam.CrystalMod.tiles.tank;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class ModelTank extends DynamicItemAndBlockModel {

	public static final ModelTank INSTANCE = new ModelTank();
	static FaceBakery faceBakery;
	static {
        faceBakery = new FaceBakery();
    }
	
	private final FakeTankState state;
	private final FluidStack stack;
	private final int type;
	public ModelTank(){
		super(true, false);
		state = null;
		this.stack = null;
		this.type = -1;
	}
	public ModelTank(int type, FluidStack fluid){
		super(false, true);
		state = null;
		this.type = type;
		this.stack = fluid;
	}
	public ModelTank(FakeTankState state){
		super(false, false);
		this.state = state;
		this.stack = null;
		this.type = -1;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return super.getQuads(state, side, rand);
	}
	
	@Override
	public List<BakedQuad> getGeneralQuads() {
		List<BakedQuad> list = Lists.newArrayList();
		boolean item = this.type >=0;
		boolean shade = true;
		TankType tankType = null;
		if(state !=null){
			tankType = state.state.getValue(BlockTank.TYPE);
		} else if(item){
			tankType = TankType.values()[type];
		} 
		
		if(tankType == null){
			tankType = TankType.BLUE;
		}
		
		ModelRotation coverModelRot = ModelRotation.X0_Y0;
        final BlockFaceUV uvCover = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        TextureAtlasSprite blockTexture = RenderUtil.getTexture(ModBlocks.crystalIngot.getStateFromMeta(CrystalIngotBlockType.BLUE.getMeta()));
       
    	if(tankType == TankType.RED){
	        blockTexture = RenderUtil.getTexture(ModBlocks.crystalIngot.getStateFromMeta(CrystalIngotBlockType.RED.getMeta()));
        }
        if(tankType == TankType.GREEN){
	        blockTexture = RenderUtil.getTexture(ModBlocks.crystalIngot.getStateFromMeta(CrystalIngotBlockType.GREEN.getMeta()));
        }
        if(tankType == TankType.DARK){
	        blockTexture = RenderUtil.getTexture(ModBlocks.crystalIngot.getStateFromMeta(CrystalIngotBlockType.DARK.getMeta()));
        }
        if(tankType == TankType.PURE){
	        blockTexture = RenderUtil.getTexture(ModBlocks.crystalIngot.getStateFromMeta(CrystalIngotBlockType.PURE.getMeta()));
        }
        if(tankType == TankType.CREATIVE){
	        blockTexture = RenderUtil.getSprite("crystalmod:blocks/tank/tank_creative");
        }
        TextureAtlasSprite textureUp = blockTexture;
    	TextureAtlasSprite textureDown = blockTexture;
    	TextureAtlasSprite textureNorth = blockTexture;
    	TextureAtlasSprite textureSouth = blockTexture;
    	TextureAtlasSprite textureWest = blockTexture;
    	TextureAtlasSprite textureEast = blockTexture;
        for(EnumFacing dir : EnumFacing.VALUES){
	        BlockPartFace faceCover = new BlockPartFace(dir, 0, "", uvCover);
	        
	        if(dir !=EnumFacing.UP && dir !=EnumFacing.DOWN){
	        	faceCover.blockFaceUV.uvs = new float[] { 1.0f, 1.0f, 15.0f, 15.0f };
	        	textureUp = RenderUtil.getSprite("crystalmod:blocks/tank/glass");
		    	textureDown = textureUp;
		    	textureNorth = textureUp;
		    	textureSouth = textureUp;
		    	textureWest = textureUp;
		    	textureEast = textureUp;
	        }else{
	        	faceCover.blockFaceUV.uvs = new float[] { 0.0f, 0.0f, 16.0f, 16.0f };
	        }
	        
	        switch (dir.ordinal()) {
	            case 0: {
	            	coverModelRot = ModelRotation.X270_Y0;
	                break;
	            }
	            case 1: {
	            	coverModelRot = ModelRotation.X90_Y0;
	                break;
	            }
	            case 2: {
	            	coverModelRot = ModelRotation.X180_Y0;
	                break;
	            }
	            case 3: {
	            	coverModelRot = ModelRotation.X0_Y0;
	                break;
	            }
	            case 4: {
	            	coverModelRot = ModelRotation.X0_Y90;
	                break;
	            }
	            case 5: {
	            	coverModelRot = ModelRotation.X0_Y270;
	                break;
	            }
	        }
	        if(dir == EnumFacing.UP || dir == EnumFacing.DOWN){
	        	if(tankType == TankType.CREATIVE){
			        textureUp = RenderUtil.getSprite("crystalmod:blocks/tank/tank_creative");
		        }
		        faceCover.blockFaceUV.uvs = new float[] { 0.0f, 15.0f, 16.0f, 16.0f };
		        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 16.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 16.0f), faceCover, textureUp, EnumFacing.UP, coverModelRot, (BlockPartRotation)null, true, shade));
		        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 0.0f, 16.0f), faceCover, textureDown, EnumFacing.DOWN, coverModelRot, (BlockPartRotation)null, true, shade));
		        faceCover.blockFaceUV.uvs = new float[] { 0.0f, 0.0f, 16.0f, 16.0f };
		        
		        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, coverModelRot, (BlockPartRotation)null, true, shade));
		        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, true, shade));
		    	
		        faceCover.blockFaceUV.uvs = new float[] { 0.0f, 15.0f, 1.0f, 16.0f };
		        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(0.0f, 16.0f, 16.0f), faceCover, textureWest, EnumFacing.WEST, coverModelRot, (BlockPartRotation)null, true, shade));
		    	list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(16.0f, 0.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 16.0f), faceCover, textureEast, EnumFacing.EAST, coverModelRot, (BlockPartRotation)null, true, shade));
		    	faceCover.blockFaceUV.uvs = new float[] { 0.0f, 0.0f, 16.0f, 16.0f };
	        }else{
	        	list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(1.0f, 15.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(15.0f, 15.0f, 16.0f), faceCover, textureUp, EnumFacing.UP, coverModelRot, (BlockPartRotation)null, true, shade));
		    	list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(1.0f, 1.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(15.0f, 1.0f, 16.0f), faceCover, textureDown, EnumFacing.DOWN, coverModelRot, (BlockPartRotation)null, true, shade));
		    	list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(1.0f, 1.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(15.0f, 15.0f, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, coverModelRot, (BlockPartRotation)null, true, shade));
		    	list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(1.0f, 1.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(15.0f, 15.0f, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, true, shade));
		    	list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(1.0f, 1.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(1.0f, 15.0f, 16.0f), faceCover, textureWest, EnumFacing.WEST, coverModelRot, (BlockPartRotation)null, true, shade));
		    	list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(15.0f, 1.0f, 15.0f), new org.lwjgl.util.vector.Vector3f(15.0f, 15.0f, 16.0f), faceCover, textureEast, EnumFacing.EAST, coverModelRot, (BlockPartRotation)null, true, shade));
		    }
	        
        }
	        
        textureUp = blockTexture;
    	textureDown = blockTexture;
    	textureNorth = blockTexture;
    	textureSouth = blockTexture;
    	textureWest = blockTexture;
    	textureEast = blockTexture;
	        
        float minZ = 15f;
    	float UpLeftOffsetHollow = 15f;
    	float maxPole = 15f;
		float minPole = 1f;
		final BlockPartFace faceCover = new BlockPartFace(EnumFacing.SOUTH, 0, "", uvCover);
		faceCover.blockFaceUV.uvs = new float[] { 0.0f, 0.0f, 1.0f, 1.0f };
    	list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, maxPole, minZ), new Vector3f(16.0f, maxPole, 16.0f), faceCover, textureUp, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(16.0f, minPole, 16.0f), faceCover, textureDown, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(16.0f, maxPole, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(16.0f, maxPole, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(UpLeftOffsetHollow, maxPole, 16.0f), faceCover, textureWest, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(16.0f, minPole, minZ), new Vector3f(16.0f, maxPole, 16.0f), faceCover, textureEast, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, maxPole, minZ), new Vector3f(1.0f, maxPole, 16.0f), faceCover, textureUp, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, minPole, minZ), new Vector3f(1.0f, minPole, 16.0f), faceCover, textureDown, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, minPole, minZ), new Vector3f(1.0f, maxPole, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, minPole, minZ), new Vector3f(1.0f, maxPole, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, minPole, minZ), new Vector3f(0f, maxPole, 16.0f), faceCover, textureWest, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(1.0f, minPole, minZ), new Vector3f(1.0f, maxPole, 16.0f), faceCover, textureEast, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, maxPole, 0), new Vector3f(16.0f, maxPole, 1.0f), faceCover, textureUp, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, 0), new Vector3f(16.0f, minPole, 1.0f), faceCover, textureDown, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, 0), new Vector3f(16.0f, maxPole, 1.0f), faceCover, textureNorth, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, 0), new Vector3f(16.0f, maxPole, 1.0f), faceCover, textureSouth, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, 0), new Vector3f(UpLeftOffsetHollow, maxPole, 1.0f), faceCover, textureWest, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(16.0f, minPole, 0), new Vector3f(16.0f, maxPole, 1.0f), faceCover, textureEast, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
	
		
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, minPole, 0), new Vector3f(1.0f, maxPole, 1.0f), faceCover, textureNorth, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, minPole, 0), new Vector3f(1.0f, maxPole, 1.0f), faceCover, textureSouth, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(0f, minPole, 0), new Vector3f(0f, maxPole, 1.0f), faceCover, textureWest, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		list.add(faceBakery.makeBakedQuad(new Vector3f(1.0f, minPole, 0), new Vector3f(1.0f, maxPole, 1.0f), faceCover, textureEast, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, true, shade));
		
		
		FluidStack fluid = stack !=null ? stack : (state !=null && state.tank !=null && state.tank.tank !=null) ? state.tank.tank.getFluid() : null;
		if(fluid !=null){
			int maxBuckets = 0;
			int size = 0;
			if(item){
				size = type;
			}else{
				size = (state !=null && state.state !=null && state.state.getValue(BlockTank.TYPE) !=null) ? state.state.getValue(BlockTank.TYPE).getMeta() : 0;
			}
			
			maxBuckets = BlockTank.tankCaps[size]*Fluid.BUCKET_VOLUME;
			
			list.addAll(RenderUtil.getTankQuads(fluid, maxBuckets, 1.0F, 16.0F));
		}
		return list;
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
		return RenderUtil.getSprite("crystalmod:blocks/tank/glass");
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return (state instanceof FakeTankState) ? new ModelTank(((FakeTankState)state)) : null;
	}
	
	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		int type = stack.getMetadata();
		FluidStack fluid = null;
		if(stack.hasTagCompound()){
			FluidTank tank = ItemBlockTank.loadTank(stack.getTagCompound());
			if(tank !=null){
				fluid = tank.getFluid();
			}
		}
		return new ModelTank(type, fluid);
	}
}
