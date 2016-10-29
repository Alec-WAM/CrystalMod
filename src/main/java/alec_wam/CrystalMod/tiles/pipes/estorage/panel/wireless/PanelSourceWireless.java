package alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.api.estorage.IPanelSource;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketItemNBT;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.SortType;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.ViewType;
import alec_wam.CrystalMod.util.ItemNBTHelper;

public class PanelSourceWireless implements IPanelSource {

	protected TileEntityWirelessPanel panel;
	private ItemStack stack;
	
	public PanelSourceWireless(TileEntityWirelessPanel panel, ItemStack stack){
		this.panel = panel;
		this.stack = stack;
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
		return stack !=null ? SortType.values()[ItemNBTHelper.getInteger(stack, "SortType", 0)] : SortType.NAME;
	}

	@Override
	public void setSortType(SortType type) {
		ItemNBTHelper.setInteger(stack, "SortType", type.ordinal());
		CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(stack)));
	}

	@Override
	public ViewType getViewType() {
		return stack !=null ? ViewType.values()[ItemNBTHelper.getInteger(stack, "ViewType", 0)] : ViewType.BOTH;
	}

	@Override
	public void setViewType(ViewType type) {
		ItemNBTHelper.setInteger(stack, "ViewType", type.ordinal());
		CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(stack)));
	}

	@Override
	public boolean getJEISync() {
		return stack !=null ? ItemNBTHelper.getBoolean(stack, "JEISync", false) : false;
	}

	@Override
	public void setJEISync(boolean sync) {
		ItemNBTHelper.setBoolean(stack, "JEISync", sync);
		CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(stack)));
	}

	@Override
	public String getSearchBar() {
		if(stack !=null){
        	return ItemNBTHelper.getString(stack, "SearchBarText", "");
        }
		return "";
	}

	@Override
	public void setSearchBar(String text) {
		if(stack !=null){
        	ItemNBTHelper.setString(stack, "SearchBarText", text);
        	CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(stack)));
        }
	}

}
