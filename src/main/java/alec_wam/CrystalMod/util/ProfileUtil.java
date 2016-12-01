package alec_wam.CrystalMod.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class ProfileUtil {
	
	public static final ConcurrentMap<String, UUID> uuidCache = buildCache(3 * 60 * 60, 1024 * 5);
	public static final ConcurrentMap<UUID, String> nameCache = buildCache(3 * 60 * 60, 1024 * 5);
	
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
	
	public static String getUsername(UUID uuid){
		if(!nameCache.containsKey(uuid)){
			String name = getUnCachedName(uuid);
			if(!name.equalsIgnoreCase(ERROR)){
				nameCache.put(uuid, name);
			}
			return name;
		}
		return nameCache.get(uuid);
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
	
}
