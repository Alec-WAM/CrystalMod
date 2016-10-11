package alec_wam.CrystalMod.tiles.machine.power.engine;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.*;

import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.client.model.dynamic.DelegatingDynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ModelEngine extends DelegatingDynamicItemAndBlockModel 
{
	public static final ModelEngine INSTANCE = new ModelEngine();
    static FaceBakery faceBakery;
    int type = -1;
    
    public ModelEngine() {
    	state = null;
    }
    
    private final FakeEngineState state;
    
    
    public ModelEngine(int type) {
    	state = null;
    	this.type = type;
    }
    
    public ModelEngine(final FakeEngineState state) {
    	this.state = state;
    }
    
    public List<BakedQuad> getFaceQuads(final EnumFacing p_177551_1_) {
        return new ArrayList<BakedQuad>();
    }
    
    public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        BlockFaceUV uvSide = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        
        ModelRotation modelRot = ModelRotation.X0_Y0;
        
        EnumFacing dir = type >=0 ? EnumFacing.EAST : (state !=null && state.engine !=null) ? EnumFacing.NORTH : EnumFacing.UP;
        if(dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH){
        	modelRot = ModelRotation.X0_Y90;
        }
        BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        BlockPartFace faceSide = new BlockPartFace((EnumFacing)null, 0, "", uvSide);
        final boolean scale = true;
        IBlockState fState = Blocks.FURNACE.getDefaultState();
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(fState);
        TextureAtlasSprite funraceUp = CoverUtil.findTexture(Block.getStateId(fState), model, EnumFacing.UP);
        TextureAtlasSprite funraceDown = funraceUp;
        TextureAtlasSprite funraceSide = funraceUp;

        //float height = 8f;
        
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, 14f, 2.0f), new Vector3f(15.0f, 14, 14.0f), face, funraceUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, 6f, 2.0f), new Vector3f(15.0f, 6f, 14.0f), face, funraceDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, 6f, 2.0f), new Vector3f(15.0f, 14, 2.0f), faceSide, funraceSide, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, 6f, 2.0f), new Vector3f(15.0f, 14, 14.0f), faceSide, funraceSide, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, 6f, 2.0f), new Vector3f(1.0f, 14, 14.0f), faceSide, funraceSide, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, 6f, 2.0f), new Vector3f(15.0f, 14, 14.0f), faceSide, funraceSide, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        IBlockState iState = Blocks.IRON_BLOCK.getDefaultState();
        IBakedModel model2 = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(iState);
        TextureAtlasSprite metalUp = CoverUtil.findTexture(Block.getStateId(iState), model2, EnumFacing.UP);
        TextureAtlasSprite metalDown = metalUp;
        TextureAtlasSprite metalSide = metalUp;
        
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(2.0f, 6f, 3.0f), new Vector3f(14.0f, 6, 13.0f), face, metalUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(2.0f, 1f, 3.0f), new Vector3f(14.0f, 1f, 13.0f), face, metalDown, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(2.0f, 1f, 3.0f), new Vector3f(14.0f, 6, 3.0f), faceSide, metalSide, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(2.0f, 1f, 3.0f), new Vector3f(14.0f, 6, 13.0f), faceSide, metalSide, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(2.0f, 1f, 3.0f), new Vector3f(2.0f, 6, 13.0f), faceSide, metalSide, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(14.0f, 1f, 3.0f), new Vector3f(14.0f, 6, 13.0f), faceSide, metalSide, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        int meta = type >=0 ? type : (state !=null && state.engine !=null) ? state.engine.getBlockMetadata() : 0;
        int blockMeta = meta == 0 ? 0 : meta == 1 ? 3 : 4;
        IBlockState cState = ModBlocks.crystalIngot.getStateFromMeta(blockMeta);
        IBakedModel modelHead = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(cState);
        TextureAtlasSprite headUp = CoverUtil.findTexture(Block.getStateId(cState), modelHead, EnumFacing.UP);
        
        
        //BOTTOM BARS
        float barMin = 0f;
        float barMax = 1f;
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMax, 2.0f), new Vector3f(16.0f, barMax, 14.0f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(16.0f, barMin, 14.0f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(16.0f, barMax, 2.0f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(16.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(15.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(16.0f, barMin, 2.0f), new Vector3f(16.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMax, 2.0f), new Vector3f(1.0f, barMax, 14.0f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(1.0f, barMin, 14.0f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(1.0f, barMax, 2.0f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(1.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(0.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(11.0f, barMin, 2.0f), new Vector3f(1.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMax, 14f), new Vector3f(15f, barMax, 15f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMin, 15.0f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMax, 14f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMax, 15.0f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMax, 15.0f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 14f), new Vector3f(15.0f, barMax, 15.0f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMax, 1f), new Vector3f(15f, barMax, 2f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMin, 2f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMax, 1f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMax, 2f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMax, 2f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 1f), new Vector3f(15.0f, barMax, 2f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        
        //TOP BARS
        barMin = 12f;
        barMax = 13f;
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMax, 2.0f), new Vector3f(16.0f, barMax, 14.0f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(16.0f, barMin, 14.0f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(16.0f, barMax, 2.0f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(16.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 2.0f), new Vector3f(15.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(16.0f, barMin, 2.0f), new Vector3f(16.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMax, 2.0f), new Vector3f(1.0f, barMax, 14.0f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(1.0f, barMin, 14.0f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(1.0f, barMax, 2.0f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(1.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(0.0f, barMin, 2.0f), new Vector3f(0.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(11.0f, barMin, 2.0f), new Vector3f(1.0f, barMax, 14.0f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMax, 14f), new Vector3f(15f, barMax, 15f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMin, 15.0f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMax, 14f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMax, 15.0f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMin, 14f), new Vector3f(15.0f, barMax, 15.0f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 14f), new Vector3f(15.0f, barMax, 15.0f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1f, barMax, 1f), new Vector3f(15f, barMax, 2f), face, headUp, EnumFacing.UP, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMin, 2f), face, headUp, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMax, 1f), faceSide, headUp, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMax, 2f), faceSide, headUp, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(1.0f, barMin, 1f), new Vector3f(15.0f, barMax, 2f), faceSide, headUp, EnumFacing.WEST, modelRot, (BlockPartRotation)null, scale, true));
        list.add(ModelEngine.faceBakery.makeBakedQuad(new Vector3f(15.0f, barMin, 1f), new Vector3f(15.0f, barMax, 2f), faceSide, headUp, EnumFacing.EAST, modelRot, (BlockPartRotation)null, scale, true));

        
        //TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureManager();
        
        return list;
    }
    
    public boolean isAmbientOcclusion() {
        return false;
    }
    
    public boolean isGui3d() {
        return true;
    }
    
    public boolean isBuiltInRenderer() {
        return false;
    }
    
    public TextureAtlasSprite getParticleTexture() {
        return RenderUtil.getTexture(ModBlocks.crystalIngot.getStateFromMeta(CrystalIngotBlockType.DARKIRON.getMeta()));
    }
    
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    static HashMap<Integer, ModelEngine> models;
    
    static {
        ModelEngine.faceBakery = new FaceBakery();
        ModelEngine.models = new HashMap<Integer, ModelEngine>();
    }

	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return (state instanceof FakeEngineState) ? new ModelEngine((FakeEngineState)state) : null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		if(models.containsKey(stack.getMetadata())){
    		return models.get(stack.getMetadata());
    	}
    	int type = stack.getMetadata();
    	ModelEngine model = new ModelEngine(type);
    	models.put(type, model);
		return model;
	}

	
}

