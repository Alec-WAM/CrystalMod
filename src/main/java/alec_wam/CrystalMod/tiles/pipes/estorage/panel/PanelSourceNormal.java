package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.api.estorage.IPanelSource;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.SortType;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.ViewType;

public class PanelSourceNormal implements IPanelSource {

	protected TileEntityPanel panel;
	
	public PanelSourceNormal(TileEntityPanel panel){
		this.panel = panel;
	}
	
	@Override
	public EStorageNetwork getNetwork() {
		return panel.network;
	}
	
	@Override
	public BlockPos getPanelPos(){
		return panel.getPos();
	}

	@Override
	public SortType getSortType() {
		return panel.sortType;
	}

	@Override
	public void setSortType(SortType type) {
		panel.setSort(type);
	}

	@Override
	public ViewType getViewType() {
		return panel.viewType;
	}

	@Override
	public void setViewType(ViewType type) {
		panel.setView(type);
	}

	@Override
	public boolean getJEISync() {
		return panel.jeiSync;
	}

	@Override
	public void setJEISync(boolean sync) {
		panel.setJEISync(sync);
	}

	@Override
	public String getSearchBar() {
		return panel.searchBarText;
	}

	@Override
	public void setSearchBar(String text) {
		panel.setSearchBar(text);
	}

}
