package alec_wam.CrystalMod.util;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class StringUtils {
	
	public static String makeListReadable(Collection<?> list){
		List<String> strings = Lists.newArrayList();
		for(Object obj : list){
			strings.add(""+obj.toString());
		}
		return joinNiceStringFromCollection(strings);
	}
	
	public static String makeReadable(Collection<String> list){
		return joinNiceStringFromCollection(list);
	}
	
	/**
     * Creates a linguistic series joining the input objects together.  Examples: 1) {} --> "",  2) {"Steve"} -->
     * "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil and Mark"
     */
    public static String joinNiceString(Object[] elements)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < elements.length; ++i)
        {
            String s = elements[i].toString();

            if (i > 0)
            {
                if (i == elements.length - 1)
                {
                    stringbuilder.append(" and ");
                }
                else
                {
                    stringbuilder.append(", ");
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    /**
     * Creates a linguistic series joining together the elements of the given collection.  Examples: 1) {} --> "",  2)
     * {"Steve"} --> "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil
     * and Mark"
     */
    public static String joinNiceStringFromCollection(Collection<String> strings)
    {
        /**
         * Creates a linguistic series joining the input objects together.  Examples: 1) {} --> "",  2) {"Steve"} -->
         * "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil and Mark"
         */
        return joinNiceString(strings.toArray(new String[strings.size()]));
    }
    
    public static String getRomanString(int level)
    {
        if(level <= 10){
        	return Lang.translateToLocal("enchantment.level." + level);
        }
        return "" + level;
    }
	
}
