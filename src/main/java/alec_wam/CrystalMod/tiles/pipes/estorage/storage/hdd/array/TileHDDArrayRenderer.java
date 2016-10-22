package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array;


import java.awt.Color;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.items.ItemIngot;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileHDDArrayRenderer extends TileEntitySpecialRenderer<TileHDDArray> {

	@Override
    public void renderTileEntityAt(TileHDDArray te, double x, double y, double z, float partialTicks, int destroyStage) {
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
    		float yOff = 0;
	        
	        float pixel = (1.0f/32.0f);
	        for(int h = 0; h < te.getSizeInventory(); h++){
	        	double xPos = (pixel*32)-(h % 2 != 0 ? (pixel*18) : (pixel*5));
	        	double yPos = (pixel*32)-((pixel*5)+yOff);
	        	double width = -(pixel*9);
	        	double height = -(pixel*4);
		        int r = 0;
		        int g = 0;
		        int b = 0;
		        int a = 255;
		        Color color = Color.BLACK;
		        Color hddColor = null;
		        boolean hasHDD = false;
		        
		        ItemStack hddStack = te.getStackInSlot(h);
		        if(hddStack !=null){
		        	hasHDD = true;
		        	int[] hddColors = {ItemIngot.RGB_BLUE, ItemIngot.RGB_RED, ItemIngot.RGB_GREEN, ItemIngot.RGB_DARK, ItemIngot.RGB_PURE};
		        	color = new Color(hddColors[hddStack.getMetadata() % hddColors.length]);
		        	hddColor = Color.RED;
		        }
		        r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				a = color.getAlpha();
		        float minZ = -0.001f;
		        
		        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		        worldrenderer.pos(xPos, yPos, minZ).color(r, g, b, a).endVertex();
		        worldrenderer.pos(xPos, yPos + height, minZ).color(r, g, b, a).endVertex();
		        worldrenderer.pos(xPos + width, yPos + height, minZ).color(r, g, b, a).endVertex();
		        worldrenderer.pos(xPos + width, yPos, minZ).color(r, g, b, a).endVertex();
		        tessellator.draw();
		        if(hasHDD){
			        color = Color.GRAY.brighter();
			        minZ -= 0.001f;
			        r = color.getRed();
					g = color.getGreen();
					b = color.getBlue();
					a = color.getAlpha();
					xPos-=(pixel*1);
					yPos-=(pixel*1);
					width+=(pixel*2);
					height+=(pixel*2);
			        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			        worldrenderer.pos(xPos, yPos, minZ).color(r, g, b, a).endVertex();
			        worldrenderer.pos(xPos, yPos + height, minZ).color(r, g, b, a).endVertex();
			        worldrenderer.pos(xPos + width, yPos + height, minZ).color(r, g, b, a).endVertex();
			        worldrenderer.pos(xPos + width, yPos, minZ).color(r, g, b, a).endVertex();
			        tessellator.draw();
			        
			        if(hddColor !=null){
				        //DOT
				        color = hddColor;
				        minZ -= 0.001f;
				        r = color.getRed();
						g = color.getGreen();
						b = color.getBlue();
						a = color.getAlpha();
						xPos-=pixel*6;
						width=-(pixel);
						height=-(pixel);
				        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				        worldrenderer.pos(xPos, yPos, minZ).color(r, g, b, a).endVertex();
				        worldrenderer.pos(xPos, yPos + height, minZ).color(r, g, b, a).endVertex();
				        worldrenderer.pos(xPos + width, yPos + height, minZ).color(r, g, b, a).endVertex();
				        worldrenderer.pos(xPos + width, yPos, minZ).color(r, g, b, a).endVertex();
				        tessellator.draw();
			        }
		        }
		        
		        if(h % 2 !=0){
		        	yOff += (pixel*6);
		        }
	        }
		}
        GlStateManager.enableTexture2D();
        //GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
