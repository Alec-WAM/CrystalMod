package alec_wam.CrystalMod.blocks.decorative.bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.model.dynamic.DelegatingDynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.tiles.machine.FakeTileState;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.BlockPlanks;
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
import net.minecraft.world.World;

public class ModelBridge extends DelegatingDynamicItemAndBlockModel 
{
	static FaceBakery faceBakery;
    WoodType type;
    
    public ModelBridge(WoodType type) {
    	super();
    	this.type = type;
    	state = null;
    }
    
    public FakeTileState<?> state;
    
    public ModelBridge(FakeTileState<?> state, EnumFacing facing, long rand) {
        super(state, facing, rand);
        this.state = state;
    }

    public ModelBridge(ItemStack itemStack, World world, EntityLivingBase entity) {
        super(itemStack, world, entity);
        state = null;
    }
    
    public List<BakedQuad> getFaceQuads(final EnumFacing p_177551_1_) {
        return new ArrayList<BakedQuad>();
    }
    
    @Override
	public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        WoodType type = WoodType.OAK;
        if(ItemStackTools.isValid(itemStack)){
        	type = WoodType.byMetadata(itemStack.getMetadata());
        } 
        else if(state !=null){
        	type = state.state.getValue(WoodenBlockProperies.WOOD);
        }
        TextureAtlasSprite planks = WoodenBlockProperies.getPlankTexture(type);
        Vector3f min = new Vector3f(0f, 16.0f*0.2f, 0f);
        Vector3f max = new Vector3f(16.0f, 16.0f*0.2f, 16.0f);
        BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f,0.0f, 16.0f, 16.0f }, 0);
        new BlockPartFace((EnumFacing)null, 0, "", uv);
        ModelRotation modelRot = ModelRotation.X0_Y0;
        float sidewidth = 16.0f*0.2f;
        //NS
        TileBridge bridge = state !=null && state.tile !=null && state.tile instanceof TileBridge ? (TileBridge) state.tile : null;
        
        boolean left = false;
        boolean right = false;
        boolean front = false;
        boolean back = false;
        
        BlockPlanks.EnumType leftType = null;
        BlockPlanks.EnumType rightType = null;
        BlockPlanks.EnumType frontType = null;
        BlockPlanks.EnumType backType = null;

        if(bridge !=null){
        	if(bridge.getBase(EnumFacing.WEST) !=null){
        		leftType = bridge.getBase(EnumFacing.WEST);
        		left = true;
        	}
        	if(bridge.getBase(EnumFacing.EAST) !=null){
        		rightType = bridge.getBase(EnumFacing.EAST);
        		right = true;
        	}
        	if(bridge.getBase(EnumFacing.NORTH) !=null){
        		frontType = bridge.getBase(EnumFacing.NORTH);
        		front = true;
        	}
        	if(bridge.getBase(EnumFacing.SOUTH) !=null){
        		backType = bridge.getBase(EnumFacing.SOUTH);
        		back = true;
        	}
        }
        
        min.x = !left ? 0 : sidewidth;
        max.x = 16.0f-(!right ? 0 : sidewidth);
        min.z = !front ? 0 : sidewidth;
        max.z = 16.0f-(!back ? 0 : sidewidth);
        
        BlockFaceUV uvS = new BlockFaceUV(new float[] { min.x, min.z, max.x, max.z}, 0);
        BlockPartFace faceS = new BlockPartFace((EnumFacing)null, 0, "", uvS);
        
        boolean scale = true;
        if(scale){
        	faceS.blockFaceUV.uvs = new float[] { 0, 0, 16, 16};
        }
        
        list.add(faceBakery.makeBakedQuad(min, max, faceS, planks, EnumFacing.UP, modelRot, (BlockPartRotation)null, false, true));
        min.y = max.y = 0.0f;
        list.add(faceBakery.makeBakedQuad(min, max, faceS, planks, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, false, true));
        
        uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, sidewidth, 16.0f}, 0);
        new BlockPartFace((EnumFacing)null, 0, "", uv);
        float minSideZ = front ? sidewidth : 0.0f;
    	float maxSideZ = back ? 16.0f-sidewidth : 16.0F;
        if(left){
        	addBase(list, 0f, sidewidth, minSideZ, maxSideZ, ModelRotation.X0_Y0, getLogSprite(leftType, false), getLogSprite(leftType, true));
        } else {
        	min = new Vector3f(0f, 0f, 0f);
            max = new Vector3f(0f, 16.0f*0.2f, 16f);
            min.z = !front ? 0 : sidewidth;
            max.z = 16.0f-(!back ? 0 : sidewidth);
            BlockFaceUV uv2 = new BlockFaceUV(new float[] { sidewidth, 0.0f, 16.0f-sidewidth, 16.0f*0.2f}, 0);
            BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        	list.add(faceBakery.makeBakedQuad(min, max, face2, planks, EnumFacing.WEST, modelRot, (BlockPartRotation)null, false, true));
        }
        
        if(right){
        	addBase(list, 16.0f-sidewidth, 16.0f, minSideZ, maxSideZ, ModelRotation.X0_Y0, getLogSprite(rightType, false), getLogSprite(rightType, true));
        } else {
        	min = new Vector3f(16f, 0f, 0f);
            max = new Vector3f(16.0f, 16.0f*0.2f, 16f);
            min.z = !front ? 0 : sidewidth;
            max.z = 16.0f-(!back ? 0 : sidewidth);
            BlockFaceUV uv2 = new BlockFaceUV(new float[] { sidewidth, 0.0f, 16.0f-sidewidth, 16.0f*0.2f}, 0);
            BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        	list.add(faceBakery.makeBakedQuad(min, max, face2, planks, EnumFacing.EAST, modelRot, (BlockPartRotation)null, false, true));
        }
        
        if(front){
        	addBase(list, 0f, sidewidth, 0f, 16f, ModelRotation.X0_Y90, getLogSprite(frontType, false), getLogSprite(frontType, true));
        }
        else {
        	min = new Vector3f(0f, 0f, 0f);
            max = new Vector3f(16.0f, 16.0f*0.2f, 0f);
            min.x = !left ? 0 : sidewidth;
            max.x = 16.0f-(!right ? 0 : sidewidth);
            BlockFaceUV uv2 = new BlockFaceUV(new float[] { sidewidth, 0.0f, 16.0f-sidewidth, 16.0f*0.2f}, 0);
            BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        	list.add(faceBakery.makeBakedQuad(min, max, face2, planks, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, false, true));
        }
        
        if(back){
        	addBase(list, 0f, sidewidth, 0f, 16f, ModelRotation.X0_Y270, getLogSprite(backType, false), getLogSprite(backType, true));
        }
        else {
        	min = new Vector3f(0f, 0f, 16f);
            max = new Vector3f(16.0f, 16.0f*0.2f, 16f);
            min.x = !left ? 0 : sidewidth;
            max.x = 16.0f-(!right ? 0 : sidewidth);
            BlockFaceUV uv2 = new BlockFaceUV(new float[] { sidewidth, 0.0f, 16.0f-sidewidth, 16.0f*0.2f}, 0);
            BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        	list.add(faceBakery.makeBakedQuad(min, max, face2, planks, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, false, true));
        }
        
        boolean leftSide = left;
        boolean topLeft = bridge !=null && bridge.hasPost(EnumFacing.WEST, 2);
        boolean rightSide = right;
        boolean topRight = bridge !=null && bridge.hasPost(EnumFacing.EAST, 2);
        boolean frontSide = front;
        boolean topFront = bridge !=null && bridge.hasPost(EnumFacing.NORTH, 2);
        boolean backSide = back;
        boolean topBack = bridge !=null && bridge.hasPost(EnumFacing.SOUTH, 2);
        
        float minTopZ = topFront ? sidewidth : 0.0f;
    	float maxTopZ = topBack ? 16.0f-sidewidth : 16.0F;
                
        if(leftSide){
        	for(int i = 0; i < 2; i++){
	        	int poleIndex = i == 0 ? 1 : 3;
	        	if(bridge !=null && bridge.hasPost(EnumFacing.WEST, i)){
	        		addPoleSetup(list, 0, sidewidth, minTopZ, maxTopZ, ModelRotation.X0_Y0, poleIndex, getLogSprite(leftType, false), getLogSprite(leftType, true), topLeft, i == 1);
	        	}
        	}
        }  
        if(rightSide){
        	for(int i = 0; i < 2; i++){
	        	int poleIndex = i == 0 ? 1 : 3;
	        	if(bridge !=null && bridge.hasPost(EnumFacing.EAST, i)){
	        		addPoleSetup(list, 16f-sidewidth, 16f, minTopZ, maxTopZ, ModelRotation.X0_Y0, poleIndex, getLogSprite(rightType, false), getLogSprite(rightType, true), topRight, i == 1);
	        	}
        	}
        }       
        if(frontSide){
        	for(int i = 0; i < 2; i++){
	        	int poleIndex = i == 0 ? 1 : 3;
	        	if(bridge !=null && bridge.hasPost(EnumFacing.NORTH, i)){
	        		addPoleSetup(list, 16f-sidewidth, 16f, 0f, 16f, ModelRotation.X0_Y270, poleIndex, getLogSprite(frontType, false), getLogSprite(frontType, true), topFront, i == 1);
	        	}
        	}
        }    
        if(backSide){
        	for(int i = 0; i < 2; i++){
	        	int poleIndex = i == 0 ? 1 : 3;
	        	if(bridge !=null && bridge.hasPost(EnumFacing.SOUTH, i)){
	        		addPoleSetup(list, 0, sidewidth, 0f, 16f, ModelRotation.X0_Y270, poleIndex, getLogSprite(backType, false), getLogSprite(backType, true), topBack, i == 1);
	        	}
        	}
        }    
        return list;
    }
    
    public void addBase(List<BakedQuad> list, float minX, float maxX, float minZ, float maxZ, ModelRotation rot, TextureAtlasSprite log, TextureAtlasSprite logTop){
    	BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16f, 16.0f}, 0);
    	BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
    	
    	Vector3f min = new Vector3f(minX, 16.0f*0.2f, minZ);
    	Vector3f max = new Vector3f(maxX, 16.0f*0.2f, maxZ);
    	list.add(faceBakery.makeBakedQuad(min, max, face, log, EnumFacing.UP, rot, (BlockPartRotation)null, false, true));
    	min.y = max.y = 0.0f;
    	list.add(faceBakery.makeBakedQuad(min, max, face, log, EnumFacing.DOWN, rot, (BlockPartRotation)null, false, true));
    	
    	BlockFaceUV uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16f, 16.0f}, 90);
        BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        min = new Vector3f(minX, 0, minZ);
        max = new Vector3f(minX, 16.0f*0.2f, maxZ);
    	list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.WEST, rot, (BlockPartRotation)null, false, true));
    	
    	min = new Vector3f(maxX, 0, minZ);
        max = new Vector3f(maxX, 16.0f*0.2f, maxZ);
    	list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.EAST, rot, (BlockPartRotation)null, false, true));
    	
    	min = new Vector3f(minX, 0f, minZ);
        max = new Vector3f(maxX, 16.0f*0.2f, minZ);
        uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f}, 0);
        face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        list.add(faceBakery.makeBakedQuad(min, max, face2, logTop, EnumFacing.NORTH, rot, (BlockPartRotation)null, false, true));
    	
    	min = new Vector3f(minX, 0f, maxZ);
        max = new Vector3f(maxX, 16.0f*0.2f, maxZ);
        list.add(faceBakery.makeBakedQuad(min, max, face2, logTop, EnumFacing.SOUTH, rot, (BlockPartRotation)null, false, true));
    }
    
    public void addPoleSetup(List<BakedQuad> list, float minX, float maxX, float minBar, float maxBar, ModelRotation rot, int poleIndex, TextureAtlasSprite log, TextureAtlasSprite logTop, boolean topBar, boolean renderTopBar){
    	float chunk = 16.0F*0.2f;
        float poleHeight = 12.0f;
        float[] polePos = {chunk*0, chunk*1, chunk*2, chunk*3, chunk*4};
    	Vector3f min = new Vector3f(minX, 16.0f*0.2f, polePos[poleIndex]);
    	Vector3f max = new Vector3f(maxX, poleHeight, polePos[poleIndex]);
        BlockFaceUV uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f}, 0);
        BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        
        list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.NORTH, rot, (BlockPartRotation)null, false, true));
        min.z = max.z = chunk*(poleIndex+1);
        list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.SOUTH, rot, (BlockPartRotation)null, false, true));
        
        min = new Vector3f(minX, 16.0f*0.2f, polePos[poleIndex]);
        max = new Vector3f(minX, poleHeight, polePos[poleIndex]+(chunk));
        list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.WEST, rot, (BlockPartRotation)null, false, true));
        
        min.x = max.x = maxX;
        list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.EAST, rot, (BlockPartRotation)null, false, true));
        
        if(!topBar){
        	min = new Vector3f(minX, poleHeight, polePos[poleIndex]);
            max = new Vector3f(maxX, poleHeight, polePos[poleIndex]+(chunk));
            list.add(faceBakery.makeBakedQuad(min, max, face2, logTop, EnumFacing.UP, rot, (BlockPartRotation)null, false, true));
        } 
        else if(renderTopBar){
        	uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f}, 90);
            face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
            min = new Vector3f(minX, poleHeight, minBar);
            max = new Vector3f(minX, poleHeight+chunk, maxBar);
        	list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.WEST, rot, (BlockPartRotation)null, false, true));
        	min.x = max.x = maxX;
        	list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.EAST, rot, (BlockPartRotation)null, false, true));
        	
        	uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f}, 0);
            face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
        	min = new Vector3f(minX, poleHeight+chunk, minBar);
            max = new Vector3f(maxX, poleHeight+chunk, maxBar);
            list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.UP, rot, (BlockPartRotation)null, false, true));
            min.y = max.y = poleHeight;
            list.add(faceBakery.makeBakedQuad(min, max, face2, log, EnumFacing.DOWN, rot, (BlockPartRotation)null, false, true));
        
            min = new Vector3f(minX, poleHeight, minBar);
            max = new Vector3f(maxX, poleHeight+chunk, minBar);
            list.add(faceBakery.makeBakedQuad(min, max, face2, logTop, EnumFacing.NORTH, rot, (BlockPartRotation)null, false, true));
            min.z = max.z = maxBar;
            list.add(faceBakery.makeBakedQuad(min, max, face2, logTop, EnumFacing.SOUTH, rot, (BlockPartRotation)null, false, true));
        }
    }
    
    public TextureAtlasSprite getLogSprite(BlockPlanks.EnumType type, boolean top){
    	if(type == null)return RenderUtil.getMissingSprite();
    	if(top)return RenderUtil.getSprite("minecraft:blocks/log_"+type.getUnlocalizedName().toLowerCase()+"_top");
        return RenderUtil.getSprite("minecraft:blocks/log_"+type.getUnlocalizedName().toLowerCase());
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
    	WoodType type = WoodType.OAK;
    	if(this.type !=null){
    		type = this.type;
        }
        return WoodenBlockProperies.getPlankTexture(type);
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    public static final Map<Integer, ModelBridge> ITEMMODELS = Maps.newHashMap();
    
    static {
        ModelBridge.faceBakery = new FaceBakery();
    }

	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return (state instanceof FakeTileState) ? new ModelBridge((FakeTileState<?>)state, side, rand) : null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		int meta = stack.getMetadata();
		if(!ITEMMODELS.containsKey(meta)){
			ITEMMODELS.put(meta, new ModelBridge(stack, world, entity));
		}
		return ITEMMODELS.get(stack.getMetadata());
	}

	
}

