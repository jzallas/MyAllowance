package allowance.fps.com.myallowance.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import allowance.fps.com.myallowance.FinanceManager;
import allowance.fps.com.myallowance.R;

public class AccountReviewFragment extends Fragment {

    private FinanceManager mFinanceManager = null;

    private TextView expectedSpendingText;

    private TextView actualSpendingText;

    private TextView budgetStart;

    private TextView budgetEnd;

    private FloatingActionButton addTransactionsFab;

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
        expectedSpendingText = (TextView) rootview
                .findViewById(R.id.account_review_expended_spending_value);
        actualSpendingText = (TextView) rootview
                .findViewById(R.id.account_review_actual_spending_value);
        budgetStart = (TextView) rootview.findViewById(R.id.account_review_start);
        budgetEnd = (TextView) rootview.findViewById(R.id.account_review_goal);
        addTransactionsFab = (FloatingActionButton) rootview.findViewById(R.id.add_transaction_fab);

        initListener();
        return rootview;
    }

    private void initListener() {
        addTransactionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initBudgetValue() {

    }

    private void initRecentTransactions() {

    }
}
