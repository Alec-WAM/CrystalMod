package alec_wam.CrystalMod.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Util {

	public static Random rand = new Random();

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
	
	public static int[] convertToInt(List<Integer> list){
		int[] newArray = new int[list.size()];
		for(int i = 0; i < list.size(); i++){
			newArray[i] = list.get(i);
		}
		return newArray;
	}


}
