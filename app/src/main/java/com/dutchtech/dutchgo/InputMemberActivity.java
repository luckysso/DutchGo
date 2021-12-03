package com.dutchtech.dutchgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.dutchtech.dutchgo.databinding.ActivityInputMemberBinding;

public class InputMemberActivity extends AppCompatActivity {

    private ActivityInputMemberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.confirmMem.setOnClickListener(v ->
        {
            String memberToInput = binding.memName.getText().toString();
            if (StringExtend.isEmptyString(memberToInput)) {
                Toast.makeText(getApplicationContext(), "공백을 입력할 수 없습니다", Toast.LENGTH_LONG).show();
            } else if (memberToInput.contains(",")) {
                Toast.makeText(getApplicationContext(), ",을 입력할 수 없습니다", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("memberToInput", memberToInput);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        binding.cancelMem.setOnClickListener(v -> finish());
    }
}
