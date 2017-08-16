package alec_wam.CrystalMod.tiles.machine.elevator.caller;

import alec_wam.CrystalMod.tiles.machine.elevator.caller.TileEntityElevatorCaller.ElevatorButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

public class TileEntityElevatorCallerRenderer extends TileEntitySpecialRenderer<TileEntityElevatorCaller> {

	@Override
    public void renderTileEntityAt(TileEntityElevatorCaller te, double x, double y, double z, float partialTicks, int destroyStage) {
    	GlStateManager.pushMatrix();
    	GlStateManager.translate(x+0.5, y+1.5, z+0.5);
    	GlStateManager.scale(1, -1, -1);
    	int angle = 180;
    	EnumFacing face = te.getWorld().getBlockState(te.getPos()).getValue(BlockElevatorCaller.FACING_HORIZ);
    	if(face == EnumFacing.SOUTH){
    		angle = 0;
    	}
    	if(face == EnumFacing.WEST){
    		angle = 90;
    	}
    	if(face == EnumFacing.EAST){
    		angle = 270;
    	}
    	GlStateManager.rotate(angle, 0, 1, 0);
    	GlStateManager.translate(-1, 0, -1);
    	
        for(ElevatorButton button : te.buttons){
        	Tessellator tessellator = Tessellator.getInstance();
        	VertexBuffer worldrenderer = tessellator.getBuffer();
        	GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            double minX = 0.5;
            double minY = 0.5;
            double minZ = 0.5-0.0001;
            float r = 1.0f;
            float g = 1.0f;
            float b = button.dye == 1 ? 0f : 1.0f;
            float a = 1.0f;
            worldrenderer.pos(minX + button.posX, minY+button.posY, minZ).color(r, g, b, a).endVertex();
            worldrenderer.pos(minX + button.posX, minY+button.posY+button.height, minZ).color(r, g, b, a).endVertex();
            worldrenderer.pos(minX + button.posX + button.width, minY+button.posY+button.height, minZ).color(r, g, b, a).endVertex();
            worldrenderer.pos(minX + button.posX + button.width, minY+button.posY, minZ).color(r, g, b, a).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        	
        	GlStateManager.pushMatrix();
        	GlStateManager.translate(button.posX + 0.5D, button.posY + 0.5D, 0.498);
        	GlStateManager.translate(button.width / 2, button.height / 2, 0);
            float textScale = Math.min((float)button.width / 10F, (float)button.height / 10F);
            GlStateManager.scale(textScale, textScale, textScale);
            int color = 0;
            getFontRenderer().drawString(button.buttonText, -getFontRenderer().getStringWidth(button.buttonText) / 2, -getFontRenderer().FONT_HEIGHT / 2, color);
            GlStateManager.popMatrix();
        }
        
        GlStateManager.popMatrix();
    }
}
