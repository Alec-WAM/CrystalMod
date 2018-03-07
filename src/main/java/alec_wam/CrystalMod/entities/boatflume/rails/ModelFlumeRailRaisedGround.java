package alec_wam.CrystalMod.entities.boatflume.rails;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.blocks.FakeBlockStateWithData;
import alec_wam.CrystalMod.client.model.dynamic.DelegatingDynamicItemAndBlockModel;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBase;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBase.EnumRailDirection;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBaseLand;
import alec_wam.CrystalMod.tiles.machine.FakeTileState;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModelFlumeRailRaisedGround extends DelegatingDynamicItemAndBlockModel 
{
	static FaceBakery faceBakery;
    final String railType;
    
    public ModelFlumeRailRaisedGround(String type) {
    	super();
    	this.railType = type;
        state = null;
    }
    
    public FakeBlockStateWithData state;
    
    public ModelFlumeRailRaisedGround(String type, FakeBlockStateWithData state, EnumFacing facing, long rand) {
        super(state, facing, rand);
        this.railType = type;
        this.state = state;
    }

    public ModelFlumeRailRaisedGround(String type, ItemStack itemStack, World world, EntityLivingBase entity) {
        super(itemStack, world, entity);
        this.railType = type;
        state = null;
    }
    
    public TextureAtlasSprite getRailSprite(){
    	return RenderUtil.getSprite("crystalmod:blocks/flume/"+railType);
    }
    
    @Override
	public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
       
        TextureAtlasSprite rail = getRailSprite();
        TextureAtlasSprite wall = RenderUtil.getSprite("crystalmod:blocks/flume/trough");
        IProperty<BlockFlumeRailBase.EnumRailDirection> property = null;
        EnumRailDirection railDirection = null;
        if(state !=null && state.getBlock() instanceof BlockFlumeRailBase){
        	property = ((BlockFlumeRailBase)state.getBlock()).getShapeProperty();
        	railDirection = state.getValue(property);
        }
        ModelRotation rotation = ModelRotation.X0_Y0;
        if(railDirection !=null){
        	if(railDirection == EnumRailDirection.ASCENDING_SOUTH){
        		rotation = ModelRotation.X0_Y180;
        	}
        	if(railDirection == EnumRailDirection.ASCENDING_EAST){
        		rotation = ModelRotation.X0_Y90;
        	}
        	if(railDirection == EnumRailDirection.ASCENDING_WEST){
        		rotation = ModelRotation.X0_Y270;
        	}
        }
        
        BlockPartRotation railAngle = new BlockPartRotation(new Vector3f(0.5f, 0.5f, 0.5f), Axis.X, 45.0f, true);
        
        //Rail
        BlockFaceUV uvBasic = new BlockFaceUV(new float[] { 0, 0, 16, 16}, 0);
        BlockPartFace faceRail = new BlockPartFace((EnumFacing)null, 0, "", uvBasic);
        
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0f, 9.0f, 0.0f), new Vector3f(16.0f, 9.0f, 16.0f), faceRail, rail, EnumFacing.UP, rotation, railAngle, false, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0f, 8.0f, 0.0f), new Vector3f(16.0f, 8.0f, 16.0f), faceRail, wall, EnumFacing.DOWN, rotation, railAngle, false, true));

        boolean transitionPieceTop = false;
        boolean topIsLandRail = false;
        boolean transitionPieceBottom = false;
        boolean bottomIsLandRail = false;
        if(railDirection !=null){
        	
        	EnumFacing railFacing = null;
        	if(railDirection == EnumRailDirection.ASCENDING_NORTH){
        		railFacing = EnumFacing.NORTH;
        	}
        	if(railDirection == EnumRailDirection.ASCENDING_SOUTH){
        		railFacing = EnumFacing.SOUTH;
        	}
        	if(railDirection == EnumRailDirection.ASCENDING_EAST){
        		railFacing = EnumFacing.EAST;
        	}
        	if(railDirection == EnumRailDirection.ASCENDING_WEST){
        		railFacing = EnumFacing.WEST;
        	}
        	BlockPos otherPos = state.pos.offset(railFacing).up();
    		IBlockState otherRail = state.blockAccess.getBlockState(otherPos);
    		if(BlockFlumeRailBase.isRailBlock(otherRail)){
    			topIsLandRail = otherRail.getBlock() instanceof BlockFlumeRailBaseLand;
    			BlockFlumeRailBase otherRailBlock = (BlockFlumeRailBase)otherRail.getBlock();
    			EnumRailDirection otherDirection = otherRailBlock.getRailDirection(state.blockAccess, otherPos, otherRail, null);
    			transitionPieceTop = !otherDirection.isAscending();
    		}
    		
    		otherPos = state.pos.offset(railFacing.getOpposite());
    		otherRail = state.blockAccess.getBlockState(otherPos);
    		if(BlockFlumeRailBase.isRailBlock(otherRail)){
    			bottomIsLandRail = otherRail.getBlock() instanceof BlockFlumeRailBaseLand;
    			BlockFlumeRailBase otherRailBlock = (BlockFlumeRailBase)otherRail.getBlock();
    			EnumRailDirection otherDirection = otherRailBlock.getRailDirection(state.blockAccess, otherPos, otherRail, null);
    			transitionPieceBottom = !otherDirection.isAscending();
    		}
        }
        
        //Walls
        BlockFaceUV uvWallSide = new BlockFaceUV(new float[] { 0, 0, 16, 4}, 0);
        BlockPartFace faceWall = new BlockPartFace((EnumFacing)null, 0, "", uvWallSide);
        BlockFaceUV uvWallTop = new BlockFaceUV(new float[] { 0, 0, 1, 1}, 0);
        BlockPartFace faceWallTop = new BlockPartFace((EnumFacing)null, 0, "", uvWallTop);
    	
        float wallMax = 11.0F;
        float wallMin = 8.0F;
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0f, wallMax, 0.0f), new Vector3f(17.0f, wallMax, bottomIsLandRail ? 14.0F : 16.0f), faceWallTop, wall, EnumFacing.UP, rotation, railAngle, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0f, wallMin, 0.0f), new Vector3f(17.0f, wallMin, bottomIsLandRail ? 14.0F : 16.0f), faceWallTop, wall, EnumFacing.DOWN, rotation, railAngle, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(17.0f, wallMin, 0.0f), new Vector3f(17.0f, wallMax, bottomIsLandRail ? 14.0F : 16.0f), faceWall, wall, EnumFacing.EAST, rotation, railAngle, false, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0f, wallMin, 0.0f), new Vector3f(16.0f, wallMax, bottomIsLandRail ? 14.0F : 16.0f), faceWall, wall, EnumFacing.WEST, rotation, railAngle, false, true));
        
        list.add(faceBakery.makeBakedQuad(new Vector3f(-1.0f, wallMax, 0.0f), new Vector3f(0.0f, wallMax, bottomIsLandRail ? 14.0F : 16.0f), faceWallTop, wall, EnumFacing.UP, rotation, railAngle, false, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(-1.0f, wallMin, 0.0f), new Vector3f(0.0f, wallMin, bottomIsLandRail ? 14.0F : 16.0f), faceWallTop, wall, EnumFacing.DOWN, rotation, railAngle, false, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0f, wallMin, 0.0f), new Vector3f(0.0f, wallMax, bottomIsLandRail ? 14.0F : 16.0f), faceWall, wall, EnumFacing.EAST, rotation, railAngle, false, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(-1.0f, wallMin, 0.0f), new Vector3f(-1.0f, wallMax, bottomIsLandRail ? 14.0F : 16.0f), faceWall, wall, EnumFacing.WEST, rotation, railAngle, false, true));

        
        if(transitionPieceTop){
        	if(topIsLandRail){
        		BlockFaceUV uvWallTransition = new BlockFaceUV(new float[] { 0, 0, 2, 1}, 0);
                BlockPartFace faceWallT = new BlockPartFace((EnumFacing)null, 0, "", uvWallTransition);
	        	BlockPartRotation angle2 = new BlockPartRotation(new Vector3f(0.5f, 0.5f, 0.5f), Axis.X, 20.5f, true);
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(17.001f, 11.1f, -0.3f), new Vector3f(17.001f, 14.0f, 2.0f), faceRail, wall, EnumFacing.EAST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(16.001f, 11.1f, -0.3f), new Vector3f(16.001f, 14.0f, 2.0f), faceRail, wall, EnumFacing.WEST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(16.001f, 14.0f, -0.3f), new Vector3f(17.001f, 14.0f, 2.0f), faceWallT, wall, EnumFacing.UP, rotation, angle2, false, true));
	        	
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(-1.001f, 11.1f, -0.3f), new Vector3f(-1.001f, 14.0f, 2.0f), faceRail, wall, EnumFacing.WEST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(0.001f, 11.1f, -0.3f), new Vector3f(0.001f, 14.0f, 2.0f), faceRail, wall, EnumFacing.EAST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(-1.001f, 14.0f, -0.3f), new Vector3f(0.001f, 14.0f, 2.0f), faceWallT, wall, EnumFacing.UP, rotation, angle2, false, true));
        	} else {
        		list.add(faceBakery.makeBakedQuad(new Vector3f(16.0f, wallMin, 0.0f), new Vector3f(17.0f, wallMax, 0.0f), faceRail, wall, EnumFacing.NORTH, rotation, railAngle, false, true));                
        		list.add(faceBakery.makeBakedQuad(new Vector3f(-1.0f, wallMin, 0.0f), new Vector3f(0.0f, wallMax, 0.0f), faceRail, wall, EnumFacing.NORTH, rotation, railAngle, false, true));                
        	}
        	BlockFaceUV uvTop = new BlockFaceUV(new float[] { 0, 0, 16, 1}, 0);
            BlockPartFace faceRailTop = new BlockPartFace((EnumFacing)null, 0, "", uvTop);
            BlockPartRotation angle3 = new BlockPartRotation(new Vector3f(0.5f, 0.5f, 0.5f), Axis.X, 19.0f, true);
        	list.add(faceBakery.makeBakedQuad(new Vector3f(0.0f, 17.0f, 0.0f), new Vector3f(16.0f, 17.0f, 1.0f), faceRailTop, rail, EnumFacing.UP, rotation, null, false, true));
        }
        if(transitionPieceBottom){
        	if(bottomIsLandRail){
	        	BlockPartRotation angle2 = new BlockPartRotation(new Vector3f(0.5f, 0.5f, 0.5f), Axis.X, 42.8f, true);
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(17.001f, 7.4f, 14.0F), new Vector3f(17.001f, 10.8f, 16.03f), faceRail, wall, EnumFacing.EAST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(15.999f, 7.4f, 14.0F), new Vector3f(15.999f, 10.8f, 16.03f), faceRail, wall, EnumFacing.WEST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(15.999f, 10.8f, 14.03f), new Vector3f(17.001f, 10.8f, 16.03f), faceRail, wall, EnumFacing.UP, rotation, angle2, false, true));
	        	
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(-1.001f, 7.4f, 14.0F), new Vector3f(-1.001f, 10.8f, 16.03f), faceRail, wall, EnumFacing.WEST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(0.001f, 7.4f, 14.0F), new Vector3f(0.001f, 10.8f, 16.03f), faceRail, wall, EnumFacing.EAST, rotation, angle2, false, true));
	        	list.add(faceBakery.makeBakedQuad(new Vector3f(-1.001f, 10.8f, 14.03f), new Vector3f(0.001f, 10.8f, 16.03f), faceRail, wall, EnumFacing.UP, rotation, angle2, false, true));
        	} else {
        		list.add(faceBakery.makeBakedQuad(new Vector3f(16.0f, wallMin, 16.0f), new Vector3f(17.0f, wallMax, 16.0f), faceRail, wall, EnumFacing.SOUTH, rotation, railAngle, false, true));                
        		list.add(faceBakery.makeBakedQuad(new Vector3f(-1.0f, wallMin, 16.0f), new Vector3f(0.0f, wallMax, 16.0f), faceRail, wall, EnumFacing.SOUTH, rotation, railAngle, false, true));                
        	}
        }
        
        return list;
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
        return RenderUtil.getSprite("crystalmod:blocks/flume/trough");
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
   static {
        ModelFlumeRailRaisedGround.faceBakery = new FaceBakery();
    }

	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return (state instanceof FakeBlockStateWithData) ? new ModelFlumeRailRaisedGround(railType, (FakeBlockStateWithData)state, side, rand) : null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		return new ModelFlumeRailRaisedGround(railType, stack, world, entity);
	}

	
}

