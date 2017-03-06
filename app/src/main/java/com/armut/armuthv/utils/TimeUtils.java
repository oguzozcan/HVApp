package com.armut.armuthv.utils;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Locale;

/**
 * Created by oguzemreozcan on 27/05/16.
 */
public class TimeUtils {
    private final static Locale localeTr = new Locale("tr");
    public final static DateTimeFormatter updateDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss").withZone(DateTimeZone.forID("Europe/Moscow")); // .withLocale(localeTr)
    public final static DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd MMMM yyyy HH:mm").withZone(DateTimeZone.forID("Europe/Moscow")); // .withLocale(localeTr)
    public final static DateTimeFormatter dtfOutWithWeek = DateTimeFormat.forPattern("dd MMMM yyyy EEEE HH:mm").withZone(DateTimeZone.forID("Europe/Moscow")); // .withLocale(localeTr)
    private final static DateTimeFormatter dtfOutWOTime = DateTimeFormat.forPattern("dd MMMM yyyy").withZone(DateTimeZone.forID("Europe/Moscow")); // .withLocale(localeTr)
    private final static DateTimeFormatter dtfSimple = DateTimeFormat.forPattern("dd.MM.yyyy HH.mm").withZone(DateTimeZone.forID("Europe/Moscow")); // .withLocale(localeTr)
    private final static DateTimeFormatter dtfSimpleWOTime = DateTimeFormat.forPattern("dd.MM.yyyy").withZone(DateTimeZone.forID("Europe/Moscow"));// .withLocale(localeTr)
    private final static DateTimeFormatter timeSimple = DateTimeFormat.forPattern("HH.mm");
    private final static DateTimeFormatter timeReadable = DateTimeFormat.forPattern("HH:mm");
    public final static DateTimeFormatter dtfOutWOYearShort = DateTimeFormat.forPattern("dd MMM").withZone(DateTimeZone.forID("Europe/Moscow"));//.withLocale(localeTr);//.withZone(DateTimeZone.forID("Europe/Istanbul"));
    public final static DateTimeFormatter dtfOutWOYear = DateTimeFormat.forPattern("dd MMMM HH:mm").withZone(DateTimeZone.forID("Europe/Moscow"));//.withLocale(localeTr);//.withZone(DateTimeZone.forID("Europe/Istanbul"));
    //public final static DateTimeFormatter dayOfWeek = DateTimeFormat.forPattern("dddd");//.withLocale(localeTr);//.withZone(DateTimeZone.forID("Europe/Istanbul"));
    //public static final DateTimeFormatter dfISO = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withChronology(ISOChronology.getInstanceUTC());//.withLocale(TimeUtil.localeTr)
   //public static final DateTimeFormatter dfISO = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSK'Z'").withChronology(ISOChronology.getInstanceUTC());//.withLocale(TimeUtil.localeTr)
    //.withChronology(ISOChronology.getInstanceUTC()); //  //,SSS
    public static final DateTimeFormatter dfISOMS = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withChronology(ISOChronology.getInstanceUTC()); // "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'"
    private static final String TAG = "TimeUtil";//.withLocale(TimeUtil.localeTr).withChronology(ISOChronology.getInstanceUTC())


    public static String calculateTimeInBetween(String date) {
        Log.d(TAG, "Date: " + date);
        if(date == null){
            return "";
        }
        DateTime startDate = TimeUtils.getDateTime(date, TimeUtils.dfISOMS);//DateTime.now(); // now() : since Joda Time 2.0 // ISO idi
        DateTime endDate = DateTime.now();//new DateTime(2011, 12, 25, 0, 0);
        Period period = new Period(endDate, startDate, PeriodType.dayTime());//MonthDayTime());
        period.normalizedStandard(PeriodType.dayTime());
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix(" GÜN ", " GÜN ")
                .appendHours().appendSuffix(" SA ", " SA ")
                .toFormatter();
        String dateToFormat = formatter.print(period);
        Log.d(TAG, "TIMEx: " + dateToFormat);
        if(dateToFormat.equals("")){
            return "";
        }
        if(dateToFormat.startsWith("-")){
            dateToFormat = dateToFormat.substring(1);
            dateToFormat = dateToFormat + "ÖNCE";
        }else {
            dateToFormat = dateToFormat + "SONRA";
        }

        return dateToFormat;
    }

    public static String getTodayJoda(boolean humanReadable) {
        DateTime dt = DateTime.now();
        if (humanReadable) {
            return dtfOut.print(dt);
        }
        return updateDateFormat.print(dt);
    }

    public static String getTodayJoda(DateTimeFormatter dtf) {
        DateTime dt = DateTime.now();
        return dtf.print(dt);
    }

    public static String convertDateTimeFormat(DateTimeFormatter fromFormat, DateTimeFormatter toFormat, String date) {
        if(date == null){
            return "";
        }
        try {
            DateTime dt = fromFormat.parseDateTime(date).toDateTime();
            return dt.toString(toFormat);//toFormat.print(fromFormat.parseDateTime(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    public static String getDayOfWeek(DateTimeFormatter fromFormat, String date){
        DateTime dt = fromFormat.parseDateTime(date).toDateTime();
        DateTime.Property pDoW = dt.dayOfWeek();
        return pDoW.getAsText();
    }

    public static int getDayOfWeekAsNumber(DateTimeFormatter fromFormat, String date){
        DateTime dt = fromFormat.parseDateTime(date).toDateTime();
        return dt.getDayOfWeek();
    }

    public static DateTime getDateTime(String dateTimeText, boolean humanReadable) {
        DateTime dt;
        if (humanReadable) {
            dt = dtfOut.parseDateTime(dateTimeText).toDateTime();
        } else {
            dt = dtfSimple.parseDateTime(dateTimeText).toDateTime();
        }
        return dt;
    }

    private static DateTime getDateTime(String dateTimeText, DateTimeFormatter dtf) {
        try {
            return dtf.parseDateTime(dateTimeText).toDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return dfISOMS.parseDateTime(dateTimeText).toDateTime();
        }
    }

    public static String convertSimpleDateToReadableForm(String date, boolean isTimeIncluded) {
        return isTimeIncluded ? dtfOut.print(dtfSimple.parseDateTime(date)) :
                dtfOutWOTime.print(dtfSimpleWOTime.parseDateTime(date));
    }

    public static String convertSimpleDateToApiForm(String date, boolean isTimeIncluded) {
        return isTimeIncluded ? dfISOMS.print(dtfOut.parseDateTime(date)) :
                dfISOMS.print(dtfOutWOTime.parseDateTime(date));
    }

    public static String convertSimpleTimeToReadableForm(String time) {
        return timeReadable.print(timeSimple.parseDateTime(time));
    }

    public static String convertReadableDateToSimpleForm(String date) {
        return dtfSimple.print(dtfOut.parseDateTime(date));
    }


    public static String getDateTimeFromMillis(long milliseconds){
        DateTime someDate = new DateTime(milliseconds, DateTimeZone.UTC);
        return dfISOMS.print(someDate);
    }

    public static String getDaysBeforeOrAfterToday(int days, boolean humanReadable) {
        DateTime dateTime = DateTime.now();
        if (days > 0) {
            dateTime = dateTime.plusDays(days);
        } else {
            dateTime = dateTime.minusDays(-1 * days);
        }
        if (humanReadable) {
            return dtfOut.print(dateTime);
        }
        return updateDateFormat.print(dateTime);
    }

    public static int compareDateWithToday(String dateString, DateTimeFormatter dtf){
                DateTime startDate = dtf.parseDateTime(dateString);
        Period p = new Period(startDate, DateTime.now(), PeriodType.dayTime());
        //int years = p.getYears();
        //int months = p.getMonths();
        int days = p.getDays();
        int hours = p.getHours();
        //int minutes = p.getMinutes();
        Log.d(TAG, "DIFFERENCE IN DAYS: " + days + " - DIFFERENCE IN HOURS: " + hours);
        return days;
    }

}
