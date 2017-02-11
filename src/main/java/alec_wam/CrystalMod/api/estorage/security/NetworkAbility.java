package alec_wam.CrystalMod.api.estorage.security;

public enum NetworkAbility {
	/**Can the Player Insert items into the network**/
	INSERT("insert"),
	/**Can the Player Extract items into the network**/
	EXTRACT("extract"),
	/**Can the Player auto-craft**/
	CRAFT("craft"),
	/**Can the Player open network GUIs**/
	VIEW("view"),
	/**Can the Player edit network settings**/
	SETTINGS("settings"),
	/**Can the Player add or remove network components**/
	BUILD("build"),
	/**Can the Player edit security settings**/
	SECURITY("security");
	
	private String id;
	NetworkAbility(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
}
