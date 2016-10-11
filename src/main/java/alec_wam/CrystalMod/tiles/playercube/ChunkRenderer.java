package alec_wam.CrystalMod.tiles.playercube;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ChunkRenderer {
    /**
     * Boolean for whether this renderer needs to be updated or not
     */
    public boolean needsUpdate;
    public boolean isRemoved;

    private FakeChunk chunk;
    private int glRenderList = -1;

    /**
     * Bytes sent to the GPU
     */
    @SuppressWarnings("unused")
    private int bytesDrawn;

    public ChunkRenderer(FakeChunk mobilechunk) {
        chunk = mobilechunk;
        needsUpdate = true;
    }

    public void render(float partialTicks) {
    	
    	boolean ineff = false;
    	
    	if(ineff){
    		updateSimpleRender(partialTicks);
    		return;
    	}
    	
        if (needsUpdate) {
        	if (glRenderList >= 0) {
                GLAllocation.deleteDisplayLists(glRenderList);
                glRenderList = -1;
            }
    		glRenderList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(glRenderList, GL11.GL_COMPILE);
            updateSimpleRender(partialTicks);
            GL11.glEndList();
            needsUpdate = false;
        }
        if(this.glRenderList >=0){
        	GlStateManager.callList(glRenderList);
        }
    }

    @SuppressWarnings("deprecation")
	private void updateSimpleRender(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(1.0F, 0.0F, 180.0F, 0.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        for(BlockPos pos : chunk.cubeBlocks.keySet()){
	        IBlockState blockState = chunk.getBlockState(pos);
	        Block block = blockState.getBlock();
	
	        for (BlockRenderLayer enumWorldBlockLayer : BlockRenderLayer.values()) {
	            if (!block.canRenderInLayer(enumWorldBlockLayer)) continue;
	            
	
	            if (block.getRenderType(blockState) != EnumBlockRenderType.INVISIBLE) {
	            	net.minecraftforge.client.ForgeHooksClient.setRenderLayer(enumWorldBlockLayer);
	                dispatchBlockRender(blockState, pos, worldrenderer);
	            }
	        }
        }
        //Minecraft.getMinecraft().renderEngine.bindTexture(net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture);
        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        
       
        if(!chunk.chunkTileEntityMap.isEmpty()){
          //RenderHelper.enableStandardItemLighting();

           GlStateManager.pushMatrix();
	       World tesrDispatchWorld = TileEntityRendererDispatcher.instance.worldObj;
	
	        for(TileEntity tile : chunk.chunkTileEntityMap.values()){
		        if (tile != null) {
		            TileEntitySpecialRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tile);
		
		            if (renderer != null && tile.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) {
		                TileEntity tileClone = tile;
		                final World world = TileEntityRendererDispatcher.instance.worldObj;
		                tileClone.setWorldObj(chunk.getFakeWorld());
		                TileEntityRendererDispatcher.instance.setWorld(chunk.getFakeWorld());
		                TileEntityRendererDispatcher.instance.renderTileEntityAt(tileClone, tileClone.getPos().getX(), tileClone.getPos().getY(), tileClone.getPos().getZ(), partialTicks);
		                TileEntityRendererDispatcher.instance.setWorld(world);
		            }
		        }
	        }
	        
	        //
	        TileEntityRendererDispatcher.instance.setWorld(tesrDispatchWorld);
	        //Minecraft.getMinecraft().renderEngine.bindTexture(net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture);
	        GlStateManager.popMatrix();
	        Minecraft.getMinecraft().renderEngine.bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
        }
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    public void dispatchBlockRender(IBlockState blockState, BlockPos blockPos, VertexBuffer worldRenderer) {
        worldRenderer.color(1.0F, 1.0F, 1.0F, 1.0F);
        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        blockRendererDispatcher.renderBlock(blockState, blockPos, chunk, worldRenderer);
    }

    public void markDirty() {
        needsUpdate = true;
    }

    public void markRemoved() {
        isRemoved = true;

        try {
            if (glRenderList != 0) {
                //System.out.println("Deleting mobile chunk display list " + glRenderList);
                GLAllocation.deleteDisplayLists(glRenderList);
                glRenderList = 0;
            }
        } catch (Exception e) {
        	System.out.println("Failed to destroy mobile chunk display list");
        	e.printStackTrace();
        }
    }
}
