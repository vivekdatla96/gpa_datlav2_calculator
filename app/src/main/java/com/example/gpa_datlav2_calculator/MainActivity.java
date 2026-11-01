package com.example.gpa_datlav2_calculator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText[] gradeInputs = new EditText[5];
    private TextView gpaResult;
    private Button computeGpaButton;

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
        gpaResult = findViewById(R.id.gpaResult);
        computeGpaButton = findViewById(R.id.computeGpaButton);

        computeGpaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(R.string.clear_form).equals(computeGpaButton.getText().toString())) {
                    clearForm();
                } else {
                    if (validateInputs()) {
                        double gpa = calculateGpa();
                        updateGpaDisplay(gpa);
                    }
                }
            }
        });

        for (EditText editText : gradeInputs) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Change button text back to "Compute GPA" when  starts typing
                    computeGpaButton.setText(R.string.compute_gpa);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        for (EditText input : gradeInputs) {
            if (input.getText().toString().trim().isEmpty()) {
                input.setError("Cannot be empty");
                isValid = false;
            } else {
                try {
                    double value = Double.parseDouble(input.getText().toString());
                    if (value < 0 || value > 100) {
                        input.setError("Enter a valid grade (0-100)");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    input.setError("Invalid number");
                    isValid = false;
                }
            }
        }
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
        gpaResult.setText(String.format("GPA: %.2f", gpa));
        computeGpaButton.setText(R.string.clear_form);

        if (gpa < 60) gpaResult.setBackgroundColor(ContextCompat.getColor(this, R.color.gpa_result_low));
        else if (gpa >= 60 && gpa < 80) gpaResult.setBackgroundColor(ContextCompat.getColor(this, R.color.gpa_result_mid));
        else gpaResult.setBackgroundColor(ContextCompat.getColor(this, R.color.gpa_result_high));
    }

    private void clearForm() {
        for (EditText input : gradeInputs) {
            input.setText("");
            input.setError(null);
        }
        gpaResult.setText("");
        gpaResult.setBackgroundColor(ContextCompat.getColor(this, R.color.gpa_result_empty));
        computeGpaButton.setText(R.string.compute_gpa);
    }

}
