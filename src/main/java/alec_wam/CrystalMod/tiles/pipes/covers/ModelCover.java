package alec_wam.CrystalMod.tiles.pipes.covers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Maps;

public class ModelCover extends DynamicItemAndBlockModel
{
	public static final ModelCover INSTANCE = new ModelCover();
	static FaceBakery faceBakery;
	private CoverData data;
    public ModelCover() {
    	this(null);
    }
    public ModelCover(CoverData data) {
    	super(false, true);
    	this.data = data;
    }
    
    public List<BakedQuad> getFaceQuads(final EnumFacing p_177551_1_) {
        return new ArrayList<BakedQuad>();
    }
    
    public void addCover(final CoverData data, final EnumFacing dir, final List<BakedQuad> list){
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
        final IBlockState bState = data.getBlockState();
        final IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(bState);
        final int id = Block.getStateId(bState);
        final TextureAtlasSprite blockTextureUp = CoverUtil.findTexture(id, model, EnumFacing.UP);
        final TextureAtlasSprite blockTextureDown = CoverUtil.findTexture(id, model, EnumFacing.DOWN);
        final TextureAtlasSprite blockTextureNorth = CoverUtil.findTexture(id, model, EnumFacing.NORTH);
        final TextureAtlasSprite blockTextureSouth = CoverUtil.findTexture(id, model, EnumFacing.SOUTH);
        final TextureAtlasSprite blockTextureWest = CoverUtil.findTexture(id, model, EnumFacing.WEST);
        final TextureAtlasSprite blockTextureEast = CoverUtil.findTexture(id, model, EnumFacing.EAST);

        TextureAtlasSprite textureUp = blockTextureUp;
    	TextureAtlasSprite textureDown = blockTextureDown;
    	TextureAtlasSprite textureNorth = blockTextureNorth;
    	TextureAtlasSprite textureSouth = blockTextureSouth;
    	TextureAtlasSprite textureWest = blockTextureWest;
    	TextureAtlasSprite textureEast = blockTextureEast;
        if(dir == EnumFacing.UP){
        	textureUp = blockTextureWest;
        	textureDown = blockTextureEast;
        	textureNorth = blockTextureDown;
        	textureSouth = blockTextureUp;
        	textureWest = blockTextureNorth;
        	textureEast = blockTextureSouth;
        }
        if(dir == EnumFacing.DOWN){
        	textureUp = blockTextureEast;
        	textureDown = blockTextureWest;
        	textureNorth = blockTextureUp;
        	textureSouth = blockTextureDown;
        	textureWest = blockTextureNorth;
        	textureEast = blockTextureSouth;
        }
        if(dir == EnumFacing.NORTH){
        	textureUp = blockTextureDown;
        	textureDown = blockTextureUp;
        	textureNorth = blockTextureSouth;
        	textureSouth = blockTextureNorth;
        }
        if(dir == EnumFacing.WEST){
        	textureNorth = blockTextureEast;
        	textureSouth = blockTextureWest;
        	textureWest = blockTextureNorth;
        	textureEast = blockTextureSouth;
        }
        if(dir == EnumFacing.EAST){
        	textureNorth = blockTextureWest;
        	textureSouth = blockTextureEast;
        	textureWest = blockTextureSouth;
        	textureEast = blockTextureNorth;
        }
        int color =	Color.BLUE.getRGB();
        int color2 = Minecraft.getMinecraft().getBlockColors().colorMultiplier(bState, null, null, 0);
        
        Color test = new Color(color2);
        Color real = new Color(test.getBlue(), test.getGreen(), test.getRed());
        color = real.getRGB();
        if(color == -1){
        	 
        	float f = 1.0f;
            int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
            color = -16777216 | i << 16 | i << 8 | i;
        }
        
        faceCover.blockFaceUV.uvs = new float[]{0.0F, 0.0F, 16F, 1F};
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 16.0f, 7.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 8.0f), faceCover, textureUp, EnumFacing.UP, coverModelRot, (BlockPartRotation)null, true, color));
        
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 7.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 0.0f, 8.0f), faceCover, textureDown, EnumFacing.DOWN, coverModelRot, (BlockPartRotation)null, true, color));
        
        faceCover.blockFaceUV.uvs = new float[]{0.0F, 0.0F, 1F, 16F};
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 7.0f), new org.lwjgl.util.vector.Vector3f(0.0f, 16.0f, 8.0f), faceCover, textureWest, EnumFacing.WEST, coverModelRot, (BlockPartRotation)null, true, color));
    	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(16.0f, 0.0f, 7.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 8.0f), faceCover, textureEast, EnumFacing.EAST, coverModelRot, (BlockPartRotation)null, true, color));
        
    	faceCover.blockFaceUV.uvs = new float[]{0.0F, 0.0F, 16F, 16F};
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 7.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 8.0f), faceCover, textureNorth, EnumFacing.NORTH, coverModelRot, (BlockPartRotation)null, true, color));
    	list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 7.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 8.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, true, color));
    	
    	
    	
    	//list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, 7.0f), new org.lwjgl.util.vector.Vector3f(16.0f, 16.0f, 8.0f), faceCover, textureSouth, EnumFacing.SOUTH, coverModelRot, (BlockPartRotation)null, true, color));
    	
    }
    
    public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        //final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        //final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        
        //final ModelRotation modelRot = ModelRotation.X0_Y0;
        //final boolean scale = true;
        
        //float offset1 = 4f;
        //float offset = 4f;
        /*list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4.0f, 4f, 4.0f), new org.lwjgl.util.vector.Vector3f(12.0f, 4.0f, 12.0f), face, ModelPipe.glassPlusTexture, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(5.0f, 4f, 4f), new org.lwjgl.util.vector.Vector3f(11.0f, 4.0f, 12f), face, ModelPipe.glassSquareTexture, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4f, 4.0f, 5.0f), new org.lwjgl.util.vector.Vector3f(12f, 4.0f, 11.0f), face, ModelPipe.glassSquareTexture, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4.0f, 12f, 4.0f), new org.lwjgl.util.vector.Vector3f(12.0f, 12.0f, 12.0f), face, ModelPipe.glassPlusTexture, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(5.0f, 12.0f, (float)(4)), new org.lwjgl.util.vector.Vector3f(11.0f, 12.0f, (float)(12)), face, ModelPipe.glassSquareTexture, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f((float)(4), 12.0f, 5.0f), new org.lwjgl.util.vector.Vector3f((float)(12), 12.0f, 11.0f), face, ModelPipe.glassSquareTexture, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4.0f, 4.0f, 4f), new org.lwjgl.util.vector.Vector3f(12.0f, 12.0f, 4.0f), face, ModelPipe.glassPlusTexture, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f((float)(4), 5.0f, 4.0f), new org.lwjgl.util.vector.Vector3f((float)(12), 11.0f, 4.0f), face, ModelPipe.glassSquareTexture, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(5.0f, (float)(4), 4.0f), new org.lwjgl.util.vector.Vector3f(11.0f, (float)(12), 4.0f), face, ModelPipe.glassSquareTexture, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4.0f, 4.0f, 12.0f), new org.lwjgl.util.vector.Vector3f(12.0f, 12.0f, 12.0f), face, ModelPipe.glassPlusTexture, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f((float)(4), 5.0f, 12.0f), new org.lwjgl.util.vector.Vector3f((float)(12), 11.0f, 12.0f), face, ModelPipe.glassSquareTexture, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(5.0f, (float)(4), 12.0f), new org.lwjgl.util.vector.Vector3f(11.0f, (float)(12), 12.0f), face, ModelPipe.glassSquareTexture, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4f, 4.0f, 4.0f), new org.lwjgl.util.vector.Vector3f(4.0f, 12.0f, 12.0f), face, ModelPipe.glassPlusTexture, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4.0f, 5.0f, (float)(4)), new org.lwjgl.util.vector.Vector3f(4.0f, 11.0f, (float)(12)), face, ModelPipe.glassSquareTexture, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(4.0f, (float)(4), 5.0f), new org.lwjgl.util.vector.Vector3f(4.0f, (float)(12), 11.0f), face, ModelPipe.glassSquareTexture, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(12.0f, 4.0f, 4.0f), new org.lwjgl.util.vector.Vector3f(12.0f, 12.0f, 12.0f), face, ModelPipe.glassPlusTexture, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(12.0f, 5.0f, (float)(4)), new org.lwjgl.util.vector.Vector3f(12.0f, 11.0f, (float)(12)), face, ModelPipe.glassSquareTexture, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        list.add(faceBakery.makeBakedQuad(new org.lwjgl.util.vector.Vector3f(12.0f, (float)(4), 5.0f), new org.lwjgl.util.vector.Vector3f(12.0f, (float)(12), 11.0f), face, ModelPipe.glassSquareTexture, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation)null, scale, true));
        */
        if(this.data !=null)
        addCover(data, EnumFacing.SOUTH, list);
        
        return list;
    }
    
    public boolean isGui3d() {
        return true;
    }
    
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }
    
    static {
        faceBakery = new FaceBakery();
    }

    public static final Map<CoverData, ModelCover> map = Maps.newHashMap();
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return RenderUtil.getTexture(Blocks.GLASS.getDefaultState());
	}
	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return null;
	}
	@Override
	public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		CoverData data = ItemPipeCover.getCoverData(stack);
        if (data == null) {
        	return this;
        }
        if (!map.containsKey(data)) {
            map.put(data, new ModelCover(data));
        }
        return map.get(data);
	}
	
}

