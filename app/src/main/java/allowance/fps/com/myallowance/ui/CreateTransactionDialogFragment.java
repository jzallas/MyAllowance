package allowance.fps.com.myallowance.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import allowance.fps.com.myallowance.R;

public class CreateTransactionDialogFragment extends DialogFragment {

    private EditText mTitle;

    private EditText mDescription;

    private EditText mAmount;

    private TransactionListener mListener;

    private EditText mDate;

    private int mSelectedYear;

    private int mSelectedMonth;

    private int mSelectedDay;

    public void setListener(
            TransactionListener listener) {
        mListener = listener;
    }

    public static CreateTransactionDialogFragment newInstance() {
        return new CreateTransactionDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragement_create_transaction, container, false);
        mAmount = (EditText) rootview.findViewById(R.id.input_amount);
        mTitle = (EditText) rootview.findViewById(R.id.input_title);
        mDate = (EditText) rootview.findViewById(R.id.input_date);
        mDescription = (EditText) rootview.findViewById(R.id.input_description);

        rootview.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTitle() && validteAmount()) {
                    if (mListener != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(mSelectedYear,
                                mSelectedMonth,
                                mSelectedDay);
                        mListener.createTransaction(
                                mTitle.getEditableText().toString(),
                                mDescription.getEditableText().toString(),
                                Double.parseDouble(mAmount.getEditableText().toString()),
                                calendar);
                    }
                }
            }
        });

        rootview.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int currentmonth = Calendar.getInstance().get(Calendar.MONTH);
                final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                    int dayOfMonth) {
                                mSelectedYear = year;
                                mSelectedMonth = monthOfYear;
                                mSelectedDay = dayOfMonth;
                                mDate.setText(
                                        String.format("%d/%d/%d",
                                                monthOfYear + 1,
                                                dayOfMonth,
                                                year));
                            }
                        },
                        currentYear,
                        currentmonth,
                        currentDay);
                dialog.show();
            }
        });
        return rootview;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Create Transaction");
        return dialog;
    }


    private boolean validateTitle() {
        String title = mTitle.getEditableText().toString();
        boolean valid = !TextUtils.isEmpty(title);
        if (!valid) {
            mTitle.setError("Empty or invalid input.");
        }
        return valid;
    }

    private boolean validteAmount() {
        String amount = mAmount.getEditableText().toString();
        boolean valid = !TextUtils.isEmpty(amount);
        if (!valid) {
            mAmount.setError("Empty or invalid input.");
        }
        return valid;
    }

    public interface TransactionListener {

        void createTransaction(String title, String description, Double amount, Calendar time);
    }
}
