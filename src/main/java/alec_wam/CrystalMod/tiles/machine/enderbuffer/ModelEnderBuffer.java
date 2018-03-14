package alec_wam.CrystalMod.tiles.machine.enderbuffer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DelegatingDynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.FakeTileState;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.client.CustomModelUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ModelEnderBuffer extends DelegatingDynamicItemAndBlockModel 
{
	public static final ModelEnderBuffer INSTANCE = new ModelEnderBuffer();
    static FaceBakery faceBakery;
    
    public ModelEnderBuffer() {
    	super();
    	state = null;
    }
    
    public FakeTileState<TileEntityEnderBuffer> state;
    public ItemStack stack;
    
    public ModelEnderBuffer(FakeTileState<TileEntityEnderBuffer> state, EnumFacing facing, long rand) {
        super(state, facing, rand);
        this.stack = null;
        this.state = state;
    }

    public ModelEnderBuffer(ItemStack itemStack, World world, EntityLivingBase entity) {
        super(itemStack, world, entity);
        stack = itemStack;
        state = null;
    }
    
    @Override
	public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        TextureAtlasSprite spriteIdle = RenderUtil.getSprite(CrystalMod.resource("blocks/machine/enderbuffer/enderbuffer"));
        TextureAtlasSprite spriteActive = RenderUtil.getSprite(CrystalMod.resource("blocks/machine/enderbuffer/enderbuffer_active"));

        TileEntityEnderBuffer buffer = state !=null && state.tile !=null && state.tile instanceof TileEntityEnderBuffer ? state.tile : null;
        
        boolean isActive = (buffer !=null) ? buffer.isActive() : false;
        
        TextureAtlasSprite sprite = isActive ? spriteActive : spriteIdle;
        BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        float max = 16f;
        
        int code = 0;
        if(buffer !=null){
        	code = buffer.code;
        } else if(stack !=null && ItemNBTHelper.verifyExistance(stack, BlockMachine.TILE_NBT_STACK)){
        	NBTTagCompound tileNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(BlockMachine.TILE_NBT_STACK);
        	if(tileNBT.hasKey("Code")){
        		code = tileNBT.getInteger("Code");
        	}
        }
        
        int color1 = code & 15;
        int color2 = (code >> 4) & 15;
        int color3 = (code >> 8) & 15;
        @SuppressWarnings("deprecation")
		IBlockState state1 = Blocks.WOOL.getStateFromMeta(color1);
        @SuppressWarnings("deprecation")
        IBlockState state2 = Blocks.WOOL.getStateFromMeta(color2);
        @SuppressWarnings("deprecation")
        IBlockState state3 = Blocks.WOOL.getStateFromMeta(color3);
        
        TextureAtlasSprite cSprite1 = RenderUtil.getTexture(state1);
        TextureAtlasSprite cSprite2 = RenderUtil.getTexture(state2);
        TextureAtlasSprite cSprite3 = RenderUtil.getTexture(state3);

        float cMaxH = 12.3f;
        float cMinH = 9.7f;
        float cMin = -0.3f;
        float cMax = 16.3f;
        
        BlockFaceUV uvBand = new BlockFaceUV(new float[] { 0.0f, cMinH, 16.0f, cMaxH }, 0);
        BlockPartFace faceBand = new BlockPartFace((EnumFacing)null, 0, "", uvBand);
        
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMinH, cMax), face, cSprite1, EnumFacing.DOWN, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMaxH, cMin), new Vector3f(cMax, cMaxH, cMax), face, cSprite1, EnumFacing.UP, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMin), faceBand, cSprite1, EnumFacing.NORTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMin, cMaxH, cMax), faceBand, cSprite1, EnumFacing.WEST, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMax), faceBand, cSprite1, EnumFacing.SOUTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMax), faceBand, cSprite1, EnumFacing.EAST, ModelRotation.X0_Y0, null, true));
        
        cMaxH = 9.3f;
        cMinH = 6.7f;
        faceBand.blockFaceUV.uvs = new float[] { 0.0f, cMinH, 16.0f, cMaxH };
        
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMinH, cMax), face, cSprite2, EnumFacing.DOWN, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMaxH, cMin), new Vector3f(cMax, cMaxH, cMax), face, cSprite2, EnumFacing.UP, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMin), faceBand, cSprite2, EnumFacing.NORTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMin, cMaxH, cMax), faceBand, cSprite2, EnumFacing.WEST, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMax), faceBand, cSprite2, EnumFacing.SOUTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMax), faceBand, cSprite2, EnumFacing.EAST, ModelRotation.X0_Y0, null, true));
        
        cMaxH = 6.3f;
        cMinH = 3.7f;
        faceBand.blockFaceUV.uvs = new float[] { 0.0f, cMinH, 16.0f, cMaxH };
        
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMinH, cMax), face, cSprite3, EnumFacing.DOWN, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMaxH, cMin), new Vector3f(cMax, cMaxH, cMax), face, cSprite3, EnumFacing.UP, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMin), faceBand, cSprite3, EnumFacing.NORTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMin, cMaxH, cMax), faceBand, cSprite3, EnumFacing.WEST, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMax), faceBand, cSprite3, EnumFacing.SOUTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(cMin, cMinH, cMin), new Vector3f(cMax, cMaxH, cMax), faceBand, cSprite3, EnumFacing.EAST, ModelRotation.X0_Y0, null, true));
        
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(max, 0, max), face, sprite, EnumFacing.DOWN, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, max, 0), new Vector3f(max, max, max), face, sprite, EnumFacing.UP, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(max, max, 0), face, sprite, EnumFacing.NORTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(0, max, max), face, sprite, EnumFacing.WEST, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(max, max, max), face, sprite, EnumFacing.SOUTH, ModelRotation.X0_Y0, null, true));
        list.add(CustomModelUtil.INSTANCE.makeBakedQuad(new Vector3f(0, 0, 0), new Vector3f(max, max, max), face, sprite, EnumFacing.EAST, ModelRotation.X0_Y0, null, true));
        
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
        return RenderUtil.getSprite(CrystalMod.resource("blocks/machine/enderbuffer/enderbuffer"));
    }
    
    @Override
	public ItemCameraTransforms getItemCameraTransforms() {
        return super.getItemCameraTransforms();
    }
    
    static {
        ModelEnderBuffer.faceBakery = new FaceBakery();
    }

	@SuppressWarnings("unchecked")
	@Override
	public IBakedModel handleBlockState(IBlockState state, EnumFacing side,	long rand) {
		return (state instanceof FakeTileState<?>) ? new ModelEnderBuffer((FakeTileState<TileEntityEnderBuffer>)state, side, rand) : null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		return new ModelEnderBuffer(stack, world, entity);
	}

	
}

