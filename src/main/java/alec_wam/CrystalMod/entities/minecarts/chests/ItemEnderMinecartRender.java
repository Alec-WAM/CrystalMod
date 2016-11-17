package alec_wam.CrystalMod.entities.minecarts.chests;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.integration.minecraft.ItemMinecartRender;

public class ItemEnderMinecartRender implements ICustomItemRenderer {

	private EntityEnderChestMinecart minecart = null;
	
	public EntityEnderChestMinecart getMinecart() {
		if(minecart == null){
			minecart = new EntityEnderChestMinecart(CrystalMod.proxy.getClientWorld());
		}
		return minecart;
	}
	
	@Override
	public void render(ItemStack stack) {

		EntityEnderChestMinecart minecart = getMinecart();
		if(minecart == null){
			return;
		}
		ItemMinecartRender.renderMinecart(minecart, lastTransform);
	}

	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
