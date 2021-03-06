package alec_wam.CrystalMod.tiles.pipes.attachments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.tiles.pipes.ModelPipeBaked;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ModelAttachment extends DynamicItemAndBlockModel
{
	public static final ModelAttachment INSTANCE = new ModelAttachment();
	
	private ItemStack stack;
	
	public ModelAttachment() {
    	this(ItemStackTools.getEmptyStack());
    }
    public ModelAttachment(ItemStack stack) {
    	super(false, true);
    	this.stack = stack;
    }
    
    public List<BakedQuad> getFaceQuads(final EnumFacing p_177551_1_) {
        return new ArrayList<BakedQuad>();
    }
    
    private Map<String, AttachmentData> cachedAttchments = Maps.newHashMap();
    
    @Override
	public List<BakedQuad> getGeneralQuads() {
        final List<BakedQuad> list = new ArrayList<BakedQuad>();
        if(!ItemStackTools.isNullStack(stack)){
        	if(ItemNBTHelper.verifyExistance(stack, "ID")){
        		String id = ItemNBTHelper.getString(stack, "ID", "");
        		AttachmentData data = null;
        		if(!cachedAttchments.containsKey(id)){
        			cachedAttchments.put(id, AttachmentUtil.getFromID(id));
        		}
        		data = cachedAttchments.get(id);
        		if(data !=null){
        			data.addQuads(ModelPipeBaked.faceBakery, list, EnumFacing.EAST);
        		}
        	}
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

    public static final Map<String, ModelAttachment> map = Maps.newHashMap();
	
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
		if (!ItemNBTHelper.verifyExistance(stack, "ID")) {
        	return this;
        }
		String id = ItemNBTHelper.getString(stack, "ID", "");
        if (!map.containsKey(id)) {
            map.put(id, new ModelAttachment(stack));
        }
        return map.get(id);
	}
	
}

