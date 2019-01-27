package alec_wam.CrystalMod.tiles.machine.elevator;

import java.util.List;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;

public class TileEntityElevatorRenderer extends TileEntitySpecialRenderer<TileEntityElevator> {

	@Override
    public void renderTileEntityAt(TileEntityElevator te, double x, double y, double z, float partialTicks, int destroyStage) {
    	Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("crystalmod-elevator");
        if(te.isMoving()){
	        double movingY = te.getMovingY();
	        IBlockState movingState = te.getMovingState();
	        List<BlockPos> platformPos = te.getPositions();
	        
	    	AxisAlignedBB aabb = te.getAABBAboveElevator(0);
	    	boolean on = CrystalMod.proxy.getClientPlayer().getEntityBoundingBox().intersectsWith(aabb);
	    	double diff = on ? (te.getPos().getY() - (y+movingY) - 1) : 0;
	    	 
	        GlStateManager.pushMatrix();
	        RenderHelper.disableStandardItemLighting();
	        GlStateManager.blendFunc(770, 771);
	        GlStateManager.enableBlend();
	        GlStateManager.disableCull();
	        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	        if (Minecraft.isAmbientOcclusionEnabled()) {
	            GlStateManager.shadeModel(GL11.GL_SMOOTH);
	        } else {
	            GlStateManager.shadeModel(GL11.GL_FLAT);
	        }
	        
	        Tessellator tessellator = Tessellator.getInstance();
	        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
	        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
	        for (BlockPos pos : platformPos) {
	            int dx = te.getPos().getX();
	            double dy = y - te.getPos().getY() + (movingY - te.getPos().getY()) + diff;
	            int dz = te.getPos().getZ();
	        	tessellator.getBuffer().setTranslation(x - dx, dy, z - dz);
		        renderBlock(dispatcher, movingState, pos, te.getWorld(), tessellator.getBuffer());
	            
	        }
	        tessellator.getBuffer().setTranslation(0, 0, 0);
	        tessellator.draw();
	        
	
	        
	        GlStateManager.disableBlend();
	        GlStateManager.enableCull();
	        GlStateManager.popMatrix();
	        RenderHelper.enableStandardItemLighting();
        }
        profiler.endSection();
    }

	@Override
	public boolean isGlobalRenderer(TileEntityElevator te) {
    	return te.isMoving();
	}
	
    public static boolean renderBlock(BlockRendererDispatcher dispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, VertexBuffer worldRendererIn) {
    	 try {
             EnumBlockRenderType enumblockrendertype = state.getRenderType();

             if (enumblockrendertype == EnumBlockRenderType.INVISIBLE) {
                 return false;
             } else {
                 if (blockAccess.getWorldType() != WorldType.DEBUG_WORLD) {
                     try {
                         state = state.getActualState(blockAccess, pos);
                     } catch (Exception var8) {
                         ;
                     }
                 }

                 switch (enumblockrendertype) {
                     case MODEL:
                         IBakedModel model = dispatcher.getModelForState(state);
                         state = state.getBlock().getExtendedState(state, blockAccess, pos);
                         return dispatcher.getBlockModelRenderer().renderModel(blockAccess, model, state, pos, worldRendererIn, false);
                     case ENTITYBLOCK_ANIMATED:
                         return false;
                     default:
                         return false;
                 }
             }
         } catch (Throwable throwable) {
             CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
             CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
             CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
             throw new ReportedException(crashreport);
         }
    }


}
