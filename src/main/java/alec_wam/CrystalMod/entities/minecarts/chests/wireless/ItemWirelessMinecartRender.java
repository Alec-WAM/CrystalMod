package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;

public class ItemWirelessMinecartRender implements ICustomItemRenderer {

	private EntityWirelessChestMinecart minecart = null;
	
	public EntityWirelessChestMinecart getMinecart() {
		if(minecart == null){
			minecart = new EntityWirelessChestMinecart(CrystalMod.proxy.getClientWorld());
		}
		return minecart;
	}
	
	@Override
	public void render(ItemStack stack, TransformType type) {

		EntityWirelessChestMinecart minecart = getMinecart();
		if(minecart == null){
			return;
		}
		
		minecart.setCode(ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, 0));
        String owner = ItemNBTHelper.getString(stack, WirelessChestHelper.NBT_OWNER, "");
        if(UUIDUtils.isUUID(owner))minecart.setOwner(UUIDUtils.fromString(owner));
		
		boolean atrib = true;
		GlStateManager.pushMatrix();
		if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (type == TransformType.GUI)
		{
			GlStateManager.pushMatrix();
			float scale = 1.8f;
			//Vec3d offset = essence.getRenderOffset();
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.5, 0);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
	        GlStateManager.disableRescaleNormal();
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		else if (type == TransformType.FIRST_PERSON_RIGHT_HAND || type == TransformType.FIRST_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 1.5f;
			GlStateManager.scale(0.8F*scale, 0.8F*scale, 0.8F*scale);
			GlStateManager.translate(2, 0.5, 0);
			if(type == TransformType.FIRST_PERSON_RIGHT_HAND){
				GlStateManager.rotate(60F, 0F, 1F, 0F);
			}
			if(type == TransformType.FIRST_PERSON_LEFT_HAND){
				GlStateManager.rotate(120F, 0F, 1F, 0F);
			}
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}
		else if (type == TransformType.THIRD_PERSON_RIGHT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 2.0f;
			GlStateManager.scale(1.5F*scale, 1.5F*scale, 1.5F*scale);
			if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(-45, 1, 0, 0);
				GlStateManager.translate(0, -5, 0.5);
			}else{
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(45, 1, 0, 0);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.translate(0, -5, 0.5);
			}
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -1, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}

		if(atrib)GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

}
