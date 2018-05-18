package alec_wam.CrystalMod.tiles.xp;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;

public class RenderTileEntityXPTank extends TileEntitySpecialRenderer<TileEntityXPTank> implements ICustomItemRenderer {

	@Override
	public void renderTileEntityAt(TileEntityXPTank tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		if(tile == null)return;
		GlStateManager.pushMatrix();
		if(tile.xpCon.getFluidAmount() > 0){
			float height = 0.1F + (0.7F * ((float)tile.xpCon.getExperienceLevel() / 100.0F));
			RenderUtil.renderFluidCuboid(tile.xpCon.getFluid(), tile.getPos(), x, y, z, 0.1F, 0.1, 0.1F, 0.9F, height, 0.9F, false);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack) {
        GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5, -0.5, -0.5);
        int xp = TileEntityXPTank.getXPFromStack(stack);
		if(xp > 0){
			float height = 0.1F + (0.7F * ((float)xp / 100.0F));
			RenderUtil.renderFluidCuboid(ModFluids.fluidXpJuice, 0, 0, 0, 0.1F, 0.1, 0.1F, 0.9F, height, 0.9F, false, 15);
		}
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(-0.5, -0.5, 0.5);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(ModBlocks.xpTank.getDefaultState(), 1.0F);
        GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	public TRSRTransformation getTransform(TransformType type) {
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}
}