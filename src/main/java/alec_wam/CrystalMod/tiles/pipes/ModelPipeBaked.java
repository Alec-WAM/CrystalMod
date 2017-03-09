package alec_wam.CrystalMod.tiles.pipes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ModelPipeBaked implements IPerspectiveAwareModel 
{
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation("crystalmod:crystalpipe");
	public static FaceBakery faceBakery;
    
    private final FacadeBuilder facadeBuilder;

    private final ItemStack renderStack;
    
	public ModelPipeBaked( FacadeBuilder facadeBuilder )
	{
		this.renderStack = ItemStackTools.getEmptyStack();
		this.facadeBuilder = facadeBuilder;
	}
	
	public ModelPipeBaked( ItemStack stack )
	{
		this.renderStack = stack;
		this.facadeBuilder = null;
	}
    
    private void drawGlassStump(final ModelRotation modelRot, final List<BakedQuad> list) {
        final boolean scale = false;
        TextureAtlasSprite glass = RenderUtil.getSprite("crystalmod:blocks/pipe/power_plus");
        float max = 11/*pixel*11.0F*/;
        float min = 5/*pixel*5F*/;
        
        final BlockPartFace face4 = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { min, max+4F, max, 16.0f }, 0));
        final BlockPartFace face3 = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { min, max, min, 16.0f }, 0));
        final BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { max, max, max, 16.0f }, 0));
        final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[] { min, max, max, 16.0f }, 0));
        
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, max), new Vector3f(max, max, 16.0f), face, glass, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0f), face4, glass, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(min, max, 16.0f), face3, glass, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, max), new Vector3f(max, max, 16.0f), face2, glass, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));
    }
    
    private void renderIronCap(final FakeState state, final int dir, final List<BakedQuad> list) {
        ModelRotation modelRot = ModelRotation.X0_Y0;
        switch (dir) {
            case 0: {
                modelRot = ModelRotation.X270_Y0;
                break;
            }
            case 1: {
                modelRot = ModelRotation.X90_Y0;
                break;
            }
            case 2: {
                modelRot = ModelRotation.X180_Y0;
                break;
            }
            case 3: {
                modelRot = ModelRotation.X0_Y0;
                break;
            }
            case 4: {
                modelRot = ModelRotation.X0_Y90;
                break;
            }
            case 5: {
                modelRot = ModelRotation.X0_Y270;
                break;
            }
        }
        //modelRot = ModelRotation.X0_Y0;
        
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
        
        ConnectionMode mode = (state !=null && state.pipe !=null) ? state.pipe.getConnectionMode(EnumFacing.getFront(dir)) : ConnectionMode.DISABLED;
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
    
    public void addCover(final FakeState state, final CoverData data, final EnumFacing dir, final List<BakedQuad> list){
    	ModelRotation coverModelRot = ModelRotation.X0_Y0;
        final BlockFaceUV uvCover = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace faceCover = new BlockPartFace(dir, 0, "", uvCover);
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
            	coverModelRot = ModelRotation.X0_Y180;
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
        IBlockState coverState = data.getBlockState();
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(coverState);
        final boolean scale = false;
        //if(test)return;
        int id = Block.getStateId(coverState);
        final TextureAtlasSprite blockTextureUp = CoverUtil.findTexture(id, model, EnumFacing.UP);
        final TextureAtlasSprite blockTextureDown = CoverUtil.findTexture(id, model, EnumFacing.DOWN);
        final TextureAtlasSprite blockTextureNorth = CoverUtil.findTexture(id, model, EnumFacing.NORTH);
        final TextureAtlasSprite blockTextureSouth = CoverUtil.findTexture(id, model, EnumFacing.SOUTH);
        final TextureAtlasSprite blockTextureWest = CoverUtil.findTexture(id, model, EnumFacing.WEST);
        final TextureAtlasSprite blockTextureEast = CoverUtil.findTexture(id, model, EnumFacing.EAST);
        boolean safe = (state !=null && state.pipe !=null);
        //boolean attachment = /*safe ? state.pipe.getAttachmentData(dir) !=null :*/ true;
        TextureAtlasSprite textureUp = blockTextureUp;
    	TextureAtlasSprite textureDown = blockTextureDown;
    	TextureAtlasSprite textureNorth = blockTextureNorth;
    	TextureAtlasSprite textureSouth = blockTextureSouth;
    	TextureAtlasSprite textureWest = blockTextureWest;
    	TextureAtlasSprite textureEast = blockTextureEast;
    	boolean shrinkUp = false;
    	boolean shrinkDown = false;
    	boolean shrinkLeft = false;
    	boolean shrinkRight = false;
    	
        if(dir == EnumFacing.UP){
        	shrinkUp = !(safe && state.pipe.getCoverData(EnumFacing.NORTH) == null);
        	shrinkDown = !(safe && state.pipe.getCoverData(EnumFacing.SOUTH) == null);
        	shrinkLeft = !(safe && state.pipe.getCoverData(EnumFacing.WEST) == null);
        	shrinkRight = !(safe && state.pipe.getCoverData(EnumFacing.EAST) == null);
        	textureUp = blockTextureWest;
        	textureDown = blockTextureEast;
        	textureNorth = blockTextureDown;
        	textureSouth = blockTextureUp;
        	textureWest = blockTextureNorth;
        	textureEast = blockTextureSouth;
        }
        if(dir == EnumFacing.DOWN){
        	shrinkUp = !(safe && state.pipe.getCoverData(EnumFacing.SOUTH) == null);
        	shrinkDown = !(safe && state.pipe.getCoverData(EnumFacing.NORTH) == null);
        	shrinkLeft = !(safe && state.pipe.getCoverData(EnumFacing.WEST) == null);
        	shrinkRight = !(safe && state.pipe.getCoverData(EnumFacing.EAST) == null);
        	textureUp = blockTextureEast;
        	textureDown = blockTextureWest;
        	textureNorth = blockTextureUp;
        	textureSouth = blockTextureDown;
        	textureWest = blockTextureNorth;
        	textureEast = blockTextureSouth;
        }
        if(dir == EnumFacing.NORTH){
        	//REALLY UP
        	shrinkDown = !(safe && state.pipe.getCoverData(EnumFacing.DOWN) == null);
        	shrinkUp = !(safe && state.pipe.getCoverData(EnumFacing.UP) == null);
        	shrinkLeft = !(safe && state.pipe.getCoverData(EnumFacing.EAST) == null);
        	shrinkRight = !(safe && state.pipe.getCoverData(EnumFacing.WEST) == null);
        	textureUp = blockTextureUp;
        	textureDown = blockTextureDown;
        	textureNorth = blockTextureSouth;
        	textureSouth = blockTextureNorth;
        	textureWest = blockTextureEast;
        	textureEast = blockTextureWest;
        }
        if(dir == EnumFacing.SOUTH){
        	shrinkUp = !(safe && state.pipe.getCoverData(EnumFacing.UP) == null);
        	shrinkDown = !(safe && state.pipe.getCoverData(EnumFacing.DOWN) == null);
        	shrinkLeft = !(safe && state.pipe.getCoverData(EnumFacing.WEST) == null);
        	shrinkRight = !(safe && state.pipe.getCoverData(EnumFacing.EAST) == null);
        }
        if(dir == EnumFacing.WEST){
        	shrinkUp = !(safe && state.pipe.getCoverData(EnumFacing.UP) == null);
        	shrinkDown = !(safe && state.pipe.getCoverData(EnumFacing.DOWN) == null);
        	shrinkLeft = !(safe && state.pipe.getCoverData(EnumFacing.NORTH) == null);
        	shrinkRight = !(safe && state.pipe.getCoverData(EnumFacing.SOUTH) == null);
        	textureNorth = blockTextureEast;
        	textureSouth = blockTextureWest;
        	textureWest = blockTextureSouth;
        	textureEast = blockTextureSouth;
        }
        if(dir == EnumFacing.EAST){
        	shrinkUp = !(safe && state.pipe.getCoverData(EnumFacing.UP) == null);
        	shrinkDown = !(safe && state.pipe.getCoverData(EnumFacing.DOWN) == null);
        	shrinkLeft = !(safe && state.pipe.getCoverData(EnumFacing.SOUTH) == null);
        	shrinkRight = !(safe && state.pipe.getCoverData(EnumFacing.NORTH) == null);
        	textureNorth = blockTextureWest;
        	textureSouth = blockTextureEast;
        	textureWest = blockTextureSouth;
        	textureEast = blockTextureNorth;
        }	
        
        	
        	
        	boolean cut = false;
        	
        	float attchmentSize = 8;
        	if(safe && state.pipe.getAttachmentData(dir) !=null){
        		attchmentSize = 1.8f;
        	}
        	if(safe && state.pipe.isConnectedTo(dir)){
        		attchmentSize = 4.8f;
        	}
        	float minZ = 14f;
        	float minX = shrinkLeft ? 2f : 0.0f;
			float maxX = shrinkRight ? 14f : 16.0f;
			
        	float UpLeftOffsetHollow = 16f-attchmentSize;
        	float DownRightOffsetHollow = attchmentSize;
        	
        	
        	int color =	Color.BLUE.getRGB();
        	IBlockAccess world = (state !=null) ? state.blockAccess : null;
        	BlockPos pos = (state !=null) ? state.pos : null;
            int color2 = Minecraft.getMinecraft().getBlockColors().colorMultiplier(coverState, world, pos, 0);
            
            Color test = new Color(color2);
            Color real = new Color(test.getBlue(), test.getGreen(), test.getRed());
            color = real.getRGB();
            if(color == -1){
            	 
            	float f = 1.0f;
                int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
                color = -16777216 | i << 16 | i << 8 | i;
            }
        	
        	//UP
        	boolean uvLocked = false;
			float upMaxZ = shrinkUp ? 14f : 16f;
			BlockFaceUV uv = new BlockFaceUV(new float[] { minX, minZ, maxX, 16f }, 0);
			BlockPartFace face = new BlockPartFace(EnumFacing.UP, 0, "", uv);
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, upMaxZ, minZ), new Vector3f(maxX, upMaxZ, 16f), face, textureUp, EnumFacing.UP, coverModelRot, (BlockPartRotation)null, uvLocked, color));
				
			BlockFaceUV uvD = new BlockFaceUV(new float[] { minX, minZ, maxX, 16f }, 180);
			BlockPartFace faceD = new BlockPartFace(EnumFacing.DOWN, 0, "", uvD);
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, UpLeftOffsetHollow, minZ), new Vector3f(maxX, UpLeftOffsetHollow, 16f), faceD, textureDown, EnumFacing.DOWN, coverModelRot, (BlockPartRotation)null, uvLocked, color));
    			
				
			faceCover.blockFaceUV.uvs = new float[]{16f-maxX, 16f-upMaxZ, 16f-minX, DownRightOffsetHollow};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, UpLeftOffsetHollow, minZ), new Vector3f(maxX, upMaxZ, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, coverModelRot, (BlockPartRotation)null, uvLocked, color));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
    			
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, DownRightOffsetHollow};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0.0f, UpLeftOffsetHollow, minZ), new Vector3f(16f, 16f, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, uvLocked, color));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
			
			faceCover.blockFaceUV.uvs = new float[]{minZ, 16-upMaxZ, 16f, DownRightOffsetHollow};
	        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, UpLeftOffsetHollow, minZ), new Vector3f(minX, upMaxZ, 16.0f), faceCover, textureWest, EnumFacing.WEST, coverModelRot, (BlockPartRotation)null, uvLocked, color));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
    	       
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 16f-upMaxZ, 16f-minZ, DownRightOffsetHollow};
	        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(maxX, UpLeftOffsetHollow, minZ), new Vector3f(maxX, upMaxZ, 16.0f), faceCover, textureEast, EnumFacing.EAST, coverModelRot, null, uvLocked, color));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
	        
        	//DOWN
    		float downMinZ = shrinkDown ? 2.0F : 0.0f;	
			uv = new BlockFaceUV(new float[] { minX, minZ, maxX, 16.0f }, 0);
			face = new BlockPartFace(EnumFacing.UP, 0, "", uv);
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, DownRightOffsetHollow, minZ), new Vector3f(maxX, DownRightOffsetHollow, 16f), face, textureUp, EnumFacing.UP, coverModelRot, (BlockPartRotation)null, uvLocked, color));
				
			uvD = new BlockFaceUV(new float[] { minX, minZ, maxX, 16f }, 180);
			faceD = new BlockPartFace(EnumFacing.DOWN, 0, "", uvD);
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, downMinZ, minZ), new Vector3f(maxX, downMinZ, 16f), faceD, textureDown, EnumFacing.DOWN, coverModelRot, (BlockPartRotation)null, uvLocked, color));
			
			
			faceCover.blockFaceUV.uvs = new float[]{16.0f-maxX, UpLeftOffsetHollow, 16f-minX, 16f-downMinZ};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, downMinZ, minZ), new Vector3f(maxX, DownRightOffsetHollow, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, coverModelRot, (BlockPartRotation)null, uvLocked, color));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
			
			faceCover.blockFaceUV.uvs = new float[]{0.0f, UpLeftOffsetHollow, 16f, 16f};
			list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0.0f, 0.0f, minZ), new Vector3f(16f, DownRightOffsetHollow, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, uvLocked, color));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
			
			faceCover.blockFaceUV.uvs = new float[]{minZ, UpLeftOffsetHollow, 16f, 16f-downMinZ};
	        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(minX, downMinZ, minZ), new Vector3f(minX, DownRightOffsetHollow, 16.0f), faceCover, textureWest, EnumFacing.WEST, coverModelRot, (BlockPartRotation)null, uvLocked, color));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
	        
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, UpLeftOffsetHollow, 16f-minZ, 16f-downMinZ};
	        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(maxX, downMinZ, minZ), new Vector3f(maxX, DownRightOffsetHollow, 16.0f), faceCover, textureEast, EnumFacing.EAST, coverModelRot, (BlockPartRotation)null, uvLocked, color));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
        	
	        //if(cut)return;
        	//if(test)return;
        	
    		float maxPole = UpLeftOffsetHollow;
    		float minPole = DownRightOffsetHollow;
    		//LEFT
			faceCover.blockFaceUV.uvs = new float[]{UpLeftOffsetHollow, minPole, 16f-minX, maxPole};
			list.add(faceBakery.makeBakedQuad(new Vector3f(minX, minPole, minZ), new Vector3f(DownRightOffsetHollow, maxPole, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, coverModelRot, (BlockPartRotation)null, scale, true));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
			
			faceCover.blockFaceUV.uvs = new float[]{0.0f, minPole, DownRightOffsetHollow, maxPole};
			list.add(faceBakery.makeBakedQuad(new Vector3f(0.0f, minPole, minZ), new Vector3f(DownRightOffsetHollow, maxPole, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, scale, true));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
			
			faceCover.blockFaceUV.uvs = new float[]{minZ, minPole, 16f, maxPole};
	        list.add(faceBakery.makeBakedQuad(new Vector3f(minX, minPole, minZ), new Vector3f(DownRightOffsetHollow, maxPole, 16.0f), faceCover, textureWest, EnumFacing.WEST, coverModelRot, (BlockPartRotation)null, scale, true));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
	        
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, minPole, 16f-minZ, maxPole};
	        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0f, minPole, minZ), new Vector3f(DownRightOffsetHollow, maxPole, 16.0f), faceCover, textureEast, EnumFacing.EAST, coverModelRot, (BlockPartRotation)null, scale, true));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
    		
			//RIGHT
			faceCover.blockFaceUV.uvs = new float[]{16-maxX, minPole, DownRightOffsetHollow, maxPole};
			list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(maxX, maxPole, 16.0f), faceCover, textureNorth, EnumFacing.NORTH, coverModelRot, (BlockPartRotation)null, scale, true));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
    			
			faceCover.blockFaceUV.uvs = new float[]{UpLeftOffsetHollow, minPole, 16f, maxPole};
			list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(16f, maxPole, 16.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, scale, true));
			faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
				
			faceCover.blockFaceUV.uvs = new float[]{minZ, minPole, 16f, maxPole};
	        list.add(faceBakery.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(16f, maxPole, 16.0f), faceCover, textureWest, EnumFacing.WEST, coverModelRot, (BlockPartRotation)null, scale, true));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
	        
	        
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, minPole, 16f-minZ, maxPole};
	        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(UpLeftOffsetHollow, minPole, minZ), new Vector3f(maxX, maxPole, 16.0f), faceCover, textureEast, EnumFacing.EAST, coverModelRot, (BlockPartRotation)null, true));
	        faceCover.blockFaceUV.uvs = new float[]{0.0f, 0.0f, 16f, 16F};
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
		
		if(facadeBuilder !=null && renderState !=null){
			List<BakedQuad> facadeQuads = Lists.newArrayList();
			List<AxisAlignedBB> bbs = Lists.newArrayList();
			boolean addFacade = false;
			if(addFacade){
				facadeBuilder.addFacades(renderState, layer, 
						renderState.pipe.covers,
						bbs,
						rand,
						facadeQuads
				);
			
				quads.addAll(facadeQuads);
			}
		}
		
		if(renderState !=null){
			/*@Nonnull
		    QuadCollector paintQuads = new QuadCollector();
		    boolean hasPaintRendered = false;
		    String cacheResult;

		    EnumFacing face = EnumFacing.SOUTH;
		    CoverData data = renderState.pipe.getCoverData(face);
		    if(data !=null){
		    	ModelWrangler.wrangleBakedModel(renderState.blockAccess, renderState.pos, data.getBlockState(), face, paintQuads);
		    }
		    quads.addAll(paintQuads.getQuads(side, layer));*/
			/*for(EnumFacing facing : EnumFacing.VALUES){
				CoverData facadeState = renderState.pipe.covers.get( facing );
				if(facadeState != null){
					IBlockState blockState = facadeState.getBlockState();
					
					if(renderState !=null && renderState.blockAccess !=null && renderState.pos !=null){
						IBlockAccess world = new PipeBlockAccessWrapper(renderState.blockAccess, renderState.pos, facing);
						blockState = facadeState.getBlockState().getBlock().getActualState(blockState, world, renderState.pos);
					}
					if(blockState.getBlock().canRenderInLayer(blockState, layer)){
						IBakedModel blockModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState( blockState );
						if(renderState !=null && renderState.blockAccess !=null && renderState.pos !=null){
							IBlockAccess world = new PipeBlockAccessWrapper(renderState.blockAccess, renderState.pos, facing);
							blockState = blockState.getBlock().getExtendedState(blockState, world, renderState.pos);
						}
						List<BakedQuad> modelQuads = blockModel.getQuads(blockState, facing, rand);
						for(BakedQuad quad : modelQuads){
							if(quad.getFace() == facing){
								quads.add(quad);
							}
						}
					}
				}
			}*/
		}

		return quads;
	}
    
    public List<BakedQuad> addPipeQuads(FakeState state, List<BakedQuad> list) {
        final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        
        boolean scale = true;
        
        boolean extension0 = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.getFront(0)) && state.pipe.getConnectionMode(EnumFacing.getFront(0)) != ConnectionMode.DISABLED) : false;
        boolean extension1 = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.getFront(1)) && state.pipe.getConnectionMode(EnumFacing.getFront(1)) != ConnectionMode.DISABLED) : false;
        boolean extension2 = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.getFront(2)) && state.pipe.getConnectionMode(EnumFacing.getFront(2)) != ConnectionMode.DISABLED) : false;
        boolean extension3 = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.getFront(3)) && state.pipe.getConnectionMode(EnumFacing.getFront(3)) != ConnectionMode.DISABLED) : false;
        boolean extension4 = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.getFront(4)) && state.pipe.getConnectionMode(EnumFacing.getFront(4)) != ConnectionMode.DISABLED) : false;
        boolean extension5 = (state !=null && state.pipe !=null) ? (state.pipe.isConnectedTo(EnumFacing.getFront(5)) && state.pipe.getConnectionMode(EnumFacing.getFront(5)) != ConnectionMode.DISABLED) : false;

        TextureAtlasSprite glass = RenderUtil.getSprite("crystalmod:blocks/pipe/power_plus");
        TextureAtlasSprite glassSquare = getCoreTexture(state);
        
		scale = false;
		float max = 12.0f;
		float min = 4.0f;
		final BlockFaceUV uv2 = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 180);
		final BlockPartFace face2 = new BlockPartFace((EnumFacing)null, 0, "", uv2);
		//if (!extension0) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, glass, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, glassSquare, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, min, max), face2, glassSquare, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        //}
        //if (!extension1) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, glass, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, glassSquare, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, min), new Vector3f(max, max, max), face2, glassSquare, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        //}
        //if (!extension2) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, glass, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, glassSquare, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, min), face, glassSquare, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        //}
        //if (!extension3) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, glass, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, glassSquare, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, max, max), face, glassSquare, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        //}
        //if (!extension4) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, glass, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, glassSquare, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(min, max, max), face, glassSquare, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        //}
        //if (!extension5) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, glass, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, glassSquare, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, min), new Vector3f(max, max, max), face, glassSquare, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        //}
        scale = true;
        
        if (extension0) {
            this.drawGlassStump(ModelRotation.X270_Y0, list);
        }
        if (extension1) {
            this.drawGlassStump(ModelRotation.X90_Y0, list);
        }
        if (extension2) {
            this.drawGlassStump(ModelRotation.X180_Y0, list);
        }
        if (extension3) {
            this.drawGlassStump(ModelRotation.X0_Y0, list);
        }
        if (extension4) {
            this.drawGlassStump(ModelRotation.X0_Y90, list);
        }
        if (extension5) {
            this.drawGlassStump(ModelRotation.X0_Y270, list);
        }
        
        for (int n = 0; n < 6; ++n) {
        	boolean safe = (state !=null && state.pipe !=null);
        	EnumFacing dir = EnumFacing.getFront(n);
        	if (safe ? state.pipe.containsExternalConnection(dir) : false) {
            	this.renderIronCap(state, n, list);
            }
            if (safe && state.pipe.getCoverData(dir) !=null) {
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
        if(!ItemStackTools.isNullStack(renderStack)){
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

	public boolean isAmbientOcclusion() {
        return true;
    }
    
    public boolean isGui3d() {
        return true;
    }
    
    public boolean isBuiltInRenderer() {
        return false;
    }
    
    public TextureAtlasSprite getParticleTexture() {
        return getCoreTexture(null);
    }
    
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

