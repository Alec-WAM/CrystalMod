package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.integration.minecraft.ItemMinecartRender;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemWirelessMinecartRender implements ICustomItemRenderer {

	private EntityWirelessChestMinecart minecart = null;
	
	public EntityWirelessChestMinecart getMinecart() {
		if(minecart == null){
			minecart = new EntityWirelessChestMinecart(CrystalMod.proxy.getClientWorld());
		}
		return minecart;
	}
	
	@Override
	public void render(ItemStack stack) {

		EntityWirelessChestMinecart minecart = getMinecart();
		if(minecart == null){
			return;
		}
		
		minecart.setCode(ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, 0));
        String owner = ItemNBTHelper.getString(stack, WirelessChestHelper.NBT_OWNER, "");
        if(UUIDUtils.isUUID(owner))minecart.setOwner(UUIDUtils.fromString(owner));
        ItemMinecartRender.renderMinecart(minecart, lastTransform);
	}

	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}
}
