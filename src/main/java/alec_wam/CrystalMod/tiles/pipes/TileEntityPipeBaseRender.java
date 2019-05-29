package alec_wam.CrystalMod.tiles.pipes;


import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityPipeBaseRender extends TileEntityRenderer<TileEntityPipeBase> {

	private static final ResourceLocation TEXTURE_IO_IN = CrystalMod.resourceL("textures/block/pipe/io_in.png");
	private static final ResourceLocation TEXTURE_IO_OUT = CrystalMod.resourceL("textures/block/pipe/io_out.png");
	private static final ResourceLocation TEXTURE_IO_BOTH = CrystalMod.resourceL("textures/block/pipe/io_both.png");


	@Override
    public void render(TileEntityPipeBase te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	GlStateManager.translated(x, y, z);
    	GlStateManager.disableLighting();
        
    	for(EnumFacing face : EnumFacing.values()){
    		GlStateManager.pushMatrix();
	    	if(face == EnumFacing.DOWN){
	    		GlStateManager.translated(0.5, 0.5, 0.5);
	    		GlStateManager.rotatef(180, 1, 0, 0);
	    		GlStateManager.translated(-0.5, -0.5, -0.5);
	    	} 
	    	if(face == EnumFacing.NORTH){
	    		GlStateManager.translated(0.5, 0.5, 0.5);
	    		GlStateManager.rotatef(90, -1, 0, 0);
	    		GlStateManager.translated(-0.5, -0.5, -0.5);
	    	}
	    	if(face == EnumFacing.SOUTH){
	    		GlStateManager.translated(0.5, 0.5, 0.5);
	    		GlStateManager.rotatef(90, 1, 0, 0);
	    		GlStateManager.translated(-0.5, -0.5, -0.5);
	    	}
	    	if(face == EnumFacing.WEST){
	    		GlStateManager.translated(0.5, 0.5, 0.5);
	    		GlStateManager.rotatef(90, 0, 0, 1);
	    		GlStateManager.translated(-0.5, -0.5, -0.5);
	    	}
	    	if(face == EnumFacing.EAST){
	    		GlStateManager.translated(0.5, 0.5, 0.5);
	    		GlStateManager.rotatef(90, 0, 0, -1);
	    		GlStateManager.translated(-0.5, -0.5, -0.5);
	    	} 
	    	if(te.hasExternalConnection(face)){
	    		renderIOArrows(te, face);
	    	}
	        GlStateManager.popMatrix();
    	}
        
        GlStateManager.enableLighting();
        
        GlStateManager.popMatrix();
    }
	
	public void renderIOArrows(TileEntityPipeBase pipe, EnumFacing face){
		float minExt = 5.6F / 16.0F;
        float maxExt = 10.4F / 16.0F;
        ResourceLocation texture = TEXTURE_IO_IN;
        PipeConnectionMode connectionMode = pipe.getConnectionSetting(face);
        if(connectionMode == PipeConnectionMode.OUT){
        	texture = TEXTURE_IO_OUT;
        }
        if(connectionMode == PipeConnectionMode.BOTH){
        	texture = TEXTURE_IO_BOTH;
        }
        
        this.bindTexture(texture);
        float minZ = 4.0F / 16.0F;
        float maxZ = 12.0F / 16.0F;
        float minY = 14.0F / 16.0f;
        float maxY = 15.0F / 16.0f;
        Tessellator tessellator = Tessellator.getInstance();
    	BufferBuilder worldrenderer = tessellator.getBuffer();
    	
    	worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        //North
        worldrenderer.pos(maxExt, minY, maxZ + 0.001f).tex(0, 0).endVertex();
        worldrenderer.pos(maxExt, maxY, maxZ + 0.001f).tex(0, 1).endVertex();
        worldrenderer.pos(minExt, maxY, maxZ + 0.001f).tex(1, 1).endVertex();
        worldrenderer.pos(minExt, minY, maxZ + 0.001f).tex(1, 0).endVertex();
       
        //South
        worldrenderer.pos(minExt, minY, minZ - 0.001f).tex(0, 0).endVertex();
        worldrenderer.pos(minExt, maxY, minZ - 0.001f).tex(0, 1).endVertex();
        worldrenderer.pos(maxExt, maxY, minZ - 0.001f).tex(1, 1).endVertex();
        worldrenderer.pos(maxExt, minY, minZ - 0.001f).tex(1, 0).endVertex();
        
        //East
        worldrenderer.pos(maxZ + 0.001f, minY, minExt).tex(0, 0).endVertex();
        worldrenderer.pos(maxZ + 0.001f, maxY, minExt).tex(0, 1).endVertex();
        worldrenderer.pos(maxZ + 0.001f, maxY, maxExt).tex(1, 1).endVertex();
        worldrenderer.pos(maxZ + 0.001f, minY, maxExt).tex(1, 0).endVertex();
        
        //West
        worldrenderer.pos(minZ - 0.001f, minY, maxExt).tex(0, 0).endVertex();
        worldrenderer.pos(minZ - 0.001f, maxY, maxExt).tex(0, 1).endVertex();
        worldrenderer.pos(minZ - 0.001f, maxY, minExt).tex(1, 1).endVertex();
        worldrenderer.pos(minZ - 0.001f, minY, minExt).tex(1, 0).endVertex();
        tessellator.draw();
	}
	
}
