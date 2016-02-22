package allowance.fps.com.myallowance.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by eyau on
 * 2/21/16.
 */
public class DateUtil {

    private static final DateTimeFormatter mFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");

    public static String getShortDate(Date date) {
        return mFormatter.print(date.getTime());
    }
}
