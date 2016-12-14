package alec_wam.CrystalMod.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ProfileUtil {
	public static final ConcurrentMap<UUID, GameProfile> profileCache = buildCache(3 * 60 * 60, 1024 * 5);
	public static final ConcurrentMap<String, UUID> uuidCache = buildCache(3 * 60 * 60, 1024 * 5);
	public static final ConcurrentMap<UUID, MinecraftProfilePropertiesResponse> propertiesCache = buildCache(3 * 60 * 60, 1024 * 5);
	
	public static <K, V> ConcurrentMap<K, V> buildCache(int seconds, int maxSize) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

        if (seconds > 0) {
            builder.expireAfterWrite(seconds, TimeUnit.SECONDS);
        }

        if (maxSize > 0) {
            builder.maximumSize(maxSize);
        }

        return builder.build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }).asMap();
    }
	
	public static String downloadJsonData(String urlLoc){
		URL url;
		String string = "";
		try 
		{
			url = new URL(urlLoc);
	        URLConnection connection = url.openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        if(connection !=null){
		        InputStream io = connection.getInputStream();
		        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(io));
				
		        String line;
	
		        while ((line = bufferedReader.readLine()) != null)
		        {
		        	if (!line.startsWith("//") && !line.isEmpty())
		        	{
		        		string+=(line);
		        	}
		        }
	
		        bufferedReader.close();
	        }
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return string;
	}
	
	public static JSONObject getJSONObject(String urlLoc){
		final String data = downloadJsonData(urlLoc);
		String jsonString = data.replace("[", "").replace("]", "");
		try{
			return new JSONObject(jsonString);
		} catch(JSONException e){
			ModLogger.warning(data);
			e.printStackTrace();
		}
		return null;
	}
	
	public static UUID getUUID(String username){
		if(!uuidCache.containsKey(username)){
			UUID uuid = getUnCachedUUID(username);
			if(uuid !=null){
				uuidCache.put(username, uuid);
			}
			return uuid;
		}
		return uuidCache.get(username);
	}
	
	public static GameProfile getProfile(UUID uuid){
		if(!profileCache.containsKey(uuid)){
			GameProfile profile = getUnCachedProfile(uuid);
			profileCache.put(uuid, profile);
			return profile;
		}
		return profileCache.get(uuid);
	}
	
	private static UUID getUnCachedUUID(String username){
		JSONObject jsonResult = getJSONObject(getURL(username, RequestType.NAMETOUUID));
	
		if(jsonResult == null || !jsonResult.has("id"))return null;
		
		Object object = jsonResult.get("id");
		if(object == null || !(object instanceof String))return null;
		String uuid = jsonResult.getString("id");
		if(!Strings.isNullOrEmpty(uuid)){
			return UUIDUtils.fromString(uuid);
		}
		return null;
	}
	
	public static MinecraftProfilePropertiesResponse getProfileData(UUID uuid){
		if(!propertiesCache.containsKey(uuid)){
			String jsonResult = downloadJsonData(getURL(UUIDUtils.fromUUID(uuid), RequestType.UUIDTONAME));
			MinecraftProfilePropertiesResponse response = null;
			try {
				response = getResponse(jsonResult);
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}
			propertiesCache.put(uuid, response);
			return response;
		}
		return propertiesCache.get(uuid);
	}
	
	public static PropertyMap getProperties(UUID uuid){
		MinecraftProfilePropertiesResponse data = getProfileData(uuid);
		if(data !=null){
			return data.getProperties();
		}
		return null;
	}
	
	public static String getUsername(UUID uuid){
		MinecraftProfilePropertiesResponse data = getProfileData(uuid);
		if(data !=null){
			return data.getName();
		}
		return ERROR;
	}
	
	private static String getUnCachedName(UUID uuid){
		JSONObject jsonResult = getJSONObject(getURL(UUIDUtils.fromUUID(uuid), RequestType.UUIDTONAME));
	
		if(jsonResult == null || !jsonResult.has("name"))return ERROR;
		
		Object object = jsonResult.get("name");
		if(object == null || !(object instanceof String))return ERROR;
		String name = jsonResult.getString("name");
		if(!Strings.isNullOrEmpty(name)){
			return name;
		}
		return ERROR;
	}
	
	private static GameProfile getUnCachedProfile(UUID uuid){
		GameProfile profile = new GameProfile(uuid, ProfileUtil.getUnCachedName(uuid));
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			FMLClientHandler.instance().getClient().getSessionService().fillProfileProperties(profile, true);
		} else {
			FMLServerHandler.instance().getServer().getMinecraftSessionService().fillProfileProperties(profile, true);
		}
		return profile;
	}
	
	public static final String ERROR = "<ERROR>";
	
	protected final static int timeout = 5000;
	
	public static String getURL(String value, RequestType type){
		if(type == RequestType.NAMETOUUID){
			return String.format("https://api.mojang.com/users/profiles/minecraft/%s", value);
		}
		return String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", value);
	}
	
	public static enum RequestType{
		UUIDTONAME, NAMETOUUID;
	}
	
	private static Gson gson;
	
	public static Gson getGSon(){
		if(gson == null){
			GsonBuilder builder = new GsonBuilder();
	        builder.registerTypeAdapter(GameProfile.class, new GameProfileSerializer());
	        builder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
	        builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
	        builder.registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer());
	        gson = builder.create();
		}
		return gson;
	}
	
	public static MinecraftProfilePropertiesResponse getResponse(String jsonResult) throws AuthenticationException {
		try {
			MinecraftProfilePropertiesResponse result = getGSon().fromJson(jsonResult, MinecraftProfilePropertiesResponse.class);

			if (result == null) return null;

			if (StringUtils.isNotBlank(result.getError())) {
				if ("UserMigratedException".equals(result.getCause())) {
					throw new UserMigratedException(result.getErrorMessage());
				} else if (result.getError().equals("ForbiddenOperationException")) {
					throw new InvalidCredentialsException(result.getErrorMessage());
				} else {
					throw new AuthenticationException(result.getErrorMessage());
				}
			}

			return result;
		} catch (IllegalStateException e) {
			throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
		} catch (JsonParseException e) {
			throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
		}
	}
	
	public static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {
        @Override
        public GameProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = (JsonObject) json;
            UUID id = object.has("id") ? context.<UUID>deserialize(object.get("id"), UUID.class) : null;
            String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            return new GameProfile(id, name);
        }

        @Override
        public JsonElement serialize(GameProfile src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (src.getId() != null) result.add("id", context.serialize(src.getId()));
            if (src.getName() != null) result.addProperty("name", src.getName());
            return result;
        }
    }
	
}
