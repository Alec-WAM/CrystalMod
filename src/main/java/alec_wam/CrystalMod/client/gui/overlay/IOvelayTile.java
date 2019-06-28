package alec_wam.CrystalMod.client.gui.overlay;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IOvelayTile {

	@OnlyIn(Dist.CLIENT)
	public InfoProvider getInfo();
	
	public static interface InfoProvider {
		public void render(ClientWorld world, TileEntity tile, BlockPos pos, Direction side);
	}
	
}
