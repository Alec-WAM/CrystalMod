package alec_wam.CrystalMod.tiles.fusion;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.handler.ClientEventHandler;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class RenderTileFusionPedistal<T extends TileFusionPedistal> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileFusionPedistal tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null)return;
		GlStateManager.pushMatrix();
		tile.getWorld().theProfiler.startSection("crystalmod-fusionpedistal");
		EnumFacing facing = tile.getRotation();
		float liftDistance = 0.6f;
		float itemLift = 0.0f;
		float lazerLift = 0.0f;
		boolean hasItem = ItemStackTools.isValid(tile.getStack());
		if(tile.craftingCooldown.getValue() > 0 && hasItem){
        	int remaining = (tile.craftingCooldown.getValue());
        	itemLift = (liftDistance) * (remaining / 100f);
        	float scale = tile.getStack().getItem() instanceof ItemBlock ? 0.1f : 0.25f;
        	lazerLift = (liftDistance-scale) * (remaining / 100f);
		}

        if(hasItem){
        	GlStateManager.pushMatrix();
        	GlStateManager.translate(x, y, z);
	        GlStateManager.translate(0.5 + (facing.getFrontOffsetX() * 0.45), 0.5 + (facing.getFrontOffsetY() * 0.45), 0.5 + (facing.getFrontOffsetZ() * 0.45));
	        float scale = tile.getStack().getItem() instanceof ItemBlock ? 0.65f : 0.5f;
	        
	        GlStateManager.scale(scale, scale, scale);
	        if (facing.getAxis() == EnumFacing.Axis.Y){
	            if (facing == EnumFacing.DOWN){
	                GlStateManager.rotate(180, 1, 0, 0);
	            }
	        }
	        else {
	            GlStateManager.rotate(90, facing.getFrontOffsetZ(), 0, facing.getFrontOffsetX() * -1);
	        }
	        GlStateManager.pushMatrix();
	        float speed = 0.8f;
	        if(tile.craftingCooldown.getValue() > 0){
	        	speed = 50f;
	        	GlStateManager.translate(0, itemLift, 0);
	        	int remaining = (tile.craftingCooldown.getValue());
	        	double offset = 1.0d + (0.5*(remaining / 100f));
	        	GlStateManager.scale(offset, offset, offset);
	        }
	        
	        
	        GlStateManager.rotate((((ClientEventHandler.elapsedTicks * speed) + partialTicks)) % 360, 0F, 1F, 0F);
	        RenderUtil.renderItem(tile.getStack(), TransformType.FIXED);
	        GlStateManager.popMatrix();

	    	
	        boolean renderFancyEffect = tile.craftingCooldown.getValue() > 0;
	        if(renderFancyEffect){
	        	Tessellator tessellator = Tessellator.getInstance();
	            VertexBuffer vertexbuffer = tessellator.getBuffer();
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
	            GlStateManager.disableTexture2D();
	            GlStateManager.shadeModel(7425);
	            GlStateManager.enableBlend();
	            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
	            GlStateManager.disableAlpha();
	            GlStateManager.enableCull();
	            GlStateManager.depthMask(false);
	            GlStateManager.pushMatrix();
	            
	            GlStateManager.translate(0, itemLift, 0);
	            GlStateManager.scale(0.08, 0.08, 0.08);
	            
            	GlStateManager.rotate((float)(((ClientEventHandler.elapsedTicks * 2))) % 360, 0, 1, 0);

	            for (int i = 0; i < (f + f * f) / 2.0F * 60.0F; ++i)
	            {
	                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
	                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
	                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
	                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
	                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
	                GlStateManager.rotate(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
	                float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
	                float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
	                int r = 255, g = 255, b = 255;
	                
	                if(tile.runningRecipe !=null){
	    				Vec3d colorVec = tile.runningRecipe.getRecipeColor();
	    				if(colorVec !=null){
	    					r = (int)colorVec.xCoord;
	    					g = (int)colorVec.yCoord;
	    					b = (int)colorVec.zCoord;
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
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	            GlStateManager.enableTexture2D();
	            GlStateManager.enableAlpha();
	            RenderHelper.enableStandardItemLighting();
	        }
	        
	        GlStateManager.popMatrix();
        }
		
		if(!tile.linkedPedistals.isEmpty() && tile.isCrafting.getValue() && hasItem){
			Vec3d masterVec = new Vec3d(tile.getPos());
			
			masterVec = masterVec.addVector(facing.getFrontOffsetX() * 0.35, facing.getFrontOffsetY() * 0.35, facing.getFrontOffsetZ() * 0.35);
			
			Color color = Color.RED.darker().darker();
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			boolean renderLaser = false;
			if(tile.runningRecipe !=null){
				Vec3d colorVec = tile.runningRecipe.getRecipeColor();
				if(colorVec !=null){
					r = (int)colorVec.xCoord;
					g = (int)colorVec.yCoord;
					b = (int)colorVec.zCoord;
				}
			}
			
			
			
			double progress = (double)tile.craftingProgress.getValue() / (double)200;
			List<IPedistal> lazerList = Lists.newArrayList();
			for(IPedistal pedistal : tile.linkedPedistals){
				if(ItemStackTools.isEmpty(pedistal.getStack()))continue;
				TileEntity pedistalTile = (TileEntity)pedistal;
				Vec3d pedistalVec = new Vec3d(pedistalTile.getPos());
				if(pedistal.getRotation() !=null)pedistalVec = pedistalVec.addVector(pedistal.getRotation().getFrontOffsetX() * 0.35, pedistal.getRotation().getFrontOffsetY() * 0.35, pedistal.getRotation().getFrontOffsetZ() * 0.35);
				
				if(ItemStackTools.isValid(pedistal.getStack())){
					Vec3d vec1 = masterVec.addVector(0.5, 0.5+lazerLift, 0.5);
			        Vec3d vec2 = pedistalVec.addVector(0.5, 0.5, 0.5);
			        Vec3d combinedVec = vec2.subtract(vec1);
			        GlStateManager.pushMatrix();
			        GlStateManager.translate(vec2.xCoord-TileEntityRendererDispatcher.staticPlayerX, vec2.yCoord-TileEntityRendererDispatcher.staticPlayerY, vec2.zCoord-TileEntityRendererDispatcher.staticPlayerZ);
			        double pitch = Math.atan2(combinedVec.yCoord, Math.sqrt(combinedVec.xCoord*combinedVec.xCoord+combinedVec.zCoord*combinedVec.zCoord));
			        double yaw = Math.atan2(-combinedVec.zCoord, combinedVec.xCoord);
			        GlStateManager.rotate((float)(180*yaw/Math.PI), 0, 1, 0);
			        GlStateManager.rotate((float)(180*pitch/Math.PI), 0, 0, 1);
			        double distance = combinedVec.lengthVector();
			        
			        GlStateManager.translate(-distance*(progress), 0, 0);
			        float scale = pedistal.getStack().getItem() instanceof ItemBlock ? 0.65f : 0.5f;
			        GlStateManager.scale(scale, scale, scale);
			        RenderUtil.renderItem(pedistal.getStack(), TransformType.FIXED);
			        GlStateManager.popMatrix();
				}
				if(renderLaser)lazerList.add(pedistal);
			}
			for(IPedistal pedistal : lazerList){
				TileEntity pedistalTile = (TileEntity)pedistal;
				Vec3d pedistalVec = new Vec3d(pedistalTile.getPos());
				if(pedistal.getRotation() !=null)pedistalVec = pedistalVec.addVector(pedistal.getRotation().getFrontOffsetX() * 0.35, pedistal.getRotation().getFrontOffsetY() * 0.35, pedistal.getRotation().getFrontOffsetZ() * 0.35);
				renderBeam(masterVec.xCoord+0.5, masterVec.yCoord+0.5+lazerLift, masterVec.zCoord+0.5, pedistalVec.xCoord+0.5, pedistalVec.yCoord+0.5, pedistalVec.zCoord+0.5, (int)(50*progress), partialTicks, 0.1d, r, g, b, 1F);
			}
		}
		tile.getWorld().theProfiler.endSection();
		GlStateManager.popMatrix();
	}
	
	//Copied from RenderDragon (added color)
	public static void renderBeam(double x1, double y1, double z1, double x2, double y2, double z2, int tick, float partialTicks, double beamWidth, int r, int g, int b, float alpha)
    {
		Tessellator tessy = Tessellator.getInstance();
        VertexBuffer render = tessy.getBuffer();

        Vec3d vec1 = new Vec3d(x1, y1, z1);
        Vec3d vec2 = new Vec3d(x2, y2, z2);
        Vec3d combinedVec = vec2.subtract(vec1);

        double rot = tick > 0 ? ((ClientEventHandler.elapsedTicks + partialTicks) * (tick)) % 360 : 0;
        double pitch = Math.atan2(combinedVec.yCoord, Math.sqrt(combinedVec.xCoord*combinedVec.xCoord+combinedVec.zCoord*combinedVec.zCoord));
        double yaw = Math.atan2(-combinedVec.zCoord, combinedVec.xCoord);

        double length = combinedVec.lengthVector();

        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
        GlStateManager.translate(x1-TileEntityRendererDispatcher.staticPlayerX, y1-TileEntityRendererDispatcher.staticPlayerY, z1-TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.rotate((float)(180*yaw/Math.PI), 0, 1, 0);
        GlStateManager.rotate((float)(180*pitch/Math.PI), 0, 0, 1);
        GlStateManager.rotate((float)rot, 1, 0, 0);
        GlStateManager.disableTexture2D();
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

        GlStateManager.enableTexture2D();

        GlStateManager.alphaFunc(func, ref);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileFusionPedistal tile){
        return true;
    }
}