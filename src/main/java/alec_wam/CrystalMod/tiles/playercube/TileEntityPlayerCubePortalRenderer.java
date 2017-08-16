package alec_wam.CrystalMod.tiles.playercube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
        	
        	profiler.startSection("entites");
        	/*if(tile.getCube() !=null){
	        	GlStateManager.enableColorMaterial();
	        	GlStateManager.pushMatrix();
	        	RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
	        	RenderHelper.enableStandardItemLighting();
	        	BlockPos pos = tile.getCube().minBlock;
	        	List<Entity> ents = CubeManager.getInstance().getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX()+16, pos.getY()+16, pos.getZ()+16));
	        	for(Entity entity : ents){
	        		if (entity ==null || entity.isInvisible() || entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) || !entity.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) continue;
		        	double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTick;
		            double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTick;
		            double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTick;
		            float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTick;
		            rendermanager.renderEntityWithPosYaw(entity, d0, d1, d2, 0.0F, partialTick);
	        	}
	        	RenderHelper.disableStandardItemLighting();
	            GlStateManager.popMatrix();
        	}*/
            /*RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);*/
            profiler.endSection();
        }
        
        
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        profiler.endSection();
        /*if (breakStage >= 0)
        {
            bindTexture(DESTROY_STAGES[breakStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } 
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1F, -1F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int k = 0;
        GlStateManager.rotate(k, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        
        
        if(tile.mobileChunk !=null){
        	this.bindTexture(TextureMap.locationBlocksTexture);
        	GlStateManager.translate(-8, -0F, -8);
        	((FakeChunkClient) tile.mobileChunk).getRenderer().render(partialTick);
        }
        
        
        if (breakStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);*/
    }

    @Override
	public void renderTileEntityAt(TileEntityPlayerCubePortal tileentity, double x, double y, double z, float partialTick, int breakStage)
    {
        render(tileentity, x, y, z, partialTick, breakStage);
    }
}

