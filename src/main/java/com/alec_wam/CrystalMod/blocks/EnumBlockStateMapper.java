package com.alec_wam.CrystalMod.blocks;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public class EnumBlockStateMapper<T extends Enum<T> & IStringSerializable> extends StateMapperBase {
	
	protected PropertyEnum<T> prop;
	
	public EnumBlockStateMapper(PropertyEnum<T> prop){
		this.prop = prop;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		T type = state.getValue(prop);
		StringBuilder builder = new StringBuilder();
		String nameOverride = null;

		builder.append(type.getName());
		builder.append("=");
		builder.append(type);

		nameOverride = state.getBlock().getRegistryName().getResourcePath();

		if (builder.length() == 0) {
			builder.append("normal");
		}

		ResourceLocation baseLocation = nameOverride == null ? state.getBlock()
				.getRegistryName() : new ResourceLocation("crystalmod",
				nameOverride);

		return new ModelResourceLocation(baseLocation, builder.toString());
	}
}
