package allowance.fps.com.myallowance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import allowance.fps.com.myallowance.database.AllowanceDbHelper;
import allowance.fps.com.myallowance.model.Transaction;

public class DefaultAllowanceManager implements AllowanceManager {

  private final String KEY_START_DATE = "start_date";
  private final String KEY_HOBBY_AMOUNT = "hobby_amount";
  private final String KEY_TOTAL_SAVED = "total_saved";

  private final Context mAppContext;

  private AllowanceDbHelper mDbHelper;

  private static final double DEFAULT_ALLOCATED_AMOUNT = 250.0;
  private static final int DEFAULT_DAY_SPAN = 14;

  public DefaultAllowanceManager(Context context) {
    mAppContext = context.getApplicationContext();
    mDbHelper = new AllowanceDbHelper(context);
  }

  @Override
  public void setStartDate(Date date) {
    if (date == null) {
      return;
    }
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    preferences.edit().putLong(KEY_START_DATE, date.getTime()).apply();
  }

  @Override
  public Date getStartDate() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    if (preferences.contains(KEY_START_DATE)) {
      return new Date(preferences.getLong(KEY_START_DATE, 0));
    }
    return null;
  }

  @Override
  public Date getEndDate() {
    Date startDate = getStartDate();
    if (startDate != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(startDate);
      calendar.add(Calendar.DAY_OF_YEAR, DEFAULT_DAY_SPAN);
      return calendar.getTime();
    }
    return null;
  }

  @Override
  public void setAllocatedHobbyAmount(Double amount) {
    if (amount == null) {
      return;
    }
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    preferences.edit().putString(KEY_HOBBY_AMOUNT, amount.toString()).apply();
  }

  @Override
  public Double getAllocatedHobbyAmount() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    Double hobbyAmount = Double.parseDouble(preferences.getString(KEY_HOBBY_AMOUNT, "-1"));
    return hobbyAmount < 0 ? null : hobbyAmount;
  }

  @Override
  public Double getTotalAllowedAmount() {
    return DEFAULT_ALLOCATED_AMOUNT;
  }

  @Override
  public Double getTotalSaved() {
    return Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(mAppContext)
        .getString(KEY_TOTAL_SAVED, "0"));
  }

  @Override
  public boolean performTransaction(Transaction transaction) {
    return mDbHelper.insertTransaction(transaction);
  }

  @Override
  public boolean editTransaction(Transaction transaction) {
    // TODO: 2/20/16 edit db item
    return false;
  }

  @Override
  public boolean deleteTransaction(Transaction transaction) {
    return mDbHelper.deleteTransaction(transaction);
  }

  @Override
  public List<Transaction> getLatestTenTransactions() {
    List<Transaction> transactionList = mDbHelper.getTransactionList();
    if (transactionList.size() <= 10) {
      return transactionList;
    }
    return transactionList.subList(0, 10);
  }
}
