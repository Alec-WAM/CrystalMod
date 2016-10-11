package alec_wam.CrystalMod.tiles.machine.elevator;

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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

public class TileEntityElevatorRenderer extends TileEntitySpecialRenderer<TileEntityElevator> {

	@Override
    public void renderTileEntityAt(TileEntityElevator te, double x, double y, double z, float partialTicks, int destroyStage) {
    	Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("crystalmod-elevator");
        if (te.isMoving()) {
        	// Correction in the y translation to avoid jitter when both player and platform are moving
        	AxisAlignedBB aabb = te.getAABBAboveElevator(0);
        	boolean on = Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().intersectsWith(aabb);
        	double diff = on ? (te.getPos().getY() - (y+te.getMovingY()) - 1) : 0;
        	 
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

            IBlockState movingState = te.getMovingState();
            
            GlStateManager.translate(0, te.getMovingY() - te.getPos().getY() + diff, 0);
            Tessellator tessellator = Tessellator.getInstance();
            BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            //chunk.setWorld(te.getWorld());
            for (BlockPos pos : te.getPositions()) {
                int dx = te.getPos().getX() - pos.getX();
                int dy = te.getPos().getY() - pos.getY();
                int dz = te.getPos().getZ() - pos.getZ();
                
                tessellator.getBuffer().setTranslation(x - pos.getX() - dx, y - pos.getY() - dy, z - pos.getZ() - dz);
                //chunk.setBlockState(pos, movingState);
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

    @SuppressWarnings("deprecation")
	private static boolean renderBlock(BlockRendererDispatcher dispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, VertexBuffer worldRendererIn) {
        try {
            /*if (!state.getBlock().canRenderInLayer(MinecraftForgeClient.getRenderLayer())) {
                return false;
            } else {
                IBakedModel model = dispatcher.getModelFromBlockState(state, blockAccess, pos);
                state = state.getBlock().getExtendedState(state, blockAccess, pos);
                return dispatcher.getBlockModelRenderer().renderModel(blockAccess, model, state, pos, worldRendererIn, false);
            }*/
        	
        	for (BlockRenderLayer enumWorldBlockLayer : BlockRenderLayer.values()) {
	            if (!state.getBlock().canRenderInLayer(enumWorldBlockLayer)) continue;
	            
	
	            if (state.getBlock().getRenderType(state) != EnumBlockRenderType.INVISIBLE) {
	            	net.minecraftforge.client.ForgeHooksClient.setRenderLayer(enumWorldBlockLayer);
	            	worldRendererIn.color(1.0F, 1.0F, 1.0F, 1.0F);
	            	IBakedModel model = dispatcher.getModelForState(state);
	                BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
	                return blockRendererDispatcher.getBlockModelRenderer().renderModel(blockAccess, model, state, pos, worldRendererIn, false);
	            }
	        }
        	return false;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
        }
    }


}
