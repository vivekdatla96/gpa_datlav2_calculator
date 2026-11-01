package com.example.gpa_datlav2_calculator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_GPA = "gpa";

    private EditText[] gradeInputs = new EditText[5];
    private TextInputLayout[] gradeLayouts = new TextInputLayout[5];
    private TextView gpaResult;
    private Button computeGpaButton;
    // Last computed GPA, or null while no result is shown
    private Double shownGpa = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        gradeInputs[0] = findViewById(R.id.grade1);
        gradeInputs[1] = findViewById(R.id.grade2);
        gradeInputs[2] = findViewById(R.id.grade3);
        gradeInputs[3] = findViewById(R.id.grade4);
        gradeInputs[4] = findViewById(R.id.grade5);
        gradeLayouts[0] = findViewById(R.id.gradeLayout1);
        gradeLayouts[1] = findViewById(R.id.gradeLayout2);
        gradeLayouts[2] = findViewById(R.id.gradeLayout3);
        gradeLayouts[3] = findViewById(R.id.gradeLayout4);
        gradeLayouts[4] = findViewById(R.id.gradeLayout5);
        gpaResult = findViewById(R.id.gpaResult);
        computeGpaButton = findViewById(R.id.computeGpaButton);

        computeGpaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shownGpa != null) {
                    clearForm();
                } else {
                    computeGpa();
                }
            }
        });

        // "Done" on the last field computes the GPA, same as tapping the button
        gradeInputs[4].setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    computeGpa();
                    return true;
                }
                return false;
            }
        });

        for (int i = 0; i < gradeInputs.length; i++) {
            final TextInputLayout layout = gradeLayouts[i];
            gradeInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Editing a grade makes the old result stale: hide it and the field's error
                    layout.setError(null);
                    resetResult();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (shownGpa != null) {
            outState.putDouble(KEY_GPA, shownGpa);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Runs after the grade fields are restored, so the result is shown again on rotation
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_GPA)) {
            updateGpaDisplay(savedInstanceState.getDouble(KEY_GPA));
        }
    }

    private void computeGpa() {
        if (validateInputs()) {
            hideKeyboard();
            updateGpaDisplay(calculateGpa());
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        EditText firstInvalid = null;
        for (int i = 0; i < gradeInputs.length; i++) {
            EditText input = gradeInputs[i];
            String error = null;
            if (input.getText().toString().trim().isEmpty()) {
                error = getString(R.string.error_empty);
            } else {
                try {
                    double value = Double.parseDouble(input.getText().toString());
                    if (value < 0 || value > 100) {
                        error = getString(R.string.error_range);
                    }
                } catch (NumberFormatException e) {
                    error = getString(R.string.error_invalid);
                }
            }
            gradeLayouts[i].setError(error);
            if (error != null) {
                isValid = false;
                if (firstInvalid == null) firstInvalid = input;
            }
        }
        if (firstInvalid != null) firstInvalid.requestFocus();
        return isValid;
    }

    private double calculateGpa() {
        double sum = 0;
        for (EditText input : gradeInputs) {
            sum += Double.parseDouble(input.getText().toString());
        }
        return sum / gradeInputs.length;
    }

    private void updateGpaDisplay(double gpa) {
        shownGpa = gpa;
        computeGpaButton.setText(R.string.clear_form);

        // The status line repeats the color's meaning in words, so it doesn't depend on color alone
        int colorRes;
        int statusRes;
        if (gpa < 60) {
            colorRes = R.color.gpa_result_low;
            statusRes = R.string.status_low;
        } else if (gpa < 80) {
            colorRes = R.color.gpa_result_mid;
            statusRes = R.string.status_mid;
        } else {
            colorRes = R.color.gpa_result_high;
            statusRes = R.string.status_high;
        }

        String headline = getString(R.string.gpa_result, gpa);
        SpannableString text = new SpannableString(headline + "\n" + getString(statusRes));
        int statusStart = headline.length() + 1;
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, headline.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new RelativeSizeSpan(0.6f), statusStart, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        gpaResult.setText(text);
        gpaResult.setBackgroundColor(ContextCompat.getColor(this, colorRes));
    }

    private void resetResult() {
        if (shownGpa == null) return;
        shownGpa = null;
        gpaResult.setText("");
        gpaResult.setBackgroundColor(ContextCompat.getColor(this, R.color.gpa_result_empty));
        computeGpaButton.setText(R.string.compute_gpa);
    }

    private void clearForm() {
        for (int i = 0; i < gradeInputs.length; i++) {
            gradeInputs[i].setText("");
            gradeLayouts[i].setError(null);
        }
        resetResult();
        gradeInputs[0].requestFocus();
    }

    private void hideKeyboard() {
        View focused = getCurrentFocus();
        if (focused != null) {
            InputMethodManager imm = getSystemService(InputMethodManager.class);
            imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
            focused.clearFocus();
        }
    }

}
