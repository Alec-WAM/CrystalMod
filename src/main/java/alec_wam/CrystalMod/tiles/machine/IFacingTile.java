package alec_wam.CrystalMod.tiles.machine;

public interface IFacingTile {
	
	public void setFacing(int facing);
	
	public int getFacing();

	public default boolean useVerticalFacing() {
		return false;
	}
	
}
