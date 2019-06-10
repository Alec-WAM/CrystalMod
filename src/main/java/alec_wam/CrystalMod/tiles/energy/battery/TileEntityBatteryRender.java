package alec_wam.CrystalMod.tiles.energy.battery;


import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class TileEntityBatteryRender extends TileEntityRenderer<TileEntityBattery> {
	private static final ResourceLocation TEXTURE_METER_UNCHARGED = CrystalMod.resourceL("textures/block/battery/meter/uncharged.png");
	private static final ResourceLocation TEXTURE_METER_CHARGED = CrystalMod.resourceL("textures/block/battery/meter/charged.png");
	private static final ResourceLocation TEXTURE_METER_CREATIVE = CrystalMod.resourceL("textures/block/battery/meter/creative.png");
	private static final ResourceLocation[] TEXTURE_METER = new ResourceLocation[9];
	static {
		for(int i = 0; i < 9; i++){
			TEXTURE_METER[i] = CrystalMod.resourceL("textures/block/battery/meter/"+i+".png");
		}
	}

	@Override
    public void render(TileEntityBattery te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
    	GlStateManager.translated(x, y, z);
    	renderMeter(te.getFacing(), te.energyStorage.getCEnergyStored(), te.energyStorage.getMaxCEnergyStored(), te.isCreative());
        GlStateManager.popMatrix();
    }
	
	public static void renderMeter(Direction rotation, int energy, int maxEnergy, boolean creative){
		GlStateManager.pushMatrix();
		GlStateManager.translated(0.5, 0.5, 0.5);
		GlStateManager.rotatef(180, 0, 0, 1);
		GlStateManager.translated(-0.5, -0.5, -0.5);
    	int angle = 0;
    	Direction face = rotation;
    	if(face == Direction.UP){
    		angle = 90;
    		GlStateManager.translated(0, 1, 0);
    		GlStateManager.rotatef(angle, 1, 0, 0);
    	}
    	if(face == Direction.DOWN){
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
        
        int meter = (energy * 8 / maxEnergy);
        GlStateManager.disableLighting();
        Minecraft.getInstance().getTextureManager().bindTexture(creative ? TEXTURE_METER_CREATIVE : TEXTURE_METER[meter]);
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
        
        
        ResourceLocation meterTex = (energy > 0 || creative) ? TEXTURE_METER_CHARGED : TEXTURE_METER_UNCHARGED;
        Minecraft.getInstance().getTextureManager().bindTexture(meterTex);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        min = new Vector3f(0.67f-offset, -0.34f+0.67f, 0.67f);
        max = new Vector3f(0.67f*2-offset, -0.34f+0.67f, 0.67f*2);
        worldrenderer.pos(min.getX(), min.getY(), min.getZ()).tex(minU, minV).endVertex();
        worldrenderer.pos(min.getX(), max.getY(), max.getZ()).tex(minU, maxV).endVertex();
        worldrenderer.pos(max.getX(), max.getY(), max.getZ()).tex(maxU, maxV).endVertex();
        worldrenderer.pos(max.getX(), min.getY(), min.getZ()).tex(maxU, minV).endVertex();
        tessellator.draw();
        
        meterTex = (energy >= maxEnergy || creative) ? TEXTURE_METER_CHARGED : TEXTURE_METER_UNCHARGED;
        Minecraft.getInstance().getTextureManager().bindTexture(meterTex);
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
