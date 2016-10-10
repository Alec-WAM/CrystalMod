package com.alec_wam.CrystalMod.util;

import java.util.Collection;

import net.minecraft.command.CommandBase;

public class StringUtils {

	public static String makeReadable(Collection<String> list){
		return CommandBase.joinNiceStringFromCollection(list);
	}
	
}
