package allowance.fps.com.myallowance.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Calendar;

import allowance.fps.com.myallowance.FinanceManager;
import allowance.fps.com.myallowance.R;

import static android.text.TextUtils.isEmpty;

public class AccountSetupFragment extends Fragment {


    private EditText mInputStartDay;

    private EditText mInputName;

    private FinanceManager mFinanceManager;

    private Switch mTodaySwitch;

    public static AccountSetupFragment newInstance() {
        return new AccountSetupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_account_setup, container, false);

        mInputStartDay = (EditText) rootview.findViewById(R.id.input_startdate);
        mInputName = (EditText) rootview.findViewById(R.id.input_name);
        mTodaySwitch = (Switch) rootview.findViewById(R.id.today_switch);
        Button nextBtn = (Button) rootview.findViewById(R.id.next_btn);
        mFinanceManager = new FinanceManager(getActivity());
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    getActivity().getFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, AccountReviewFragment.newInstance())
                            .commit();
                } else {

                }
            }
        });

        mTodaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    mFinanceManager.setStartDate(Calendar.getInstance());
                    mInputStartDay.setText(String.format("%d/%d/%d", month, day, year));
                }
            }
        });

        return rootview;
    }

    private boolean validateInputs() {
        return validateDate() && validateName();
    }

    private boolean validateDate() {
        String nameInputString = mInputStartDay.getEditableText().toString();
        boolean valid = !isEmpty(nameInputString) || mTodaySwitch.isChecked();
        if (!valid) {
            mInputStartDay.setError("Empty or invalid format. Please enter again");
        }
        return valid;
    }

    private boolean validateName() {
        String nameInputString = mInputName.getEditableText().toString();
        boolean valid = !isEmpty(nameInputString);
        if (!valid) {
            mInputName.setError("Empty or invalid format. Please enter again");
        }
        return valid;
    }
}
