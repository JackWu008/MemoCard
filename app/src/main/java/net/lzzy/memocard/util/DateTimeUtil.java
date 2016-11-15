package net.lzzy.memocard.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Administrator on 2016/4/19.
 * date转化成yyyy-MM-dd HH:mm
 */
public class DateTimeUtil {
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);//把时间转化成2016-05-20 11:11
    public static final SimpleDateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
}
