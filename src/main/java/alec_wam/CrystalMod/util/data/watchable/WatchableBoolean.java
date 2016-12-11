package alec_wam.CrystalMod.util.data.watchable;

public class WatchableBoolean {

	private boolean currentValue;
	private boolean lastValue;
	
	public void setValue(boolean value){
		currentValue = value;
	}
	
	public boolean getValue(){
		return currentValue;
	}

	public boolean getLastValue() {
		return lastValue;
	}

	public void setLastValue(boolean lastValue) {
		this.lastValue = lastValue;
	}
}
