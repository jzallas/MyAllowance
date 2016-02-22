package allowance.fps.com.myallowance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AllowanceWallet {

  protected static final Double ALLOWED_TOTAL = 250.0;

  protected static final String KEY_REMAINING_ALLOWANCE = "PREF_REMAINING_ALLOWANCE";

  private Double mRemaining = null;

  private Context mAppContext;

  public static AllowanceWallet create(Context context) {
    return new AllowanceWallet(context);
  }

  public static AllowanceWallet exchange(AllowanceWallet allowanceWallet) {
    allowanceWallet.mRemaining = ALLOWED_TOTAL;
    return allowanceWallet;
  }

  private AllowanceWallet(Context context) {
    mAppContext = context.getApplicationContext();
  }

  public Double getTotalAllowedAmount() {
    return ALLOWED_TOTAL;
  }

  private void setRemainingAmount(Double remaining) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    prefs.edit().putString(KEY_REMAINING_ALLOWANCE, remaining.toString()).apply();
    mRemaining = remaining;
  }

  public Double getRemainingAmount() {
    if (mRemaining == null) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
      String remaining = prefs.getString(KEY_REMAINING_ALLOWANCE, ALLOWED_TOTAL.toString());
      mRemaining = Double.parseDouble(remaining);
    }
    return mRemaining;
  }

  public void withdraw(Double value) {
    Double newRemaining = getRemainingAmount() - value;
    setRemainingAmount(newRemaining);
  }

  public void deposit(Double value) {
    Double newRemaining = getRemainingAmount() + value;
    setRemainingAmount(newRemaining);
  }
}
