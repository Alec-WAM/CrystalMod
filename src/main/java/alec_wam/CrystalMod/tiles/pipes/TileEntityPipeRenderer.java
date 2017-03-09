package alec_wam.CrystalMod.tiles.pipes;


import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.MinecraftForgeClient;

public class TileEntityPipeRenderer extends TileEntitySpecialRenderer<TileEntityPipe> {

	@Override
    public void renderTileEntityAt(TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	/*GlStateManager.translate(x, y, z);
    	int pass = MinecraftForgeClient.getRenderPass();
    	Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	for(EnumFacing faceing : EnumFacing.VALUES){
    		if(te.getCoverData(faceing) !=null){
    			CoverData data = te.getCoverData(faceing);
    			AxisAlignedBB bounds = new AxisAlignedBB(0, 0, 0, 1, 1, 1);//FacadeBuilder.getFacadeBox(faceing, true);
    	    	boolean worked = FacadeBuilder.renderCover(new PipeBlockAccessWrapper(te.getWorld(), te.getPos(), faceing), te.getPos(), faceing.getIndex(), data.getBlockState(), bounds, false, false);
    	    	//ModLogger.info("Worked: "+worked);
    		}
    	}*/
    	//boolean worked = FacadeBuilder.renderCover(getWorld(), te.getPos(), 0, Blocks.COBBLESTONE.getDefaultState(), FacadeBuilder.getFacadeBox(EnumFacing.UP, true), false, false);
    	
    	//ModLogger.info("Worked: "+worked);
    	
    	//GlStateManager.scale(1, -1, -1);
    	//GlStateManager.translate(-1, 0, -1);
    	//Tessellator tessellator = Tessellator.getInstance();
    	//WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.popMatrix();
    }
}
