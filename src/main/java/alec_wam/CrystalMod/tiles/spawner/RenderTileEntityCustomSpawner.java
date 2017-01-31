package alec_wam.CrystalMod.tiles.spawner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;

public class RenderTileEntityCustomSpawner<T extends TileEntityCustomSpawner> extends TileEntitySpecialRenderer<T> {

	public void renderTileEntityAt(TileEntityCustomSpawner tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x + 0.5F, (float)y, (float)z + 0.5F);
		renderSpawner(tile.getBaseLogic(), x, y, z, partialTicks);
		GlStateManager.popMatrix();
	}

	public void renderSpawner(CustomSpawnerBaseLogic baseLogic, double x, double y, double z, float partialTicks) {
		Entity entity = baseLogic.getEntityForRenderer();

		if (entity != null)
		{
			EntityEssenceInstance<?> essence = ItemMobEssence.getEssence(baseLogic.getEntityNameToSpawn());
			if(essence == null){
				return;
			}
			if (baseLogic.powered) partialTicks = 0f;
			GlStateManager.pushMatrix();
			entity.setWorld(baseLogic.getSpawnerWorld());
			
			float f1 = 0.4375F * essence.getRenderScale(TransformType.NONE);
			GlStateManager.translate(0.0F, 0.4F, 0.0F);
			GlStateManager.rotate((float) (baseLogic.renderRotation1 + (baseLogic.renderRotation0 - baseLogic.renderRotation1) * (double) partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.4F, 0.0F);
			GlStateManager.scale(f1, f1, f1);
			entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
			GlStateManager.popMatrix();
		}
	}
	
}
