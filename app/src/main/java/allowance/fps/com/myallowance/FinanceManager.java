package allowance.fps.com.myallowance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import allowance.fps.com.myallowance.database.AllowanceDbHelper;
import allowance.fps.com.myallowance.model.Transaction;

public class FinanceManager {

  private static final String KEY_START_DATE = "PREF_START_DATE";

  private SavingManager mSavingManager;

  private AllowanceWallet mCurrentAllowance;

  private Calendar mStartDate;

  final Context mAppContext;

  final private AllowanceDbHelper mDbHelper;

  public FinanceManager(Context context) {
    mAppContext = context.getApplicationContext();
    mDbHelper = new AllowanceDbHelper(mAppContext);
    mSavingManager = new SavingManager(mAppContext);
  }

  public double getSavedTotal() {
    return mSavingManager.getTotalSavedValue();
  }

  public void setStartDate(Calendar startDate) {
    SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(mAppContext);
    sharedPreferences.edit().putLong(KEY_START_DATE, startDate.getTime().getTime()).apply();
    mStartDate = startDate;

    // when the start date changes, a new wallet is assigned
    mCurrentAllowance = mSavingManager.getNewAllowance(mCurrentAllowance);
  }

  public Calendar getStartDate() {
    if (mStartDate == null) {
      SharedPreferences sharedPreferences = PreferenceManager
          .getDefaultSharedPreferences(mAppContext);

      long startTime = sharedPreferences.getLong(KEY_START_DATE, -1);

      if (startTime >= 0) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date(startTime));
        mStartDate = startDate;
      }
    }
    return mStartDate;
  }

  public Calendar getEndDate() {
    if (getStartDate() == null) {
      return null;
    }
    Calendar endDate = (Calendar) getStartDate().clone();
    endDate.add(Calendar.DAY_OF_YEAR, 14);
    return endDate;
  }

  public double getRemaining() {
    return mCurrentAllowance.getRemainingAmount();
  }

  //region Transactions
  public void performTransaction(Transaction transaction) {
    //save the transaction in the history
    if (mDbHelper.insertTransaction(transaction)) {
      //edit your wallet
      mCurrentAllowance.withdraw(transaction.getAmountUSD());
    }
  }

  public void removeTransaction(Transaction transaction) {
    if (mDbHelper.deleteTransaction(transaction)) {
      mCurrentAllowance.deposit(transaction.getAmountUSD());
    }
  }

  public void editTransaction(Transaction originalTransaction, Transaction newTransaction) {
    if (mDbHelper.deleteTransaction(originalTransaction)) {
      mCurrentAllowance.deposit(originalTransaction.getAmountUSD());
      //save the transaction in the history
      if (mDbHelper.insertTransaction(newTransaction)) {
        //edit your wallet
        mCurrentAllowance.withdraw(newTransaction.getAmountUSD());
      }
    }
  }

  public List<Transaction> getRecentTransactions() {
    return mDbHelper.getTransactionList();
  }
  //endregion
}
