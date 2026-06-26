package de.gimik.apps.gpstracker.backend.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtility {
    private static final String INPUT_DATE_FORMAT = "dd.MM.yyyy";
    private static final String DD_MM_YYYY_HH_MM = "dd.MM.yyyy HH:mm";
    private static final String DD_MM_YYYY_HH_MM_SS = "dd.MM.yyyy HH:mm:ss";
    private static final String MAIL_REMINDER_DATE_FORMAT = "dd. MMMM yyyy";
	private static final String UPLOAD_DATE_FORMAT = "yyyy-MM-dd";
	private static final String UPLOAD_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	public static final long MILISECOND_PER_DAY = 1000 * 60 * 60 * 24;
	private static final SimpleDateFormat dateParamFormat = new SimpleDateFormat("ddMMyyyy");
	public static final SimpleDateFormat Format_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String gpstracker_DATE_FORMAT = "dd/MM/yyyy";
	private static final String TIME_FORMAT = "HH:mm";
	public static final long date2Tick(Date date){
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		
		return calendar.getTimeInMillis();
	}
	
	public static final Date tick2Date(long tick){
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTimeInMillis(tick);
		
		return calendar.getTime();
	}
	
	public static final boolean isDateTextValid(String dateText, String format){
		DateFormat df = new SimpleDateFormat(format);
		
		try {
			df.parse(dateText);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static final boolean isUploadDateTextValid(String dateText){
		return isDateTextValid(dateText, UPLOAD_DATE_FORMAT);
	}
	
	public static final boolean isUploadDateTimeTextValid(String dateText){
		return isDateTextValid(dateText, UPLOAD_TIME_FORMAT);
	}
	
	public static final Date parseUploadDate(String dateText){
		DateFormat df = new SimpleDateFormat(UPLOAD_DATE_FORMAT);
		
		try {
			return df.parse(dateText);
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final Date parseUploadTime(String dateText){
		DateFormat df = new SimpleDateFormat(UPLOAD_TIME_FORMAT);
		
		try {
			return df.parse(dateText);
			
		} catch (Exception e) {
			return null;
		}
	}
	
	 public static final Date parseInputTime (String timeText){
	        DateFormat df = new SimpleDateFormat(TIME_FORMAT);

	        try {
	            return df.parse(timeText);

	        } catch (Exception e) {
	            return null;
	        }
	    }
	public static final String formatUploadDate(Date date){
		DateFormat df = new SimpleDateFormat(UPLOAD_DATE_FORMAT);
		
		try {
			return df.format(date);
			
		} catch (Exception e) {
			return "";
		}
	}
	  public static final Date parseInput_DD_MM_YYYY_HH_MM(String dateText){
	        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);

	        try {
	            return df.parse(dateText);

	        } catch (Exception e) {
	            return null;
	        }
	    }
	  public static final Date parseInputDategpstracker(String dateText){
	        DateFormat df = new SimpleDateFormat(gpstracker_DATE_FORMAT);

	        try {
	            return df.parse(dateText);

	        } catch (Exception e) {
	            return null;
	        }
	    }
	  public static final Date parseInput_DD_MM_YYYY_HH_MM_SS(String dateText){
	        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS);

	        try {
	            return df.parse(dateText);

	        } catch (Exception e) {
	            return null;
	        }
	    }
	  public static final Timestamp parseInputToTimeStamp_DD_MM_YYYY_HH_MM(String dateText){
	        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);

	        try {
	        	Timestamp fromTS1 = new Timestamp(df.parse(dateText).getTime());
	            return fromTS1;

	        } catch (Exception e) {
	            return null;
	        }
	    }

	    public static final String formatInputDateToString_DD_MM_YYYY_HH_MM(Date date){
	        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
	        try {
	            return df.format(date);
	        } catch (Exception e) {
	            return "";
	        }
	    }
	    public static final String formatInputDateToString_DD_MM_YYYY_HH_MM_SS(Date date){
	        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS);
	        try {
	            return df.format(date);
	        } catch (Exception e) {
	            return "";
	        }
	    }
	  public static final Date parseInputDouble(Double value){
	        try {
	        	if(value == null)
	        		return null;
	        	Long timeInMillis = Long.parseLong(value.toString());
	        	Calendar calendar = Calendar.getInstance();
	  	      	calendar.setTimeInMillis(timeInMillis);
	            return calendar.getTime();
	        } catch (Exception e) {
	            return null;
	        }
	    }
    public static final Date parseInputDate(String dateText){
        DateFormat df = new SimpleDateFormat(INPUT_DATE_FORMAT);

        try {
            return df.parse(dateText);

        } catch (Exception e) {
            return null;
        }
    }

    public static final String formatInputDate(Date date){
        DateFormat df = new SimpleDateFormat(INPUT_DATE_FORMAT);

        try {
            return df.format(date);

        } catch (Exception e) {
            return "";
        }
    }

    public static final String formatMailReminderDate(Date date){
        DateFormat df = new SimpleDateFormat(MAIL_REMINDER_DATE_FORMAT);

        try {
            return df.format(date);

        } catch (Exception e) {
            return "";
        }
    }

	public static int getDayBetween(Date fromDate, Date toDate) {
		Date fromDateOnly = getDateOnly(fromDate);
		Date toDateOnly = getDateOnly(toDate);

		return (int) ((toDateOnly.getTime() - fromDateOnly.getTime()) / MILISECOND_PER_DAY);
	}
	
	public static final Date getDateOnly(Date date) {
		if (date == null) {
			return null;
		}

		try {
			return dateParamFormat.parse(dateParamFormat.format(date));
		} catch (ParseException e) {
			return null;
		}
	}

    public static Date parseFromDate(String dateText) {
        Date fromDate = parseInputDate(dateText);

        if (fromDate != null)
            return fromDate;

        return DataUtility.getMinDate();
    }

    public static Date parseToDate(String dateText) {
        Date toDate = parseInputDate(dateText);

        if (toDate != null)
            return toDate;

        return DataUtility.getMaxDate();
    }

    public static Date getBeginTimeOfDay(Date date){
        if (date == null)
            return null;
        DateTime dateTime = new DateTime(date);
        MutableDateTime mutableDateTime = dateTime.toMutableDateTime();
        mutableDateTime.set(DateTimeFieldType.hourOfDay(),0);
        mutableDateTime.set(DateTimeFieldType.minuteOfHour(),0);
        mutableDateTime.set(DateTimeFieldType.secondOfMinute(),0);
        mutableDateTime.set(DateTimeFieldType.millisOfSecond(),0);

        return  mutableDateTime.toDate();
    }

    public static Date getEndTimeOfDay(Date date){
        if (date == null)
            return null;
        DateTime dateTime = new DateTime(date);
        MutableDateTime mutableDateTime = dateTime.toMutableDateTime();
        mutableDateTime.set(DateTimeFieldType.hourOfDay(),23);
        mutableDateTime.set(DateTimeFieldType.minuteOfHour(),59);
        mutableDateTime.set(DateTimeFieldType.secondOfMinute(),59);
        mutableDateTime.set(DateTimeFieldType.millisOfSecond(), 999);

        return  mutableDateTime.toDate();
    }
    public static void main(String[] avg) {
    	SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
    	dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    	
    	//Local time zone   
    	SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    	dateFormatLocal.setTimeZone(TimeZone.getTimeZone("UTC"));
    	//Time in GMT
    	try {
//    		2019-05-09T06:23:00+07:00
			String a = dateFormatGmt.format(new Date(1557489660000L));
			Date b = dateFormatLocal.parse("2019-05-09 06:23:00 GMT+00:00");
			TimeZone  tz = dateFormatGmt.getTimeZone();
			Date m = new Date(b.getTime() - b.getTimezoneOffset() * 60000 );
			String c = dateFormatLocal.format(b);
			System.out.println(a);
			System.out.println(b);
			System.out.println(m);
			System.out.println(c);
			 DateFormat ddf = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
			 ddf.setTimeZone(TimeZone.getTimeZone("GMT"));
			 Date e = ddf.parse("09/09/2019 06:23");
			 
			System.out.println(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static Date mergeDateTime(Date date , Date time){
    	Date result = null;
    	if(date == null || time == null)
    		return result;
    	Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(date);	
		Calendar calendarTime = Calendar.getInstance();
		calendarTime.setTime(time);
		calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
		calendarDate.set(Calendar.MINUTE,calendarTime.get(Calendar.MINUTE));
		calendarDate.set(Calendar.SECOND,calendarTime.get(Calendar.SECOND));
		result = calendarDate.getTime();
    	return result;
    }
}
