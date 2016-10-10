package com.alec_wam.CrystalMod.util;

import java.io.File;
import java.io.FileInputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SaveHelper {
      public static enum SaveFileType{
    	  MASTER, PLAYERDATA, DATA;
      }
	
	
	  public static File getSaveDir(SaveFileType type)
	  {
		File master = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getSaveHandler().getWorldDirectory();  
		switch(type){
		case PLAYERDATA : return new File(master, "playerdata");
		case DATA : return new File(master, "data");
		default : case MASTER : return master;
		}
	  }
	  
	  public static File getOrCreateFile(File file){
		    try
		    {
		      if (!file.exists()) {
		    	  file.createNewFile();
		      }
		    }
		    catch (Throwable t)
		    {
		    }
	        return file;
	  }
	  
	  public static File getCrystalFile(){
		    File worldFile = SaveHelper.getSaveDir(SaveFileType.MASTER);
	        File crystalFile = new File(worldFile, "/crystalmod/");
	        try
		    {
		      if (!crystalFile.exists()) {
		    	  crystalFile.mkdirs();
		      }
		    }
		    catch (Throwable t)
		    {
		    }
	        return crystalFile;
	  }
	  
	  public static File getSaveFile(SaveFileType type, String fileName)
	  {
	    File file = SaveHelper.getSaveDir(type);
	    try
	    {
	      if (!file.exists()) {
	        file.mkdirs();
	      }
	    }
	    catch (Throwable t) {}
	    try
	    {
	      file = new File(file, fileName);
	    }
	    catch (Throwable t) {}
	    try
	    {
	      if (!file.exists()) {
	        file.createNewFile();
	      }
	    }
	    catch (Throwable t)
	    {
	      //System.out.println(t);
	    }
	    return file;
	  }
	  
	  public static NBTTagCompound getNBTtag(File file)
	  {
	    NBTTagCompound nbtData = new NBTTagCompound();
	    if(!file.exists())return nbtData;
	    try
	    {
	      FileInputStream fileinputstream = new FileInputStream(file);
	      nbtData = CompressedStreamTools.readCompressed(fileinputstream);
	      fileinputstream.close();
	    }
	    catch (Throwable t) {}
	    return nbtData;
	  }
	  
	
}
