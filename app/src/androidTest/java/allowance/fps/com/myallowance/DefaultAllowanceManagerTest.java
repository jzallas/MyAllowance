package allowance.fps.com.myallowance;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import allowance.fps.com.myallowance.database.AllowanceDbHelper;
import allowance.fps.com.myallowance.model.Transaction;

public class DefaultAllowanceManagerTest extends AndroidTestCase {

  private AllowanceManager mAllowanceManager;
  private SharedPreferences mSharedPreferences;

  public void setUp() throws Exception {
    mAllowanceManager = new DefaultAllowanceManager(getContext());
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    mSharedPreferences.edit().clear().commit();
    AllowanceDbHelper.deleteAll(getContext());
  }

  public void testStartDate() throws Exception {
    Date date = new Date();
    mAllowanceManager.setStartDate(date);

    Date savedDate = mAllowanceManager.getStartDate();
    assertNotNull(savedDate);
    assertEquals(date, savedDate);
  }

  public void testGetEndDate() throws Exception {
    Date date = new Date();

    mAllowanceManager.setStartDate(date);

    Date endDate = mAllowanceManager.getEndDate();

    assertNotNull(endDate);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    calendar.add(Calendar.DAY_OF_YEAR, 14);

    assertEquals(calendar.getTime(), endDate);
  }

  public void testAllocatedHobbyAmount() throws Exception {

    final Double expectedAmount = 250.0;

    mAllowanceManager.setAllocatedHobbyAmount(expectedAmount);

    final Double actualAmount = mAllowanceManager.getAllocatedHobbyAmount();

    assertNotNull(actualAmount);
    assertEquals(expectedAmount, actualAmount);


  }

  public void testGetTotalAllowedAmount() throws Exception {
    assertEquals(250.0, mAllowanceManager.getTotalAllowedAmount());
  }

  public void testGetTotalSaved() throws Exception {
    final Double expectedSaved = 369.43;
    mSharedPreferences.edit().putString("total_saved", expectedSaved.toString()).commit();

    assertEquals(expectedSaved, mAllowanceManager.getTotalSaved());
  }

  public void testPerformTransaction() throws Exception {
    Transaction transaction = createTransaction();
    mAllowanceManager.performTransaction(transaction);

    assertTrue(mAllowanceManager.getLatestTenTransactions().contains(transaction));
  }

  public Transaction createTransaction() {
    return new Transaction("title", "desc", 25.04, new Date());
  }

  public void testEditTransaction() throws Exception {
    // TODO: 2/20/16
  }

  public void testDeleteTransaction() throws Exception {
    Transaction transaction = createTransaction();
    mAllowanceManager.performTransaction(transaction);

    mAllowanceManager.deleteTransaction(transaction);

    assertTrue(mAllowanceManager.getLatestTenTransactions().isEmpty());
  }

  public void testGetLatestTenTransactions() throws Exception {

    for (int i = 0; i < 11; i++) {
      mAllowanceManager.performTransaction(createTransaction());
    }

    List<Transaction> txList = mAllowanceManager.getLatestTenTransactions();
    assertNotNull(txList);
    assertTrue(txList.size() <= 10);
  }
}