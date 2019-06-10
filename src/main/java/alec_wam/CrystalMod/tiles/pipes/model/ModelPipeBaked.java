package alec_wam.CrystalMod.tiles.pipes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.tiles.pipes.BlockPipe;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.ConnectionType;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

@SuppressWarnings("deprecation")
public class ModelPipeBaked implements IBakedModel 
{
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation("crystalmod:crystalpipe");
	public static FaceBakery faceBakery;
    private final TextureAtlasSprite core;
	public ModelPipeBaked(TextureAtlasSprite core)
	{
		this.core = core;
	}
    
    private void renderIronCap(final BlockState state, final Direction dir, final List<BakedQuad> list) {
        ModelRotation modelRot = ModelRotation.X0_Y0;
        switch (dir) {
            case DOWN: {
                modelRot = ModelRotation.X270_Y0;
                break;
            }
            case UP: {
                modelRot = ModelRotation.X90_Y0;
                break;
            }
            case NORTH: {
                modelRot = ModelRotation.X180_Y0;
                break;
            }
            case SOUTH: {
                modelRot = ModelRotation.X0_Y0;
                break;
            }
            case WEST: {
                modelRot = ModelRotation.X0_Y90;
                break;
            }
            case EAST: {
                modelRot = ModelRotation.X0_Y270;
                break;
            }
        }
        
         
        TextureAtlasSprite iron = RenderUtil.getSprite("crystalmod:block/pipe/iron_cap");
        boolean scale = true;
        TextureAtlasSprite modeSprite = iron;       	
        final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace((Direction)null, 0, "", uv);
        final BlockFaceUV uvThin = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 3.0f }, 0);
        final BlockPartFace faceThin = new BlockPartFace((Direction)null, 0, "", uvThin);
        final BlockFaceUV uvThinS = new BlockFaceUV(new float[] { 0.0f, 0.0f, 3.0f, 16.0f }, 0);
        final BlockPartFace faceThinS = new BlockPartFace((Direction)null, 0, "", uvThinS);
        
        float minSmall = 4.0F;
        float maxSmall = 12.0F;
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, maxSmall, 14.0f), new Vector3f(maxSmall, maxSmall, 15.0f), faceThin, modeSprite, Direction.UP, modelRot, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, minSmall, 14.0f), new Vector3f(maxSmall, minSmall, 15.0f), faceThin, modeSprite, Direction.DOWN, modelRot, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, minSmall, 14.0f), new Vector3f(maxSmall, maxSmall, 15.0f), face, iron, Direction.NORTH, modelRot, (BlockPartRotation)null, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, minSmall, 14.0f), new Vector3f(minSmall, maxSmall, 15.0f), faceThinS, modeSprite, Direction.WEST, modelRot, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(maxSmall, minSmall, 14.0f), new Vector3f(maxSmall, maxSmall, 15.0f), faceThinS, modeSprite, Direction.EAST, modelRot, (BlockPartRotation)null, scale));
        
        float minLarge = 3.0F;
        float maxLarge = 13.0F;
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, maxLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), faceThin, iron, Direction.UP, modelRot, (BlockPartRotation)null, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(maxLarge, minLarge, 16.0f), faceThin, iron, Direction.DOWN, modelRot, (BlockPartRotation)null, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), face, iron, Direction.NORTH, modelRot, (BlockPartRotation)null, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), face, iron, Direction.SOUTH, modelRot, (BlockPartRotation)null, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(minLarge, maxLarge, 16.0f), faceThinS, iron, Direction.WEST, modelRot, (BlockPartRotation)null, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(maxLarge, minLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), faceThinS, iron, Direction.EAST, modelRot, (BlockPartRotation)null, true));
        
    }
    
    @Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
	{
    	if(side !=null)
		{
			return Collections.emptyList();
		}
		
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		addPipeQuads( state, quads );
		return quads;
	}
    
    public List<BakedQuad> addPipeQuads(BlockState state, List<BakedQuad> list) {
        final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace((Direction)null, 0, "", uv);
        
        boolean scale = true;
        
        boolean extensionUp = state !=null && BlockPipe.getConnection(state, Direction.UP) != ConnectionType.NONE;
        boolean extensionDown = state !=null && BlockPipe.getConnection(state, Direction.DOWN) != ConnectionType.NONE;
        boolean extensionNorth = state !=null && BlockPipe.getConnection(state, Direction.NORTH) != ConnectionType.NONE;
        boolean extensionSouth = state !=null && BlockPipe.getConnection(state, Direction.SOUTH) != ConnectionType.NONE;
        boolean extensionEast = state !=null && BlockPipe.getConnection(state, Direction.EAST) != ConnectionType.NONE;
        boolean extensionWest = state !=null && BlockPipe.getConnection(state, Direction.WEST) != ConnectionType.NONE;
        
        TextureAtlasSprite connector = getConnectorSprite();
        TextureAtlasSprite core = getCoreTexture();
        
		scale = false;
		float max = 11.0f;
		float min = 5.0f;
		final BlockFaceUV uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 180);
		final BlockPartFace face2 = new BlockPartFace((Direction)null, 0, "", uv2);
		
		//DOWN
		list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, connector, Direction.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, core, Direction.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, core, Direction.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        
        //UP
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, connector, Direction.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, core, Direction.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, core, Direction.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        
        //NORTH
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, connector, Direction.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, core, Direction.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, core, Direction.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        
        //SOUTH
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, connector, Direction.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, core, Direction.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, core, Direction.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        
        //WEST
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, connector, Direction.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, core, Direction.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, core, Direction.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        
        //EAST
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, connector, Direction.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, core, Direction.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, core, Direction.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale));
        
        scale = true;
        
        float minExt = 5.6F;
        float maxExt = 10.4F;
        boolean scaleExt = false;
        final BlockPartFace faceExtTop = new BlockPartFace((Direction)null, 0, "", new BlockFaceUV(new float[] { 5, 0, 11, 4 }, 0));
        final BlockPartFace faceExtBottom = new BlockPartFace((Direction)null, 0, "", new BlockFaceUV(new float[] { 5, 12, 11, 16 }, 0));
        final BlockPartFace faceExtLeft = new BlockPartFace((Direction)null, 0, "", new BlockFaceUV(new float[] { 0, 5, 4, 11 }, 0));
        final BlockPartFace faceExtRight = new BlockPartFace((Direction)null, 0, "", new BlockFaceUV(new float[] { 12, 5, 16, 11 }, 0));

        if (extensionDown) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, 0.0F, maxExt), new Vector3f(maxExt, minExt, maxExt), faceExtBottom, connector, Direction.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, 0.0F, minExt), new Vector3f(maxExt, minExt, minExt), faceExtBottom, connector, Direction.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, 0.0F, minExt), new Vector3f(minExt, minExt, maxExt), faceExtBottom, connector, Direction.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, 0.0F, minExt), new Vector3f(maxExt, minExt, maxExt), faceExtBottom, connector, Direction.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
        }
        if (extensionUp) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, maxExt), new Vector3f(maxExt, 16.0F, maxExt), faceExtTop, connector, Direction.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, minExt), new Vector3f(maxExt, 16.0F, minExt), faceExtTop, connector, Direction.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, minExt), new Vector3f(minExt, 16.0F, maxExt), faceExtTop, connector, Direction.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, maxExt, minExt), new Vector3f(maxExt, 16.0F, maxExt), faceExtTop, connector, Direction.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
        }
        if (extensionNorth) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, 0.0F), new Vector3f(maxExt, maxExt, minExt), faceExtTop, connector, Direction.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, minExt), new Vector3f(minExt, minExt, 0.0F), faceExtTop, connector, Direction.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, minExt, 0.0F), new Vector3f(minExt, maxExt, minExt), faceExtLeft, connector, Direction.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, 0.0F), new Vector3f(maxExt, maxExt, minExt), faceExtRight, connector, Direction.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
        }
        if (extensionSouth) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, maxExt), new Vector3f(maxExt, maxExt, 16.0f), faceExtBottom, connector, Direction.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, 16.0F), new Vector3f(minExt, minExt, maxExt), faceExtBottom, connector, Direction.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, minExt, maxExt), new Vector3f(minExt, maxExt, 16.0f), faceExtRight, connector, Direction.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, maxExt), new Vector3f(maxExt, maxExt, 16.0f), faceExtLeft, connector, Direction.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
        }
        if (extensionWest) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, maxExt, minExt), new Vector3f(minExt, maxExt, maxExt), faceExtLeft, connector, Direction.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, minExt, maxExt), new Vector3f(0.0F, minExt, minExt), faceExtRight, connector, Direction.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, minExt, minExt), new Vector3f(minExt, maxExt, minExt), faceExtRight, connector, Direction.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, minExt, maxExt), new Vector3f(minExt, maxExt, maxExt), faceExtLeft, connector, Direction.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
        }
        if (extensionEast) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, maxExt, minExt), new Vector3f(16.0F, maxExt, maxExt), faceExtRight, connector, Direction.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, minExt, maxExt), new Vector3f(maxExt, minExt, minExt), faceExtLeft, connector, Direction.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, minExt), new Vector3f(16.0F, maxExt, minExt), faceExtLeft, connector, Direction.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, maxExt), new Vector3f(16.0F, maxExt, maxExt), faceExtRight, connector, Direction.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt));
        }
        
        for (Direction dir : Direction.values()) {
        	if (state !=null && BlockPipe.getConnection(state, dir) == ConnectionType.EXTERNAL) {
            	renderIronCap(state, dir, list);
            }
        }
        return list;
    }
    
    private TextureAtlasSprite getCoreTexture() {
    	if(core == null){
    		return RenderUtil.getMissingSprite();
    	}
    	return core;
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
        return getCoreTexture();
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
    	return ItemCameraTransforms.DEFAULT;
    }    
    
    static {
        faceBakery = new FaceBakery();
    }

	
	public static TextureAtlasSprite getIronSprite(){
		TextureAtlasSprite iron = RenderUtil.getSprite("crystalmod:block/pipe/iron_cap");
		return iron !=null ? iron : RenderUtil.getMissingSprite();
	}
	
	public static TextureAtlasSprite getConnectorSprite(){
		TextureAtlasSprite iron = RenderUtil.getSprite("crystalmod:block/pipe/connector");
		return iron !=null ? iron : RenderUtil.getMissingSprite();
	}
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.EMPTY;
	}
	
}

