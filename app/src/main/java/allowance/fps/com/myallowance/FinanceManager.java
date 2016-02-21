package allowance.fps.com.myallowance;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import allowance.fps.com.myallowance.database.AllowanceDbHelper;
import allowance.fps.com.myallowance.model.Transaction;

public class FinanceManager {

  private SavingManager mSavingManager;

  private AllowanceWallet mCurrentAllowance;

  private Calendar mStartDate;

  final private AllowanceDbHelper mDbHelper;

  public FinanceManager(Context context) {
    mDbHelper = new AllowanceDbHelper(context.getApplicationContext());
    mSavingManager = new SavingManager();
  }

  public double getSavedTotal() {
    return mSavingManager.getTotalSavedValue();
  }

  public void setStartDate(Calendar startDate) {
    mStartDate = startDate;
    // when the start date changes, a new wallet is assigned
    mCurrentAllowance = mSavingManager.getNewAllowance(mCurrentAllowance);
  }

  public Calendar getStartDate() {
    return mStartDate;
  }

  public Calendar getEndDate() {
    if (mStartDate == null) {
      return null;
    }
    Calendar endDate = (Calendar) mStartDate.clone();
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
