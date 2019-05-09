package alec_wam.CrystalMod.tiles.chests.metal;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityMetalCrystalChestRender extends TileEntityRenderer<TileEntityMetalCrystalChest>
{
	private static Map<MetalCrystalChestType, ResourceLocation> locations;

	static {
		Builder<MetalCrystalChestType, ResourceLocation> builder = ImmutableMap.<MetalCrystalChestType,ResourceLocation>builder();
		for (MetalCrystalChestType typ : MetalCrystalChestType.values()) {
			builder.put(typ, new ResourceLocation("crystalmod", "textures/model/chests/metal_"+typ.getName()+".png"));
		}
		locations = builder.build();
	}

	private static ModelChest model = new ModelChest();

	@Override
	public void render(TileEntityMetalCrystalChest tile, double x, double y, double z, float partialTick, int breakStage)
	{
		if (tile == null) {
			return;
		}
		EnumFacing facing = EnumFacing.NORTH;
		MetalCrystalChestType type = tile.type;

		if (tile.getBlockState().getBlock() instanceof BlockMetalCrystalChest) {
			facing = tile.getBlockState().get(BlockMetalCrystalChest.FACING);
		}
		float lidangle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTick;
		renderChest(x, y, z, type, facing, lidangle, breakStage);
	}

	public static void renderChest(double x, double y, double z, MetalCrystalChestType type, EnumFacing facing, float lidangle, int breakStage){
		if (breakStage >= 0)
		{
			Minecraft.getInstance().textureManager.bindTexture(DESTROY_STAGES[breakStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 4.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else{
			Minecraft.getInstance().textureManager.bindTexture(locations.get(type));
		}
		GlStateManager.pushMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scalef(1.0F, -1F, -1F);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
		int k = 0;
		if (facing == EnumFacing.SOUTH) {
			k = 180;
		}
		if (facing == EnumFacing.EAST) {
			k = 90;
		}
		if (facing == EnumFacing.WEST) {
			k = -90;
		}
		GlStateManager.rotatef(k, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		lidangle = 1.0F - lidangle;
		lidangle = 1.0F - lidangle * lidangle * lidangle;
		model.getLid().rotateAngleX = -((lidangle * 3.141593F) / 2.0F);
		// Render the chest itself
		model.renderAll();
		if (breakStage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
