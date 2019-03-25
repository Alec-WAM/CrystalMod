package alec_wam.CrystalMod.items.tools.blockholder;

import java.util.List;

import com.google.common.base.Function;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemRenderBlockHolder implements ICustomItemRenderer {

	public static final ItemRenderBlockHolder INSTANCE = new ItemRenderBlockHolder();
	
	@Override
	public void render(ItemStack stack) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5, -0.5, -0.5);
		
		if(lastTransform == TransformType.THIRD_PERSON_LEFT_HAND){
			GlStateManager.translate(0.5, 0.0, 0.5);
		}
		if(lastTransform == TransformType.THIRD_PERSON_RIGHT_HAND){
			GlStateManager.translate(-0.5, 0.3, 0.5);
		}
		
		GlStateManager.pushMatrix();
		IBakedModel bakedModel = getBakedModel();
		renderItemModel(stack, bakedModel, TransformType.NONE, -1, false);
		GlStateManager.popMatrix();
		
		renderSelectedBlock(stack);
		
		GlStateManager.popMatrix();
	}
	
	public void renderSelectedBlock(ItemStack stack){
		ItemStack blockStack = ItemBlockHolder.getBlockStack(stack);
		if(ItemStackTools.isValid(blockStack)){
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5, 0.5, 0.5);			
			if(lastTransform == TransformType.GUI){
				GlStateManager.rotate(90.0F, 0, 1, 0);
			}		
			if(lastTransform == TransformType.FIXED){
				GlStateManager.rotate(180.0F, 0, 1, 0);
			}
			if(lastTransform == TransformType.FIRST_PERSON_LEFT_HAND || lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND || lastTransform == TransformType.THIRD_PERSON_RIGHT_HAND){
				GlStateManager.rotate(270.0F, 0, 1, 0);
			}
			if(lastTransform == TransformType.THIRD_PERSON_LEFT_HAND){
				GlStateManager.rotate(90.0F, 0, 1, 0);
			}
			int count = ItemBlockHolder.getBlockCount(stack);
			Minecraft.getMinecraft().getRenderItem().renderItem(blockStack, TransformType.FIXED);
			if(count <= 0){	        	
				IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(blockStack, (World)null, (EntityLivingBase)null);
		        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.FIXED, false);
		        GlStateManager.translate(-0.5, -0.5, -0.5);	
				renderItemModel(stack, model, TransformType.NONE, 2147418112, false);				
			}
			GlStateManager.popMatrix();
		}
	}
	
	public void renderItemModel(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, int color, boolean leftHanded)
    {
        if (!stack.isEmpty())
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, transform, leftHanded);

            if(bakedmodel !=null){
    			Tessellator tessellator = Tessellator.getInstance();
    	        VertexBuffer vertexbuffer = tessellator.getBuffer();
    	        vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
    	
    	        for (EnumFacing enumfacing : EnumFacing.values())
    	        {
    	            this.renderQuads(vertexbuffer, bakedmodel.getQuads((IBlockState)null, enumfacing, 0L), color, stack);
    	        }
    	
    	        this.renderQuads(vertexbuffer, bakedmodel.getQuads((IBlockState)null, (EnumFacing)null, 0L), color, stack);
    	        tessellator.draw();
    		}
            
            
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        }
    }
	
	public void renderQuads(VertexBuffer renderer, List<BakedQuad> quads, int color, ItemStack stack)
    {
        int i = 0;

        for (int j = quads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = (BakedQuad)quads.get(i);
            int k = color;

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }

	
	public IBakedModel bakedModel;
	public IBakedModel getBakedModel(){
		if(bakedModel == null){		
			IModel model;
			try {
				model = ModelLoaderRegistry.getModelOrMissing(CrystalMod.resourceL("item/"+getModelName()));
				Function<ResourceLocation, TextureAtlasSprite> textureGetter;
		        textureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
		        {
		            @Override
					public TextureAtlasSprite apply(ResourceLocation location)
		            {
		                return RenderUtil.getSprite(location);
		            }
		        };
		
		        bakedModel = model.bake(new SimpleModelState(DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS), DefaultVertexFormats.BLOCK, textureGetter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bakedModel;
	}
	
	public String getModelName(){
		return "blockholder";
	}
	

	public TransformType lastTransform;	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
