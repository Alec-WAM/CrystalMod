package alec_wam.CrystalMod.tiles.fusion;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import alec_wam.CrystalMod.api.tile.IPedestal;
import alec_wam.CrystalMod.client.ClientEventHandler;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("deprecation")
public class TileEntityFusionPedestalRender extends TileEntityRenderer<TileEntityFusionPedestal> {

	@Override
	public void render(TileEntityFusionPedestal tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null)return;
		GlStateManager.pushMatrix();
		tile.getWorld().getProfiler().startSection("crystalmod-fusionpedestal");
		Direction facing = tile.getRotation();
		float liftDistance = 0.6f;
		float itemLift = 0.0f;
		float lazerLift = 0.0f;
		boolean hasItem = ItemStackTools.isValid(tile.getStack());
		if(tile.craftingCooldown.getValue() > 0 && hasItem){
        	int remaining = (tile.craftingCooldown.getValue());
        	itemLift = (liftDistance) * (remaining / 100f);
        	lazerLift = itemLift;
		}

        if(hasItem){
        	GlStateManager.pushMatrix();
        	GlStateManager.translated(x, y, z);
	        GlStateManager.translated(0.5 + (facing.getXOffset() * 0.45), 0.4 + (facing.getYOffset() * 0.45), 0.5 + (facing.getZOffset() * 0.45));
	        float scale = tile.getStack().getItem() instanceof BlockItem ? 0.65f : 0.5f;
	        
	        GlStateManager.scaled(scale, scale, scale);
	        if (facing.getAxis() == Direction.Axis.Y){
	            if (facing == Direction.DOWN){
	                GlStateManager.rotatef(180, 1, 0, 0);
	            }
	        }
	        else {
	            GlStateManager.rotatef(90, facing.getZOffset(), 0, facing.getXOffset() * -1);
	        }
	        GlStateManager.pushMatrix();
	        float speed = 0.8f;
	        if(tile.craftingCooldown.getValue() > 0){
	        	speed = 50f;
	        	GlStateManager.translated(0, itemLift, 0);
	        	int remaining = (tile.craftingCooldown.getValue());
	        	double offset = 1.0d + (0.5*(remaining / 100f));
	        	GlStateManager.scaled(offset, offset, offset);
	        }
	        
	        
	        GlStateManager.rotatef((((ClientEventHandler.elapsedTicks * speed) + partialTicks)) % 360, 0F, 1F, 0F);
	        RenderUtil.renderItem(tile.getStack(), TransformType.FIXED);
	        GlStateManager.popMatrix();

	    	
	        boolean renderFancyEffect = tile.craftingCooldown.getValue() > 0;
	        if(renderFancyEffect){
	        	Tessellator tessellator = Tessellator.getInstance();
	            BufferBuilder vertexbuffer = tessellator.getBuffer();
	            RenderHelper.disableStandardItemLighting();
	            float f = (tile.craftingCooldown.getValue()) / 100.0F;
	            if(f == 1.0F){
	            	f = 0.98f;
	            }
	            
	            float f1 = 0.0F;

	            if (f > 0.8F)
	            {
	                f1 = (f - 0.8F) / 0.2F;
	            }

	            Random random = new Random(432L);
	            GlStateManager.disableTexture();
	            GlStateManager.shadeModel(7425);
	            GlStateManager.enableBlend();
	            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
	            GlStateManager.disableAlphaTest();
	            GlStateManager.enableCull();
	            GlStateManager.depthMask(false);
	            GlStateManager.pushMatrix();
	            
	            GlStateManager.translated(0, itemLift, 0);
	            GlStateManager.scaled(0.08, 0.08, 0.08);
	            
            	GlStateManager.rotatef((float)(((ClientEventHandler.elapsedTicks * 2))) % 360, 0, 1, 0);

	            for (int i = 0; i < (f + f * f) / 2.0F * 60.0F; ++i)
	            {
	                GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
	                GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
	                GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
	                GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
	                GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
	                GlStateManager.rotatef(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
	                float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
	                float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
	                int r = 255, g = 255, b = 255;
	                
	                if(tile.runningRecipe !=null){
	    				Vec3d colorVec = tile.runningRecipe.getRecipeColor();
	    				if(colorVec !=null){
	    					r = (int)colorVec.x;
	    					g = (int)colorVec.y;
	    					b = (int)colorVec.z;
	    				}
	    			}
	                
	                float red = r/255F, green = g/255F, blue = b/255F;
	                float alpha = 0.0F;
	                vertexbuffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
	                vertexbuffer.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int)(255.0F * (1.0F - f1))).endVertex();
	                vertexbuffer.pos(-0.866D * f3, f2, -0.5F * f3).color(red, green, blue, alpha).endVertex();
	                vertexbuffer.pos(0.866D * f3, f2, -0.5F * f3).color(red, green, blue, alpha).endVertex();
	                vertexbuffer.pos(0.0D, f2, 1.0F * f3).color(red, green, blue, alpha).endVertex();
	                vertexbuffer.pos(-0.866D * f3, f2, -0.5F * f3).color(red, green, blue, alpha).endVertex();
	                tessellator.draw();
	            }

	            GlStateManager.popMatrix();
	            GlStateManager.depthMask(true);
	            GlStateManager.disableCull();
	            GlStateManager.disableBlend();
	            GlStateManager.shadeModel(7424);
	            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	            GlStateManager.enableTexture();
	            GlStateManager.enableAlphaTest();
	            RenderHelper.enableStandardItemLighting();
	        }
	        
	        GlStateManager.popMatrix();
        }
		
		if(!tile.linkedPedestals.isEmpty() && tile.isCrafting.getValue() && hasItem){
			Vec3d masterVec = new Vec3d(tile.getPos());
			
			masterVec = masterVec.add(facing.getXOffset() * 0.35, facing.getYOffset() * 0.35, facing.getZOffset() * 0.35);
			
			Color color = Color.RED.darker().darker();
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			boolean renderLaser = true;
			if(tile.runningRecipe !=null){
				Vec3d colorVec = tile.runningRecipe.getRecipeColor();
				if(colorVec !=null){
					r = (int)colorVec.x;
					g = (int)colorVec.y;
					b = (int)colorVec.z;
				}
			}			
			
			//Default 200
			int craftTime = 100;
			double progress = (double)tile.craftingProgress.getValue() / (double)craftTime;
			List<IPedestal> lazerList = Lists.newArrayList();
			for(IPedestal pedestal : tile.linkedPedestals){
				if(ItemStackTools.isEmpty(pedestal.getStack()))continue;
				TileEntity pedestalTile = (TileEntity)pedestal;
				Vec3d pedestalVec = new Vec3d(pedestalTile.getPos());
				if(pedestal.getRotation() !=null)pedestalVec = pedestalVec.add(pedestal.getRotation().getXOffset() * 0.35, pedestal.getRotation().getYOffset() * 0.35, pedestal.getRotation().getZOffset() * 0.35);
				
				if(ItemStackTools.isValid(pedestal.getStack())){
					Vec3d vec1 = masterVec.add(0.5, 0.4+lazerLift, 0.5);
			        Vec3d vec2 = pedestalVec.add(0.5, 0.5, 0.5);
			        Vec3d combinedVec = vec2.subtract(vec1);
			        GlStateManager.pushMatrix();
			        GlStateManager.translated(vec2.x-TileEntityRendererDispatcher.staticPlayerX, vec2.y-TileEntityRendererDispatcher.staticPlayerY, vec2.z-TileEntityRendererDispatcher.staticPlayerZ);
			        double pitch = Math.atan2(combinedVec.y, Math.sqrt(combinedVec.x*combinedVec.x+combinedVec.z*combinedVec.z));
			        double yaw = Math.atan2(-combinedVec.z, combinedVec.x);
			        GlStateManager.rotatef((float)(180*yaw/Math.PI), 0, 1, 0);
			        GlStateManager.rotatef((float)(180*pitch/Math.PI), 0, 0, 1);
			        double distance = combinedVec.length();
			        
			        GlStateManager.translated(-distance*(progress), 0, 0);
			        float scale = pedestal.getStack().getItem() instanceof BlockItem ? 0.5f : 0.5f;
			        GlStateManager.scaled(scale, scale, scale);
			        RenderUtil.renderItem(pedestal.getStack(), TransformType.FIXED);
			        GlStateManager.popMatrix();
				}
				if(renderLaser)lazerList.add(pedestal);
			}
			for(IPedestal pedestal : lazerList){
				TileEntity pedestalTile = (TileEntity)pedestal;
				Vec3d pedestalVec = new Vec3d(pedestalTile.getPos());
				if(pedestal.getRotation() !=null)pedestalVec = pedestalVec.add(pedestal.getRotation().getXOffset() * 0.35, pedestal.getRotation().getYOffset() * 0.35, pedestal.getRotation().getZOffset() * 0.35);
				renderBeam(masterVec.x+0.5, masterVec.y+0.4+lazerLift, masterVec.z+0.5, pedestalVec.x+0.5, pedestalVec.y+0.5, pedestalVec.z+0.5, (int)(50*progress), partialTicks, 0.1d, r, g, b, 0.3F);
			}
		}
		tile.getWorld().getProfiler().endSection();
		GlStateManager.popMatrix();
	}
	
	//Copied from RenderDragon (added color)
	public static void renderBeam(double x1, double y1, double z1, double x2, double y2, double z2, int tick, float partialTicks, double beamWidth, int r, int g, int b, float alpha)
    {
		Tessellator tessy = Tessellator.getInstance();
        BufferBuilder render = tessy.getBuffer();

        Vec3d vec1 = new Vec3d(x1, y1, z1);
        Vec3d vec2 = new Vec3d(x2, y2, z2);
        Vec3d combinedVec = vec2.subtract(vec1);

        double rot = tick > 0 ? ((ClientEventHandler.elapsedTicks + partialTicks) * (tick)) % 360 : 0;
        double pitch = Math.atan2(combinedVec.y, Math.sqrt(combinedVec.x*combinedVec.x+combinedVec.z*combinedVec.z));
        double yaw = Math.atan2(-combinedVec.z, combinedVec.x);

        double length = combinedVec.length();

        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
        GlStateManager.translated(x1-TileEntityRendererDispatcher.staticPlayerX, y1-TileEntityRendererDispatcher.staticPlayerY, z1-TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.rotatef((float)(180*yaw/Math.PI), 0, 1, 0);
        GlStateManager.rotatef((float)(180*pitch/Math.PI), 0, 0, 1);
        GlStateManager.rotatef((float)rot, 1, 0, 0);
        GlStateManager.disableTexture();
        render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        final int MAX_LIGHT_X = 0xF000F0;
        final int MAX_LIGHT_Y = 0xF000F0;
        
        float red = r/255F, green = g/255F, blue = b/255F;
        
        for(double i = 0; i < 4; i++){
            double width = beamWidth*(i/4.0);
            render.pos(length, width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, -width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(length, -width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();

            render.pos(length, -width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, -width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(length, width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();

            render.pos(length, width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(length, width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();

            render.pos(length, -width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, -width, width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(0, -width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
            render.pos(length, -width, -width).tex(0, 0).lightmap(MAX_LIGHT_X, MAX_LIGHT_Y).color(red, green, blue, alpha).endVertex();
        }
        tessy.draw();

        GlStateManager.enableTexture();

        GlStateManager.alphaFunc(func, ref);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityFusionPedestal tile){
        return true;
    }
}