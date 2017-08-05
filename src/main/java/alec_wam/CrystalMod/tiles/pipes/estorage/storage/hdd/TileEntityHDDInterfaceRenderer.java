package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;


import java.awt.Color;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.items.ItemIngot;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
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
    	GlStateManager.translate(x, y, z);
    	EnumFacing face = EnumFacing.getFront(te.facing);
    	float angleY = 0;
    	float angleX = 0;
    	if(face == EnumFacing.SOUTH){
    		angleY = 180;
    	}
    	if(face == EnumFacing.WEST){
    		angleY = 90;
    	}
    	if(face == EnumFacing.EAST){
    		angleY = 270;
    	}
    	if(face == EnumFacing.UP){
    		angleX = 90;
    	}
    	if(face == EnumFacing.DOWN){
    		angleX = 270;
    	}
    	
    	GlStateManager.translate(0.5, 0.5, 0.5);
    	if(angleY != 0)GlStateManager.rotate(angleY, 0, 1, 0);
    	if(angleX != 0)GlStateManager.rotate(angleX, 1, 0, 0);
    	GlStateManager.translate(-0.5, -0.5, -0.5);
    	
    	Tessellator tessellator = Tessellator.getInstance();
    	VertexBuffer worldrenderer = tessellator.getBuffer();
    	GlStateManager.disableTexture2D();
    	{
    		boolean drawBar = ItemStackTools.isValid(te.getStackInSlot(0));
    		if(drawBar){
	    		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		        double minX = 0.565;
		        double minZ = 0.0-0.0001;
		        double xPos = 0.15;
		        double hddHealth = (te.getStackInSlot(0).getItem().getDurabilityForDisplay(te.getStackInSlot(0)));
		        double width = 0.13;		        
		        double minY = 0.0;
		        double height = 0.7 * (1.0 - hddHealth);
		        double yPos = 0.15;
		        
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
    		}
		}
        GlStateManager.enableTexture2D();
        //GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
