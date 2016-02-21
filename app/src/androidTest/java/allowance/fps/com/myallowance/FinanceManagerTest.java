package allowance.fps.com.myallowance;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.Date;
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
    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().commit();
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

  public void testLoadingFromPreferences() {

    final Double expectedSaved = 363.32;
    final Double expectedRemaining = 23.11;

    // reset FinanceManager
    mFinanceManager = new FinanceManager(getContext());

    // fill shared prefs with junk
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    preferences.edit()
        .putLong(FinanceManager.KEY_START_DATE, mToday.getTime().getTime())
        .putString(SavingManager.KEY_TOTAL_SAVED, expectedSaved.toString())
        .putString(AllowanceWallet.KEY_REMAINING_ALLOWANCE, expectedRemaining.toString())
        .commit();

    assertEquals(expectedRemaining, mFinanceManager.getRemaining());
    assertEquals(expectedSaved, mFinanceManager.getSavedTotal());
    assertEquals(mToday, mFinanceManager.getStartDate());

  }

  public void testSavingToPreferences() {
    final double txValue = 2.46;
    Transaction tx = new Transaction("title", "desc", txValue, mToday.getTime());

    mFinanceManager.performTransaction(tx);

    // changing the date should update saved total
    Calendar twoWeeksLater = (Calendar) mToday.clone();
    twoWeeksLater.add(Calendar.DAY_OF_YEAR, 14);

    mFinanceManager.setStartDate(twoWeeksLater);

    // add another transaction
    final double newTxValue = 2.76;
    Transaction tx2 = new Transaction("title2", "desc2", newTxValue, mToday.getTime());
    mFinanceManager.performTransaction(tx2);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    // check to make sure start date was saved
    Long time = preferences.getLong(FinanceManager.KEY_START_DATE, -1);
    assertTrue(time > 0);

    // check to make sure start date is correct
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date(time));
    assertEquals(twoWeeksLater, calendar);

    // check to make sure total was saved
    String saved = preferences.getString(SavingManager.KEY_TOTAL_SAVED, null);
    assertNotNull(saved);

    // check to make sure saved total is correct
    final double expectedSaved = AllowanceWallet.ALLOWED_TOTAL - txValue;
    assertEquals(expectedSaved, Double.parseDouble(saved));

    // check to make sure allowance was saved
    String remaining = preferences.getString(AllowanceWallet.KEY_REMAINING_ALLOWANCE, null);
    assertNotNull(remaining);

    // check to make sure allowance is correct
    final double expectedRemaining = AllowanceWallet.ALLOWED_TOTAL - newTxValue;
    assertEquals(expectedRemaining, Double.parseDouble(remaining));
  }

  public void testExpired() throws Exception {
    // manual teardown because setUp doesn't cover this case
    tearDown();

    Calendar past = Calendar.getInstance();
    past.add(Calendar.DAY_OF_YEAR, -29);

    mFinanceManager = new FinanceManager(getContext());
    mFinanceManager.setStartDate(past);

    // creating a new finance manager should result in 2 roll overs
    mFinanceManager = new FinanceManager(getContext());

    // check rollover values
    final Double expectedSaving = AllowanceWallet.ALLOWED_TOTAL * 2;
    assertEquals(expectedSaving, mFinanceManager.getSavedTotal());

    // check rollover dates
    Calendar newExpectedStartDate = Calendar.getInstance();
    newExpectedStartDate.add(Calendar.DAY_OF_YEAR, -1);

    assertEquals(newExpectedStartDate, mFinanceManager.getStartDate());
  }


}