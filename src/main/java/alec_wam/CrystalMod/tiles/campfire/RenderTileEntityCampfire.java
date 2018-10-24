package alec_wam.CrystalMod.tiles.campfire;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class RenderTileEntityCampfire extends TileEntitySpecialRenderer<TileEntityCampfire> {

	private final ItemStack STICK = new ItemStack(Items.STICK);
	@Override
    public void renderTileEntityAt(TileEntityCampfire te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		if(te.getBurnTime() > 0){
			TextureAtlasSprite sprite = RenderUtil.getTexture(Blocks.FIRE.getDefaultState());
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer renderer = tessellator.getBuffer();
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5, 0, 0.5);
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.translate(-0.5, 0, -0.5);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			double offset = 0.2;
			
			renderer.pos(offset, 0, 0.5).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
			renderer.pos(1 - offset, 0, 0.5).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
			renderer.pos(1 - offset, 1 - offset, 0.5).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
			renderer.pos(offset, 1 - offset, 0.5).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
			
			renderer.pos(1 - offset, 0, 0.5).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
			renderer.pos(offset, 0, 0.5).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
			renderer.pos(offset, 1 - offset, 0.5).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
			renderer.pos(1 - offset, 1 - offset, 0.5).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
			
			
			renderer.pos(0.5, 0, offset).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
			renderer.pos(0.5, 0, 1 - offset).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
			renderer.pos(0.5, 1 - offset, 1 - offset).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
			renderer.pos(0.5, 1 - offset, offset).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
			
			renderer.pos(0.5, 0, 1 - offset).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
			renderer.pos(0.5, 0, offset).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
			renderer.pos(0.5, 1 - offset, offset).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
			renderer.pos(0.5, 1 - offset, 1 - offset).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
			
			tessellator.draw();
			GlStateManager.popMatrix();
		}
		if(te.getStickCount() > 0){
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5, 0.0, 0.5);
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.scale(0.4, 0.4, 0.4);
			for(int i = 0; i < te.getStickCount(); i++){
				GlStateManager.pushMatrix();
				handleStickRotation(i);
				Minecraft.getMinecraft().getRenderItem().renderItem(STICK, ItemCameraTransforms.TransformType.FIXED);
				GlStateManager.popMatrix();
			}
			
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}
	
	private void handleStickRotation(int stickIndex){
		if(stickIndex < 4){
			GlStateManager.rotate((360 / 8) * stickIndex, 0, 0, 1);
			GlStateManager.translate(0.0, 0.0, -0.05 * stickIndex);
		}
		/*if(stickIndex > 2 && stickIndex < 16){
			GlStateManager.rotate((360 / 8) * stickIndex, 0, 0, 1);
			GlStateManager.rotate(20, 1, 1, 0);
			GlStateManager.translate(0.0, 0.0, -0.05 * stickIndex);
		}*/
		else if(stickIndex > 3 && stickIndex < 6){
			GlStateManager.rotate(65 - ((360 / 4) * stickIndex), 0, 0, 1);
			GlStateManager.rotate(20, 1, 1, 0);
			GlStateManager.translate(-0, 0.0, -0.12);
		}
		else {
			GlStateManager.rotate(45 - ((360 / 4) * stickIndex) + 45, 0, 0, 1);
			GlStateManager.rotate(20, 1, 1, 0);
			GlStateManager.translate(-0, 0.0, -0.12);
		}
	}
}
