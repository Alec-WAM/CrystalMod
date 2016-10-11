package alec_wam.CrystalMod.tiles.weather;

public enum MoonPhase
{
	FullMoon("FullMoon", 0, 0), 
    WaningGibbous("WaningGibbous", 1, 7), 
    LastQuarter("LastQuarter", 2, 6), 
    WaningCrescent("WaningCrescent", 3, 5), 
    NewMoon("NewMoon", 4, 4), 
    WaxingCrescent("WaxingCrescent", 5, 3), 
    FirstQuarter("FirstQuarter", 6, 2), 
    WaxingGibbous("WaxingGibbous", 7, 1);
	public final String id;
	public final int index;
	public final int days;
	MoonPhase(String name, int index, int days){
		id = name;
		this.index = index;
		this.days = days;
	}
    
}
