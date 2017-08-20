package alec_wam.CrystalMod.entities.minecarts.chests;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.integration.minecraft.ItemMinecartRender;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemCrystalChestMinecartRender implements ICustomItemRenderer {

	private Map<CrystalChestType, EntityCrystalChestMinecartBase> minecarts = Maps.newHashMap();
	
	public EntityCrystalChestMinecartBase getMinecart(CrystalChestType type) {
		if(!minecarts.containsKey(type)){
			minecarts.put(type, EntityCrystalChestMinecartBase.makeMinecart(CrystalMod.proxy.getClientWorld(), type));
		}
		return minecarts.get(type);
	}

	@Override
	public void render(ItemStack stack) {
		CrystalChestType chesttype = CrystalChestType.values()[CrystalChestType.validateMeta(stack.getMetadata())];
		EntityCrystalChestMinecartBase minecart = getMinecart(chesttype);
		if(minecart == null){
			return;
		}
		ItemMinecartRender.renderMinecart(minecart, lastTransform, false);
	}

	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
