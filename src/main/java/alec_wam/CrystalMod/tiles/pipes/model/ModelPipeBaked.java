package alec_wam.CrystalMod.tiles.pipes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.tiles.pipes.BlockPipe;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.ConnectionType;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.EnumFacing;

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
    
    private void renderIronCap(final IBlockState state, final EnumFacing dir, final List<BakedQuad> list) {
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
        final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        final BlockFaceUV uvThin = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 3.0f }, 0);
        final BlockPartFace faceThin = new BlockPartFace((EnumFacing)null, 0, "", uvThin);
        final BlockFaceUV uvThinS = new BlockFaceUV(new float[] { 0.0f, 0.0f, 3.0f, 16.0f }, 0);
        final BlockPartFace faceThinS = new BlockPartFace((EnumFacing)null, 0, "", uvThinS);
        
        float minSmall = 4.0F;
        float maxSmall = 12.0F;
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, maxSmall, 14.0f), new Vector3f(maxSmall, maxSmall, 15.0f), faceThin, modeSprite, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, minSmall, 14.0f), new Vector3f(maxSmall, minSmall, 15.0f), faceThin, modeSprite, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, minSmall, 14.0f), new Vector3f(maxSmall, maxSmall, 15.0f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minSmall, minSmall, 14.0f), new Vector3f(minSmall, maxSmall, 15.0f), faceThinS, modeSprite, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(maxSmall, minSmall, 14.0f), new Vector3f(maxSmall, maxSmall, 15.0f), faceThinS, modeSprite, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));
        
        float minLarge = 3.0F;
        float maxLarge = 13.0F;
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, maxLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), faceThin, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(maxLarge, minLarge, 16.0f), faceThin, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), face, iron, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(minLarge, minLarge, 15.0f), new Vector3f(minLarge, maxLarge, 16.0f), faceThinS, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(maxLarge, minLarge, 15.0f), new Vector3f(maxLarge, maxLarge, 16.0f), faceThinS, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
        
    }
    
    @Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand)
	{
    	if(side !=null)
		{
			return Collections.emptyList();
		}
		
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		addPipeQuads( state, quads );
		return quads;
	}
    
    public List<BakedQuad> addPipeQuads(IBlockState state, List<BakedQuad> list) {
        final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        
        boolean scale = true;
        
        boolean extensionUp = state !=null && BlockPipe.getConnection(state, EnumFacing.UP) != ConnectionType.NONE;
        boolean extensionDown = state !=null && BlockPipe.getConnection(state, EnumFacing.DOWN) != ConnectionType.NONE;
        boolean extensionNorth = state !=null && BlockPipe.getConnection(state, EnumFacing.NORTH) != ConnectionType.NONE;
        boolean extensionSouth = state !=null && BlockPipe.getConnection(state, EnumFacing.SOUTH) != ConnectionType.NONE;
        boolean extensionEast = state !=null && BlockPipe.getConnection(state, EnumFacing.EAST) != ConnectionType.NONE;
        boolean extensionWest = state !=null && BlockPipe.getConnection(state, EnumFacing.WEST) != ConnectionType.NONE;
        
        TextureAtlasSprite connector = getConnectorSprite();
        TextureAtlasSprite core = getCoreTexture();
        
		scale = false;
		float max = 11.0f;
		float min = 5.0f;
		final BlockFaceUV uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 180);
		final BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
		
		//DOWN
		list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, connector, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, core, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, core, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        
        //UP
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, connector, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, core, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, core, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        
        //NORTH
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, connector, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, core, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, core, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        
        //SOUTH
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, connector, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, core, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, core, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        
        //WEST
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, connector, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, core, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, core, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        
        //EAST
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, connector, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, core, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, core, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        
        scale = true;
        
        float minExt = 5.6F;
        float maxExt = 10.4F;
        boolean scaleExt = false;
        final BlockPartFace faceExtTop = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { 5, 0, 11, 4 }, 0));
        final BlockPartFace faceExtBottom = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { 5, 12, 11, 16 }, 0));
        final BlockPartFace faceExtLeft = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { 0, 5, 4, 11 }, 0));
        final BlockPartFace faceExtRight = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { 12, 5, 16, 11 }, 0));

        if (extensionDown) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, 0.0F, maxExt), new Vector3f(maxExt, minExt, maxExt), faceExtBottom, connector, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, 0.0F, minExt), new Vector3f(maxExt, minExt, minExt), faceExtBottom, connector, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, 0.0F, minExt), new Vector3f(minExt, minExt, maxExt), faceExtBottom, connector, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, 0.0F, minExt), new Vector3f(maxExt, minExt, maxExt), faceExtBottom, connector, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
        }
        if (extensionUp) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, maxExt), new Vector3f(maxExt, 16.0F, maxExt), faceExtTop, connector, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, minExt), new Vector3f(maxExt, 16.0F, minExt), faceExtTop, connector, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, minExt), new Vector3f(minExt, 16.0F, maxExt), faceExtTop, connector, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, maxExt, minExt), new Vector3f(maxExt, 16.0F, maxExt), faceExtTop, connector, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
        }
        if (extensionNorth) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, 0.0F), new Vector3f(maxExt, maxExt, minExt), faceExtTop, connector, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, minExt), new Vector3f(minExt, minExt, 0.0F), faceExtTop, connector, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, minExt, 0.0F), new Vector3f(minExt, maxExt, minExt), faceExtLeft, connector, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, 0.0F), new Vector3f(maxExt, maxExt, minExt), faceExtRight, connector, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
        }
        if (extensionSouth) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, maxExt, maxExt), new Vector3f(maxExt, maxExt, 16.0f), faceExtBottom, connector, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, 16.0F), new Vector3f(minExt, minExt, maxExt), faceExtBottom, connector, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, minExt, maxExt), new Vector3f(minExt, maxExt, 16.0f), faceExtRight, connector, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, maxExt), new Vector3f(maxExt, maxExt, 16.0f), faceExtLeft, connector, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
        }
        if (extensionWest) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, maxExt, minExt), new Vector3f(minExt, maxExt, maxExt), faceExtLeft, connector, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(minExt, minExt, maxExt), new Vector3f(0.0F, minExt, minExt), faceExtRight, connector, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, minExt, minExt), new Vector3f(minExt, maxExt, minExt), faceExtRight, connector, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, minExt, maxExt), new Vector3f(minExt, maxExt, maxExt), faceExtLeft, connector, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
        }
        if (extensionEast) {
        	list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, maxExt, minExt), new Vector3f(16.0F, maxExt, maxExt), faceExtRight, connector, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, minExt, maxExt), new Vector3f(maxExt, minExt, minExt), faceExtLeft, connector, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, minExt), new Vector3f(16.0F, maxExt, minExt), faceExtLeft, connector, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(maxExt, minExt, maxExt), new Vector3f(16.0F, maxExt, maxExt), faceExtRight, connector, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scaleExt, true));
        }
        
        for (EnumFacing dir : EnumFacing.values()) {
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

