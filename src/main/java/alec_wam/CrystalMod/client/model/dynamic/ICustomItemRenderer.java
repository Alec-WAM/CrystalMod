package alec_wam.CrystalMod.client.model.dynamic;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomItemRenderer {

	@SideOnly(Side.CLIENT)
	public void render(ItemStack stack);
	
	@SideOnly(Side.CLIENT)
	public TRSRTransformation getTransform(TransformType type);
	
	@SideOnly(Side.CLIENT)
	public default List<ModelResourceLocation> getModels() {
		return Lists.newArrayList();
	}
	
}
