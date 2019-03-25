package alec_wam.CrystalMod.tiles.pipes;


import java.awt.Color;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.items.ItemIngot;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverCutter;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverRender;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileEntityPipeRenderer extends TileEntitySpecialRenderer<TileEntityPipe> {

	@Override
    public void renderTileEntityAt(TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage) {
		//destroyStage = 8;
		if (destroyStage >= 0)
        {
			if(te.diggingCoverSide !=null){
	            this.bindTexture(DESTROY_STAGES[destroyStage]);
	            GlStateManager.matrixMode(5890);
	            GlStateManager.pushMatrix();
	            GlStateManager.matrixMode(5888);
	            
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            GlStateManager.enableBlend();
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
	            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
	            GlStateManager.enablePolygonOffset();
	            GlStateManager.alphaFunc(516, 0.1F);
	            GlStateManager.enableAlpha();
	            
	            GlStateManager.pushMatrix();
	            GlStateManager.translate(x, y, z);	
	            float angleX = 0.0f;
	            float angleY = 0.0f;
	            if(te.diggingCoverSide == EnumFacing.UP){
	            	angleX = 90.0F;
	            }
	            if(te.diggingCoverSide == EnumFacing.DOWN){
	            	angleX = -90.0F;
	            }
	            if(te.diggingCoverSide == EnumFacing.EAST){
	            	angleY = -90.0F;
	            }
	            if(te.diggingCoverSide == EnumFacing.WEST){
	            	angleY = 90.0F;
	            }
	            if(te.diggingCoverSide == EnumFacing.SOUTH){
	            	angleY = 180.0F;
	            }   
	            GlStateManager.translate(0.5, 0.5, 0.5);
	            GlStateManager.rotate(angleX, 1, 0, 0);
	            GlStateManager.rotate(angleY, 0, 1, 0);
	        	GlStateManager.translate(-0.5, -0.5, -0.5);
	            
	    		Tessellator tessellator = Tessellator.getInstance();
	        	VertexBuffer worldrenderer = tessellator.getBuffer();	        	
	        	worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	        	float alpha = 0.3F;
	        	worldrenderer.pos(0, 0, 0).tex(0, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		        worldrenderer.pos(0, 1, 0).tex(0, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		        worldrenderer.pos(1, 1, 0).tex(1, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		        worldrenderer.pos(1, 0, 0).tex(1, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		        tessellator.draw();
		        
		        GlStateManager.disableAlpha();
		        GlStateManager.doPolygonOffset(0.0F, 0.0F);
		        GlStateManager.disablePolygonOffset();
		        GlStateManager.enableAlpha();
		        GlStateManager.depthMask(true);
	            GlStateManager.popMatrix();
	            
	            
	            GlStateManager.matrixMode(5890);
	            GlStateManager.popMatrix();
	            GlStateManager.matrixMode(5888);
			}
        }
        else
        {
        	Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
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
        }
    }
}
