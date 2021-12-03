package com.dutchtech.dutchgo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dutchtech.dutchgo.databinding.ActivityInputPayInfoBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
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
            OutputView.printMemByChecked(this, binding.attendeeCbLayout, members,CheckBox.class);
            OutputView.printMemByChecked(this, binding.payerCbLayout,members,RadioButton.class);
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
        LinkedHashSet<String> payer = new LinkedHashSet<>();

        binding.confirmMem.setOnClickListener(v -> {
            attendees.clear();
            payer.clear();

            OutputView.addCheckedItems(binding.attendeeCbLayout, attendees);
            OutputView.addCheckedItems(binding.payerCbLayout, payer);

            String dateText = binding.dateText.getText().toString();
            String payKind = binding.payKind.getText().toString();
            String priceText = binding.price.getText().toString();

            Iterator<String> iter=payer.iterator();
            String payerText=iter.next();

            boolean isEmptyString = attendees.isEmpty() || payer.isEmpty() || dateText.isEmpty() || payKind.isEmpty() || priceText.isEmpty();

            if (isEmptyString) {
                Toast.makeText(getApplicationContext(), "입력하지 않거나 선택하지 않은 곳이 있습니다", Toast.LENGTH_LONG).show();
            } else {
                int price = Integer.parseInt(priceText);
                PayHistory payHistory = new PayHistory(attendees, payerText, dateText, payKind, price);
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