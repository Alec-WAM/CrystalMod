package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.client.ClientEventHandler;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("deprecation")
public class TileEntityPedestalRender extends TileEntityRenderer<TileEntityPedestal>
{
    public TileEntityPedestalRender()
    {
        
    }

    @Override
    public void render(TileEntityPedestal tile, double x, double y, double z, float partialTicks, int destroyStage)
    {
    	if(tile == null)return;
		ItemStack renderStack = tile.getStack();
    	if(ItemStackTools.isValid(renderStack)){
			GlStateManager.pushMatrix();
			GlStateManager.color4f(1, 1, 1, 1);

			GlStateManager.translated(x, y, z);
			GlStateManager.pushMatrix();

			EnumFacing facing = tile.getRotation();
			float offsetY = 0.0f;
			GlStateManager.translated(0.5 + (facing.getXOffset() * 0.35), 0.5 + (facing.getYOffset() * 0.35) + offsetY, 0.5 + (facing.getZOffset() * 0.35));
			float scale = 0.5f;
			GlStateManager.scalef(scale, scale, scale);

			if (facing.getAxis() == EnumFacing.Axis.Y){
				if (facing == EnumFacing.DOWN){
					GlStateManager.rotatef(180, 1, 0, 0);
				}
			}
			else {
				GlStateManager.rotatef(90, facing.getZOffset(), 0, facing.getXOffset() * -1);
			}
			GlStateManager.rotatef((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);
			RenderUtil.renderItem(renderStack, TransformType.FIXED);

			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
    }
}
