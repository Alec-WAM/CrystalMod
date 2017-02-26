package alec_wam.CrystalMod.tiles.shieldrack;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

public class RenderTileShieldRack extends TileEntitySpecialRenderer<TileShieldRack> {

	@Override
    public void renderTileEntityAt(TileShieldRack te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		EnumFacing rot = EnumFacing.getHorizontal(te.getFacing());
		if(rot == EnumFacing.NORTH){
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-1, 0, -1);
		}
		
		if(rot == EnumFacing.WEST){
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(0, 0, -1);
		}
		
		if(rot == EnumFacing.EAST){
			GlStateManager.rotate(270, 0, 1, 0);
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-1, 0, 0);
		}
		
		if(ItemStackTools.isValid(te.getShieldStack())){
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.75, 0.5, 0);
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.scale(2, 2, 2);
			RenderUtil.renderItem(te.getShieldStack(), TransformType.FIXED);
			GlStateManager.popMatrix();
		}
		float weaponScale = 1.0f;
		if(ItemStackTools.isValid(te.getLeftStack())){
			GlStateManager.pushMatrix();
			boolean rotateToHandle = ToolUtil.isSword(te.getLeftStack());
			GlStateManager.translate(rotateToHandle ? 0.35 : 0.25, 0.5, 0.1);
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.rotate(rotateToHandle ? 90 : -90, 0, 0, 1);
			GlStateManager.scale(weaponScale, weaponScale, weaponScale);
			RenderUtil.renderItem(te.getLeftStack(), TransformType.FIXED);
			GlStateManager.popMatrix();
		}
		if(ItemStackTools.isValid(te.getRightStack())){
			GlStateManager.pushMatrix();
			boolean rotateToHandle = ToolUtil.isSword(te.getRightStack());
			GlStateManager.translate(rotateToHandle ? 0.65 : 0.75, 0.5, 0.1);			
			GlStateManager.rotate(rotateToHandle ? 90 : -90, 0, 0, 1);
			
			GlStateManager.scale(weaponScale, weaponScale, weaponScale);
			RenderUtil.renderItem(te.getRightStack(), TransformType.FIXED);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}
	
}
