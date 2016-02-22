package allowance.fps.com.myallowance.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import allowance.fps.com.myallowance.R;

public class CreateTransactionDialogFragment extends DialogFragment {

    private EditText mTitle;

    private EditText mDescription;

    private EditText mAmount;

    private TransactionListener mListener;

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
        mDescription = (EditText) rootview.findViewById(R.id.input_description);

        rootview.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTitle() && validteAmount()) {
                    if (mListener != null) {
                        mListener.createTransaction(
                                mTitle.getEditableText().toString(),
                                mDescription.getEditableText().toString(),
                                Double.parseDouble(mAmount.getEditableText().toString()),
                                Calendar.getInstance().getTime());
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

        void createTransaction(String title, String description, Double amount, Date time);
    }
}
