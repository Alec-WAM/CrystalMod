package alec_wam.CrystalMod.tiles.darkinfection;

import alec_wam.CrystalMod.blocks.connected.BlockConnectedTexture;
import alec_wam.CrystalMod.client.model.dynamic.ModelConnectedTexture;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDenseDarkness extends BlockConnectedTexture {

	@Override
	@SideOnly(Side.CLIENT)
	public ModelConnectedTexture getModel() {
		return new ModelDenseDarkness();
	}

}
