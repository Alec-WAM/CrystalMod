package alec_wam.CrystalMod.api.estorage;


public interface INetworkItemProvider extends INetworkTile {

	public int getPriority();

	public void setPriority(int i);
	
	public INetworkInventory getNetworkInventory();
	
}
