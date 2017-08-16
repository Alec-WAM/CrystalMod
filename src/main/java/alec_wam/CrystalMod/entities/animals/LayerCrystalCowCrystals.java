package alec_wam.CrystalMod.entities.animals;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCrystalCowCrystals implements LayerRenderer<EntityCrystalCow>
{
    private final RenderCrystalCow mooshroomRenderer;

    public LayerCrystalCowCrystals(RenderCrystalCow mooshroomRendererIn)
    {
        this.mooshroomRenderer = mooshroomRendererIn;
    }

    @Override
	public void doRenderLayer(EntityCrystalCow entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isCrystalGrown())
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            this.mooshroomRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            
            IBlockState state = Blocks.RED_MUSHROOM.getDefaultState();
            
            try{
            	switch(entitylivingbaseIn.getColor()){
            		default : case 0 : state = ModBlocks.crystalPlantBlue.getDefaultState(); break;
            		case 1 : state = ModBlocks.crystalPlantRed.getDefaultState(); break;
            		case 2 : state = ModBlocks.crystalPlantGreen.getDefaultState(); break;
            		case 3 : state = ModBlocks.crystalPlantDark.getDefaultState(); break;
            	}
            }catch(Exception e){
            	e.printStackTrace();
            }
            GlStateManager.enableCull();
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, 1.0F);
            GlStateManager.translate(0.2F, 0.35F, 0.5F);
            GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.5F, 0.0F, /*Different to fit on back*/-0.8F);
            blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
            GlStateManager.popMatrix();
            
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.1F, 0.0F, -0.6F);
            GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.5F, 0.0F, -0.5F);
            blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            ((ModelQuadruped)this.mooshroomRenderer.getMainModel()).head.postRender(0.0625F);
            GlStateManager.scale(1.0F, -1.0F, 1.0F);
            GlStateManager.translate(0.0F, 0.7F, -0.2F);
            GlStateManager.rotate(12.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.5F, 0.0F, -0.5F);
            blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.disableCull();
        }
    }

    @Override
	public boolean shouldCombineTextures()
    {
        return true;
    }
}