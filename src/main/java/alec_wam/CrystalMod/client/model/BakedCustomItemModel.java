package alec_wam.CrystalMod.client.model;

import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;
import alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class BakedCustomItemModel extends DynamicItemAndBlockModel
{
	private IBakedModel baseModel;
	private ICustomItemRenderer render;
	private ItemStack stack;
	
	private TransformType prevTransform;
	
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
		stack = s;
	}
    
	private void doRender(TransformType type)
	{
		if(type == null)return;
		if(stack != null)
		{
			if(render !=null){
				render.render(stack, type);
			}
		}
	}
	
	@Override
	public List<BakedQuad> getGeneralQuads()
	{
        
		Tessellator tessellator = Tessellator.getInstance();
		VertexFormat prevFormat = null;
		int prevMode = -1;
		
		if(RenderUtil.isDrawing(tessellator))
		{
			prevFormat = tessellator.getBuffer().getVertexFormat();
			prevMode = tessellator.getBuffer().getDrawMode();
			tessellator.draw();
		}
		
		List<BakedQuad> generalQuads = new LinkedList<BakedQuad>();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
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
		
		return generalQuads;
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
		return baseModel == null ? false : baseModel.isBuiltInRenderer();
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
        prevTransform = cameraTransformType;
    	
        return super.handlePerspective(cameraTransformType);
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