package com.dutchtech.dutchgo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dutchtech.dutchgo.databinding.ActivityDutchKindBinding;

import java.io.Serializable;
import java.util.Set;

public class DutchKindActivity extends AppCompatActivity {

    private ActivityDutchKindBinding binding;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDutchKindBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.basicCal.setOnClickListener(v ->
        {
            Intent intent = getIntent();
            Set<String> members = (Set<String>) intent.getSerializableExtra("members");
            intent = new Intent(getApplicationContext(), InputPayInfoActivity.class);
            intent.putExtra("members", (Serializable) members);
            startActivity(intent);
            finish();
        });
    }
}