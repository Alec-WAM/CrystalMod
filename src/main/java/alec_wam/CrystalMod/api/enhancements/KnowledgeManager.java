package alec_wam.CrystalMod.api.enhancements;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.SaveHelper;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class KnowledgeManager {

	public static final List<IEnhancement> CLIENT_KNOWLEDGE_LIST = Lists.newArrayList();
	
	public static List<IEnhancement> getKnownEnhancements(UUID uuid){
		List<IEnhancement> list = Lists.newArrayList();
		for(IEnhancement enhancement : EnhancementManager.getEnhancements()){
			if(hasKnowledge(uuid, enhancement)){
				list.add(enhancement);
			}
		}
		return list;
	}
	
	public static boolean hasKnowledge(UUID uuid, IEnhancement enhancement){
		if(!enhancement.requiresKnowledge()) return true;
		NBTTagCompound nbt = getPlayerData(uuid);
		String id = enhancement.getID().toString();
		if(nbt.hasKey(id)){
			return nbt.getBoolean(id);
		}
		return false;
	}
	
	public static void setHasKnowledge(UUID uuid, IEnhancement enhancement, boolean value){
		NBTTagCompound nbt = getPlayerData(uuid);
		String id = enhancement.getID().toString();
		nbt.setBoolean(id, value);
		try
	    {
			CompressedStreamTools.write(nbt, getDataFile(uuid));
	    }
	    catch (Throwable t) {}
	}
	
	public static void syncData(EntityPlayerMP player){
		UUID uuid = EntityPlayer.getUUID(player.getGameProfile());
		if(player !=null && uuid !=null){
			NBTTagCompound nbt = new NBTTagCompound();
			for(IEnhancement enhancement : getKnownEnhancements(uuid)){
				nbt.setBoolean(enhancement.getID().toString(), true);
			}
			CrystalModNetwork.sendTo(new PacketEntityMessage(player, "SyncKnowledge", nbt), player);
		}
	}
	
	public static void loadData(NBTTagCompound nbt){
		CLIENT_KNOWLEDGE_LIST.clear();
		for(IEnhancement enhancement : EnhancementManager.getEnhancements()){
			if(nbt.hasKey(enhancement.getID().toString())){
				CLIENT_KNOWLEDGE_LIST.add(enhancement);
			}
		}
	}
	
	public static boolean hasClientKnowledge(IEnhancement enhancement){
		return CLIENT_KNOWLEDGE_LIST.contains(enhancement);
	}
	
	public static NBTTagCompound getPlayerData(UUID uuid){
		try
	    {
			return CompressedStreamTools.read(getDataFile(uuid));
	    }
	    catch (Throwable t) {return new NBTTagCompound();}
	}
	
	public static File getDataFile(UUID uuid){
		File crystalFile = SaveHelper.getCrystalFile();
		File folder = new File(crystalFile, "/playerknowledge/");
		if(!folder.exists()){
			folder.mkdirs();
		}
		return SaveHelper.getOrCreateFile(new File(folder, UUIDUtils.fromUUID(uuid)+".data"));
	}
}
