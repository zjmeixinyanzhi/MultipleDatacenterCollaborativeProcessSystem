package FileOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建时间：2015-1-14 下午10:26:40
 * 项目名称：TimeConsumeCount
 * 2015-1-14
 * @author 张杰
 * @version 1.0
 * 文件名称：countConsumeTime.java
 * 类说明：耗时统计类
 */
public class TimeConsumeCount {
	// 起始时间
	public Date startTime = null;
	// 终止时间
	public Date endTime = null;
	// 消耗时间
	public long timeSpan = 0;
	// 设置日期格式
	public SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// 获取时间格式
	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	// 设置时间格式
	public void setDateFormat(SimpleDateFormat df) {
		this.dateFormat = df;
	}

	// 获取时间跨度 毫秒计
	public long getTimeSpan() {
		timeSpan = endTime.getTime() - startTime.getTime();
		return timeSpan;
	}	

	// 时间转换成时分秒
	public String getTimeSpanByFormat() {
		String formatTimeSpan = "0秒";
		long temp = timeSpan / 1000;
		String format;
		Object[] array;
		Integer hours = (int) (temp / (60 * 60));
		Integer minutes = (int) (temp / 60 - hours * 60);
		Integer seconds = (int) (temp - minutes * 60 - hours * 60 * 60);
		if (hours > 0) {
			format = "%1$,d时%2$,d分%3$,d秒";
			array = new Object[] { hours, minutes, seconds };
		} else if (minutes > 0) {
			format = "%1$,d分%2$,d秒";
			array = new Object[] { minutes, seconds };
		} else {
			format = "%1$,d秒";
			array = new Object[] { seconds };
		}
		formatTimeSpan = String.format(format, array);

		return formatTimeSpan;
	}

	public Date getStartTime() {
		return startTime;
	}

	// 当前时间
	public void setStartTimeByCurrentTime() {
		this.startTime = new Date();
	}

	// 指定时间
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	// 当前时间
	public void setEndTimeByCurrentTime() {
		this.endTime = new Date();
	}

	// 指定时间
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public static void main(String[] args) throws InterruptedException {
		TimeConsumeCount countConsumeTime = new TimeConsumeCount();
		Date starttime = null;
		try {
			starttime = countConsumeTime.getDateFormat().parse(
					"2014-10-15 19:03:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		countConsumeTime.setStartTime(starttime);
		countConsumeTime.setStartTimeByCurrentTime();
		Thread.sleep(50);
		countConsumeTime.setEndTimeByCurrentTime();
		System.out.println(countConsumeTime.getTimeSpan());
		System.out.println(countConsumeTime.getTimeSpanByFormat());
	}

}
