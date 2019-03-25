package alec_wam.CrystalMod.tiles.playercube;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;

public class TileEntityPlayerCubePortalRenderer<T extends TileEntityPlayerCubePortal> extends TileEntitySpecialRenderer<T>
{
   public void render(TileEntityPlayerCubePortal tile, double x, double y, double z, float partialTick, int breakStage)
    {
        if (tile == null) {
            return;
        }
        Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("crystalmod-cubeportal");
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float) x, (float) y, (float) z);
        
        if(tile.mobileChunk !=null){
        	this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        	
        	if(tile.getCube() !=null){
        		BlockPos pos = tile.getCube().minBlock;
        		if(pos.getX() > 0)
        		GlStateManager.translate((float) -pos.getX()/16, 0, 0);
        		if(pos.getZ() > 0)
            	GlStateManager.translate(0, 0, (float)-pos.getZ()/16);
        	}
        	
        	GlStateManager.scale(1f/16, 1f/16, 1f/16);
        	profiler.startSection("blocks");
        	((FakeChunkClient) tile.mobileChunk).getRenderer().render(partialTick);
        	profiler.endSection();
        	
        	if(!Strings.isNullOrEmpty(tile.cubeID)){
        		TextureAtlasSprite sprite = RenderUtil.getTexture(ModBlocks.cubeBlock.getDefaultState());
        		double barSize = 0.05;
        		RenderUtil.renderCuboid(sprite, tile.getPos(), 0, 0, 0, barSize * 16.0F, barSize * 16.0F, barSize * 16.0F, (1.0-barSize) * 16.0F, (1.0-barSize) * 16.0F, (1.0-barSize) * 16.0F, -1);
        	}
        }       
        
        
        
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        profiler.endSection();
    }

    @Override
	public void renderTileEntityAt(TileEntityPlayerCubePortal tileentity, double x, double y, double z, float partialTick, int breakStage)
    {
        render(tileentity, x, y, z, partialTick, breakStage);
    }
}

