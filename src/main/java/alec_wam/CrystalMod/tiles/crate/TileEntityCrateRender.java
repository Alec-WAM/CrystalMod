package alec_wam.CrystalMod.tiles.crate;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("deprecation")
public class TileEntityCrateRender extends TileEntityRenderer<TileEntityCrate>
{
    public TileEntityCrateRender()
    {
        
    }

    @Override
    public void render(TileEntityCrate tile, double x, double y, double z, float partialTicks, int destroyStage)
    {
    	if(tile == null)return;
		
		if(ItemStackTools.isValid(tile.getStack())){
			GlStateManager.pushMatrix();

			GlStateManager.translated(x+0.5, y+0.5, z+0.5);
			
			GlStateManager.pushMatrix();
			IBlockState state = tile.getBlockState();
			EnumFacing facing = state.get(BlockCrate.FACING);
	        int ambLight = getWorld().getCombinedLight(tile.getPos().offset(facing), 0);
	        int lu = ambLight % 65536;
	        int lv = ambLight / 65536;
	        OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, lu / 1.0F, lv / 1.0F);
			boolean block = tile.getStack().getItem() instanceof ItemBlock;
			float scale = 0.5f;
			GlStateManager.scalef(scale, scale, scale);
			
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
			GlStateManager.rotatef(90*multi, 0, 1, 0);
			GlStateManager.rotatef(90*multiY, 1, 0, 0);
			
			GlStateManager.rotatef(90 * tile.rotation, 0, 0, 1);
			
			GlStateManager.translated(0, 0, 1.03);

			boolean gui = !ModConfig.BLOCKS.Crate_3D_Items.get();
			GlStateManager.pushMatrix();
			if(!gui){
				GlStateManager.rotatef(180, 0, 1, 0);
				if(block){
					IBakedModel model = Minecraft.getInstance().getItemRenderer().getModelWithOverrides(tile.getStack());
					//Don't scale up item type block models because it blocks the stack size text
					if(model.isGui3d()){
						GlStateManager.scaled(1.3, 1.3, 1.3);
					}
				} 
			}
			else{
				if(block){
					GlStateManager.scaled(0.8, 0.8, 0.001f);
				}
				else{
					GlStateManager.translated(0, 0.01, 0.0f);
				}
			}
			ItemStack renderStack = ItemUtil.copy(tile.getStack(), 1);
			if(gui){
				RenderUtil.renderItem(renderStack, TransformType.GUI);
			} else {
				RenderUtil.renderItem(renderStack, TransformType.FIXED);
			}
			GlStateManager.popMatrix();
			
			boolean renderVoid = tile.hasVoidUpgrade;
			if(renderVoid){
				GlStateManager.pushMatrix();
				GlStateManager.rotatef(180, 1, 0, 0);
				GlStateManager.translated(0, 0, 0.025);
				double qX = -1;
                int w = 2;
                double qY = -1;
                int h = 2;
                double qZ = 0;
                Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                Tessellator tessellator = Tessellator.getInstance();
                TextureAtlasSprite sprite = RenderUtil.getSprite("crystalmod:block/crate/void");
                BufferBuilder worldrenderer = tessellator.getBuffer();
                worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                worldrenderer.pos(qX + 0, qY + 0, qZ).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
                worldrenderer.pos(qX + 0, qY + h, qZ).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
                worldrenderer.pos(qX + w, qY + h, qZ).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
                worldrenderer.pos(qX + w, qY + 0, qZ).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
			}
			
			
			if (renderStack.getItem().showDurabilityBar(renderStack))
            {
				GlStateManager.pushMatrix();
				GlStateManager.scalef( 1.0f / 62f, 1.0f / 62f, 1.0f );
				GlStateManager.rotatef(180, 1, 0, 0);
				GlStateManager.scalef( 3, 3, 1.0f );
				GlStateManager.translated(-9, -3.4f, 0.01f );
                double health = renderStack.getItem().getDurabilityForDisplay(renderStack);
                int j1 = (int)Math.round(13.0D - health * 13.0D);
                int k = (int)Math.round(255.0D - health * 255.0D);
                GlStateManager.disableLighting();
                //GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlphaTest();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder worldrenderer = tessellator.getBuffer();
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
                GlStateManager.enableAlphaTest();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                //GlStateManager.enableDepth();
                GlStateManager.popMatrix();
            }
			
			GlStateManager.pushMatrix();
			FontRenderer fontRender = Minecraft.getInstance().fontRenderer;
			String info = tile.getStack().getDisplayName().getFormattedText();
			
			GlStateManager.scalef( 1.0f / 62f, 1.0f / 62f, 1.0f );
			GlStateManager.rotatef(180, 1, 0, 0);
			GlStateManager.disableLighting();
				
			boolean textBackground = true;
			
			GlStateManager.pushMatrix();
			int width = fontRender.getStringWidth(info);
			GlStateManager.translated(-0.20, -50f, 0f );
			float scale2 = Math.min(100F / (width+10), 1.2F);
	        GlStateManager.scalef(scale2, scale2, 1);
	        GlStateManager.translated(-width/2, fontRender.FONT_HEIGHT*(1.0f-scale), 0);
	        if(textBackground){
		        GlStateManager.disableTexture2D();
	            Tessellator tessellator = Tessellator.getInstance();
	            BufferBuilder worldrenderer = tessellator.getBuffer();
	            double qX = -1;
	            int w = width + 1;
	            double qY = -1;
	            int h = 10;
	            double qZ = 0;
	            int r = 105;
	            int g = 84; 
	            int b = 51;
	            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
	            worldrenderer.pos(qX + 0, qY + 0, qZ).color(r, g, b, 255).endVertex();
	            worldrenderer.pos(qX + 0, qY + h, qZ).color(r, g, b, 255).endVertex();
	            worldrenderer.pos(qX + w, qY + h, qZ).color(r, g, b, 255).endVertex();
	            worldrenderer.pos(qX + w, qY + 0, qZ).color(r, g, b, 255).endVertex();
	            tessellator.draw();
	            GlStateManager.enableTexture2D();
			}
	        GlStateManager.translated(0, 0, -0.0001f);
	        fontRender.drawString(info, 0, 0, 0 );
			GlStateManager.popMatrix();
			
			
			int stackSize = ItemStackTools.getStackSize(tile.getStack());
			if(stackSize > 1){
				GlStateManager.pushMatrix();
				int maxStack = tile.getStack().getMaxStackSize();
				boolean basicSize = maxStack == 1 || stackSize < maxStack;
				if(basicSize){
					info = ""+stackSize;
				} else {
					int stacks = stackSize / maxStack;
					int rem = stackSize % maxStack;
					info = stacks + "x" + maxStack + (rem > 0 ? " + " + rem : "");
				}				
				
				width = fontRender.getStringWidth(info);
				GlStateManager.translated(-0.20, 30f, 0f);
				scale2 = Math.min(100F / (width+10), 1.2F);
		        GlStateManager.scalef(scale2, scale2, 1);
		        GlStateManager.translated(-width/2, fontRender.FONT_HEIGHT*(1.0f-scale), 0);
		        if(textBackground){
			        GlStateManager.disableTexture2D();
		            Tessellator tessellator = Tessellator.getInstance();
		            BufferBuilder worldrenderer = tessellator.getBuffer();
		            double qX = -1;
		            int w = width + 1;
		            double qY = -1;
		            int h = 10;
		            double qZ = 0;
		            int r = 105;
		            int g = 84; 
		            int b = 51;
		            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		            worldrenderer.pos(qX + 0, qY + 0, qZ).color(r, g, b, 255).endVertex();
		            worldrenderer.pos(qX + 0, qY + h, qZ).color(r, g, b, 255).endVertex();
		            worldrenderer.pos(qX + w, qY + h, qZ).color(r, g, b, 255).endVertex();
		            worldrenderer.pos(qX + w, qY + 0, qZ).color(r, g, b, 255).endVertex();
		            tessellator.draw();
		            GlStateManager.enableTexture2D();
				}
		        GlStateManager.translated(0, 0, -0.0001f);
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
