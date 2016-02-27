package allowance.fps.com.myallowance.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

import allowance.fps.com.myallowance.FinanceManager;
import allowance.fps.com.myallowance.R;
import allowance.fps.com.myallowance.model.Transaction;
import allowance.fps.com.myallowance.util.DateUtil;
import allowance.fps.com.myallowance.util.MoneyUtil;

import static allowance.fps.com.myallowance.util.MoneyUtil.getMoney;

public class AccountReviewFragment extends Fragment implements
        CreateTransactionDialogFragment.TransactionListener {

    private FinanceManager mFinanceManager = null;

    private TextView mExpectedSpendingText;

    private TextView mActualSpendingText;

    private TextView mBudgetSpent;

    private TextView mBudgetEndAmount;

    private TextView mTodayDate;

    private TextView mBudgetDuration;

    private FloatingActionButton mAddTransactionsFab;

    private ProgressBar mProgressBar;

    private LinearLayout mRecentTransaction;

    private TextView mRemainingBudget;

    private TextView mTotalSaved;

    private CreateTransactionDialogFragment mCreateTransactionDialogFragment;

    public static AccountReviewFragment newInstance() {
        return new AccountReviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFinanceManager = new FinanceManager(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_account_review, container, false);
        mExpectedSpendingText = (TextView) rootview
                .findViewById(R.id.account_review_expended_spending_value);
        mActualSpendingText = (TextView) rootview
                .findViewById(R.id.account_review_actual_spending_value);
        mBudgetSpent = (TextView) rootview.findViewById(R.id.account_review_spent);
        mRemainingBudget = (TextView) rootview.findViewById(R.id.account_review_remaining);
        mBudgetEndAmount = (TextView) rootview.findViewById(R.id.account_review_goal);
        mBudgetDuration = (TextView) rootview.findViewById(R.id.budget_timeline);
        mTotalSaved= (TextView) rootview.findViewById(R.id.account_review_total);
        mTodayDate = (TextView) rootview.findViewById(R.id.today_date);
        mAddTransactionsFab = (FloatingActionButton) rootview
                .findViewById(R.id.add_transaction_fab);
        mProgressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);
        mRecentTransaction = (LinearLayout) rootview
                .findViewById(R.id.account_review_recent_transactions);

        initDate();
        initBudgetValue();
        initRecentTransactions();
        initListener();
        return rootview;
    }

    private void initListener() {
        mAddTransactionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTransactionDialog();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void initDate() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        mTodayDate.setText("Today " + String.format("%d/%d/%d", month, day, year));
        Calendar endDateCal = mFinanceManager.getEndDate();

        month = endDateCal.get(Calendar.MONTH) + 1;
        year = endDateCal.get(Calendar.YEAR);
        day = endDateCal.get(Calendar.DAY_OF_MONTH);

        mBudgetDuration.setText("Budget starts on " + String.format("%d/%d/%d",
                mFinanceManager.getStartDate().get(Calendar.MONTH) + 1,
                mFinanceManager.getStartDate().get(Calendar.DAY_OF_MONTH),
                mFinanceManager.getStartDate().get(Calendar.YEAR))
                + "\nends on " + String.format("%d/%d/%d", month, day, year));
    }

    @SuppressLint("DefaultLocale")
    private void initBudgetValue() {
        double actualSpending = mFinanceManager.getAllowed() - mFinanceManager.getRemaining();
        mBudgetSpent.setText(getMoney(actualSpending));
        mBudgetEndAmount.setText(getMoney(mFinanceManager.getAllowed()));
        mRemainingBudget.setText(getMoney(mFinanceManager.getRemaining()));

        int progress = (int) ((actualSpending * 100) / mFinanceManager.getAllowed());
        mProgressBar.setProgress(progress);

        mExpectedSpendingText.setText(getMoney(mFinanceManager.getAllowed()));
        mActualSpendingText.setText(getMoney(actualSpending));

        mTotalSaved.setText(getMoney(mFinanceManager.getSavedTotal()));
    }

    private void initRecentTransactions() {
        mRecentTransaction.removeAllViews();
        if (mFinanceManager.getRecentTransactions() != null || !mFinanceManager
                .getRecentTransactions().isEmpty()) {

            for (Transaction transaction : mFinanceManager.getRecentTransactions()) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.list_item_transactions, null);
                TextView title = (TextView) view.findViewById(R.id.transaction_title);
                TextView note = (TextView) view.findViewById(R.id.transaction_note);
                TextView date = (TextView) view.findViewById(R.id.transaction_date);
                TextView amount = (TextView) view.findViewById(R.id.transaction_amount);
                title.setText(transaction.getTitle());
                note.setText(transaction.getDescription());
                date.setText(DateUtil.getShortDate(transaction.getDate()));
                amount.setText(MoneyUtil.getMoney(transaction.getAmountUSD()));
                mRecentTransaction.addView(view);
            }
        }
    }

    private void showCreateTransactionDialog() {
        mCreateTransactionDialogFragment = CreateTransactionDialogFragment
                .newInstance();
        mCreateTransactionDialogFragment.setListener(this);
        mCreateTransactionDialogFragment.show(getActivity().getFragmentManager(),
                AccountReviewFragment.class.getSimpleName());
    }

    @Override
    public void createTransaction(String title, String description, Double amount, Calendar time) {
        Log.v("eee", "Callback to controller: createTransaction");
        if(mCreateTransactionDialogFragment!=null){
            mCreateTransactionDialogFragment.dismiss();
        }
        Transaction transaction = new Transaction(
                title,
                description,
                amount,
                time.getTime());
        mFinanceManager.performTransaction(transaction);
        refreshView();
    }

    private void refreshView() {
        initRecentTransactions();
        initBudgetValue();
    }
}
