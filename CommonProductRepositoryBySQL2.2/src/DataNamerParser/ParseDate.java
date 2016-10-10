package DataNamerParser;

import java.util.Date;


public class ParseDate 
{
	
	public static Date ParseDateTime(String yyyydayofyearhhmmss){	
		int year = Integer.parseInt(yyyydayofyearhhmmss.substring(0, 4))-1900;//将字符串转化为等效int类型
		int dayOfYear = Integer.parseInt(yyyydayofyearhhmmss.substring(4, 7));
		int month = DateHandler.ParseMonthFromDayOfYear(year, dayOfYear)-1;
		int day = DateHandler.ParseDayFromDayOfYear(year, dayOfYear);
		int hour = Integer.parseInt(yyyydayofyearhhmmss.substring(7, 9));
		int minute = Integer.parseInt(yyyydayofyearhhmmss.substring(9, 11));
		int second = Integer.parseInt(yyyydayofyearhhmmss.substring(11, 13));
		return new java.util.Date(year, month, day, hour, minute, second);
	}	
}
