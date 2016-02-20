package allowance.fps.com.myallowance.database;

import android.test.AndroidTestCase;

import java.util.Date;
import java.util.List;

import allowance.fps.com.myallowance.model.Transaction;

public class AllowanceDbHelperTest extends AndroidTestCase {

  AllowanceDbHelper mAllowanceDbHelper = null;

  private static long TEST_TIME = 1456004752;
  private static String TEST_TITLE = "title";
  private static String TEST_DESC = "desc";
  private static double TEST_AMOUNT = 2.50;

  public Transaction createTransaction(Date date) {
    return new Transaction(TEST_TITLE, TEST_DESC, TEST_AMOUNT,
        date == null ? new Date(TEST_TIME) : date);
  }

  public void setUp() throws Exception {
    mAllowanceDbHelper = new AllowanceDbHelper(getContext());
    mAllowanceDbHelper.deleteAll();
  }

  public void tearDown() throws Exception {
    mAllowanceDbHelper.deleteAll();
    mAllowanceDbHelper.closeDB();
  }

  public void testInsertTransaction() throws Exception {
    Transaction testTransaction = createTransaction(null);

    boolean inserted = mAllowanceDbHelper.insertTransaction(testTransaction);
    assertTrue(inserted);

    List<Transaction> transactionList = mAllowanceDbHelper.getTransactionList();
    assertNotNull(transactionList);

    assertEquals(1, transactionList.size());

    Transaction actualTransaction = transactionList.get(0);

    assertNotNull(actualTransaction);

    assertEquals(testTransaction, actualTransaction);
  }

  public void testDeleteTransaction() throws Exception {
    Transaction testTransaction = createTransaction(null);

    mAllowanceDbHelper.insertTransaction(testTransaction);

    List<Transaction> transactionList = mAllowanceDbHelper.getTransactionList();
    assertNotNull(transactionList);

    assertEquals(1, transactionList.size());

    boolean deleted = mAllowanceDbHelper.deleteTransaction(testTransaction);

    assertTrue(deleted);

    transactionList = mAllowanceDbHelper.getTransactionList();
    assertNotNull(transactionList);

    assertTrue(transactionList.isEmpty());
  }

  public void testGetTransactionList() throws Exception {
    Transaction testTransaction1 = createTransaction(new Date());
    mAllowanceDbHelper.insertTransaction(testTransaction1);
    Transaction testTransaction2 = createTransaction(new Date());
    mAllowanceDbHelper.insertTransaction(testTransaction2);
    Transaction testTransaction3 = createTransaction(new Date());
    mAllowanceDbHelper.insertTransaction(testTransaction3);

    List<Transaction> transactionList = mAllowanceDbHelper.getTransactionList();
    assertNotNull(transactionList);

    assertFalse(transactionList.isEmpty());

    assertEquals(3, transactionList.size());

    assertTrue(transactionList.contains(testTransaction1));

    assertTrue(transactionList.contains(testTransaction2));

    assertTrue(transactionList.contains(testTransaction3));
  }
}