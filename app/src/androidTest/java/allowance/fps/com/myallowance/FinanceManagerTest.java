package allowance.fps.com.myallowance;

import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.List;

import allowance.fps.com.myallowance.database.AllowanceDbHelper;
import allowance.fps.com.myallowance.model.Transaction;


public class FinanceManagerTest extends AndroidTestCase {

  FinanceManager mFinanceManager;

  Calendar mToday;

  public void setUp() throws Exception {
    mFinanceManager = new FinanceManager(getContext());
    mToday = Calendar.getInstance(); // today;
    mFinanceManager.setStartDate(mToday);
  }

  public void tearDown() throws Exception {
    AllowanceDbHelper.deleteAll(getContext());
  }

  public void testGetSavedTotal() throws Exception {
    assertEquals(0.0, mFinanceManager.getSavedTotal());

    // changing the date should update saved total
    Calendar twoWeeksLater = (Calendar) mToday.clone();
    twoWeeksLater.add(Calendar.DAY_OF_YEAR, 14);

    mFinanceManager.setStartDate(twoWeeksLater);

    assertEquals(250.0, mFinanceManager.getSavedTotal());
  }

  public void testGetStartDate() throws Exception {
    assertEquals(mToday, mFinanceManager.getStartDate());
  }

  public void testGetEndDate() throws Exception {

    // changing the date should update saved total
    Calendar twoWeeksLater = (Calendar) mToday.clone();
    twoWeeksLater.add(Calendar.DAY_OF_YEAR, 14);

    assertEquals(twoWeeksLater, mFinanceManager.getEndDate());
  }

  public void testGetRemaining() throws Exception {
    final double initialExpectedRemaining = 250.0;
    assertEquals(initialExpectedRemaining, mFinanceManager.getRemaining());

    final double txValue = 2.35;
    mFinanceManager.performTransaction(new Transaction("title", "desc", txValue, mToday.getTime()));

    final double expectedRemaining = initialExpectedRemaining - txValue;
    assertEquals(expectedRemaining, mFinanceManager.getRemaining());
  }

  public void testPerformTransaction() throws Exception {
    final double initialExpectedRemaining = 250.0;
    assertEquals(initialExpectedRemaining, mFinanceManager.getRemaining());

    final double txValue = 2.35;

    Transaction transaction = new Transaction("title", "desc", txValue, mToday.getTime());
    mFinanceManager.performTransaction(transaction);

    List<Transaction> txList = mFinanceManager.getRecentTransactions();

    assertNotNull(txList);
    assertFalse(txList.isEmpty());

    assertTrue(txList.contains(transaction));

    final double expectedRemaining = initialExpectedRemaining - txValue;
    assertEquals(expectedRemaining, mFinanceManager.getRemaining());
  }

  public void testRemoveTransaction() throws Exception {
    final double initialExpectedRemaining = 250.0;
    assertEquals(initialExpectedRemaining, mFinanceManager.getRemaining());

    Transaction transaction = new Transaction("title", "desc", 2.35, mToday.getTime());
    mFinanceManager.performTransaction(transaction);

    mFinanceManager.removeTransaction(transaction);

    List<Transaction> txList = mFinanceManager.getRecentTransactions();

    assertNotNull(txList);
    assertTrue(txList.isEmpty());

    assertEquals(initialExpectedRemaining, mFinanceManager.getRemaining());
  }

  public void testEditTransaction() throws Exception {
    final double initialExpectedRemaining = 250.0;
    assertEquals(initialExpectedRemaining, mFinanceManager.getRemaining());

    final double initialTxValue = 2.35;

    Transaction transaction = new Transaction("title", "desc", initialTxValue, mToday.getTime());
    mFinanceManager.performTransaction(transaction);


    final double newTxValue = 2.46;
    Transaction newTransaction = new Transaction("title", "desc", newTxValue, mToday.getTime());

    mFinanceManager.editTransaction(transaction, newTransaction);

    List<Transaction> txList = mFinanceManager.getRecentTransactions();

    assertNotNull(txList);
    assertFalse(txList.isEmpty());

    assertTrue(txList.contains(newTransaction));
    assertFalse(txList.contains(transaction));

    final double expectedRemaining = initialExpectedRemaining - newTxValue;
    assertEquals(expectedRemaining, mFinanceManager.getRemaining());
  }
}