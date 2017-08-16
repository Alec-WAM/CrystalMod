package alec_wam.CrystalMod.tiles.crate;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class RenderTileCrate<T extends TileCrate> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileCrate tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null)return;
		
		if(ItemStackTools.isValid(tile.getStack())){
			GlStateManager.pushMatrix();

			GlStateManager.translate(x+0.5, y+0.5, z+0.5);
			
			GlStateManager.pushMatrix();
			EnumFacing facing = tile.facing;
	        int ambLight = getWorld().getCombinedLight(tile.getPos().offset(facing), 0);
	        int lu = ambLight % 65536;
	        int lv = ambLight / 65536;
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lu / 1.0F, lv / 1.0F);
			boolean block = tile.getStack().getItem() instanceof ItemBlock;
			float scale = 0.5f;
			GlStateManager.scale(scale, scale, scale);
			
			int multi = 0;
			int multiY = 0;
			if(facing == EnumFacing.NORTH){
				multi = 2;
			}
			if(facing == EnumFacing.EAST){
				multi = 1;
			}
			if(facing == EnumFacing.WEST){
				multi = 3;
			}
			if(facing == EnumFacing.UP){
				multiY = 3;
			}
			if(facing == EnumFacing.DOWN){
				multiY = 1;
			}
			GlStateManager.rotate(90*multi, 0, 1, 0);
			GlStateManager.rotate(90*multiY, 1, 0, 0);
			GlStateManager.translate(0, 0, 1.03);

			GlStateManager.pushMatrix();
			if(block){
				GlStateManager.scale(1.3, 1.3, 1.3);
				GlStateManager.rotate(180, 0, 1, 0);
				//GlStateManager.translate(0, 0, 0.1);
			}
			ItemStack renderStack = ItemUtil.copy(tile.getStack(), 1);
			RenderUtil.renderItem(renderStack, TransformType.FIXED);
			GlStateManager.popMatrix();
			
			
			if (renderStack.getItem().showDurabilityBar(renderStack))
            {
				GlStateManager.pushMatrix();
				GlStateManager.scale( 1.0f / 62f, 1.0f / 62f, 1.0f );
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.scale( 3, 3, 1.0f );
				GlStateManager.translate(-9, -3.4f, 0.01f );
                double health = renderStack.getItem().getDurabilityForDisplay(renderStack);
                int j1 = (int)Math.round(13.0D - health * 13.0D);
                int k = (int)Math.round(255.0D - health * 255.0D);
                GlStateManager.disableLighting();
                //GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer worldrenderer = tessellator.getBuffer();
                double qX = 2;
                int w = 13;
                double qY = 13;
                int h = 2;
                double qZ = 0;
                worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(qX + 0, qY + 0, qZ).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(qX + 0, qY + h, qZ).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(qX + w, qY + h, qZ).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(qX + w, qY + 0, qZ).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                w = 12;
                h = 1;
                qZ = -0.0001;
                worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(qX + 0, qY + 0, qZ).color((255 - k) / 4, 64, 0, 255).endVertex();
                worldrenderer.pos(qX + 0, qY + h, qZ).color((255 - k) / 4, 64, 0, 255).endVertex();
                worldrenderer.pos(qX + w, qY + h, qZ).color((255 - k) / 4, 64, 0, 255).endVertex();
                worldrenderer.pos(qX + w, qY + 0, qZ).color((255 - k) / 4, 64, 0, 255).endVertex();
                tessellator.draw();
                w = j1;
                qZ = -0.0002;
                worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(qX + 0, qY + 0, qZ).color(255 - k, k, 0, 255).endVertex();
                worldrenderer.pos(qX + 0, qY + h, qZ).color(255 - k, k, 0, 255).endVertex();
                worldrenderer.pos(qX + w, qY + h, qZ).color(255 - k, k, 0, 255).endVertex();
                worldrenderer.pos(qX + w, qY + 0, qZ).color(255 - k, k, 0, 255).endVertex();
                tessellator.draw();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                //GlStateManager.enableDepth();
                GlStateManager.popMatrix();
            }
			
			GlStateManager.pushMatrix();
			FontRenderer fontRender = Minecraft.getMinecraft().fontRendererObj;
			String info = tile.getStack().getDisplayName();
			
			GlStateManager.scale( 1.0f / 62f, 1.0f / 62f, 1.0f );
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.disableLighting();
			
			GlStateManager.pushMatrix();
			int width = fontRender.getStringWidth(info);
			GlStateManager.translate(-0.20, -50f, 0f );
			float scale2 = Math.min(100F / (width+10), 1.2F);
	        GlStateManager.scale(scale2, scale2, 1);
	        GlStateManager.translate(-width/2, fontRender.FONT_HEIGHT*(1.0f-scale), 0);
			fontRender.drawString( info, 0, 0, 0 );
			GlStateManager.popMatrix();
			
			
			int stackSize = ItemStackTools.getStackSize(tile.getStack());
			if(stackSize > 1){
				GlStateManager.pushMatrix();
				info = ""+stackSize;
				width = fontRender.getStringWidth(info);
				GlStateManager.translate(-0.20, 30f, 0f);
				scale2 = Math.min(100F / (width+10), 1.2F);
		        GlStateManager.scale(scale2, scale2, 1);
		        GlStateManager.translate(-width/2, fontRender.FONT_HEIGHT*(1.0f-scale), 0);
				fontRender.drawString( info, 0, 0, 0 );
				GlStateManager.popMatrix();
			}
			
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();

			GlStateManager.popMatrix();
			
			GlStateManager.popMatrix();
		}
	}
}