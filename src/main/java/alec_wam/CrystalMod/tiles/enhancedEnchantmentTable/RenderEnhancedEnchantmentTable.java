package alec_wam.CrystalMod.tiles.enhancedEnchantmentTable;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

public class RenderEnhancedEnchantmentTable extends TileEntitySpecialRenderer<TileEntityEnhancedEnchantmentTable>{

	@Override
	public void renderTileEntityAt(TileEntityEnhancedEnchantmentTable tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null || ItemStackTools.isEmpty(tile.getStackInSlot(0)))return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		float pixel = 1.0F / 16.0F;
		GlStateManager.translate(8 * pixel, 14 * pixel, 8 * pixel);
		float rotation = 0.0F;
		if(tile.getFacing() == EnumFacing.SOUTH.getHorizontalIndex())rotation = 180;
		if(tile.getFacing() == EnumFacing.WEST.getHorizontalIndex())rotation = 90;
		if(tile.getFacing() == EnumFacing.EAST.getHorizontalIndex())rotation = 270;
		GlStateManager.rotate(rotation, 0, 1, 0);
		GlStateManager.rotate(45.0F, 1, 0, 0);
		GlStateManager.translate(0 * pixel, 0.5 * pixel, -2 * pixel);
		GlStateManager.scale(0.4, 0.4, 0.4);
		RenderUtil.renderItem(tile.getStackInSlot(0), TransformType.FIXED);
		GlStateManager.popMatrix();
	}
}