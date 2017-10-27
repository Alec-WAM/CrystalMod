package alec_wam.CrystalMod.tiles.weather;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.client.util.IconRenderer;
import alec_wam.CrystalMod.client.util.IconRenderer.Icon;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.client.GuiUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

public class GuiWeather extends GuiContainer {

	private TileEntityWeather weather;
	
	public GuiWeather(TileEntityWeather weather) {
		super(new ContainerWeather());
		this.weather = weather;
		this.width = this.xSize = 232;
		this.height = this.ySize = 236;
	}

	@Override
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
        
        WorldInfo info = CrystalMod.proxy.getClientWorld().getWorldInfo();
        
        float f = CrystalMod.proxy.getClientWorld().getRainStrength(partialTicks);

        if (f > 0.0F)
        {
        	//Rain
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        	blockpos$mutableblockpos.setPos(weather.getPos().getX(), weather.getPos().getY(), weather.getPos().getZ());
        	Biome biomegenbase = CrystalMod.proxy.getClientWorld().getBiome(blockpos$mutableblockpos);
        	float f2 = biomegenbase.getFloatTemperature(blockpos$mutableblockpos);
        	f2 = CrystalMod.proxy.getClientWorld().getBiomeProvider().getTemperatureAtHeight(f2, weather.getPos().getY());
        	boolean canSnow = (f2 < 0.15F);
			mc.getTextureManager().bindTexture(canSnow ? locationSnowPng : locationRainPng);
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        int update = (Integer) ReflectionUtils.getPrivateValue(mc.entityRenderer, EntityRenderer.class, ObfuscatedNames.EntityRenderer_rendererUpdateCount);
	        float h = (update + partialTicks) / 32f*(canSnow ? 0.5f : 6);
	        float k2 = h;
	        float l2 = h+1;
	        double d5 = 0;
	        worldrenderer.pos(0 + 0, 0 + 4, this.zLevel).tex(((0)), ((float)(k2 * 0.25D + d5))).endVertex();
	        worldrenderer.pos(0 + 4, 0 + 4, this.zLevel).tex(((1)), ((float)(k2 * 0.25D + d5))).endVertex();
	        worldrenderer.pos(0 + 4, 0 + 0, this.zLevel).tex(((1)), ((float)(l2 * 0.25D + d5))).endVertex();
	        worldrenderer.pos(0 + 0, 0 + 0, this.zLevel).tex(((0)), ((float)(l2 * 0.25D + d5))).endVertex();
	        tessellator.draw();
        }else{
        	//Sun
        	mc.getTextureManager().bindTexture(locationSunPng);
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        worldrenderer.pos(0 + 0, 0 + 4, this.zLevel).tex(((0)), ((0))).endVertex();
	        worldrenderer.pos(0 + 4, 0 + 4, this.zLevel).tex(((1)), ((0))).endVertex();
	        worldrenderer.pos(0 + 4, 0 + 0, this.zLevel).tex(((1)), ((1))).endVertex();
	        worldrenderer.pos(0 + 0, 0 + 0, this.zLevel).tex(((0)), ((1))).endVertex();
	        tessellator.draw();
        }
        GlStateManager.popMatrix();
        
        //Moon
        GlStateManager.pushMatrix();
        GlStateManager.translate(2+(76*2), 23, 0);
        GlStateManager.scale(18.3, 18, 0);
		mc.getTextureManager().bindTexture(locationMoonPhasesPng);
        int moonPhase = CrystalMod.proxy.getClientWorld().getMoonPhase();
        int k = moonPhase % 4;
        int i1 = moonPhase / 4 % 2;
        float f22 = (k + 0) / 4.0F;
        float f23 = (i1 + 0) / 2.0F;
        float f24 = (k + 1) / 4.0F;
        float f14 = (i1 + 1) / 2.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0 + 0, 0 + 4, this.zLevel).tex(((f24)), ((f14))).endVertex();
        worldrenderer.pos(0 + 4, 0 + 4, this.zLevel).tex(((f22)), ((f14))).endVertex();
        worldrenderer.pos(0 + 4, 0 + 0, this.zLevel).tex(((f22)), ((f23))).endVertex();
        worldrenderer.pos(0 + 0, 0 + 0, this.zLevel).tex(((f24)), ((f23))).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        
        
        boolean clearInfo = weather.clearTime > 0;
        int offset = clearInfo ? 0 : -16;
        
        //Info
        GlStateManager.pushMatrix();
        if(clearInfo){
        	IconRenderer.renderIcon(Icon.WEATHER_SUNNY, 5, 102);
        }else{        
	        if(!info.isRaining()){
	        	IconRenderer.renderIcon(Icon.WEATHER_RAIN, 5, 102);
	        } else {
	        	IconRenderer.renderIcon(Icon.WEATHER_SUNNY, 5, 102);
	        }
	        IconRenderer.renderIcon(Icon.WEATHER_STORM, 5, 120);
        }
        
        GlStateManager.popMatrix();
        
        if(clearInfo){
        	String clearWheaterTime = TimeUtil.getTimeFromTicks(weather.clearTime);
        	fontRendererObj.drawString("Clear Weather: "+clearWheaterTime, 25, 107, java.awt.Color.GRAY.getRGB());
        } else {	        
	        String timeUntilNextRain = TimeUtil.getTimeFromTicks(weather.rainTime);
	        if(info.isRaining()){
	        	fontRendererObj.drawString(timeUntilNextRain, 25, 107, java.awt.Color.BLUE.getRGB());
	        } else {
	        	fontRendererObj.drawString(timeUntilNextRain, 25, 107, java.awt.Color.BLUE.getRGB());
	        }
	        
	        String timeUntilNextThunder = TimeUtil.getTimeFromTicks(weather.thunderTime);
	        if(info.isThundering()){
	        	fontRendererObj.drawString("Thunder Ends: "+timeUntilNextThunder, 25, 123, java.awt.Color.YELLOW.getRGB());
	        } else {
	        	fontRendererObj.drawString(timeUntilNextThunder, 25, 123, java.awt.Color.YELLOW.getRGB());
	        }
        }
        
        if(moonPhase > 0){
        	int daysLeft = 8-moonPhase;
        	int day = (int) (weather.getWorld().getWorldTime() % 24000L);
        	int timeLeft = ((daysLeft) * 24000) - (day);
        	String timeUntilFullMoon = TimeUtil.getTimeFromTicks(timeLeft);
            fontRendererObj.drawString("Next Full Moon: "+timeUntilFullMoon, 25, 165+offset, java.awt.Color.GRAY.getRGB());
        }
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    GuiUtil.renderBlankGuiBackground(sx, sy, xSize, ySize);
	}

}
