package alec_wam.CrystalMod.util.data.watchable;

public class WatchableInteger {

	private int currentValue;
	private int lastValue;
	
	public void add(int amt){
		currentValue+=amt;
	}
	
	public void sub(int amt){
		currentValue-=amt;
	}
	
	public void subSafe(int amt){
		currentValue-=amt;
		if(currentValue < 0){
			currentValue = 0;
		}
	}
	
	public void div(int amt){
		currentValue=currentValue/amt;
	}
	
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
	
	public boolean needsSync(){
		return currentValue != lastValue;
	}
	
	public void syncValues(){
		this.lastValue = this.currentValue;
	}
}
