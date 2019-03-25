package alec_wam.CrystalMod.entities.accessories.boats;

import alec_wam.CrystalMod.entities.accessories.boats.EntityBoatChest.EnumBoatChestType;
import alec_wam.CrystalMod.tiles.chest.wireless.RenderTileWirelessChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderEntityBoatChest extends Render<EntityBoatChest> {
 	
	public static final ItemStack CHEST = new ItemStack(Blocks.CHEST);
	public RenderEntityBoatChest(RenderManager renderManager) {
		super(renderManager);
	}
	
 	@Override
	public void doRender(EntityBoatChest entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
 		if(!entity.isRiding())
			return;
 		Entity boat = entity.getRidingEntity();
		float rot = 180F - entityYaw;
		
		ItemStack stack = entity.getChestStack();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(rot, 0F, 1F, 0F);
		GlStateManager.translate(0F, 0.7F, -0.15F);
		if(boat.getPassengers().size() == 1)
			GlStateManager.translate(0F, 0F, 0.6F);	
		boolean wireless = entity.getType() == EnumBoatChestType.WIRELESS;
		if(wireless){
			GlStateManager.scale(0.95F, 0.95F, 0.95F);	
			float lidangle = entity.prevLidAngle + (entity.lidAngle - entity.prevLidAngle) * partialTicks;
			//TODO Look into Lighting
			RenderTileWirelessChest.renderChest(-0.5, -0.4f, -0.5, entity.getCode(), 2, entity.isPrivate(), lidangle, -1);
		} else {
			GlStateManager.scale(1.75F, 1.75F, 1.75F);		
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);		
		}
		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBoatChest entity) {
		return null;
	}
 }
