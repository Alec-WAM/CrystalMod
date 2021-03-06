package alec_wam.CrystalMod.api.estorage;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.SortType;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.ViewType;
import net.minecraft.util.math.BlockPos;

public interface IPanelSource {

	public EStorageNetwork getNetwork();
	
	public BlockPos getPanelPos();
	
	public SortType getSortType();
	
	public void setSortType(SortType type);
	
	public ViewType getViewType();
	
	public void setViewType(ViewType type);
	
	public boolean getJEISync();
	
	public void setJEISync(boolean sync);
	
	public String getSearchBar();
	
	public void setSearchBar(String text);
	
}
