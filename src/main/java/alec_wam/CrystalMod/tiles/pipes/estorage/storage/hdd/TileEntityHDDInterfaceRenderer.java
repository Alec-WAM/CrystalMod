package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

public class TileEntityHDDInterfaceRenderer extends TileEntitySpecialRenderer<TileEntityHDDInterface> {

	@Override
    public void renderTileEntityAt(TileEntityHDDInterface te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	GlStateManager.translate(x+0.5, y+1.5, z+0.5);
    	GlStateManager.scale(1, -1, -1);
    	
    	float angle = 0.0f;
    	float angleY = 0.0f;
    	EnumFacing face = EnumFacing.getFront(te.facing);
    	if(face == EnumFacing.NORTH){
    		angle = 180;
    	}
    	if(face == EnumFacing.EAST){
    		angle = 270;
    	}
    	if(face == EnumFacing.WEST){
    		angle = 90;
    	}
    	if(face == EnumFacing.UP){
    		angleY = -90;
    		GlStateManager.translate(0.12, 0.5, 0.5);
    		GlStateManager.rotate(angleY, 1, 0, 0);
    		GlStateManager.translate(0.45, -0.5, 0.5);
    	}
    	if(face == EnumFacing.DOWN){
    		angleY = 90;
    		GlStateManager.translate(0, 1, 1);
    		GlStateManager.rotate(angleY, 1, 0, 0);
    		GlStateManager.rotate(180, 0, 0, 1);
    	}
    	GlStateManager.rotate(angle, 0, 1, 0);
    	GlStateManager.translate(-1, 0, -1);
    	GlStateManager.disableLighting();
    	Tessellator tessellator = Tessellator.getInstance();
    	VertexBuffer worldrenderer = tessellator.getBuffer();
    	GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        double minX = 0.5;
        double minY = 0.5;
        double minZ = 0.5-0.0001;
        double xPos = 0.15;
        double hddHealth = (te.getStackInSlot(0) == null ? 0.0D : te.getStackInSlot(0).getItem().getDurabilityForDisplay(te.getStackInSlot(0)));
        double j = 0.7D - hddHealth * 0.7D;
        double yOff = j;
        double yPos = 0.15+yOff;
        double width = 0.13;
        double height = 0.7-yOff;
        int i = (int)Math.round(255.0D - hddHealth * 255.0D);
        float r = i;
        float g = 255-i;
        float b = 0.0f;
        float a = 1f;
        worldrenderer.pos(minX + xPos, minY + yPos, minZ).color(r, g, b, a).endVertex();
        worldrenderer.pos(minX + xPos, minY + yPos + height, minZ).color(r, g, b, a).endVertex();
        worldrenderer.pos(minX + xPos + width, minY + yPos + height, minZ).color(r, g, b, a).endVertex();
        worldrenderer.pos(minX + xPos + width, minY + yPos, minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
