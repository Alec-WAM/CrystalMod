package alec_wam.CrystalMod.util;

public enum CompareType{
	NOT("!=") {
		@Override
		public boolean passes(int amount, int filter){
			return amount !=filter;
		}
	}, 
	EQUALS("=") {
		@Override
		public boolean passes(int amount, int filter){
			return amount == filter;
		}
	}, 
	LESS("<") {
		@Override
		public boolean passes(int amount, int filter){
			return amount < filter;
		}
	}, 
	GREATER(">") {
		@Override
		public boolean passes(int amount, int filter){
			return amount > filter;
		}
	}, 
	LESS_EQUAL("<=") {
		@Override
		public boolean passes(int amount, int filter){
			return amount <= filter;
		}
	}, 
	GREATER_EQUAL(">=") {
		@Override
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
