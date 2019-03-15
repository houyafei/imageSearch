package sample.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServiceUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT);

    public static String dateFormat(Date date) {
        return date == null ? "null date" : FORMAT.format(date);
    }

    public static Date parse2Date(String dateStr) {
        try {
            return FORMAT.parse(dateStr);
        } catch (ParseException e) {
            System.out.println(dateStr + " is not the format: " + DATE_FORMAT);
        }
        return null;
    }
}
