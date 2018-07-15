package alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

public class ModelPipeBaked implements IPerspectiveAwareModel 
{
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation("crystalmod:crystalpipe");
	public static FaceBakery faceBakery;
    private final ItemStack renderStack;
    
	public ModelPipeBaked()
	{
		this.renderStack = ItemStackTools.getEmptyStack();
	}
	
	public ModelPipeBaked( ItemStack stack )
	{
		this.renderStack = stack;
	}
    
    private void renderIronCap(final FakeState state, final EnumFacing dir, final List<BakedQuad> list) {
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
        
        final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        
        final BlockFaceUV uv270 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 270);
        final BlockPartFace face270 = new BlockPartFace((EnumFacing)null, 0, "", uv270);
        
        final BlockFaceUV uv90 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 90);
        final BlockPartFace face90 = new BlockPartFace((EnumFacing)null, 0, "", uv90);
        
        final BlockFaceUV uv180 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 180);
        final BlockPartFace face180 = new BlockPartFace((EnumFacing)null, 0, "", uv180);
        
        TextureAtlasSprite iron = RenderUtil.getSprite("crystalmod:blocks/pipe/iron_cap");
        TextureAtlasSprite spriteLapis = RenderUtil.getSprite("crystalmod:blocks/pipe/io_out");
        TextureAtlasSprite spriteRedstone = RenderUtil.getSprite("crystalmod:blocks/pipe/io_in");
        TextureAtlasSprite spriteQuartz = RenderUtil.getSprite("crystalmod:blocks/pipe/io_inout");
        
        boolean scale = false;
        if(state !=null && state.pipe !=null && state.pipe.getPipeType() !=null){
        	if(!state.pipe.getPipeType().useIOTextures()){
        		spriteRedstone = iron;
        		spriteLapis = iron;
        		spriteQuartz = iron;
        		scale = true;
        	}
        }
        
        ConnectionMode mode = (state !=null && state.pipe !=null) ? state.pipe.getConnectionMode(dir) : ConnectionMode.DISABLED;
        TextureAtlasSprite modeSprite = mode == ConnectionMode.IN_OUT ? spriteQuartz : mode == ConnectionMode.OUTPUT ? spriteRedstone : mode == ConnectionMode.INPUT ? spriteLapis : iron;
        
        
        
        if(mode != ConnectionMode.DISABLED){        	
	        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 13.0f, 14.0f), new Vector3f(13.0f, 13.0f, 15.0f), face180, modeSprite, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 14.0f), new Vector3f(13.0f, 3.0f, 15.0f), face, modeSprite, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 14.0f), new Vector3f(13.0f, 13.0f, 15.0f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 14.0f), new Vector3f(13.0f, 13.0f, 15.0f), face, iron, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 14.0f), new Vector3f(3.0f, 13.0f, 15.0f), face90, modeSprite, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(13.0f, 3.0f, 14.0f), new Vector3f(13.0f, 13.0f, 15.0f), face270, modeSprite, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));
	        
	        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 14.0f, 15.0f), new Vector3f(14.0f, 14.0f, 16.0f), face, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 15.0f), new Vector3f(14.0f, 2.0f, 16.0f), face, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 15.0f), new Vector3f(14.0f, 14.0f, 16.0f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 15.0f), new Vector3f(14.0f, 14.0f, 16.0f), face, iron, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 15.0f), new Vector3f(2.0f, 14.0f, 16.0f), face, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
	        list.add(faceBakery.makeBakedQuad(new Vector3f(14.0f, 2.0f, 15.0f), new Vector3f(14.0f, 14.0f, 16.0f), face, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
        }
    }
    
    @Override
	public List<BakedQuad> getQuads( @Nullable IBlockState state, @Nullable EnumFacing side, long rand )
	{
		FakeState renderState = state != null && state instanceof FakeState ? (FakeState)state : null;

		if(side !=null)
		{
			return Collections.emptyList();
		}
		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		
		if(layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT || renderStack !=null)addPipeQuads( renderState, quads );
		return quads;
	}
    
    public List<BakedQuad> addPipeQuads(FakeState state, List<BakedQuad> list) {
        final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        
        boolean scale = true;
        
        boolean extensionDown = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.DOWN) && state.pipe.getConnectionMode(EnumFacing.DOWN) != ConnectionMode.DISABLED) : false;
        boolean extensionUp = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.UP) && state.pipe.getConnectionMode(EnumFacing.UP) != ConnectionMode.DISABLED) : false;
        boolean extensionNorth = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.NORTH) && state.pipe.getConnectionMode(EnumFacing.NORTH) != ConnectionMode.DISABLED) : false;
        boolean extensionSouth = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.SOUTH) && state.pipe.getConnectionMode(EnumFacing.SOUTH) != ConnectionMode.DISABLED) : false;
        boolean extensionWest = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.WEST) && state.pipe.getConnectionMode(EnumFacing.WEST) != ConnectionMode.DISABLED) : false;
        boolean extensionEast = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.EAST) && state.pipe.getConnectionMode(EnumFacing.EAST) != ConnectionMode.DISABLED) : false;

        TextureAtlasSprite connector = getConnectorSprite();
        TextureAtlasSprite core = getCoreTexture(state);
        
		scale = false;
		float max = 12.0f;
		float min = 4.0f;
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
        
        float minExt = 5;
        float maxExt = 11;
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
        
        for (EnumFacing dir : EnumFacing.VALUES) {
        	boolean safe = (state !=null && state.pipe !=null);
        	if (safe ? state.pipe.containsExternalConnection(dir) : false) {
            	renderIronCap(state, dir, list);
            }
            if (safe && state.pipe.getCoverData(dir) !=null) {
            	//CoverData data = state.pipe.getCoverData(dir);
            	//List<BakedQuad> q = CoverRender.getBakedCoverQuads(state.blockAccess, state.pos, data.getBlockState(), n, FacadeBuilder.getFacadeBox(dir, false));
            	//list.addAll(q);
                //this.addCover(state, state.pipe.getCoverData(dir), dir, list);
            }
            if (safe && state.pipe.getAttachmentData(dir) !=null) {
            	state.pipe.getAttachmentData(dir).addQuads(faceBakery, list, dir);
            }
        }
        
        return list;
    }
    
    private TextureAtlasSprite getCoreTexture(FakeState state) {
    	TextureAtlasSprite glassSquare = null;        
        String texture = "";
        if(ItemStackTools.isValid(renderStack)){
        	int type = renderStack.getMetadata();
	        if(type == 0){
	        	texture = ("crystalmod:blocks/pipe/item_square");
	        }
	        if(type == 1){
	        	texture = ("crystalmod:blocks/pipe/fluid_square");
	        }
	        if(type == 2){
	        	texture = ("crystalmod:blocks/pipe/storage_square");
	        }
	        if(type == 3){
	        	texture = "crystalmod:blocks/pipe/power_square_"+ItemNBTHelper.getInteger(renderStack, "Tier", 0);
	        }
	        if(type == 4){
	        	texture = "crystalmod:blocks/pipe/rfpower_square_"+ItemNBTHelper.getInteger(renderStack, "Tier", 0);
	        }
        }else{
        	if(state !=null && state.pipe !=null){
        		texture = (state.pipe.getPipeType().getCoreTexture(state.pipe));
	        }
        }
        
        if(!Strings.isNullOrEmpty(texture))glassSquare = RenderUtil.getSprite(texture);
        
        if(glassSquare == null){
        	glassSquare = RenderUtil.getMissingSprite();
        }
        
        return glassSquare;
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
        return getCoreTexture(null);
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    
    static {
        faceBakery = new FaceBakery();
    }

	
	public static TextureAtlasSprite getIronSprite(){
		TextureAtlasSprite iron = RenderUtil.getSprite("crystalmod:blocks/pipe/iron_cap");
		return iron !=null ? iron : RenderUtil.getMissingSprite();
	}
	
	public static TextureAtlasSprite getConnectorSprite(){
		TextureAtlasSprite iron = RenderUtil.getSprite("crystalmod:blocks/pipe/pipe_connector");
		return iron !=null ? iron : RenderUtil.getMissingSprite();
	}
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return new ItemOverrideList(new ArrayList<ItemOverride>()){
			@Override
			public IBakedModel handleItemState( IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity )
			{
				if( !( stack.getItem() == Item.getItemFromBlock(ModBlocks.crystalPipe)) )
				{
					return originalModel;
				}
				return new ModelPipeBaked( stack );
			}
		};
	}

	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
    	return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS, cameraTransformType);
    }
	
}

