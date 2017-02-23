package alec_wam.CrystalMod.util;

public enum CompareType{
	NOT("!=") {
		public boolean passes(int amount, int filter){
			return amount !=filter;
		}
	}, 
	EQUALS("=") {
		public boolean passes(int amount, int filter){
			return amount == filter;
		}
	}, 
	LESS("<") {
		public boolean passes(int amount, int filter){
			return amount < filter;
		}
	}, 
	GREATER(">") {
		public boolean passes(int amount, int filter){
			return amount > filter;
		}
	}, 
	LESS_EQUAL("<=") {
		public boolean passes(int amount, int filter){
			return amount <= filter;
		}
	}, 
	GREATER_EQUAL(">=") {
		public boolean passes(int amount, int filter){
			return amount >= filter;
		}
	};
	
	final String visual;
	CompareType(String visual){
		this.visual = visual;
	}
	
	public String getStringValue(){
		return visual;
	}
	
	public abstract boolean passes(int amount, int filter);
}
