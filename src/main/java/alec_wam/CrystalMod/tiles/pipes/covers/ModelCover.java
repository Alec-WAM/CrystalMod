package alec_wam.CrystalMod.tiles.pipes.covers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.client.util.ColorData;
import alec_wam.CrystalMod.client.util.ColorDataARGB;
import alec_wam.CrystalMod.client.util.CustomBakedQuad;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

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
    	final List<BakedQuad> bakedQuads = new LinkedList<BakedQuad>();
    	ItemStack stack = new ItemStack(data.getBlockState().getBlock(), 1, data.getBlockState().getBlock().getMetaFromState(data.getBlockState()));
    	IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, (World)null, (EntityLivingBase)null);
    	bakedQuads.addAll(model.getQuads((IBlockState)null, (EnumFacing)null, 0L));
    	for (final EnumFacing face2 : EnumFacing.VALUES) {
    		bakedQuads.addAll(model.getQuads((IBlockState)null, face2, 0L));
    	}
    	List<CustomBakedQuad> quads = CustomBakedQuad.fromArray(bakedQuads);
    	quads = CoverRender.sliceQuads(quads, 3, CoverUtil.getCoverBoundingBox(EnumFacing.SOUTH, true));
    	for(CustomBakedQuad quad : quads){
    		for(int i = 0; i < 4; i++){
    			quad.vertices[i].vec.z -=0.45d;
    		}
    		int color = -1;
    		if (quad.hasTint()) {
    			color = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, quad.tintIndex);
    			if (EntityRenderer.anaglyphEnable) {
    				color = TextureUtil.anaglyphColor(color);
    			}
    			color |= 0xFF000000;
    		}
    		final CustomBakedQuad copyQuad = quad.copy();
    		final ColorData c = new ColorDataARGB(color);
    		for (final ColorData qC : copyQuad.colours) {
    			qC.multiply(c);
    		}
    		list.add(copyQuad.bake(DefaultVertexFormats.ITEM));
    	}
    }
    
    @Override
	public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        if(this.data !=null){
        	addCover(data, EnumFacing.SOUTH, list);
        }
        return list;
    }
    
    @Override
	public boolean isGui3d() {
        return true;
    }
    
    @Override
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

