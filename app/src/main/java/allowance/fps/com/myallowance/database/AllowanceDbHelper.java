package allowance.fps.com.myallowance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import allowance.fps.com.myallowance.model.Transaction;

public class AllowanceDbHelper extends SQLiteOpenHelper {

  public static final String TAG = AllowanceDbHelper.class.getName();

  // database version
  private static final int DATABASE_VERSION = 1;

  // database name
  private static final String DATABASE_NAME = "allowance_db";

  // table names
  private static final String TABLE_TRANSACTIONS = "transactions";

  // shared column names
  private static final String COLUMN_ID = "_id";

  // thumbnails column names
  private static final String COLUMN_DATE = "date";
  private static final String COLUMN_TITLE = "title";
  private static final String COLUMN_DESC = "description";
  private static final String COLUMN_AMOUNT = "amount_usd";

  // table create statement
  private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE "
      + TABLE_TRANSACTIONS + "(" + COLUMN_ID
      + " INTEGER PRIMARY KEY AUTOINCREMENT,"
      + COLUMN_TITLE + " TEXT,"
      + COLUMN_DESC + " TEXT,"
      + COLUMN_DATE + " LONG,"
      + COLUMN_AMOUNT + " INTEGER)";

  public AllowanceDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE_TRANSACTIONS);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_TRANSACTIONS);
    onCreate(db);
  }

  public boolean insertTransaction(Transaction transaction) {
    SQLiteDatabase db = getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(COLUMN_TITLE, transaction.getTitle());
    values.put(COLUMN_AMOUNT, (int) (transaction.getAmountUSD() * 100));
    values.put(COLUMN_DESC, transaction.getDescription());
    values.put(COLUMN_DATE, transaction.getDate().getTime());

    try {
      db.insertOrThrow(TABLE_TRANSACTIONS, null, values);
    } catch (SQLiteException e) {
      Log.e(TAG, "insertTransaction(...)", e);
      return false;
    }
    return true;
  }

  private static int dollarToPenny(Double dollar) {
    return (int) (dollar * 100);
  }

  private static double pennyToDollar(int penny) {
    return (double) penny / 100.0;
  }

  public boolean deleteTransaction(Transaction transaction) {
    SQLiteDatabase db = getWritableDatabase();

    String where = String.format("%s = \"%s\" AND %s = \"%s\" AND %s = %d AND %s = %d",
        COLUMN_TITLE, transaction.getTitle(),
        COLUMN_DESC, transaction.getDescription(),
        COLUMN_AMOUNT, dollarToPenny(transaction.getAmountUSD()),
        COLUMN_DATE, transaction.getDate().getTime());

    return db.delete(TABLE_TRANSACTIONS, where, null) > 0;
  }

  private Cursor getTransactionCursor() {
    SQLiteDatabase db = getReadableDatabase();
    return db.rawQuery("select * from " + TABLE_TRANSACTIONS, null);
  }

  public List<Transaction> getTransactionList() {
    Cursor cursor = getTransactionCursor();

    ArrayList<Transaction> transactions = new ArrayList<>();

    if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
      do {
        transactions.add(cursorToTransaction(cursor));
      } while (cursor.moveToNext());
    }

    return transactions;
  }

  private Transaction cursorToTransaction(Cursor cursor) {
    int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
    int descIndex = cursor.getColumnIndex(COLUMN_DESC);
    int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
    int amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT);

    return new Transaction(cursor.getString(titleIndex),
        cursor.getString(descIndex),
        pennyToDollar(cursor.getInt(amountIndex)),
        new Date(cursor.getLong(dateIndex)));
  }

  /**
   * closes the database after opening it
   */
  public void closeDB() {
    SQLiteDatabase db = this.getReadableDatabase();
    if (db != null && db.isOpen()) {
      db.close();
    }
  }

  public static void deleteAll(Context context){
    new AllowanceDbHelper(context).deleteAll();
  }

  private void deleteAll() {
    getWritableDatabase().delete(TABLE_TRANSACTIONS, null, null);
  }
}
