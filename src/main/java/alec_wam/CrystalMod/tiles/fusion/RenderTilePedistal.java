package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.handler.ClientEventHandler;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;

public class RenderTilePedistal<T extends TilePedistal> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TilePedistal tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null)return;
		if(ItemStackTools.isValid(tile.getStackInSlot(0))){
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);

			GlStateManager.translate(x, y, z);
			GlStateManager.pushMatrix();

			EnumFacing facing = tile.getRotation();
			boolean itemBlock = tile.getStack().getItem() instanceof ItemBlock;
			boolean flatItemBlock = itemBlock && !tile.getStack().getItem().isFull3D();
			float offsetY = 0.0f;
			GlStateManager.translate(0.5 + (facing.getFrontOffsetX() * 0.35), 0.5 + (facing.getFrontOffsetY() * 0.35) + offsetY, 0.5 + (facing.getFrontOffsetZ() * 0.35));
			float scale = itemBlock ? 0.5f : 0.5f;
			GlStateManager.scale(scale, scale, scale);

			if (facing.getAxis() == EnumFacing.Axis.Y){
				if (facing == EnumFacing.DOWN){
					GlStateManager.rotate(180, 1, 0, 0);
				}
			}
			else {
				GlStateManager.rotate(90, facing.getFrontOffsetZ(), 0, facing.getFrontOffsetX() * -1);
			}
			GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);
			RenderUtil.renderItem(tile.getStack(), TransformType.FIXED);

			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
	}
}