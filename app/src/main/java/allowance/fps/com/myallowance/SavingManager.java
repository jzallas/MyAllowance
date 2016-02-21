package allowance.fps.com.myallowance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavingManager {

  private Double mTotalSaved = null;

  private static final String KEY_TOTAL_SAVED = "PREF_TOTAL_SAVED";

  private final Context mAppContext;

  public SavingManager(Context context) {
    mAppContext = context.getApplicationContext();
  }

  /**
   * get the total saved value
   */
  public Double getTotalSavedValue() {
    if (mTotalSaved == null) {
      SharedPreferences sharedPreferences = PreferenceManager
          .getDefaultSharedPreferences(mAppContext);
      String saved = sharedPreferences.getString(KEY_TOTAL_SAVED, "0.0");
      mTotalSaved = Double.parseDouble(saved);
    }
    return mTotalSaved;
  }

  private void setTotalSavedValue(Double totalSaved) {
    mTotalSaved = totalSaved;
    SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(mAppContext);
    sharedPreferences.edit().putString(KEY_TOTAL_SAVED, mTotalSaved.toString()).apply();
  }

  /**
   * Exchange your old allowance for a new allowance
   *
   * @param allowanceWallet the old wallet
   * @return a new wallet
   */
  public AllowanceWallet getNewAllowance(AllowanceWallet allowanceWallet) {
    if (allowanceWallet != null) {
      Double newSavedValue = getTotalSavedValue() + allowanceWallet.getRemainingAmount();
      setTotalSavedValue(newSavedValue);
    }
    return new AllowanceWallet();
  }
}
