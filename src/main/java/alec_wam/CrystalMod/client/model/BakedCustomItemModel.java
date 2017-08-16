package alec_wam.CrystalMod.client.model;

import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
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
import net.minecraftforge.common.model.TRSRTransformation;

public class BakedCustomItemModel extends DynamicItemAndBlockModel
{
	private IBakedModel baseModel;
	private ICustomItemRenderer render;
	public BakedCustomItemModel(IBakedModel model, ICustomItemRenderer render)
	{
		super(false, true);
		this.render = render;
		this.baseModel = model;
	}
	
	public BakedCustomItemModel(IBakedModel model, ICustomItemRenderer render, ItemStack s)
	{
		super(false, true);
		baseModel = model;
		this.render = render;
	}
	
	@Override
	public List<BakedQuad> getGeneralQuads()
	{
        
		/*Tessellator tessellator = Tessellator.getInstance();
		VertexFormat prevFormat = null;
		int prevMode = -1;
		
		double x = 0;
		double y = 0;
		double z = 0;
		boolean validBufferCord = true;
		
		try{
			x = ObfuscationReflectionHelper.getPrivateValue(VertexBuffer.class, tessellator.getBuffer(), 10);
			y = ObfuscationReflectionHelper.getPrivateValue(VertexBuffer.class, tessellator.getBuffer(), 11);
			z = ObfuscationReflectionHelper.getPrivateValue(VertexBuffer.class, tessellator.getBuffer(), 12);
		}catch(Exception e){
			validBufferCord = false;
		}
		
		if(RenderUtil.isDrawing(tessellator))
		{
			prevFormat = tessellator.getBuffer().getVertexFormat();
			prevMode = tessellator.getBuffer().getDrawMode();
			tessellator.draw();
		}
		
		List<BakedQuad> generalQuads = new LinkedList<BakedQuad>();
		if(prevFormat !=null && prevTransform == null)ModLogger.info(prevFormat.toString());
		GlStateManager.pushMatrix();
		if(prevTransform == null){
			
			GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
		}else{
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
		}
		GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
		doRender(prevTransform);
		GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634); 
        GlStateManager.enableCull();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	GlStateManager.popMatrix();
    	
    	if(prevFormat != null)
    	{
    		net.minecraft.client.renderer.VertexBuffer worldrenderer = tessellator.getBuffer();
	    	worldrenderer.begin(prevMode, prevFormat);
    	}
		*/
		return Lists.newArrayList();
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return baseModel == null ? true : baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return baseModel == null ? true : baseModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return baseModel == null ? RenderUtil.getTexture(Blocks.STONE.getDefaultState()) : baseModel.getParticleTexture();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return baseModel == null ? ItemCameraTransforms.DEFAULT : baseModel.getItemCameraTransforms();
	}
    
	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) 
    {    	
		if(render == null)return super.handlePerspective(cameraTransformType);
        TRSRTransformation tr = render.getTransform(cameraTransformType);
        Matrix4f mat = null;
        if(tr != null && !tr.equals(TRSRTransformation.identity())) mat = TRSRTransformation.blockCornerToCenter(tr).getMatrix();
        return Pair.of(this, mat);
    }

	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return null;
	}

	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		return new BakedCustomItemModel(baseModel, render, stack);
	}
}