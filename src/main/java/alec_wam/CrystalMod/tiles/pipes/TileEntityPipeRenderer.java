package alec_wam.CrystalMod.tiles.pipes;


import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.tiles.pipes.covers.CoverCutter;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverRender;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileEntityPipeRenderer extends TileEntitySpecialRenderer<TileEntityPipe> {

	@Override
    public void renderTileEntityAt(TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage) {
		/*if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {*/
        	Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        //}
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();		
		BlockPos pos = te.getPos();
    	
		
		
    	if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
    	
    	for(EnumFacing side : EnumFacing.VALUES){
    		if(te.getCoverData(side) !=null){
    			CoverData data = te.getCoverData(side);
    			IBlockAccess world = te.getWorld();
    			IBlockState state = data.getBlockState();
    			Tessellator tess = Tessellator.getInstance();
    			VertexBuffer buffer = tess.getBuffer();    			
    			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    			AxisAlignedBB bounds = CoverUtil.getCoverBoundingBox(side, false);
    			buffer.setTranslation(x-pos.getX(), y-pos.getY(), z-pos.getZ());
    			CoverCutter.ITransformer[] cutType = null;
    			if(te.containsExternalConnection(side))cutType = CoverCutter.hollowPipeTile;
    			if(te.containsPipeConnection(side))cutType = CoverCutter.hollowPipeLarge;
    			CoverRender.renderBakedCoverQuads(buffer, world, pos, state, side.getIndex(), bounds, cutType);
    			buffer.setTranslation(0, 0, 0);
    			tess.draw();    			
    		}
    	}
    	
    	GlStateManager.disableBlend();
		GlStateManager.enableCull();
        GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
		
		/*if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }*/
    }
}
