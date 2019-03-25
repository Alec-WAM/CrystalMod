package alec_wam.CrystalMod.api.estorage.security;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;

public class SecurityData {

	private UUID uuid;
	private Map<NetworkAbility, Boolean> abilities = Maps.newHashMap();
	
	public SecurityData(UUID uuid){
		this.uuid = uuid;
	}
	
	public SecurityData(UUID uuid, Map<NetworkAbility, Boolean> abilities){
		this(uuid);
		this.abilities = abilities;
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
	
	public byte[] compress() throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			cdo.writeLong(uuid.getMostSignificantBits());
			cdo.writeLong(uuid.getLeastSignificantBits());
			for(NetworkAbility ability : NetworkAbility.values()){
				cdo.writeBoolean(hasAbility(ability));
			}
			return cdo.getCompressed();
		}
		finally {
			cdo.close();
		}
	}
	
	public static SecurityData decompress(byte[] compressed) throws IOException {
		CompressedDataInput cdi = new CompressedDataInput(compressed);
		try {
			long most = cdi.readLong();
			long least = cdi.readLong();
			UUID uuid = new UUID(most, least);
			Map<NetworkAbility, Boolean> abilities = Maps.newHashMap();
			for(NetworkAbility ability : NetworkAbility.values()){
				abilities.put(ability, cdi.readBoolean());
			}
			SecurityData data = new SecurityData(uuid, abilities);
			return data;
		} finally {
			cdi.close();
		}
	}
	
}
