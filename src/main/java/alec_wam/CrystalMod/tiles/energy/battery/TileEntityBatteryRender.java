package alec_wam.CrystalMod.tiles.energy.battery;


import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityBatteryRender extends TileEntityRenderer<TileEntityBattery> {

	@Override
    public void render(TileEntityBattery te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	GlStateManager.translated(x+1, y+1, z);
    	GlStateManager.rotatef(180, 0, 0, 1);
    	int angle = 0;
    	EnumFacing face = te.getFacing();
    	if(face == EnumFacing.UP){
    		angle = 90;
    		GlStateManager.translated(0, 1, 0);
    		GlStateManager.rotatef(angle, 1, 0, 0);
    	}
    	if(face == EnumFacing.DOWN){
    		angle = 270;
    		GlStateManager.translated(0, 0, 1);
    		GlStateManager.rotatef(angle, 1, 0, 0);
    	}
    	
    	Tessellator tessellator = Tessellator.getInstance();
    	BufferBuilder worldrenderer = tessellator.getBuffer();
    	GlStateManager.translated(-0.5, 0.5, -0.5);
        float offset = 0.0f;
        
        Vector3f min = new Vector3f(0.67f-offset, -0.34f, 0.67f);
        Vector3f max = new Vector3f(0.67f*2-offset, -0.34f+0.67f, 0.67f*2);
        float minU = 0+(0.249f);
        float maxU = 1-(0.249f);
        float minV = 0+(0.249f);
        float maxV = 1-(0.249f);
        
        boolean creative = te.isCreative();
        String meter = ""+te.getScaledEnergyStored(8);
        if(creative){
        	meter = "creative";
        }
        GlStateManager.disableLighting();
        bindTexture(new ResourceLocation("crystalmod:textures/block/battery/meter/"+meter+".png"));
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(min.getX(), min.getY(), min.getZ()).tex(minU, minV).endVertex();
        worldrenderer.pos(min.getX(), max.getY(), min.getZ()).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.getX(), max.getY(), min.getZ()).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.getX(), min.getY(), min.getZ()).tex(maxU, minV).endVertex();
        
        min = new Vector3f(0.67f*2-offset, -0.34f, 2-0.67f);
        max = new Vector3f(0.67f-offset, -0.34f+0.67f, 2-0.67f);
        worldrenderer.pos(min.getX(), min.getY(), min.getZ()).tex(minU, minV).endVertex();
        worldrenderer.pos(min.getX(), max.getY(), min.getZ()).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.getX(), max.getY(), max.getZ()).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.getX(), min.getY(), max.getZ()).tex(maxU, minV).endVertex();
        
        min = new Vector3f(0.67f-offset, -0.34f, 0.67f*2);
        max = new Vector3f(0.67f-offset, -0.34f+0.67f, 0.67f);
        worldrenderer.pos(min.getX(), min.getY(), min.getZ()).tex(minU, minV).endVertex();
        worldrenderer.pos(min.getX(), max.getY(), min.getZ()).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.getX(), max.getY(), max.getZ()).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.getX(), min.getY(), max.getZ()).tex(maxU, minV).endVertex();
        
        min = new Vector3f(2-0.67f-offset, -0.34f, 0.67f);
        max = new Vector3f(2-0.67f-offset, -0.34f+0.67f, 0.67f*2);
        worldrenderer.pos(min.getX(), min.getY(), min.getZ()).tex(minU, minV).endVertex();
        worldrenderer.pos(min.getX(), max.getY(), min.getZ()).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.getX(), max.getY(), max.getZ()).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.getX(), min.getY(), max.getZ()).tex(maxU, minV).endVertex();
        tessellator.draw();
        
        
        String meterTex = (te.energyStorage.getCEnergyStored() > 0 || creative) ? "charged" : "uncharged";
        bindTexture(new ResourceLocation("crystalmod:textures/block/battery/meter/"+meterTex+".png"));
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        min = new Vector3f(0.67f-offset, -0.34f+0.67f, 0.67f);
        max = new Vector3f(0.67f*2-offset, -0.34f+0.67f, 0.67f*2);
        worldrenderer.pos(min.getX(), min.getY(), min.getZ()).tex(minU, minV).endVertex();
        worldrenderer.pos(min.getX(), max.getY(), max.getZ()).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.getX(), max.getY(), max.getZ()).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.getX(), min.getY(), min.getZ()).tex(maxU, minV).endVertex();
        tessellator.draw();
        
        meterTex = (te.energyStorage.getCEnergyStored() >= te.energyStorage.getMaxCEnergyStored() || creative) ? "charged" : "uncharged";
        bindTexture(new ResourceLocation("crystalmod:textures/block/battery/meter/"+meterTex+".png"));
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        min = new Vector3f(0.67f-offset, -0.34f, 0.67f*2);
        max = new Vector3f(0.67f*2-offset, -0.34f, 0.67f);
        worldrenderer.pos(min.getX(), min.getY(), min.getZ()).tex(minU, minV).endVertex();
        worldrenderer.pos(min.getX(), max.getY(), max.getZ()).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.getX(), max.getY(), max.getZ()).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.getX(), min.getY(), min.getZ()).tex(maxU, minV).endVertex();
        tessellator.draw();
        
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
	
}
