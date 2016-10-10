package DataNamerParser;

public class DateHandler 
{

	public static boolean IsLeapYear(int year)
	{
		return (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0));
	}

	public static int ParseMonthFromDayOfYear(int year, int dayOfYear)
	{
		int Jan = 31;
		int Feb = (IsLeapYear(year) ? 29 : 28) + Jan;
		int Mar = 31 + Feb;
		int Apr = 30 + Mar;
		int May = 31 + Apr;
		int Jun = 30 + May;
		int Jul = 31 + Jun;
		int Aug = 31 + Jul;
		int Sep = 30 + Aug;
		int Oct = 31 + Sep;
		int Nov = 30 + Oct;
		int Dec = 31 + Nov;

		if (dayOfYear <= Jan)
		{
			return 1;
		}
		else if (dayOfYear <= Feb)
		{
			return 2;
		}
		else if (dayOfYear <= Mar)
		{
			return 3;
		}
		else if (dayOfYear <= Apr)
		{
			return 4;
		}
		else if (dayOfYear <= May)
		{
			return 5;
		}
		else if (dayOfYear <= Jun)
		{
			return 6;
		}
		else if (dayOfYear <= Jul)
		{
			return 7;
		}
		else if (dayOfYear <= Aug)
		{
			return 8;
		}
		else if (dayOfYear <= Sep)
		{
			return 9;
		}
		else if (dayOfYear <= Oct)
		{
			return 10;
		}
		else if (dayOfYear <= Nov)
		{
			return 11;
		}
		else
		{
			return 12;
		}
	}

	public static int ParseDayFromDayOfYear(int year, int dayOfYear)
	{
		int Jan = 31;
		int Feb = (IsLeapYear(year) ? 29 : 28) + Jan;
		int Mar = 31 + Feb;
		int Apr = 30 + Mar;
		int May = 31 + Apr;
		int Jun = 30 + May;
		int Jul = 31 + Jun;
		int Aug = 31 + Jul;
		int Sep = 30 + Aug;
		int Oct = 31 + Sep;
		int Nov = 30 + Oct;
		int Dec = 31 + Nov;

		if (dayOfYear <= Jan)
		{
			return dayOfYear;
		}
		else if (dayOfYear <= Feb)
		{
			return dayOfYear - Jan;
		}
		else if (dayOfYear <= Mar)
		{
			return dayOfYear - Feb;
		}
		else if (dayOfYear <= Apr)
		{
			return dayOfYear - Mar;
		}
		else if (dayOfYear <= May)
		{
			return dayOfYear - Apr;
		}
		else if (dayOfYear <= Jun)
		{
			return dayOfYear - May;
		}
		else if (dayOfYear <= Jul)
		{
			return dayOfYear - Jun;
		}
		else if (dayOfYear <= Aug)
		{
			return dayOfYear - Jul;
		}
		else if (dayOfYear <= Sep)
		{
			return dayOfYear - Aug;
		}
		else if (dayOfYear <= Oct)
		{
			return dayOfYear - Sep;
		}
		else if (dayOfYear <= Nov)
		{
			return dayOfYear - Oct;
		}
		else
		{
			return dayOfYear - Nov;
		}
	}


}
