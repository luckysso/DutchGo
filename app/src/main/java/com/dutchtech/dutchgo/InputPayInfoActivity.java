package com.dutchtech.dutchgo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dutchtech.dutchgo.databinding.ActivityInputPayInfoBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class InputPayInfoActivity extends AppCompatActivity {

    private ActivityInputPayInfoBinding binding;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputPayInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        {
            Intent intent = getIntent();
            Set<String> members = (Set<String>) intent.getSerializableExtra("members");
            assert members != null;
            OutputView.printMemByAttendee(this, binding.attendeeCbLayout, members);
            binding.payerCbLayout.setPadding(50, 0, 50, 0);
            ArrayList<String> memberArr = new ArrayList<>(members);
            Spinner spinner=new Spinner(this);
            spinner.setTag("payerSpinner");
            ArrayAdapter<String> spinnerArrAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,memberArr);
            spinner.setAdapter(spinnerArrAdapter);
            binding.payerCbLayout.addView(spinner);
        }

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener datePicker = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String format = "yyyy.MM.dd";
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);

            binding.dateText.setText(dateFormat.format(calendar.getTime()));
        };

        binding.calendar.setOnClickListener(v -> {
            DatePickerDialog dpDialog = new DatePickerDialog(InputPayInfoActivity.this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpDialog.show();
            dpDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        });


        LinkedHashSet<String> attendees = new LinkedHashSet<>();

        binding.confirmMem.setOnClickListener(v -> {
            attendees.clear();

            for (int i = 0; i < binding.attendeeCbLayout.getChildCount(); i++) {
                View view = binding.attendeeCbLayout.getChildAt(i);
                if (((CheckBox) view).isChecked()) {
                    String cbText = (String) ((TextView) view).getText();
                    attendees.add(cbText);
                }
            }

            Spinner spinner = binding.payerCbLayout.findViewWithTag("payerSpinner");
            String payer = spinner.getSelectedItem().toString();

            String dateText = binding.dateText.getText().toString();
            String payKind = binding.payKind.getText().toString();
            String priceText = binding.price.getText().toString();

            boolean isEmptyString = attendees.isEmpty() || payer.isEmpty() || dateText.isEmpty() || payKind.isEmpty() || priceText.isEmpty();

            if (isEmptyString) {
                Toast.makeText(getApplicationContext(), "입력하지 않거나 선택하지 않은 곳이 있습니다", Toast.LENGTH_LONG).show();
            } else {
                int price = Integer.parseInt(priceText);
                PayHistory payHistory = new PayHistory(attendees, payer, dateText, payKind, price);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("class", "InputPayInfo");
                intent.putExtra("payHistory", payHistory);
                startActivity(intent);
                finish();
            }
        });
    }


}