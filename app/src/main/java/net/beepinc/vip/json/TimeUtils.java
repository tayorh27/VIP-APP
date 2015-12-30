package net.beepinc.vip.json;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tayo on 10/4/2015.
 */
public class TimeUtils {

    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    public final static long ONE_WEEK = ONE_DAY * 7;

    public final static long ONE_MONTH = ONE_WEEK * 4;

    public final static long ONE_YEAR = ONE_MONTH * 12;

    private TimeUtils() {
    }

    public static String setAgo(long duration){

        String outTime = "";

        Date currentDate = new Date();
        long currentTime = currentDate.getTime();

        long diff = currentTime - duration;

        double years = Math.floor((double) diff/ONE_YEAR);
        double months = Math.floor((double) diff/ONE_MONTH);
        double weeks = Math.floor((double)diff/ONE_WEEK);

        double days = Math.floor((double) diff/ONE_DAY);
        double hours = Math.floor((double) (diff % ONE_DAY) / ONE_HOUR);
        double minutes = Math.floor((double) (diff % ONE_HOUR) / ONE_MINUTE);
        double seconds = Math.floor((double) (diff % ONE_MINUTE) / ONE_SECOND);

        int yr = (int)years;
        int mt = (int)months;
        int w = (int)weeks;

        int d = (int)days;
        int h = (int)hours;
        int m = (int)minutes;
        int s = (int)seconds;

        if(years > 0){
            if(yr == 1){
                outTime = String.valueOf(yr)+"yr ago";
            }else{
                outTime = String.valueOf(yr)+"yrs ago";
            }
        }else if(months > 0){
            if(mt == 1){
                outTime = String.valueOf(mt)+"mon ago";
            }else{
                outTime = String.valueOf(mt)+"mons ago";
            }
        }else if(weeks > 0){
            if(w == 1){
                outTime = String.valueOf(w)+"wk ago";
            }else{
                outTime = String.valueOf(w)+"wks ago";
            }
        }else if(days > 0){
            if(d == 1){
                outTime = String.valueOf(d)+"day ago";
            }else {
                outTime = String.valueOf(d) + "days ago";
            }
        }else if(hours > 0){
            if(h == 1) {
                outTime = String.valueOf(h) + "hr ago";
            }else {
                outTime = String.valueOf(h) + "hrs ago";
            }
        }else if(minutes > 0){
            if(m == 1) {
                outTime = String.valueOf(m) + "min ago";
            }else {
                outTime = String.valueOf(m) + "mins ago";
            }
        }else if(seconds > 0){
            outTime = String.valueOf(s)+"secs ago";
        }else {
            outTime = "moments ago";
        }

        return outTime;
    }

    /**
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" and ");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            return res.toString();
        } else {
            return "0 second";
        }
    }


    public static void main(String args[]) {
        System.out.println(millisToLongDHMS(123));
        System.out.println(millisToLongDHMS((5 * ONE_SECOND) + 123));
        System.out.println(millisToLongDHMS(ONE_DAY + ONE_HOUR));
        System.out.println(millisToLongDHMS(ONE_DAY + 2 * ONE_SECOND));
        System.out.println(millisToLongDHMS(ONE_DAY + ONE_HOUR + (2 * ONE_MINUTE)));
        System.out.println(millisToLongDHMS((4 * ONE_DAY) + (3 * ONE_HOUR)
                + (2 * ONE_MINUTE) + ONE_SECOND));
        System.out.println(millisToLongDHMS((5 * ONE_DAY) + (4 * ONE_HOUR)
                + ONE_MINUTE + (23 * ONE_SECOND) + 123));
        System.out.println(millisToLongDHMS(42 * ONE_DAY));
        /*
          output :
                0 second
                5 seconds
                1 day, 1 hour
                1 day and 2 seconds
                1 day, 1 hour, 2 minutes
                4 days, 3 hours, 2 minutes and 1 second
                5 days, 4 hours, 1 minute and 23 seconds
                42 days
         */

    }
}