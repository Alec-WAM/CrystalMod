package alec_wam.CrystalMod.tiles.weather;

import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class GuiWeather extends GuiContainer {

	private TileEntityWeather weather;
	
	public GuiWeather(TileEntityWeather weather) {
		super(new ContainerWeather());
		this.weather = weather;
		this.width = this.xSize = 232;
		this.height = this.ySize = 236;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		float partialTicks = RenderUtil.getPartialTick();
		final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
	    final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
	    final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
	    final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
    	
        GlStateManager.pushMatrix();
        GlStateManager.translate(5, 23, 0);
        GlStateManager.scale(18, 18, 0);
        float f = mc.theWorld.getRainStrength(partialTicks);

        if (f > 0.0F)
        {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        	blockpos$mutableblockpos.setPos(weather.getPos().getX(), weather.getPos().getY(), weather.getPos().getZ());
        	Biome biomegenbase = mc.theWorld.getBiomeGenForCoords(blockpos$mutableblockpos);
        	float f2 = biomegenbase.getFloatTemperature(blockpos$mutableblockpos);
        	f2 = mc.theWorld.getBiomeProvider().getTemperatureAtHeight(f2, weather.getPos().getY());
        	boolean canSnow = (f2 < 0.15F);
			mc.getTextureManager().bindTexture(canSnow ? locationSnowPng : locationRainPng);
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        int update = (Integer) ReflectionUtils.getPrivateValue(mc.entityRenderer, EntityRenderer.class, ObfuscatedNames.EntityRenderer_rendererUpdateCount);
	        float h = (update + partialTicks) / 32f*(canSnow ? 0.5f : 6);
	        float k2 = h;
	        float l2 = h+1;
	        double d5 = 0;
	        worldrenderer.pos((double)(0 + 0), (double)(0 + 4), (double)this.zLevel).tex((double)((float)(0)), (double)((float)(k2 * 0.25D + d5))).endVertex();
	        worldrenderer.pos((double)(0 + 4), (double)(0 + 4), (double)this.zLevel).tex((double)((float)(1)), (double)((float)(k2 * 0.25D + d5))).endVertex();
	        worldrenderer.pos((double)(0 + 4), (double)(0 + 0), (double)this.zLevel).tex((double)((float)(1)), (double)((float)(l2 * 0.25D + d5))).endVertex();
	        worldrenderer.pos((double)(0 + 0), (double)(0 + 0), (double)this.zLevel).tex((double)((float)(0)), (double)((float)(l2 * 0.25D + d5))).endVertex();
	        tessellator.draw();
        }else{
        	mc.getTextureManager().bindTexture(locationSunPng);
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        worldrenderer.pos((double)(0 + 0), (double)(0 + 4), (double)this.zLevel).tex((double)((float)(0)), (double)((float)(0))).endVertex();
	        worldrenderer.pos((double)(0 + 4), (double)(0 + 4), (double)this.zLevel).tex((double)((float)(1)), (double)((float)(0))).endVertex();
	        worldrenderer.pos((double)(0 + 4), (double)(0 + 0), (double)this.zLevel).tex((double)((float)(1)), (double)((float)(1))).endVertex();
	        worldrenderer.pos((double)(0 + 0), (double)(0 + 0), (double)this.zLevel).tex((double)((float)(0)), (double)((float)(1))).endVertex();
	        tessellator.draw();
        }
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(2+(76*2), 23, 0);
        GlStateManager.scale(18.3, 18, 0);
		mc.getTextureManager().bindTexture(locationMoonPhasesPng);
        int i = mc.theWorld.getMoonPhase();
        int k = i % 4;
        int i1 = i / 4 % 2;
        float f22 = (float)(k + 0) / 4.0F;
        float f23 = (float)(i1 + 0) / 2.0F;
        float f24 = (float)(k + 1) / 4.0F;
        float f14 = (float)(i1 + 1) / 2.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(0 + 0), (double)(0 + 4), (double)this.zLevel).tex((double)((float)(f24)), (double)((float)(f14))).endVertex();
        worldrenderer.pos((double)(0 + 4), (double)(0 + 4), (double)this.zLevel).tex((double)((float)(f22)), (double)((float)(f14))).endVertex();
        worldrenderer.pos((double)(0 + 4), (double)(0 + 0), (double)this.zLevel).tex((double)((float)(f22)), (double)((float)(f23))).endVertex();
        worldrenderer.pos((double)(0 + 0), (double)(0 + 0), (double)this.zLevel).tex((double)((float)(f24)), (double)((float)(f23))).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        
        /*if(f > 0){
        	String time = ""+TimeUtil.getTimeFromTicks(mc.theWorld.getWorldInfo().getRainTime());
        	this.drawString(fontRendererObj, "Rain Left: "+time, 5, 23+76+3, java.awt.Color.BLUE.getRGB());
        }*/
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/weather.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}

}
