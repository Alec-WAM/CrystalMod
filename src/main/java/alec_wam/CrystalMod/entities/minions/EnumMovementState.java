package alec_wam.CrystalMod.entities.minions;

public enum EnumMovementState 
{
	STAY(0),
	GUARD(1),
	FOLLOW(2);
	
	private int id;
	
	EnumMovementState(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public static EnumMovementState fromId(int id)
	{
		for (EnumMovementState state : EnumMovementState.values())
		{
			if (state.id == id)
			{
				return state;
			}
		}
		
		return null;
	}
}
