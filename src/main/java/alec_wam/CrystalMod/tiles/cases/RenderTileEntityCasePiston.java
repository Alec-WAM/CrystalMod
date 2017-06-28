package alec_wam.CrystalMod.tiles.cases;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.handler.ClientEventHandler;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;

public class RenderTileEntityCasePiston extends TileEntitySpecialRenderer<TileEntityCasePiston> {

	@Override
	public void renderTileEntityAt(TileEntityCasePiston tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null)return;
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);

		//GlStateManager.translate(x, y, z);
		boolean shouldRender = false;
		for(EnumFacing facing : EnumFacing.VALUES){
			shouldRender |= tile.progress[facing.getIndex()] > 0;
		}
		if(shouldRender){
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
			EnumFacing[] faces = EnumFacing.VALUES;
			for(EnumFacing facing : faces){
				float progress = tile.getProgress(facing, partialTicks);
				IBlockState piston = Blocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonBase.FACING, facing).withProperty(BlockPistonExtension.SHORT, false);
				BlockPos pos = tile.getPos();
				double dx = -progress * facing.getFrontOffsetX();
				double dy = -progress * facing.getFrontOffsetY();
				double dz = -progress * facing.getFrontOffsetZ();
				tessellator.getBuffer().setTranslation(x - pos.getX() - dx, y - pos.getY() - dy, z - pos.getZ() - dz);
				renderBlock(dispatcher, piston, pos, tile.getWorld(), tessellator.getBuffer());
			}
			tessellator.getBuffer().setTranslation(0, 0, 0);
			tessellator.draw();


			GlStateManager.disableBlend();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
			RenderHelper.enableStandardItemLighting();
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
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
			EnumFacing[] faces = {EnumFacing.NORTH};
			BlockPos pos = tile.getPos();
			int k2 = tile.getWorld().getCombinedLight(pos.offset(EnumFacing.UP), 0);
	        int l2 = k2 >> 16 & 65535;
	        int i3 = k2 & 65535;
	        
	        TextureAtlasSprite sprite = RenderUtil.getSprite("crystalmod:blocks/case_piston_head");
			double d3 = (double)sprite.getMinU();
			double d4 = (double)sprite.getMinV();
			double d5 = (double)sprite.getMaxU();
			double d6 = (double)sprite.getMaxV();
			VertexBuffer buffer = tessellator.getBuffer();
			
			float red = 1;
			float green = 1;
			float blue = 1;
	        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
	        buffer.pos(0, 1, 1).tex(d5, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 1, 1).tex(d5, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 1, 0).tex(d3, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 1, 0).tex(d3, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        
	        buffer.pos(0, 0, 0).tex(d5, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 0, 0).tex(d5, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 0, 1).tex(d3, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 0, 1).tex(d3, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        
	        buffer.pos(0, 0, 0).tex(d5, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 1, 0).tex(d5, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 1, 0).tex(d3, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 0, 0).tex(d3, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        
	        buffer.pos(1, 0, 1).tex(d5, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 1, 1).tex(d5, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 1, 1).tex(d3, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 0, 1).tex(d3, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        
	        buffer.pos(0, 0, 1).tex(d5, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 1, 1).tex(d5, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 1, 0).tex(d3, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(0, 0, 0).tex(d3, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        
	        buffer.pos(1, 0, 0).tex(d5, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 1, 0).tex(d5, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 1, 1).tex(d3, d4).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        buffer.pos(1, 0, 1).tex(d3, d6).lightmap(l2, i3).color(red, green, blue, 1.0f).endVertex();
	        tessellator.draw();


			GlStateManager.disableBlend();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
			RenderHelper.enableStandardItemLighting();
		}
		
		GlStateManager.popMatrix();
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