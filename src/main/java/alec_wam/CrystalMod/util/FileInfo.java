package alec_wam.CrystalMod.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import com.google.common.collect.Maps;

public class FileInfo {
	private static boolean hasLoadedCache = false;
	private static Map<String, String> infoCache = Maps.newHashMap();
	
	public static String getValue(String value, String def){
		checkCache();
		return infoCache.getOrDefault(value, def);
	}
	
	private static void checkCache(){
		if(!hasLoadedCache){
			try{
				URL masterFile = new URL("https://github.com/Alec-WAM/CrystalMod/blob/master/hostedfiles/FileInfo.txt");
				BufferedReader in = new BufferedReader(new InputStreamReader(masterFile.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null){
					if(inputLine.contains("=")){
						int index = inputLine.indexOf("=");
						String key = inputLine.substring(0, index-1);
						String value = inputLine.substring(index+1);
						infoCache.put(key, value);
					}
				}
				in.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			hasLoadedCache = true;
		}
	}
	
}
