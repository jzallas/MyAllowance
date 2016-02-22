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

  protected static final String KEY_START_DATE = "PREF_START_DATE";

  private SavingManager mSavingManager;

  private AllowanceWallet mCurrentAllowance;

  private Calendar mStartDate;

  final Context mAppContext;

  final private AllowanceDbHelper mDbHelper;

  /**
   * Create a new {@link FinanceManager}
   *
   * @param context
   */
  public FinanceManager(Context context) {
    mAppContext = context.getApplicationContext();
    mDbHelper = new AllowanceDbHelper(mAppContext);
    mSavingManager = new SavingManager(mAppContext);
    if (getStartDate() != null) {
      // only get a wallet if a startDate was set
      // if a start date isn't set, we'll get a new wallet when it does get set
      mCurrentAllowance = mSavingManager.getNewAllowance(null);
    }

    handleExpired();
  }

  private void handleExpired() {
    Calendar endDate = getEndDate();
    while (endDate != null && endDate.before(Calendar.getInstance())) {
      setStartDate(endDate);
      endDate = getEndDate();
    }
  }

  /**
   * Check your total saved value
   *
   * @return the total amount saved from rollovers
   */
  public double getSavedTotal() {
    checkForDateSet();
    return mSavingManager.getTotalSavedValue();
  }

  /**
   * Set the beginning of your cycle
   *
   * @param startDate
   */
  public void setStartDate(Calendar startDate) {
    SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(mAppContext);
    sharedPreferences.edit().putLong(KEY_START_DATE, startDate.getTime().getTime()).apply();
    mStartDate = startDate;

    // when the start date changes, a new wallet is assigned
    mCurrentAllowance = mSavingManager.getNewAllowance(mCurrentAllowance);
  }

  /**
   * Get the beginning of your cycle
   *
   * @return
   */
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

  private void checkForDateSet() {
    if (getStartDate() == null) {
      throw new RuntimeException(
          "You must set the startDate before you can use this FinanceManager");
    }
  }

  /**
   * Get the end of your cycle
   *
   * @return
   */
  public Calendar getEndDate() {
    if (getStartDate() == null) {
      return null;
    }
    Calendar endDate = (Calendar) getStartDate().clone();
    endDate.add(Calendar.DAY_OF_YEAR, 14);
    return endDate;
  }

  /**
   * Get the amount remaining that you can spend in this cycle
   *
   * @return
   */
  public double getRemaining() {
    checkForDateSet();
    return mCurrentAllowance.getRemainingAmount();
  }

  /**
   * Get maximum amount that you're allowed to spend in this cycle
   *
   * @return
   */
  public double getAllowed() {
    checkForDateSet();
    return AllowanceWallet.ALLOWED_TOTAL;
  }

  //region Transactions

  /**
   * Add a transaction to the ledger. This will update your amount remaining
   *
   * @param transaction
   */
  public void performTransaction(Transaction transaction) {
    checkForDateSet();
    //save the transaction in the history
    if (mDbHelper.insertTransaction(transaction)) {
      //edit your wallet
      mCurrentAllowance.withdraw(transaction.getAmountUSD());
    }
  }

  /**
   * Remove a transaction from the ledger. This will update your amount remaining.
   *
   * @param transaction
   */
  public void removeTransaction(Transaction transaction) {
    checkForDateSet();
    if (mDbHelper.deleteTransaction(transaction)) {
      mCurrentAllowance.deposit(transaction.getAmountUSD());
    }
  }

  /**
   * Edit a transaction from the ledger. This will update your amount remaining.
   *
   * @param originalTransaction
   * @param newTransaction
   */
  public void editTransaction(Transaction originalTransaction, Transaction newTransaction) {
    checkForDateSet();
    if (mDbHelper.deleteTransaction(originalTransaction)) {
      mCurrentAllowance.deposit(originalTransaction.getAmountUSD());
      //save the transaction in the history
      if (mDbHelper.insertTransaction(newTransaction)) {
        //edit your wallet
        mCurrentAllowance.withdraw(newTransaction.getAmountUSD());
      }
    }
  }

  /**
   * Get a list of the past transactions
   *
   * @return
   */
  public List<Transaction> getRecentTransactions() {
    checkForDateSet();
    return mDbHelper.getTransactionList();
  }
  //endregion
}
