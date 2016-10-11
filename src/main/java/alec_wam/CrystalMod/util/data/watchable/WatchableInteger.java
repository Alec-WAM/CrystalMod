package alec_wam.CrystalMod.util.data.watchable;

public class WatchableInteger {

	private int currentValue;
	private int lastValue;
	
	public void setValue(int value){
		currentValue = value;
	}
	
	public int getValue(){
		return currentValue;
	}

	public int getLastValue() {
		return lastValue;
	}

	public void setLastValue(int lastValue) {
		this.lastValue = lastValue;
	}
}
