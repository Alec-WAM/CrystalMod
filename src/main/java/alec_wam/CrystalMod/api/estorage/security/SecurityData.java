package alec_wam.CrystalMod.api.estorage.security;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

public class SecurityData {

	private UUID uuid;
	private Map<NetworkAbility, Boolean> abilities = Maps.newHashMap();
	
	public SecurityData(UUID uuid){
		this.uuid = uuid;
	}
	
	public Map<NetworkAbility, Boolean> getAbilities(){
		return abilities;
	}
	
	public UUID getUUID(){
		return uuid;
	}
	
	public boolean hasAbility(NetworkAbility ability){
		return abilities.get(ability);
	}
	
}
