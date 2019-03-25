package alec_wam.CrystalMod.items.tools.backpack.block;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackInventory;
import alec_wam.CrystalMod.items.tools.backpack.ModelBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class RenderTileEntityBackpack extends TileEntitySpecialRenderer<TileEntityBackpack> {
	private final ModelBackpack backpackModel = new ModelBackpack(1.0F);
	@Override
    public void renderTileEntityAt(TileEntityBackpack te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		ItemStack backpack = te.getBackpack();
		if(ItemStackTools.isValid(backpack)){
			IBackpack type = BackpackUtil.getType(backpack);
			if(type !=null){
				if (destroyStage >= 0)
		        {
		            this.bindTexture(DESTROY_STAGES[destroyStage]);
		            GlStateManager.matrixMode(5890);
		            GlStateManager.pushMatrix();
		            GlStateManager.scale(4.0F, 2.0F, 1.0F);
		            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
		            GlStateManager.matrixMode(5888);
		        }
		        else
		        {
					ResourceLocation res = type.getTexture(backpack, 0);
					if(res == null){
						res = TextureMap.LOCATION_MISSING_TEXTURE;
					}
					this.bindTexture(res);
		        }
				float rot = 0.0f;
				EnumFacing facing = EnumFacing.getHorizontal(te.getFacing());
				if(facing == EnumFacing.SOUTH){
					GlStateManager.translate(0.5, 0.9, 0.1);
					rot = 180;
				}
				else if(facing == EnumFacing.EAST){
					GlStateManager.translate(0.1, 0.9, 0.5);
					rot = -90;
				}
				else if(facing == EnumFacing.WEST){
					GlStateManager.translate(0.9, 0.9, 0.5);
					rot = 90;
				} else {
					GlStateManager.translate(0.5, 0.9, 0.9);
				}
				
				GlStateManager.rotate(rot, 0, 1, 0);
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.rotate(1, 0, 1, 0);
				//GlStateManager.translate(0.5, -0.9, -1.0);
				
				GlStateManager.pushMatrix();
				this.backpackModel.Strap_Long.isHidden = this.backpackModel.Strap_Long2.isHidden = true;
				this.backpackModel.render(null, 0, 0, 0, 0, 0, 0.0625F);
				type.renderExtras(backpack);
	            
				if(type instanceof IBackpackInventory && te instanceof TileEntityBackpackInventory){
					InventoryBackpack inventory = ((TileEntityBackpackInventory)te).inventory;
					InventoryBackpackUpgrades upgradeInv = type.getUpgradeInventory(backpack);
					if(inventory !=null && upgradeInv !=null){
						if(upgradeInv.hasUpgrade(BackpackUpgrade.POCKETS)){
							if(inventory instanceof NormalInventoryBackpack){
								NormalInventoryBackpack normalInv = (NormalInventoryBackpack)inventory;
								ItemStack weapon = normalInv.getToolStack(0);
								ItemStack tool = normalInv.getToolStack(1);
								if(ItemStackTools.isValid(weapon)){
									GlStateManager.pushMatrix();
									GlStateManager.translate(0.4, 0.4, 0.4);
									GlStateManager.rotate(90, 0, 1, 0);
									GlStateManager.rotate(-45, 0, 0, 1);
									GlStateManager.scale(0.5, 0.5, 0.5);
									RenderUtil.renderItem(weapon, TransformType.FIXED);
									GlStateManager.popMatrix();
								}
								if(ItemStackTools.isValid(tool)){
									GlStateManager.pushMatrix();
									GlStateManager.translate(-0.4, 0.4, 0.4);
									GlStateManager.rotate(90, 0, 1, 0);
									GlStateManager.rotate(135, 0, 0, 1);
									GlStateManager.scale(0.5, 0.5, 0.5);
									RenderUtil.renderItem(tool, TransformType.FIXED);
									GlStateManager.popMatrix();
								}
							}
						}
					}
				}
				GlStateManager.popMatrix();
				
				if (destroyStage >= 0)
		        {
		            GlStateManager.matrixMode(5890);
		            GlStateManager.popMatrix();
		            GlStateManager.matrixMode(5888);
		        }
			}
		}
		GlStateManager.popMatrix();
	}
	
}
