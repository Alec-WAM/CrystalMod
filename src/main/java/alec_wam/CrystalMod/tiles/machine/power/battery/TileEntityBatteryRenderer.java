package alec_wam.CrystalMod.tiles.machine.power.battery;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.util.vector.Vector3f;

public class TileEntityBatteryRenderer extends TileEntitySpecialRenderer<TileEntityBattery> {

	@Override
    public void renderTileEntityAt(TileEntityBattery te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	GlStateManager.translate(x+1, y+1, z);
    	GlStateManager.rotate(180, 0, 0, 1);
    	int angle = 0;
    	EnumFacing face = EnumFacing.getFront(te.facing);
    	if(face == EnumFacing.UP){
    		angle = 90;
    		GlStateManager.translate(0, 1, 0);
    		GlStateManager.rotate(angle, 1, 0, 0);
    	}
    	if(face == EnumFacing.DOWN){
    		angle = 270;
    		GlStateManager.translate(0, 0, 1);
    		GlStateManager.rotate(angle, 1, 0, 0);
    	}
    	
    	Tessellator tessellator = Tessellator.getInstance();
    	VertexBuffer worldrenderer = tessellator.getBuffer();
    	//GlStateManager.disableTexture2D();
        
    	/*Vector3f batMin = new Vector3f(0, 0, 0);
    	Vector3f batMax = new Vector3f(1, 0, 1);
    	
    	float pixel = 0.1f;
    	float batMinU = 0;
    	float batMaxU = 1;
    	float batMinV = 0;
    	float batMaxV = 1;

        
        int i = Minecraft.getMinecraft().theWorld.getCombinedLight(te.getPos(), 0);
        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    	//GlStateManager.disableLighting();
    	for(EnumFacing eFace : EnumFacing.VALUES){
    		GlStateManager.pushMatrix();
    		IOType io = te.getIO(eFace);
	    	
	    	if(eFace == EnumFacing.SOUTH){
	    		GlStateManager.translate(0, 1, 0);
	    		GlStateManager.rotate(90, 1, 0, 0);
	    	}
	    	
	    	if(eFace == EnumFacing.NORTH){
	    		GlStateManager.translate(0, 0, 1);
	    		GlStateManager.rotate(-90, 1, 0, 0);
	    	}
	    	
	    	if(eFace == EnumFacing.EAST){
	    		GlStateManager.translate(1, 0, 0);
	    		GlStateManager.rotate(90, 0, 0, 1);
	    	}
	    	
	    	if(eFace == EnumFacing.WEST){
	    		GlStateManager.translate(0, 1, 0);
	    		GlStateManager.rotate(-90, 0, 0, 1);
	    	}
	    	
	    	if(eFace == EnumFacing.DOWN){
	    		GlStateManager.translate(0, 1, 1);
	    		GlStateManager.rotate(180, 1, 0, 0);
	    	}
	    	worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
	    	bindTexture(new ResourceLocation("crystalmod:textures/blocks/machine/battery/io_"+io.getName().toLowerCase()+".png"));
	        worldrenderer.pos(batMin.x, 0, batMin.z).tex(batMinU, batMinV).endVertex();
	        worldrenderer.pos(batMax.x, 0, batMin.z).tex(batMinU, batMaxV).endVertex();
	        worldrenderer.pos(batMax.x, 0, batMax.z).tex(batMaxU, batMaxV).endVertex();
	        worldrenderer.pos(batMin.x, 0, batMax.z).tex(batMaxU, batMinV).endVertex();
	        tessellator.draw();
	        GlStateManager.popMatrix();
    	}*/
    	
        
        GlStateManager.translate(-0.5, 0.5, -0.5);
        float offset = 0.0f;
        
        Vector3f min = new Vector3f(0.67f-offset, -0.34f, 0.67f);
        Vector3f max = new Vector3f(0.67f*2-offset, -0.34f+0.67f, 0.67f*2);
        float minU = 0+(0.249f);
        float maxU = 1-(0.249f);
        float minV = 0+(0.249f);
        float maxV = 1-(0.249f);
        
        int meter = te.getScaledEnergyStored(8);
        GlStateManager.disableLighting();
        bindTexture(new ResourceLocation("crystalmod:textures/blocks/machine/battery/meter/"+meter+".png"));
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(min.x, min.y, min.z).tex(minU, minV).endVertex();
        worldrenderer.pos(min.x, max.y, min.z).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.x, max.y, min.z).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.x, min.y, min.z).tex(maxU, minV).endVertex();
        
        min = new Vector3f(0.67f*2-offset, -0.34f, 2-0.67f);
        max = new Vector3f(0.67f-offset, -0.34f+0.67f, 2-0.67f);
        worldrenderer.pos(min.x, min.y, min.z).tex(minU, minV).endVertex();
        worldrenderer.pos(min.x, max.y, min.z).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.x, max.y, max.z).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.x, min.y, max.z).tex(maxU, minV).endVertex();
        
        min = new Vector3f(0.67f-offset, -0.34f, 0.67f*2);
        max = new Vector3f(0.67f-offset, -0.34f+0.67f, 0.67f);
        worldrenderer.pos(min.x, min.y, min.z).tex(minU, minV).endVertex();
        worldrenderer.pos(min.x, max.y, min.z).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.x, max.y, max.z).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.x, min.y, max.z).tex(maxU, minV).endVertex();
        
        min = new Vector3f(2-0.67f-offset, -0.34f, 0.67f);
        max = new Vector3f(2-0.67f-offset, -0.34f+0.67f, 0.67f*2);
        worldrenderer.pos(min.x, min.y, min.z).tex(minU, minV).endVertex();
        worldrenderer.pos(min.x, max.y, min.z).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.x, max.y, max.z).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.x, min.y, max.z).tex(maxU, minV).endVertex();
        tessellator.draw();
        
        
        String meterTex = te.energyStorage.getCEnergyStored() > 0 ? "charged" : "uncharged";
        bindTexture(new ResourceLocation("crystalmod:textures/blocks/machine/battery/meter/"+meterTex+".png"));
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        min = new Vector3f(0.67f-offset, -0.34f+0.67f, 0.67f);
        max = new Vector3f(0.67f*2-offset, -0.34f+0.67f, 0.67f*2);
        worldrenderer.pos(min.x, min.y, min.z).tex(minU, minV).endVertex();
        worldrenderer.pos(min.x, max.y, max.z).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.x, max.y, max.z).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.x, min.y, min.z).tex(maxU, minV).endVertex();
        tessellator.draw();
        
        meterTex = te.energyStorage.getCEnergyStored() >= te.energyStorage.getMaxCEnergyStored() ? "charged" : "uncharged";
        bindTexture(new ResourceLocation("crystalmod:textures/blocks/machine/battery/meter/"+meterTex+".png"));
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        min = new Vector3f(0.67f-offset, -0.34f, 0.67f*2);
        max = new Vector3f(0.67f*2-offset, -0.34f, 0.67f);
        worldrenderer.pos(min.x, min.y, min.z).tex(minU, minV).endVertex();
        worldrenderer.pos(min.x, max.y, max.z).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.x, max.y, max.z).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.x, min.y, min.z).tex(maxU, minV).endVertex();
        tessellator.draw();
        
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
