package alec_wam.CrystalMod.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class Util {
	  
	public static Random rand = new Random();
	
	  public static String getNameForBlock(Block block) {
	        Object obj = Block.REGISTRY.getNameForObject(block);
	        if (obj == null) {
	            return null;
	        }
	        return obj.toString();
	    }

	public static ItemStack getContainerItem(ItemStack stack) {
		if( ItemStackTools.isNullStack(stack) )
		{
			return ItemStackTools.getEmptyStack();
		}

		final Item i = stack.getItem();
		if( i == null || !i.hasContainerItem( stack ) )
		{
			if( ItemStackTools.getStackSize(stack) > 1 )
			{
				ItemStackTools.incStackSize(stack, -1);
				return stack;
			}
			return ItemStackTools.getEmptyStack();
		}

		ItemStack ci = i.getContainerItem( stack.copy() );
		if( !ItemStackTools.isNullStack(ci) && ci.isItemStackDamageable() && ci.getItemDamage() == ci.getMaxDamage() )
		{
			ci = ItemStackTools.getEmptyStack();
		}

		return ci;
	}

	public static boolean notNullAndInstanceOf(Object object, Class<?> clazz)
    {
        return object != null && clazz.isInstance(object);
    }

    /**
	 * Checks if Minecraft is running in offline mode.
	 * @return if mod is running in offline mode.
	 */
    public static boolean isInternetAvailable()
    {
        try {
			return isHostAvailable("http://www.google.com") || isHostAvailable("http://www.amazon.com")
			        || isHostAvailable("http://www.facebook.com")|| isHostAvailable("http://www.apple.com");
		} catch (IOException e) {
			return false;
		}
    }

    private static boolean isHostAvailable(String hostName) throws IOException
    {
    	try {
			new URL(hostName).openConnection().connect();
			return true;
		}
    	catch (MalformedURLException e) 
		{
			e.printStackTrace();
			return false;
		}
    	catch (IOException e) {
    		e.printStackTrace();
			return false;
		}
    }
    
    public static boolean isImageDataUploaded(ThreadDownloadImageData data) {
		if(data !=null){
			boolean trying = false;
			try{
				return ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, data, 7);
			}catch(Exception e2){}
			return trying;
		}
		return false;
	}
    
	public static class UserNameCheckGSon{
		String username = null;
		Boolean premium = null;
		
		public String getUsername() {
	      return username;
	    }
	    public void setUsername(String username) {
	      this.username = username;
	    }
	    
	    public Boolean getPremium() {
	      return premium;
	    }
	    public void setPremium(Boolean premium) {
	      this.premium = premium;
	    }
	}

	public static boolean isMultipleOf(int input, int mult) {
		return input % mult == 0;
	}

	/**
     * Counts the elements in the array that are not null.
     *
     * @param array The array to check.
     * @param <T>   What we are dealing with.
     * @return The count of non-null objects in the array.
     */
    public static <T> int countNoNull(T[] array) {
        return count(array, Objects::nonNull);
    }

    /**
     * Counts elements in the array that conform to the Function check.
     *
     * @param array The array to check.
     * @param check The Function to apply to each element.
     * @param <T>   What we are dealing with.
     * @return The count.
     */
    public static <T> int count(T[] array, Function<T, Boolean> check) {
        int counter = 0;
        for (T value : array) {
            if (check.apply(value)) {
                counter++;
            }
        }
        return counter;
    }
	
	
}
