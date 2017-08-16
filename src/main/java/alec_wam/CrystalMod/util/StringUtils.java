package alec_wam.CrystalMod.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketDimensionNameRequest;
import net.minecraft.command.CommandBase;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class StringUtils {

	public static final Map<Integer, String> DIMENSION_NAMES = Maps.newHashMap();
	
	public static final String getDimensionName(UUID requestID, int dimension){
		if(FMLCommonHandler.instance().getSide() == Side.SERVER){
			try{
				DimensionType type = DimensionManager.getProviderType(dimension);
				return type.getName();
			} catch(Exception e){}
		} else {
			if(!DIMENSION_NAMES.containsKey(dimension)){
				CrystalModNetwork.sendToServer(new PacketDimensionNameRequest(requestID, dimension));
			} else {
				return DIMENSION_NAMES.get(dimension);
			}
		}
		
		return ""+dimension;
	}
	
	public static String makeListReadable(Collection<?> list){
		List<String> strings = Lists.newArrayList();
		for(Object obj : list){
			strings.add(""+obj.toString());
		}
		return CommandBase.joinNiceStringFromCollection(strings);
	}
	
	public static String makeReadable(Collection<String> list){
		return CommandBase.joinNiceStringFromCollection(list);
	}

	public static String[] makeStringArray(Object[] objs) {
		String[] array = new String[objs.length];
		for(int i = 0; i < objs.length; i++){
			array[i] = objs[i].toString();
		}
		return array;
	}
	
}
