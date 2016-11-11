package alec_wam.CrystalMod.items.game;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.world.game.tag.TagManager;

public class ItemRenderFlag implements ICustomItemRenderer {

	@Override
	public void render(ItemStack stack, TransformType type) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0.5, 0.5);
		GlStateManager.rotate(180, 1, 0, 0);
		if(type == TransformType.GUI){
			GlStateManager.translate(-1, -0.1, 0);
			GlStateManager.scale(0.8, 0.8, 0.8);
			GlStateManager.rotate(90, 0, 1, 0);
		}else if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.rotate(80, -1, 0, 0);
			GlStateManager.rotate(-50, 0, 0, 1);
			GlStateManager.translate(-0.8, 0, 0.4);
		}else if(type == TransformType.GROUND || type == TransformType.FIRST_PERSON_RIGHT_HAND){
			GlStateManager.translate(-0.5, 0, 0.5);
			GlStateManager.rotate(-45, 0, 1, 0);
		}
		GlStateManager.translate(-0.5, -0.5, -0.5);
		int color = ItemNBTHelper.getInteger(stack, "FlagColor", Color.WHITE.getRGB());
		TagManager.getInstance().renderFlag(color, 90);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.popMatrix();
	}

}
