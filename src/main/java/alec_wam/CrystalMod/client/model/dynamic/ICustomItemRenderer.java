package alec_wam.CrystalMod.client.model.dynamic;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomItemRenderer {

	@SideOnly(Side.CLIENT)
	public void render(ItemStack stack, TransformType type);
	
}
