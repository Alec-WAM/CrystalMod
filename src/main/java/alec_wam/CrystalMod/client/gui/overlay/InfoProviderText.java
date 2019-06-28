package alec_wam.CrystalMod.client.gui.overlay;

import java.util.List;

import alec_wam.CrystalMod.client.gui.overlay.IOvelayTile.InfoProvider;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class InfoProviderText implements InfoProvider {

	public List<String> lines;
	public InfoProviderText(List<String> lines){
		this.lines = lines;
	}
	
	@Override
	public void render(ClientWorld world, TileEntity tile, BlockPos pos, Direction side) {
		int x = HUDOverlayHandler.getOverlayX();
		int y = HUDOverlayHandler.getOverlayY();
		MainWindow window = Minecraft.getInstance().mainWindow;
		InfoBoxBuilder builder = new InfoBoxBuilder(x, y, window.getScaledWidth(), window.getScaledHeight(), 300, lines);
		RenderUtil.drawHoveringTextBox(builder);
	}

}
