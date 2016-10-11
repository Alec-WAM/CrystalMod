package alec_wam.CrystalMod.util;

public class TimeUtil {

	public static int SECOND = 20;
	public static int MINUTE = SECOND*60;
	public static int HOUR = MINUTE*60;
	public static int DAY = HOUR*24;
	
	public static String getTimeFromTicks(int ticks){
		int seconds = getSeconds(ticks);
		int minutes = seconds/60;
		int hours = minutes/60;
		int days = hours/24;
		String time = "00:00";
		if(days > 0){
			int hoursLeft = (hours-(24*hours));
			int minutesLeft = (minutes-(60*hours));
			int secondsLeft = (seconds-(60*minutes));
			time = days+":"+(hoursLeft < 10 ? "0" : "")+hoursLeft+":"+(minutesLeft < 10 ? "0" : "")+minutesLeft+":"+(secondsLeft < 10 ? "0" : "")+secondsLeft;
		}else if(hours > 0){
			int minutesLeft = (minutes-(60*hours));
			int secondsLeft = (seconds-(60*minutes));
			time = hours+":"+(minutesLeft < 10 ? "0" : "")+minutesLeft+":"+(secondsLeft < 10 ? "0" : "")+secondsLeft;
		}else if(minutes > 0){
			int secondsLeft = (seconds-(60*minutes));
			time = minutes+":"+(secondsLeft < 10 ? "0" : "")+secondsLeft;
		}else if(seconds > 0){
			time = "00:"+seconds;
		}
		return time;
	}
	
	public static int getSeconds(int ticks){
		return ticks/SECOND;
	}
	
	public static int getMinutes(int ticks){
		return ticks/MINUTE;
	}
	
	public static int getHours(int ticks){
		return ticks/HOUR;
	}
	
	public static int getDays(int ticks){
		return ticks/DAY;
	}
	
}
