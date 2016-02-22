package allowance.fps.com.myallowance.util;

import android.annotation.SuppressLint;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtil {

    @SuppressLint("DefaultLocale")
    public static String getMoney(double number) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(number);
    }
}
