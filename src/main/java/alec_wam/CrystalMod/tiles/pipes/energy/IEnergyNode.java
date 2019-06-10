package alec_wam.CrystalMod.tiles.pipes.energy;

public interface IEnergyNode {

	public Object getHost();
	
	public int fillEnergy(int amount, boolean simulate);
	
	public int drainEnergy(int amount, boolean simulate);
	
	public int getEnergy();
	
	public int getMaxEnergy();
	
}
