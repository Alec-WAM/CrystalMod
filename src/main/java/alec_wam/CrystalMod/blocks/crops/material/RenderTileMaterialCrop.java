package alec_wam.CrystalMod.blocks.crops.material;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class RenderTileMaterialCrop<T extends TileMaterialCrop> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileMaterialCrop tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null || tile.getCrop() == null)return;
		tile.getWorld().theProfiler.startSection("crystalmod-materialcrop");
		
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(x, y, z);
		int timeLeft = tile.getGrowthTime();
		
		double percent = (timeLeft * 100) / tile.getCrop().getGrowthTime(tile.getWorld(), tile.getPos());
		
		double logic = percent * 0.01;
		double scale = Math.max(logic, 0.2);
		
		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(-0.5, 0, -0.5);
		renderPlant(tile.getWorld(), tile.getPos(), tile.getCrop(), tile.isGrown() ? tile.getCropYield() : 0, destroyState);
		GlStateManager.popMatrix();
		tile.getWorld().theProfiler.endSection();
	}
	
	public void renderPlant(@Nullable IBlockAccess world, @Nullable BlockPos pos, IMaterialCrop crop, int cropCount, int destroyState){
		if(crop == null) return;
		
		if (destroyState >= 0)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(DESTROY_STAGES[destroyState]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();
		int l = 0;
        float f = (l >> 16 & 255) / 255.0F;
        float f1 = (l >> 8 & 255) / 255.0F;
        float f2 = (l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }
        
        IPlantType plant = crop.getPlantType();
        
        int k2 = (world == null || pos == null) ? 0 : world.getCombinedLight(pos, 0);
        int l2 = k2 >> 16 & 65535;
        int i3 = k2 & 65535;
        
        TextureAtlasSprite spriteRoots = RenderUtil.getSprite(plant.getRoot());
        /*double d7 = 0.45D * (double)1.0F;
        double d8 = 0.5D - d7;
        double d9 = 0.5D + d7;
        double d10 = 0.5D - d7;
        double d11 = 0.5D + d7;*/
        
        double d3 = spriteRoots.getMinU();
        double d4 = spriteRoots.getMinV();
        double d5 = spriteRoots.getMaxU();
        double d6 = spriteRoots.getMaxV();
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        buffer.pos(0, 0, 0.5).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0, 1, 0.5).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(1, 1, 0.5).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(1, 0, 0.5).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
		
        buffer.pos(1, 0, 0.5).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(1, 1, 0.5).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0, 1, 0.5).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0, 0, 0.5).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        tessellator.draw();
        
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        buffer.pos(0.5, 0, 1).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0.5, 1, 1).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0.5, 1, 0).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0.5, 0, 0).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
		
        buffer.pos(0.5, 0, 0).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0.5, 1, 0).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0.5, 1, 1).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        buffer.pos(0.5, 0, 1).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, 1.0f).endVertex();
        tessellator.draw();
        
        int pass = 0;
        for(ResourceLocation res : plant.getSpites()){
        	TextureAtlasSprite sprite = RenderUtil.getSprite(res);
        	int colorVec = crop.getPlantColor(world, pos, pass);
	        f = (colorVec >> 16 & 255) / 255.0F;
	        f1 = (colorVec >> 8 & 255) / 255.0F;
	        f2 = (colorVec & 255) / 255.0F;
            float alpha = 1;
            
            d3 = sprite.getMinU();
            d4 = sprite.getMinV();
            d5 = sprite.getMaxU();
            d6 = sprite.getMaxV();
            if (EntityRenderer.anaglyphEnable)
            {
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }
            
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(0, 0, 0.5).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0, 1, 0.5).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(1, 1, 0.5).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(1, 0, 0.5).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
    		
            buffer.pos(1, 0, 0.5).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(1, 1, 0.5).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0, 1, 0.5).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0, 0, 0.5).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            tessellator.draw();
            
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(0.5, 0, 1).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0.5, 1, 1).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0.5, 1, 0).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0.5, 0, 0).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
    		
            buffer.pos(0.5, 0, 0).tex(d3, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0.5, 1, 0).tex(d3, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0.5, 1, 1).tex(d5, d4).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            buffer.pos(0.5, 0, 1).tex(d5, d6).lightmap(l2, i3).color(f, f1, f2, alpha).endVertex();
            tessellator.draw();
            
            pass++;
        }
        
        if(cropCount > 0){
        	ItemStack renderStack = crop.getRenderStack(world, pos);
        	for(int s = 0; s < cropCount; s++){
        		GlStateManager.pushMatrix();
        		if(s == 0)GlStateManager.translate(0.7, 0.8, 0.5);
        		if(s == 1)GlStateManager.translate(0.3, 0.8, 0.5);
        		if(s == 2)GlStateManager.translate(0.5, 0.65, 0.5);
        		if(s == 3)GlStateManager.translate(0.7, 0.5, 0.5);
        		if(s == 4)GlStateManager.translate(0.3, 0.5, 0.5);
        		GlStateManager.scale(0.3, 0.3, 0.3);
        		RenderUtil.renderItem(renderStack, TransformType.GROUND);
        		GlStateManager.popMatrix();
        		
        		/*GlStateManager.pushMatrix();
        		GlStateManager.translate(0.5, 0, 0.5);
        		GlStateManager.rotate(180, 0, 1, 0);
        		GlStateManager.translate(-0.5, 0, -0.5);
        		if(s == 0)GlStateManager.translate(0.7, 0.8, 0.5);
        		if(s == 1)GlStateManager.translate(0.3, 0.8, 0.5);
        		if(s == 2)GlStateManager.translate(0.5, 0.65, 0.5);
        		if(s == 3)GlStateManager.translate(0.7, 0.5, 0.5);
        		if(s == 4)GlStateManager.translate(0.3, 0.5, 0.5);
        		GlStateManager.scale(0.3, 0.3, 0.3);
        		RenderUtil.renderItem(renderStack, TransformType.GROUND);
        		GlStateManager.popMatrix();*/
        	}
        }

		if (destroyState >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
		GlStateManager.popMatrix();
	}
}