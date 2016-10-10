package com.alec_wam.CrystalMod.util;

import java.util.UUID;

public class UUIDUtils 
{
    public static String fromUUID(UUID value) 
    {
    	if(value == null)return "";
        return value.toString().replace("-", "");
    }

    public static boolean isUUID(String input) 
    {
    	try{
        UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        }catch(java.lang.IllegalArgumentException e){
    		return false;
    	}
    	return true;
    }
    
    public static UUID fromString(String input) 
    {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
